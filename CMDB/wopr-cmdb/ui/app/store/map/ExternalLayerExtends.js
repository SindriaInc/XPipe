Ext.define('CMDBuildUI.store.map.ExternalLayerExtends', {
    extend: 'CMDBuildUI.store.Base',

    model: 'CMDBuildUI.model.map.GeoExternalLayerSize',

    alias: 'store.map.externallayerextends',

    config: {
        defaultRootProperty: 'data'
    },

    proxy: {
        type: 'baseproxy',
        url: Ext.String.format('{0}/wms', CMDBuildUI.util.Config.geoserverBaseUrl),
        extraParams: {
            service: 'wms',
            version: '1.3.0',
            request: 'getCapabilities',
            limit:0
        },
        reader: {
            type: 'xml',
            record: 'Layer',
            rootProperty: 'Capability'
        }
    },

    // Apply filter to remove items which has name different from title.
    filters: [function (item) {
        return item.get('Name') === item.get('Title');
    }]
});