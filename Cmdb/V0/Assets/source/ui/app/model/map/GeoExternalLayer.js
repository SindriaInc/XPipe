Ext.define('CMDBuildUI.model.map.GeoExternalLayer', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: '_id',
        type: 'string'
    }, {
        name: 'name', //TODO: make this field unique in the database
        type: 'string'
    }, {
        name: 'type',
        type: 'string'
    }, {
        name: 'index',
        type: 'integer'
    }, {
        name: 'geoserver_name',
        type: 'string'
    }, {
        name: 'zoomMin',
        type: 'integer'
    }, {
        name: 'zoomDef',
        type: 'integer'
    }, {
        name: 'zoomMax',
        type: 'integer'
    }, {
        name: 'visibility',
        type: 'auto'
    }, {
        name: 'owner_type',
        type: 'string'
    }, {
        name: 'owner_id',
        type: 'integer'
    }]
});
