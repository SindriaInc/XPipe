
Ext.define('CMDBuildUI.view.map.tab.cards.NavigationTree', {
    extend: 'Ext.tree.Panel',

    requires: [
        'CMDBuildUI.view.map.tab.cards.NavigationTreeController',
        'CMDBuildUI.view.map.tab.cards.NavigationTreeModel',

        'CMDBuildUI.store.map.Tree'
    ],
    mixins: [
        'CMDBuildUI.view.map.Mixin'
    ],
    alias: 'widget.map-tab-cards-navigationtree',
    controller: 'map-tab-cards-navigationtree',
    viewModel: {
        type: 'map-tab-cards-navigationtree'
    },
    reference: 'map-tab-cards-navigationtree',
    statics: {
        root_id_composed: 'root_id_composed',
        root_id: 'root_id'
    },
    config: {
        objectId: {
            $value: undefined,
            evented: false
        },
        objectType: {
            $value: undefined,
            evented: false
        },
        objectTypeName: {
            $value: undefined,
            evented: false
        },

        forceFirstCheck: {
            $value: true,
            evented: false
        },
        /**
         * hash map used only for saving tree nodes and them by id quickly. 
         * Is populated in view.loadChildren() function
         */
        nodeHashMap: undefined,
        initialized: {
            $value: false
        },

        /**
         * If true, will be made extra checks before loading parents
         */
        extraCheckOnType: {
            $value: false
        },

        attach_nav_tree_collection: {
            value: undefined,
            evented: true
        }
    },

    store: undefined,
    publishes: [
        'store',
        'objectId',
        'objectTypeName',
        'objectType',
        'initialized',
        'attach_nav_tree_collection'
    ],

    twoWayBindable: [
        "objectId"
    ],

    cls: 'noicontree',
    selModel: {
        type: 'treemodel',
        mode: 'MULTI'
    },

    layout: 'fit',
    hideHeaders: true,
    columns: [{
        xtype: 'treecolumn',
        dataIndex: 'text',
        flex: 20
    }, {
        xtype: 'actioncolumn',
        width: '100',
        handler: 'actionColumn',
        iconCls: 'x-fa fa-arrow-circle-right NavigationTree',
        flex: 1
    }],

    initComponent: function () {
        // this.setNodeHashMap(Ext.util.HashMap.create());
        this.setNodeHashMap(new Ext.util.Collection({
            keyFn: function (item) {
                return item.get('_id_composed');
            },
            grouper: {
                groupFn: function (item) {
                    return item.get('_id');
                }
            }
        }));

        this.getViewModel().bind({
            store: '{map-tab-cards-navigationtree.store}'
        }, function (data) {
            if (data.store) {
                var root = data.store.getRoot();
                this.getNodeHashMap().add(root);
            }
        }, this);

        // this piece of code is responsable for listening new nodes appended that might be marked as selected in the tree
        this.getViewModel().bind({
            objectTypeName: '{map-tab-cards-navigationtree.objectTypeName}',
            tree: '{map-tab-tabpanel.navigationTree}'
        }, function (data) {
            if (data.objectTypeName && data.tree) {
                var domainStore = Ext.getStore('domains.Domains');

                var navTreeNodes = data.tree.findAllBy(function (item) {
                    var targetClassName = item.get('targetClass');
                    var targetKlass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(targetClassName);
                    var domain = domainStore.findRecord('_id', item.get('domain'));

                    if (targetKlass) {
                        //removes the invalid subTypes
                        var targetKlassSubTypes = Ext.Array.map(targetKlass.getChildren(), function (item) {
                            return item.getId();
                        });
                        if (domain) {
                            targetKlassSubTypes = Ext.Array.difference(
                                targetKlassSubTypes, item.get('direction') == '_1' ?
                                domain.get('disabledSourceDescendants') :
                                domain.get('disabledDestinationDescendants')
                            );
                        }

                        //removes the invalid superTypes
                        var targetKlassHierarchy = targetKlass.getHierarchy();
                        if (domain) {
                            targetKlassHierarchy = Ext.Array.difference(
                                targetKlassHierarchy, item.get('direction') == '_1' ?
                                domain.get('disabledDestinationDescendants') :
                                domain.get('disabledSourceDescendants')
                            );
                        }

                        if (data.objectTypeName == targetClassName) {
                            return item;
                        }

                        else if (Ext.Array.contains(targetKlassSubTypes, data.objectTypeName)) {
                            this.setExtraCheckOnType(true);
                            //if the object is subtype of the target
                            return item;
                        }
                        else if (Ext.Array.contains(targetKlassHierarchy, data.objectTypeName)) {
                            //if the object is a supertype of the target
                            this.setExtraCheckOnType(true);
                            return item;
                        }
                    } else {
                        return false;
                    }
                }, this);

                function handleAdd(objectId, store, navTreeNodes) {
                    if (store) {
                        var selected = [],
                            not_found_ids = [],
                            selectionModel = this.getSelectionModel();

                        Ext.Array.forEach(navTreeNodes, function (item, index, array) {
                            var _id_composed = Ext.String.format('{0}-{1}', objectId, item.getId());
                            var found = store.getNodeById(_id_composed);
                            if (found) {
                                selected.push(found);
                            } else {
                                not_found_ids.push(_id_composed);
                            }
                        }, this);

                        if (not_found_ids.length) {
                            // this.getSelectionModel().select([], false, true);
                            if (selectionModel.selectionMode !== "SIMPLE") {
                                selectionModel.deselectAll(true);
                            }
                            addListener.call(this, objectId, store, navTreeNodes, not_found_ids);
                        }

                        if (selected.length) {
                            Ext.Array.forEach(selected, function (item, index, array) {
                                Ext.asap(function (record) {
                                    this.expandConsecutively(record).then(function (record) {

                                        if (this.getForceFirstCheck() == true) {
                                            this.getController().customCheck(record);
                                            this.setForceFirstCheck(false);
                                        }

                                    }, Ext.emptyFn, Ext.emptyFn, this);
                                }, this, [item]);
                            }, this);
                            var keepSelect = selectionModel.selectionMode === "SIMPLE" ? true : false;
                            selectionModel.select(selected, keepSelect, true);
                        }
                    }
                }

                function _handleAdd(objectId, store, navTreeNodes, not_found_ids, st, node, index, eOpts) {
                    var found = Ext.Array.contains(not_found_ids, node.getId()) ? true : false;
                    if (found) {
                        handleAdd.call(this, objectId, store, navTreeNodes);
                    } else {
                        addListener.call(this, objectId, store, navTreeNodes, not_found_ids);
                    }
                }

                function addListener(objectId, store, navTreeNodes, not_found_ids) {
                    store.addListener('nodeappend', _handleAdd, this, {
                        single: this,
                        args: [objectId, store, navTreeNodes, not_found_ids]
                    });
                }

                ////
                function handleInsertion(objectType, objectTypeName, objectId, description, store, navTreeNodes) {
                    if (store) {
                        var not_found_node = [];

                        Ext.Array.forEach(navTreeNodes, function (navTreeNode, index, array) {
                            // if (!navTreeNode.isNavRoot()) {

                            var _id_composed = Ext.String.format('{0}-{1}', objectId, navTreeNode.getId());
                            var found = store.getNodeById(_id_composed);
                            if (!found) {
                                var newnode = Ext.create('CMDBuildUI.model.gis.GeoValueTree', {
                                    _id_composed: _id_composed,
                                    _id: objectId,
                                    description: description,
                                    type: objectTypeName,
                                    navTreeNodeId: navTreeNode.getId(),
                                    leaf: navTreeNode.isNavLeaf(),
                                    checked: true
                                });
                                newnode.setNavTreeNode(navTreeNode);
                                not_found_node.push(newnode);
                            }
                            // }
                        }, this);

                        /**
                         * 
                         */
                        function makeExtraCheckOnType(type, navtreedef) {
                            var targetKlassName = navtreedef.get('targetClass');
                            var targetKlass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(targetKlassName);
                            var typeKlass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(type);

                            if (targetKlassName == type) {
                                return true;
                            }

                            var targetKlassSubTypes = targetKlass.getChildren();

                            //filters the unwanted classes
                            if (navtreedef.get("subclassFilter")) {
                                var subclassFilter = navtreedef.get("subclassFilter").split(',');
                                var tmpTargetKlassSubTypes = [];
                                Ext.Array.forEach(targetKlassSubTypes, function (item, index, array) {
                                    if (Ext.Array.contains(subclassFilter, item.getId())) {
                                        tmpTargetKlassSubTypes = Ext.Array.merge(tmpTargetKlassSubTypes, item);
                                        tmpTargetKlassSubTypes = Ext.Array.merge(tmpTargetKlassSubTypes, item.getChildren());
                                    }
                                }, this);

                                targetKlassSubTypes = tmpTargetKlassSubTypes;
                            }

                            if (Ext.Array.contains(targetKlassSubTypes, typeKlass)) {
                                return true;
                            }

                            if (Ext.Array.contains(targetKlass.getHierarchy(), type)) {
                                //should never enter here because the "type" is not a superclass but always the final type
                                return true;
                            }
                            return false;
                        }

                        if (not_found_node.length) {
                            Ext.Array.forEach(not_found_node, function (item, index, array) {

                                if (this.getExtraCheckOnType() == true) {
                                    var navtreedef = item.getNavTreeNode();

                                    if (makeExtraCheckOnType.call(this, item.get('type'), navtreedef)) {
                                        this.recursiveLoadParent.call(this, item);
                                    }
                                } else {
                                    this.recursiveLoadParent(item);
                                }
                            }, this);
                        }
                    }
                }

                if (navTreeNodes) {

                    //this bind is responsable for setting as selected the records in the navigation tree
                    this.getViewModel().bind({
                        objectId: '{map-tab-cards-navigationtree.objectId}',
                        store: '{map-tab-cards-navigationtree.store}',
                        initialized: '{map-tab-cards-navigationtree.initialized}'
                    }, function (data) {
                        if (data.store) {
                            data.store.removeListener('nodeappend', _handleAdd, this);

                            if (data.objectId && data.initialized) {
                                handleAdd.call(this, data.objectId, data.store, navTreeNodes);
                            } else {
                                var selectionModel = this.getSelectionModel();
                                if (selectionModel.selectionMode !== "SIMPLE") {
                                    selectionModel.deselectAll();
                                }
                            }
                        }
                    }, this);

                    //this bind is responsable for pupulating the tree when selecting a card, loading the card based on the gisnavigationtree
                    this.getViewModel().bind({
                        objectType: '{map-tab-cards-navigationtree.objectType}',
                        store: '{map-tab-cards-navigationtree.store}',
                        initialized: '{map-tab-cards-navigationtree.initialized}',
                        theObject: '{map-tab-tabpanel.theObject}'
                    }, function (data) {
                        if (data.store && data.initialized && data.theObject) {
                            handleInsertion.call(this, data.objectType, data.theObject.get('_type'), data.theObject.get('_id'), Ext.String.htmlEncode(data.theObject.get('Description')), data.store, navTreeNodes);
                        }
                    }, this);
                }
            }
        }, this);

        this.callParent(arguments);

        this.setStore({
            type: 'tree',
            id: 'navigationtreestore',
            model: 'CMDBuildUI.model.gis.GeoValueTree',
            root: {
                checked: true,
                expanded: true,
                description: CMDBuildUI.locales.Locales.gis.root,
                _id_composed: CMDBuildUI.view.map.tab.cards.NavigationTree.root_id_composed,
                _id: CMDBuildUI.view.map.tab.cards.NavigationTree.root_id
            },
            sorters: [{
                property: 'text'
            }]
        });
    },

    loadChildren: function (node, navtreedef, callback, scope) {
        var deferred = new Ext.Deferred();
        node._childrenloaded = deferred;

        var destinationType,
            destinationTypeName,
            sourceTypeName,
            sourceId,

            path,
            advancedFilter,

            selectAll = this.getViewModel().get("settingsMap.selectAll"),
            objectTypeName = this.getObjectTypeName(),
            selectionModel = this.getSelectionModel();

        if (navtreedef.isNavRoot()) {
            destinationTypeName = navtreedef.get('targetClass');
        } else {
            // get config
            destinationTypeName = node.get('isIntermediate') ? node.get('type') : navtreedef.get('targetClass');
            sourceTypeName = node.get('isIntermediate') ? navtreedef.getParent().get('targetClass') : node.get('type');
            sourceId = node.get('_id');

            if (this.checkDisabledDomainClasses(navtreedef, sourceTypeName, destinationTypeName) == false) {
                node._childrenloaded = true;
                deferred.resolve();
                return;
            }
        }


        destinationType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(destinationTypeName);
        advancedFilter = navtreedef.getDownFilter(sourceTypeName, sourceId, destinationTypeName);
        path = CMDBuildUI.util.helper.ModelHelper.getListBaseUrl(destinationType, destinationTypeName);

        var childrenstore = this.getTemporaryStore(path, advancedFilter);

        // load store
        childrenstore.load({
            callback: function (records, operation, success) {
                //FIXME: move the logics after the load in another component
                //Could add elements to attach_nav_tree_collection and make handle all by it's handler "onCollectionAddHandler" which calculates automatically the correct check value to insert. withouc calculating it here
                if (success) {

                    //stores the nodes
                    var nodeHashMap = this.getNodeHashMap();

                    //stores nodes checked value
                    var hashMap = this.getMapContainerView().getHashMap();
                    hashMap.beginUpdate();

                    var showOnlyOne = navtreedef.get('showOnlyOne');
                    var firstShowOnlyOne = {};

                    if (showOnlyOne) {
                        for (var i = 0; i < node.childNodes.length; i++) {
                            var item = node.childNodes[i];
                            if (item.getNavTreeNode().getId() == navtreedef.getId()) {
                                if (hashMap.get(item.get('_id')) == true) {
                                    firstShowOnlyOne[navtreedef.getId()] = true;
                                    i = node.childNodes.length;
                                }
                            }
                        }
                    }

                    // append items to the node
                    records.forEach(function (r) {
                        var _id_composed = Ext.String.format('{0}-{1}', r.get('_id'), navtreedef.getId());

                        if (nodeHashMap.getByKey(_id_composed)) {
                            return;
                        }

                        var fireevent;
                        var hashKey = r.get("_id");
                        var hashValue = nodeHashMap.getGroups().getByKey(hashKey);
                        var checked;

                        if (showOnlyOne) {
                            if (!Ext.isEmpty(hashValue)) {
                                checked = hashValue.getAt(0).get('checked');

                                if (checked == true) {
                                    if (!firstShowOnlyOne[navtreedef.getId()]) {
                                        checked = true;
                                        firstShowOnlyOne[navtreedef.getId()] = true;
                                    } else {
                                        checked = false;
                                        fireevent = true;
                                    }
                                } else {
                                    checked = false;
                                }
                            } else {
                                if (!firstShowOnlyOne[navtreedef.getId()]) {
                                    if (node.get('checked') == true) {
                                        checked = true;
                                        firstShowOnlyOne[navtreedef.getId()] = true;
                                    } else if (node.get('checked') == false) {
                                        checked = false;
                                    }
                                } else {
                                    checked = false;
                                }
                            }
                        } else {
                            if (!Ext.isEmpty(hashValue)) {
                                checked = hashValue.getAt(0).get('checked');

                                if (checked != node.get('checked')) {
                                    fireevent = true;
                                }
                            } else {
                                checked = node.get('checked');
                            }
                        }

                        var description = Ext.String.htmlEncode(r.get("Description")),
                            c = node.createNode({
                                text: description,
                                // _navtreedef: navtreedef,
                                leaf: navtreedef.isNavLeaf(),
                                checked: checked,

                                isIntermediate: false,
                                _id_composed: _id_composed,
                                _id: r.get('_id'),
                                description: description,
                                navTreeNodeId: navtreedef.getId(),
                                parentid: node.get('_id'),
                                parenttype: node.get('type'),
                                type: r.get('_type')

                            });
                        c.setNavTreeNode(navtreedef);

                        nodeHashMap.add(c);

                        //adds the new check information
                        hashMap.add(r.get("_id"), checked);

                        node.appendChild(c);

                        if (fireevent == true) {
                            this.fireEventArgs('checkchange', [c, checked]);
                        }

                        if (selectAll && c.get("type") == objectTypeName) {
                            selectionModel.select(c, true, true);
                        }

                    }, this);

                    node._childrenloaded = true;
                    node.sort();
                    hashMap.endUpdate();
                    deferred.resolve();
                } else {

                    node._childrenloaded = false;
                    deferred.reject();
                }
                // delete item after its usage
                Ext.asap(function () {
                    childrenstore.destroy();
                });
            },
            scope: this
        });

        return deferred.promise;
    },

    recursiveLoadParent: function (node) {
        var deferred = new Ext.Deferred();
        this._recursiveLoadParent(node).then(function () {
            deferred.resolve(node);
        }, Ext.emptyFn, Ext.emptyFn, this);

        return deferred.promise;
    },

    _recursiveLoadParent: function (node) {
        var deferred = new Ext.Deferred();

        this.loadParent.call(this, node).then(function (parent) {
            if (parent) {
                var nodeHashMap = this.getNodeHashMap();
                var treeNode = nodeHashMap.get(parent.getId());
                if (treeNode) {
                    this.insertNodeWithChilds(treeNode, node);
                    deferred.resolve();
                } else {
                    this._recursiveLoadParent.call(this, parent).then(function () {
                        deferred.resolve();
                    }, Ext.emptyFn, Ext.emptyFn, this);
                }
            } else {
                deferred.resolve();
            }
        }, Ext.emptyFn, Ext.emptyFn, this);

        return deferred.promise;
    },

    /**
     * 
     * @param {CMDBuildUI.model.gis.GeoValueTree} node 
     */
    loadParent: function (node) {
        //FIXME: due to bug, the navtreenode attached to the node is not congruent with the rest of the tree, making the function at #641 using the getNavTreeNode() on the "nodeToCheck" instead of "node"
        var deferred = new Ext.Deferred();
        var navtreedef = node.getNavTreeNode();

        if (navtreedef.isNavRoot()) {
            var root;
            if (navtreedef.get("ecqlFilter")) {
                root = this.getNodeHashMap().get(CMDBuildUI.view.map.tab.cards.NavigationTree.root_id_composed);

                this.hasChildren(root, node).then(function (root) {
                    if (root) {
                        deferred.resolve(root);
                    } else {
                        deferred.resolve(null);
                    }
                },
                    Ext.emptyFn, Ext.emptyFn, this);
            } else {
                root = this.getNodeHashMap().get('root_id_composed');
                deferred.resolve(root);
            }
            return deferred.promise;
        }

        var destinationType;
        var destinationTypeName;
        var sourceTypeName;
        var sourceId;

        var path;
        var advancedFilter;

        destinationTypeName = navtreedef.getParent().get('targetClass');
        sourceTypeName = node.get('type');

        sourceId = node.get('_id');
        destinationType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(destinationTypeName);
        advancedFilter = navtreedef.getUpFilter(sourceTypeName, sourceId, destinationTypeName);
        path = CMDBuildUI.util.helper.ModelHelper.getListBaseUrl(destinationType, destinationTypeName);

        // create temp store 
        var parentstore = this.getTemporaryStore(path, advancedFilter);

        parentstore.load({
            callback: function (records, operation, success) {
                //FIXME: move the logics after the load in another component
                var newnode;
                if (success) {

                    //if has ecqlFilter
                    if (navtreedef.get("ecqlFilter")) {
                        if (records && records.length == 1) {
                            var record = records[0],
                                parentNavTree = navtreedef.getParent(),
                                description = Ext.String.htmlEncode(record.get('Description'));

                            newnode = Ext.create('CMDBuildUI.model.gis.GeoValueTree', {
                                _id_composed: Ext.String.format('{0}-{1}', record.get('_id'), parentNavTree.getId()),
                                _id: record.get('_id'),
                                type: record.get('_type'),
                                navTreeNodeId: parentNavTree.getId(),
                                description: description
                            });
                            newnode.setNavTreeNode(parentNavTree);

                            /**this piece of code is created to check if the found parent starting from the given node has children.
                             * It could not have children because the cql filter is not applied in the call for the parent but must be called from the parent to the children
                             * 
                             */
                            this.hasChildren(newnode, node).then(function (newnode) {
                                if (newnode) {
                                    newnode.appendChild(node);
                                    node.set('parentid', record.get('_id'));

                                    deferred.resolve(newnode);
                                } else {
                                    deferred.resolve(null);
                                }
                            }, Ext.emptyFn, Ext.emptyFn, this);
                        } else {
                            deferred.resolve();
                        }
                    } else {

                        if (records && records.length == 1) {
                            var record = records[0],
                                parentNavTree = navtreedef.getParent(),
                                description = Ext.String.htmlEncode(record.get('Description'));

                            newnode = Ext.create('CMDBuildUI.model.gis.GeoValueTree', {
                                _id_composed: Ext.String.format('{0}-{1}', record.get('_id'), parentNavTree.getId()),
                                _id: record.get('_id'),
                                type: record.get('_type'),
                                navTreeNodeId: parentNavTree.getId(),
                                description: description
                            });
                            newnode.setNavTreeNode(parentNavTree);
                            newnode.appendChild(node);
                            node.set('parentid', record.get('_id'));
                            deferred.resolve(newnode);
                            return;
                        } else {

                        }
                        deferred.reject();
                    }
                } else {
                    deferred.reject();
                }
            },
            scope: this
        });

        return deferred.promise;
    },

    hasChildren: function (node, nodeToCheck) {
        var deferred = new Ext.Deferred();
        var navtreedef = nodeToCheck.getNavTreeNode();

        if (!navtreedef.isNavRoot()) {
            var sourceTypeName = node.get('type');
            var sourceId = node.get('_id');
        }

        var destinationTypeName = nodeToCheck.get('type');
        var destinationType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(destinationTypeName);

        var advancedFilter = navtreedef.getDownFilter(sourceTypeName, sourceId, destinationTypeName);
        var path = CMDBuildUI.util.helper.ModelHelper.getListBaseUrl(destinationType, destinationTypeName);

        var tmpStore = this.getTemporaryStore(path, advancedFilter);
        tmpStore.load({
            callback: function (records, operation, success) {
                if (success) {

                    var found = Ext.Array.findBy(records, function (item, index, array) {
                        return item.get('_id') == nodeToCheck.get('_id');
                    }, this);

                    if (found) {
                        deferred.resolve(node);
                    } else {
                        deferred.resolve(false);
                    }
                } else {
                    deferred.resolve(false);
                }
                // delete item after its usage
                Ext.asap(function () {
                    tmpStore.destroy();
                });
            }
        });

        return deferred.promise;
    },

    /**
     * 
     * @param {*} domainName 
     * @param {*} sourceClass 
     * @param {*} destinationClass 
     * @returns {Boolean} if true the check is passed. if false, the check is failed
     */
    checkDisabledDomainClasses: function (navtreedef, sourceClassName, destinationClassName) {
        var domains = Ext.getStore('domains.Domains');
        var domain = domains.findRecord('_id', navtreedef.get('domain'));

        if (Ext.isEmpty(domain)) {
            return false;
        }

        if (Ext.Array.contains(domain.get('disabledSourceDescendants'), navtreedef.get('direction') == '_1' ? destinationClassName : sourceClassName)) {
            return false;
        }

        if (Ext.Array.contains(domain.get('disabledDestinationDescendants'), navtreedef.get('direction') == '_1' ? sourceClassName : destinationClassName)) {
            return false;
        }

        return true;
    },

    getTemporaryStore: function (proxyUrl, advancedFilter) {
        // create temp store 
        var parentstore = Ext.create("Ext.data.Store", {
            fields: ['_id', 'Description'],
            proxy: {
                type: 'baseproxy',
                url: proxyUrl,
                extraParams: {
                    attrs: 'Id,Description,IdClass'
                }
            },
            advancedFilter: advancedFilter,
            pageSize: 0,
            sorters: ['Description'],
            remoteSort: true
        });
        return parentstore;
    },

    /**
     * 
     * @param {CMDBuildUI.model.gis.GeoValueTree} node 
     * @param {CMDBuildUI.model.gis.GeoValueTree} child 
     */
    insertNode: function (node, child) {
        var nodeHashMap = this.getNodeHashMap();

        //stores nodes checked value
        var hashMap = this.getMapContainerView().getHashMap();

        var checked = this.checkedValue(node, child);
        child.set('checked', checked);

        var parentNavTree = node.getNavTreeNode();
        var navTreeNode = child.getNavTreeNode();

        var addIntermediate = navTreeNode.get("subclassViewMode") === "subclasses" && navTreeNode.get("subclassViewShowIntermediateNodes");
        if (addIntermediate || (parentNavTree && parentNavTree.childs().getRange().length > 1 && navTreeNode.get("subclassViewMode") === "cards")) {
            var _id_composed = Ext.String.format('{0}-{1}-{2}-intermediate', node.get('_id'), navTreeNode.getId(), node.get('type'));
            var subChildNode = node.findChild('_id_composed', _id_composed);

            if (!subChildNode) {
                subChildNode = node.createNode({
                    description: navTreeNode.get("_description_translation") || CMDBuildUI.util.helper.ModelHelper.getObjectDescription(navTreeNode.get("targetClass")),
                    leaf: true,
                    checked: node.get('checked'),
                    isIntermediate: true,
                    navTreeNodeId: navTreeNode.getId(),
                    _id_composed: _id_composed,
                    _id: node.getId(),
                    type: child.get('type')

                });
                subChildNode.setNavTreeNode(navTreeNode);
                node.appendChild(subChildNode);
            }
            node = subChildNode;
        }

        var nodes = [];
        if (navTreeNode.get("subclassViewMode") === "subclasses") {
            var childKlass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(child.get('type')),
                subclassFilter = navTreeNode.get("subclassFilter");

            if (!Ext.isEmpty(subclassFilter)) {
                subclassFilter.split(',').forEach(function (subtype) {
                    var subtypeKlass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(subtype);
                    var isKlassChild = subtypeKlass === childKlass || Ext.Array.contains(subtypeKlass.getChildren(), childKlass);

                    if (isKlassChild) {
                        var _id_composed = Ext.String.format('{0}-{1}-{2}-intermediate', node.get('_id'), navTreeNode.getId(), subtype);
                        var subTypeChildNode = node.findChild('_id_composed', _id_composed);
                        var desc = CMDBuildUI.util.helper.ModelHelper.getObjectDescription(subtype);

                        if (!subTypeChildNode) {
                            subTypeChildNode = node.appendChild({
                                description: desc,
                                // _navtreedef: subChild,
                                leaf: true,
                                checked: node.get('checked'),
                                isIntermediate: true,
                                navTreeNodeId: navTreeNode.getId(),
                                _id_composed: _id_composed,
                                _id: node.get('_id'),
                                type: subtype
                            });
                            subTypeChildNode.setNavTreeNode(navTreeNode);
                        }
                        nodes.push(subTypeChildNode);
                    }
                });
            }
        }

        if (navTreeNode.isNavLeaf()) {
            child.set('leaf', true);
        } else {
            if (child._childrenloaded && Ext.isEmpty(child.childNodes)) {
                child.set('leaf', true);
            } else {
                child.set('leaf', false);
            }
        }

        nodeHashMap.add(child);
        if (nodes.length) {
            Ext.Array.forEach(nodes, function (node) {
                node.appendChild(child);
            }, this);
        } else {
            node.appendChild(child);
        }


        //adds the new check information
        hashMap.add(child.get("_id"), checked);
        this.fireEventArgs('checkchange', [child, checked]);
    },

    insertNodeWithChilds: function (node, child) {
        var a = [];
        this.linearizeChilds.call(this, child, a);
        //TODO: instead of calling the handler, should add elements to 'attach_nav_tree_collection' component. Then the event will bi fired automatically and handled; 
        //like in app/view/map/MapController.js:893
        //maybe the before TODO: can't be applied due to app/view/map/tab/cards/NavigationTree.js:653.         
        // this.getController().onCollectionAdd(a);


        var attach_nav_tree_collection = this.getAttach_nav_tree_collection();
        attach_nav_tree_collection.add(a);

    },

    linearizeChilds: function (node, arr) {
        var _tmpchilds = [];
        Ext.Array.forEach(node.childNodes, function (item, index, array) {
            _tmpchilds.push(item);
        }, this);

        arr.push(node);
        Ext.Array.forEach(_tmpchilds, function (item, index, array) {
            item.remove();
            arr.push(item);
            this.linearizeChilds(item, arr);
        }, this);
    },

    checkedValue: function (parent, child) {
        var navtreedef = child.getNavTreeNode();
        var showOnlyOne = navtreedef.get('showOnlyOne');

        var fireevent;
        var hashKey = child.get("_id");
        var hashValue = this.getNodeHashMap().get(hashKey);
        var checked;

        if (showOnlyOne) {
            if (!Ext.isEmpty(hashValue)) {
                checked = hashValue[0].get('checked');

                if (checked == true) {

                    //this for analizes the siblings. Modifies the checked value
                    for (var i = 0; i < parent.childNodes && checked; i++) {
                        var item = childNodes[i];
                        if (item.get('navTreeNodeId') == child.get('navTreeNodeId') && item.get('checked') == true) {
                            checked = false;
                        }
                    }

                    if (checked == true) {
                        //no sibling has a checked value;
                        if (parent.get('checked') == true) {
                            return true;
                        } else {
                            //fireevent
                            return false;
                        }
                    } else {
                        //a sibling has a checked value

                        //fireevent
                        return false;
                    }

                } else {
                    return false;
                }

            } else {

                checked = true;
                for (var i = 0; i < parent.childNodes && checked; i++) {
                    var item = childNodes[i];
                    if (item.get('navTreeNodeId') == child.get('navTreeNodeId') && item.get('checked') == true) {
                        checked = false;
                    }
                }

                if (checked == true) {
                    if (parent.get('checked') == true) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }

            }
        } else {
            if (!Ext.isEmpty(hashValue)) {
                checked = hashValue[0].get('checked');

                if (checked != parent.get('checked')) {
                    fireevent = true;
                }
                return checked;

            } else {
                return parent.get('checked');
            }
        }
    },

    expandConsecutively: function (node) {
        var deferred = new Ext.Deferred();
        var path = [];
        var tmpNode = node;

        node = node.parentNode;
        while (!node.isRoot()) {
            path.unshift(node);
            node = node.parentNode;
        }

        if (!this._mask) {
            var mask = CMDBuildUI.util.Utilities.addLoadMask(this);
            this._mask = mask;
        }
        var i = 0;
        this._expandConsecutively(i, path, deferred, tmpNode);

        return deferred.promise;
    },

    _expandConsecutively: function (i, path, deferred, record) {
        if (i < path.length) {
            this.getController().expandCMDBuildNode(path[i]).then(function () {
                path[i].expand();
                this._expandConsecutively(++i, path, deferred, record);
            }, Ext.emptyFn, Ext.emptyFn, this);
        } else {
            if (this._mask) {
                CMDBuildUI.util.Utilities.removeLoadMask(this._mask);
                delete this._mask;
                this.ensureVisible(record.getPath(), {
                    animate: true
                });

                deferred.resolve(record);
            }
        }
    }
});
