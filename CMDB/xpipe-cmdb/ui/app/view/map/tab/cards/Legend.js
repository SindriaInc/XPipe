
Ext.define('CMDBuildUI.view.map.tab.cards.Legend', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.map.tab.cards.LegendController',
        'CMDBuildUI.view.map.tab.cards.LegendModel'
    ],
    controller: 'map-tab-cards-legend',
    viewModel: {
        type: 'map-tab-cards-legend'
    },
    alias: 'widget.map-tab-cards-legend',
    config: {
        theThematism: undefined,
        highlightselected: true
    },
    publishes: [
        'highlightselected'
    ],
    twoWayBindable: [
        'highlightselected'
    ],

    scrollable: true,

    items: [{
        xtype: 'checkboxfield',
        boxLabel: CMDBuildUI.locales.Locales.thematism.highlightSelected,
        localized: {
            boxLabel: 'CMDBuildUI.locales.Locales.thematism.highlightSelected'
        },
        checked: true,
        padding: '0 0 0 10',
        bind: {
            value: '{map-tab-cards-legend.highlightselected}'
        }
    }, {
        xtype: 'thematisms-thematism-rules',
        reference: 'thematisms-thematism-rules',
        forceFit: true
    }],

    updateTheThematism: function (value, oldvalue) {
        var view = this.lookupReference('thematisms-thematism-rules');
        view.getViewModel().set('theThematism', value);

        if (value) {
            view.setrules();
        }
    }
});
