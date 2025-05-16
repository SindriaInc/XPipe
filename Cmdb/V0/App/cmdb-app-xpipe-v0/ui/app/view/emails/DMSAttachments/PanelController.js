Ext.define('CMDBuildUI.view.emails.DMSAttachments.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.emails-dmsattachments-panel',
    control: {
        '#': {
            beforeRender: 'onBeforeRender'
        },
        '#comboclass': {
            change: 'onComboClassChange'
        },
        '#saveBtn': {
            click: 'onSaveBtn'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }

    },

    /**
     * @param {CMDBuildUI.view.emails.Edit.Panel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        vm.set('firstload', true);
        vm.bind({
            store: '{attributeslist}',
            objectTypeName: '{objectTypeName}'
        }, function (data) {
            if (data.store && data.objectTypeName) {
                view.down('#comboclass').setValue(data.objectTypeName);
            }
        });
    },

    /**
     * @param {Ext.form.field.ComboBox} combos
     * @param {String} newValue
     * @param {String} oldValue
     * @param {Object} eOpts
     * 
     */
    onComboClassChange: function (combo, newValue, oldValue, eOpts) {
        var comboClassSelection = combo.getSelection();
        if (comboClassSelection && newValue) {
            this.setContainerGrid(comboClassSelection.get('type'), newValue);
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtn: function (button, e, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        var attachPanel = this.getView(),
            attachmentStore = attachPanel.config.store,
            attachmentsgrid = attachPanel.down('#attachmentgrid'),
            attachmentsSelected = attachmentsgrid.getSelection(),
            cardsgrid = attachPanel.down('#cardsgrid'),
            cardSelected = cardsgrid.getSelection(),
            objectTypeName, objectId;

        if (!Ext.isEmpty(cardSelected)) {
            var cardSelect = cardSelected[0];
            objectTypeName = cardSelect.get('_type');
            objectId = cardSelect.getId();
        }

        attachmentsSelected.forEach(function (selatt) {
            if (attachmentStore.findRecord("name", selatt.get("name"))) {
                var w = Ext.create('Ext.window.Toast', {
                    title: CMDBuildUI.locales.Locales.notifier.warning,
                    html: CMDBuildUI.locales.Locales.emails.alredyexistfile,
                    iconCls: 'x-fa fa-exclamation-circle',
                    align: 'br',
                    alwaysOnTop: CMDBuildUI.util.Utilities._popupAlwaysOnTop++
                });
                w.show();
            } else {
                selatt.set('objectTypeName', objectTypeName);
                selatt.set('objectId', objectId);
                selatt.set('DMSAttachment', true);
                selatt.set('newAttachment', true);
                attachmentStore.add(selatt.getData());
            }
        });

        CMDBuildUI.util.helper.FormHelper.endSavingForm();
        attachPanel.up("panel").close();
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        this.getView().up("panel").close();
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchSubmit: function (field, trigger, eOpts) {
        var searchTerm = field.getValue();

        if (searchTerm) {
            var store = this.getView().down("#cardsgrid").getStore();
            store.getAdvancedFilter().addQueryFilter(searchTerm);
            store.load();
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
        var store = this.getView().down("#cardsgrid").getStore();
        store.getAdvancedFilter().clearQueryFilter();
        store.load();
        field.reset();
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

    privates: {
        setContainerGrid: function (typeSelected, newValue) {
            var view = this.getView(),
                vm = this.getViewModel(),
                classContainer = view.down("#classcontainer"),
                attachmentContainer = view.down('#attachmentcontainer'),
                preferences = CMDBuildUI.util.helper.UserPreferences.getGridPreferences(typeSelected, newValue),
                sorters = [],
                storetype, object;

            // clear containers
            classContainer.removeAll(true);
            attachmentContainer.removeAll(true);

            if (CMDBuildUI.util.helper.ModelHelper.objecttypes.klass == typeSelected) {
                storetype = 'classes-cards';
                object = CMDBuildUI.util.helper.ModelHelper.getClassFromName(newValue);
            } else if (CMDBuildUI.util.helper.ModelHelper.objecttypes.process == typeSelected) {
                storetype = 'processes-instances';
                object = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(newValue);
            }

            if (preferences && !Ext.isEmpty(preferences.defaultOrder)) {
                preferences.defaultOrder.forEach(function (o) {
                    sorters.push({
                        property: o.attribute,
                        direction: o.direction === "descending" ? "DESC" : 'ASC'
                    });
                });
            } else if (object && object.defaultOrder().getCount()) {
                object.defaultOrder().getRange().forEach(function (o) {
                    sorters.push({
                        property: o.get("attribute"),
                        direction: o.get("direction") === "descending" ? "DESC" : 'ASC'
                    });
                });
            } else if (typeSelected !== CMDBuildUI.util.helper.ModelHelper.objecttypes.process && !object.isSimpleClass()) {
                sorters.push({
                    property: 'Description'
                });
            }

            // get columns for selected type
            CMDBuildUI.util.helper.GridHelper.getColumnsForType(
                typeSelected,
                newValue
            ).then(function (columns) {
                CMDBuildUI.util.helper.ModelHelper.getModel(typeSelected, newValue).then(function (model) {

                    // define grid
                    var grid = classContainer.add({
                        xtype: "grid",
                        itemId: 'cardsgrid',
                        scrollable: true,
                        maxHeight: 250,
                        bind: {
                            store: '{cardss}'
                        },
                        viewModel: {
                            stores: {
                                cardss: {
                                    type: storetype,
                                    model: model.getName(),
                                    autoLoad: true,
                                    autoDestroy: true,
                                    proxy: {
                                        type: 'baseproxy',
                                        url: model.getProxy().getUrl()
                                    },
                                    sorters: sorters,
                                    listeners: {
                                        beforeload: function (store, operation, eOpts) {
                                            if (vm.get('objectType') == typeSelected && vm.get('objectTypeName') == newValue) {
                                                var selId = vm.get('objectId');
                                                if (selId) {
                                                    var extraparams = store.getProxy().getExtraParams();
                                                    extraparams.positionOf = selId;
                                                    extraparams.positionOf_goToPage = false;
                                                }
                                            } else {
                                                vm.set('firstload', false);
                                            }
                                            grid.reconfigure(null, columns);
                                        },
                                        load: function (store, record) {
                                            if (vm.get('firstload')) {
                                                vm.set('firstload', false);
                                                var selId = vm.get('objectId');
                                                if (selId) {
                                                    var metadata = store.getProxy().getReader().metaData,
                                                        posinfo = metadata.positions[selId];

                                                    if (!posinfo.pageOffset) {
                                                        grid.setSelection(store.getById(selId));
                                                    } else {
                                                        grid.ensureVisible(posinfo.positionInTable, {
                                                            focus: true,
                                                            select: true,
                                                            callback: function (success, record, node) {
                                                                grid.setSelection(record);
                                                                var extraparams = store.getProxy().getExtraParams();
                                                                delete extraparams.positionOf;
                                                                delete extraparams.positionOf_goToPage;
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        listeners: {
                            selectionChange: function (selection, record, eOpts) {
                                attachmentContainer.removeAll(true);
                                if (!Ext.isEmpty(record)) {
                                    var rec = record[0],
                                        cardId = rec.getId(),
                                        cardType = rec.get('_type'),
                                        proxyurl = CMDBuildUI.util.api.Classes.getAttachments(cardType, cardId);

                                    attachmentContainer.add({
                                        xtype: 'attachments-grid',
                                        itemId: 'attachmentgrid',
                                        viewModel: {
                                            stores: {
                                                attachments: {
                                                    type: 'attachments',
                                                    autoLoad: true,
                                                    autoDestroy: true,
                                                    proxy: {
                                                        url: proxyurl,
                                                        type: 'baseproxy',
                                                        extraParams: {
                                                            detailed: true
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                });
            });
        }
    }
});