Ext.define('CMDBuildUI.view.graph.tab.tabPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.graph-tab-tabpanel',
    data: {
        name: 'CMDBuildUI',
        relationLenghtValue: 0
    },
    formulas: {
        startFirstNode: {
            bind: {
                selectedNode: '{selectedNode}'
            },
            get: function (data) {
                if (!data.selectedNode || data.selectedNode.length === 0) return;

                var node = data.selectedNode[0];
                if (node.id != null && node.type != null) {
                    var view = this.getView();
                    var card = view.lookupReference('graph-tab-cards-card');
                    var relation = view.lookupReference('graph-tab-cards-relations');

                    if (card.isDisabled()) {
                        card.setDisabled(false);
                        relation.setDisabled(false);
                        view.setActiveTab(0);
                    }
                }
            }
        },
        relationLenght: {
            bind: {
                relationLenghtValue: '{relationLenghtValue}'
            },
            get: function (data) {
                var relationLenghtvalue = data.relationLenghtValue;
                var view = this.getView().lookupReference('graph-tab-cards-relations');
                view.setTitle(CMDBuildUI.locales.Locales.relationGraph.cardRelations + ' (' + relationLenghtvalue + ')'); //TODO:Translate NOTE: Make the get function working correctly
            }
        }
    }
});
