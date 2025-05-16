Ext.define('CMDBuildUI.store.pluginmanager.Plugins', {
    extend: 'CMDBuildUI.store.Base',

    requires: ['CMDBuildUI.model.pluginmanager.Plugins'],
    alias: 'store.pluginmanager-plugins',

    model: 'CMDBuildUI.model.pluginmanager.Plugins',

    proxy: {
        type: 'baseproxy',
        url: CMDBuildUI.util.Config.baseUrl + '/system/plugins'
    }
});