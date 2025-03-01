Ext.define('CMDBuildUI.store.map.geoattributeGrid', {
    extend: 'CMDBuildUI.store.Base',

    alias: 'store.map-geoattributegrid',
    storeId: 'geoattributeGrid',
    model: 'CMDBuildUI.model.gis.GeoValue',

    proxy: {
        type: 'baseproxy'
    },

    autoLoad: false
});