Ext.define('CMDBuildUI.view.fields.reference.ReferenceComboController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.fields-referencecombofield',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            beforequery: 'onBeforeQuery',
            change: 'onChange',
            cleartrigger: 'onClearTrigger',
            searchtrigger: 'onSearchTrigger',
            expand: 'onExpand'
        }
    },
    onBeforeRender: function (view, eOpts) {
        var vm = view.lookupViewModel();

        function addStore(initialvalue) {
            var metadata = view.metadata,
                url, object;

            // set url
            switch (metadata.targetType) {
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                    url = CMDBuildUI.util.api.Classes.getCardsUrl(metadata.targetClass);
                    object = CMDBuildUI.util.helper.ModelHelper.getClassFromName(metadata.targetClass);
                    break;
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                    url = CMDBuildUI.util.api.Processes.getInstancesUrl(metadata.targetClass);
                    object = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(metadata.targetClass);
                    break;
            }

            // sorters
            var sorters = [];
            if (object && object.defaultOrder().getCount()) {
                object.defaultOrder().getRange().forEach(function (o) {
                    sorters.push({
                        property: o.get("attribute"),
                        direction: o.get("direction") === "descending" ? "DESC" : 'ASC'
                    });
                });
            } else {
                sorters.push({
                    property: 'Description'
                });
            }

            // create store
            var store = {
                model: 'CMDBuildUI.model.domains.Reference',
                proxy: {
                    type: 'baseproxy',
                    url: url,
                    extraParams: {
                        attrs: 'Id,Description,Code'
                    }
                },
                listeners: {
                    "load": {
                        fn: view.onStoreLoaded,
                        scope: view
                    }
                },
                remoteFilter: false,
                remoteSort: true,
                pageSize: CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.referencecombolimit),
                sorters: sorters,
                autoLoad: !!url,
                autoDestroy: true
            };


            // set extra params for initial value
            if (initialvalue) {
                store.proxy.extraParams.positionOf = initialvalue;
            }

            // if has ecql filter
            if ((metadata.ecqlFilter || metadata.useDomainFilter) && view.getFilterRecordLinkName()) {
                var ecqlFilter;

                if (metadata.useDomainFilter && !Ext.Object.isEmpty(metadata._referenceFilters)) {
                    if (metadata.direction === 'inverse' && !Ext.isEmpty(metadata._referenceFilters.sourceFilter)) {
                        ecqlFilter = metadata._referenceFilters.sourceFilter_ecqlFilter;
                    } else if (metadata.direction === 'direct' && !Ext.isEmpty(metadata._referenceFilters.destinationFilter)) {
                        ecqlFilter = metadata._referenceFilters.destinationFilter_ecqlFilter;
                    }
                } else {
                    ecqlFilter = metadata.ecqlFilter;
                }

                // add advanced filter
                if (!view.ignoreCqlFilter) {
                    store.advancedFilter = {
                        ecql: view.getEcqlFilter()
                    };
                }

                // add binds to reload store on attributes changes
                var binds = CMDBuildUI.util.ecql.Resolver.getViewModelBindings(
                    ecqlFilter,
                    view.getRecordLinkName()
                );
                if (!Ext.Object.isEmpty(binds)) {
                    vm.bind({
                        bindTo: binds
                    }, function (data) {
                        var combostore = view.getStore();

                        // abort pending requests
                        if (combostore.isLoading()) {
                            var operations = Ext.Object.getValues(combostore.getProxy().pendingOperations),
                                operation = operations.length ? operations[0] : null;
                            operation ? operation.abort() : null;
                        }

                        if (!view.ignoreCqlFilter) {
                            combostore.getAdvancedFilter().addEcqlFilter(view.getEcqlFilter());
                        }

                        if (view.getValue()) {
                            combostore.proxy.extraParams.positionOf = view.getValue();
                        }

                        combostore.load();
                    });
                }

            }

            var domain = Ext.getStore("domains.Domains").findRecord("name", view.metadata.domain);
            if (domain) {
                var disabledDescendants = domain.get("cardinality") === CMDBuildUI.model.domains.Domain.cardinalities.onetomany ?
                    domain.get("disabledSourceDescendants") :
                    domain.get("disabledDestinationDescendants");

                if (!Ext.isEmpty(disabledDescendants)) {
                    var enabledDescendants = domain.get("cardinality") === CMDBuildUI.model.domains.Domain.cardinalities.onetomany ?
                        domain.get("sources") :
                        domain.get("destinations");
                    if (!store.advancedFilter) {
                        store.advancedFilter = {};
                    }
                    view.typesfilter = store.advancedFilter.attributes = {
                        _type: [{
                            operator: 'in',
                            value: enabledDescendants
                        }]
                    };
                }

                // add filter for 1:1 domains
                if (domain.get("cardinality") === CMDBuildUI.model.domains.Domain.cardinalities.onetoone) {
                    if (!store.advancedFilter) {
                        store.advancedFilter = {};
                    }
                    var rel = {
                        domain: domain.get('name'),
                        type: CMDBuildUI.util.helper.FiltersHelper.relationstypes.noone
                    };
                    if (domain.get('source') === view.metadata.targetClass) {
                        rel.source = domain.get('source');
                        rel.destination = domain.get('destination');
                        rel.direction = '_1';
                    } else {
                        rel.source = domain.get('destination');
                        rel.destination = domain.get('source');
                        rel.direction = '_2';
                    }
                    view.relsfilter = store.advancedFilter.relation = [rel];
                }
            }

            // set store
            view.setStore(store);
        }
        if (view.getInitialConfig().bind && !Ext.Object.isEmpty(view.getInitialConfig().bind)) {
            vm.bind({
                bindTo: view.getInitialConfig().bind.value,
                single: true
            }, function (value) {
                addStore(value);
            });
        } else {
            addStore();
        }
    },

    /**
     * @param {CMDBuildUI.view.fields.reference.ReferenceCombo} view
     * @param {Numeric|String} newvalue
     * @param {Numeric|String} oldvalue
     * @param {Object} eOpts
     */
    onChange: function (view, newvalue, oldvalue, eOpts) {
        var object = view._ownerRecord;
        if (!object && view.getRecordLinkName()) {
            object = view.lookupViewModel().get(view.getRecordLinkName());
        }
        if (object && object.set) {
            var selected = view.getSelection();
            object.set(Ext.String.format("_{0}_description", view.getName()), selected ? selected.get("Description") : null);
            object.set(Ext.String.format("_{0}_code", view.getName()), selected ? selected.get("Code") : null);
        }
        if (view.hasBindingValue && Ext.isEmpty(newvalue) && !Ext.isEmpty(oldvalue)) {
            view.getBind().value.setValue(null);
        }
    },

    /**
     * @param {CMDBuildUI.view.fields.reference.ReferenceCombo} combo
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onClearTrigger: function (combo, trigger, eOpts) {
        combo.clearValue();
        // clear local filter if combo is expanded
        if (combo.isExpanded) {
            combo.doLocalQuery("");
        }
        combo.lastSelectedRecords = [];
        this.getViewModel().set("selection", null);
    },

    /**
     * @param {CMDBuildUI.view.fields.reference.ReferenceCombo} view
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchTrigger: function (view, trigger, eOpts) {
        // prevent multiple popup opening
        if (!view.popupAlreadyOpened) {
            view.popupAlreadyOpened = true;
            var object = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(view.metadata.targetClass);
            if (object) {
                var objectTypeDescription = object.getTranslatedDescription();
                var title = Ext.String.format(
                    "{0} - {1}",
                    CMDBuildUI.locales.Locales.common.grid.list,
                    objectTypeDescription
                );
                var popup = CMDBuildUI.util.Utilities.openPopup(null, title, {
                    xtype: 'fields-reference-selectionpopup',
                    defaultSearchFilter: eOpts.searchquery ? eOpts.searchquery : null,
                    viewModel: {
                        data: {
                            objectType: view.metadata.targetType,
                            objectTypeName: view.metadata.targetClass,
                            objectTypeDescription: objectTypeDescription,
                            storeinfo: {
                                type: null,
                                proxyurl: view.getStore().getProxy().getUrl(),
                                autoload: false,
                                ecqlfilter: view.ignoreCqlFilter ? {} : view.getEcqlFilter(),
                                typesfilter: view.typesfilter,
                                relsfilter: view.relsfilter
                            },
                            selection: view.getSelection()
                        }
                    },

                    setValueOnParentCombo: function (record) {
                        if (view.column) {
                            var object = view._ownerRecord;
                            object.set(view.getName(), record[0].getId());
                        }

                        var store = view.getStore();
                        if (store && !store.findRecord(record[0].getId())) {
                            store.add(record);
                        }
                        view.setSelection(record);
                    },

                    closePopup: function () {
                        popup.removeAll(true);
                        popup.close();
                    }
                }, {
                    close: function () {
                        // set to false flag
                        view.popupAlreadyOpened = false;
                    }
                });
            }

        }
    },

    /**
     * Before query listener.
     * @param {Object} queryPlan An object containing details about the query to be executed.
     * @param {String} queryPlan.query The query value to be used to match against the ComboBox's {@link #valueField}.
     * @param {String} queryPlan.lastQuery The query value used the last time a store query was made.
     * @param {Boolean} queryPlan.forceAll If `true`, causes the query to be executed even if the minChars threshold is not met.
     * @param {Boolean} queryPlan.cancel A boolean value which, if set to `true` upon return, causes the query not to be executed.
     * @param {Boolean} queryPlan.rawQuery If `true` indicates that the raw input field value is being used, and upon store load,
     * the input field value should **not** be overwritten.
     */
    onBeforeQuery: function (queryPlan) {
        var view = this.getView();
        if (!view._allowexpand) {
            view.expand(queryPlan.query);
            return false;
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.fields.reference.ReferenceCombo} view
     * @param {Object} eOpts
     */
    onExpand: function (view, eOpts) {
        var picker = view.getPicker();
        if (picker.getSelectionModel().hasSelection()) {
            var selected = picker.getSelectionModel().getSelection()[0];
            var itemNode = picker.getNode(selected);

            if (itemNode) {
                picker.setScrollY(itemNode.offsetTop);
            }
        }
    }
});