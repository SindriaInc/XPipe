Ext.define('CMDBuildUI.view.administration.content.bim.projects.View', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.bim.projects.ViewController',
        'CMDBuildUI.view.administration.content.bim.projects.ViewModel'
    ],

    alias: 'widget.administration-content-bim-projects-view',
    controller: 'administration-content-bim-projects-view',
    viewModel: {
        type: 'administration-content-bim-projects-view'
    },

    loadMask: true,
    defaults: {
        textAlign: 'left',
        scrollable: true
    },
    layout: 'border',
    items: [{
        xtype: 'administration-content-bim-projects-topbar',
        region: 'north'
    },
    {
        xtype: 'administration-content-bim-projects-grid',
        region: 'center'
    }],

    listeners: {
        afterlayout: function (panel) {
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
        }
    },

    initComponent: function () {
        var vm = this.getViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.bim.projects);
        this.callParent(arguments);
    }
});