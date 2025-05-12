Ext.define('CMDBuildUI.view.graph.tab.cards.ListCardModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.graph-tab-cards-listcard',
    data: {
        name: 'CMDBuildUI'
    },
    formulas: {
        setActiveRows: {
            bind: {
                selectedNodes: '{selectedNode}'
            },
            get: function (data) {
                var selectedNodes = data.selectedNodes;
                if (!selectedNodes || selectedNodes.length === 0) return;

                var grid = this.getView();
                var store = grid.getStore();
                var selectionModel = grid.getSelectionModel();
                var records = [];

                selectedNodes.forEach(function (selectedNode) {
                    records.push(store.findRecord('_destinationId', selectedNode.id));
                }, this);

                selectionModel.select(records, false, true);//This doesn't fire selectionchange event
            }
        }
    },
    stores: {
        listCardStore: {
            model: 'CMDBuildUI.model.domains.Relation',
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            sorters: [{
                property: 'destTypeDescription',
                direction: 'ASC'
            }, {
                property: '_destinationCode',
                direction: 'ASC'
            }, {
                property: '_destinationDescription',
                direction: 'ASC'
            }]
            // autoLoad: true
        }
    }
});
