Ext.define('CMDBuildUI.view.widgets.linkcards.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.widgets-linkcards-panel',

    control: {
        "#": {
            beforerender: "onBeforeRender"
        },
        grid: {
            select: 'onGridSelect',
            deselect: 'onGridDeselect'
        },
        tableview: {
            actionviewobject: "onActionViewObject",
            actioneditobject: "onActionEditObject"
        },
        '#togglefilter': {
            toggle: 'onToggleFilterToggle'
        },
        '#refreshselection': {
            click: 'onRefreshSelectionClick'
        },
        '#closebtn': {
            click: 'onCloseBtnClick'
        },
        '#checkedonly': {
            toggle: 'onCheckedOnlyToggleHandle'
        }
    },

    /**
     * @param {CMDBuildUI.view.widgets.linkcards.Panel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel(),
            widget = vm.get("theWidget"),
            target = vm.get("theTarget"),
            outputAttr = widget.get('_output'),
            typeinfo = CMDBuildUI.view.widgets.linkcards.Panel.getTypeInfo(widget),
            objectTypeName = typeinfo.objectTypeName,
            objectType = typeinfo.objectType,
            isFormReadOnly = view.getFormMode() === CMDBuildUI.util.helper.FormHelper.formmodes.read;

        this.manageStoreBindings();
        // if output var is not declared the grid should be in read mode, without checkbox
        if (Ext.isEmpty(outputAttr)) {
            widget.set('NoSelect', true);
            vm.set("defaultsLoaded", true);
        } else if (!widget.get("_defaultsLoaded") && Ext.isEmpty(target.get(outputAttr))) {
            CMDBuildUI.view.widgets.linkcards.Panel.loadDefaults(widget, target).then(function (records) {
                var defaults = [];
                records.forEach(function (r) {
                    defaults.push({
                        _id: r.getId()
                    });
                });
                widget.set("_defaultsLoaded", true);
                target.set(outputAttr, defaults);
                vm.set("defaultsLoaded", true);
            });
        } else {
            vm.set("defaultsLoaded", true);
        }

        if (objectType) {
            vm.set("objectType", objectType);
            vm.set("objectTypeName", objectTypeName);
        } else {
            Ext.asap(function () {
                CMDBuildUI.util.Notifier.showErrorMessage(Ext.String.format(CMDBuildUI.locales.Locales.errors.classnotfound, objectTypeName));
            });
            return;
        }

        // get the model for objtect type name
        CMDBuildUI.util.helper.GridHelper.getColumnsForType(
            objectType,
            objectTypeName, {
            allowFilter: true,
            addTypeColumn: CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName, objectType).get("prototype")
        }).then(function (columns) {
            var modelname = CMDBuildUI.util.helper.ModelHelper.getModelName(objectType, objectTypeName),
                model = CMDBuildUI.util.helper.ModelHelper.getModelFromName(modelname);

            // add view object action columns
            columns.push({
                xtype: 'actioncolumn',
                minWidth: 30,
                maxWidth: 30,
                hideable: false,
                disabled: true,
                align: 'center',
                bind: {
                    disabled: '{disableViewAction}'
                },
                iconCls: 'x-fa fa-external-link',
                tooltip: CMDBuildUI.locales.Locales.widgets.linkcards.opencard,
                handler: function (grid, rowIndex, colIndex) {
                    var record = grid.getStore().getAt(rowIndex);
                    grid.fireEvent("actionviewobject", grid, record, rowIndex, colIndex);
                }
            });

            // add edit object action columns
            if (!isFormReadOnly) {
                columns.push({
                    xtype: 'actioncolumn',
                    minWidth: 30,
                    maxWidth: 30,
                    hideable: false,
                    disabled: true,
                    align: 'center',
                    bind: {
                        disabled: '{disableEditAction}'
                    },
                    iconCls: 'x-fa fa-pencil',
                    tooltip: CMDBuildUI.locales.Locales.widgets.linkcards.editcard,
                    handler: function (grid, rowIndex, colIndex) {
                        var record = grid.getStore().getAt(rowIndex);
                        grid.fireEvent("actioneditobject", grid, record, rowIndex, colIndex);
                    }
                });
            }

            // define selection model
            var selModel = {
                selType: 'checkboxmodel',
                showHeaderCheckbox: false,
                checkOnly: false,
                pruneRemoved: false
            },
                disableSelection = false;

            if (vm.get("theWidget").get("NoSelect") || isFormReadOnly) {
                selModel = null;
                disableSelection = true;
            } else if (vm.get("theWidget").get("SingleSelect")) {
                selModel.mode = "SINGLE";
            }

            // configure grid
            var gridConfig = {
                xtype: 'grid',
                columns: columns,
                forceFit: true,
                loadMask: true,
                itemId: 'grid',
                reference: 'grid',
                selModel: selModel,
                disableSelection: disableSelection,
                bind: {
                    store: '{gridrows}',
                    selection: '{selection}'
                },
                bubbleEvents: [
                    'itemupdated'
                ],
                plugins: [
                    'gridfilters'
                ],
                requires: [
                    'Ext.grid.filters.Filters'
                ]
            };
            // configure grid height if needed
            if (widget.get('_inline')) {
                gridConfig.height = view.up("form").getHeight() * 0.5;
            }
            // add grid
            view.add(gridConfig);
            Ext.asap(function () {
                vm.set("model", model);
            });
        });

    },

    /**
     * @param {*} store
     * @param {*} records
     * @param {*} successful
     * @param {*} operation
     * @param {*} eOpts
     */
    onGridrowsLoad: function (store, records, successful, operation, eOpts) {
        var view = this.getView(),
            vm = view.lookupViewModel(),
            theTarget = vm.get('theTarget');

        if (theTarget && records) {
            var output = theTarget.get(view.getOutput());
            if (!Ext.isEmpty(output)) {
                var grid = view.down("grid");
                if (grid) {
                    var selection = grid.getSelection();

                    records.forEach(function (r) {
                        var isSelected = !!Ext.Array.findBy(output, function (i) {
                            return i._id == r.get("_id");
                        });
                        if (isSelected) {
                            selection.push(r);
                        }
                    });

                    // suspend and resume select event to set selection
                    // to prevent the select event
                    grid.suspendEvent("select");
                    grid.setSelection(selection);
                    grid.resumeEvent("select");
                }
            }
        }
    },

    /**
     *
     * @param {Ext.selection.RowModel} selMod
     * @param {Ext.data.Model} record
     * @param {Number} index
     * @param {Object} eOpts
     */
    onGridSelect: function (selMod, record, index, eOpts) {
        var view = this.getView(),
            vm = this.getViewModel(),
            theTarget = vm.get('theTarget'),
            outputView = view.getOutput(),
            output, isAlreadyInList;

        if (theTarget) {
            if (selMod.mode === "SINGLE") {
                output = [];
                isAlreadyInList = false;
            } else {
                output = theTarget.get(outputView) || [];
                isAlreadyInList = !!Ext.Array.findBy(output, function (i) {
                    return i._id == record.get("_id");
                });
            }
            output = Ext.Array.map(output, function (i) {
                return i;
            });
            if (!isAlreadyInList && !Ext.isEmpty(outputView)) {

                // add record id in the output variable on target
                output.push({
                    _id: record.get("_id")
                });
                theTarget.set(outputView, output);
            }
        }
    },

    /**
     *
     * @param {Ext.selection.RowModel} selMod
     * @param {Ext.data.Model} record
     * @param {Number} index
     * @param {Object} eOpts
     */
    onGridDeselect: function (selMod, record, index, eOpts) {
        var view = this.getView(),
            vm = this.getViewModel(),
            theTarget = vm.get('theTarget'),
            outputView = view.getOutput();

        if (theTarget && !Ext.isEmpty(outputView)) {
            var output = theTarget.get(outputView) || [],
                item = Ext.Array.findBy(output, function (i) {
                    return i._id == record.get("_id");
                });

            output = Ext.Array.map(output, function (i) {
                return i;
            });
            if (item) {
                // remove record id in the output variable on target
                Ext.Array.remove(output, item);
                theTarget.set(outputView, output);
            }
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Boolean} selected
     * @param {Object} eOpts
     */
    onToggleFilterToggle: function (button, selected, eOpts) {
        var store = this.getViewModel().get('gridrows');
        var advancedFilter = store.getAdvancedFilter();
        // get the filter if toggle is not selected
        if (!selected) {
            var vm = this.getViewModel();
            var filter = vm.get("theWidget").get("_Filter_ecql");
            var target = vm.get("theTarget");

            if (filter) {
                // calculate ecql
                var ecql = CMDBuildUI.util.ecql.Resolver.resolve(
                    filter,
                    target
                );
                if (ecql) {
                    advancedFilter.addEcqlFilter(ecql)
                    store.load();
                }
            }
        } else {
            advancedFilter.clearEcqlFitler();
            store.load();
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onRefreshSelectionClick: function (button, e, eOpts) {
        var me = this,
            vm = button.lookupViewModel(),
            view = me.getView(),
            grid = view.down("grid"),
            loader = CMDBuildUI.util.Utilities.addLoadMask(grid);

        if (grid) {
            grid.setSelection();
            CMDBuildUI.view.widgets.linkcards.Panel.loadDefaults(
                vm.get("theWidget"),
                vm.get("theTarget")
            ).then(function (defaultSelections, widget) {
                var defaults = [],
                    theTarget = vm.get('theTarget'),
                    outputView = view.getOutput();

                // add selected on output variable on target
                defaultSelections.forEach(function (r) {
                    defaults.push({
                        _id: r.get("_id")
                    });
                });

                if (theTarget && !Ext.isEmpty(outputView)) {
                    theTarget.set(outputView, defaults);
                }

                CMDBuildUI.util.Utilities.removeLoadMask(loader);

                // if the the checked only button is pressed
                // use the method onCheckedOnlyToggle
                if (view.down("#checkedonly").pressed) {
                    me.onCheckedOnlyToggle(grid, true);
                } else {
                    grid.getStore().load();
                }
            });
        }
    },

    /**
     * @param {CMDBuildUI.view.attachments.Grid} grid
     * @param {Ext.data.Model} record
     * @param {Number} rowIndex
     * @param {Number} colIndex
     */
    onActionViewObject: function (grid, record, rowIndex, colIndex) {
        var title, config;
        var vm = this.getViewModel();
        if (vm.get("objectType") === CMDBuildUI.util.helper.ModelHelper.objecttypes.klass || vm.get("objectType") === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
            title = CMDBuildUI.util.helper.ModelHelper.getObjectDescription(record.get("_type"), vm.get("objectType"));

            var xtype = vm.get("objectType") === CMDBuildUI.util.helper.ModelHelper.objecttypes.klass ? 'classes-cards-card-view' : 'processes-instances-instance-view';
            config = {
                xtype: xtype,
                record: record,
                padding: 10,
                viewModel: {
                    data: {
                        objectTypeName: record.get("_type"),
                        objectId: record.getId(),
                        objectType: vm.get("objectType")
                    }
                },
                shownInPopup: true,
                hideTools: true
            };
        }
        CMDBuildUI.util.Utilities.openPopup(null, title, config);
    },

    /**
     * @param {CMDBuildUI.view.attachments.Grid} grid
     * @param {Ext.data.Model} record
     * @param {Number} rowIndex
     * @param {Number} colIndex
     */
    onActionEditObject: function (grid, record, rowIndex, colIndex) {
        var title, config, popup,
            vm = this.getViewModel();
        if (vm.get("objectType") === CMDBuildUI.util.helper.ModelHelper.objecttypes.klass) {
            title = CMDBuildUI.util.helper.ModelHelper.getClassDescription(record.get("_type"));
            config = {
                xtype: 'classes-cards-card-edit',
                padding: 10,
                viewModel: {
                    data: {
                        objectTypeName: record.get("_type"),
                        objectId: record.getId()
                    }
                },
                redirectAfterSave: false,
                buttons: [{
                    ui: 'management-action',
                    text: CMDBuildUI.locales.Locales.common.actions.save,
                    autoEl: {
                        'data-testid': 'widgets-linkcards-save'
                    },
                    formBind: true,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.common.actions.save'
                    },
                    handler: function (btn, event) {
                        CMDBuildUI.util.helper.FormHelper.startSavingForm();
                        var panel = btn.lookupController(),
                            cancelBtn = this.up().down("#cancelbutton");

                        btn.showSpinner = true;
                        CMDBuildUI.util.Utilities.disableFormButtons([btn, cancelBtn]);

                        // save
                        panel.saveForm().then(function (record) {
                            CMDBuildUI.util.helper.FormHelper.endSavingForm();
                            popup.destroy();
                            grid.getStore().load();
                        }).otherwise(function () {
                            CMDBuildUI.util.helper.FormHelper.endSavingForm();
                            CMDBuildUI.util.Utilities.enableFormButtons([btn, cancelBtn]);
                        });
                    }
                }, {
                    text: CMDBuildUI.locales.Locales.common.actions.cancel,
                    itemId: 'cancelbutton',
                    ui: 'secondary-action-small',
                    autoEl: {
                        'data-testid': 'widgets-linkcards-cancel'
                    },
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
                    },
                    handler: function (btn, event) {
                        popup.destroy();

                    }
                }]
            };
        }
        popup = CMDBuildUI.util.Utilities.openPopup(null, title, config);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCloseBtnClick: function (button, e, eOpts) {
        this.getView().fireEvent("popupclose");
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
        var searchTerm = field.getValue();
        if (searchTerm) {
            var store = vm.get("gridrows");
            if (store) {
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
        var store = vm.get("gridrows");
        if (store) {
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
     * @param {Boolean} pressed
     * @param {Object} eOpts
     */
    onCheckedOnlyToggleHandle: function (button, pressed, eOpts) {
        var view = this.getView(),
            grid = view.down('#grid');

        this.onCheckedOnlyToggle(grid, pressed);
    },

    /**
     * @param {Ext.data.Grid} grid
     * @param {Boolean} pressed
     */
    onCheckedOnlyToggle: function (grid, pressed) {
        var view = this.getView(),

            store = grid.getStore(),
            advancedFilter = store.getAdvancedFilter();

        // clear attribute filter on Id
        advancedFilter.removeAttributeFitler('_id');

        if (pressed) {
            var vm = view.lookupViewModel(),
                theTarget = vm.get('theTarget'),
                ids = [];

            // get ids from output variable on target
            if (theTarget) {
                var output = theTarget.get(view.getOutput()) || [];

                output.forEach(function (i) {
                    ids.push(i._id);
                });
            }

            advancedFilter.addAttributeFilter(
                '_id',
                CMDBuildUI.util.helper.FiltersHelper.operators.in,
                ids
            );
        }
        store.load();
    },

    privates: {
        _selectionTryCount: 0,

        manageStoreBindings: function () {
            var vm = this.getViewModel(),
                filter = vm.get('theWidget._Filter_ecql'),
                target = 'theTarget';

            if (filter) {
                var bindings = CMDBuildUI.util.ecql.Resolver.getViewModelBindings(filter, target);
                bindings = Ext.Object.merge({}, bindings, {
                    gridrows: '{gridrows}'
                });
                vm.bind({
                    bindTo: bindings
                }, function (data) {

                    if (Ext.Object.getValues(data).length) {
                        if (data.gridrows) {

                            // calculate ecql
                            var ecql = CMDBuildUI.util.ecql.Resolver.resolve(
                                filter, vm.get(target)
                            );

                            if (ecql && !vm.get("disablegridfilter")) {
                                vm.set("storeinfo.advancedfilter", {
                                    ecql: ecql
                                });
                            }
                            if (!data.gridrows.destroyed) {
                                data.gridrows.load();
                            }
                        }
                    }
                });
            }
        }
    }
});