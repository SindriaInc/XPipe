/**
 * @file CMDBuildUI.util.helper.Configurations
 * @module CMDBuildUI.util.helper.Configurations
 * @author Tecnoteca srl
 * @access public
 */
Ext.define('CMDBuildUI.util.helper.Configurations', {
    singleton: true,

    /**
     * @cfg {CMDBuildUI.model.Configuration} config
     * @private
     */
    _config: null,

    /**
     * @private
     *
     * @argument {Boolean} force
     *
     * @returns {Ext.promise.Promise}
     */
    loadPublicConfs: function (force) {
        var deferred = new Ext.Deferred();

        if (!this.hasConfig() || force) {
            var me = this;
            Ext.Ajax.request({
                url: CMDBuildUI.util.Config.baseUrl + CMDBuildUI.util.api.Common.getPublicConfigurationUrl(),
                callback: function (opts, success, response) {
                    if (response.responseText) {
                        var data = Ext.JSON.decode(response.responseText);
                        if (!data.data[CMDBuildUI.model.Configuration.common.companylogo]) {
                            data.data[CMDBuildUI.model.Configuration.common.companylogo] = null;
                        }
                        me.updateConfig(data.data);
                    }
                    deferred.resolve();
                }
            });
        } else {
            deferred.resolve();
        }
        return deferred.promise;
    },

    /**
     * Update the configs used in viewport.
     * 
     * @private
     *
     */
    updateConfigsInViewport: function () {
        CMDBuildUI.util.helper.SessionHelper.updateInstanceName(this.get(CMDBuildUI.model.Configuration.common.instancename));
        CMDBuildUI.util.helper.SessionHelper.updateCompanyLogoId(this.get(CMDBuildUI.model.Configuration.common.companylogo));
        CMDBuildUI.util.helper.SessionHelper.updateLanguageInfo(this.get(CMDBuildUI.model.Configuration.common.uselanguageprompt));
        CMDBuildUI.util.helper.SessionHelper.updateCanChangePasswordVisibility(!this.get(CMDBuildUI.model.Configuration.ui.systempasswordchangeenabled));
        CMDBuildUI.util.helper.SessionHelper.updateSchedulerInfo(this.get(CMDBuildUI.model.Configuration.scheduler.enabled));
        CMDBuildUI.util.Ajax.updateAjaxTimeout();
    },

    /**
     * @private
     *
     * @returns {Ext.promise.Promise}
     */
    loadSystemConfs: function () {
        var deferred = new Ext.Deferred();

        var me = this;
        Ext.Ajax.request({
            url: CMDBuildUI.util.Config.baseUrl + CMDBuildUI.util.api.Common.getSystemConfigurationUrl(),
            callback: function (opts, success, response) {
                if (response.responseText) {
                    var data = Ext.JSON.decode(response.responseText);
                    me.updateConfig(data.data);
                }

                if (me.get(CMDBuildUI.model.Configuration.common.keepalive)) {
                    me.initKeepAliveTask();
                }
                deferred.resolve();
            }
        });

        return deferred.promise;
    },

    /**
     * Get value for given configuration.
     *
     * @param {String} configuration Configuration property name
     *
     * @returns {*}
     *
     */
    get: function (configuration) {
        return this.getConfigObject().get(configuration);
    },

    /**
     * An object containing information about DMS and Scheduler features.
     *
     * @returns {Object}
     * An object with following keys:
     * dms, scheduler
     *
     */
    getEnabledFeatures: function () {
        return this._enabledFeatures;
    },

    privates: {
        /**
         * @property {Object} _enabledFeatures
         */
        _enabledFeatures: {},

        /**
         * @returns {Boolean}
         */
        hasConfig: function () {
            return this._config !== null;
        },

        /**
         * @returns {CMDBuildUI.model.Configuration}
         */
        getConfigObject: function () {
            if (!this.hasConfig()) {
                this._config = Ext.create("CMDBuildUI.model.Configuration");
            }
            return this._config;
        },

        /**
         * @param {Object} newdata
         */
        updateConfig: function (newdata) {
            if (!Ext.Object.isEmpty(newdata)) {
                var conf = this.getConfigObject();
                for (var key in newdata) {
                    conf.set(key, newdata[key]);
                }
            }
        },

        /**
         *
         */
        initKeepAliveTask: function () {
            var me = this;
            Ext.Ajax.request({
                url: CMDBuildUI.util.Config.baseUrl + CMDBuildUI.util.api.Common.getKeepAliveUrl(),
                method: "POST"
            }).then(function (response) {
                if (response && response.responseText) {
                    var data = Ext.JSON.decode(response.responseText);
                    if (data.data.recommendedKeepaliveIntervalSeconds) {
                        setTimeout(function () {
                            me.initKeepAliveTask();
                        }, 1000 * data.data.recommendedKeepaliveIntervalSeconds);
                    }
                }
            });
        },

        /**
         * Update enabled features
         */
        updateEnabledFeatures: function () {
            var privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");
            this._enabledFeatures = {
                // dms - check config
                dms: this.get(CMDBuildUI.model.Configuration.dms.enabled),
                // scheduler - check config and privileges
                scheduler: this.get(CMDBuildUI.model.Configuration.scheduler.enabled) &&
                    (privileges.calendar_access || privileges.calendar_event_create)
            }
        },

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
        }
    }
});