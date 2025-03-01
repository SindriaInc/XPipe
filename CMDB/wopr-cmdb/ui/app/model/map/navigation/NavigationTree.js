Ext.define('CMDBuildUI.model.map.navigation.NavigationTree', {
    extend: 'CMDBuildUI.model.base.Base',
    
    fields: [{
        name: '_id',
        type: 'string',
        mapping: '_id'
    }, {
        name: '_type',
        type: 'string',
        mapping: 'type' //This doesn't works when using ext.create('CMDBuildUI.model.map.navigation.NavigationTree', {...}) the _type field is always empty string
    },{
        name: 'description',
        type: 'string',
        mapping: 'description'
    },{
        name: 'parentid',
        type: 'number',
        mapping: 'parentid'
    },{
        name: 'parenttype',
        type: 'string',
        mapping: 'parenttype'
    }, {
        name: 'navTreeNodeId',
        type: 'string',
        mapping: 'navTreeNodeId'
    }]
    //Missing cmp from server with the navId property "navigationTreeId"
    
    /* proxy: {
        type: 'baseproxy',
        url: CMDBuildUI.util.api.
    } */
});
