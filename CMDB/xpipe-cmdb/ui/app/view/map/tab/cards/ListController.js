Ext.define('CMDBuildUI.view.map.tab.cards.ListController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.map-tab-cards-list',

    listen: {
        component: {
            '#': {
                beforerender: 'onBeforeRender',
                afterrender: 'onAfterRender',
                selectionchange: 'onSelectionChange',
                beforedeselect: 'onBeforeDeselect',
                deselect: 'onDeselect',
                select: 'onSelect'
            }
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

    /**
     * 
     * @param {Ext.selection.RowModel} selectionmodel 
     * @param {Ext.data.Model} record 
     * @param {Number} index 
     * @param {Object} eOpts 
     * @returns false will not deselect the record and not firing 'onDeselect' event
     * returns true will deselect the record and firing 'onDeselect' event
     */
    onBeforeDeselect: function (selectionmodel, record, index, eOpts) {
        return this.getViewModel().get("settingsMap.selectAll") ? false : true;
    },

    /**
      * @param {CMDBuildUI.view.classes.cards.Grid} view
      * @param {Object} eOpts
      */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();

        //configure columns
        vm.bind({
            objectType: '{map-tab-tabpanel.objectType}',
            objectTypeName: '{map-tab-tabpanel.objectTypeName}'
        }, function (data) {
            if (data.objectType && data.objectTypeName) {
                CMDBuildUI.util.helper.ModelHelper.getModel(data.objectType, data.objectTypeName).then(function (model) {
                    if (!view.destroyed) {
                        view.reconfigure(null, CMDBuildUI.util.helper.GridHelper.getColumns(model.getFields(), {
                            allowFilter: view.getAllowFilter(),
                            reducedGrid: true
                        }));
                    }
                });
            }
        });
    },

    /**
      * @param {CMDBuildUI.view.classes.cards.Grid} view
      * @param {Object} eOpts
      */
    onAfterRender: function (view, eOpts) {
        var me = this,
            vm = this.getViewModel();

        //selects element
        Ext.asap(function () {
            vm.bind({
                store: '{cards}',
                objectId: '{map-tab-cards-list.objectId}',
                storeload: '{storeinfo.loaded}'
            }, function (data) {
                if (data.store && data.storeload) {
                    me.showHandler(data.store, data.objectId, view);
                }
            });
        });
    },

    /**
     * 
     * @param {Ext.selection.Model} selectionModel 
     * @param {Ext.data.Model[]} selected 
     * @param {Object} eOpts 
     */
    onSelectionChange: function (selectionModel, selected, eOpts) {
        if (selectionModel.selectionMode !== "SIMPLE") {
            this.getView().setObjectId(selected && selected[0] ? Ext.num(selected[0].getId()) : null);
        }
    },


    showHandler: function (cards, newId, view) {
        var index = newId ? cards.find('_id', newId) : null,
            record = index != null ? cards.getAt(index) : null,
            selectionModel = view.getSelectionModel();

        switch (true) {
            //deselects all
            case index == null:
                selectionModel.deselectAll();
                break;

            //The record is not found, have to load it first
            case index == -1:
                this.loadWithPosition(cards, newId, view);
                break;

            //the record is found, make it visible
            case index >= 0:
                if (selectionModel.selectionMode !== "SIMPLE") {
                    selectionModel.select(record);
                    view.ensureVisible(record);
                }
                break;
        }
    },

    loadWithPosition: function (store, objectId, view) {

        var extraparams = store.getProxy().getExtraParams();
        extraparams.positionOf = objectId;
        extraparams.positionOf_goToPage = false;
        store.load({
            callback: function () {
                this.afterLoadWithPosition.call(this, store, objectId, view);
            },
            scope: this
        });
    },

    afterLoadWithPosition: function (store, objectId, view) {
        // check if item is found with filters
        var metadata = store.getProxy().getReader().metaData;

        function selectItem(positionInfo) {
            if (!positionInfo.pageOffset) {
                view.getSelectionModel().select(positionInfo.positionInPage, false, true); //HACK: avoid propagation 
            } else {
                view.ensureVisible(positionInfo.positionInTable, {
                    select: true
                });
            }
        }

        if (metadata.positions[objectId] && metadata.positions[objectId].found) {
            var posinfo = metadata && metadata.positions && metadata.positions[objectId] || { positionInPage: 0 };
            selectItem(posinfo);
        } else if (
            !CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.common.keepfilteronupdatedcard) &&
            !store.getAdvancedFilter().isEmpty()
        ) {
            var vm = this.getViewModel(),
                advancedFilter = store.getAdvancedFilter();
            // clear search query
            vm.set("search.value", "");
            advancedFilter.clearQueryFilter();
            var filterslauncher = view.getCardView().down("filters-launcher");
            if (filterslauncher) {
                // clear attributes and relations filter
                filterslauncher.clearFilter(true);
            }
            // show message to user
            Ext.asap(function () {
                CMDBuildUI.util.Notifier.showInfoMessage(CMDBuildUI.locales.Locales.common.grid.filterremoved);
            });
            // load store
            store.load({
                callback: function () {
                    var metadata = store.getProxy().getReader().metaData;
                    if (metadata.positions[objectId] && metadata.positions[objectId].found) {
                        var posinfo = metadata && metadata.positions && metadata.positions[objectId] || { positionInPage: 0 };
                        selectItem(posinfo);
                    } else {
                        // show not found message to user
                        Ext.asap(function () {
                            CMDBuildUI.util.Notifier.showWarningMessage(CMDBuildUI.locales.Locales.common.grid.itemnotfound);
                        });
                    }
                }
            });
        } else {
            // show not found message to user
            var extraparams = store.getProxy().getExtraParams();
            delete extraparams.positionOf;
            delete extraparams.positionOf_goToPage;
            Ext.asap(function () {
                CMDBuildUI.util.Notifier.showWarningMessage(CMDBuildUI.locales.Locales.common.grid.itemnotfound);
            });
        }

    }
});
