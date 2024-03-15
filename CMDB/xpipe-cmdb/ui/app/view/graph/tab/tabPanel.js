
Ext.define('CMDBuildUI.view.graph.tab.tabPanel', {
    extend: 'Ext.tab.Panel',

    requires: [
        'CMDBuildUI.view.graph.tab.tabPanelController',
        'CMDBuildUI.view.graph.tab.tabPanelModel'
    ],
    alias: 'widget.graph-tab-tabpanel',
    controller: 'graph-tab-tabpanel',
    deferredRender: false,
    viewModel: {
        type: 'graph-tab-tabpanel'
    },
    items: [{
        title: CMDBuildUI.locales.Locales.relationGraph.card,
        localized: {
            title: 'CMDBuildUI.locales.Locales.relationGraph.card'
        },
        xtype: 'graph-tab-cards-card',
        reference: 'graph-tab-cards-card'
    }, {
        // text: '{relationLenght}', //TODO: translate NOTE: This bind doesnt work.
        xtype: 'graph-tab-cards-relations',
        reference: 'graph-tab-cards-relations'
    }, {
        title: CMDBuildUI.locales.Locales.relationGraph.cardList,
        localized: {
            title: 'CMDBuildUI.locales.Locales.relationGraph.cardList'
        },
        reference: 'graph-tab-cards-listcard',
        xtype: 'graph-tab-cards-listcard'
    }, {
        title: CMDBuildUI.locales.Locales.relationGraph.classList,
        localized: {
            title: 'CMDBuildUI.locales.Locales.relationGraph.classList'
        },
        xtype: 'graph-tab-cards-listclass'
    }],

    activeTab: 0,
    dockedItems:[{
        xtype: 'graph-tab-bottominfo-bottominfo',
        dock: 'bottom'
    }]
});
