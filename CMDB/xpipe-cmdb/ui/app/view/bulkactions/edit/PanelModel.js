Ext.define('CMDBuildUI.view.bulkactions.edit.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.bulkactions-edit-panel',

    data: {
        theObject: {}
    },

    stores: {
        attributes: {
            model: 'CMDBuildUI.model.Attribute',
            proxy: 'memory',
            data: '{attributeslist}',
            filters: [{
                property: 'writable',
                value: true
            }, {
                property: 'name',
                operator: 'notin',
                value: ['Notes']
            }],
            sorters: ['_description_translation'],
            grouper: {
                property: '_group_description_translation'
            }
        }
    }

});
