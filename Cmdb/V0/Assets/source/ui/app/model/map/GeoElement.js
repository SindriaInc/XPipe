Ext.define('CMDBuildUI.model.map.GeoElement', {
    extend: 'CMDBuildUI.model.base.Base',
    //TODO: define a better model
    fields: [{
        name: '_type',
        type: 'string'
    }, {
        name: '_id',
        type: 'auto'
    }, {
        name: 'x',  //TODO: //define type
        type: 'auto'
    }, {
        name: 'y',
        type: 'auto'    //TODO: define type
    }, {
        name: '_owner_type',
        type: 'string'    //TODO: define type
    }, {
        name: '_owner_id',
        type: 'integer'    //TODO: define type
    }]
});
