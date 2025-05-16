
Ext.define('CMDBuildUI.view.graph.tab.bottomInfo.BottomInfo', {
    extend: 'Ext.toolbar.Toolbar',

    requires: [
        'CMDBuildUI.view.graph.tab.bottomInfo.BottomInfoController',
        'CMDBuildUI.view.graph.tab.bottomInfo.BottomInfoModel'
    ],

    controller: 'graph-tab-bottominfo-bottominfo',
    viewModel: {
        type: 'graph-tab-bottominfo-bottominfo'
    },
    alias: 'widget.graph-tab-bottominfo-bottominfo',
    defaults: {
        labelAlign: "left",
        labelWidth: 'auto',//it works but no documentation on sencha docs
        cls: Ext.baseCSSPrefix + 'process-action-field',
        padding: CMDBuildUI.util.helper.FormHelper.properties.padding
    },
    items: [{
        xtype: 'displayfield',
        fieldLabel: CMDBuildUI.locales.Locales.relationGraph.nodes,
        localized: {
            fieldLabel: 'CMDBuildUI.locales.Locales.relationGraph.nodes'
        },
        bind: {
            value: '{nodesNumber}'
        }
    }, {
        xtype: 'displayfield',
        fieldLabel: CMDBuildUI.locales.Locales.relationGraph.edges,
        localized: {
            fieldLabel: 'CMDBuildUI.locales.Locales.relationGraph.edges'
        },
        bind: {
            value: '{edgesNumber}'
        }
    }]
});
