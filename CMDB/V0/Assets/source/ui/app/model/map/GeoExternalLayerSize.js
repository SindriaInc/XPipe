Ext.define('CMDBuildUI.model.map.GeoExternalLayerSize', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'Name',
        type: 'string'
    }, {
        name: 'Title',
        type: 'string'
    }, {
        name: 'minx',
        type: 'auto',
        mapping: 'BoundingBox:nth(2)/@minx'
    }, {
        name: 'miny',
        type: 'auto',
        mapping: 'BoundingBox:nth(2)/@miny',
        convert: function (v, record) {
            CMDBuildUI.util.Logger.log("RECORD CONVERT MODEL" + record, CMDBuildUI.util.Logger.levels.debug);
        }
    }, {
        name: 'maxx',
        type: 'auto',
        mapping: 'BoundingBox:nth(2)/@maxx'
    }, {
        name: 'maxy',
        type: 'auto',
        mapping: 'BoundingBox:nth(2)/@maxy'
    }, {
        name: 'CRS',
        type: 'string',
        mapping: 'BoundingBox:nth(2)/@CRS'
    }]
});
