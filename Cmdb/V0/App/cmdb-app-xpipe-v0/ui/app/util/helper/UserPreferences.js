/**
 * @file CMDBuildUI.util.helper.UserPreferences
 * @module CMDBuildUI.util.helper.UserPreferences
 * @author Tecnoteca srl
 * @access public
 */

/**
 * @typedef FavouritesMenuConfig
 * @type {Object}
 * @property {String} position The position of the menu. Availabel options are "top" or "bottom".
 * @property {Boolena} collapsed Default collapsed at page opening.
 * @property {Object[]} items Favourites menu items.
 */
Ext.define('CMDBuildUI.util.helper.UserPreferences', {
    singleton: true,

    menuPosition: {
        before: "before",
        after: "after"
    },

    /**
     * Load user preferences
     *
     * @private
     *
     * @returns {Ext.promise.Promise} Resolve method has as argument an
     *      instance of {CMDBuildUI.store.users.Preferences}.
     *      Reject method has as argument a {String} containing error message.
     */
    load: function () {
        var me = this;
        // create deferred instance
        var deferred = new Ext.Deferred();

        // load preferences
        CMDBuildUI.model.users.Preference.load('preferences', {
            callback: function (record, operation, success) {
                if (success) {
                    me._preferences = record;
                    deferred.resolve(record);
                } else {
                    deferred.reject(operation);
                }
            }
        });

        // returns promise
        return deferred.promise;
    },

    /**
     * Get CMDBuildUI.store.users.Preferences instance. Null if preferences are not loaded yet.
     *
     * @returns {CMDBuildUI.store.users.Preferences}
     *
     */
    getPreferences: function () {
        return this._preferences;
    },

    /**
     * Get user preference.
     *
     * @param {String} property
     *
     * @returns {String|Number|Boolean|Object}
     *
     */
    get: function (property) {
        return this._preferences.get(property);
    },

    /**
     * Get thousands separator character.
     *
     * @returns {String}
     *
     */
    getThousandsSeparator: function () {
        if (!this.formats.thousandsSeparator) {
            if (!Ext.isEmpty(this.get(CMDBuildUI.model.users.Preference.thousandsSeparator))) {
                this.formats.thousandsSeparator = this.get(CMDBuildUI.model.users.Preference.thousandsSeparator);
            } else {
                this.formats.thousandsSeparator = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.fields.thousandsSeparator);
            }
        }
        return this.formats.thousandsSeparator;
    },

    /**
     * Get decimals separator character.
     *
     * @returns {String}
     *
     */
    getDecimalsSeparator: function () {
        if (!this.formats.decimalsSeparator) {
            if (!Ext.isEmpty(this.get(CMDBuildUI.model.users.Preference.decimalsSeparator))) {
                this.formats.decimalsSeparator = this.get(CMDBuildUI.model.users.Preference.decimalsSeparator)
            } else {
                this.formats.decimalsSeparator = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.fields.decimalsSeparator);
            }
        }
        return this.formats.decimalsSeparator;
    },

    /**
     * Get date format.
     *
     * @returns {String}
     *
     */
    getDateFormat: function () {
        if (!this.formats.date) {
            if (!Ext.isEmpty(this.get(CMDBuildUI.model.users.Preference.dateFormat))) {
                this.formats.date = this.get(CMDBuildUI.model.users.Preference.dateFormat)
            } else if (!Ext.isEmpty(CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.fields.dateFormat))) {
                this.formats.date = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.fields.dateFormat);
            } else {
                this.formats.date = CMDBuildUI.locales.Locales.common.dates.date;
            }
        }
        return this.formats.date;
    },

    /**
     * Get send email delay.
     *
     * @returns {Number} in seconds
     *
     */
    getEmailSendDelay: function () {
        if (!this.defaultEmailDelay) {
            var delay = 0;
            if (!Ext.isEmpty(this.get(CMDBuildUI.model.users.Preference.notifications.defaultEmailDelay))) {
                delay = this.get(CMDBuildUI.model.users.Preference.notifications.defaultEmailDelay);
            } else if (!Ext.isEmpty(CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.email.defaultDelay))) {
                delay = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.email.defaultDelay);
            }
            this.defaultEmailDelay = Number(delay);
        }
        return this.defaultEmailDelay;
    },


    /**
     * Get time with seconds format.
     *
     * @returns {String}
     *
     */
    getTimeWithSecondsFormat: function () {
        if (!this.formats.timeWithSeconds) {
            if (!Ext.isEmpty(this.get(CMDBuildUI.model.users.Preference.timeFormat))) {
                this.formats.timeWithSeconds = this.get(CMDBuildUI.model.users.Preference.timeFormat);
            } else if (!Ext.isEmpty(CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.fields.timeFormat))) {
                this.formats.timeWithSeconds = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.fields.timeFormat);
            } else {
                this.formats.timeWithSeconds = CMDBuildUI.locales.Locales.common.dates.time;
            }
        }
        return this.formats.timeWithSeconds;
    },

    /**
     * Get time without seconds format.
     *
     * @returns {String}
     *
     */
    getTimeWithoutSecondsFormat: function () {
        if (!this.formats.timeWithoutSeconds) {
            this.formats.timeWithoutSeconds = this.getTimeWithSecondsFormat().replace(":s", "");
        }
        return this.formats.timeWithoutSeconds;
    },

    /**
     * Get timestamp with seconds format
     *
     * @returns {String}
     *
     */
    getTimestampWithSecondsFormat: function () {
        if (!this.formats.timestampWithSeconds) {
            this.formats.timestampWithSeconds = this.getDateFormat() + " " + this.getTimeWithSecondsFormat();
        }
        return this.formats.timestampWithSeconds;
    },

    /**
     * Get timestamp without seconds format
     *
     * @returns {String}
     *
     */
    getTimestampWithoutSecondsFormat: function () {
        if (!this.formats.timestampWithoutSeconds) {
            this.formats.timestampWithoutSeconds = this.getDateFormat() + " " + this.getTimeWithoutSecondsFormat();
        }
        return this.formats.timestampWithoutSeconds;
    },

    /**
     * Return user grid preferences for grid by object type and object type name.
     *
     * @param {String} objectType Object type. One of CMDBuildUI.util.helper.ModelHelper.objecttypes properties.
     * @param {String} objectTypeName Class/Process/View/... name.
     *
     * @returns {Object} Grid preferences.
     *
     */
    getGridPreferences: function (objectType, objectTypeName) {
        var gridsconfig = this.get(CMDBuildUI.model.users.Preference.gridsconfig);

        // convert from string to JSON
        if (Ext.isString(gridsconfig)) {
            gridsconfig = Ext.JSON.decode(gridsconfig);
            this.getPreferences().set(CMDBuildUI.model.users.Preference.gridsconfig, gridsconfig);
        }

        // crete hierarchy if not exists
        if (Ext.isEmpty(gridsconfig[objectType])) {
            gridsconfig[objectType] = {};
        }
        if (Ext.isEmpty(gridsconfig[objectType][objectTypeName])) {
            gridsconfig[objectType][objectTypeName] = {};
        }
        return gridsconfig[objectType][objectTypeName];
    },

    /**
     * Update user grid preferences for grid by object type and object type name.
     *
     * @param {String} objectType Object type. One of CMDBuildUI.util.helper.ModelHelper.objecttypes properties.
     * @param {String} objectTypeName Class/Process/View/... name.
     * @param {Object} config Grid preferences.
     *
     * @returns {Ext.promise.Promise}
     *
     */
    updateGridPreferences: function (objectType, objectTypeName, config) {
        // update grid configs
        var gridconf = this.getGridPreferences(objectType, objectTypeName);
        for (var key in config) {
            if (config[key] === undefined) {
                delete gridconf[key];
            } else {
                gridconf[key] = config[key];
            }
        }

        // update preferences
        var gridsconfig = this.get(CMDBuildUI.model.users.Preference.gridsconfig);
        gridsconfig[objectType][objectTypeName] = gridconf;

        // save configs
        var jsondata = {};
        jsondata[CMDBuildUI.model.users.Preference.gridsconfig] = Ext.JSON.encode(gridsconfig);

        return this.updatePreferences(jsondata);
    },

    /**
     * @private
     *
     * Returns user's favourites menu configuration.
     *
     * @returns {FavouritesMenuConfig} Grid preferences.
     *
     */
    getFavouritesMenuConfig: function () {
        var favouritesmenu = this.get(CMDBuildUI.model.users.Preference.favouritesmenu);

        // convert from string to JSON
        if (Ext.isString(favouritesmenu)) {
            favouritesmenu = Ext.JSON.decode(favouritesmenu);
            this.getPreferences().set(CMDBuildUI.model.users.Preference.favouritesmenu, favouritesmenu);
        }

        return favouritesmenu || {};
    },

    /**
     * Returns the list of favourites.
     *
     * @returns {Object[]}
     */
    getFavouritesMenuItems: function () {
        var config = this.getFavouritesMenuConfig();
        return config.items || [];
    },

    /**
     * Update favourites menu items.
     *
     * @param {Object[]} items Array of items.
     *
     * @returns {Ext.promise.Promise}
     */
    updateFavouritesMenuItems: function (items) {
        // update config
        var config = this.getFavouritesMenuConfig();
        config.items = items;
        this.getPreferences().set(CMDBuildUI.model.users.Preference.favouritesmenu, config);

        // prepare data for saving
        var jsondata = {};
        jsondata[CMDBuildUI.model.users.Preference.favouritesmenu] = Ext.JSON.encode(config);

        return this.updatePreferences(jsondata);
    },

    /**
     * Returns favourites menu position.
     *
     * @returns {String}
     */
    getFavouritesMenuPosition: function () {
        var config = this.getFavouritesMenuConfig();
        return config.position || 'before';
    },

    /**
     * Update favourites menu position.
     *
     * @param {String} position Menu position.
     *
     * @returns {Ext.promise.Promise}
     */
    updateFavouritesMenuPosition: function (position) {
        // update config
        var config = this.getFavouritesMenuConfig();
        config.position = position;
        this.getPreferences().set(CMDBuildUI.model.users.Preference.favouritesmenu, config);

        // prepare data for saving
        var jsondata = {};
        jsondata[CMDBuildUI.model.users.Preference.favouritesmenu] = Ext.JSON.encode(config);

        return this.updatePreferences(jsondata);
    },

    /**
     * Returns favourites menu collapsed configuration.
     *
     * @returns {Boolean}
     */
    getFavouritesMenuCollapsed: function () {
        var config = this.getFavouritesMenuConfig();
        return config.collapsed || false;
    },

    /**
     * Update favourites menu collapsed configuration.
     *
     * @param {Boolean} collapsed Collapsed configuration.
     *
     * @returns {Ext.promise.Promise}
     */
    updateFavouritesMenuCollapsed: function (collapsed) {
        // update config
        var config = this.getFavouritesMenuConfig();
        config.collapsed = collapsed;
        this.getPreferences().set(CMDBuildUI.model.users.Preference.favouritesmenu, config);

        // prepare data for saving
        var jsondata = {};
        jsondata[CMDBuildUI.model.users.Preference.favouritesmenu] = Ext.JSON.encode(config);

        return this.updatePreferences(jsondata);
    },

    /**
     * Determinates if an item is in favourites menu checking its object type name and menu type.
     *
     * @param {String} menuType
     * @param {String} objectTypeName
     *
     * @returns {Boolean}
     */
    isItemInFavourites: function (menuType, objectTypeName) {
        var favourites = this.getFavouritesMenuItems() || [];
        return Ext.Array.findBy(favourites, function (item, index) {
            return item.objectTypeName === objectTypeName && item.menuType === menuType;
        });
    },

    /**
     * Update user preferences.
     *
     * @private
     *
     * @param {Object} params User preferences.
     *
     * @returns {Ext.promise.Promise}
     */
    updatePreferences: function (params) {
        var deferred = new Ext.Deferred();
        Ext.Ajax.request({
            url: CMDBuildUI.util.Config.baseUrl + CMDBuildUI.util.api.Common.getPreferencesUrl(),
            method: "POST",
            jsonData: params,
            success: function () {
                deferred.resolve();
            },
            failure: function () {
                deferred.reject();
            }
        });
        // returns promise
        return deferred.promise;
    },

    privates: {
        /**
         * @property {CMDBuildUI.model.users.Preference} _preferences
         * Object containing user preferences
         */
        _preferences: null,

        /**
         * An object containing all formats.
         */
        formats: {
            thousandsSeparator: null,
            decimalsSeparator: null,
            date: null,
            timeWithSeconds: null,
            timeWithoutSeconds: null,
            timestampWithSeconds: null,
            timestampWithoutSeconds: null
        }
    }
});