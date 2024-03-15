Ext.define('CMDBuildUI.model.classes.Card', {
    extend: 'CMDBuildUI.model.base.Base',

    mixins: [
        'CMDBuildUI.mixins.model.Lock',
        'CMDBuildUI.mixins.model.Emails'
    ],

    fields: [{
        name: '_type',
        type: 'string'
    }, {
        name: '_tenant',
        type: 'string'
        // mapping: 'TenantId'
    }, {
        name: '_model',
        persist: false
    }],

    isCard: true,

    /**
     * @return {Numeric|String} Record id. The same value returned by this.getId() function.
     */
    getRecordId: function () {
        return this.getId();
    },

    /**
     * @return {Numeric|String} Record type. The same value returned by this.get("_type") function.
     */
    getRecordType: function () {
        return this.get("_type");
    },

    /**
     * Override load method to add "includeModel" parameter in request.
     *
     * @param {Object} [options] Options to pass to the proxy.
     *
     * @return {Ext.data.operation.Read} The read operation.
     */
    load: function (options) {
        options = Ext.merge({
            params: {
                includeModel: true,
                includeWidgets: true,
                includeStats: true
            }
        }, options || {});
        return this.callParent([options]);
    },

    /**
     * @return {Object}
     */
    getOverridesFromPermissions: function () {
        var overrides = {};
        if (this.get("_model")) {
            this.get("_model").attributes.forEach(function (attr) {
                overrides[attr._id] = {
                    writable: attr.writable,
                    hidden: attr.hidden
                };
            });
        }
        return overrides;
    },

    getGeoValues: function (force) {
        var deferred = new Ext.Deferred();
        var geovalues = this.geovalues();

        if ((!geovalues.isLoaded() && !geovalues.isLoading()) || force) {
            geovalues.getProxy().setUrl(CMDBuildUI.util.api.Classes.getGeoValuesUrl(
                this.get("_type"),
                this.getId()
            ));

            // load store
            geovalues.load({
                callback: function (records, operation, success) {
                    if (success) {
                        deferred.resolve(geovalues, true);
                    }
                }
            });
        } else if (geovalues.isLoading()) {
            geovalues.addListener('load', function (deferred, store, records, successful, operation, eOpts) {
                deferred.resolve(store);
            }, this, {
                single: true,
                args: [deferred]
            })
        } else {
            deferred.resolve(geovalues, false);
        }

        // return promise
        return deferred.promise;
    },

    getGeoLayers: function (force) {
        var deferred = new Ext.Deferred(),
            geolayers = this.geolayers(),
            geoserverEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.geoserverEnabled);

        if ((!geolayers.isLoaded() || force) && geoserverEnabled) {
            geolayers.getProxy().setUrl(CMDBuildUI.util.api.Classes.getGeoLayersUrl(
                this.get("_type"),
                this.getId()
            ));

            // load store
            geolayers.load({
                callback: function (records, operation, success) {
                    if (success) {
                        deferred.resolve(geolayers, true);
                    }
                }
            });
        } else {
            deferred.resolve(geolayers, false);
        }
        return deferred.promise;
    }
});