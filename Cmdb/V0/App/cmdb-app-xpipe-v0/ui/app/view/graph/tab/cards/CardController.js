Ext.define('CMDBuildUI.view.graph.tab.cards.CardController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.graph-tab-cards-card',
    listen: {
        store: {
            '#relationStore': {
                clear: 'onRelationStoreClear'
            }
        }
    },
    /**
     * @param {Ext.data.Store} relationStore
     * @param {Object} eOpts 
     */
    onRelationStoreClear: function (relationStore, eOpts) {
        this.disableCard();
    },

    /**
     * disable relation tab
     */
    disableCard: function () {
        var containerViewModel = this.getView().up('graph-tab-tabpanel');
        var relationTab = containerViewModel.lookupReference('graph-tab-cards-card');

        relationTab.setDisabled(true);
    }
});
