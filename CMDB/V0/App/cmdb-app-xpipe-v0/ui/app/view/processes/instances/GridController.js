Ext.define('CMDBuildUI.view.processes.instances.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.processes-instances-grid',

    listen: {
        global: {
            processinstanceaborted: 'onProcessInstanceAborted',
            processinstancecreated: 'onProcessInstanceCreated',
            processinstanceupdated: 'onProcessInstanceUpdated',
            processinstanceresume: 'onRefreshBtnClick',
            objectidchanged: 'onObjectIdChanged'
        }
    },

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            rowdblclick: 'onRowDblClick',
            selectionchange: 'onSelectionChange',
            selectedidchanged: 'onSelectedIdChanged',
            itemcontextmenu: 'onItemContextMenu'
        },
        '#addbtn': {
            beforerender: 'onAddBtnBeforeRender'
        },
        '#statuscombo': {
            beforerender: 'onStatusComboBeforeRender',
            cleartrigger: 'onStatusComboClear'
        },
        '#refreshBtn': {
            click: 'onRefreshBtnClick'
        },
        '#printPdfBtn': {
            click: 'onPrintBtnClick'
        },
        '#printCsvBtn': {
            click: 'onPrintBtnClick'
        },
        '#contextMenuBtn': {
            beforerender: 'onContextMenuBtnBeforeRender'
        },
        '#savePreferencesBtn': {
            click: 'onSavePreferencesBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.Grid} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view) {
        var vm = this.getViewModel(),
            objectTypeName = vm.get("objectTypeName");

        CMDBuildUI.util.helper.GridHelper.getColumnsForType(
            vm.get("objectType"),
            objectTypeName,
            {
                allowFilter: view.getAllowFilter()
            }
        ).then(function (columns) {
            vm.set("isModelLoaded", true);
            columns.push(CMDBuildUI.util.helper.GridHelper.getProcessFlowStatusColumn());

            // hide selection column
            if (!view.isMultiSelectionEnabled() && view.selModel.column) {
                view.selModel.column.hide();
            }

            // reconfigure columns
            view.reconfigure(null, columns);

            CMDBuildUI.util.helper.GridHelper.setIconGridPreferences(view);
        });

    },

    /**
     * @param {Ext.selection.RowModel} element
     * @param {CMDBuildUI.model.classes.Card[]} record
     * @param {HTMLElement} rowIndex
     * @param {Event} e
     * @param {Object} eOpts
     */
    onRowDblClick: function (element, record, rowIndex, e, eOpts) {
        if (record.get("_tasklist") && record.get("_tasklist").length === 1) {
            var url = CMDBuildUI.util.Navigation.getProcessBaseUrl(
                record.getRecordType(),
                record.getRecordId(),
                record.get("_tasklist")[0]._id,
                'edit'
            );

            this.redirectTo(url, true);
        }
        return false;
    },

    /**
     * @param {Ext.selection.RowModel} selection
     * @param {CMDBuildUI.model.classes.Card[]} selected
     * @param {Object} eOpts
     */
    onSelectionChange: function (selection, selected, eOpts) {
        var vm = this.getViewModel();

        if (this.getView().isMainGrid()) {

            var selId = selected.length ? selected[0].getId() : null;

            //generates the right path even if ther's no selection
            // var path = this.getRouteUrl(vm.get("objectTypeName"), selId);

            var path = CMDBuildUI.util.Navigation.getProcessBaseUrl(this.getViewModel().get("objectTypeName"), selId)

            //changes the path only if managementDetailWindow is closed
            if (!CMDBuildUI.util.Navigation.getManagementDetailsWindow(false)) {
                Ext.util.History.add(path);
            }
        }
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.Grid} view
     * @param {Numeric|String} newid
     * @param {Numeric|String} oldid
     */
    onSelectedIdChanged: function (view, newid, oldid) {
        var vm = this.getViewModel();
        var me = this;

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

        // bind cards store to open selected card
        vm.bind({
            bindTo: '{instances}'
        }, function (store) {
            if (store && !store.isLoaded()) {
                this.loadWithPosition(store, newid);
            }
        }, this);
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

        //only if the store has made the first load can continue
        if (store.isLoaded()) {
            store.load();
        }
    },

    /**
     * 
     * @param {Number} instanceId  the id of the instance
     */
    onObjectIdChanged: function (instanceId) {
        var view = this.getView();
        var vm = this.getViewModel();

        //get instances store
        var instances = vm.get('instances');

        //get plugin
        var plugin = view.getPlugin('forminrowwidget');

        if (instanceId) {
            this.showHandler.call(this, instanceId, instances, plugin, view);
        }
    },

    /**
     * @param {Number} newId
     * @param {Est.data.Store} instances  the instances store
     * @param {*} plugin the forminrowwidget
     * @param {*} view 
     */
    showHandler: function (newId, instances, plugin, view, eOpts) {

        var expandedRecords = plugin.recordsExpanded;

        //get the index in the store of the passed id
        var index = newId ? instances.find('_id', newId) : null;

        //get the record from the index
        var record = index != null ? instances.getAt(index) : null;

        switch (true) {

            //deselects all and collapse all the expanded rows
            case index == null:
                view.getSelectionModel().deselectAll()

                for (var internalId in expandedRecords) {
                    var record = expandedRecords[internalId];
                    var index = instances.indexOf(record);

                    plugin.toggleRow(index, record);
                }
                break;

            //The record is not found, have to load it first
            case index == -1:
                this.loadWithPosition(instances, newId);
                break;

            //the record is found but not expanded
            case index >= 0 && !expandedRecords[record.internalId]:

                view.ensureVisible(record);
                plugin.toggleRow(index, record);
                break;
        } //if the record is found and it is expanded, no operation is made
    },

    /**
     * Filter grid items.
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchSubmit: function (field, trigger, eOpts) {
        var vm = this.getViewModel();
        // get value
        var searchTerm = vm.get("search").value;
        if (searchTerm) {
            var store = vm.get("instances");
            if (store) {
                CMDBuildUI.util.Ajax.setActionId("proc.inst.search");
                // add filter
                store.getAdvancedFilter().addQueryFilter(searchTerm);
                store.load();
            }
        } else {
            this.onSearchClear(field);
        }
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchClear: function (field, trigger, eOpts) {
        var vm = this.getViewModel();
        var store = vm.get("instances");
        if (store) {
            CMDBuildUI.util.Ajax.setActionId("proc.inst.clearsearch");
            // clear store filter
            store.getAdvancedFilter().clearQueryFilter();
            store.load();
            // reset input
            field.reset();
        }
    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onSearchSpecialKey: function (field, event) {
        if (event.getKey() == event.ENTER) {
            this.onSearchSubmit(field);
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onAddBtnBeforeRender: function (button, eOpts) {
        var me = this;
        var vm = button.lookupViewModel();
        this.getView().updateAddButton(
            button,
            function (item, event, eOpts) {
                me.onAddBtnClick(item, event, eOpts);
            },
            vm.get("objectTypeName"),
            vm.get("objectType")
        );
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onAddBtnClick: function (button, eOpts) {
        CMDBuildUI.util.Ajax.setActionId("proc.inst.add");
        // var url = 'processes/' + button.objectTypeName + '/instances/new';
        var url = CMDBuildUI.util.Navigation.getProcessBaseUrl(
            button.objectTypeName,
            null,
            null,
            'new'
        );

        this.redirectTo(url, true);
    },

    /**
     * Update grid on instance creation.
     * 
     * @param {CMDBuildUI.model.processes.Instance} record
     */
    onProcessInstanceCreated: function (record, forcereload) {
        var me = this;
        var view = this.getView();
        var store = view.getStore();

        if (!record) {

            if (forcereload) {
                store.load();
            }
            return;
        }

        var newid = record.getId();
        view.lookupViewModel().set("selectedId", record.getId());
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
                    if (CMDBuildUI.util.Ajax.getActionId() !== CMDBuildUI.util.Ajax.processStatAbort) {
                        me.afterLoadWithPosition(store, newid);
                    }
                },
                scope: this,
                single: true
            }
        });
        // load store
        store.load();
    },

    /**
     * @param {CMDBuildUI.model.processes.Instance} instance
     */
    onProcessInstanceUpdated: function (instance) {
        if (instance) {
            this.getViewModel().set("selectedId", instance.getId());
            this.getView().updateRowWithExpader(instance);
        } else {
            this.onProcessInstanceAborted();
        }
    },

    /**
     * On process instance aborted
     */
    onProcessInstanceAborted: function (record) {
        var grid = this.getView();
        if (grid.getPlugin("forminrowwidget") && record) {
            grid.getPlugin("forminrowwidget").view.fireEventArgs('itemremoved', [grid, record, this]);
        } else {
            var store = grid.getStore();
            store.load();
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.processes.instances.Grid} combo 
     * @param {Ext.event.Event} event 
     * @param {Object} eOpts 
     */
    onStatusComboBeforeRender: function (combo, event, eOpts) {
        var me = this,
            vm = combo.lookupViewModel();
        vm.bind({
            bindTo: {
                store: '{instances}',
                value: '{statuscombo.value}',
                field: '{statuscombo.field}'
            }
        }, function (data) {
            if (data.store) {
                var view = me.getView(),
                    attr, value, baseFilter;
                // set status filter
                if (data.value && data.value !== "__ALL__") {
                    attr = data.field;
                    value = data.value;
                } else if (data.value !== "__ALL__") {
                    var record = view.getOpenRunningStatusValue();
                    if (record) {
                        attr = CMDBuildUI.model.processes.Process.flowstatus.field;
                        value = record.getId();
                    }
                }

                // calculate filter
                if (view.getFilter() && attr && value) {
                    // merge base filter (used for views) with status filter
                    var advancedFilter = new CMDBuildUI.util.AdvancedFilter(view.getFilter());
                    advancedFilter.addAttributeFilter(attr, 'equal', value);
                    baseFilter = advancedFilter.encode();
                } else if (view.getFilter()) {
                    // use base filter 
                    baseFilter = view.getFilter();
                } else if (attr && value) {
                    // use status filter
                    baseFilter = {
                        attribute: {
                            simple: {
                                attribute: attr,
                                operator: 'equal',
                                value: [value]
                            }
                        }
                    };
                }
                if (baseFilter) {
                    data.store.getAdvancedFilter().addBaseFilter(baseFilter);
                } else {
                    data.store.getAdvancedFilter().clearBaseFilter();
                }
                if (!vm.get("defaultfilter") || data.store.isLoaded()) {
                    data.store.load();
                }
            }
        });
    },

    /**
     * On status combo clear trigger
     * @param {Ext.form.field.ComboBox} combo 
     * @param {Ext.form.trigger.Trigger} trigger 
     * @param {Object} eOpts 
     */
    onStatusComboClear: function (combo, trigger, eOpts) {
        combo.setValue(null);
    },

    /**
     * 
     * @param {Ext.button.Button} button 
     * @param {Event} event 
     * @param {Object} eOpts 
     */
    onRefreshBtnClick: function (button, event, eOpts) {
        var view = this.getView();
        view.lookupViewModel().get("instances").load();
        view.setSelection();
    },

    /**
     * 
     * @param {Ext.menu.Item} menuitem 
     * @param {Ext.event.Event} event 
     * @param {Object} eOpts 
     */
    onPrintBtnClick: function (menuitem, event, eOpts) {
        var format = menuitem.printformat;
        var store = this.getViewModel().get("instances");
        var queryparams = {};

        // url and format
        var url = CMDBuildUI.util.api.Classes.getPrintCardsUrl(this.getViewModel().get("objectTypeName"), format);
        queryparams.extension = format;

        // visibile columns
        var columns = this.getView().getVisibleColumns();
        var attributes = [];
        columns.forEach(function (c) {
            if (c.dataIndex) {
                attributes.push(c.dataIndex);
            }
        });
        queryparams.attributes = Ext.JSON.encode(attributes);

        // apply sorters
        var sorters = store.getSorters().getRange();
        if (sorters.length) {
            queryparams.sort = store.getProxy().encodeSorters(sorters);
        }

        // filters
        var filter = store.getAdvancedFilter();
        if (!(filter.isEmpty() && filter.isBaseFilterEmpty())) {
            queryparams.filter = filter.encode();
        }

        // open file in popup
        CMDBuildUI.util.Utilities.openPrintPopup(url + "?" + Ext.Object.toQueryString(queryparams), format);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onContextMenuBtnBeforeRender: function (button, eOpts) {
        this.getView().initContextMenu(button);
    },

    /**
     * 
     * @param {Ext.panel.Tool} tool 
     * @param {Event} event 
     */
    onSavePreferencesBtnClick: function (tool, event) {
        var view = this.getView();
        var vm = view.lookupViewModel();
        CMDBuildUI.util.helper.GridHelper.saveGridPreferences(view, tool, vm.get("objectType"), vm.get("objectTypeName"));
    },

    onItemContextMenu: function (grid, record, item, index, e, eOpts) {
        var me = this,
            vm = grid.lookupViewModel(),
            objectTypeName = record.get("_type"),
            objectType = vm.get('objectType'),
            process = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName, objectType),
            activity = record.get('_tasklist')[0],
            position = e.getXY(),
            items = [{
                text: CMDBuildUI.locales.Locales.processes.openactivity,
                iconCls: 'x-fa fa-external-link',
                bind: {
                    hidden: '{hiddentools.open}'
                },
                handler: function () {
                    CMDBuildUI.view.processes.instances.Util.doOpenInstance(objectTypeName, record.getId(), activity._id, CMDBuildUI.mixins.DetailsTabPanel.actions.view);
                }
            }, {
                text: CMDBuildUI.locales.Locales.processes.editactivity,
                iconCls: 'x-fa fa-pencil',
                disabled: !activity.writable,
                bind: {
                    hidden: '{hiddentools.edit}'
                },
                handler: function () {
                    CMDBuildUI.view.processes.instances.Util.doEditInstance(objectTypeName, record.getId(), activity._id, CMDBuildUI.mixins.DetailsTabPanel.actions.edit);
                }
            }];

        if (record.get('_tasklist').length > 1) {
            items = [];
        }
        items.push({
            text: CMDBuildUI.locales.Locales.processes.abortprocess,
            iconCls: 'x-fa fa-trash',
            disabled: CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges").admin_access || !process.get("stoppableByUser"),
            bind: {
                hidden: '{hiddentools.delete}'
            },
            handler: function () {
                CMDBuildUI.view.processes.instances.Util.doAbortInstance(objectType, objectTypeName, record);
            }
        });

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
         * @param {String} processName
         * @param {Numeric|String} instanceId
         * @param {String} activityId
         * @param {String} action
         * @return {String}
         */
        getRouteUrl: function (processName, instanceId, action) {
            var path = 'processes/' + processName + '/instances';
            if (instanceId) {
                path += '/' + instanceId;
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

            // function to expand row
            function expandRow() {
                view.expandRowAfterLoadWithPosition(store, newid);
                var extraparams = store.getProxy().getExtraParams();
                delete extraparams.positionOf;
                delete extraparams.positionOf_goToPage;
            }

            // check if item is found with filers
            var metadata = store.getProxy().getReader().metaData;
            if (metadata && metadata.positions[newid] && metadata.positions[newid].found) {
                expandRow();
            } else if (!CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.common.keepfilteronupdatedcard)) {
                var advancedFitler = store.getAdvancedFilter();
                // clear search query
                vm.set("search.value", "");
                advancedFitler.clearQueryFilter();
                // clear attributes and relations filter
                view.lookupReference("filterslauncher").clearFilter(true);
                // remove status filter
                vm.set("statuscombo.value", "__ALL__");

                // add load event listener
                store.on({
                    load: {
                        fn: function () {
                            var meta = store.getProxy().getReader().metaData;
                            if (meta && meta.positions[newid] && meta.positions[newid].found) {
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

                Ext.asap(function () {
                    // show message to user
                    CMDBuildUI.util.Notifier.showInfoMessage(CMDBuildUI.locales.Locales.common.grid.filterremoved);
                    // load store
                    store.load();
                });
            } else {
                this.redirectTo(CMDBuildUI.util.Navigation.getProcessBaseUrl(this.getViewModel().get("objectTypeName"), false));
                CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
            }
        },

        /**
         * @param {CMDBuildUI.view.processes.instances.Grid} grid
         * @param {String} objectType 
         * @param {String} objectTypeName 
         * @param {CMDBuildUI.model.processes.Instance} record 
         * @returns 
         */
        getContextMenus: function (grid, objectType, objectTypeName, record) {
            var modelItem = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName, objectType),
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
                            _grid: grid,
                            abortProcess: function () {
                                this._abortProcess(record);
                            },
                            resumeProcess: function () {
                                this._resumeProcess(record);
                            }
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