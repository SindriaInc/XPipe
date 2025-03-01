/**
 * @file CMDBuildUI.util.helper.MapHelper
 * @module CMDBuildUI.util.helper.MapHelper
 * @author Tecnoteca srl
 * @access public
 */
Ext.define('CMDBuildUI.util.helper.MapHelper', {
    singleton: true,

    /**
     * Returns the GIS layers menu configuration.
     *
     * @returns {Ext.promise.Promise<Ext.data.TreeStore>}
     *
     */
    getLayersMenu: function () {
        var deferred = new Ext.Deferred(),
            me = this;

        if (!me._layersmenu) {
            me._layersmenu = Ext.create("Ext.data.TreeStore", {
                model: 'CMDBuildUI.model.menu.MenuItem',
                proxy: {
                    type: 'baseproxy',
                    url: '/menu'
                },
                root: {
                    expanded: false // to make it working with autoLoad=false
                },
                defaultRootProperty: 'data',
                defaultRootId: 'gismenu',
                sorters: ['index'],
                autoLoad: false
            });
            me._layersmenu.load({
                callback: function (records, operation, success) {
                    deferred.resolve(me._layersmenu);
                }
            });
        } else {
            deferred.resolve(me._layersmenu);
        }

        return deferred.promise;
    }
})