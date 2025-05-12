
Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.Values',{
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.ValuesController',
        'CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.ValuesModel'
    ],
    
    alias: 'widget.administration-content-lookuptypes-tabitems-values-values',
    controller: 'administration-content-lookuptypes-tabitems-values-values',
    viewModel: {
        type: 'administration-content-lookuptypes-tabitems-values-values'
    },
    layout: 'fit',
    ui: 'administration-tabandtools',
    config: {
        objectTypeName: null
    },
    autoEl: {
        'data-testid': 'administration-content-lookuptypes-tabitems-values-values'
    },
    items:[{xtype: 'administration-content-lookuptypes-tabitems-values-grid-grid'}]
    
});
