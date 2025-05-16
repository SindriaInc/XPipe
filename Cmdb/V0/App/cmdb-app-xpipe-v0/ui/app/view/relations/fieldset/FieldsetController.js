Ext.define('CMDBuildUI.view.relations.fieldset.FieldsetController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.relations-fieldset',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#addrelationbtn': {
            click: 'onAddRelationBtnClick'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.relations.fieldset.Fieldset} view 
     * @param {Object} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        var vm = view.lookupViewModel();
        var me = this;
        vm.bind("{targetmodel}", function (targetmodel) {
            if (targetmodel) {
                // details xtype
                var detailxtype, btnHidden;
                if (vm.get("targettype") === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
                    detailxtype = 'processes-instances-rowcontainer';
                    btnHidden = true;
                    vm.set('relstore.proxyurl', CMDBuildUI.util.api.Processes.getProcessInstanceRelations(vm.get('current.objectTypeName'), vm.get('current.objectId')));
                } else {
                    detailxtype = 'classes-cards-card-view';
                    btnHidden = false;
                    vm.set('relstore.proxyurl', CMDBuildUI.util.api.Classes.getCardRelations(vm.get('current.objectTypeName'), vm.get('current.objectId')));
                }

                btnHidden = view.formmode === CMDBuildUI.util.helper.FormHelper.formmodes.read ? true : btnHidden;

                vm.set("addrelationbtn.hidden", btnHidden);
                vm.bind("{allRelations}", function (store) {
                    store.load({
                        callback: function (records, operation, success) {
                            // get columns
                            CMDBuildUI.util.helper.GridHelper.getColumnsForType(
                                vm.get("targettype"),
                                vm.get("targettypename")
                            ).then(function (cols) {
                                vm.get("domain").getAttributes().then(function (attrs) {
                                    // get column defs for relation attributes
                                    attrs.getRange().forEach(function (attr) {
                                        var field = CMDBuildUI.util.helper.ModelHelper.getModelFieldFromAttribute(attr),
                                            col = CMDBuildUI.util.helper.GridHelper.getColumn(field);
                                        if (col) {
                                            var baserenderer = col.renderer;
                                            col.hidden = false;
                                            col.renderer = function (value, metaData, record, rowindex, colindex, store, view) {
                                                // find relation item
                                                var rels = view.lookupViewModel().get("allRelations");
                                                if (rels) {
                                                    var relPos = rels.findBy(function (relItem) {
                                                        return relItem.get("_type") === vm.get("domain").getId() && relItem.get("_destinationId") == record.get("_id");
                                                    });
                                                    if (relPos !== -1) {
                                                        var relItem = rels.getAt(relPos);
                                                        // return base render
                                                        return baserenderer(relItem.get(attr.get("name")), metaData, relItem, rowindex, colindex, store, view);
                                                    }
                                                }
                                            };
                                            cols.push(col);
                                        }
                                    });
                                    if (!view.destroyed) {
                                        // add grid
                                        view.add({
                                            xtype: 'relations-fieldset-grid',
                                            columns: cols,
                                            maxHeight: (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.relationlimit) + 1) * 31 + 15,

                                            plugins: [{
                                                ptype: 'forminrowwidget',
                                                expandOnDblClick: true,
                                                removeWidgetOnCollapse: true,
                                                widget: {
                                                    xtype: detailxtype,
                                                    viewModel: {
                                                        data: {
                                                            basepermissions: view.lookupViewModel().get("basepermissions")
                                                        }
                                                    }, // do not remove otherwise the viewmodel will not be initialized
                                                    tabpaneltools: [{
                                                        xtype: 'tool',
                                                        itemId: 'viewcardaction',
                                                        iconCls: 'x-fa fa-external-link',
                                                        cls: 'management-tool',
                                                        tooltip: CMDBuildUI.locales.Locales.relations.opencard,
                                                        callback: function (panel, tool, event) {
                                                            me.onItemViewCardActionClick(panel.up());
                                                        },
                                                        autoEl: {
                                                            'data-testid': 'relations-fieldset-viewcardaction'
                                                        }
                                                    }, {
                                                        xtype: 'tool',
                                                        itemId: 'editrelationcard',
                                                        iconCls: 'x-fa fa-pencil',
                                                        cls: 'management-tool',
                                                        disabled: true,
                                                        hidden: btnHidden,
                                                        tooltip: CMDBuildUI.locales.Locales.relations.editrelation,
                                                        callback: function (panel, tool, event) {
                                                            me.onItemEditRelationCardClick(panel.up());
                                                        },
                                                        autoEl: {
                                                            'data-testid': 'relations-fieldset-editrelationcard'
                                                        },
                                                        bind: {
                                                            disabled: '{!basepermissions.edit && !permissions.edit}'
                                                        }
                                                    }, {
                                                        xtype: 'tool',
                                                        itemId: 'deleterelationcard',
                                                        iconCls: 'x-fa fa-trash',
                                                        cls: 'management-tool',
                                                        disabled: true,
                                                        hidden: btnHidden,
                                                        tooltip: CMDBuildUI.locales.Locales.relations.deleterelation,
                                                        callback: function (panel, tool, event) {
                                                            me.onItemDeleteRelationCardClick(panel.up());
                                                        },
                                                        autoEl: {
                                                            'data-testid': 'relations-fieldset-deleterelationcard'
                                                        },
                                                        bind: {
                                                            disabled: '{!basepermissions.edit && !permissions.delete}'
                                                        }
                                                    }, {
                                                        xtype: 'tool',
                                                        itemId: 'editcardaction',
                                                        iconCls: 'x-fa fa-pencil-square-o',
                                                        cls: 'management-tool',
                                                        disabled: true,
                                                        hidden: btnHidden,
                                                        tooltip: CMDBuildUI.locales.Locales.relations.editcard,
                                                        callback: function (panel, tool, event) {
                                                            me.onItemEditCardActionClick(panel.up());
                                                        },
                                                        autoEl: {
                                                            'data-testid': 'relations-fieldset-editcardaction'
                                                        },
                                                        bind: {
                                                            disabled: '{!basepermissions.edit && !permissions.edit}'
                                                        }
                                                    }]
                                                }
                                            }],
                                            listeners: {
                                                rowdblclick: function (grid, record, element, rowIndex, e, eOpts) {
                                                    me.redirectToItem(vm.get("targettype"), record.get("_type"), record.get("_id"));
                                                },
                                                rowclick: function (grid, record, element, rowIndex, e, eOpts) {
                                                    if (vm.get("targettype") === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
                                                        vm.set('relstore.proxyurl', CMDBuildUI.util.api.Processes.getProcessInstanceRelations(vm.get('current.objectTypeName'), vm.get('current.objectId')));
                                                    } else {
                                                        vm.set('relstore.proxyurl', CMDBuildUI.util.api.Classes.getCardRelations(vm.get('current.objectTypeName'), vm.get('current.objectId')));
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });
                            });
                        }
                    });
                });
            }
        });

        vm.bind("{records}", function (records) {
            records.addListener("load", function () {
                if (vm.getData()) {
                    vm.set("recordscount", records.getTotalCount());
                }
            });
        });
    },

    /**
     * 
     * @param {Ext.button.Button} button 
     * @param {Object} eOpts 
     */
    onAddRelationBtnClick: function (button, eOpts) {
        var vm = this.getViewModel(),
            view = this.getView(),

            domain = vm.get("domain"),
            direction = vm.get("direction") === "_1" ? 'inverse' : 'direct';

        var popup;
        var title = vm.get("basetitle");
        var config = {
            xtype: 'relations-list-add-container',
            originTypeName: vm.get("objectTypeName"),
            originId: vm.get("objectId"),
            multiSelect: true,
            viewModel: {
                data: {
                    objectTypeName: vm.get("targettypename"),
                    relationDirection: direction,
                    theDomain: domain
                }
            },
            listeners: {
                popupclose: function () {
                    popup.removeAll(true);
                    popup.close();
                }
            },
            onSaveSuccess: function () {
                vm.get('records').load();
                if (vm.get('allRelations').isLoaded()) {
                    vm.get('allRelations').load({
                        callback: function () {
                            try {
                                view.down('relations-fieldset-grid').reconfigure();
                            } catch (error) {
                                // do nothing
                            }
                        }
                    });
                }
            }
        };

        popup = CMDBuildUI.util.Utilities.openPopup('popup-add-relation', title, config, null);

    },

    privates: {
        /**
         * 
         * @param {Ext.form.Panel} view 
         */
        onItemViewCardActionClick: function (view) {
            var vm = view.lookupViewModel();
            this.redirectToItem(vm.get("targettype"), vm.get("targettypename"), vm.get("objectId"));

            if (vm.get('isInBimPopup')) {
                CMDBuildUI.util.Utilities.closePopup('bimPopup');
            }
        },

        /**
         * 
         * @param {Ext.form.Panel} view 
         */
        onItemEditCardActionClick: function (view) {
            var popup;
            var vm = view.lookupViewModel();
            // open popup
            var config = {
                xtype: 'classes-cards-card-edit',
                padding: 10,
                viewModel: {
                    data: {
                        objectTypeName: vm.get("objectTypeName"),
                        objectId: vm.get("objectId")
                    }
                },
                buttons: [{
                    ui: 'management-action',
                    itemId: 'detailsavebtn',
                    text: CMDBuildUI.locales.Locales.common.actions.save,
                    autoEl: {
                        'data-testid': 'relations-fieldset-editcard-save'
                    },
                    formBind: true,
                    handler: function (btn, event) {
                        CMDBuildUI.util.helper.FormHelper.startSavingForm();
                        var cancelBtn = this.up().down("#detailclosebtn");
                        btn.showSpinner = true;
                        CMDBuildUI.util.Utilities.disableFormButtons([btn, cancelBtn]);

                        popup.down("classes-cards-card-edit").getController().saveForm().then(function (record) {
                            Ext.GlobalEvents.fireEventArgs("updateMasterDetailStore", [vm.get("domain").getId()]);
                            Ext.GlobalEvents.fireEvent("updateRelationStore");
                            view.up("grid").updateRowWithExpader(record);
                            CMDBuildUI.util.helper.FormHelper.endSavingForm();
                            popup.destroy();
                        }).otherwise(function () {
                            CMDBuildUI.util.helper.FormHelper.endSavingForm();
                            CMDBuildUI.util.Utilities.enableFormButtons([btn, cancelBtn]);
                        });
                    }
                }, {
                    ui: 'secondary-action',
                    itemId: 'detailclosebtn',
                    text: CMDBuildUI.locales.Locales.common.actions.close,
                    autoEl: {
                        'data-testid': 'relations-fieldset-editcard-cancel'
                    },
                    handler: function (btn, event) {
                        popup.destroy();
                    }
                }],

                listeners: {
                    itemupdated: function () {
                        popup.close();
                        grid.getStore().load();
                    },
                    cancelupdating: function () {
                        popup.close();
                    }
                }
            };
            popup = CMDBuildUI.util.Utilities.openPopup(null, CMDBuildUI.locales.Locales.relations.editcard, config);
        },

        /**
         * 
         * @param {Ext.form.Panel} view 
         */
        onItemEditRelationCardClick: function (view) {
            var vm = view.lookupViewModel();
            this.loadRelationStore(vm.get('allRelations')).then(function (store) {
                var relationItem = store.findRecord('_destinationId', vm.get("theObject").getId());
                CMDBuildUI.view.relations.Utils.editRelation(relationItem, {
                    proxyurl: vm.get("relstore.proxyurl"),
                    objecttypename: vm.get("current.objectTypeName"),
                    objectid: vm.get("current.objectId")
                }).then(function () {
                    vm.get('records').load();
                    Ext.GlobalEvents.fireEventArgs("updateMasterDetailStore", [relationItem.get("_type")]);
                    Ext.GlobalEvents.fireEvent("updateRelationStore");
                });
            });
        },

        /**
         * 
         * @param {Ext.form.Panel} view 
         */
        onItemDeleteRelationCardClick: function (view) {
            var vm = view.lookupViewModel();
            this.loadRelationStore(vm.get('allRelations')).then(function (store) {
                var relationItem = store.findRecord('_destinationId', vm.get("theObject").getId());
                CMDBuildUI.view.relations.Utils.deleteRelation(relationItem).then(function () {
                    vm.get('records').load();
                    Ext.GlobalEvents.fireEventArgs("updateMasterDetailStore", [relationItem.get("_type")]);
                    Ext.GlobalEvents.fireEvent("updateRelationStore");
                });
            });
        },

        /**
         * 
         * @param {String} targettype 
         * @param {String} targettypename 
         * @param {Number} objectid 
         */
        redirectToItem: function (targettype, targettypename, objectid) {
            var path;
            switch (targettype) {
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                    path = Ext.String.format('classes/{0}/cards/{1}/view', targettypename, objectid);
                    break;
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                    path = Ext.String.format('processes/{0}/instances/{1}', targettypename, objectid);
                    break;
            }
            this.redirectTo(path);
        },

        /**
         * 
         * @param {Ext.form.Panel} view 
         */
        loadRelationStore: function (relationStore) {
            var deferred = new Ext.Deferred();
            if (!relationStore.isLoaded() || Ext.Object.isEmpty(relationStore.getAdvancedFilter().getAttributes())) {
                relationStore.getAdvancedFilter().addAttributeFilter('_type', 'equal', this.getViewModel().get('domain.name'));
                relationStore.load(function () {
                    deferred.resolve(relationStore);
                });
            } else {
                deferred.resolve(relationStore);
            }
            return deferred.promise;
        }
    }
});