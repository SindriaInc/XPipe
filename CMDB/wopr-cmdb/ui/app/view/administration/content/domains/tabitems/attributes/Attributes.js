Ext.define('CMDBuildUI.view.administration.content.domains.tabitems.attributes.Attributes', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.components.attributes.*',
        'CMDBuildUI.view.administration.content.domains.tabitems.attributes.AttributesController',
        'CMDBuildUI.view.administration.content.domains.tabitems.attributes.AttributesModel'
    ],

    alias: 'widget.administration-content-domains-tabitems-attributes-attributes',
    controller: 'administration-content-domains-tabitems-attributes-attributes',
    viewModel: {
        type: 'administration-content-domains-tabitems-attributes-attributes'
    },
    layout: 'fit',
    ui: 'administration-tabandtools',
    config: {
        objectTypeName: null
    },
    autoEl: {
        'data-testid': 'administration-content-domains-tabitems-attributes-attributes'
    },
    items: [{
        xtype: 'administration-components-attributes-grid-grid'
    }]

});