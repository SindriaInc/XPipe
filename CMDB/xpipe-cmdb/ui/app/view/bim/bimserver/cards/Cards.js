Ext.define('CMDBuildUI.view.bim.bimserver.tab.cards.Cards', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.bim.bimserver.tab.cards.CardsController',
        'CMDBuildUI.view.bim.bimserver.tab.cards.CardsModel'
    ],
    alias: 'widget.bim-bimserver-tab-cards-cards',
    controller: 'bim-bimserver-tab-cards-cards',
    viewModel: {
        type: 'bim-bimserver-tab-cards-cards'
    },

    reference: 'bim-bimserver-tab-cards-cards',
    config: {
        objectId: undefined,
        objectTypeName: undefined,
        theObject: undefined
    },
    publishes: [
        'objectId',
        'objectTypeName',
        'theObject'
    ],
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
    }]
});
