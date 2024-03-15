Ext.define('CMDBuildUI.view.relations.masterdetail.TabController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.relations-masterdetail-tab',

    control: {
        '#': {
            targetdataupdated: 'onTargetDataUpdated'
        },
        'grid': {
            rowdblclick: 'onGridRowDblclick'
        },
        '#adddetailbtn': {
            click: 'onAddDetailButtonClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.relations.masterdetail.Tab} view
     * @param {CMDBuildUI.model.classes.Class|CMDBuildUI.model.processes.Process} targetTypeObject
     * @param {CMDBuildUI.model.classes.Card} targetTypeModel
     * @param {CMDBuildUI.model.domains.Domain} domain
     * @param {Object} eOpts
     */
    onTargetDataUpdated: function (view, targetTypeObject, targetTypeModel, domain, eOpts) {
        var me = this;
        var vm = view.lookupViewModel();
        // get reference field name for target model
        targetTypeModel.getFields().forEach(function (field) {
            if (field.attributeconf && field.attributeconf.domain === domain.get("name")) {
                me._targetreferencefield = field.name;
            }
        });

        var detailxtype, btnHidden = true;
        if (vm.get("targetType") === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
            detailxtype = 'processes-instances-rowcontainer';
        } else {
            detailxtype = 'classes-cards-card-view';
            if (!view.getReadOnly()) {
                btnHidden = false;
            }
        }
        CMDBuildUI.util.helper.GridHelper.getColumnsForType(
            vm.get("targetType"),
            vm.get("targetTypeName"),
            {
                allowFilter: false,
                addTypeColumn: targetTypeObject.get("prototype"),
                aggregate: domain.get('masterDetailAggregateAttrs')
            }
        ).then(function (columns) {
            columns.forEach(function (column) {
                if (column.dataIndex === me._targetreferencefield) {
                    column.hidden = true;
                }
            });
            var masterDetailAggregateAttrs = domain.get('masterDetailAggregateAttrs');
            view.add({
                xtype: 'relations-masterdetail-grid',
                reference: 'mdgrid',
                columns: columns,
                features: Ext.isArray(masterDetailAggregateAttrs) && masterDetailAggregateAttrs.length > 0 ? [{
                    ftype: 'bufferedsummary',
                    showSummaryRow: true
                }] : null,
                plugins: [{
                    ptype: 'forminrowwidget',
                    pluginId: 'forminrowwidget',
                    expandOnDblClick: true,
                    removeWidgetOnCollapse: true,
                    widget: {
                        xtype: detailxtype,
                        padding: 10,
                        shownInPopup: true,
                        viewModel: {
                            data: {
                                theObject: null,
                                basepermissions: view.lookupViewModel().get("basepermissions")
                            }
                        }, // do not remove otherwise the viewmodel will not be initialized
                        tabpaneltools: [{
                            xtype: 'tool',
                            itemId: 'detaileditbtn',
                            iconCls: 'x-fa fa-pencil',
                            cls: 'management-tool',
                            disabled: true,
                            hidden: btnHidden,
                            tooltip: CMDBuildUI.locales.Locales.relations.editdetail,
                            callback: function (panel, tool, event) {
                                var theObject = panel.lookupViewModel().get('theObject');
                                me.onItemEditButtonClick(panel.up(), theObject, domain);
                            },
                            listeners: {
                                render: function () {
                                    var editpermission = this.lookupViewModel().get('classObject').get('_can_update');
                                    this.setDisabled(!editpermission);
                                }
                            },

                            autoEl: {
                                'data-testid': 'relations-masterdetail-tab-editBtn'
                            }
                        }, {
                            xtype: 'tool',
                            itemId: 'detailopenbtn',
                            iconCls: 'x-fa fa-external-link',
                            cls: 'management-tool',
                            hidden: false,
                            tooltip: CMDBuildUI.locales.Locales.relations.opendetail,
                            callback: function (panel, tool, event) {
                                me.onItemViewButtonClick(panel.up());
                            },
                            autoEl: {
                                'data-testid': 'relations-masterdetail-tab-openBtn'
                            }
                        }, {
                            xtype: 'tool',
                            itemId: 'detaildeletebtn',
                            iconCls: 'x-fa fa-trash',
                            cls: 'management-tool',
                            disabled: true,
                            hidden: btnHidden,
                            tooltip: CMDBuildUI.locales.Locales.relations.deletedetail,
                            callback: function (panel, tool, event) {
                                me.onItemDeleteButtonClick(panel.up());
                            },
                            autoEl: {
                                'data-testid': 'relations-masterdetail-tab-deleteBtn'
                            },
                            listeners: {
                                render: function () {
                                    var deletepermission = this.lookupViewModel().get('classObject').get('_can_delete');
                                    this.setDisabled(!deletepermission);
                                }
                            }
                        }]
                    }
                }],
                viewModel: {
                    data: {
                        objectTypeName: view.getTargetTypeName()
                    }
                }
            });

        });

        // update button
        if (targetTypeObject.get("prototype")) {
            // if is superclass add menu with all children leafs
            var children = targetTypeObject.getChildren(true);
            var menu = [];
            for (var i = 0; i < children.length; i++) {
                var child = children[i];
                menu.push({
                    text: child.getTranslatedDescription(),
                    iconCls: 'x-fa fa-file-text-o',
                    listeners: {
                        click: 'onAddDetailMenuItemClick'
                    },
                    disabled: !child.get(CMDBuildUI.model.base.Base.permissions.edit) ||
                        (domain.get("cardinality") === CMDBuildUI.model.domains.Domain.cardinalities.onetomany && Ext.Array.contains(domain.get("disabledDestinationDescendants"), child.getId())) ||
                        (domain.get("cardinality") === CMDBuildUI.model.domains.Domain.cardinalities.manytoone && Ext.Array.contains(domain.get("disabledSourceDescendants"), child.getId())),
                    type: child.get("name")
                });
            }
            this.lookupReference("adddetailbtn").setMenu(Ext.Array.sort(menu, function (a, b) {
                return a.text === b.text ? 0 : (a.text < b.text ? -1 : 1);
            }));
        }

        vm.bind("{records}", function (records) {
            records.addListener("load", function () {
                vm.set("mditems", records.getTotalCount());
            });
        });
    },

    /**
     * @param {Ext.button.Button} button Add detail botton
     * @param {Event} e
     * @param {Object} eOpts
     */
    onAddDetailButtonClick: function (button, e, eOpts) {
        var view = this.getView();
        var targetTypeName = view.getTargetTypeName();
        var targetTypeObject = view.getTargetTypeObject();
        if (!targetTypeObject.get("prototype")) {
            // if element is not prototype
            this.showAddDetailForm(targetTypeName, targetTypeObject.get("_description_translation"));
        }
    },

    /**
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onAddDetailMenuItemClick: function (item, event, eOpts) {
        this.showAddDetailForm(item.type, item.text);
    },

    /**
     * Show add detail form
     * @param {String} targetTypeName
     * @param {String} targetTypeDescription
     */
    showAddDetailForm: function (targetTypeName, targetTypeDescription) {
        var me = this,
            vm = this.getViewModel(),
            view = this.getView(),
            mask;
        CMDBuildUI.util.helper.ModelHelper.getModel(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
            targetTypeName
        ).then(function (model) {
            var panel,
                title = Ext.String.format("{0} {1}", CMDBuildUI.locales.Locales.classes.cards.addcard, targetTypeDescription),
                domain = view.getDomain(),
                isSimpleClass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(domain.get("source")).isSimpleClass(),
                config = {
                    xtype: 'classes-cards-card-create',
                    padding: 10,
                    fireGlobalEventsAfterSave: false,
                    viewModel: {
                        data: {
                            objectTypeName: targetTypeName
                        }
                    },
                    hideInlineElements: {
                        inlineNotes: false
                    },
                    defaultValues: [{
                        domain: domain.get("name"),
                        value: vm.get("objectId"),
                        valuedescription: vm.get("objectDescription"),
                        editable: false,
                        destination: domain.get("destination")
                    }],
                    overrideReadOnlyFields: domain.get('masterDetailDisabledCreateAttrs'),
                    redirectAfterSave: false,
                    buttons: [{
                        ui: 'management-action',
                        itemId: 'detailsavebtn',
                        text: CMDBuildUI.locales.Locales.common.actions.save,
                        autoEl: {
                            'data-testid': 'widgets-createmodifycard-save'
                        },
                        formBind: true,
                        localized: {
                            text: 'CMDBuildUI.locales.Locales.common.actions.save'
                        },
                        handler: function (btn, event) {
                            var cancelBtn = this.up().down("#detailclosebtn");
                            btn.showSpinner = true;
                            CMDBuildUI.util.Utilities.disableFormButtons([btn, cancelBtn]);

                            panel.down("classes-cards-card-create").getController().saveForm().then(function (record) {
                                if (!isSimpleClass) {
                                    mask = CMDBuildUI.util.Utilities.addLoadMask(panel);
                                }
                            }).otherwise(function () {
                                CMDBuildUI.util.Utilities.enableFormButtons([btn, cancelBtn]);
                            });
                        }
                    }, {
                        ui: 'secondary-action',
                        itemId: 'detailclosebtn',
                        text: CMDBuildUI.locales.Locales.common.actions.close,
                        autoEl: {
                            'data-testid': 'widgets-createmodifycard-close'
                        },
                        localized: {
                            text: 'CMDBuildUI.locales.Locales.common.actions.close'
                        },
                        handler: function (btn, event) {
                            panel.destroy();
                        }
                    }],
                    onAfterSave: function (record) {
                        if (isSimpleClass) {
                            vm.get("records").load();
                            panel.destroy();
                        } else {
                            // add relation
                            var relStore = Ext.create("Ext.data.Store", {
                                model: "CMDBuildUI.model.domains.Relation",
                                proxy: {
                                    type: "baseproxy",
                                    url: CMDBuildUI.util.api.Classes.getCardRelations(record.get("_type"), record.get("_id"))
                                },
                                autoLoad: false
                            });

                            relStore.load({
                                callback: function (records, operation, success) {

                                    var _is_direct = Ext.Array.include(domain.get('sources'), record.get('_type'));
                                    // creates the relation if it is not created by a reference field
                                    if (domain && !relStore.query("_type", domain.getId()).length) {
                                        relStore.add({
                                            _type: domain.getId(),
                                            _sourceType: record.get("_type"),
                                            _sourceId: record.get("_id"),
                                            _destinationType: vm.get('objectTypeName'),
                                            _destinationId: vm.get('objectId'),
                                            _is_direct: _is_direct
                                        });
                                        relStore.sync({
                                            callback: function () {
                                                onRelationCreated();
                                            }
                                        });
                                    } else {
                                        onRelationCreated();
                                    }

                                }
                            });

                            function onRelationCreated() {
                                vm.get("records").load();
                                Ext.GlobalEvents.fireEvent("updateRelationStore");

                                CMDBuildUI.util.Utilities.removeLoadMask(mask);
                                panel.destroy();

                                // destroy store
                                Ext.asap(function () {
                                    relStore.destroy();
                                });
                            }
                        }
                    }

                };

            panel = CMDBuildUI.util.Utilities.openPopup('popup-add-detail', title, config, null);
        });
    },

    /**
     * Called when card is shown
     * @param {CMDBuildUI.view.classes.cards.card.View} cardpanel 
     */
    onItemViewButtonClick: function (cardpanel) {
        var cvm = cardpanel.lookupViewModel();
        this.redirectToItem(cvm.get("objectTypeName"), cvm.get("objectId"), cvm.get("activityId"));
    },

    /**
     * Called when card is shown
     * @param {CMDBuildUI.view.classes.cards.card.View} cardpanel 
     * @param {Ext.data.Model} theObject
     * @param {CMDBuildUI.view.domains.Domain} domain
     */
    onItemEditButtonClick: function (cardpanel, theObject, domain) {
        var me = this,
            popup,
            grid = this.lookupReference("mdgrid"),
            title = cardpanel.getTitle(),
            cvm = cardpanel.lookupViewModel(),
            config = {
                xtype: 'classes-cards-card-edit',
                padding: 10,
                viewModel: {
                    data: {
                        objectType: cvm.get('objectType'),
                        objectTypeName: cvm.get("objectTypeName"),
                        objectId: cvm.get("objectId"),
                        basepermissions: {
                            edit: theObject ? theObject.data._model[CMDBuildUI.model.base.Base.permissions.edit] : false
                        }
                    }
                },
                hideInlineElements: false,
                redirectAfterSave: false,
                fireGlobalEventsAfterSave: false,
                overrideReadOnlyFields: this._targetreferencefield ? Ext.Array.merge([this._targetreferencefield], domain.get('masterDetailDisabledCreateAttrs') || []) : domain.get('masterDetailDisabledCreateAttrs') || [],
                buttons: [{
                    ui: 'management-action',
                    itemId: 'detailsavebtn',
                    text: CMDBuildUI.locales.Locales.common.actions.save,
                    autoEl: {
                        'data-testid': 'widgets-createmodifycard-save'
                    },
                    formBind: true,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.common.actions.save'
                    },
                    handler: function (btn, event) {
                        var cancelBtn = this.up().down("#detailclosebtn");
                        btn.showSpinner = true;
                        CMDBuildUI.util.Utilities.disableFormButtons([btn, cancelBtn]);

                        popup.down("classes-cards-card-edit").getController().saveForm().then(function (record) {
                            grid.updateRowWithExpader(record);
                            Ext.GlobalEvents.fireEvent("updateRelationStore");
                            popup.destroy();
                        }).otherwise(function () {
                            CMDBuildUI.util.Utilities.enableFormButtons([btn, cancelBtn]);
                        });
                    }
                }, {
                    ui: 'secondary-action',
                    itemId: 'detailclosebtn',
                    text: CMDBuildUI.locales.Locales.common.actions.close,
                    autoEl: {
                        'data-testid': 'widgets-createmodifycard-close'
                    },
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.common.actions.close'
                    },
                    handler: function (btn, event) {
                        popup.destroy();
                    }
                }]
            };

        // open popup
        popup = CMDBuildUI.util.Utilities.openPopup(null, title, config);
    },

    /**
     * Called when card is shown
     * @param {CMDBuildUI.view.classes.cards.card.View} cardpanel 
     */
    onItemDeleteButtonClick: function (cardpanel) {
        var me = this;
        CMDBuildUI.view.classes.cards.Util.deleteCard(cardpanel.lookupViewModel().get("theObject")).then(function (record) {
            me.getViewModel().get("records").load();
            Ext.GlobalEvents.fireEvent("updateRelationStore");
        });
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
            var store = vm.get("records");
            if (store) {
                CMDBuildUI.util.Ajax.setActionId("card.details.search");
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
        var vm = this.getViewModel(),
            store = vm.get("records");
        if (store) {
            CMDBuildUI.util.Ajax.setActionId("card.details.clearfilter");
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
     * 
     * @param {CMDBuildUI.view.attachments.Grid} view 
     * @param {Ext.data.Model} record 
     * @param {HTMLElement} element 
     * @param {Number} rowIndex 
     * @param {Ext.event.Event} event 
     * @param {Object} eOpts 
     */
    onGridRowDblclick: function (view, record, element, rowIndex, event, eOpts) {
        this.redirectToItem(record.get("_type"), record.getId());
    },

    privates: {
        /**
         * @property {String} _targetreferencefield
         * The name of the field in target model which has as domain current domain.
         */
        _targetreferencefield: null,

        /**
         * 
         * @param {Ext.data.Model} record 
         */
        redirectToItem: function (objecttypename, objectid, activityid) {
            var vm = this.getViewModel();
            if (vm.get("targetType") === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
                url = Ext.String.format(
                    "processes/{0}/instances/{1}",
                    objecttypename,
                    objectid
                );
            } else {
                url = Ext.String.format(
                    "classes/{0}/cards/{1}",
                    objecttypename,
                    objectid
                );
            }

            // open detail in detail window when is not in read-only mode
            if (!this.getView().getReadOnly()) {
                url += "/view";
            }
            this.redirectTo(url);
        }
    }
});