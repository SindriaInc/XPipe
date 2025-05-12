Ext.define('CMDBuildUI.view.graph.GraphContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.graph-graphcontainer',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender',
            beforedestroy: 'onBeforeDestroy'
        },

        //graph-canvas-bottommenu-canvasmenu
        '#sliderLevel': {
            changecomplete: 'onSliderChangeComplete',
            change: 'onSliderChange',
            beforerender: 'onSliderBeforeRender'
        },
        '#sliderValue': {
            beforerender: 'onSliderValueBeforeRender'
        }
    },
    listen: {
        global: {
            sceneselectednode: 'onSceneSelectedNode',
            doubleclicknode: 'onDoubleClickNode'
        },
        store: {
            '#relationStore': {
                clear: 'onRelationStoreClear'
            }
        }
    },

    /**
    * Adds the canvas with the menu in the relation graph popup
    * @param container
    * @param eOpts
    */
    onBeforeRender: function (container, eOpts) {
        this.loadAllDomainTrees();
    },

    /**
     * Adds the canvas with the menu in the relation graph popup
     * @param container
     * @param eOpts
     */
    onAfterRender: function (container, eOpts) {
        var vm = container.getViewModel();
        vm.set('node', { //NOTE: change name
            _type: container._type, //class name
            _id: container._id, //card id
            _code: container._code,
            _description: container._description
        });
    },

    /**
     * @param {Ext.Component} container
     * @param {Object} eOpts
     */
    onBeforeDestroy: function (container, eOpts) {
        if (this.loadMask) {
            CMDBuildUI.util.Utilities.removeLoadMask(this.loadMask);
        }
        CMDBuildUI.graph.threejs.SceneUtils.destroyScene();
    },

    /**
     * @param id
     * @param type
     * @param {String} description
     * @param {String} code
     * @param treeId
     * @param {Object} config Config to pass in the callback
     * @param callback the callback function 
     * @returns the records related to that card
     */
    proxyNodeRelation: function (id, type, description, code, treeId, config, callback) {
        var me = this;
        var newStore = Ext.create('Ext.data.Store', {
            model: "CMDBuildUI.model.domains.Relation",
            proxy: {
                url: Ext.String.format("/classes/{0}/cards/{1}/relations", type, id),
                type: 'baseproxy'
            },
            autoLoad: true,
            autoDestroy: true
        });
        newStore.load({
            params: {
                limit: -1,
                detailed: true
            },
            callback: function (records, operation, success) {
                if (this.getView()) {
                    Ext.Array.forEach(records, function (item, index, allitems) {
                        item.set("_destinationCode", Ext.String.htmlEncode(item.get("_destinationCode")));
                        item.set("_destinationDescription", Ext.String.htmlEncode(item.get("_destinationDescription")));
                    });
                    callback.call(me, id, type, description, code, treeId, records, config, operation, success);
                }
            },
            scope: this
        });
    },

    /**
     * @param {[NodeDataStructure]} nodes
     * @param {Object} config 
     * @type {Object} config.callback Function to launch when the loading of all the nodes is complete
     * callback.fn {Function}
     * callback.scope {Object}
     */
    nodeRelations: function (nodes, config) {
        config.l = nodes.length;
        //LoadMask
        if (!this.loadMask && nodes.length != 0) {
            this.loadMask = CMDBuildUI.util.Utilities.addLoadMask(this.getView());
        }

        nodes.forEach(function (node) {
            this.waitingCallbackResponse = true;
            this.proxyNodeRelation(node.getId(), node.getType(), node.getDescription(), node.getCode(), node.getTreeId(), config, this.ProxyNodeRelationCallback);
        }, this);
    },


    /**
     * 
     * @param {*} id 
     * @param {*} type 
     * @param {String} description
     * @param {String} code
     * @param {[Ext.data.Model]} records 
     * @param {*} operation 
     * @param {*} success 
     */
    ProxyNodeRelationCallback: function (id, type, description, code, treeId, records, config, operation, success) {
        records = records || [];
        config = config || {};
        Ext.applyIf(config, {
            compound: true,
            ignoreL: false,
            l: 1
        });

        config.l = config.l - 1;
        var tmpRecords = [];

        /**
         * removes record on wich i have not permission due to server error
         */
        records.forEach(function (record) {
            var recType = record.get('_destinationType');
            var classPermission = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(recType);

            var domainStore = Ext.getStore('domains.Domains');
            var domainPermission = domainStore.find('_id', record.get('_type'));

            if (classPermission != null && domainPermission != -1) {
                record.set('_sourceDescription', description);
                record.set('_sourceCode', code);
                tmpRecords.push(record);
            }
        }, this);

        if (this.ignoreCallbackResponse == false) {
            /**
            * removes nodes on wich i have not visibility du to navigation tree selected
            */
            records = this.applyNavigationTree(tmpRecords, treeId, type);

            var recordCytoscape;
            if (config.compound === true) {
                // var recordNodeDataStructure = compoundNodeDataStructure(id, records);
                recordCytoscape = this.compoundCytoscape(id, type, description, code, records); //HACK: Here the records are hugely modifie
            } else {
                recordCytoscape = records;
            }

            this.fillMemoryNodeDataStructure(id, records);
            this.fillCytoscape(id, recordCytoscape);
        }

        if (config.l === 0 || config.ignoreL) {
            // console.log('loadingEnd');
            this.waitingCallbackResponse = false;
            if (this.ignoreCallbackResponse == true) {
                this.ignoreCallbackResponse = false;
                return;
            }
            //LoadMask
            CMDBuildUI.util.Utilities.removeLoadMask(this.loadMask);
            delete this.loadMask;
            if (config.callback != null) {
                config.callback.fn.call(config.callback.scope);
            }
            this.fireEvent('acquisitionend', this.getView());
        }
    },

    /**
     * @param {String} mode The defined mode
     * @param {Object} config
     * @type {String[]} config.nodes
     * @type {Number} config.depth
     * @type {object} config.callback
     *  config.callback: {
     *      fn: @type {Function},
     *      scope: @type {Object}
     *  }
     */
    graphRelation: function (mode, config) {
        var me = this;

        config = config || {};
        Ext.applyIf(config, {
            scope: me
        });

        var memoryNodeDataStructure = this.getViewModel().get('memoryNodeDataStructure');
        switch (mode) {
            // case 'childOfDepth':
            //     this.childOdDepth(config.depth, {
            //         fn: function () {
            //             var cy = this.getViewModel().get('cy');
            //             CMDBuildUI.graph.threejs.SceneUtils.fillScene(cy);

            //             /** */
            //             if (config.callback) {
            //                 config.callback.fn.call(config.callback.scope || this);
            //             }
            //         },
            //         scope: this
            //     });
            //     break;
            case 'childOfMultiDepth':
                //Ideal for the slider changes

                function recCallback() {
                    var lastDepth = memoryNodeDataStructure.getLastDepth();
                    if (lastDepth < config.depth && !memoryNodeDataStructure.isComplete()) {
                        this.childOfDepth(lastDepth, {
                            fn: recCallback,
                            scope: this
                        });
                    } else {
                        var cy = this.getViewModel().get('cy');
                        CMDBuildUI.graph.threejs.SceneUtils.fillScene(cy);

                        /**
                         * callback call of graphRelation
                         */
                        if (config.callback) {
                            config.callback.fn.call(
                                config.callback.scope || this,
                                config.callback.arguments.ids || null
                            );
                        }
                    }
                }

                var lastDepth = memoryNodeDataStructure.getLastDepth();
                if (lastDepth < config.depth) {
                    this.childOfDepth(lastDepth, {
                        fn: recCallback,
                        scope: this
                    });
                }
                break;
            case 'relationOf':
                //ideal for multiple nodes at unknow depth
                var tmpNodes = [];
                config.nodes.forEach(function (id) {
                    tmpNodes.push(memoryNodeDataStructure.getNode(id));
                }, this);

                this.nodeRelations(tmpNodes,
                    {
                        callback: {
                            fn: function () {
                                var cy = this.getViewModel().get('cy');
                                CMDBuildUI.graph.threejs.SceneUtils.fillScene(cy);

                                /**
                                 * callback call of graphRelation
                                 */
                                if (config.callback) {
                                    config.callback.fn.call(config.callback.scope || this, config.callback.arguments || null);
                                }
                            },
                            scope: this
                        }
                    });
                break;
            case 'nodesLocal':
                //Used for when ther's no need to make proxy calls
                var nodes = config.nodeRelationsObject;
                var returned = [];
                for (var id in nodes) {
                    if (!this.loadMask) {
                        this.loadMask = CMDBuildUI.util.Utilities.addLoadMask(this.getView());
                    }
                    var node = memoryNodeDataStructure.getNode(id);
                    this.ProxyNodeRelationCallback(
                        id,
                        node.getType(),
                        node.getDescription(),
                        node.getCode(),
                        node.getTreeId(),
                        nodes[id],
                        {
                            compound: false,
                            ignoreL: true
                        });
                    returned.push(nodes[id]);
                }
                var cy = this.getViewModel().get('cy');
                CMDBuildUI.graph.threejs.SceneUtils.fillScene(cy);

                config.callback.fn.call(config.callback.fn.scope || config.scope, returned);
                break;
            default:
                console.error('No mode found');
        }

    },

    /**
     * @param {Number} depth
     * @param {Object} callback
     * callback.fn {Function}
     * callback.scope {Object}
     */
    childOfDepth: function (depth, callback) {
        var memoryNodeDataStructure = this.getViewModel().get('memoryNodeDataStructure');
        var nodes = memoryNodeDataStructure.getNodesAtdepth(depth, { all: false });
        this.nodeRelations(nodes, { callback: callback });
    },

    /**
     * @param {String} id
     * @returns 
     */
    getNodesFromCompound: function (id) {

    },

    /**
     * @param {Number} id node id
     * @param {Ext.data.Mode} records the node childs
     */
    fillCytoscape: function (id, records) {
        var cy = this.getViewModel().get('cy');
        var relationStore = this.getStore('relationStore');
        var cId, cType; //child id, child type

        records.forEach(function (record) {
            cId = record.get('_destinationId');
            cType = record.get('_destinationType');

            if (!this.isLoaded(cId, cType)) {
                /**
                 * Fill cytoscape noden and edges
                 */
                cy.add(this.cytoNode(record, id));
                cy.add(this.cytoEdge(record, id));

                /**
                 * fill the memory store;
                 */
                relationStore.insert(0, record); //HACK:Here are added the information in relationStore
            } else {
                if (cy.$('#' + record.get('_id')).length === 0) {
                    /** 
                     * Fill cytoscape edge
                    */
                    cy.add(this.cytoEdge(record, id));

                    /**
                    * fill the memory store;
                    */
                    relationStore.insert(0, record); //HACK:Here are added the information in relationStore
                }
            }
        }, this);
    },

    /**
     * @param {String} id The id of a specific node
     * @param {[Ext.data.Model]} records  The relations of the node whit id = id //CMDBuildUI.model.domains.Relation
     * @returns {[Ext.data.Model]} the records to insert into the cytoscape
     */
    compoundNodeDataStructure: function (id, records) {
        return records;
    },

    /**
     * @param {String} id The id of a specific node
     * @param {String} type the type of the specific node
     * @param {String} description the description of the node  
     * @param {String} code
     * @param {[Ext.data.Model]} records  The relations of the node whit id = id //CMDBuildUI.model.domains.Relation
     * @returns {[Ext.data.Model]} the records to insert into the cytoscape
     */
    compoundCytoscape: function (id, type, description, code, records) {
        var clusteringThreshold = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.relgraph.clusteringThreshold); //HACK:set the trashold
        var memoryNodeDataStructure = this.getViewModel().get('memoryNodeDataStructure');
        var _map_Node_CompoundName = this.getViewModel().get('_map_Node_CompoundName');
        var cy = this.getViewModel().get('cy');
        var newRecords = [];
        var oldRecords = [];

        records.forEach(function (record) {
            var node = memoryNodeDataStructure.getNode(record.get('_destinationId'));
            if (node != null) {
                oldRecords.push(record);
            } else {
                newRecords.push(record);
            }
        }, this);

        /**
         * handles the relations with already existing nodes
         */
        tmpOldRecords = [];
        oldRecords.forEach(function (oldRecord) {
            var compoundId = _map_Node_CompoundName[oldRecord.get('_destinationId')]; //Handle the case in wich the existing nodes are compound nodes
            var newRecord;

            if (compoundId) { //Se la relation ha come target un nodo che e' compound entra
                newRecord = tmpOldRecords.find(function (el) { // Verifica di non aver creato la relazione in un ciclo precedente
                    if (el.get('_destinationId') == compoundId) {
                        return el;
                    }
                }, this);
                if (!newRecord) {   // Se non e' stato creato entra e crealo
                    //This check could be removed
                    var compoundNode = cy.nodes('#' + compoundId).toArray()[0];
                    if (!compoundNode) {
                        console.error('Compound Node mus be bresent in this point');
                    }

                    //LOOK: Here is created the relation between new nodes and existing ones under compound status
                    // var tmpRecord = relationStore.findRecord('_id', compoundName);
                    newRecord = Ext.create('CMDBuildUI.model.domains.Relation', {
                        _id: 'compound_' + oldRecord.get('_id') + '_ID', //the id of the relation remains the same
                        _destinationType: 'compound_' + oldRecord.get('_destinationType'),
                        _destinationId: compoundId,
                        _type: 'compound_' + oldRecord.get('_type'),
                        _destinationDescription: 'compound node',
                        _is_direct: oldRecord.get('_is_direct'),
                        _sourceId: id,
                        _sourceType: type,
                        _sourceDescription: description,
                        _sourceCode: code
                    });
                    newRecord.nodes().insert(0, oldRecord);
                    //NOTE: can add the sourceId field if needed

                    tmpOldRecords.push(newRecord);
                } else {//se e' stato creato aggiorna in campo nodes
                    newRecord.nodes().insert(0, oldRecord);
                }
            } else {
                tmpOldRecords.push(oldRecord);
            }
        }, this);

        /**
         * Search for new nodes to make as compound
         */
        var bool;
        var countArray = this._countArray(newRecords);
        var returnRecords = [];
        for (var el in countArray) {
            var typeArray = countArray[el];

            if (typeArray.length >= clusteringThreshold) {
                var tmpRecord = this.cytoAdHocRecord(typeArray);

                //Saves information of wich nodesId are in compound node
                typeArray.forEach(function (relationRecord) {
                    bool = true;
                    var id = relationRecord.get('_destinationId');
                    _map_Node_CompoundName[id] = tmpRecord.get('_destinationId');
                }, this);
                returnRecords.push(tmpRecord);

            } else {
                returnRecords = returnRecords.concat(typeArray);
            }
        }
        returnRecords = returnRecords.concat(tmpOldRecords);

        //Updates the viewModel variable if modified
        if (bool === true) {
            this.getViewModel().set('_map_Node_CompoundName', null);
            this.getViewModel().set('_map_Node_CompoundName', _map_Node_CompoundName);
        }

        return returnRecords;
    },

    /**
     * @param {[Ext.data.Model]} records CMDBuildUI.model.domains.Relation
     * @returns {Ext.data.Model} CMDBuildUI.model.domains.Relation
     */
    cytoAdHocRecord: function (records) {

        //Creates the new record
        var newRecord = Ext.create('CMDBuildUI.model.domains.Relation', {
            _id: 'compound_' + records[0].get('_id') + '_ID',
            _destinationType: 'compound_' + records[0].get('_destinationType'), //+ '_DESTINATIONTYPE', //class
            _destinationId: 'compound_' + records[0].get('_destinationId'),
            _type: 'compound_' + records[0].get('_type'),
            _destinatinCode: 'compound node',
            _destinationDescription: 'compound node',
            _is_direct: records[0].get('_is_direct'),
            _sourceId: records[0].get('_sourceId'),
            _sourceType: records[0].get('_sourceType'),
            _sourceCode: records[0].get('_sourceCode'),
            _sourceDescription: records[0].get('_sourceDescription')

        }); //NOTE:Don't change the compound name, it's used

        newRecord.nodes().insert(0, records);
        return newRecord;
    },
    /**
     * 
     * @param {[Ext.data.Model]} records
     * @param {Boolean} skip
     * @returns {Object} The structure of the returned object:
     * {
     *  'type1': [record1, record2, record3],
     *  'type2': [id4, id5, id6]
     * }
     * @type {Ext.data.Model} record1, record2, ...
     *  
     */
    _countArray: function (records) {
        var returned = {};
        records.forEach(function (record) {
            var type = record.get('_type');
            if (returned[type] == null) {
                returned[type] = [record];
            } else {
                returned[type].push(record);
            }
        }, this);
        return returned;
    },
    /**
     * This function fills the memoryNodedataStructure
     * @param {String} id
     * @param {Ext.data.Mode} records
     */
    fillMemoryNodeDataStructure: function (id, records) {
        var memoryNodeDataStructure = this.getViewModel().get('memoryNodeDataStructure');
        var parentNode = memoryNodeDataStructure.getNode(id);
        var childs = [];

        /**
         * Finds the relations that are new
         */
        var oldRecords = [];
        records.forEach(function (record) {
            var node = memoryNodeDataStructure.getNode(record.get('_destinationId'));
            if (!node) {
                oldRecords.push(record);
            }
        }, this);

        var countArray = this._countArray(oldRecords);
        var clusteringThreshold = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.relgraph.clusteringThreshold); //HACK: set trashHold

        if (parentNode) {
            records.forEach(function (record) {
                var config = countArray[record.get('_type')] != null ? countArray[record.get('_type')] : [];
                childs.push({
                    id: record.get('_destinationId'),
                    type: record.get('_destinationType'),
                    description: record.get('_destinationDescription'),
                    code: record.get('_destinationCode'),
                    treeId: record.get('treeId'), //this field is not in the model. Is added when enters in the applyNavTree if selected
                    config: config.length >= clusteringThreshold ? { childLoaded: true } : null
                });
            }, this);

            memoryNodeDataStructure.addChildren(parentNode, childs);
        }
    },

    isLoaded: function (id, type) {
        var cy = this.getViewModel().get('cy');
        var nd = cy.nodes('#' + id);

        switch (nd.length) {
            case 0:
                return false;
            case 1:
                return true;
            default:
                console.error("Can't be a different number of elements");
        }
    },

    /**
     * creates the node structure for the cytoscape node
     * @param {Ext.record} record the source of info to extract
     * @param {String} parentId
     * @returns an addable node in the cytoscape graph
     */
    cytoNode: function (record, parentId) {
        return {
            group: 'nodes',
            data: this.mapNode_2_nodes(record, parentId)
        };
    },

    /**
     * @param {Ext.data.Model} record the source of info
     * @param {Number} id the parent data in the listNodeDataStructure
     * @returns an addable edge to the cytoscape graph
     */
    cytoEdge: function (record, id) {
        return {
            group: 'edges',
            data: this.mapNode_2_edges(record, id)
        };
    },

    /**
     * this function takes a record and transforms its'data. Used for cytoscape node transformation
     * @param {Object} record
     * @param {String} parentId
     * @returns the .data field of an a cytoscape node 
     */
    mapNode_2_nodes: function (record, parentId) {
        var relationId = record.get('_id');
        var description;

        if (relationId.includes('compound')) {
            description = Ext.String.format('{0} ({1})',
                CMDBuildUI.util.helper.ModelHelper.getObjectDescription(record.get('_destinationType').replace('compound_', '')),
                record.nodes().getRange().length
            );
        } else {
            description = record.get('_destinationDescription');
        }

        return {
            id: record.get('_destinationId'),
            type: record.get('_destinationType'),
            description: description,
            code: record.get('_destinationCode'),
            parent: parentId
        };
    },

    /**
     * this function takes a record and it's parent and transforms its'data. Used for cytoscape edges creation
     * @param {Ext.data.Model} record
     * @param {Number} parent
     * @returns the .data field of a cytoscape edge
     */
    mapNode_2_edges: function (record, id) {
        var cy = this.getViewModel().get('cy'),
            node = cy.nodes('#' + id),
            domainStore = Ext.getStore('domains.Domains'),
            type = record.get('_type'),
            relationId = record.get('_id'),
            relationAttributes = record.get("_relationAttributes"),
            destinationType, destinationDescription;

        if (relationId.includes('compound')) {
            type = record.get('_type').replace('compound_', '');
            destinationType = 'compound node';
            destinationDescription = Ext.String.format('{0} ({1})',
                CMDBuildUI.util.helper.ModelHelper.getObjectDescription(record.get('_destinationType').replace('compound_', '')),
                record.nodes().getRange().length);
        } else {
            destinationType = record.get('_destinationType');
            destinationDescription = record.get('_destinationDescription');
        }

        var domain = domainStore.findRecord('_id', type);

        return {
            id: relationId,
            source: id,
            target: record.get('_destinationId'),
            type: record.get('_type'),
            isDirect: record.get('_is_direct'),
            sourceDescription: node.data('description'),
            sourceType: node.data('type'),
            descriptionDirect: domain.get('_descriptionDirect_translation'),
            descriptionInverse: domain.get('_descriptionInverse_translation'),
            destinationDescription: destinationDescription,
            destinationType: destinationType,
            relationAttributes: !Ext.isEmpty(Ext.Object.getValues(relationAttributes)) ? relationAttributes : null
        };
    },

    /**
     * @param {[Number]} ids an array of card id. The corrisponding cards are selected;
     */
    onSceneSelectedNode: function (ids) {
        this.getViewModel().set('selectedNode', null);

        var store = this.getViewModel().get('relationStore');
        var selectedNodes = [];

        ids.forEach(function (id) {
            var record = store.findRecord('_destinationId', id);
            var targetClass = record.get('_destinationType');
            selectedNodes.push({
                id: id,
                type: targetClass
            });
        }, this);

        /**
         * fill card tab TODO: Use the this.updateSelectionNode method
         */
        this.getViewModel().set('selectedNode', selectedNodes);
    },

    /**
     * Updates the information related to the bind card 
     * @param {[Object]}
     */
    updateSelectionNode: function (nodes) {
        nodes = nodes || this.getViewModel().get('selectedNode');
        this.getViewModel().set('selectedNode', null);
        this.getViewModel().set('selectedNode', nodes);
    },
    /**
     * @param {[Number]} ids contains the ids of the double clicked nodes
     */
    onDoubleClickNode: function (ids) {
        this.tmpSelectedNode = this.getViewModel().get('selectedNode');
        // var newSelected = [];
        var newSceneSelected = [];
        var copy_map = JSON.parse(JSON.stringify(this.getViewModel().get('_map_Node_CompoundName')));

        this.updateSelectionNode([]);
        var compounds = [];
        var nonCompound = [];
        ids.forEach(function (id) {
            if (id.includes('compound')) {
                compounds.push(id);
            } else {
                nonCompound.push(id);
            }
        }, this);

        this.expandCompound(compounds, {
            callback: {
                /**
                 * @param {[Object]} nodes
                 */
                fn: function (relationToAdd) {
                    this.graphRelation('nodesLocal', {
                        nodeRelationsObject: relationToAdd,
                        callback: {
                            /**
                             * @param {[{String} ,[{Ext.data.Model}]]} newRelation a pair rapresentin the id of the compound node and the arry af node added from that compound
                             */
                            fn: function (newRelations) {

                                newRelations.forEach(function (newRelationsCompound) {
                                    var nodeId = newRelationsCompound[0].get('_destinationId');
                                    var wasSelected = false;
                                    var wasInCompound = copy_map[nodeId] || false;

                                    if (wasInCompound) {
                                        for (var i = 0; i < this.tmpSelectedNode.length; i++) {
                                            var tmpId = this.tmpSelectedNode[i].id;
                                            if (tmpId === wasInCompound) {
                                                wasSelected = true;
                                            }
                                        }
                                    }

                                    if (wasSelected) {
                                        newRelationsCompound.forEach(function (newRelationCompound) {
                                            // newSelected.push({
                                            //     id: String(newRelationCompound.get('_destinationId')),
                                            //     type: newRelationCompound.get('_destinationType')
                                            // });
                                            newSceneSelected.push(String(newRelationCompound.get('_destinationId')))
                                        }, this);
                                    }

                                }, this);

                                if (nonCompound.length === 0) {
                                    this.tmpSelectedNode.forEach(function (tmpSelectedNode_) {
                                        if (!tmpSelectedNode_.id.includes('compound')) {
                                            newSceneSelected.push(tmpSelectedNode_.id);
                                        }
                                    }, this);
                                    CMDBuildUI.graph.threejs.SceneUtils.setSelectedNode(newSceneSelected);
                                }
                            },
                            scope: this
                        }
                    });
                },
                scope: this
            }
        });

        this.graphRelation('relationOf', {
            nodes: nonCompound,
            callback: {
                fn: function () {
                    this.tmpSelectedNode.forEach(function (tmpSelectedNode_) {
                        if (!tmpSelectedNode_.id.includes('compound')) {
                            newSceneSelected.push(tmpSelectedNode_.id);
                        }
                    }, this);
                    CMDBuildUI.graph.threejs.SceneUtils.setSelectedNode(newSceneSelected);
                },
                scope: this
            }
        });

    },

    /**
     * this function expands the compound nodes rapresented by the id
     * @param {String[]} ids
     * @param {Object} config
     * @type {Object} config.callback
     * @type {Function} config.callback.fn
     * @type {Object} config.callback.scope
     * @returns {Object} containing the parent id and the array of childs. 
     */
    expandCompound: function (ids, config) {
        config = config || {};
        Ext.applyIf(config, {
            callback: null
        });
        //Remove from scene
        //remove from cytoscape
        //remove from relationStore
        //remove from this._map_Node_CompoundName

        var relationStore = this.getViewModel().get('relationStore');
        var cy = this.getViewModel().get('cy');
        var relationToAdd = {};
        var _map_Node_CompoundName = this.getViewModel().get('_map_Node_CompoundName');
        ids.forEach(function (id) {
            var cyNode = cy.$('#' + id);
            var edges = cyNode.connectedEdges().toArray();
            var relationToRemove = [];
            var edgeToRemove = [];

            edges.forEach(function (edge) {
                var idEdge = edge.id();
                var relation = relationStore.findRecord('_id', idEdge);
                var compoundRelations = relation.nodes().getRange();

                compoundRelations.forEach(function (compoundRelation) {
                    var sourceId = compoundRelation.get('_sourceId');
                    //remove from this._map_Node_CompoundRelation
                    delete _map_Node_CompoundName[compoundRelation.get('_destinationId')];
                    if (!relationToAdd[sourceId]) {
                        relationToAdd[sourceId] = [compoundRelation];
                    } else {
                        relationToAdd[sourceId].push(compoundRelation);
                    }

                }, this);

                relationToRemove.push(relation);
                edgeToRemove.push(idEdge);
            }, this);
            //remove from relationStore;
            relationStore.remove(relationToRemove);
            //rmove from cytoscape
            cy.remove(cyNode);
            //remove from Scene
            CMDBuildUI.graph.threejs.SceneUtils.removeObjects([id], edgeToRemove);
        }, this);

        this.getViewModel().set('_map_Node_CompoundName', {});
        this.getViewModel().set('_map_Node_CompoundName', _map_Node_CompoundName);

        config.callback.fn.call(config.callback.scope || this, relationToAdd);
    },

    //<-- Remove Node Part START-->
    /**
     *@param {Object} newNode the new Starting node after reset
     */
    resetEnvironment: function (newNode) {
        if (newNode) {
            this.tmpSelectedNode = newNode;
        } else {
            var selNode = this.getViewModel().get('selectedNode');
            if (selNode.length != 0) { //FIXME: vedere come gestire gli stati del selNode
                this.tmpSelectedNode = selNode[0];
            }
        }

        var vm = this.getViewModel();
        var relationStore = vm.get('relationStore');
        vm.set('_map_Node_CompoundName', {});
        relationStore.removeAll();


    },
    //<-- Remove Node Part END-->

    /**
     * @param {Ext.data.Store} relationStore
     * @param {Object} eOpts 
     */
    onRelationStoreClear: function (relationStore, eOpts) {
        //createsw the new momoryNodeDataStructure
        this.eraseNodeDataStructure();
        this.eraseCytoscape();
        this.eraseThreeJsScene();
        //empty NodeDataStructure


        this.getViewModel().set('node', {
            _type: this.tmpSelectedNode.type,
            _id: this.tmpSelectedNode.id,
            _code: this.tmpSelectedNode.code,
            _description: this.tmpSelectedNode.description
        });
    },

    /**
     * 
     */
    eraseNodeDataStructure: function () {
        //deletes the old memoryNodeDataStructure
        this.getViewModel().set('memoryNodeDataStructure', null);
    },

    /**
     * 
     */
    eraseCytoscape: function () {
        this.getViewModel().set('cy', null);
    },

    /**
     * 
     */
    eraseThreeJsScene: function () {
        CMDBuildUI.graph.threejs.SceneUtils.destroyScene();
        var canvasView = this.getView().down('graph-canvas-canvaspanel');

        canvasView.setHtml('<div id="cy"></div>');
        var div = document.getElementById('cy');
        div.style.height = 'inherit';


    },

    //---------------------------------------------------------------------//
    //  graph-canvas-bottommenu-canvasmenu 

    /**
     * @param {Ext.Component} slider
     * @param {Object} eOpts
     */
    onSliderBeforeRender: function (slider, eOtps) {
        var value = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.relgraph.baselevel);
        // var maxValue = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.relgraph.clusteringThreshold);
        //set slider values
        slider.setValue(value);
        // slider.setMaxValue(maxValue);
    },

    /**
     * @param {Ext.slider.Multi} slider 
     * @param {Number} newValue
     * @param {Ext.slider.Thumb} thumb
     * @param {object} eOpts
     */
    onSliderChange: function (slider, newValue, thumb, eOpts) {
        var tbtext = this.getComponent('#sliderValue');
        tbtext.setHtml(newValue);
    },

    /**
     * @param {Ext.slider.Multi} slider 
     * @param {Number} newValue
     * @param {Ext.slider.Thumb} thumb
     * @param {object} eOpts
     */
    onSliderChangeComplete: function (slider, newValue, thumb, eOpts) {

        var selNode = this.getViewModel().get('selectedNode')[0];
        var dataNode = this.getViewModel().get('cy').nodes('#' + selNode.id).data();
        var tmpSelectedNode = {
            type: dataNode.type,
            id: dataNode.id,
            code: dataNode.code,
            description: dataNode.description
        };

        if (this.waitingCallbackResponse === true) {
            this.ignoreCallbackResponse = true;
        }

        this.resetEnvironment(tmpSelectedNode);
    },

    /**
     * @param {Ext.Component} tbtext
     * @param {Object} eOpts
     */
    onSliderValueBeforeRender: function (tbtext, eOpts) {
        var value = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.relgraph.baselevel);
        tbtext.setHtml(value);
    },

    /**
     * This function loads all the information for each navigationTree
     */
    loadAllDomainTrees: function () {
        var vm = this.getViewModel();
        var navStore = Ext.getStore('navigationtrees.NavigationTrees');
        var domainTrees = navStore.getRange();
        var l = domainTrees.length;

        function _auxFunction(length) {
            --length;
            if (length === 0) {
                vm.set('navTreesLoaded', true);
            }
            return length;
        }

        domainTrees.forEach(function (domainTree) {
            var id = domainTree.getId();
            var childs = domainTree.nodes();

            if (!childs.isLoaded()) {
                CMDBuildUI.model.navigationTrees.DomainTree.load(id, {
                    callback: function (record, operation, success) {
                        if (success) {
                            navStore.add([record]);
                            l = _auxFunction(l)
                        }
                    }
                });
            } else {
                l = _auxFunction(l);
            }
        }, this)
    },

    /**
     * @param {[Ext.data.Model]} records all the relation of A specific card
     * @param {String} parentTreeId The type of the parent of all those records
     * @param {String} parentTreeClass
     * @returns {[Ext.data.Model]} An array of records eliminating the ones who doesn't respect the navigation tree dependencies
     */
    applyNavigationTree: function (records, parentTreeId, parentTreeClass) {
        var view = this.getView();
        var navTreeName = view.down('graph-topmenu-topmenu').getViewModel().get('lastCheckedValue');

        switch (navTreeName) {
            case 'init': {
                //No filter on domains
                return records;
            }
            case null: {
                //No filter on domain
                return records;
            }
            default: {
                var domainTree = Ext.getStore('navigationtrees.NavigationTrees').getById(navTreeName);
                var newRecords = [];

                if (!parentTreeId) {
                    parentTreeId = this.traversalVisit(domainTree, parentTreeClass);
                }
                var domains = this.getValidDomains(domainTree, parentTreeId);

                records.forEach(function (record) {
                    var ix = this.inArray(domains, record.get('_type'));
                    if (ix !== -1) {
                        record.set('treeId', domains[ix].treeId);
                        newRecords.push(record);
                    }
                }, this);
                return newRecords;
            }
        }
    },

    /**
     * @param {Ext.data.Store} domainTree
     * @param {String} treeId
     * @returns {String[]} an array containing all the domain valid for that class    
     */
    getValidDomains: function (domainTree, treeId) {
        var currentRecord = domainTree.getNode(treeId);
        var childs = domainTree.getChild(treeId);
        var domains = [];

        /**
         * Case in wich the currentClass is the root
         */
        if (currentRecord.get('domain') != '') {
            domains.push({
                domainName: currentRecord.get('domain'),
                treeId: currentRecord.get('parent')
            });
        }

        /**
         * Adds child domains 
         */
        childs.forEach(function (child) {
            domains.push({
                domainName: child.get('domain'),
                treeId: child.get('_id')
            });
        }, this);

        return domains;

    },

    inArray: function (array, value) {
        for (var i = 0; i < array.length; i++) {
            if (array[i].domainName === value) {
                return i;
            }
        }

        return -1;
    },

    /**
     * @param {Ext.data.Store} domainTree model: CMDBuildUI.model.navigationTrees.TreeNode
     * @param {String} targetClass
     * @returns {Number} Id of the class in the inOrder visit of the tree
     */
    traversalVisit: function (domainTree, targetClass) {
        var queue = [domainTree.getRoot()];
        var tmpNode;

        while (queue.length != 0) {
            tmpNode = queue.shift();

            var tmpClass = tmpNode.get('targetClass');
            var tmpTargetClass = targetClass;

            while (tmpTargetClass != '') {
                if (tmpClass == tmpTargetClass) {
                    return tmpNode.get('_id');
                }
                tmpTargetClass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(tmpTargetClass).get('parent');
            }

            domainTree.getChild(tmpNode.getId()).forEach(function (child) {
                queue.push(child);
            }, this);
        }

        console.error('should Never happend');
    },


    /**
     * @param {Ext.data.Store} domainTree
     * @returns {Ext.data.Model} the root of the domainTree
     */
    getRoot: function (domainTree) {
        return domainTree.getAt(0);
    },

    /**
     * @param {Ext.data.Store} domainTree
     * @param {Ext.data.Model} parent
     * @return {[Ext.data.Model]} an array of it's childs
     */
    getChilds: function (domainTree, parent) {
        var childs = [];

        domainTree.getRange().forEach(function (record) {
            if (record.get('parent') == parent.get('_id')) {
                childs.push(record);
            }
        }, this);

        return childs;
    },


    /**
     * 
     * NodeDataStructure type
     * @param {String} id 
     * @param {String} type
     * @param {String}
     * @param {Object} _config
     */
    NodeDataStructure: function (id, type, description, code, treeId, _config) {
        this.id = id;
        this.type = type;
        this.treeId = treeId;
        this.description = description;
        this.code = code;
        this.children = [];
        /**
         * This field tells if this node has passed through the operation of child (relation) acquisition
         */
        this.childLoaded = false;
        /**
         * This field indicates at which depth this element is found
         */
        this.depth = null;

        this.init = function (conf) {
            if (typeof (conf.depth) === 'number') {
                this.depth = conf.depth;
            }

            if (conf.childLoaded === true) {
                this.childLoaded = true;
            }
        };

        this.getId = function () {
            return this.id;
        };

        this.getType = function () {
            return this.type;
        };

        this.getDescription = function () {
            return this.description;
        };

        this.setDescription = function (description) {
            this.description = description;
        };

        this.setCode = function (code) {
            this.code = code;
        };
        this.getCode = function () {
            return this.code;
        };
        this.setTreeId = function (treeId) {
            this.treeId = treeId;
        };

        this.getTreeId = function () {
            return this.treeId;
        };
        this.setCompoundFather = function (compoundFather) {
            this.compoundFather = compoundFather;
        };
        this.getCompoundFather = function () {
            return this.compoundFather;
        };
        this.getDepth = function () {
            return this.depth;
        };

        /**
         * Sets the node depth. If yet inizializied doesn't change the value
         * @param {Number} depth 
         */
        this.setDepth = function (depth) {
            if (typeof (this.depth) != 'number') {
                this.depth = depth;
            }
        };

        /**
         * This function changes the internal state of the value childLoaded 
         * settting that to true
         */
        this._setChildLoaded = function () {
            this.childLoaded = true;
        };

        /**
         * @returns {Boolean} the value of this.childLoaded
         */
        this._getChildLoaded = function () {
            return this.childLoaded;
        };
        /**
         * Adds children to the curren NodeDataStructure
         * Sets depth and 
         * @param {[NodeDataStructure]} nodes 
         * @param {Object} config Configuration object
         */
        this.addChildren = function (nodes, config) {

            nodes.forEach(function (node) {

                var yetIn = false;
                for (var i = 0; i < this.children.length && !yetIn; i++) {
                    if (this.children[i].getId() == node.getId()) {
                        yetIn = true;
                    }
                }

                if (!yetIn) {
                    this.children.push(node); //If the node is not saved
                    node.setDepth(this.depth + 1);
                    /**
                     * Fires the related afterNodeAdd function
                     * NOTE: This event could be fire only once saving the nodes in an array: tmpNodes.push(node) and handle an array of objects instead of the single node
                     */
                    if (config.afterNodeAdd) {
                        config.afterNodeAdd.fn.call(config.afterNodeAdd.scope || this, node);
                    }
                }
            }, this);

            this._setChildLoaded();
        };

        /** */
        if (_config) {
            this.init(_config);
        }
    },

    /**
     * ListNodeDataStructure
     * @param {Object} _config the global scope 
     */

    ListNodeDataStructure: function (_config) {
        this.nodes = {};

        /**
         * This field have this style:
         * {
         *  0: {
         *      _meta: {
         *          complete: true    
         *      },
         *      nodes: {
         *          id1: NodeDataStructure,
         *          ...,
         *          idn: NodeDataStructure
         *      }
         *  },
         *  1: {...},
         *  ...,
         *  n: {...}
         * } 
         * 
         * _meta: Contains information about the set of nodes
         *  _meta.complete: Indicates if all the possible element that can be found at level n are in the set
         *  ES: 
         *      at level 0 all the elements that can be found in that level are present by default so depth[0]._meta.complete = true
         *      at level 1 can be found all the childs (suppose to be 2 elements) of the root so depth[1]._meta.complete = true
         *      at level 2 if i load only one element of the level 1 the depth[2]._meta.complete = false. After loading the other element yet not loaded and adding his child i can set depth[2]._meta.complete = true
         * 
         */
        this.depth = {};
        this.me = null;

        this._init = function (conf) {
            if (!conf.scope) {
                console.error('Define scope');
            } else {
                this.me = conf.scope;
            }

            if (!conf.root) {
                console.error('Define Root');
            } else {
                var tmpNodeDataStructure = new this.me.NodeDataStructure(conf.root._id, conf.root._type, conf.root._description, conf.root._code, conf._tree_id, { depth: 0 });
                //add the node in this.nodes
                this._saveNode(tmpNodeDataStructure);
                //sync the this.depth
                this.depth[0] = {
                    _meta: {
                        complete: true
                    },
                    nodes: {}
                };
                this.depth[0].nodes[conf.root._id] = tmpNodeDataStructure;
            }
        };

        /**
         * 
         * @param {String[]} ids array of ids
         * @returns {[NodeDataStructure]}
         */
        this.getNodes = function (ids) {
            var tmpNodes = [];
            var tmpNode;

            ids.forEach(function (id) {
                tmpNode = this.nodes[id];
                if (tmpNode) {
                    tmpNodes.push(tmpNode);
                } else {
                    console.error('cant find that node');
                }
            }, this);

            return tmpNodes;
        };

        /**
         * 
         * @param {Number} id 
         * @returns {NodeDataStructure} the node with that id
         */
        this.getNode = function (id) {
            var node = this.nodes[id];

            if (node) {
                return node;
            } else {
                return null;
            }
        };

        /**
         * Adds nodes in the this.nodes object
         * @param {NodeDataStructure} node 
         */
        this._saveNode = function (node) {
            var id = node.getId();
            if (!this.nodes[id]) {
                this.nodes[id] = node;
            } else {
                console.error('Duplicated Id');
            }
        };

        /**
         * 
         * @param {NodeDataStructure} parentNode 
         * @param {Object} childNodes 
         * childNodes.id
         * childNodes.type
         */
        this.addChildren = function (parentNode, childNodes) {
            var tmpAdds = [];
            var tmpNode;

            childNodes.forEach(function (childNode) {
                tmpNode = this.getNode(childNode.id);
                if (!tmpNode) {
                    tmpNode = new this.me.NodeDataStructure(childNode.id, childNode.type, childNode.description, childNode.code, childNode.treeId, childNode.config);
                    this._saveNode(tmpNode);
                }
                tmpAdds.push(tmpNode);
            }, this);


            parentNode.addChildren(tmpAdds, {
                /**
                 * This function modifies the this.depth list
                 * @param {DataNodeStructure} node
                 */
                afterNodeAdd: {
                    fn: function (node) {
                        var depth = node.getDepth();

                        if (!this.depth[depth]) {
                            this.depth[depth] = {
                                _meta: {
                                    complete: false
                                },
                                nodes: {}
                            };
                        }

                        this.depth[depth].nodes[node.getId()] = node;
                    },
                    scope: this
                }
            });
        };

        /**
         * @param {Number} depth
         * @param {Object} config
         * config.all if true load all the childs else only the ones with not loaded. Default is true
         * @returns {[NodeDataStructure]}
         */
        this.getNodesAtdepth = function (depth, config) {
            config = config || {};
            Ext.applyIf(config, {
                all: true
            });
            if (!this.depth[depth]) {
                console.error('No elements at this depth');
            }

            var tmpIds = [];
            for (var id in this.depth[depth].nodes) {
                if (config.all === true) {
                    tmpIds.push(id);
                } else if (config.all === false) {
                    if (this.depth[depth].nodes[id].childLoaded == false) {
                        tmpIds.push(id)
                    }
                }

            }
            return this.getNodes(tmpIds);
        };


        /**
         * This function returns the last depth at wich the _meta.complete = true
         * Each time this function is called the values are recalculated in a efficient way
         * @returns {Number} the last complete depth load
         */
        this.getLastDepth = function () {
            var maxDepth = this._getMaxDepth();
            var lastComplete = maxDepth;

            while (this.depth[lastComplete]._meta.complete !== true) {
                lastComplete--;
            }

            /**
             * lastComplete have always the .complete field = true
             */
            while (lastComplete != maxDepth) {
                if (this._areAllNodesLoaded(lastComplete)) {
                    lastComplete++;
                    this.depth[lastComplete]._meta.complete = true;
                } else {
                    return lastComplete;
                }
            }

            return lastComplete;
        };

        /**
         * @param {Number} depth
         * @returns {Boolean} true if all the nodes in that depth have NodeDateStructure.childLoaded = true; False otherwise
         */
        this._areAllNodesLoaded = function (depth) {
            var tmpNode;

            for (var id in this.depth[depth].nodes) {
                tmpNode = this.depth[depth].nodes[id];
                if (tmpNode._getChildLoaded() === false) {
                    return false;
                }
            }

            return true;
        };

        /**
         * @returns {Number} the maximum depth reached
         */
        this._getMaxDepth = function () {
            var maxDepth = 0;

            for (var dh in this.depth) {
                if (parseInt(dh) > parseInt(maxDepth)) {
                    maxDepth = dh;
                }
            }

            return maxDepth;
        };

        this.isComplete = function () {
            var maxDepth = this._getMaxDepth();
            if (this._areAllNodesLoaded(maxDepth)) {
                return true;
            }

            return false;
        };

        /**
         * Start configuration
         */
        this._init(_config);
    },
    privates: {

        /**
         * @type {Boolean}
         * This field invalidates the incoming callback response
         */
        ignoreCallbackResponse: false,

        /**
         * @type {Boolean}
         * This field indicates if we are waiting for a callback response
         */
        waitingCallbackResponse: false,

        /**
         * @param {String} name
         */
        getComponent: function (name) {
            var view = this.getView();
            var elem = view.down(name);
            return elem;
        }
    }
});