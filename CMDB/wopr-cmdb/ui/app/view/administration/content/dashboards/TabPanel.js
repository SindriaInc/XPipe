
Ext.define('CMDBuildUI.view.administration.content.dashboards.TabPanel', {
    extend: 'Ext.tab.Panel',

    alias: 'widget.administration-content-dashboards-tabpanel',
    controller: 'administration-content-dashboards-tabpanel',
    viewModel: {
        type: 'administration-content-dashboards-tabpanel'
    },
    requires: [
        'CMDBuildUI.view.administration.content.dashboards.TabPanelController',
        'CMDBuildUI.view.administration.content.dashboards.TabPanelModel',
        'CMDBuildUI.view.administration.content.dashboards.PropertiesTab',
        'CMDBuildUI.view.administration.content.dashboards.PermissionsTab'
    ],

    tabPosition: 'top',
    tabRotation: 0,
    cls: 'administration-mainview-tabpanel',
    ui: 'administration-tabandtools',
    scrollable: true,
    forceFit: true,
    layout: 'fit',

    bind: {
        activeTab: '{activeTab}'
    },
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',

        items: [{
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.administration.dashboards.adddashboard,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.dashboards.adddashboard'
            },
            ui: 'administration-action-small',
            reference: 'adddashboard',
            itemId: 'adddashboard',
            autoEl: {
                'data-testid': 'administration-dashboard-toolbar-addDashboardBtn'
            },
            bind: {
                disabled: '{!toolAction._canAdd}'
            }
        }, {
            xtype: 'admin-globalsearchfield',
            objectType: 'dashboards'
        }, {
            xtype: 'tbfill'
        }, {
            xtype: 'tbtext',
            dock: 'right',
            itemId: 'dashboardGridCounter'
        }]
    }],
    listeners: {
        itemupdated: 'onItemUpdated',
        cancelcreation: 'onCancelCreation',
        cancelupdating: 'onCancelUpdating',
        afterlayout: function (panel) {
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
        }
    },

    initComponent: function () {
        var vm = this.getViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.navigation.dashboards);
        this.callParent(arguments);
    }
});
