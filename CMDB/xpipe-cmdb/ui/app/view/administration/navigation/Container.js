Ext.define('CMDBuildUI.view.administration.navigation.Container', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.navigation.ContainerController',
        'CMDBuildUI.view.administration.navigation.ContainerModel',

        'CMDBuildUI.view.administration.navigation.Tree'
    ],

    xtype: 'administration-navigation-container',
    controller: 'administration-navigation-container',
    viewModel: {
        type: 'administration-navigation-container'
    },

    title: CMDBuildUI.locales.Locales.main.navigation,
    bind:{
        title: '{navTitle}'
    },
    layout: 'container',
    width: 250,
    scrollable: true,

    ui: 'administration',
    autoEl: {
        'data-testid': 'administration-navigation-container'
    },
    cls: Ext.baseCSSPrefix + 'panel-bold-header',
    items: [{
        xtype: 'administration-navigation-tree',
        store:'administration.MenuAdministration',
        bind:{
            selected: '{startingNode}'
        }
    }],
    initComponent: function () {
        this.callParent(arguments);
    }
});