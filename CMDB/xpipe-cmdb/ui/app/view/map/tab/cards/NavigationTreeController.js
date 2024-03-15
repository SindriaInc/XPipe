//TODO: Handle the loading of new records in the main store
Ext.define('CMDBuildUI.view.map.tab.cards.NavigationTreeController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.map-tab-cards-navigationtree',
    listen: {
        component: {
            '#': {
                beforerender: 'onBeforeRender',
                beforeselect: 'onBeforeSelect',
                beforedeselect: 'onBeforeDeselect',
                selectionchange: 'onSelectionChange',
                select: 'onSelect',
                deselect: 'onDeselect',
                itemexpand: 'onItemExpand',
                checkchange: 'onCheckChange',
                beforecellclick: 'onBeforeCellClick'
            }
        },

        global: {
            cardupdated: 'onCardUpdated'
        }
    },

    /**
     * 
     * @param {Ext.selection.RowModel} selectionModel 
     * @param {Ext.data.Model} selected 
     * @param {Number} index 
     * @param {Object} eOpts 
     */
    onSelect: function (selectionModel, selected, index, eOpts) {
        if (selectionModel.selectionMode === "SIMPLE" && selected) {
            this.getView().onSelectItem(selected)
        }
    },

    /**
     * 
     * @param {Ext.selection.RowModel} selectionModel 
     * @param {Ext.data.Model} deselected 
     * @param {Number} index 
     * @param {Object} eOpts 
     */
    onDeselect: function (selectionModel, deselected, index, eOpts) {
        if (selectionModel.selectionMode === "SIMPLE" && deselected) {
            this.getView().modifySelection(deselected, true);
        }
    },

    onBeforeRender: function (view) {

        this.getViewModel().bind({
            navigationtree: '{map-tab-tabpanel.navigationTree}',
            store: '{map-tab-cards-navigationtree.store}'
        }, function (data) {
            if (data.navigationtree && data.store) {
                var view = this.getView();
                var mask = CMDBuildUI.util.Utilities.addLoadMask(view);
                view.loadChildren(data.store.getRoot(), data.navigationtree.getRoot()).then(function () {
                    if (this.getView()) {
                        this.expandCMDBuildNode(data.store.getRoot()).then(function () {
                            this.onCheckChange(data.store.getRoot(), true);
                            this.getView().setInitialized(true);
                            CMDBuildUI.util.Utilities.removeLoadMask(mask);
                        }, Ext.emptyFn, Ext.emptyFn, this);
                    }
                }, Ext.emptyFn, Ext.emptyFn, this);
            }
        }, this);

        this.getViewModel().bind({
            attach_nav_tree_collection: '{map-tab-tabpanel.attach_nav_tree_collection}',
            initialized: '{map-tab-cards-navigationtree.initialized}'
        }, function (data) {
            if (data.attach_nav_tree_collection && data.initialized) {
                data.attach_nav_tree_collection.addListener('add', this.onCollectionAddHandler, this);

                if (data.attach_nav_tree_collection.length != 0) {
                    var hashMap = this.getView().getMapContainerView().getHashMap();
                    hashMap.beginUpdate();
                    this.onCollectionAdd.call(this, data.attach_nav_tree_collection.getRange());
                    hashMap.endUpdate();
                }
            }
        }, this);
    },

    onBeforeCellClick: function (view, td, cellIndex, record, tr, rowIndex, e, eOpts) {
        if (e.target.className.includes('checkbox')) {
            var checked = !record.get('checked');
            record.set('checked', checked);
            view.fireEventArgs('checkchange', [record, checked, e, eOpts]);
            return false;
        }
    },
    /**
     * 
     * @param {Ext.selection.RowModel} selectionmodel 
     * @param {Ext.data.Model} record 
     * @param {Number} index 
     * @param {Object} eOpts 
     * 
     * @note returning false will not select the record and will not continue by firing 'selectionchenge' event
     * returning true will add the record to selectionmodel and firing 'selectionchange' event
     */
    onBeforeSelect: function (selectionmodel, record, index, eOpts) {
        var view = this.getView();
        var objectTypeName = view.getObjectTypeName();

        //intermediate foldesr are not selectable
        if (record.get('isIntermediate')) {
            return false;
        }

        if (objectTypeName == record.get('type')) {

            if (selectionmodel.selectionMode === "SIMPLE") {
                return true;
            }

            var isNew = false;

            //check if record is newer despite of already selected one
            var selectedRange = selectionmodel.getSelected().getRange();
            for (var i = 0; i < selectedRange.length && !isNew; i++) {
                var selected = selectedRange[i];
                if (selected.get('_id') != record.get('_id')) {
                    isNew = true;
                }
            }

            if (isNew == true) {
                //this piece of code is usefull to remove the existing selection. Due to selectionMode = 'MULTI' this part is needed

                //suspend events
                selectionmodel.deselectAll(true);

                //fire events
                selectionmodel.select([record], false, false);

                return false;
            } else {
                return true;
            }
        }

        return false;
    },

    /**
     * 
     * @param {Ext.selection.RowModel} selectionModel 
     * @param {Ext.data.Model} record 
     * @param {Number} index 
     * @param {Object} eOpts 
     * @returns false will not deselect the record and not firing 'onDeselect' event
     * returns true will deselect the record and firing 'onDeselect' event
     */
    onBeforeDeselect: function (selectionModel, record, index, eOpts) {
        return this.getViewModel().get("settingsMap.selectAll") ? false : true;
    },

    /**
     * 
     * @param {Ext.selection.Model} selectionModel 
     * @param {Ext.data.Model[]} selected 
     * @param {Object} eOpts 
     */
    onSelectionChange: function (selectionModel, selected, eOpts) {
        if (selectionModel.selectionMode !== "SIMPLE") {
            var objectId = null;
            if (selected.length > 0) {
                objectId = Ext.num(selected[0].get('_id'));
            }

            this.getView().setObjectId(objectId);
        }
    },

    /**
     * 
     * @param {*} collection 
     * @param {*} details 
     * @param {*} eOpts 
     */
    onCollectionAddHandler: function (collection, details, eOpts) {
        //stores nodes checked value
        var hashMap = this.getView().getMapContainerView().getHashMap();
        hashMap.beginUpdate();
        this.onCollectionAdd.call(this, details.items);
        hashMap.endUpdate();

    },

    /**
     * This function elaborates the new GeoValueTree elements wich must be added in the tree.
     * The function verifies if the records are yet in the tree, if they are the inserting is skipped otherwise they are inserted in the tree.
     * 
     * The function is called recursively until all items are added in the tree. 
     * NOTE: The item not added are stored and added late. The late insertion can be caused because the child we want to insert doesn't have the proper parent in the tree yet.
     * this is caused because the onCollectionAddHandler.details.items are sorted by the key and is not mantained the order given by the server.
     * @param {} items 
     */
    onCollectionAdd: function (items) {
        var itemsNotAdded = [];
        var nodeHashMap = this.getView().getNodeHashMap();

        Ext.Array.forEach(items, function (item, index, array) {

            var _id_composed = item.getId();

            if (nodeHashMap.get(_id_composed)) {
                return;
            } else {
                var navtreedef = item.getNavTreeNode();

                var _id_composed_parent;
                if (navtreedef.isNavRoot()) {
                    _id_composed_parent = CMDBuildUI.view.map.tab.cards.NavigationTree.root_id_composed;
                } else {
                    _id_composed_parent = Ext.String.format('{0}-{1}', item.get('parentid'), navtreedef.getParent().getId());
                }

                var pareentNode = nodeHashMap.get(_id_composed_parent);
                if (pareentNode) {
                    this.getView().insertNode(pareentNode, item);
                } else {
                    itemsNotAdded.push(item);
                }
                return;
            }
        }, this);

        if (items.length == itemsNotAdded.length) {
            return;
        }

        if (itemsNotAdded.length) {
            this.onCollectionAdd.call(this, itemsNotAdded);
        }
    },

    /**
     * 
     * @param {*} node 
     * @param {*} menuDef 
     */
    expandCMDBuildNode: function (node) {
        //FIXME: the intermediate nodes are different nodes, shoud not have set the type or, carry the one of the parent as _id do

        var deferred = new Ext.Deferred();
        if (node.isIntermediate) {
            deferred.resolve();
            return deferred;
        }

        var promises = [];
        var childNode = node;
        // node.childNodes.forEach(function (childNode) {
        if (childNode._childrenloaded == undefined || childNode._childrenloaded == false) {

            var menudef = childNode.getNavTreeNode();
            // childNode._childrenloaded = true;
            menudef.childs().getRange().forEach(function (subChild) {
                var subChildNode;
                var addintermediate = subChild.get("subclassViewMode") === "subclasses" && subChild.get("subclassViewShowIntermediateNodes");
                if (addintermediate || (menudef.childs().getRange().length > 1 && subChild.get("subclassViewMode") === "cards")) {
                    var _id_composed = Ext.String.format('{0}-{1}-{2}-intermediate', childNode.get('_id'), subChild.getId(), childNode.get('type'));
                    subChildNode = childNode.findChild('_id_composed', _id_composed);

                    if (!subChildNode) {
                        subChildNode = childNode.appendChild({
                            description: subChild.get("_description_translation") || CMDBuildUI.util.helper.ModelHelper.getObjectDescription(subChild.get("targetClass")),
                            // _navtreedef: subChild,
                            leaf: true,
                            checked: childNode.get('checked'),
                            isIntermediate: true,
                            _id_composed: _id_composed,
                            _id: childNode.get('_id'),
                            type: subChild.get('targetClass')
                        });
                        subChildNode.setNavTreeNode(subChild);
                    }
                } else {
                    subChildNode = childNode;
                }


                if (subChild.get("subclassViewMode") === "subclasses") {
                    var subclassFilter = subChild.get("subclassFilter");

                    if (!Ext.isEmpty(subclassFilter)) {
                        subclassFilter.split(',').forEach(function (subtype) {
                            var _id_composed = Ext.String.format('{0}-{1}-{2}-intermediate', subChildNode.get('_id'), subChild.getId(), subtype);
                            var subTypeChildNode = subChildNode.findChild('_id_composed', _id_composed);

                            if (!subTypeChildNode) {
                                var desc = subChild.get("_subclass_" + subtype + "_description_translation");
                                if (!desc) {
                                    desc = CMDBuildUI.util.helper.ModelHelper.getObjectDescription(subtype);
                                }
                                subTypeChildNode = subChildNode.appendChild({
                                    description: desc,
                                    // _navtreedef: subChild,
                                    leaf: true,
                                    checked: subChildNode.get('checked'),
                                    isIntermediate: true,
                                    _id_composed: _id_composed,
                                    _id: subChildNode.get('_id'),
                                    type: subtype
                                });
                                subTypeChildNode.setNavTreeNode(subChild);
                            }
                            if (subTypeChildNode._childrenloaded == undefined || subTypeChildNode._childrenloaded == false) {
                                promises.push(this.getView().loadChildren(subTypeChildNode, subChild));
                            } else if (subTypeChildNode._childrenloade instanceof Ext.Deferred) {
                                promises.push(subTypeChildNode._childrenloaded);
                            }

                        }, this);
                    }
                } else {
                    if (subChildNode._childrenloaded == undefined || subChildNode._childrenloaded == false) {
                        promises.push(this.getView().loadChildren(subChildNode, subChild));
                    } else if (subChildNode._childrenloade instanceof Ext.Deferred) {
                        promises.push(subChildNode._childrenloaded);
                    }

                }
            }, this);
        } else if (childNode._childrenloaded instanceof Ext.Deferred) {
            promises.push(childNode._childrenloaded);
        }
        // }, this);

        if (promises.length != 0) {
            Ext.Promise.all(promises).then(function () {
                if (!node._childrenloaded) {
                    node._childrenLoaded = true;
                }
                deferred.resolve();
            }, Ext.emptyFn, Ext.emptyFn, this);
        } else {
            deferred.resolve();
        }
        return deferred.promise;
    },
    /**
     * Fired by user expanding the node
     * @param {*} node 
     * @param {*} eOpts 
     */
    onItemExpand: function (node, eOpts) {
        this.expandCMDBuildNode(node).then(function () { });

    },

    actionColumn: function (view, rowIndex, colIndex, item, e, record, row) {
        var navTreeView = this.getView(),
            objectType = CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
            objectTypeName = navTreeView.getObjectTypeName(),
            objectId = Ext.num(record.get('_id')),

            targhetTypeName = record.get('type'), //record.getNavTreeNode().get('targetClass');
            targhetKlass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(targhetTypeName),
            targhetTypeKlassHierarchy = targhetKlass.getHierarchy();

        switch (objectType) {
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:

                if (Ext.Array.contains(targhetTypeKlassHierarchy, objectTypeName)) {
                    if (!navTreeView.getViewModel().get("settingsMap.selectAll")) {
                        if (!record.get('isIntermediate')) {
                            if (navTreeView.getObjectId(objectId) == record.get("_id")) {
                                var selectionModel = navTreeView.getSelectionModel();
                                if (selectionModel.selectionMode === "SIMPLE") {
                                    if (selectionModel.isSelected(record)) {
                                        navTreeView.modifySelection(record, true);
                                        selectionModel.deselect(record, true);
                                    } else {
                                        navTreeView.modifySelection(record, false);
                                        selectionModel.select(record, true, true);
                                    }
                                }
                            }
                            navTreeView.setObjectId(objectId);
                        } else {
                            navTreeView.setObjectId(null);
                        }

                        this.customCheck(record);
                    }
                } else {

                    var url;
                    if (!record.get('isIntermediate')) {
                        url = CMDBuildUI.util.Navigation.getClassBaseUrl(targhetTypeName, objectId, null, true);
                    } else {
                        url = CMDBuildUI.util.Navigation.getClassBaseUrl(targhetTypeName, null, null, true);
                    }

                    CMDBuildUI.util.Utilities.redirectTo(url);
                }
                break;
            default:
                CMDBuildUI.util.Logger.log(
                    Ext.String.format('Redirect from tree not implemented for type: {0}', data.objectType),
                    CMDBuildUI.util.Logger.levels.debug);
                break;
        }
    },

    onStoreUpdate: function (store, record, operation, modifiedFieldNames, details, eOpts) {

        //when record is added or checked field is modified
        if (!record.isRoot() && (!modifiedFieldNames || Ext.Array.contains(modifiedFieldNames, 'checked'))) {
            var navtreedef = record.item.getNavTreeNode();
            if (navtreedef.get('showOnlyOne') == true) {

                Ext.Array.forEach(record.parentNode.childNodes, function (item, array, index) {
                    var checked = record.get('checked');
                    if (item != record && item.getNavTreeNode() == navtreedef && checked == true) {
                        this.onCheckChange(item, false);
                    }
                }, this);
            }
        }
    },

    /**
     * 
     * @param {Ext.data.TreeModel} node 
     * @param {Boolean} checked 
     * @param {Ext.event.Event} e 
     * @param {Object} eOpts 
     */
    onCheckChange: function (node, checked, e, eOpts) {
        node.set('checked', !checked); //restores node check to not interupt this.downCheckChange first check
        var hashMap = this.getView().getMapContainerView().getHashMap();
        hashMap.beginUpdate();

        if (!node.isRoot() && !node.get('isIntermediate')) {
            //finds double occurencies for the node in the tree
            var nodeHashMap = this.getView().getNodeHashMap();
            var group = nodeHashMap.getGroups().get(node.get('_id'));

            Ext.Array.forEach(group.getRange(), function (node) {
                this.downCheckChange(node, checked, hashMap);
                this.upCheckChange(node, checked, hashMap);
            }, this);

        } else {
            this.downCheckChange(node, checked, hashMap);
            this.upCheckChange(node, checked, hashMap);
        }

        hashMap.endUpdate();
    },

    downCheckChange: function (node, checked, hashMap) {
        if (node.get('checked') == checked) {
            return;
        }

        node.set('checked', checked);
        var nodeHashMap = this.getView().getNodeHashMap();
        /**
         * sets the sigling to unchecked if needed
         */
        if (checked == true) {
            var navtreedef = node.getNavTreeNode();
            if (navtreedef && navtreedef.get('showOnlyOne') == true) {
                Ext.Array.forEach(node.parentNode.childNodes, function (item, array, index) {

                    if (item != node && item.getNavTreeNode() == navtreedef) {
                        var group = nodeHashMap.getGroups().get(item.get('_id'));
                        Ext.Array.forEach(group.getRange(), function (item) {
                            this.downCheckChange(item, false, hashMap);
                        }, this);
                    }
                }, this);
            }
        }

        if (!node.get('isIntermediate')) {
            hashMap.add(node.get('_id'), checked);
        }

        var firstShowOnlyOne = {};
        node.eachChild(function (child) {
            if (!child.get('isIntermediate')) {

                //finds double occurencies for the child in the tree

                var group = nodeHashMap.getGroups().get(child.get('_id'));
                if (group) {

                    var childs = group.getRange();

                    Ext.Array.forEach(childs, function (child) {
                        var navtreedef = child.getNavTreeNode();
                        if (navtreedef.get('showOnlyOne') == true) {
                            if (!firstShowOnlyOne[navtreedef.getId()]) {
                                if (checked == true) {
                                    firstShowOnlyOne[navtreedef.getId()] = true;
                                    this.downCheckChange(child, true, hashMap);
                                    this.upCheckChange(child, true, hashMap);

                                } else {
                                    this.downCheckChange(child, false, hashMap);
                                    this.upCheckChange(child, false, hashMap);
                                }

                            } else {
                                if (checked == true) {
                                    this.downCheckChange(child, false, hashMap);
                                    this.upCheckChange(child, false, hashMap);
                                } else {
                                    this.downCheckChange(child, false, hashMap);
                                    this.upCheckChange(child, false, hashMap);
                                }
                            }
                        } else {
                            this.downCheckChange(child, checked, hashMap);
                            this.upCheckChange(child, checked, hashMap);
                        }

                    }, this);
                }
            } else {
                this.downCheckChange(child, checked, hashMap);
                this.upCheckChange(child, checked, hashMap);
            }
        }, this);
    },

    upCheckChange: function (node, checked, hashMap) {
        if (checked == true) {
            node.set('checked', checked);
            if (!node.get('isIntermediate')) {
                hashMap.add(node.get('_id'), checked);
            }
            var parent = node.parentNode;

            var navtreedef = node.getNavTreeNode();
            if (navtreedef && navtreedef.get('showOnlyOne') == true) {
                var nodeHashMap = this.getView().getNodeHashMap();

                Ext.Array.forEach(parent.childNodes, function (item) {
                    if (item != node && item.getNavTreeNode() == navtreedef && item.get('checked') == true) {

                        var group = nodeHashMap.getGroups().get(item.get('_id'));
                        Ext.Array.forEach(group.getRange(), function (item) {
                            this.downCheckChange(item, false, hashMap);
                        }, this);
                    }
                }, this);
            }

            if (parent) {
                this.upCheckChange(parent, checked, hashMap);
            }
        }
    },

    /**
     * 
     * @param {Ext.data.TreeModel} node 
     * @param {Boolean} checked 
     */
    customCheck: function (node) {
        if (node.get('checked') == true) {
            return;
        }

        var parentNode = node;
        var navigationTree = node.getNavTreeNode();

        while (!navigationTree.isNavRoot()) {
            if (navigationTree.get('showOnlyOne')) {
                this.onCheckChange.call(this, parentNode, true);
                return;
            } else {
                parentNode = parentNode.parentNode;
                navigationTree = parentNode.getNavTreeNode();
            }
        }

        this.onCheckChange.call(this, node, true);
    },

    /**
     * Issue #3688
     * @param {*} card 
     */
    onCardUpdated: function (card) {
        var view = this.getView();
        var nodeHashMap = view.getNodeHashMap();

        var groups = nodeHashMap.getGroups();
        var group = groups.get(card.getId());

        if (group) {
            var description = Ext.String.htmlEncode(card.get('Description'));

            group.each(function (item, index, array) {
                item.set('description', description);
            }, this)
        }
    }
});
