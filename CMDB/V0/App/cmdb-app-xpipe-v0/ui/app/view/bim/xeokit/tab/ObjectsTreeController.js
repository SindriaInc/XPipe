Ext.define('CMDBuildUI.view.bim.xeokit.tab.ObjectsTreeController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.bim-xeokit-tab-objectstree',

    control: {
        '#': {
            checkchange: 'onCheckChange',
            objectselected: 'onObjectSelected'
        }
    },

    /**
     * Fired when checkbox's checked property changes on a node changes and used to set visible or not the entity in canvas
     * @param {Ext.data.TreeModel} node 
     * @param {Boolean} checked 
     * @param {Ext.event.Event} e 
     * @param {Object} eOpts 
     */
    onCheckChange: function (node, checked, e, eOpts) {
        var scene = this.getView().getContainer().getViewer().scene,
            id = node.get("entityId");
        scene.setObjectsVisible(id, checked);
    },

    /**
     * Select on the tree the entity picked in the canvas
     * @param {Object} entity 
     */
    onObjectSelected: function (entity) {
        var view = this.getView(),
            vm = this.getViewModel(),
            record = this.expandTree(view.getStore().getRange()[vm.get("depth")], entity);

        if (record) {
            view.ensureVisible(record, {
                select: true
            });
        }
    },

    privates: {

        /**
         * Search in recursive mode the record to select on the tree
         * @param {Object} elements the root from which to start the search
         * @param {Object} entity 
         * @returns the record represented by the entity
         */
        expandTree: function (elements, entity) {
            var me = this,
                view = this.getView(),
                vm = this.getViewModel();

            if (!entity) {

                if (elements.childNodes.length === 1) {
                    vm.set("depth", vm.get("depth") + 1);
                    me.expandTree(elements.childNodes[0], null);
                }
                view.expandNode(elements);

            } else {
                var record = null;

                Ext.Array.each(elements, function (item, index, allitems) {
                    Ext.Array.each(item.childNodes, function (elem, index, allitems) {
                        if (elem.data.leaf) {
                            if (entity.id === elem.data.entityId) {
                                record = elem;
                                return false;
                            }
                        } else {
                            record = me.expandTree(elem, entity);
                            if (record) {
                                return false;
                            }
                        }
                    });

                    if (record) {
                        view.expandNode(item);
                        return false;
                    }

                });

                return record;
            }
        }
    }

});