/**
 * @file CMDBuildUI.util.helper.SessionHelper
 * @module CMDBuildUI.util.helper.SessionHelper
 * @author Tecnoteca srl
 * @access public
 */
Ext.define('CMDBuildUI.util.helper.SessionHelper', {
    singleton: true,

    /**
     * @constant {String} authorization Header/Cookie name for authentication parameter.
     */
    authorization: 'CMDBuild-Authorization',

    /**
     * @constant {String} authorization Header/Cookie name for language parameter.
     */
    localization: 'CMDBuild-Localization',

    /**
     * @private
     */
    logging: false,

    /**
     * Initialize session
     *
     * @private
     *
     * @param {String} token
     */
    initSession: function (token) {
        if (token) {
            CMDBuildUI.util.Ajax.sessionexpired = false;
            this.initWebSocket();
        }
    },

    /**
     * Get current language.
     * 
     * @param {Boolean} [isAuthenticated=undefined] Used to determinate from where get information.
     *
     * @returns {String} Current language.
     *
     */
    getLanguage: function (isAuthenticated) {
        var lang;
        if (isAuthenticated === undefined) {
            isAuthenticated = !!Ext.util.Cookies.get(CMDBuildUI.util.helper.SessionHelper.authorization);
        }

        if (isAuthenticated) {
            // when user is authenticated get the language from preferences
            lang = CMDBuildUI.util.helper.UserPreferences.get(
                CMDBuildUI.model.users.Preference.language
            );
        } else {
            // when user is not authenticated get the language from the local storage
            lang = CMDBuildUI.util.helper.LocalStorageHelper.get(
                CMDBuildUI.util.helper.LocalStorageHelper.keys.loginlanguage
            );
        }

        // if language is not customized, uses the instance language
        if (!lang) {
            lang = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.common.defaultlanguage);
        }
        return lang;
    },

    /**
     * Check the validity of the current session.
     *
     * @returns {Ext.promise.Promise}
     *
     */
    checkSessionValidity: function () {
        var me = this;
        var deferred = new Ext.Deferred();

        function failure(response, opts) {
            var err = "Session token expired.";
            CMDBuildUI.util.Logger.log(err, CMDBuildUI.util.Logger.levels.debug, 401);
            Ext.asap(function () {
                deferred.reject(err);
                me._checksession = false;
            });
        }

        function success(response, opts) {
            var responseJson = JSON.parse(response.responseText);
            var session = Ext.create("CMDBuildUI.model.users.Session", responseJson.data);
            if (session.get("exists")) {
                if (session.get("role") && (Ext.isEmpty(session.get("availableTenants")) || !Ext.isEmpty(session.get("activeTenants")) || session.get("ignoreTenants"))) {
                    CMDBuildUI.util.Logger.log(
                        Ext.String.format('SessionHelper checkSessionValidity have id?: {0}', session.getId() ? true : false),
                        CMDBuildUI.util.Logger.levels.debug);

                    me.initWebSocket();
                    CMDBuildUI.util.Logger.log('SessionHelper checkSessionValidity resolve promise', CMDBuildUI.util.Logger.levels.debug);

                    Ext.asap(function () {
                        deferred.resolve(session);
                        CMDBuildUI.util.Logger.log('SessionHelper checkSessionValidity promise resolved', CMDBuildUI.util.Logger.levels.debug);
                    });
                } else {
                    var err = "Group or tenant not selected.";
                    CMDBuildUI.util.Logger.log(err, CMDBuildUI.util.Logger.levels.debug, 401);
                    Ext.asap(function () {
                        deferred.reject(err);
                    });
                }
            } else {
                failure();
            }
            me._checksession = false;
        }

        if (!this._checksession) {
            this._checksession = true;
            // get saved token
            Ext.Ajax.request({
                url: CMDBuildUI.util.Config.baseUrl + CMDBuildUI.util.api.Common.getCurrentSessionUrl(),
                hideErrorNotification: true,
                method: 'GET',
                params: {
                    ext: true,
                    if_exists: true
                }
            }).then(success, failure);
        }

        return deferred.promise;
    },

    /**
     * Set session object into Viewport
     *
     * @private
     *
     * @param {CMDBuildUI.model.users.Session} session The session object.
     */
    setSessionIntoViewport: function (session) {
        var vm = this.getViewportVM();
        if (vm) {
            vm.set("theSession", session);
        }
    },

    /**
     * Get current session.
     *
     * @returns {CMDBuildUI.model.users.Session} Current session object.
     *
     */
    getCurrentSession: function () {
        return this.getViewportVM().get("theSession");
    },

    /**
     * Update instance name.
     *
     * @private
     *
     * @param {String} instancename New instance name.
     */
    updateInstanceName: function (instancename) {
        this.getViewportVM().set("instancename", instancename);
        var title = Ext.getHead().child("title");
        if (title) {
            var text = CMDBuildUI.view.main.header.Logo.applicationname;
            if (instancename) {
                var stripedinstancename = Ext.util.Format.stripTags(instancename);
                text += " - " + stripedinstancename;
            }
            title.setText(text);
        }
    },

    /**
     * Update Change password visibility.
     *
     * @private
     *
     * @param {Boolean} value If false, user can not view change password button.
     */
    updateCanChangePasswordVisibility: function (value) {
        this.getViewportVM().set("changepasswordHidden", value);
    },

    /**
     * Update company logo.
     *
     * @private
     *
     * @param {String} companylogoid
     */
    updateCompanyLogoId: function (companylogoid) {
        this.getViewportVM().set("companylogoid", companylogoid);
    },

    /**
     * Update show login change language action.
     *
     * @private
     *
     * @param {Boolean} showLanguageSelector
     */
    updateLanguageInfo: function (showLanguageSelector) {
        this.getViewportVM().set('language', {
            default: this.getLanguage(),
            showselector: showLanguageSelector
        });
    },

    /**
     * Update scheduler info.
     *
     * @private
     *
     * @param {Boolean} enabled
     */
    updateSchedulerInfo: function (enabled) {
        this.getViewportVM().set('scheduler.enabled', enabled);
    },

    /**
     * Returrns true if the user is working in administration module.
     *
     * @returns {Boolean}
     */
    isAdministrationModule: function () {
        return !!this.getViewportVM().get("isAdministrationModule");
    },

    /**
     * Implementation of window.sessionStorage.setItem()
     *
     * @private
     *
     * @param {String} key The key.
     * @param {*} value The new associated value for `key`.
     *
     */
    setItem: function (key, value) {
        if (!this.localSessionStorage.id) {
            this.localSessionStorage = new Ext.util.LocalStorage({
                id: this.LOCAL_STORAGE_ID,
                session: true
            });
        }
        this.localSessionStorage.setItem(key, Ext.JSON.encode(value));
    },

    /**
     * Implementation of window.sessionStorage.getItem()
     *
     * @private
     *
     * @param {String|Number} key The key.
     * @param {*} [defaultValue=null] The default associated value for `key`.
     * @returns {*}
     */
    getItem: function (key, defaultValue) {
        if (this.localSessionStorage.id) {
            return Ext.JSON.decode(this.localSessionStorage.getItem(key)) || defaultValue;
        }
        return defaultValue;
    },

    /**
     * Implementation of window.sessionStorage.removeItem()
     *
     * @private
     *
     * @param {String|Number} key The key.
     */
    removeItem: function (key) {
        if (this.localSessionStorage.id) {
            this.localSessionStorage.removeItem(key);
        }
    },

    /**
     * Load localization file.
     *
     * @private
     *
     * @param {String} lang
     *
     * @returns {Ext.promise.Promise}
     */
    loadLocale: function (lang) {
        var deferred = new Ext.Deferred();

        if (!lang) {
            lang = this.getLanguage();
        }
        if (lang && lang !== "en") {
            Ext.require([
                Ext.String.format("CMDBuildUI.locales.{0}.LocalesAdministration", lang),
                Ext.String.format("CMDBuildUI.locales.{0}.Locales", lang)
            ], function () {
                deferred.resolve();
            });

            Ext.Loader.loadScript({
                url: Ext.String.format("app/locales/_ext/locale-{0}.js", lang),
                onLoad: function () {
                    Ext.GlobalEvents.fireEventArgs("setTooltips");
                }
            });
        } else {
            deferred.resolve();
        }

        return deferred.promise;
    },

    /**
     * Set the starting url.
     *
     * @private
     *
     * @param {String} url
     */
    setStartingUrl: function (url) {
        this._startingurl = url;
    },

    /**
     * Get starting url.
     *
     * @returns {String} Starting url.
     *
     */
    getStartingUrl: function () {
        return this._startingurl;
    },

    /**
     * Sets current url as starting url.
     *
     * @private
     */
    updateStartingUrlWithCurrentUrl: function () {
        var currentUrl = Ext.History.getToken();
        if (currentUrl.length > 1 && currentUrl !== 'patches') {
            CMDBuildUI.util.helper.SessionHelper.setStartingUrl(currentUrl);
        }
    },

    /**
     * Clear starting url.
     *
     * @private
     */
    clearStartingUrl: function () {
        CMDBuildUI.util.helper.SessionHelper.setStartingUrl(null);
    },

    /**
     * Get active tenants for current user.
     *
     * @returns {Object[]} Active tenants.
     *
     */
    getActiveTenants: function () {
        var session = this.getCurrentSession();
        var activetenants = session.get("activeTenants");
        var availabletenants = session.get('availableTenantsExtendedData');
        var ignoretenants = session.get("ignoreTenants");

        function activeTenantsFilter(value) {
            return ignoretenants || Ext.Array.contains(activetenants, value.code);
        }
        return availabletenants.filter(activeTenantsFilter);
    },

    /**
     * Update active tenants
     *
     * @private
     *
     * @param {String[]} tenants New active tenants
     */
    updateActiveTenants: function (tenants) {
        this.getCurrentSession().set("activeTenants", tenants);
    },

    privates: {
        /**
         * An Object contains new Ext.util.LocalStorage
         * @type {Ext.util.LocalStorage}
         */
        localSessionStorage: {},
        /**
         * The id param used in new Ext.util.LocalStorage
         * @type {String}
         */
        LOCAL_STORAGE_ID: 'CMDBUILD-SESSION',

        /**
         * @property {String} _startingurl
         * The starting url
         */
        _startingurl: null,

        /**
         * @property {WebSocket} _socket
         * The web socket used by the application.
         */
        _socket: null,

        /**
         * @property {Number} _socketconnerrors
         * The number of connection errors on web socket
         */
        _socketconnerrors: 0,

        /**
         * Get Viewport ViewModel
         *
         * @returns {CMDBuildUI.view.main.MainModel}
         */
        getViewportVM: function () {
            var viewports = Ext.ComponentQuery.query('viewport');
            if (viewports.length) {
                return viewports[0].getViewModel();
            }
        },

        /**
         *
         * @param {String}
         */
        initWebSocket: function () {
            try {
                var me = this;
                if (!CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.services.websocketsEnabled)) {
                    CMDBuildUI.util.Logger.log("CMDBuild websocket is not enabled.", CMDBuildUI.util.Logger.levels.debug);
                    return;
                }
                if (!this._socket) {
                    var socket = this._socket = new WebSocket(CMDBuildUI.util.Config.socketUrl);
                    CMDBuildUI.util.Logger.log("CMDBuild websocket is now initialized.", CMDBuildUI.util.Logger.levels.debug);

                    // register on message event
                    socket.addEventListener('message', function (e) {
                        var data = Ext.JSON.decode(e.data || '');
                        if (data && data.message && data.show_user) {
                            switch (data._event) {
                                case 'alert':
                                    // compose message
                                    var msg = '';
                                    if (data.subject) {
                                        msg += '<div style="margin-bottom: 5px;font-size: 110%;font-weight: bold;">' + data.subject + '</div>';
                                    }
                                    if (data.content) {
                                        msg += data.content;
                                    } else if (data.message) {
                                        msg += data.message;
                                    }
                                    CMDBuildUI.util.Notifier.showInfoMessage(msg);

                                    // reload notification store
                                    if (data.messageId) {
                                        me.getViewportVM().get('notificationStore').load();
                                        CMDBuildUI.util.Utilities.playAlertNotificationSound();
                                    }
                                    break;
                                case 'chat':
                                    var cl = CMDBuildUI.util.Chat.getChatConversationsList();
                                    cl.fireEvent('newmessagereceived', cl, Ext.create('CMDBuildUI.model.messages.Message', data));
                                    break;
                            }
                        }
                    });
                    CMDBuildUI.util.Logger.log("CMDBuild websocket message event initialized", CMDBuildUI.util.Logger.levels.debug);

                    // send authentication to the socket
                    socket.addEventListener('open', function (e) {
                        if (socket) {
                            socket.send(Ext.JSON.encode({
                                _action: 'socket.session.login',
                                _id: CMDBuildUI.util.Utilities.generateUUID()
                            }));
                        }
                        // reset connection errors
                        me._socketconnerrors = 0;
                    });
                    CMDBuildUI.util.Logger.log("CMDBuild websocket open event initialized", CMDBuildUI.util.Logger.levels.debug);

                    // register error event
                    socket.addEventListener('error', function (e) {
                        me._socketconnerrors++;
                    });

                    // register close event
                    socket.addEventListener('close', function (e) {
                        delete me._socket;
                        var times = me._socketconnerrors < 10 ? me._socketconnerrors : 10;
                        setTimeout(function () {
                            me.initWebSocket();
                        }, 30000 * times);
                    });
                } else {
                    CMDBuildUI.util.Logger.log("CMDBuild websocket alredy opened", CMDBuildUI.util.Logger.levels.debug);
                }
            } catch (e) {
                CMDBuildUI.util.Logger.log(
                    "Error on creating CMDBuild websocket.",
                    CMDBuildUI.util.Logger.levels.error,
                    null,
                    e
                );
            }
        },

        closeWebSocket: function () {
            if (this._socket) {
                this._socket.close();
                delete this._socket;
            }
        }
    }
});