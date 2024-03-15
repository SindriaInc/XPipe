
Ext.define('CMDBuildUI.view.graph.tab.cards.Card', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.graph.tab.cards.CardController',
        'CMDBuildUI.view.graph.tab.cards.CardModel'
    ],

    alias: 'widget.graph-tab-cards-card',
    controller: 'graph-tab-cards-card',
    viewModel: {
        type: 'graph-tab-cards-card'
    },
    autoScroll: true,
    items: [{
        xtype: 'displayfield',
        labelAlign: "left",
        labelWidth: 'auto',//it works but no documentation on sencha docs
        cls: Ext.baseCSSPrefix + 'process-action-field',
        padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
        bind: {
            fieldLabel: '{mode}',
            value: '{valueLabel}'
        }
    }] //Adding components is added in the model

});
