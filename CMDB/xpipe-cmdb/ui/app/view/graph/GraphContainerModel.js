Ext.define('CMDBuildUI.view.graph.GraphContainerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.graph-graphcontainer',
    data: {
        /*
        * this is the starting node 
        */
        node: {
            _type: null,
            _id: null,
            _code: null,
            _description: null
        },
        /**
         * @type {ListNodeDataStructure}
         */
        memoryNodeDataStructure: null,
        cy: null,
        /**
         * this is the selected node
         * Contains an arry of objects
         * {[Object]}
         *  {
         *   id: null,
         *   type: null
         *  }
         */
        selectedNode: null,
        navTreesLoaded: false,

        /**
         * This object maps the id of nodes in the ids of his the compound nodes
         * {
         *  nodeId: 'Compound_node_...',
         *  nodeId2: 'Compound_node_...'
         * }
         */
        _map_Node_CompoundName: {},
        pointerExternalCanvas: false
    },
    formulas: {
        startFirstNode: {
            bind: {
                startingNode: '{node}'
            },
            get: function (data) {
                var node = data.startingNode;
                if (node._id != null && node._type != null) {

                    /**
                     * get the controller of the view;
                     */
                    var ctl = this.getView().getController();

                    /**
                     * Creates the first element record
                     */
                    var record = Ext.create('CMDBuildUI.model.domains.Relation', {
                        _destinationType: node._type, //class
                        _destinationId: node._id,
                        _destinationCode: node._code,
                        _destinationDescription: node._description
                        //_type: node._type_name//domain
                    });

                    /**
                     * fills the firs element in relation store
                     */
                    this.get('relationStore').insert(0, record);

                    /**
                     * Creates the cytoscape
                     */
                    var cy = cytoscape({});
                    this.set('cy', cy);

                    /**
                     * fills the first element in cytoscape
                     * Sets his parameter as root
                     */
                    var cyNode = ctl.cytoNode(record);
                    cyNode.data.isRoot = true;
                    cy.add(cyNode);

                    /**
                     * Create the ListNodeDataStructure 
                     * &&
                     * Fills the first element in ListNodeDataStructure
                     */
                    var ListNodeDataStructure = new ctl.ListNodeDataStructure({
                        scope: ctl,
                        root: {
                            _id: node._id,
                            _type: node._type,
                            _description: node._description,
                            _three_id: null,
                            _code: node._code
                        }
                    });

                    /**
                     * Save the ListNodeDataStructure in the view model
                     */
                    this.set('memoryNodeDataStructure', ListNodeDataStructure);

                    CMDBuildUI.graph.threejs.SceneUtils.init({
                        cy: this.get('cy'),
                        containterView: this.getView()
                    });

                    /**
                     * firstCall
                     */
                    ctl.graphRelation('childOfMultiDepth', {
                        // depth: CMDBuildUI.util.helper.Configurations.get('cm_system_rel_baseLevel'),
                        depth: this.getView().down('slider').getValue(),
                        callback: {
                            fn: CMDBuildUI.graph.threejs.SceneUtils.setSelectedNode,
                            scope: CMDBuildUI.graph.threejs.SceneUtils,
                            arguments: {
                                ids: [node._id]
                            }
                        }
                    });

                } else {
                    CMDBuildUI.util.Logger.log(
                        'Something gone wrong',
                        CMDBuildUI.util.Logger.levels.warn
                    );
                }
            }
        }
    },
    stores: {
        relationStore: {
            model: "CMDBuildUI.model.domains.Relation",
            id: 'relationStore',
            autoDestroy: true,
            proxy: {
                type: 'memory'
            }
        }
    }

});
