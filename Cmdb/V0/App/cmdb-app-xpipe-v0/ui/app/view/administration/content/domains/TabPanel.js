
Ext.define('CMDBuildUI.view.administration.content.domains.TabPanel', {
    extend: 'Ext.tab.Panel',

    alias: 'widget.administration-content-domains-tabpanel',
    controller: 'administration-content-domains-tabpanel',

    requires: [
        'CMDBuildUI.view.administration.content.domains.TabPanelController'
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
    
    listeners: {

        itemupdated: 'onItemUpdated',
        cancelcreation: 'onCancelCreation',
        cancelupdating: 'onCancelUpdating'

    }
});
