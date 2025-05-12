Ext.define('CMDBuildUI.view.graph.tab.cards.ListClassModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.graph-tab-cards-listclass',
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

                var classTarget = selectedNodes[0].type;
                var selectionModel = this.getView().getSelectionModel();
                //tells if all the selected nodes have the same class
                for (var i = 1; i < selectedNodes.length; i++) {
                    if (selectedNodes[i].type != classTarget) {
                        selectionModel.deselectAll(true);
                        return;
                    }
                }

                var store = this.getView().getStore();
                var record = store.findRecord('classTarget', classTarget);
                //not all nodes of that class are selected
                if (record.get('qt') != selectedNodes.length) {
                    selectionModel.deselectAll(true);
                    return;
                }

                selectionModel.select(record, false, true);//This doesn't fire selectionchange event
            }
        }
    },
    stores: {
        listClassStore: {
            model: 'CMDBuildUI.model.graph.ListClass',
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            sorters: {
                property: 'destTypeDescription',
                direction: 'ASC'
            }
            // autoLoad: true
        }
    }
});
