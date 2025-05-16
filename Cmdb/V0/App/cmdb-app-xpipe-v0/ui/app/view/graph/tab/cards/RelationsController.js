Ext.define('CMDBuildUI.view.graph.tab.cards.RelationsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.graph-tab-cards-relations',
    listen: {
        store: {
            '#relationStore': {
                clear: 'onRelationStoreClear'
            }
        },
        component: {
            "#": {
                beforeRender: 'onBeforeRender',
                groupexpand: 'onGroupExpand',
                beforegroupcollapse: 'onBeforeGroupCollapse'
            }
        }
    },

    /**
     * This function adds the beforegroupcollapse event
     * @param {Ext.tab.Panel} view
     * @param {Object} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        Ext.grid.feature.Grouping.override({
            collapse: function (groupBd, options) {
                options = options || {};
                Ext.applyIf(options, {
                    skip: {
                        beforegroupcollapse: false
                    }
                });
                if (options.skip.beforegroupcollapse === true) { //skips the firing event of beforegroupcollapse
                    this.callOverridden(arguments);
                } else if (view.fireEvent('beforegroupcollapse', this.view, this.getHeaderNode(groupBd), groupBd) !== false) {
                    this.callOverridden(arguments);
                }
            }
        });

        Ext.grid.feature.Grouping.override({
            expand: function (groupBd, options) {
                options = options || {};
                Ext.applyIf(options, {
                    skip: {
                        beforegroupexpand: false
                    }
                });
                if (options.skip.beforegroupexpand === true) { //skips the firing event of beforegroupexpand
                    this.callOverridden(arguments);
                } else if (view.fireEvent('beforegroupexpand', this.view, this.getHeaderNode(groupBd), groupBd) !== false) {
                    this.callOverridden(arguments);
                }
            }
        });
    },

    /**
     * @param {Ext.view.Table}
     * @param {HTMLElement} node
     * @param {String} group
     */
    onBeforeGroupCollapse: function (view, node, group) {
        return false;
    },

    /**
     * @param {Ext.view.Table}
     * @param {HTMLElement} node
     * @param {String} group
     */
    onGroupExpand: function (view, node, group) {
        var store = this.getStore('edgesRelationStore');
        var storeGroups = store.getGroups().items;

        var nodeGroup = storeGroups.find(function (storeGroup) {
            if (storeGroup.getGroupKey() === group) {
                return storeGroup;
            }
        }, this);

        var compoundId = false;
        var items = nodeGroup.items[0].nodes().getRange();

        var map = this.getViewModel().get('_map_Node_CompoundName');

        for (var i = 0; i < items.length && !compoundId; i++) {
            var nodeId = items[i].get('_destinationId'); //could chose each one of the items but we select the one in position 0;
            compoundId = map[nodeId];
        }

        if (compoundId) {
            compoundId = [compoundId];
            Ext.GlobalEvents.fireEventArgs('doubleclicknode', [compoundId]);
        }

    },

    /**
     * @param {Ext.data.Store} relationStore
     * @param {Object} eOpts 
     */
    onRelationStoreClear: function (relationStore, eOpts) {
        var vm = this.getViewModel();
        var store = vm.get('edgesRelationStore');
        store.removeAll();

        this.setTitle();
        this.disableCard();
    },

    /**
     * sets the tab title using the bind
     */
    setTitle: function () {
        var containerViewModel = this.getView().up('graph-tab-tabpanel').getViewModel();
        containerViewModel.set('relationLenghtValue', 0);
    },

    /**
     * disable relation tab
     */
    disableCard: function () {
        var containerViewModel = this.getView().up('graph-tab-tabpanel');
        var relationTab = containerViewModel.lookupReference('graph-tab-cards-relations');

        relationTab.setDisabled(true);
    }
});
