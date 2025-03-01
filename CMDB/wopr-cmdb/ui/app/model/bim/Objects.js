Ext.define('CMDBuildUI.model.bim.Objects', {
    extend: 'CMDBuildUI.model.base.Base',
    fields: [{
        name: 'ifcName',
        type: 'string'
    }, {
        name: 'name',
        type: 'string',
        calculate: function (data) {
            return data.ifcName.replace('Ifc', '');
        }
    }, {
        name: 'text',
        type: 'string'
    }, {
        name: 'oid',
        type: 'int'
    }, {
        name: 'globalId',
        type: 'string'
    }, {
        name: 'checked',
        type: 'boolean',
        persist: false
    }, {
        name: 'object',
        type: 'auto'
    }, {
        name: 'project',
        type: 'auto'
    }],
    convertOnSet: false
})