Ext.define('CMDBuildUI.model.map.navigation.NavigationTreeDescription', {
    extend: 'CMDBuildUI.model.base.Base',
    
    fields: [{
        name: 'description',
        type: 'string'
    }, {
        name: 'nodes',
        type: 'auto'
    }],
    proxy: {
        type: 'baseproxy',
        url: CMDBuildUI.util.api.DomainTrees.getDomainTrees()
    }
});
