Ext.define('CMDBuildUI.view.map.tab.cards.LegendModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.map-tab-cards-legend',
    formulas: {

        'update-highlightselected': {
            bind: {
                highlightselected: '{map-tab-cards-legend.highlightselected}'
            },
            get: function (data) {
                var view = this.getView();
                view.setHighlightselected(data.highlightselected);
            }
        }
    }
});