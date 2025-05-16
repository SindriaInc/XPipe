Ext.define('CMDBuildUI.view.classes.cards.grid.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.classes-cards-grid-grid',

    listen: {
        global: {
            cardcreated: 'onCardCreated',
            carddeleted: 'onCardDeleted',
            cardupdated: 'onCardUpdated',
            objectidchanged: 'onObjectIdChanged'
        }
    },

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            selectionchange: 'onSelectionChange',
            select: 'onSelect',
            deselect: 'onDeselect',
            reload: 'onReload',
            rowdblclick: 'onRowDblClick',
            show: 'onShow',
            itemcontextmenu: 'onItemContextMenu'
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
        if (selectionModel.selectionMode === "MULTI") {
            this.modifySelection(selected, false);
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
        if (selectionModel.selectionMode === "MULTI") {
            this.modifySelection(deselected, true);
        }
    },

    /**
     * Update grid on card update.
     * 
     * @param {CMDBuildUI.model.classes.Card} record 
     */
    onCardUpdated: function (record) {
        this.getView().updateRowWithExpader(record);
    },

    /**
     * Update grid on card creation.
     * 
     * @param {CMDBuildUI.model.classes.Card} record
     */
    onCardCreated: function (record) {
        var me = this;
        var view = this.getView();
        var store = view.getStore();
        var newid = record.getId();
        // update extra params to get new card position
        var extraparams = store.getProxy().getExtraParams();
        extraparams.positionOf = newid;
        extraparams.positionOf_goToPage = false;
        // add event listener. Use event listener instaed of callback
        // otherwise the load listener used within afterLoadWithPosition
        // is called at first load.
        store.on({
            load: {
                fn: function () {
                    me.afterLoadWithPosition(store, newid);
                },
                scope: this,
                single: true
            }
        });
        // load store
        store.load();
    },

    /**
     * Update grid of card deletion.
     */
    onCardDeleted: function (record) {
        var grid = this.getView();
        if (grid.getPlugin("forminrowwidget") && grid.getViewModel().get("activeView") !== "map") {
            grid.getPlugin("forminrowwidget").view.fireEventArgs('itemremoved', [grid, record, this]);
        } else {
            var store = grid.getStore();
            store.load();
        }
    },

    onReload: function (record, action, eOpts) {
        var currentPage;
        var view = this.getView();
        var store = view.getStore();
        var selection = view.getSelection();
        var proxy = store.getProxy();

        if (action === 'edit' || action === 'delete') {

            currentPage = Math.ceil(view.getSelectionModel().getSelection()[0].internalId / store.getConfig().pageSize);
            var selection = view.getSelectionModel().getSelection()[0];
            var index = view.store.indexOf(selection);
            view.getView().deselect(selection);


            view.suspendLayouts();
            store.load({
                params: {
                    limit: store.getConfig().pageSize,
                    page: 1,
                    start: 0
                },
                callback: function (records, operation, success) {

                    view.getView().refresh();
                    view.resumeLayouts();
                },
                scope: this
            });


        } else if (action === 'add') {
            view.suspendLayouts();
            view.getStore().loadPage(1, {
                callback: function (r, o) {
                    view.getView().refresh();


                    view.resumeLayouts();
                }
            });
        }
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.Grid} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view) {
        var vm = this.getViewModel();
        var objectTypeName = view.getObjectTypeName();
        if (!objectTypeName) {
            objectTypeName = vm.get("objectTypeName");
        }

        // get grid columns
        CMDBuildUI.util.helper.GridHelper.getColumnsForType(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
            objectTypeName,
            {
                allowFilter: view.getAllowFilter(),
                addTypeColumn: CMDBuildUI.util.helper.ModelHelper.getClassFromName(objectTypeName).get("prototype")
            }
        ).then(function (columns) {
            view.reconfigure(null, columns);
            // hide selection column
            if (!view.isMultiSelectionEnabled() && view.selModel.column) {
                view.selModel.column.hide();
            }
        });

        vm.bind(
            {
                cards: '{cards}',
                newId: '{selectedId}'
            }, function (data) {
                if (data.newId) {
                    this.loadWithPosition(data.cards, data.newId);
                }
            },
            this, //bind scope
            { //bind options
                single: true
            });

        //TODO: view the correctness of commenting this line of code
        // //sets the selectedId for the first time
        // var selectedId = CMDBuildUI.util.Navigation.getCurrentContext().objectId;
        // vm.set('selectedId', selectedId);
    },

    /**
     * fired by it's view Model
     * @param {Object} selected 
     * {
     *  type: { String }
     *  id: { String }
     *  conf: {
     *      center: true || false,
     *      zoom: true || false
     *  
     *      }
     *  }
     * @param {Ext.data.Model} records the records rapresenting the geovalues of the selected card
     */
    onSelectedChange: function (selected) {
        var view = this.getView();
        var store = view.getStore();

        if (view.isVisible()) {
            if (selected.conf.toogleRow != false) {
                this.getView().expandRowAfterLoadWithPosition(store, selected.id, {
                    suppressSelectEvent: true
                });
            }

        } else {
            var selectedId = selected.id;
            // var record = store.findRecord(selected.id);
            this._ensureNodeVisible = selectedId;
        }
        this.getViewModel().set('selectedId', selected.id);
    },

    /**
     * 
     * @param {*} grid 
     * @param {*} eOpts 
     */
    onShow: function (grid, eOpts) {
        if (this._ensureNodeVisible) {

            /**
             * This is an auxiliar function
             */
            function selectRecord() {
                var recordIndex = store.find('id', this._ensureNodeVisible);
                if (recordIndex !== -1) {
                    this.getView().expandSelection(recordIndex, store, this._ensureNodeVisible);
                    delete this._ensureNodeVisible;
                    return true;
                }
                return false;
            }

            //defines the store used in this function
            var store = grid.getStore();
            var me = this;
            //if the record is not loaded
            if (!selectRecord.call(this)) {
                Ext.asap(function () {
                    // loads the record from the server
                    var extraparams = store.getProxy().getExtraParams();
                    extraparams.positionOf = me._ensureNodeVisible;
                    extraparams.positionOf_goToPage = false;
                    extraparams.limit = store.getConfig().pageSize
                    store.load({
                        callback: function () {

                            //expands the loaded record from the store
                            grid.expandRowAfterLoadWithPosition(grid.getStore(), me._ensureNodeVisible)
                            delete me._ensureNodeVisible;
                        },
                        scope: this
                    });
                });
            }
        }
    },

    /**
     * @param {Ext.selection.RowModel} element
     * @param {CMDBuildUI.model.classes.Card} record
     * @param {HTMLElement} rowIndex
     * @param {Event} e
     * @param {Object} eOpts
     */
    onRowDblClick: function (element, record, rowIndex, e, eOpts) {
        var url = CMDBuildUI.util.Navigation.getClassBaseUrl(record.getRecordType(), record.getRecordId(), 'edit');
        this.redirectTo(url, true);
        return false;
    },

    /**
     * @param {Ext.selection.RowModel} selection
     * @param {CMDBuildUI.model.classes.Card[]} selected
     * @param {Object} eOpts
     */
    onSelectionChange: function (selection, selected, eOpts) {
        var view = this.getView();
        if (view.isMainGrid() && !view.isMultiSelectionEnabled()) {
            var selid;
            if (selected.length) {
                selid = selected[0].getId();
            }

            //TODO: make more test due this modification
            // var path = this.getRouteUrl(this.getViewModel().get("objectTypeName"), selid);
            var path = CMDBuildUI.util.Navigation.getClassBaseUrl(this.getViewModel().get("objectTypeName"), selid)

            //changes the path only if managementDetailWindow is closed
            if (!CMDBuildUI.util.Navigation.getManagementDetailsWindow(false)) {
                Ext.util.History.add(path);
                this.getViewModel().getParent().set('selectedId', selid);
            }
        }
    },

    /**
     * 
     * @param {*} newid 
     */
    loadWithPosition: function (store, newid) {
        /**
         * 
         * @param {CMDBuildUI.store.classes.Cards} store 
         * @param {Ext.data.operation.Read} operation 
         * @param {Object} eOpts 
         */
        function onFirstBeforeLoad(store, operation, eOpts) {
            var extraparams = store.getProxy().getExtraParams();
            extraparams.positionOf = newid;
            extraparams.positionOf_goToPage = false;
        }

        store.on({
            beforeload: {
                fn: onFirstBeforeLoad,
                scope: this,
                single: true
            },
            load: {
                fn: function (store, records, successful, operation, eOpts) {
                    this.afterLoadWithPosition(store, newid);
                },
                scope: this,
                single: true
            }
        });

        if (store.isLoaded()) {
            store.load();
        }
    },

    /**
     * 
     * @param {} newId 
     */
    onObjectIdChanged: function (newId) {
        var view = this.getView();
        var vm = this.getViewModel();

        var cards = vm.get('cards');
        var plugin = view.getPlugin('forminrowwidget');
        var gridVisible = view.isVisible();

        if (gridVisible && cards) {
            //execute immediatly the function
            this.showHandler(newId, cards, plugin, view);
        } else {

            if (!gridVisible) {

                //executes when the component is displayed due to grid expansion bug
                view.removeListener('show', this.showHandler, this);

                view.addListener('show', this.showHandler, this, {
                    single: true,
                    args: [newId, cards, plugin]
                })
            } else if (!cards) {
                vm.bind('cards', function (cards) {
                    this.onObjectIdChanged(newId);
                }, this, {
                    single: true
                })
            }
        }
    },

    /**
     * @param {Number} newId
     * @param {Est.data.Store} cards  the cards store
     * @param {*} index the index of the record to show
     * @param {*} plugin the forminrowwidget
     * @param {*} view 
     * @param {*} eOpts 
     */
    showHandler: function (newId, cards, plugin, view, eOpts) {
        if (!cards) {
            cards = this.getViewModel().get('cards');
        }

        var expandedRecords = plugin.recordsExpanded;
        var index = newId ? cards.find('_id', newId) : null;
        var record = index != null ? cards.getAt(index) : null;

        switch (true) {
            //deselects all and collapse all the expanded rows
            case index == null:
                view.getSelectionModel().deselectAll()

                for (var internalId in expandedRecords) {
                    var record = expandedRecords[internalId];
                    var index = cards.indexOf(record);

                    plugin.toggleRow(index, record);
                }
                break;

            //The record is not found, have to load it first
            case index == -1:
                this.loadWithPosition(cards, newId);
                break;

            //the record is found but not expanded
            case index >= 0 && !expandedRecords[record.internalId]:
                view.getView().refresh(); //https://stackoverflow.com/questions/43898410/rendered-block-refreshed-at-16-rows-while-bufferedrenderer-view-size-is-46/#50376960
                view.ensureVisible(record);
                plugin.toggleRow(index, record);
                break;
        }

    },

    onItemContextMenu: function (grid, record, item, index, e, eOpts) {
        var me = this,
            vm = grid.lookupViewModel(),
            objectTypeName = record.get("_type"),
            objectType = vm.get('objectType'),
            theObject = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName, objectType),
            position = e.getXY(),
            items = [{
                text: CMDBuildUI.locales.Locales.classes.cards.opencard,
                iconCls: 'x-fa fa-external-link',
                disabled: !theObject.get('_can_read'),
                bind: {
                    hidden: '{hiddentools.open}'
                },
                handler: function () {
                    CMDBuildUI.view.classes.cards.Util.doOpenCard(objectTypeName, record.getId(), CMDBuildUI.mixins.DetailsTabPanel.actions.view);
                }
            }, {
                text: CMDBuildUI.locales.Locales.classes.cards.modifycard,
                iconCls: 'x-fa fa-pencil',
                disabled: !theObject.get('_can_update'),
                bind: {
                    hidden: '{hiddentools.edit}'
                },
                handler: function () {
                    CMDBuildUI.view.classes.cards.Util.doEditCard(objectTypeName, record.getId(), CMDBuildUI.mixins.DetailsTabPanel.actions.edit);
                }
            }, {
                text: CMDBuildUI.locales.Locales.classes.cards.deletecard,
                iconCls: 'x-fa fa-trash',
                disabled: !theObject.get('_can_delete'),
                bind: {
                    hidden: '{hiddentools.delete}'
                },
                handler: function () {
                    CMDBuildUI.view.classes.cards.Util.doDeleteCard(objectType, objectTypeName, record);
                }
            }, {
                text: CMDBuildUI.locales.Locales.classes.cards.clone,
                iconCls: 'x-fa fa-clone',
                disabled: !theObject.get('_can_clone'),
                bind: {
                    hidden: '{hiddentools.clone}'
                },
                handler: function () {
                    CMDBuildUI.view.classes.cards.Util.doCloneCard(objectTypeName, record.getId());
                }
            }];
        var contextMenus = me.getContextMenus(grid, objectType, objectTypeName, record);
        if (contextMenus && contextMenus.length) {
            items.push({
                xtype: 'menuseparator'
            });
            items = Ext.Array.merge([], items, contextMenus);
        }
        var menu_grid = new Ext.menu.Menu({
            items: items
        });

        e.stopEvent();
        menu_grid.showAt(position);
        return false;
    },

    privates: {
        /**
         * @param {String} className
         * @param {Numeric} cardId
         * @param {String} action
         * @return {String}
         */
        getRouteUrl: function (className, cardId, action) {
            var path = 'classes/' + className + '/cards';
            if (cardId) {
                path += '/' + cardId;
            }
            if (action) {
                path += '/' + action;
            }
            return path;
        },

        /**
         * 
         * @param {Ext.data.Store} store 
         * @param {Numeric} newid 
         */
        afterLoadWithPosition: function (store, newid) {
            var view = this.getView();
            var vm = view.lookupViewModel();

            if (!view.isVisible()) {
                this._ensureNodeVisible = newid;
            } else {
                // function to expand row
                function expandRow() {
                    view.expandRowAfterLoadWithPosition(store, newid);
                    var extraparams = store.getProxy().getExtraParams();
                    delete extraparams.positionOf;
                    delete extraparams.positionOf_goToPage;
                }

                // check if item is found with filers
                var metadata = store.getProxy().getReader().metaData;
                if (metadata.positions[newid] && metadata.positions[newid].found) {
                    expandRow();
                } else if (
                    !CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.common.keepfilteronupdatedcard) &&
                    !store.getAdvancedFilter().isEmpty()
                ) {
                    var advancedFitler = store.getAdvancedFilter();
                    // clear search query
                    vm.set("search.value", "");
                    advancedFitler.clearQueryFilter();
                    // clear attributes and relations filter
                    var filterslauncher = view.up().lookupReference("filterslauncher");
                    if (filterslauncher) {
                        filterslauncher.clearFilter(true);
                    }
                    // show message to user
                    Ext.asap(function () {
                        CMDBuildUI.util.Notifier.showInfoMessage(CMDBuildUI.locales.Locales.common.grid.filterremoved);
                    });
                    // load store
                    store.on({
                        load: {
                            fn: function () {
                                var meta = store.getProxy().getReader().metaData;
                                if (meta.positions[newid] && meta.positions[newid].found) {
                                    expandRow();
                                } else {
                                    // show not found message to user
                                    Ext.asap(function () {
                                        CMDBuildUI.util.Notifier.showWarningMessage(CMDBuildUI.locales.Locales.common.grid.itemnotfound);
                                    });
                                }
                            },
                            scope: this,
                            single: true
                        }
                    });
                    store.load();
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
        },

        /**
         * Select or deselect the record in list and navigation tree tab based on the value of deselected
         * @param {Ext.data.Model} record 
         * @param {Boolean} deselected 
         */
        modifySelection: function (record, deselected) {
            var mapContainer = this.getView().getGridContainer().getMapContainer(),
                id = Ext.num(record.getId());

            if (mapContainer) {
                var listTab = mapContainer.getListTab(),
                    listStore = listTab.getStore(),
                    selectionModelList = listTab.getSelectionModel(),

                    navTreeTab = mapContainer.getNavigationTreeTab(),
                    navTreeStore = navTreeTab.getStore(),
                    selectionModelNavTree = navTreeTab.getSelectionModel(),

                    recordList = listStore.findRecord("_id", id),
                    recordNavTree = navTreeStore.findRecord("_id", id);

                if (deselected) {
                    selectionModelList.deselect(recordList);
                    selectionModelNavTree.deselect(recordNavTree, true);
                } else {
                    selectionModelList.select(recordList, true);
                    selectionModelNavTree.select(recordNavTree, true, true);
                }
            }
        },

        /**
         * @param {CMDBuildUI.view.classes.cards.grid.Grid} grid
         * @param {String} objectType 
         * @param {String} objectTypeName 
         * @param {CMDBuildUI.model.classes.Card} record 
         * @returns 
         */
        getContextMenus: function (grid, objectType, objectTypeName, record) {
            var modelItem = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                objectTypeName,
                objectType
            ),
                menu = [],
                menuitems = modelItem.contextMenuItems().getRange(),
                supportedTypes = [
                    CMDBuildUI.model.ContextMenuItem.types.custom,
                    CMDBuildUI.model.ContextMenuItem.types.component
                ];
            menuitems.forEach(function (item) {
                if (item.get("active") && supportedTypes.indexOf(item.get('type')) > -1) {
                    var bind;
                    switch (item.get("visibility")) {
                        case CMDBuildUI.model.ContextMenuItem.visibilities.one:
                            bind = {
                                disabled: '{contextmenu.disabledvone}'
                            };
                            break;
                        case CMDBuildUI.model.ContextMenuItem.visibilities.many:
                            bind = {
                                disabled: '{contextmenu.disabledvmany}'
                            };
                            break;
                        default:
                            return;
                    }

                    var executeContextMenuScript;
                    if (item.get("type") === CMDBuildUI.model.ContextMenuItem.types.custom) {
                        /* jshint ignore:start */
                        var jsfn = Ext.String.format(
                            'executeContextMenuScript = function(records, api) {\n{0}\n}',
                            item.get("script")
                        );
                        try {
                            eval(jsfn);
                        } catch (e) {
                            CMDBuildUI.util.Logger.log(
                                "Error on context menu function.",
                                CMDBuildUI.util.Logger.levels.error,
                                null,
                                e
                            );
                            executeContextMenuScript = Ext.emptyFn;
                        }
                        /* jshint ignore:end */
                    }

                    var handler = function () {
                        var api = Ext.apply({
                            _grid: grid
                        }, CMDBuildUI.util.api.Client.getApiForContextMenu());
                        switch (item.get("type")) {
                            case CMDBuildUI.model.ContextMenuItem.types.custom:
                                try {
                                    executeContextMenuScript([record], api);
                                } catch (e) {
                                    CMDBuildUI.util.Logger.log(
                                        "Error on context menu script.",
                                        CMDBuildUI.util.Logger.levels.error,
                                        null,
                                        e
                                    );
                                }
                                break;
                            case CMDBuildUI.model.ContextMenuItem.types.component:
                                CMDBuildUI.util.helper.ContextMenuHelper.openCustomComponent(item, [record], grid);
                                break;
                        }
                    };

                    menu.push({
                        iconCls: 'x-fa fa-angle-double-right',
                        text: item.get("_label_translation") || item.get("label"),
                        handler: handler,
                        bind: bind
                    });
                }
            });

            return menu;
        }
    }
});