Ext.define('CMDBuildUI.view.bim.bimserver.ContainerBimController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.bim-bimserver-containerbim',
    listen: {
        global: {
            highlitedifcobject: 'onHiglitedObject',
            ifctreeready: 'onIfcTreeReady'
        },
        component: {
            '#': {
                beforerender: 'onBeforeRender',
                resize: function () {
                    CMDBuildUI.util.bimsurfer.util.resize();
                }
            },

            // 'bim-bimserver-container panel #bim-bimserver-containerbim-menu-camera': {
            //     click: 'cameraHandler'
            // },
            'bim-bimserver-container panel #bim-bimserver-containerbim-menu-mode': {
                click: 'modeHandler'
            }
        }
    },

    onBeforeRender: function (view, eOpts) {
        this._tmpOids = [];
        this._tmpLayers = [];
        this.ifcLayers = [];

        var vm = this.getViewModel();
        vm.bind({
            hiddenTypes: '{bim-bimserver-containerbim.hiddenTypes}'
        }, function (data) {
            if (data.hiddenTypes) {
                data.hiddenTypes.addListener('endupdate', this.onHiddenTypesEndUpdate, this);
            }
        }, this);

        vm.bind({
            hiddenNode: '{bim-bimserver-containerbim.hiddenNodes}'
        }, function (data) {
            if (data.hiddenNode) {
                data.hiddenNode.addListener('endupdate', this.onHiddenNodeEndUpdate, this);
            }
        }, this);
    },

    /**
     * 
     * @param {*} collection 
     * @param {*} eOpts 
     */
    onHiddenTypesEndUpdate: function (hiddenTypes, details) {
        var hiddenNodes = this.getView().getHiddenNodes();
        var show = [];
        var semiHide = [];
        var hide = [];

        Ext.Array.forEach(hiddenTypes.getRange(), function (item, index, array) {
            var ifcName = item.get('ifcName');
            var group = hiddenNodes.getGroups().get(ifcName);

            if (group) {

                var checked = item.get('clicks') == 0 ? true : false;
                Ext.Array.forEach(group.getRange(), function (item, index, value) {

                    var oid = item.get('oid');
                    switch (checked) {
                        case true:
                            show.push(oid);
                            break;
                        case false:
                            hide.push(oid);
                            break;
                    }
                    item.set('checked', checked);
                }, this);
            }
        }, this);

        //         CMDBuildUI.util.bim.Viewer.setVisibility(show, true);
        //         CMDBuildUI.util.bim.Viewer.setVisibility(hide, false);
    },

    /**
     * 
     * @param {*} collection 
     * @param {*} details 
     */
    onHiddenNodeEndUpdate: function (hiddenNodes, details) {
        var hiddenTypes = this.getView().getHiddenTypes();
        var show = [];
        var semiHide = [];
        var hide = [];

        var tmpCollection = Ext.util.Collection.create({
            keyFn: function (item) {
                return item.get('ifcName');
            }
        });

        Ext.Array.forEach(hiddenNodes.getRange(), function (item, index, array) {
            var checked = item.get('checked');
            var oid = item.get('oid');
            var ifcName = item.get('ifcName');
            var tmpItem = tmpCollection.get(ifcName) ?
                tmpCollection.get(ifcName)
                :
                tmpCollection.add(Ext.create('CMDBuildUI.model.bim.Types', {
                    ifcName: ifcName,
                    qt: 0
                }));

            switch (checked) {
                case true:
                    show.push(oid);
                    tmpItem.set('qt', tmpItem.get('qt') + 1);
                    break;
                case false:
                    hide.push(oid);
                    tmpItem.set('qt', tmpItem.get('qt') - 1);
                    break;
            }
        }, this);

        Ext.Array.forEach(hiddenTypes.getRange(), function (item, index, array) {
            var ifcName = item.get('ifcName');

            var tmpItem = tmpCollection.get(ifcName);
            if (tmpItem) {
                var qt = tmpItem.get('qt');

                if (item.get('qt') == Math.abs(qt)) {
                    if (qt > 0) {
                        item.set('clicks', 0);
                    } else {
                        item.set('clicks', 2);
                    }
                }
            }

        }, this);

        //         CMDBuildUI.util.bim.Viewer.setVisibility(show, true);
        //         CMDBuildUI.util.bim.Viewer.setVisibility(hide, false);
    },

    /**
     * This function is executed when the ifctreeready is fired
     * @param {Number} CMDBuildUI.model.bim.Projects The project
     */
    onIfcTreeReady: function (project) {
        var view = this.getView();
        var loadmask = view.getLoadMask();
        ++loadmask.count;
        loadmask.show();

        var ifcRoot = CMDBuildUI.util.bim.IfcTree.getIfcRoot();
        var ifcNode = ifcRoot[ifcRoot.length - 1];

        var ifcTreeCreation = {
            text: text = project.get('description') ? project.get('description') : project.get('name'),
            children: [this.ifcTreeCreationRecursive(ifcNode, project)],
            leaf: false,
            checked: true,
            project: project
        };

        var treeStore = view.getTreeStore();
        treeStore.getRootNode().appendChild(ifcTreeCreation);
        this.onTreeNodeAppend(treeStore.getRootNode());

        this.openDepth(treeStore.getRootNode(), this.DEPTH);

        var layerStore = view.getLayerStore();
        layerStore.loadRawData(this.computeObject(), true);
        this.onLayerStoreLoad(layerStore.getRange());

        var selectedId = this.getView().getSelectedId();
        if (selectedId) {
            var node = treeStore.findNode('globalId', selectedId, true, true, false);
            if (node) {
                var object = node.get('object');
                CMDBuildUI.util.bim.Viewer.select(object);
            }
        }

        --loadmask.count;
        if (!loadmask.count) {
            loadmask.hide();
        }
    },

    onTreeNodeAppend: function (node) {
        var collection = this.getView().getHiddenNodes();
        collection.beginUpdate();
        this.ifcTreeNodeRecursive(collection, node);
        collection.endUpdate();
    },

    ifcTreeNodeRecursive: function (collection, record) {
        if (record) {
            var item = collection.get(record.get('oid'));
            if (!item) {
                collection.add(record);
            }
        }

        if (Ext.isArray(record.childNodes)) {
            Ext.Array.forEach(record.childNodes, function (child, index, array) {
                this.ifcTreeNodeRecursive(collection, child);
            }, this);
        }
    },

    onLayerStoreLoad: function (records) {
        var view = this.getView();
        var hiddenTypes = view.getHiddenTypes();

        hiddenTypes.beginUpdate();
        Ext.Array.forEach(records, function (record, index, array) {
            // var ifcName = record.get('ifcName');

            // if (!hiddenTypes.get(ifcName)) {
            hiddenTypes.add(record);
            // }
        }, this);
        hiddenTypes.endUpdate();
    },

    /**
    * recursive function for generating from the raw ifcTree the EXTJS root tree with childs
    * The function also makes operatins for the layer tab
    * @param {Object} node the current analized node 
    */
    ifcTreeCreationRecursive: function (node, project) {
        /**
          * manage the modification of this._tmpLayers for another file (Layers.js)
        */
        var name = node.ifcObject.object._t;
        var oid = node.ifcObject.oid;

        if (!this._tmpOids[oid]) {
            this._tmpOids[oid] = true;
            if (!this._tmpLayers[name]) {
                this._tmpLayers[name] = {
                    name: name.replace('Ifc', ""),
                    ifcName: name,
                    qt: 1,
                    objects: [node.ifcObject]
                };
            } else {
                this._tmpLayers[name].qt++;
                this._tmpLayers[name].objects.push(node.ifcObject)
            }

            /**
             * manage the creation of the ifcTree
            */
            var text = (node.ifcObject.object._t || "").replace("Ifc", "") + " " + (node.ifcObject.object.Name || "");
            var tmpNode = {
                text: text,
                children: [],
                oid: node.ifcObject.oid,
                globalId: node.ifcObject.object.GlobalId,
                gid: node.ifcObject.gid,
                ifcName: node.ifcObject.object._t,
                object: node.ifcObject,
                checked: node.ifcObject.trans.mode == 1 ? true : false,
                leaf: true,
                project: project
            };
            node.children.forEach(function (childNode) {
                tmpNode.leaf = false;

                var childTemp = this.ifcTreeCreationRecursive(childNode, project);
                childTemp ? tmpNode.children.push(childTemp) : null;

            }, this);

            return tmpNode;
        }
        return null;
    },

    /**
     * this function takes an object and removes the reference they had and creates an array with those values
     */
    computeObject: function () {
        //removes the unwanted types

        var ifcLayers = [];
        for (var ifcName in this._tmpLayers) {
            if (this._tmpLayers.hasOwnProperty(ifcName)) {

                // sets some default values to action column
                var mode = CMDBuildUI.util.bim.Viewer.transparentLayers[ifcName];
                if (mode != null) {
                    this._tmpLayers[ifcName].clicks = mode;
                } else {
                    this._tmpLayers[ifcName].clicks = 0;
                }

                ifcLayers.push(this._tmpLayers[ifcName]);
            }
        }

        return ifcLayers;
    },

    /**
     * @param {Object} highlited
     */
    onHiglitedObject: function (highlighted) {
        var objectId = highlighted.oid;

        var store = this.getView().down('bim-bimserver-tab-cards-tree').getStore()
        var root = store.getRoot();
        var node = this.recursiveFound(root, 'oid', objectId);

        if (node) {
            var globalId = node.get('globalId');
            var projectId = node.get('project').getId();

            CMDBuildUI.util.bim.Util.getRelatedCard(projectId, globalId, function (data) {

                var view = this.getView().down('#bim-bimserver-tab-cards-card');
                if (data.exists == true) {

                    if (view.isDisabled() == true) {
                        view.setDisabled(false);
                    }

                    view.setObjectId(data.ownerId);
                    view.setObjectTypeName(data.ownerType);

                } else {
                    var tabpanel = view.up('tabpanel');
                    var activeTab = tabpanel.getActiveTab();

                    if (activeTab.getItemId() == 'bim-bimserver-tab-cards-card') {
                        tabpanel.setActiveTab(0);
                    }
                    view.setDisabled(true);
                }
            }, this);
        }
    },

    /**
     * This function gets a node and looks in hi children in or to find the one with a specific value
     * @param {} node The node to inspect
     * @param {} value the value of the node we are looking for
     * @returns the node with node.value = value
     */
    recursiveFound: function (node, property, value) {
        if (node.data[property] == value) {
            return node;
        } else {
            for (var i = 0; i < node.childNodes.length; i++) {
                var tmpChild = this.recursiveFound(node.childNodes[i], property, value);
                if (tmpChild) {
                    return tmpChild;
                }
            }
        }

        return null;
    },

    /**
     * @param  {Number} poid the identifier for the bim project
     * @param  {String} type Ifc4 or Ifc2x3tc1
     * @param  {} observer
     */
    onDivRendered: function (poid, selectedId) {
        var me = this;
        var loadmask = this.getView().getLoadMask();

        loadmask.show();
        loadmask.mon(me.getView(), 'removed', function () {
            loadmask.hide();
        }, me);

        CMDBuildUI.util.bim.Viewer.show(
            poid, //project id
            function () { //success callback
                loadmask.hide();
            }, function () {
                --loadmask.count;
                if (loadmask.count <= 0) {
                    loadmask.hide();
                }
                CMDBuildUI.util.Utilities.closePopup('bimPopup');
                CMDBuildUI.util.Notifier.showErrorMessage('Bim error');
            }, this);
    },

    /**
     * 
     * @param {Ext.component} tool 
     * @param {Object} e 
     */
    modeHandler: function (tool, e) {
        var mode = tool.mode;

        switch (mode) {
            case 'pan':
                tool.mode = 'rotate';
                tool.setIconCls('x-fa fa-arrows');
                tool.setTooltip(CMDBuildUI.locales.Locales.bim.menu.pan)
                CMDBuildUI.util.bim.Viewer.setRotateMode();
                break;
            case 'rotate':
                tool.mode = 'pan'
                tool.setIconCls('x-fa fa-repeat');
                tool.setTooltip(CMDBuildUI.locales.Locales.bim.menu.rotate);
                CMDBuildUI.util.bim.Viewer.setPanMode();
                break;
        }
    },

    privates: {
        /**
         * This array contains the ifcLayers found wile exlplorying the tree 
         */
        _tmpLayers: [],


        _tmpOids: [],

        /**
         * 
         */
        DEPTH: 3,

        /**
         * This function expands all the nodes wit a depth value <= depth
         * @param {object} node the node we start;
         * @param {depth} depth the value of depth to reach from that node;
         */
        openDepth: function (node, depth) {
            var nodeDepth = node.getDepth();
            if (nodeDepth <= depth) {
                node.expand();

                var childNodes = node.childNodes;
                for (var i = 0; i < childNodes.length; i++) {
                    this.openDepth(childNodes[i], depth);
                }
            }


            // this.recursiveOpenDepth(node,0,depth);
        }
    }
});
