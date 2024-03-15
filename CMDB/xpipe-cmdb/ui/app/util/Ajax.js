/**
 * @file CMDBuildUI.util.Ajax
 * @module CMDBuildUI.util.Ajax
 * @author Tecnoteca srl
 * @access public
 */

Ext.define('CMDBuildUI.util.Ajax', {
    singleton: true,

    /**
     * Pending counter for automated test
     *
     * @private
     */
    currentPendingCount: 0,

    /**
     * @private
     */
    currentPendingDebug: false,

    /**
     * @private
     */
    currentPendingUrls: [],

    /**
     * @private
     */
    processStatAbort: 'proc.inst.abort',

    /**
     * Initialize Ajax for CMDBuild.
     *
     * @private
     */
    init: function () {
        Ext.Ajax.withCredentials = true;
        // initialize client ID
        this._clientId = CMDBuildUI.util.Utilities.generateRandomString(24);
        // init listeners
        this.initBeforeRequest();
        this.initRequestComplete();
        this.initRequestException();
    },

    /**
     * Initialize beforerequest event handler.
     *
     * @private
     */
    initBeforeRequest: function () {
        /**
         * Fired before a network request is made to retrieve a data object.
         *
         * @param {Ext.data.Connection} conn
         * @param {Object} options
         * @param {Object} eOpts
         */
        Ext.Ajax.on('beforerequest', function (conn, options, eOpts) {
            this.currentPending('add', arguments);
            // set headers
            var headers = Ext.applyIf(options.headers || {}, {
                'Content-Type': "application/json",
                'CMDBuild-ActionId': CMDBuildUI.util.Ajax.getActionId(),
                'CMDBuild-RequestId': CMDBuildUI.util.Utilities.generateUUID(),
                'CMDBuild-View': CMDBuildUI.util.Ajax.getViewContext(),
                'CMDBuild-ClientId': CMDBuildUI.util.Ajax.getClientId()
            });

            //remove _id from PUT request
            if (options.method === 'PUT') {
                Ext.Array.each(options.records, function (value, index) {
                    if (options.jsonData && options.jsonData.hasOwnProperty('_id')) {
                        delete options.jsonData._id;
                    }
                });
            }

            // add withCredentials property
            if (Ext.isEmpty(options.withCredentials)) {
                options.withCredentials = true;
            }

            // merge options with custom headers
            Ext.merge(options, {
                headers: headers
            });
        }, this);
    },

    /**
     * Initialize requestcomplete event handler.
     *
     * @private
     */
    initRequestComplete: function () {
        /**
         * Fired if the request was successfully completed.
         *
         * @param {Ext.data.Connection} conn
         * @param {Object} response
         * @param {Object} options
         * @param {Object} eOpts
         */
        Ext.Ajax.on('requestcomplete', function (conn, response, options, eOpts) {
            this.currentPending('sub', arguments);
            this.showMessages(response, options);
        }, this);
    },

    /**
     * Initialize requestexception event handler.
     *
     * @private
     */
    initRequestException: function () {
        /**
         * Fired if an error HTTP status was returned from the server.
         *
         * @param {Ext.data.Connection} conn
         * @param {Object} response
         * @param {Object} options
         * @param {Object} eOpts
         */
        Ext.Ajax.on('requestexception', function (conn, response, options, eOpts) {
            this.currentPending('sub', arguments);
            if (response.status === 401 && !CMDBuildUI.util.Ajax.sessionexpired) {
                // Cmdb.Logger.debug("Got unauthorized (401) status from server, check session...");
                CMDBuildUI.util.helper.SessionHelper.checkSessionValidity().then(function () {
                    CMDBuildUI.util.Ajax.sessionexpired = false;
                    CMDBuildUI.util.Logger.log("You cannot access this resource.", CMDBuildUI.util.Logger.levels.warn, 401);
                }, function (err) {
                    CMDBuildUI.util.Ajax.sessionexpired = true;
                    CMDBuildUI.util.helper.SessionHelper.setSessionIntoViewport();
                    if (!CMDBuildUI.util.helper.SessionHelper.getStartingUrl()) {
                        CMDBuildUI.util.helper.SessionHelper.updateStartingUrlWithCurrentUrl();
                    }
                    if (CMDBuildUI.util.Ajax.getActionId() !== "login") {
                        CMDBuildUI.util.Utilities.redirectTo("login");
                    }
                });
            } else if (response.status !== 401) {
                this.showMessages(response, options);
            }
        }, this);
    },

    /**
     *
     * Get Javascript global variable tracking the number of pending http requests from client (issue #710)
     *
     * See CMDBuildUI.util.Ajax.currentPending(null|add|sub|subtract|enable-debug|disable-debug|reset);
     *
     * @private
     *
     * @param {String} operation
     * @returns {Number} currentPendingCount
     */
    currentPending: function (operation, args) {
        switch (operation) {
            case 'add':
                if (args && args[1] && args[1].url) {
                    Ext.Array.push(this.currentPendingUrls, args[1].url);
                }
                this.currentPendingCount++;
                break;
            case 'sub':
            case 'subtract':
                if (args && args[2] && args[2].url) {
                    Ext.Array.remove(this.currentPendingUrls, args[2].url);
                }
                this.currentPendingCount--;
                break;
            case 'enable-debug':
                this.currentPendingDebug = true;
                break;
            case 'disable-debug':
                this.currentPendingDebug = false;
                break;
            case 'reset':
                Ext.Array.clean(this.currentPendingUrls);
                this.currentPendingCount = 0;
                break;
            case 'count':
                return this.currentPendingCount;
            case 'list':
                return this.currentPendingUrls;

        }
        if (this.currentPendingDebug) {
            CMDBuildUI.util.Logger.log(Ext.String.format('Pending ajax: {0}', this.currentPendingCount), CMDBuildUI.util.Logger.levels.debug);
            Ext.Array.forEach(this.currentPendingUrls, function (url) {
                CMDBuildUI.util.Logger.log(url, CMDBuildUI.util.Logger.levels.debug);
            });
            CMDBuildUI.util.Logger.log('', CMDBuildUI.util.Logger.levels.debug);
        }

    },

    /**
     * Returns the current action id.
     *
     * @returns {String}
     *
     */
    getActionId: function () {
        return this._actionid;
    },

    /**
     * Set the request action id.
     *
     * @param {String} actionid
     *
     */
    setActionId: function (actionid) {
        this._actionid = actionid;
    },

    /**
     * Returns the client id used for Ajax requests.
     *
     * @returns {String} client Id
     */
    getClientId: function () {
        return this._clientId;
    },

    /**
     * Update Ajax Timeout.
     *
     * @private
     */
    updateAjaxTimeout: function () {
        var timeout_s = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.common.ajaxtimeout) || 60;
        CMDBuildUI.util.Config.ajaxTimeout = timeout_s * 1000;
        Ext.Ajax.setTimeout(CMDBuildUI.util.Config.ajaxTimeout);
    },

    privates: {
        _actionid: null,
        /**
         * @returns {String} admin|default
         */
        getViewContext: function () {
            var vm = CMDBuildUI.util.helper.SessionHelper.getViewportVM();
            if (vm && vm.get('isAdministrationModule')) {
                return 'admin';
            }
            return 'default';
        },

        showMessages: function (response, options) {
            var messages = CMDBuildUI.util.Ajax.getResponseMessage(response);
            if (messages) {
                for (var k in messages) {
                    if (options.hideErrorNotification) {
                        var level;
                        switch (k) {
                            case "WARNING":
                                level = CMDBuildUI.util.Logger.levels.warn;
                                break;
                            case "ERROR":
                                level = CMDBuildUI.util.Logger.levels.error;
                                break;
                            case "INFO":
                                level = CMDBuildUI.util.Logger.levels.info;
                                break;
                            default:
                                level = CMDBuildUI.util.Logger.levels.info;
                        }
                        CMDBuildUI.util.Logger.log(messages[k].message, level, messages[k].code);
                    } else if (response.status !== -1) {
                        var notifier;
                        switch (k) {
                            case "WARNING":
                                notifier = CMDBuildUI.util.Notifier.showWarningMessage;
                                break;
                            case "ERROR":
                                notifier = CMDBuildUI.util.Notifier.showErrorMessage;
                                break;
                            case "INFO":
                                notifier = CMDBuildUI.util.Notifier.showInfoMessage;
                                break;
                            default:
                                notifier = CMDBuildUI.util.Notifier.showInfoMessage;
                        }
                        notifier(messages[k].usermessage, messages[k].code, undefined, messages[k].message);
                    }
                }
            }
        },

        toISOStringWithTimezone: function (date) {
            if (!date) return date;
            /* extract GTM timezone from date.toString() */
            var regex = /(?:GMT)([-+]\d*)/gm;
            var gtm = regex.exec(date.toString())[1];
            /* replace Z (UTC) to respective TimeZone */
            var finaldate = date.toISOString().replace('Z', gtm);
            return finaldate;
        },

        /**
         * @param {Object} response
         * @returns {Object} An object conaining error message and error code.
         */
        getResponseMessage: function (response) {
            if (!response.responseText) {
                return false;
            }
            var oresponse = response.responseText;
            if (!Ext.isObject(oresponse)) {
                oresponse = Ext.JSON.decode(oresponse, true);
            }
            var errors = false;
            if (oresponse && oresponse.messages) {
                errors = {};
                var usermessages = {};
                var messages = {};
                var reqid = '';
                var today = new Date();
                var expdate = this.toISOStringWithTimezone(today);
                var instancename = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.common.instancename);

                if (response.request && response.request.headers) {
                    reqid = Ext.String.format("<b>req id</b>: {0}... <br/><br/>{1}<br/><br/>{2}<br/>", response.request.headers['CMDBuild-RequestId'].toString().replace(/[^a-z0-9]/g, '').substring(0, 15), expdate, instancename);
                }
                oresponse.messages.forEach(function (m) {
                    if (!usermessages[m.level]) {
                        usermessages[m.level] = [];
                    }
                    if (!messages[m.level]) {
                        messages[m.level] = [];
                    }
                    if (m.show_user) {
                        if (reqid) {
                            usermessages[m.level].push(reqid);
                        }
                        usermessages[m.level].push(m.message);
                    } else {
                        messages[m.level].push(m.message);
                    }
                });

                for (var k1 in usermessages) {
                    errors[k1] = {};
                    if (usermessages[k1].length) {
                        errors[k1].usermessage = usermessages[k1].join("<br />");
                    } else {
                        switch (k1) {
                            case "WARNING":
                                errors[k1].usermessage = CMDBuildUI.locales.Locales.notifier.genericwarning;
                                break;
                            case "ERROR":
                                errors[k1].usermessage = CMDBuildUI.locales.Locales.notifier.genericerror;
                                break;
                            case "INFO":
                                errors[k1].usermessage = CMDBuildUI.locales.Locales.notifier.genericinfo;
                                break;
                            default:
                                errors[k1].usermessage = "";
                        }
                    }
                }

                for (var k2 in messages) {
                    if (!errors[k2]) {
                        errors[k2] = {};
                    }
                    if (messages[k2].length) {
                        errors[k2].message = messages[k2].join("<br />");
                    } else {
                        errors[k2].message = response.statusText;
                    }
                }
            }
            return errors;
        }
    }
});