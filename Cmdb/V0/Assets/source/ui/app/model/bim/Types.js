Ext.define('CMDBuildUI.model.bim.Types', {
    extend: 'CMDBuildUI.model.base.Base',
    fields: [{
        name: 'name', type: 'string',
        calculate: function (data) {
            return data.ifcName.replace('Ifc', '');
        }
    }, {
        name: 'ifcName', type: 'string'
    }, {
        name: 'qt', type: 'int'
    }, {
        name: 'clicks', type: 'int'
    }, {
        name: 'objects',
        type: 'auto',
        defaultValue: []
    }]
})