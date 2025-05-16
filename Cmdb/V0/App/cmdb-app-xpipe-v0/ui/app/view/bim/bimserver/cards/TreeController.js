Ext.define('CMDBuildUI.view.bim.bimserver.tab.cards.TreeController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.bim-bimserver-tab-cards-tree',
    listen: {
        global: {
            highlitedifcobject: 'onHighlitedIfcObject'
        },
        component: {
            '#': {
                'beforeselect': function (grid, record, index, eOpts) {
                    return false;
                },
                beforerender: 'onBeforeRender',
                checkchange: 'onCheckChange'
            }
        }
    },

    /**
     * @param {Ext.Component} treePanel
     * @param {Object} eOptsi
     */
    onBeforeRender: function (treePanel, eOpts) {
        treePanel.getView().getStore().setSorters([{
            property: 'text',
            direction: 'ASC'
        }]);
    },

    init: function () {
        this.ifcLayers = [];
        this._tmpLayers = [];
        this._tmpOids = [];
    },


    /**
     * This functions expands the tree from selected ifcObject in canvas to the root
     * @param {Object} highlited The higlited object in the canvas
     */
    onHighlitedIfcObject: function (highlited) {
        var objectId = highlited.oid

        var storeRoot = this.getView().getStore().getRoot();
        var node = this.recursiveFound(storeRoot, 'oid', objectId);

        this.getView().getSelectionModel().select(node, false, true);

        var tmpNode = node;
        while (tmpNode != null) {
            tmpNode.expand();
            tmpNode = tmpNode.parentNode;
        }
        this.getView().ensureVisible(node.getPath());
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

    onCheckChange: function (node, checked, e, eOpts) {
        var collection = this.getView().getHiddenNodes();
        collection.beginUpdate();
        var objects = [];
        this.ifcOpacityRecursive(collection, node, checked, objects);
        CMDBuildUI.util.bim.Viewer.updateVisibility(objects);
        collection.endUpdate();
    },

    ifcOpacityRecursive: function (collection, record, checked, objects) {
        if (record) {
            var object = record.get('object')

            if (object) {
                switch (checked) {
                    case true:
                        object.trans.mode = 0;
                        record.set('checked', checked)
                        break;
                    case false:
                        object.trans.mode = 2;
                        record.set('checked', checked)
                        break;
                }
                objects.push(object);
            }
            if (Ext.isArray(record.childNodes)) {
                Ext.Array.forEach(record.childNodes, function (child, index, array) {
                    this.ifcOpacityRecursive(collection, child, checked, objects);
                }, this);
            }
        }
    }
});
