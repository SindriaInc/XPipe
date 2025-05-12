Ext.define('CMDBuildUI.view.administration.content.bim.projects.Topbar', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.bim.projects.TopbarController'
    ],

    alias: 'widget.administration-content-bim-projects-topbar',
    controller: 'administration-content-bim-projects-topbar',
    viewModel: {

    },

    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true
    },

    forceFit: true,
    loadMask: true,

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',

        items: [{
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.administration.bim.addproject,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.bim.addproject'
            },
            ui: 'administration-action-small',
            reference: 'addproject',
            itemId: 'addproject',
            iconCls: 'x-fa fa-plus',
            autoEl: {
                'data-testid': 'administration-bim-projects-addLayerBtn'
            },
            bind: {
                disabled: '{!toolAction._canAdd}'
            }
        }, {
            xtype: 'localsearchfield',
            gridItemId: '#bimProjectsGrid'
        }]
    }]
});