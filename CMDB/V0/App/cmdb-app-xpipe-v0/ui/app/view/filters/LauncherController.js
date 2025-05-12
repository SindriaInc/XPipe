Ext.define('CMDBuildUI.view.filters.LauncherController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.filters-launcher',

    control: {
        '#': {
            selectionchange: 'onSelectionChange'
        },
        '#mainbtn': {
            beforerender: 'onMainBtnBeforeRender'
        },
        '#clearfiltertool': {
            click: 'onClearFilterToolClick'
        },
        '#filterdesc': {
            click: 'onOpenMenuClick'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.filters.Launcher} view
     * @param {CMDBuildUI.model.base.Filter|Number} selection
     * @param {CMDBuildUI.model.base.Filter|Number} oldvalue
     * @param {Object} eOpts
     */
    onSelectionChange: function (view, selection, oldvalue, eOpts) {
        var me = this;
        var vm = view.lookupViewModel();
        if (!Ext.isEmpty(selection) && !selection.isFilter) {
            vm.bind({
                item: "{item}",
                mainstore: "{" + view.getStoreName() + "}"
            }, function (data) {
                var store = data.item.filters();

                function findInStore() {
                    var filter = store.getById(selection);
                    if (filter) {
                        me.applyFilter(filter);
                    } else {
                        me.onClearFilterToolClick();
                        CMDBuildUI.util.Logger.log("Filter " + selection + " not found", CMDBuildUI.util.Logger.levels.warn);
                    }
                }
                if (store.isLoaded()) {
                    findInStore();
                } else {
                    store.on({
                        load: {
                            fn: findInStore,
                            single: true
                        }
                    });
                }
            });
        } else if (!Ext.isEmpty(selection) && selection.isFilter) {
            me.applyFilter(selection);
        } else if (!Ext.isEmpty(oldvalue)) {
            view.clearFilter();
        }
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onMainBtnBeforeRender: function (button, eOpts) {
        var vm = this.getViewModel();
        var me = this;

        function initMenu(filters) {
            if (filters && filters.length) {
                button.on("click", me.onOpenMenuClick, me);
            } else {
                button.on("click", me.onAddNewFilterClick, me);
            }
        }

        vm.bind({
            bindTo: {
                objectTypeName: '{objectTypeName}',
                objectType: '{objectType}'
            }
        }, function (data) {
            if (data.objectTypeName && data.objectType) {
                var item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.objectTypeName, data.objectType);
                vm.set("item", item);
                item.getFilters().then(function (filters) {
                    var range;

                    if (me.getView().getIsDms()) {
                        range = filters.query('isOnlyDmsFilter', true);
                    } else {
                        range = filters.query('isOnlyDmsFilter', undefined);
                    }
                    initMenu(range);
                });
            }
        });
    },

    /**
     * Open a popup to create a new filter
     */
    onAddNewFilterClick: function (view) {
        var vm = this.getViewModel();
        var filter = Ext.create('CMDBuildUI.model.base.Filter', {
            name: CMDBuildUI.locales.Locales.filters.newfilter,
            description: CMDBuildUI.locales.Locales.filters.newfilter,
            ownerType: vm.get("objectType") === CMDBuildUI.util.helper.ModelHelper.objecttypes.view ? CMDBuildUI.util.helper.ModelHelper.objecttypes.view : CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
            target: vm.get("objectTypeName") == CMDBuildUI.util.helper.ModelHelper.objecttypes.event ? '_CalendarEvent' : this.getViewModel().get("objectTypeName"),
            isOnlyDmsFilter: this.getView().getIsDms() ? true : undefined,
            configuration: {}
        });
        this.editFilter(filter);
    },

    onOpenMenuClick: function () {
        var view = this.getView();
        if (!view.isMenuExpanded) {

            var chained;
            if (view.getIsDms()) {

                chained = new Ext.data.ChainedStore({
                    source: this.getViewModel().get("item").filters(),
                    filters: [function (item) {
                        return item.get('isOnlyDmsFilter');
                    }]
                });

            } else {

                chained = new Ext.data.ChainedStore({
                    source: this.getViewModel().get("item").filters(),
                    filters: [function (item) {
                        return !item.get('isOnlyDmsFilter');
                    }]
                });

            }
            view.getMenu().lookupViewModel().setStores({
                filters: chained
            });
            view.expandMenu();
        }
    },

    /**
     *
     * @param {CMDBuildUI.model.base.Filter} filter The filter to edit.
     */
    editFilter: function (filter) {
        var me = this;
        var view = this.getView();
        var vm = this.getViewModel();

        var viewmodel = {
            data: {
                objectType: vm.get("objectType"),
                objectTypeName: vm.get("objectTypeName"),
                theFilter: filter
            }
        };

        // popup definition
        var popup = CMDBuildUI.util.Utilities.openPopup(null, filter.get("description"), {
            xtype: 'filters-panel',
            isDms: view.getIsDms(),
            viewModel: viewmodel,
            showAttributesPanel: view.getShowAttributesPanel(),
            showRelationsPanel: view.getShowRelationsPanel(),
            showAttachmentsPanel: view.getShowAttachmentsPanel(),
            listeners: {
                /**
                 *
                 * @param {CMDBuildUI.view.filters.Panel} panel
                 * @param {CMDBuildUI.model.base.Filter} filter
                 * @param {Object} eOpts
                 */
                applyfilter: function (panel, filter, eOpts) {
                    me.onApplyFilter(filter, popup);
                },
                /**
                 *
                 * @param {CMDBuildUI.view.filters.Panel} panel
                 * @param {CMDBuildUI.model.base.Filter} filter
                 * @param {Object} eOpts
                 */
                saveandapplyfilter: function (panel, filter, eOpts) {
                    me.onSaveAndApplyFilter(filter, popup);
                },
                /**
                 * Custom event to close popup directly from popup
                 * @param {Object} eOpts
                 */
                popupclose: function (eOpts) {
                    popup.close();
                }
            }
        });
    },

    /**
     *
     * @param {CMDBuildUI.model.base.Filter} filter The filter to delete.
     */
    deleteFilter: function (filter) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        var vm = this.getViewModel();
        var me = this;
        // clear filter before remove it
        if (this.getViewModel().get("appliedfilter.id") === filter.getId()) {
            this.onClearFilterToolClick();
        }
        // delete filter
        filter.erase();
        var store = vm.get("item").filters();
        // if there are no filters, remove filters menu
        if (!store.getCount()) {
            var view = me.getView();
            // destroy menu
            view.getMenu().destroy();
            delete view.isMenuExpanded;
            delete view.menu;
            // invert button events
            var button = me.lookup("mainbtn");
            button.on("click", me.onAddNewFilterClick, me);
            button.un("click", me.onOpenMenuClick, me);
        }
        CMDBuildUI.util.helper.FormHelper.endSavingForm();
    },

    /**
     *
     * @param {CMDBuildUI.model.base.Filter} filter
     */
    onApplyFilter: function (filter, popup) {
        var me = this;
        me.applyFilter(filter, popup);

        var button = me.lookup("mainbtn");
        button.un("click", me.onAddNewFilterClick, me);
        button.on("click", me.onOpenMenuClick, me);
    },

    /**
     *
     * @param {CMDBuildUI.model.base.Filter} filter
     */
    onSaveAndApplyFilter: function (filter, popup) {
        this.getViewModel().set('savingaction', true);
        this.onApplyFilter(filter, popup);
    },

    /**
     *
     * @param {Ext.panel.Tool} tool
     * @param {Ext.event.Event} e
     * @param {CMDBuildUI.view.filters.Launcher} owner
     * @param {Object} eOpts
     */
    onClearFilterToolClick: function (tool, e, owner, eOpts) {
        this.getView().clearFilter();
    },

    /**
     *
     * @param {Ext.view.Table} grid
     * @param {HTMLElement} td
     * @param {Number} cellIndex
     * @param {CMDBuildUI.model.base.Filter} record
     * @param {HTMLElement} tr
     * @param {Number} rowIndex
     * @param {Ext.event.Event} e
     * @param {Object} eOpts
     */
    onFiltersGridCellClick: function (grid, td, cellIndex, record, tr, rowIndex, e, eOpts) {
        if (cellIndex === 0) {
            this.applyFilter(record);
            this.getView().collapseMenu();
        }
    },

    privates: {
        /**
         *
         * @param {Object[]} runtimeattrs
         * @return {Ext.form.Field[]}
         */
        getFormForRuntimeAttributes: function (runtimeattrs) {
            var fields = [];
            var vm = this.getViewModel();
            var modelName = CMDBuildUI.util.helper.ModelHelper.getModelName(vm.get("objectType"), vm.get("objectTypeName"));
            var model = Ext.ClassManager.get(modelName);
            runtimeattrs.forEach(function (a) {
                var editor, fieldlabel,
                    linkType = false;
                if (a.attribute === "_tenant") {
                    editor = CMDBuildUI.util.helper.FormHelper.getTenantField(CMDBuildUI.util.helper.FormHelper.formmodes.update);
                    fieldlabel = CMDBuildUI.util.Utilities.getTenantLabel();
                } else if (a.attribute === "_activity") {
                    editor = CMDBuildUI.util.helper.FormHelper.getActivityField(CMDBuildUI.util.helper.FormHelper.formmodes.update);
                    fieldlabel = CMDBuildUI.locales.Locales.common.tabs.activity;
                } else {
                    var field = model.getField(a.attribute);
                    editor = CMDBuildUI.util.helper.FormHelper.getEditorForField(
                        field,
                        {
                            ignoreUpdateVisibilityToField: true,
                            ignoreCustomValidator: true,
                            ignoreAutovalue: true
                        }
                    );
                    fieldlabel = field.attributeconf.description_localized;

                    if (field.cmdbuildtype === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookupArray) {
                        editor.xtype = "lookupfield";
                    }

                    if (field.cmdbuildtype === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.link) {
                        linkType = true;
                    }
                }

                // use text field for "Description contains" operator that can be used in references
                if (CMDBuildUI.util.helper.FiltersHelper.isOperatorForRefernceOrLookupDescription(a.operator) || linkType) {
                    editor.xtype = "textfield";
                    if (editor.bind && editor.bind.store) {
                        delete editor.bind.store;
                    }
                }

                editor.fieldLabel = Ext.String.format("{0} - {1}", fieldlabel, CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(a.operator));
                editor._tempid = a._tempid;
                editor.ignoreCqlFilter = true;

                var container = {
                    xtype: 'fieldcontainer',
                    layout: 'anchor',
                    padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                    items: [editor]
                };

                if (a.operator === CMDBuildUI.util.helper.FiltersHelper.operators.between) {
                    container.items.push(Ext.applyIf({
                        fieldLabel: '',
                        _tempid: a._tempid + '-v2'
                    }, editor));
                }

                fields.push(container);
            });
            return fields;
        },

        /**
         *
         * @param {CMDBuild.model.base.Filter} filter
         */
        applyFilter: function (filter, filterpopup) {
            var me = this,
                view = me.getView(),
                vm = view.lookupViewModel();
            if (filterpopup) {
                var applyBtn = filterpopup.down("#applybutton"),
                    applyAndSaveBtn = filterpopup.down("#savebutton"),
                    cancelBtn = filterpopup.down("#cancelbutton");
            }

            function applyFilter() {
                me.getView().fireEventArgs('applyfilter', [me.getView(), filter]);
                var binding = vm.bind("{" + view.getStoreName() + "}", function (store) {
                    if (store) {
                        var advancedFilter = store.getAdvancedFilter();
                        // apply filter
                        advancedFilter.clearManagementFilter();
                        if (view.getIsDms()) {
                            var attachmentconfiguration = filter.get('configuration').attachment.composite ? filter.get('configuration') : filter.get('configuration').attachment;
                            advancedFilter.applyAdvancedFilter(attachmentconfiguration);
                        } else {
                            advancedFilter.applyAdvancedFilter(filter.get("configuration"));
                        }
                        if (CMDBuildUI.util.helper.FiltersHelper.validityCheckFilter(advancedFilter)) {
                            store.load();

                            var filters = vm.get("item").filters(),
                                findFilterRecord = filters.findRecord("_id", filter.getId());
                            if (!findFilterRecord) {
                                filters.add(filter);
                            }

                            // update selected filter data
                            vm.set("appliedfilter.id", filter.getId());
                            vm.set("appliedfilter.description", filter.get("description"));

                            if (vm.get("item").setCurrentFilter) {
                                vm.get("item").setCurrentFilter(filter);
                            }
                            if (filterpopup) {
                                filterpopup.close();
                            }

                        } else {
                            if (filterpopup) {
                                CMDBuildUI.util.Utilities.enableFormButtons([applyBtn, applyAndSaveBtn, cancelBtn]);
                            }
                            filter.reject();
                            Ext.asap(function () {
                                CMDBuildUI.util.Notifier.showWarningMessage(
                                    Ext.String.format(
                                        '<span data-testid="message-window-text">{0}</span>',
                                        CMDBuildUI.locales.Locales.errors.invalidfilter
                                    )
                                );
                            });
                        }
                        binding.destroy();
                    }
                });
            }

            var tempFilter = new CMDBuildUI.util.AdvancedFilter(),
                filterConfig = filter.get("configuration");
            tempFilter.applyAdvancedFilter(filterConfig);
            // check runtime attributes
            var runtimeattrs = [],
                savingaction = vm.get('savingaction');

            function checkRuntime(v) {
                if (v.parameterType === CMDBuildUI.util.helper.FiltersHelper.parameterstypes.runtime) {
                    v._tempid = Ext.String.format("{0}-{1}", v.attribute, Ext.String.leftPad(Ext.Number.randomInt(0, 9999), 4, '0'));
                    runtimeattrs.push(v);
                }
            }
            CMDBuildUI.view.filters.Launcher.analyzeAttributeRecursive(filterConfig.attributesCustom || filterConfig.attribute, checkRuntime);
            // validate filter with skip value validation for runtime attributes
            if (CMDBuildUI.util.helper.FiltersHelper.validityCheckFilter(tempFilter)) {
                // save filter then apply it
                this.saveFilter(filter).then(function () {
                    if (runtimeattrs.length > 0) {
                        var popup;
                        var form = {
                            xtype: 'form',
                            fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
                            scrollable: true,
                            items: me.getFormForRuntimeAttributes(runtimeattrs),
                            listeners: {
                                beforedestroy: function (form) {
                                    form.removeAll(true);
                                }
                            },
                            buttons: [{
                                text: CMDBuildUI.locales.Locales.common.actions.apply,
                                ui: 'management-action-small',
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.common.actions.apply'
                                },
                                handler: function (button, e) {
                                    var fields = {};
                                    var form = button.up("form");
                                    form.getForm().getFields().getRange().forEach(function (f) {
                                        fields[f._tempid] = f;
                                    });

                                    function updateRuntimeValues(f) {
                                        if (f.parameterType === 'runtime') {
                                            f.value = [];
                                            var v = fields[f._tempid].getValue();
                                            if (!Ext.isEmpty(v)) {
                                                f.value.push(v);
                                            }
                                            if (f.operator === CMDBuildUI.util.helper.FiltersHelper.operators.between && fields[f._tempid + '-v2'].getValue()) {
                                                f.value.push(fields[f._tempid + '-v2'].getValue());
                                            }
                                            delete f._tempid;
                                        }
                                    }
                                    filterConfig = filter.get("configuration");
                                    CMDBuildUI.view.filters.Launcher.analyzeAttributeRecursive(filterConfig.attributesCustom || filterConfig.attribute, updateRuntimeValues);
                                    applyFilter();
                                    popup.destroy();
                                }
                            }, {
                                text: CMDBuildUI.locales.Locales.common.actions.cancel,
                                ui: 'secondary-action-small',
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
                                },
                                handler: function (button, e) {
                                    CMDBuildUI.util.Utilities.enableFormButtons([applyBtn, applyAndSaveBtn, cancelBtn]);
                                    popup.destroy();
                                    // if filter is saved close popup filter
                                    if (savingaction && filterpopup) {
                                        filterpopup.close();
                                    }
                                }
                            }]
                        };
                        popup = CMDBuildUI.util.Utilities.openPopup(null, '', form, {}, {
                            width: '40%',
                            height: '40%',
                            alwaysOnTop: CMDBuildUI.util.Utilities._popupAlwaysOnTop + 1
                        });
                    } else {
                        Ext.asap(function () {
                            applyFilter();
                        });
                    }
                });
            } else {
                CMDBuildUI.util.Utilities.enableFormButtons([applyBtn, applyAndSaveBtn, cancelBtn]);
                filter.reject();
                Ext.asap(function () {
                    CMDBuildUI.util.Notifier.showWarningMessage(
                        Ext.String.format(
                            '<span data-testid="message-window-text">{0}</span>',
                            CMDBuildUI.locales.Locales.errors.invalidfilter
                        )
                    );
                });
            }
        },

        /**
         * Add filter to filters store and save filter
         *
         * @param {CMDBuild.model.base.Filter} filter
         *
         * @returns {Ext.promise.Promise }
         */
        saveFilter: function (filter) {
            CMDBuildUI.util.helper.FormHelper.startSavingForm();
            var deferred = new Ext.Deferred(),
                vm = this.getViewModel(),
                filters = vm.get("item").filters(),
                findFilterRecord = filters.findRecord("_id", filter.getId());
            if (!findFilterRecord) {
                filters.add(filter);
            }
            if (vm.get('savingaction')) {
                filter.save({
                    callback: function (record) {
                        CMDBuildUI.util.helper.FormHelper.endSavingForm();
                        deferred.resolve();
                    }
                });
                vm.set('savingaction', false);
            } else {
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                deferred.resolve();
            }
            return deferred.promise;
        },

        /**
         * @deprectaed use instead CMDBuildUI.util.helper.FiltersHelper.validityCheckFilter(advancedFilter)
         *
         * @param {CMDBuildUI.util.AdvancedFilter} advancedFilter
         */
        validityCheckFilter: function (advancedFilter) {
            return CMDBuildUI.util.helper.FiltersHelper.validityCheckFilter(advancedFilter);
        }
    }

});