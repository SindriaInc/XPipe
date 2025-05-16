Ext.define('CMDBuildUI.view.graph.tab.cards.RelationsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.graph-tab-cards-relations',
    data: {
        name: 'CMDBuildUI'
    },
    formulas: {
        updateStore: {
            bind: {
                nodeId: '{selectedNode}'
            },
            get: function (data) {
                var nodeId = data.nodeId;
                if (!nodeId || nodeId.length === 0) return;
                nodeId = nodeId[0].id;

                var cy = this.get('cy');
                var gridStore = this.getView().getStore();
                gridStore.removeAll();

                var store = this.get('relationStore');
                var edges = cy.$('#' + nodeId).connectedEdges().toArray();

                //Finds the relation of the selected node
                edges.forEach(function (edge) {
                    var record = store.findRecord('_id', edge.id());
                    if (record) {
                        var newRecord;

                        if (record.get('_sourceId') == nodeId) {
                            newRecord = record.clone();
                        } else if (record.get('_destinationId') == nodeId) { //Need to switch destination and source 

                            newRecord = Ext.create('CMDBuildUI.model.graph.EdgesRelation', {
                                _destinationCode: record.get('_sourceCode'),
                                _destinationDescription: record.get('_sourceDescription'),
                                _destinationType: record.get('_sourceType'),
                                _type: record.get('_type')
                            });
                        }

                        //copy hasMany data (_node)
                        newRecord.nodes().insert(0, record.nodes().getRange());

                        //handles compound name in destination. Used to get class description from class.Class store
                        var destinationType = newRecord.get('_destinationType');
                        destinationType.includes('compound_') ? destinationType = destinationType.replace('compound_', '') : null;

                        //get class description and set it
                        var destTypeDescription = CMDBuildUI.util.helper.ModelHelper.getObjectDescription(destinationType);
                        newRecord.set('destTypeDescription', destTypeDescription); //NOTE: wait for specification definitio

                        gridStore.insert(0, newRecord);
                    }
                }, this);

                //avoid dirty records (red corner in top left)
                gridStore.commitChanges();

                //sets viewModel variable
                var containerViewModel = this.getView().up('graph-tab-tabpanel').getViewModel();
                containerViewModel.set('relationLenghtValue', edges.length);


                /**
                 * Sets the groups collapsed or expanded
                 */
                var feature = this.getView().getView().findFeature('grouping');
                var edgesRelationStore = this.get('edgesRelationStore');
                var groups = edgesRelationStore.getGroups().getRange();

                var map = this.get('_map_Node_CompoundName');

                groups.forEach(function (group) {
                    var bool = false;
                    var groupKey = group.getGroupKey();
                    if (nodeId.includes('compound') || !groupKey.includes('compound')) {
                        feature.expand(groupKey, { skip: { beforegroupexpand: true } });
                    } else {
                        var items = group.items[0].nodes().getRange();
                        for (var i = 0; i < items.length && !bool; i++) {
                            var gpNode = items[i];
                            var gpNodeId = gpNode.get('_destinationId');
                            if (map[gpNodeId] != null) {
                                feature.collapse(groupKey, { skip: { beforegroupcollapse: true } });
                                bool = true;
                            }
                        }
                        if (!bool) {
                            feature.expand(groupKey, { skip: { beforegroupexpand: true } });
                        }
                    }
                }, this);

            }
        }

    },
    stores: {
        edgesRelationStore: {
            model: 'CMDBuildUI.model.graph.EdgesRelation',
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            grouper: {
                groupFn: function (item) {
                    return Ext.String.format('{0}_{1}', item.get('_type'), item.get('_direction'));
                }
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
