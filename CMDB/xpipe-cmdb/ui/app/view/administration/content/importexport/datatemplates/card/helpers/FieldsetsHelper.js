Ext.define('CMDBuildUI.view.administration.content.importexport.datatemplates.card.helpers.FieldsetsHelper', {
    singleton: true,

    requires: ['CMDBuildUI.util.administration.helper.FormHelper', 'Ext.grid.plugin.DragDrop'],

    getGeneralPropertiesFieldset: function () {
        return {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: "fieldset",
            layout: 'column',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
            },

            items: [
                this.createContainer([
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCodeInput({
                        code: {
                            allowBlank: false,
                            bind: {
                                value: '{theGateTemplate.code}',
                                disabled: '{actions.edit}'
                            }
                        }
                    }, true, '[name="description"]'),

                    CMDBuildUI.util.administration.helper.FieldsHelper.getDescriptionInput({
                        description: {
                            allowBlank: false,
                            bind: {
                                value: '{theGateTemplate.description}'
                            }
                        }
                    })
                ]),
                this.createContainer([
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('targetType', {
                        targetType: {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.applyon,
                            allowBlank: false,
                            forceSelection: true,
                            bind: {
                                value: '{theGateTemplate.targetType}',
                                store: '{targetTypesStore}'
                            },
                            listeners: {
                                change: function (input, newVal, oldVal) {
                                    // disable type input if newValue === 'view'
                                    var form = input.up('form');
                                    if (form) {
                                        var typeInput = form.down('#type_input');
                                        if (typeInput) {
                                            var isViewObject = newVal === CMDBuildUI.model.administration.MenuItem.types.view;
                                            var isProcessObject = newVal === CMDBuildUI.util.helper.ModelHelper.objecttypes.process;
                                            typeInput.setDisabled(isViewObject || isProcessObject);
                                            if (isViewObject || isProcessObject) {
                                                // set type import
                                                typeInput.setValue(CMDBuildUI.model.importexports.Template.types['export']);
                                            }
                                        }
                                        var targetNameField = input.up('fieldset').down('[name="targetName"]');
                                        var targetNameFieldContainer = targetNameField.up('fieldcontainer');
                                        var vm = this.lookupViewModel();
                                        if (oldVal) {
                                            vm.resetAllAttributesStores();
                                        }
                                        if (newVal && (input.getXType() === 'combobox' || input.getXType() === 'combo')) {
                                            if (targetNameField.getValue() !== '' && oldVal) {
                                                targetNameField.setValue('');
                                            }
                                            if (input.getSelectedRecord()) {
                                                targetNameFieldContainer.setFieldLabel(input.getSelectedRecord().get('label'));
                                            }
                                        }


                                    }
                                }
                            }
                        }
                    }, true),

                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('targetName', {
                        targetName: {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.classdomain,
                            allowBlank: false,
                            displayField: 'description',
                            valueField: 'name',
                            forceSelection: true,
                            bind: {
                                store: '{allClassesOrDomainsOrViews}',
                                value: '{theGateTemplate.targetName}'
                            },
                            listeners: {
                                change: function (input, newValue, oldValue) {
                                    var vm = this.lookupViewModel();
                                    var obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(newValue, vm.get('theGateTemplate.targetType'));
                                    if (obj) {
                                        if (oldValue && oldValue !== obj.get('name')) {
                                            vm.resetAllAttributesStores();
                                        }
                                        var allowedAttributes = ['Notes'];
                                        var allowTenantAttribute = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.multitenant.enabled);
                                        if (allowTenantAttribute) {
                                            allowedAttributes.push('IdTenant');
                                        }
                                        obj.getAttributes(true).then(
                                            function (attributeStore) {
                                                var attributes = Ext.Array.filter(attributeStore.getRange(), function (item) {
                                                    return item.get('active') && item.canAdminShow(allowedAttributes);
                                                });
                                                if (!vm.destroyed) {
                                                    vm.set('rawAttributeStore', attributeStore);
                                                    vm.set('allClassOrDomainAttributes', attributes);
                                                    vm.addDefaultDomainAttributes(obj);
                                                }
                                            });
                                    }
                                }
                            },
                            displayfield: {
                                xtype: 'displayfieldwithtriggers',
                                bind: {
                                    value: '{theGateTemplate.targetName}',
                                    hideTrigger: '{!theGateTemplate.targetName}'
                                },
                                triggers: {
                                    open: {
                                        cls: 'x-fa fa-external-link',
                                        handler: function (f, trigger, eOpts) {
                                            var url,
                                                targetType = this.lookupViewModel().get('theGateTemplate.targetType'),
                                                target = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(f.getValue());
                                            switch (targetType) {
                                                case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                                                    url = CMDBuildUI.util.administration.helper.ApiHelper.client.getClassUrl(target.get('name'));
                                                    break;
                                                case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                                                    url = CMDBuildUI.util.administration.helper.ApiHelper.client.getProcessUrl(target.get('name'));
                                                    break;
                                                case CMDBuildUI.util.helper.ModelHelper.objecttypes.view:
                                                    url = CMDBuildUI.util.administration.helper.ApiHelper.client.getJoinViewUrl(f.getValue());
                                                    break;
                                                case CMDBuildUI.util.helper.ModelHelper.objecttypes.domain:
                                                    url = CMDBuildUI.util.administration.helper.ApiHelper.client.getDomainUrl(target.get('name'));
                                                    break;
                                                default:
                                                    return;
                                            }
                                            CMDBuildUI.util.Utilities.closeAllPopups();
                                            CMDBuildUI.util.Utilities.redirectTo(url);
                                        }
                                    }
                                }
                            }
                        }
                    }, true)
                ]),
                this.createContainer([
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('type', {
                        type: {
                            fieldcontainer: {
                                hidden: true,
                                fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.type,
                                bind: {
                                    hidden: '{typeHidden}'
                                }
                            },
                            forceSelection: true,
                            allowBlank: false,
                            bind: {
                                value: '{theGateTemplate.type}',
                                store: '{templateTypesStore}'
                            }
                        }
                    }),

                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('fileFormat', {
                        fileFormat: {
                            allowBlank: true,
                            forceSelection: true,
                            bind: {
                                value: '{theGateTemplate.fileFormat}',
                                store: '{fileTypesStore}'
                            },
                            listeners: {
                                change: function (combo, newValue, oldValue) {
                                    var panel = combo.up('panel');
                                    var attributeCombo = panel.down('#selectAttributeForGrid');
                                    if (attributeCombo) {
                                        attributeCombo.setStoreFilter();
                                    }
                                }
                            },
                            fieldcontainer: {
                                fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.fileformat,
                                bind: {
                                    hidden: '{ fileFormatHidden || "import_database" === theGateTemplate.type ||  "import_ifc" === theGateTemplate.type }'
                                },
                                hidden: true,
                                listeners: {
                                    hide: function (component, eOpts) {
                                        var input = component.down('#fileFormat_input');
                                        input.setValue('');
                                        CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, true, input.up('form'));
                                    },
                                    show: function (component, eOpts) {
                                        var input = component.down('#fileFormat_input');
                                        CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, false, input.up('form'));
                                    }
                                }
                            }
                        }
                    })
                ]),
                this.createContainer([
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('csv_separator', {
                        csv_separator: {
                            fieldcontainer: {
                                bind: {
                                    hidden: '{!isCsv}'
                                },
                                listeners: {
                                    hide: function (component, eOpts) {
                                        var input = component.down('#csv_separator_input');
                                        input.setValue('');
                                        CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, true, input.up('form'));
                                    },
                                    show: function (component, eOpts) {
                                        var input = component.down('#csv_separator_input');
                                        CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, false, input.up('form'));
                                    }
                                }
                            },
                            forceSelection: true,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.csvseparator,
                            allowBlank: false,
                            bind: {
                                value: '{theGateTemplate.csv_separator}',
                                store: '{csvSeparatorsStore}'
                            }
                        }
                    }),
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('charset', {
                        charset: {
                            fieldcontainer: {
                                bind: {
                                    hidden: '{!isCsv}'
                                },
                                fieldLabel: CMDBuildUI.locales.Locales.main.preferences.preferredfilecharset,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.preferredfilecharset'
                                },
                                listeners: {
                                    hide: function (component, eOpts) {
                                        var input = component.down('#charset_input');
                                        input.setValue(null);
                                    }
                                }
                            },
                            combofield: {
                                forceSelection: true,
                                bind: {
                                    store: '{csvCharsetStore}',
                                    value: '{theGateTemplate.charset}'
                                },
                                emptyText: CMDBuildUI.locales.Locales.administration.importexport.texts['default'],
                                localized: {
                                    emptyText: 'CMDBuildUI.locales.Locales.administration.importexport.texts.default'
                                }
                            },
                            displayfield: {
                                bind: {
                                    value: '{theGateTemplate._charset_description}'
                                },
                                renderer: function (value) {
                                    if (!value) {
                                        var me = this;
                                        var vm = me.lookupViewModel();
                                        vm.bind({
                                            bindTo: {
                                                store: '{csvCharsetStore}',
                                                value: '{theGateTemplate.charset}'
                                            },
                                            single: true
                                        }, function (data) {
                                            if (!Ext.isEmpty(data.value) && data.store) {
                                                var record = data.store.findRecord('_id', data.value);
                                                vm.set('theGateTemplate._charset_description', record.get('description'));
                                            } else {
                                                vm.set('theGateTemplate._charset_description', CMDBuildUI.locales.Locales.administration.importexport.texts['default']);
                                            }
                                        });
                                    }
                                    return value;
                                }
                            },

                            displayField: 'description',
                            valueField: '_id',
                            queryMode: 'local',
                            forceSelection: true,
                            anyMatch: true,
                            typeAhead: true,
                            autoSelect: true,
                            name: 'charset',
                            triggers: {
                                clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                            }
                        }
                    })
                ]),
                /**
                 * not necessary from 26/06 by F.B.
                 */
                // this.createContainer([
                //     CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('firstCol', {
                //         firstCol: {
                //             fieldcontainer: {
                //                 viewModel: {},
                //                 bind: {
                //                     hidden: '{!isExcell}'
                //                 }
                //             },

                //             fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.firstcolumnnumber,
                //             allowBlank: false,
                //             minValue: 1,
                //             bind: {
                //                 value: '{theGateTemplate.firstCol}'
                //             }
                //         }
                //     })
                // ]),
                this.createContainer([
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('headerRow', {
                        headerRow: {
                            fieldcontainer: {
                                viewModel: {},
                                bind: {
                                    hidden: '{!isExcell}'
                                }
                            },

                            fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.headerrownumber,
                            allowBlank: false,
                            minValue: 0,
                            bind: {
                                value: '{theGateTemplate.headerRow}'
                            }
                        }
                    }),

                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('dataRow', {
                        dataRow: {
                            fieldcontainer: {
                                bind: {
                                    hidden: '{!isExcell}'
                                }
                            },

                            fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.datarownumber,
                            allowBlank: false,
                            minValue: 1,
                            bind: {
                                value: '{theGateTemplate.dataRow}'
                            }
                        }
                    })
                ]),
                this.createContainer([
                    // source                    
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput('source', {
                        source: {
                            fieldcontainer: {
                                hidden: true,
                                bind: {
                                    hidden: '{!isImportWithSource}'
                                },
                                listeners: {
                                    hide: function (component, eOpts) {
                                        var input = component.down('#source_input');
                                        input.setValue('');
                                        CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, true, input.up('form'));
                                    },
                                    show: function (component, eOpts) {
                                        var input = component.down('#source_input');
                                        CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, false, input.up('form'));
                                    }
                                }
                            },

                            // fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.source,
                            // localized: {
                            //     fieldLabel: 'CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.source'
                            // },
                            forceSelection: true,
                            allowBlank: true,
                            bind: {
                                value: '{theGateTemplate.source}',
                                fieldLabel: '{sourceInputLabel}'
                            }
                        }
                    })
                ]),
                /**
                 * not necessary from 26/06 by F.B.
                 */
                // this.createContainer([
                //     CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput({
                //         useHeader: {
                //             fieldcontainer: {
                //                 fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.useheader,
                //                 localized: {
                //                     fieldLabel: 'CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.useheader'
                //                 }
                //             },
                //             bind: {
                //                 value: '{theGateTemplate.useHeader}'
                //             }
                //         }
                //     }, 'useHeader'),
                //     CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput({
                //         ignoreColumnOrder: {
                //             fieldcontainer: {
                //                 fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.ignorecolumn,
                //                 localized: {
                //                     fieldLabel: 'CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.ignorecolumn'
                //                 }
                //             },
                //             bind: {
                //                 value: '{theGateTemplate.ignoreColumnOrder}'
                //             }
                //         }
                //     }, 'ignoreColumnOrder')
                // ]),

                this.createContainer([
                    CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput({
                        active: {
                            bind: {
                                value: '{theGateTemplate.active}'
                            }
                        }
                    })
                ])
            ]
        };
    },

    getAttributesFieldset: function () {
        return {
            ui: 'administration-formpagination',
            xtype: "fieldset",
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.attributes.attributes,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.attributes.attributes'
            },
            items: [{
                xtype: 'grid',
                viewModel: {},
                headerBorders: false,
                border: false,
                bodyBorder: false,
                rowLines: false,
                sealedColumns: false,
                sortableColumns: false,
                enableColumnHide: false,
                enableColumnMove: false,
                enableColumnResize: false,
                cls: 'administration-reorder-grid',
                itemId: 'importExportAttributeGrid',
                selModel: {
                    pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                },
                viewConfig: {
                    markDirty: false
                },
                plugins: {
                    ptype: 'actionColumnRowEditing',
                    id: 'actionColumnRowEditing',
                    hiddenColumnsOnEdit: ['actionColumnEdit', 'actionColumnMoveUp', 'actionColumnMoveDown', 'actionColumnCancel'],
                    clicksToEdit: 10,
                    buttonsUi: 'button-like-tool',
                    errorSummary: false,
                    placeholdersButtons: [{
                        iconCls: 'x-fa fa-ellipsis-h',
                        disabled: true,
                        xtype: 'button',
                        minWidth: 30,
                        maxWidth: 30,
                        ui: 'button-like-tool'
                    }, {
                        iconCls: 'x-fa fa-ellipsis-h',
                        disabled: true,
                        xtype: 'button',
                        minWidth: 30,
                        maxWidth: 30,
                        ui: 'button-like-tool'
                    }]
                },
                controller: {
                    control: {
                        '#': {
                            afterrender: function () {
                                if (this.getView().lookupViewModel().get('theGateTemplate.fileFormat') === 'ifc') {
                                    this.getView().headerCt.getHeaderAtIndex(1).setText(CMDBuildUI.locales.Locales.administration.gates.ifcproperty);
                                }
                            },
                            edit: function (editor, context, eOpts) {

                                context.record.set('columnName', editor.editor.items.items[1].getValue());
                                context.record.set('mode', editor.editor.items.items[2].getValue());
                                if (editor.editor.items.items[3]) {
                                    context.record.set('default', editor.editor.items.items[3].getValue());
                                }
                            },
                            beforeedit: function (editor, context, eOpts) {
                                if (editor.view.lookupViewModel().get('actions.view')) {
                                    return false;
                                }
                                var comboStore = editor.view.lookupViewModel().get('attributeModesReferenceStore');
                                comboStore.clearFilter();
                                switch (context.record.get('attribute')) {
                                    case 'IdObj1':
                                    case 'IdObj2':
                                        comboStore.addFilter([function (item) {
                                            return item.get('value') !== 'default';
                                        }]);
                                        editor.editor.items.items[2].setHideTrigger(false);
                                        editor.editor.items.items[2].setDisabled(false);
                                        return true;

                                    default:
                                        var allAttributesStore = editor.view.lookupViewModel().get('allClassOrDomainsAtributes');
                                        allAttributesStore.rejectChanges();
                                        var attribute = allAttributesStore.findRecord('name', context.record.get('attribute'), false, false, true, true);
                                        if (attribute) {
                                            switch (attribute.get('type')) {
                                                case 'lookup':
                                                case 'lookupArray':
                                                case 'reference':
                                                case 'foreignKey':
                                                    editor.editor.items.items[2].setHideTrigger(false);
                                                    editor.editor.items.items[2].setEmptyText(Ext.String.format('{0} *', CMDBuildUI.locales.Locales.administration.importexport.texts.selectmode));
                                                    editor.editor.items.items[2].setDisabled(false);
                                                    break;
                                                default:
                                                    editor.editor.items.items[2].setHideTrigger(true);
                                                    editor.editor.items.items[2].setEmptyText('');
                                                    editor.editor.items.items[2].setDisabled(true);
                                                    break;
                                            }
                                            if (attribute.get('_id') === 'IdTenant') {
                                                editor.editor.items.items[2].setHideTrigger(false);
                                                editor.editor.items.items[2].setEmptyText(Ext.String.format('{0} *', CMDBuildUI.locales.Locales.administration.importexport.texts.selectmode));
                                                editor.editor.items.items[2].setDisabled(false);
                                            }
                                        }
                                        return true;
                                }
                            }
                        }
                    }
                },
                columnWidth: 1,
                autoEl: {
                    'data-testid': 'administration-content-importexport-datatemplates-grid'
                },

                forceFit: true,
                loadMask: true,

                labelWidth: "auto",
                bind: {
                    store: '{allSelectedAttributesStore}',
                    hidden: '{isAttributeGridHidden}'
                },
                columns: [{
                    flex: 1,
                    text: CMDBuildUI.locales.Locales.administration.attributes.attribute,
                    dataIndex: '_attribute_description',
                    align: 'left',
                    editor: {
                        xtype: 'displayfield',
                        height: 19,
                        minHeight: 19,
                        maxHeight: 19,
                        padding: 0,
                        ui: 'reordergrid-editor-combo'
                    },
                    renderer: function (value, cell, record) {
                        var vm = this.lookupViewModel();

                        function recoveryDescription(_vm, _value, _record) {
                            _vm.bind({
                                bindTo: {
                                    rawAttributeStore: '{rawAttributeStore}'
                                },
                                sigle: true
                            }, function (data) {
                                if (!_value && data.rawAttributeStore) {
                                    var attribute = data.rawAttributeStore.findRecord('name', _record.get('attribute'), false, false, true, true);
                                    if (attribute) {
                                        switch (attribute.get('type')) {
                                            case CMDBuildUI.model.Attribute.types.date:
                                                vm.set('dataFormatHidden', false);
                                                vm.set('dataFormatDateHidden', false);
                                                break;
                                            case CMDBuildUI.model.Attribute.types.time:
                                                vm.set('dataFormatHidden', false);
                                                vm.set('dataFormatTimeHidden', false);
                                                break;
                                            case CMDBuildUI.model.Attribute.types.dateTime:
                                                vm.set('dataFormatHidden', false);
                                                vm.set('dataFormatTimeHidden', false);
                                                vm.set('dataFormatDateHidden', false);
                                                vm.set('dataFormatDateTimeHidden', false);
                                                break;
                                            case CMDBuildUI.model.Attribute.types.double:
                                            case CMDBuildUI.model.Attribute.types.decimal:
                                                vm.set('dataFormatHidden', false);
                                                vm.set('dataFormatDecimalHidden', false);
                                                break;
                                        }
                                        _record.set('_attribute_description', attribute.get('description'));
                                    } else {
                                        _record.set('_attribute_description', _record.get('attribute'));
                                    }
                                }
                            });
                        }
                        recoveryDescription(vm, value, record);

                        return value;
                    }
                }, {
                    text: CMDBuildUI.locales.Locales.administration.importexport.texts.columnname,
                    flex: 1,
                    dataIndex: 'columnName',
                    align: 'left',
                    editor: {
                        xtype: 'textfield',
                        height: 19,
                        minHeight: 19,
                        maxHeight: 19,
                        padding: 0,
                        ui: 'reordergrid-editor-combo'
                    }
                }, {
                    text: CMDBuildUI.locales.Locales.administration.importexport.texts.mode,
                    flex: 1,
                    dataIndex: 'mode',
                    align: 'left',
                    editor: {
                        xtype: 'combo',
                        displayField: 'label',
                        valueField: 'value',
                        queryMode: 'local',
                        emptyText: CMDBuildUI.locales.Locales.administration.importexport.texts.selectmode,
                        editable: false,
                        height: 19,
                        minHeight: 19,
                        maxHeight: 19,
                        padding: 0,
                        ui: 'reordergrid-editor-combo',
                        forceSelection: true,
                        allowBlank: false,
                        bind: {
                            store: '{attributeModesReferenceStore}'
                        },
                        listeners: {
                            beforerender: function (combo) {
                                var grid = this.up('grid');
                                var vm = grid.lookupViewModel();
                                var record = grid.editingPlugin.context.record;
                                var attribute;
                                var allAttributes = vm.get('allClassOrDomainsAtributes');
                                if (allAttributes && allAttributes.getData().length) {
                                    attribute = allAttributes.findRecord('_id', record.get('attribute'), false, false, true, true);
                                }
                                if (attribute && ['lookup', 'lookupArray', 'reference', 'foreignKey'].indexOf(attribute.get('type')) > -1 || attribute.get('_id') === 'IdTenant') {
                                    combo.setDisabled(false);
                                    combo.setHideTrigger(false);
                                } else if (['IdObj1', 'IdObj2'].indexOf(record.get('attribute')) > -1) {
                                    // cell.style = 'color:#83878b!important; border: 1px solid #cf4c35';
                                    combo.setDisabled(false);
                                } else {
                                    combo.setDisabled(true);
                                }
                            }
                        }
                    },
                    renderer: function (value, cell, record, rowIndex, colIndex, store, grid) {
                        if (value === 'default') {
                            return CMDBuildUI.locales.Locales.administration.common.labels.default;
                        }
                        var vm = this.lookupViewModel();
                        var attribute;
                        var allAttributes = this.lookupViewModel().get('allClassOrDomainsAtributes');
                        if (allAttributes && allAttributes.getData().length) {
                            attribute = allAttributes.findRecord('_id', record.get('attribute'), false, false, true, true);
                        }
                        if (!value && attribute && ['lookup', 'lookupArray', 'reference', 'foreignKey'].indexOf(attribute.get('type')) > -1) {
                            return CMDBuildUI.locales.Locales.administration.importexport.texts.selectmode;
                        } else if (!value && ['IdObj1', 'IdObj2'].indexOf(record.get('attribute')) > -1) {
                            return CMDBuildUI.locales.Locales.administration.importexport.texts.selectmode;
                        }
                        var attributeModesStore = vm.get('attributeModesReferenceStore');
                        if (attributeModesStore) {
                            var mode = attributeModesStore.findRecord('value', value, false, false, true, true);
                            return mode && mode.get('label');
                        }
                        return value;
                    }
                }, {
                    text: CMDBuildUI.locales.Locales.administration.importexport.texts.defaultvalue,
                    flex: 1,
                    dataIndex: 'default',
                    align: 'left',
                    editor: {
                        xtype: 'textfield',
                        height: 19,
                        minHeight: 19,
                        maxHeight: 19,
                        ui: 'reordergrid-editor-combo',
                        bind: {
                            disabled: '{!isImport}'
                        }
                    },
                    renderer: Ext.util.Format.htmlEncode,
                    bind: {
                        disabled: '{!isImport}'
                    }
                }, {
                    xtype: 'actioncolumn',
                    itemId: 'actionColumnEdit',
                    bind: {
                        hidden: '{actions.view}'
                    },
                    width: 30,
                    minWidth: 30, // width property not works. Use minWidth.
                    maxWidth: 30,
                    align: 'center',
                    items: [{
                        handler: function (grid, rowIndex, colIndex, item, e, record) {
                            grid.editingPlugin.startEdit(record, 1);
                        },
                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                            return CMDBuildUI.locales.Locales.administration.common.actions.edit;
                        },
                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                            if (record.get('editing')) {
                                return 'x-fa fa-check';
                            }
                            return 'x-fa fa-pencil';
                        }
                    }]
                }, {
                    xtype: 'actioncolumn',
                    itemId: 'actionColumnMoveUp',
                    bind: {
                        hidden: '{actions.view}'
                    },
                    width: 30,
                    minWidth: 30, // width property not works. Use minWidth.
                    maxWidth: 30,
                    align: 'center',
                    items: [{
                        iconCls: 'x-fa fa-arrow-up',
                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                            return CMDBuildUI.locales.Locales.administration.common.actions.moveup;
                        },
                        handler: function (grid, rowIndex, colIndex) {
                            Ext.suspendLayouts();
                            var store = grid.getStore();
                            var record = store.getAt(rowIndex);
                            var previousRecord = store.getAt(rowIndex - 1);

                            rowIndex--;
                            if (!record || rowIndex < 0) {
                                return;
                            }
                            previousRecord.set('index', rowIndex + 1);
                            record.set('index', rowIndex);

                            try {
                                store.sort('index', 'ASC');
                                grid.refresh();
                                Ext.resumeLayouts();
                            } catch (e) {

                            }
                        },
                        isDisabled: function (view, rowIndex, colIndex, item, record) {
                            if (!record.get('editing')) {
                                rowIndex = rowIndex == null ? view.getStore().findExact('id', record.get('id')) : rowIndex;
                                return rowIndex == 0;
                            } else {
                                return true;
                            }
                        },
                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                            if (record.get('editing')) {
                                return 'x-fa fa-ellipsis-h';
                            }
                            return 'x-fa fa-arrow-up';
                        }
                    }]
                }, {
                    xtype: 'actioncolumn',
                    itemId: 'actionColumnMoveDown',
                    bind: {
                        hidden: '{actions.view}'
                    },
                    width: 30,
                    minWidth: 30, // width property not works. Use minWidth.
                    maxWidth: 30,
                    align: 'center',
                    items: [{
                        iconCls: 'x-fa fa-arrow-down',
                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                            return CMDBuildUI.locales.Locales.administration.common.actions.movedown;
                        },
                        handler: function (grid, rowIndex, colIndex) {
                            Ext.suspendLayouts();
                            var store = grid.getStore();
                            var record = store.getAt(rowIndex);
                            var previousRecord = store.getAt(rowIndex + 1);

                            rowIndex++;
                            if (!record || rowIndex >= store.getCount()) {
                                return;
                            }

                            previousRecord.set('index', rowIndex - 1);
                            record.set('index', rowIndex);
                            try {
                                store.sort('index', 'ASC');
                                grid.refresh();
                                Ext.resumeLayouts();
                            } catch (e) {

                            }
                        },
                        isDisabled: function (view, rowIndex, colIndex, item, record) {
                            if (!record.get('editing')) {
                                rowIndex = rowIndex == null ? view.getStore().findExact('id', record.get('id')) : rowIndex;
                                return rowIndex >= view.store.getCount() - 1;
                            } else {
                                return true;
                            }

                        },
                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                            if (record.get('editing')) {
                                return 'x-fa fa-ellipsis-h';
                            }
                            return 'x-fa  fa-arrow-down';
                        }
                    }]
                }, {
                    xtype: 'actioncolumn',
                    itemId: 'actionColumnCancel',
                    bind: {
                        hidden: '{actions.view}'
                    },
                    width: 30,
                    minWidth: 30, // width property not works. Use minWidth.
                    maxWidth: 30,
                    align: 'center',
                    items: [{
                        iconCls: 'x-fa fa-times',
                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                            return CMDBuildUI.locales.Locales.administration.common.actions.remove;
                        },
                        handler: function (grid, rowIndex, colIndex) {

                            var vm = grid.lookupViewModel();
                            var store = vm.get('allSelectedAttributesStore');
                            var record = store.getAt(rowIndex);
                            store.remove(record);
                            vm.filterFreeAttributes();
                            var importKeyAttributes = vm.get('theGateTemplate._importKeyAttribute');
                            if (importKeyAttributes.length) {
                                vm.set('theGateTemplate._importKeyAttribute', Ext.Array.remove(importKeyAttributes, record.get('attribute')));
                            }
                            this.up('fieldset').down('#selectAttributeForGrid').setStoreFilter();
                            grid.refresh();
                        },
                        isDisabled: function (view, rowIndex, colIndex, item, record) {
                            return ['IdObj1', 'IdObj2'].indexOf(record.get('attribute')) > -1 || record.get('editing');
                        },
                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                            metadata.value = Ext.String.insert(metadata.value, Ext.String.format(' data-testid="administration-importexport-attribute-removeBtn-{0}"', rowIndex), -7);
                            if (record.get('editing')) {
                                return 'x-fa fa-ellipsis-h';
                            }
                            return 'x-fa  fa-times';
                        }
                    }]
                }]
            }, {
                margin: '20 0 20 0',
                xtype: 'grid',
                viewModel: {},
                headerBorders: false,
                border: false,
                bodyBorder: false,
                rowLines: false,
                sealedColumns: false,
                sortableColumns: false,
                enableColumnHide: false,
                enableColumnMove: false,
                enableColumnResize: false,
                cls: 'administration-reorder-grid',
                itemId: 'importExportAttributeGridNew',
                selModel: {
                    pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                },
                layout: 'hbox',
                autoEl: {
                    'data-testid': 'administration-content-importexport-datatemplates-grid-newrecord'
                },

                forceFit: true,
                loadMask: true,

                labelWidth: "auto",
                bind: {
                    store: '{newSelectedAttributesStore}',
                    hidden: '{isAttributeGridNewHidden}'
                },

                columns: [{
                    xtype: 'widgetcolumn',
                    dataIndex: 'attribute',
                    align: 'left',
                    flex: 1,
                    widget: {
                        xtype: 'combo',
                        queryMode: 'local',
                        typeAhead: true,
                        editable: true,
                        forceSelection: true,
                        emptyText: CMDBuildUI.locales.Locales.administration.importexport.texts.selectanattribute,
                        itemId: 'selectAttributeForGrid',
                        displayField: 'description',
                        valueField: 'name',
                        bind: {
                            store: '{allClassOrDomainsAtributesFiltered}'
                        },
                        autoEl: {
                            'data-testid': 'administration-importexport-attribute-name'
                        },
                        setStoreFilter: function () {
                            var me = this;
                            var vm = me.lookupViewModel();
                            var allAttrStore = vm.get('allSelectedAttributesStore');
                            if (me.getStore() && me.getStore().source) {
                                me.getStore().source.rejectChanges();
                                me.getStore().clearFilter();
                                if (allAttrStore.getRange()) {
                                    var allowedAttributes = ['Notes'];
                                    var allowTenantAttribute = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.multitenant.enabled);
                                    if (allowTenantAttribute) {
                                        allowedAttributes.push('IdTenant');
                                    }
                                    me.getStore().addFilter(function (item) {
                                        var isSelected = allAttrStore.findRecord('attribute', item.get('name'), false, false, true, true);
                                        if (isSelected) {
                                            vm.getParent().getParent().manageDataFormatFieldset(item);
                                        }

                                        // some system attributes do not have a description
                                        // set the description equal to the name
                                        if (!item.get('description')) {
                                            item.set('description', item.get('name'));
                                        }

                                        var notAllowedTypes = [CMDBuildUI.model.Attribute.types.file, CMDBuildUI.model.Attribute.types.formula];
                                        var isAttributeTypeAllowed = notAllowedTypes.indexOf(item.get('type')) === -1;
                                        // if template type is not `import` we need to exclude password fields
                                        if (vm.get('theGateTemplate.type') !== CMDBuildUI.model.importexports.Template.types.import && item.get('password')) {
                                            return false;
                                        }

                                        return item.canAdminShow(allowedAttributes) && !isSelected && isAttributeTypeAllowed;

                                    });
                                }
                            }
                        },
                        listeners: {
                            afterrender: function (input) {
                                this.lookupViewModel().bind({
                                    bindTo: {
                                        store: '{allClassOrDomainsAtributesFiltered}'
                                    },
                                    single: true
                                }, function () {
                                    input.setStoreFilter();
                                });
                            },
                            change: function (combo, newValue, oldValue) {
                                var grid = combo.up('grid');
                                var vm = combo.lookupViewModel();
                                var modeInput = combo.up('grid').down('#newComboMode');
                                var columnName = combo.up('grid').down('#newAttributeColumnName');
                                var modeStore = vm.get('attributeModesReferenceStore');
                                modeStore.clearFilter();

                                modeInput.reset();
                                var allAttributes = grid.up('form').getViewModel().get('allClassOrDomainsAtributes');
                                if (allAttributes && allAttributes.getData().length) {
                                    var attribute = allAttributes.findRecord('_id', newValue, false, false, true, true);
                                    if (attribute) {
                                        columnName.setValue(attribute.get('description'));
                                        if (['lookup', 'lookupArray', 'reference', 'foreignKey'].indexOf(attribute.get('type')) > -1 || attribute.get('_id') === 'IdTenant') {
                                            modeInput.style = 'color:#83878b!important; border: 1px solid #cf4c35';
                                            modeInput.allowBlank = false;
                                            modeInput.reset();
                                            modeInput.markInvalid('Required');
                                            modeInput.setHideTrigger(false);
                                            modeInput.setEmptyText(Ext.String.format('{0} *', CMDBuildUI.locales.Locales.administration.importexport.texts.selectmode));
                                            modeInput.enable();
                                        } else {

                                            modeInput.allowBlank = true;
                                            modeInput.style = 'border: 0px solid!important';
                                            modeInput.setHideTrigger(true);
                                            modeInput.setEmptyText('');
                                            modeInput.disable();
                                        }
                                    }
                                } else if (['IdObj1', 'IdObj2'].indexOf(newValue) > -1) {
                                    modeInput.style = 'color:#83878b!important; border: 1px solid #cf4c35';
                                    modeInput.allowBlank = false;
                                    modeInput.markInvalid('Required');
                                    modeInput.setEmptyText(Ext.String.format('{0} *', CMDBuildUI.locales.Locales.administration.importexport.texts.selectmode));

                                    modeInput.setHideTrigger(false);
                                    modeInput.enable();
                                } else {
                                    // modeInput.style = 'color:#83878b!important; border: 1px solid #d0d0d0';
                                    modeInput.allowBlank = true;
                                    modeInput.setEmptyText('');
                                    modeInput.style = 'border: 0px solid!important';
                                    modeInput.setHideTrigger(true);
                                    modeInput.disable();
                                }
                            }
                        },
                        height: 19,
                        minHeight: 19,
                        maxHeight: 19,
                        padding: 0,
                        ui: 'reordergrid-editor-combo'
                    }
                }, {
                    xtype: 'widgetcolumn',
                    dataIndex: 'columnName',
                    align: 'left',
                    flex: 1,
                    widget: {
                        xtype: 'textfield',
                        itemId: 'newAttributeColumnName',
                        height: 19,
                        minHeight: 19,
                        maxHeight: 19,
                        padding: 0,
                        ui: 'reordergrid-editor-combo',
                        autoEl: {
                            'data-testid': 'administration-importexport-attribute-columnname'
                        }
                    }
                }, {
                    xtype: 'widgetcolumn',
                    dataIndex: 'mode',
                    align: 'left',
                    flex: 1,

                    widget: {
                        xtype: 'combo',
                        displayField: 'label',
                        valueField: 'value',
                        queryMode: 'local',
                        itemId: 'newComboMode',
                        typeAhead: true,
                        editable: true,
                        height: 19,
                        minHeight: 19,
                        maxHeight: 19,
                        padding: 0,
                        ui: 'reordergrid-editor-combo',
                        hideTrigger: true,
                        forceSelection: true,
                        allowBlank: false,
                        disabled: true,
                        disabledCls: 'x-item-disabled',
                        bind: {
                            store: '{attributeModesReferenceStore}'
                        },
                        autoEl: {
                            'data-testid': 'administration-importexport-attribute-mode'
                        }
                    }
                }, {
                    xtype: 'widgetcolumn',
                    dataIndex: 'default',
                    align: 'left',
                    flex: 1,

                    widget: {
                        xtype: 'textfield',
                        height: 19,
                        minHeight: 19,
                        maxHeight: 19,
                        itemId: 'newAttributeDefaultValue',
                        ui: 'reordergrid-editor-combo',
                        bind: {
                            disabled: '{!isImport}'
                        },
                        autoEl: {
                            'data-testid': 'administration-importexport-attribute-defaultvalue'
                        }
                    },
                    renderer: Ext.util.Format.htmlEncode,
                    bind: {
                        disabled: '{!isImport}'
                    }
                }, {
                    xtype: 'actioncolumn',
                    itemId: 'actionColumnEditNew',
                    // hidden: true,
                    width: 30,
                    minWidth: 30, // width property not works. Use minWidth.
                    maxWidth: 30,
                    align: 'center',
                    items: [{
                        iconCls: 'x-fa fa-ellipsis-h',
                        disabled: true
                    }]
                }, {
                    xtype: 'actioncolumn',
                    itemId: 'actionColumnMoveUpNew',
                    width: 30,
                    minWidth: 30, // width property not works. Use minWidth.
                    maxWidth: 30,
                    align: 'center',
                    items: [{
                        iconCls: 'x-fa fa-ellipsis-h',
                        disabled: true
                    }]
                }, {
                    xtype: 'actioncolumn',
                    itemId: 'actionColumnMoveDownNew',
                    width: 30,
                    minWidth: 30, // width property not works. Use minWidth.
                    maxWidth: 30,
                    align: 'center',
                    items: [{
                        iconCls: 'x-fa fa-ellipsis-h',
                        disabled: true
                    }]
                }, {
                    xtype: 'actioncolumn',
                    itemId: 'actionColumnAddNew',
                    width: 30,
                    minWidth: 30, // width property not works. Use minWidth.
                    maxWidth: 30,
                    align: 'center',
                    items: [{
                        iconCls: 'x-fa fa-plus',
                        autoEl: {
                            'data-testid': 'administration-importexport-attribute-addBtn'
                        },
                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                            return CMDBuildUI.locales.Locales.administration.common.actions.add;
                        },
                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                            metadata.value = Ext.String.insert(metadata.value, ' data-testid="administration-importexport-attribute-addBtn"', -7);
                            return 'x-fa fa-plus';
                        },

                        handler: function (button, rowIndex, colIndex) {

                            var attributeName = button.up('grid').down('#selectAttributeForGrid');
                            var columnName = button.up('grid').down('#newAttributeColumnName');
                            var attributeMode = button.up('grid').down('#newComboMode');
                            var defaultValue = button.up('grid').down('#newAttributeDefaultValue');
                            if (Ext.isEmpty(attributeName.getValue())) {
                                attributeName.focus();
                                attributeName.expand();
                                return false;
                            }
                            if (!attributeMode.isValid()) {
                                attributeMode.focus();
                                attributeMode.expand();
                                return false;
                            }
                            Ext.suspendLayouts();
                            var mainGrid = button.up('form').down('#importExportAttributeGrid');
                            var vm = button.up('form').getViewModel();
                            var attributeStore = vm.getStore('allSelectedAttributesStore');

                            var newAttribute = CMDBuildUI.model.importexports.Attribute.create({
                                attribute: attributeName.getValue(),
                                columnName: columnName.getValue(),
                                mode: attributeMode.getValue(),
                                'default': defaultValue.getValue(),
                                index: attributeStore.getRange().length
                            });

                            attributeStore.add(newAttribute);
                            attributeName.reset();
                            columnName.reset();
                            attributeMode.reset();
                            attributeMode.allowBlank = true;
                            // attributeMode.setHidden(true);
                            this.up('grid').down('#selectAttributeForGrid').setStoreFilter();
                            defaultValue.reset();
                            Ext.resumeLayouts();
                            mainGrid.getView().refresh();
                            vm.filterFreeAttributes();
                        }
                    }]
                }]
            }, this.getDataFormatFieldset()]
        };
    },

    getImportCriteriaFieldset: function () {
        return {
            title: CMDBuildUI.locales.Locales.administration.importexport.texts.importcriteria,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.importexport.texts.importcriteria'
            },
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            itemId: 'importfieldset',
            ui: 'administration-formpagination',
            xtype: "fieldset",
            layout: 'column',
            collapsible: true,

            bind: {
                hidden: '{!isImport}'
            },
            items: [{
                xtype: 'textfield',
                hidden: true,
                itemId: 'hiddenImportKeyAttribute',
                bind: {
                    value: '{theGateTemplate._importKeyAttribute}'
                },
                listeners: {
                    afterrender: function (input) {
                        var form = input.up('form');
                        var vm = input.lookupViewModel();
                        if (!vm.get('actions.view')) {
                            vm.bind({
                                bindTo: {
                                    targetType: '{theGateTemplate.targetType}',
                                    type: '{theGateTemplate.type}',
                                    importMode: '{theGateTemplate._importMode}'
                                }
                            }, function (data) {

                                if (data.importMode === CMDBuildUI.model.importexports.Template.importModes.add || data.type === 'export' || data.targetType === 'domain') {
                                    input.allowBlank = true;
                                    // input.clearInvalid();
                                } else {
                                    input.allowBlank = false;
                                }
                                form.form.checkValidity();
                            });
                        }
                    }
                }
            },
            this.createContainer([
                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('_importMode', {
                    _importMode: {
                        fieldcontainer: {
                            hidden: true,
                            bind: {
                                hidden: '{isDomain}'
                            }
                        },
                        fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.texts.importmode,
                        allowBlank: false,
                        displayField: 'label',
                        valueField: 'value',
                        bind: {
                            store: '{importModesStore}',
                            value: '{theGateTemplate._importMode}'
                        }
                    }
                })
            ]),
            this.createContainer([
                this.createContainer([{
                    xtype: 'grid',
                    headerBorders: false,
                    border: false,
                    bodyBorder: false,
                    rowLines: false,
                    sealedColumns: false,
                    sortableColumns: false,
                    enableColumnHide: false,
                    enableColumnMove: false,
                    enableColumnResize: false,
                    cls: 'administration-reorder-grid',
                    itemId: 'importExportAttributeKeyGrid',
                    selModel: {
                        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                    },
                    viewConfig: {
                        markDirty: false
                    },

                    columnWidth: 1,
                    autoEl: {
                        'data-testid': 'administration-content-importexport-gatetemplates-keyattributes-grid'
                    },

                    forceFit: true,
                    loadMask: true,

                    labelWidth: "auto",
                    bind: {
                        store: '{keyAttributesStore}'
                    },
                    columns: [{
                        flex: 1,
                        dataIndex: 'description',
                        align: 'left',
                        editor: {
                            xtype: 'displayfield',
                            height: 19,
                            minHeight: 19,
                            maxHeight: 19,
                            padding: 0,
                            ui: 'reordergrid-editor-combo'
                        },
                        renderer: function (value, cell, record) {
                            var vm = this.lookupViewModel();

                            function recoveryDescription(_vm, _value, _record) {
                                _vm.bind({
                                    bindTo: {
                                        rawAttributeStore: '{allAttributesStore}'
                                    },
                                    single: true
                                }, function (data) {
                                    if (!_value) {
                                        var attribute = data.rawAttributeStore.findRecord('name', _record.get('attribute'));
                                        if (attribute) {
                                            _record.set('_attribute_description', attribute.get('description'));
                                        } else {
                                            _record.set('_attribute_description', _record.get('attribute'));
                                        }
                                    }
                                });
                            }
                            recoveryDescription(vm, value, record);
                            return value;
                        }
                    }, {
                        xtype: 'actioncolumn',
                        itemId: 'actionColumnCancel',
                        bind: {
                            hidden: '{actions.view}'
                        },
                        width: 30,
                        minWidth: 30, // width property not works. Use minWidth.
                        maxWidth: 30,
                        align: 'center',
                        items: [{
                            iconCls: 'x-fa fa-times',
                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                return CMDBuildUI.locales.Locales.administration.common.actions.remove;
                            },
                            handler: function (grid, rowIndex, colIndex) {
                                var vm = grid.lookupViewModel();
                                var store = grid.getStore();
                                var record = store.getAt(rowIndex);
                                store.remove(record);
                                var keys = [];
                                store.each(function (item) {
                                    keys.push(item.get('name'));
                                });
                                vm.set('theGateTemplate._importKeyAttribute', keys);
                                if (!keys.length) {
                                    grid.up('fieldcontainer').down('#selectKeyAttributeForGrid').markInvalid(CMDBuildUI.locales.Locales.administration.common.messages.thisfieldisrequired);
                                }
                                grid.lookupViewModel().filterFreeAttributes();
                            },
                            isDisabled: function (view, rowIndex, colIndex, item, record) {
                                return (!record.get('editing')) ? false : true;
                            },
                            getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                metadata.value = Ext.String.insert(metadata.value, Ext.String.format(' data-testid="administration-importexport-attribute-removeBtn-{0}"', rowIndex), -7);
                                if (record.get('editing')) {
                                    return 'x-fa fa-ellipsis-h';
                                }
                                return 'x-fa  fa-times';
                            }
                        }]
                    }]
                }, {
                    margin: '20 0 20 0',
                    xtype: 'grid',
                    headerBorders: false,
                    border: false,
                    bodyBorder: false,
                    rowLines: false,
                    sealedColumns: false,
                    sortableColumns: false,
                    enableColumnHide: false,
                    enableColumnMove: false,
                    enableColumnResize: false,
                    cls: 'administration-reorder-grid',
                    itemId: 'importExportAttributeKeyGridNew',
                    selModel: {
                        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                    },
                    columnWidth: 1,
                    autoEl: {
                        'data-testid': 'administration-content-importexport-datatemplates-grid-newrecord'
                    },

                    forceFit: true,
                    loadMask: true,

                    labelWidth: "auto",
                    bind: {
                        store: '{newKeyAttributesStore}',
                        hidden: '{actions.view}'
                    },

                    columns: [{
                        xtype: 'widgetcolumn',
                        dataIndex: 'attribute',
                        align: 'left',
                        flex: 1,
                        widget: {
                            xtype: 'combo',
                            queryMode: 'local',
                            typeAhead: true,
                            editable: true,
                            forceSelection: true,
                            emptyText: CMDBuildUI.locales.Locales.administration.importexport.texts.selectanattribute,
                            localized: {
                                emptyText: 'CMDBuildUI.locales.Locales.administration.importexport.texts.selectanattribute'
                            },
                            itemId: 'selectKeyAttributeForGrid',
                            displayField: 'description',
                            valueField: 'name',
                            bind: {
                                store: '{freeKeyAttributesStore}'
                            },
                            autoEl: {
                                'data-testid': 'administration-importexport-attributekey-name'
                            },
                            height: 19,
                            minHeight: 19,
                            maxHeight: 19,
                            padding: 0,
                            ui: 'reordergrid-editor-combo',
                            listeners: {
                                afterrender: function (combo) {
                                    var vm = combo.lookupViewModel();
                                    var form = combo.up('form');
                                    vm.bind({
                                        bindTo: {
                                            keyAttributes: '{theGateTemplate._importKeyAttribute}',
                                            importMode: '{theGateTemplate._importMode}'
                                        }
                                    }, function (data) {
                                        if (data.importMode === CMDBuildUI.model.importexports.Template.importModes.merge && (!data.keyAttributes || !data.keyAttributes.length)) {
                                            combo.allowBlank = false;
                                            combo.markInvalid(CMDBuildUI.locales.Locales.administration.common.messages.thisfieldisrequired);
                                        } else {
                                            combo.allowBlank = true;
                                            combo.clearInvalid();
                                        }
                                        form.form.checkValidity();
                                        CMDBuildUI.util.administration.helper.FormHelper.getInvalidFields(form.form);
                                    });
                                },
                                select: function (combo, record) {
                                    var vm = combo.lookupViewModel();
                                    var _keyAttributes = vm.get('theGateTemplate._importKeyAttribute');
                                    var keyAttributes = Ext.Array.from(_keyAttributes);
                                    keyAttributes.push(combo.getValue());
                                    vm.set('theGateTemplate._importKeyAttribute', keyAttributes.join(','));
                                    combo.clearValue();
                                }
                            }
                        }
                    }]
                }], {
                    columnWidth: 0.5,
                    xtype: 'fieldcontainer',
                    style: 'margin-top: 20px; margin-right: 10px',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.importkeattributes,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.importkeattributes'
                    },
                    bind: {
                        hidden: '{theGateTemplate.targetType === "domain" || theGateTemplate._importMode === "add"}'
                    },
                    autoEl: {
                        'data-testid': 'administration-importexport-attributeKeys-grid'
                    }
                })
            ]),
            this.createContainer([
                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('mergeMode', {
                    mergeMode: {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.missingrecords,
                        allowBlank: false,
                        bind: {
                            value: '{theGateTemplate.mergeMode}',
                            store: '{mergeModesStore}',
                            disabled: '{theGateTemplate.fileFormat === "ifc"}'
                        },
                        fieldcontainer: {
                            bind: {
                                hidden: '{isAddMode}'
                            },
                            listeners: {
                                beforeshow: function () {
                                    var input = this.down('#mergeMode_input');
                                    if (input) {
                                        input.forceSelection = true;
                                    }
                                },
                                beforehide: function () {
                                    var input = this.down('#mergeMode_input');
                                    if (input) {
                                        input.forceSelection = false;
                                    }
                                    if (input.getValue() === null) {
                                        input.setValue(CMDBuildUI.model.importexports.Template.missingRecords.nomerge);
                                    }
                                }
                            }
                        },
                        listeners: {
                            change: function (combo, newValue, oldValue) {
                                var form = combo.up('form');
                                if (form) {
                                    var importKeyAttribute = form.down('#importKeyAttribute_input');
                                    if (importKeyAttribute) {
                                        var allowBlank = form.lookupViewModel().get('isDomain') || newValue === CMDBuildUI.model.importexports.Template.missingRecords.nomerge;
                                        CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(importKeyAttribute, allowBlank, form);
                                    }
                                }
                            }
                        }
                    }
                }),
                // autoLoad                
                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput('handleMissingRecordsOnError', {
                    handleMissingRecordsOnError: {
                        fieldcontainer: {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.handlemissingrecordsonerror,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.handlemissingrecordsonerror'
                            },
                            hidden: true,
                            bind: {
                                hidden: Ext.String.format('{({0} !== "{1}" && {0} !== "{2}") ||  ("database" === theGateTemplate.fileFormat || "ifc" === theGateTemplate.fileFormat)}', 'theGateTemplate.mergeMode', CMDBuildUI.model.importexports.Template.missingRecords['delete'], CMDBuildUI.model.importexports.Template.missingRecords.modifycard)
                            },
                            listeners: {
                                hide: function (fieldcontainer) {
                                    fieldcontainer.down('checkbox').setValue(false);
                                }
                            }

                        },
                        bind: {
                            disabled: '{actions.view}',
                            value: '{theGateTemplate.handleMissingRecordsOnError}'
                        }
                    }
                })
            ]),


            // TODO
            this.createContainer([
                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('mergeMode_when_missing_update_attr', {
                    mergeMode_when_missing_update_attr: {
                        fieldcontainer: {
                            bind: {
                                hidden: '{!isModifyCard}'
                            }
                        },
                        fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.texts.attributetoedit,
                        allowBlank: false,
                        queryMode: 'local',
                        displayField: 'description',
                        valueField: 'name',
                        bind: {
                            store: '{allClassOrDomainsAtributes}',
                            value: '{theGateTemplate.mergeMode_when_missing_update_attr}'
                        },
                        listeners: {

                            beforeselect: function (combo, attributename, oldValue) {
                                if (oldValue) {
                                    var valueField = this.up('form').getForm().getFields().findBy(
                                        function (item) {
                                            return item.itemId === "mergeMode_when_missing_update_value_input";
                                        }
                                    );
                                    if (valueField) {
                                        valueField.up('#valueContainer').removeAll(); // .destroy();
                                    }
                                }
                            }

                        }
                    }
                }),

                // TODO
                {
                    xtype: 'fieldcontainer',
                    itemId: 'valueContainer',
                    layout: 'column',
                    columnWidth: 0.5,
                    bind: {
                        hidden: '{!isModifyCard}'
                    },
                    fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.value,
                    items: [
                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput('mergeMode_when_missing_update_value', {
                            mergeMode_when_missing_update_value: {
                                columnWidth: 1,
                                fieldcontainer: {

                                },
                                htmlEncode: true,
                                allowBlank: false,
                                bind: {
                                    value: '{theGateTemplate._mergeMode_when_missing_update_value_description}'
                                }
                            }
                        })
                    ]
                }

            ])
            ]
        };
    },

    getExportCriteriaFieldset: function () {
        return {
            title: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.exportfilter,
            xtype: "fieldset",
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            itemId: 'exportfilterfieldset',
            ui: 'administration-formpagination',
            collapsible: true,
            bind: {
                hidden: '{!isExport || isDomain}'
            },

            items: [{
                xtype: 'fieldcontainer',
                items: [{
                    layout: 'column',
                    defaults: {
                        columnWidth: 0.5
                    },
                    items: [{
                        xtype: 'fieldcontainer',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.searchfilters.fieldlabels.filters,
                        columnWidth: 1,
                        items: [{
                            xtype: 'components-administration-toolbars-formtoolbar',
                            style: 'border:none; margin-top: 5px',
                            items: [{
                                xtype: 'tool',
                                align: 'right',
                                itemId: 'editFilterBtn',
                                cls: 'administration-tool margin-right5',
                                iconCls: 'cmdbuildicon-filter',
                                tooltip: CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips.filters,
                                autoEl: {
                                    'data-testid': 'administration-searchfilter-tool-removefilterbtn'
                                },
                                bind: {
                                    disabled: '{!theGateTemplate.targetName}'
                                }
                            }, {

                                xtype: 'tool',
                                align: 'right',
                                itemId: 'removeFilterBtn',
                                cls: 'administration-tool margin-right5',
                                iconCls: 'cmdbuildicon-filter-remove',
                                tooltip: CMDBuildUI.locales.Locales.administration.classes.properties.toolbar.deleteBtn.tooltip,
                                autoEl: {
                                    'data-testid': 'administration-searchfilter-tool-removefilterbtn'
                                },
                                bind: {
                                    disabled: '{actions.view}'
                                }
                            }]
                        }]
                    }]
                }]
            }]
        };
    },

    getErrorsManagementFieldset: function () {
        return {
            title: CMDBuildUI.locales.Locales.administration.importexport.texts.notifications,

            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: "fieldset",
            layout: 'column',
            collapsible: true,
            items: [{
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 1,
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('notificationTemplate', {
                        notificationTemplate: {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.texts.notificationtemplate,
                            fieldcontainer: {
                                columnWidth: 0.5
                            },
                            allowBlank: true,
                            displayField: 'description',
                            valueField: 'name',
                            forceSelection: true,
                            combofield: {
                                bind: {
                                    store: '{notificationEmailTemplates}',
                                    value: '{theGateTemplate.notificationTemplate}'
                                }
                            },
                            bind: {},
                            displayfield: {
                                bind: {
                                    value: '{theGateTemplate._notificationEmailTemplate_description}'
                                },
                                renderer: function (value) {
                                    var me = this;
                                    var vm = me.lookupViewModel();
                                    vm.bind({
                                        bindTo: {
                                            store: '{notificationEmailTemplatesStore}',
                                            value: '{theGateTemplate.notificationTemplate}'
                                        },
                                        single: true
                                    }, function (data) {
                                        if (!Ext.isEmpty(data.value) && data.store) {
                                            var record = data.store.findRecord('name', data.value);
                                            vm.set('theGateTemplate._notificationEmailTemplate_description', record.get('description'));
                                        } else {
                                            vm.set('theGateTemplate._notificationEmailTemplate_description', value || null);
                                        }
                                    });
                                    return value;
                                }
                            }
                        }
                    }), CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('errorAccount', {
                        errorAccount: {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.texts.account,
                            fieldcontainer: {
                                columnWidth: 0.5
                            },
                            displayField: 'name',
                            valueField: 'name',
                            forceSelection: true,
                            bind: {
                                store: '{allEmailAccounts}',
                                value: '{theGateTemplate.errorAccount}'
                            },
                            triggers: {
                                clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                            },
                            displayfield: {
                                bind: {
                                    value: '{theGateTemplate._errorEmailAccount_description}'
                                },
                                renderer: function (value) {
                                    var me = this;
                                    var vm = me.lookupViewModel();
                                    vm.bind({
                                        bindTo: {
                                            store: '{allEmailAccounts}',
                                            value: '{theGateTemplate.errorAccount}'
                                        },
                                        single: true
                                    }, function (data) {
                                        if (!vm.destroyed && !Ext.isEmpty(data.value) && data.store) {
                                            var record = data.store.findRecord('name', data.value);
                                            if (record) {
                                                vm.set('theGateTemplate._errorEmailAccount_description', record.get('name'));
                                            }
                                        }
                                    });
                                    return value;
                                }
                            }
                        }
                    })
                ]
            }]
        };
    },

    getDataFormatFieldset: function () {
        return {
            title: CMDBuildUI.locales.Locales.administration.importexport.texts.dataformat,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.importexport.texts.dataformat'
            },
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: "fieldset",
            layout: 'column',
            collapsible: true,
            hidden: true,
            bind: {
                hidden: '{dataFormatHidden}'
            },
            items: [{
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 1,
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('dateFormat', {
                        dateFormat: {
                            fieldcontainer: {
                                hidden: true,
                                fieldLabel: CMDBuildUI.locales.Locales.main.preferences.labeldateformat,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.labeldateformat'
                                },
                                bind: {
                                    hidden: '{dataFormatDateHidden}'
                                }
                            },
                            displayfield: {
                                bind: {
                                    value: '{theGateTemplate.dateFormat}'
                                },
                                renderer: function (value) {
                                    var store = this.lookupViewModel().get('dateFormats');
                                    if (store) {
                                        var record = store.findRecord('value', value);
                                        value = record ? record.get('label') : value;
                                    }
                                    return value ? value : CMDBuildUI.locales.Locales.main.preferences.defaultvalue;
                                }
                            },
                            combofield: {
                                emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                                localized: {
                                    emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue'
                                },
                                forceSelection: true,
                                autoSelect: false,
                                triggers: {
                                    clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                                },
                                bind: {
                                    store: '{dateFormats}',
                                    value: '{theGateTemplate.dateFormat}'
                                }
                            }
                        }
                    }), CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('timeFormat', {
                        timeFormat: {
                            fieldcontainer: {
                                hidden: true,
                                fieldLabel: CMDBuildUI.locales.Locales.main.preferences.labeltimeformat,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.labeltimeformat'
                                },
                                bind: {
                                    hidden: '{dataFormatTimeHidden}'
                                }
                            },
                            displayfield: {
                                bind: {
                                    value: '{theGateTemplate.timeFormat}'
                                },
                                renderer: function (value) {
                                    var store = this.lookupViewModel().get('timeFormats');
                                    if (store) {
                                        var record = store.findRecord('value', value);
                                        value = record ? record.get('label') : value;
                                    }
                                    return value ? value : CMDBuildUI.locales.Locales.main.preferences.defaultvalue;
                                }
                            },
                            combofield: {
                                emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                                localized: {
                                    emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue'
                                },
                                forceSelection: true,
                                autoSelect: false,
                                triggers: {
                                    clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                                },
                                bind: {
                                    store: '{timeFormats}',
                                    value: '{theGateTemplate.timeFormat}'
                                }
                            }
                        }
                    })
                ]
            }, {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 1,
                items: [{
                    xtype: 'displayfield',
                    hidden: true,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.texts.datetimeformat,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.importexport.texts.datetimeformat'
                    },
                    bind: {
                        hidden: '{dataFormatDateTimeHidden}',
                        value: '{dateTimeFormat}'
                    }
                }]
            }, {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 1,
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('decimalSeparator', {
                        decimalSeparator: {
                            fieldcontainer: {
                                hidden: true,
                                fieldLabel: CMDBuildUI.locales.Locales.main.preferences.labeldecimalsseparator,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.labeldecimalsseparator'
                                },
                                bind: {
                                    hidden: '{dataFormatDecimalHidden}'
                                }
                            },
                            displayfield: {
                                bind: {
                                    value: '{theGateTemplate.decimalSeparator}'
                                },
                                renderer: function (value) {
                                    var store = this.lookupViewModel().get('decimalsSeparators');
                                    if (store) {
                                        var record = store.findRecord('value', value);
                                        value = record ? record.get('label') : value;
                                    }
                                    return value ? value : CMDBuildUI.locales.Locales.main.preferences.defaultvalue;
                                }
                            },
                            emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                            localized: {
                                emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue'
                            },
                            forceSelection: true,
                            autoSelect: false,
                            triggers: {
                                clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                            },
                            bind: {
                                store: '{decimalsSeparators}',
                                value: '{theGateTemplate.decimalSeparator}'
                            }
                        }
                    })
                ]
            }]
        };
    },

    createContainer: function (items, config) {
        var container = Ext.merge({}, {
            xtype: 'container',
            layout: 'column',
            columnWidth: 1,
            items: items
        }, config || {});

        return container;

    },

    privates: {

    }

});