Ext.define('CMDBuildUI.view.joinviews.configuration.items.Fieldsets', {
    extend: 'Ext.form.FieldSet',

    requires: [
        'CMDBuildUI.view.joinviews.configuration.items.FieldsetsController',
        'CMDBuildUI.view.joinviews.configuration.items.FieldsetsModel'
    ],
    alias: 'widget.joinviews-configuration-items-fieldsets',
    controller: 'joinviews-configuration-items-fieldsets',
    viewModel: {
        type: 'joinviews-configuration-items-fieldsets'
    },
    title: CMDBuildUI.locales.Locales.joinviews.fieldsets,
    localized: {
        title: 'CMDBuildUI.locales.Locales.joinviews.fieldsets'
    },
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    bind: {
        ui: '{fieldsetUi}'
    },
    layout: {
        type: 'vbox',
        align: 'stretch',
        vertical: true
    },
    items: [{

        columnWidth: 0.75,
        items: [{
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
            selModel: {
                pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
            },
            reference: 'groupingsGrid',
            itemId: 'groupingsAttributesGrid',
            bind: {
                store: '{theView.attributeGroups}'
            },
            viewConfig: {
                markDirty: false
            },
            plugins: {
                ptype: 'actionColumnRowEditing',
                id: 'actionColumnRowEditing',
                hiddenColumnsOnEdit: ['actionColumnEdit', 'actionColumnMoveUp', 'actionColumnMoveDown', 'actionColumnCancel', 'actionLocalize'],
                clicksToEdit: 10,
                buttonsUi: 'button-like-tool',
                errorSummary: false,
                placeholdersButtons: [{
                    bind: {
                        hidden: '{!isAdministrationModule}'
                    },
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
                }, {
                    iconCls: 'x-fa fa-ellipsis-h',
                    disabled: true,
                    xtype: 'button',
                    minWidth: 30,
                    maxWidth: 30,
                    ui: 'button-like-tool'
                }]
            },

            columns: [{
                flex: 2,
                text: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.group,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.group'
                },
                dataIndex: 'description',
                align: 'left',
                editor: {
                    xtype: 'textfield',
                    height: 19,
                    minHeight: 19,
                    maxHeight: 19,
                    padding: 0,
                    ui: 'reordergrid-editor-combo'
                },
                variableRowHeight: true
            }, {
                flex: 1,
                text: CMDBuildUI.locales.Locales.administration.attributes.texts.displaymode,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.attributes.texts.displaymode'
                },
                dataIndex: 'defaultDisplayMode',
                align: 'left',
                height: 19,
                editor: {
                    xtype: 'combo',
                    displayField: 'label',
                    valueField: 'value',
                    height: 19,
                    minHeight: 19,
                    maxHeight: 19,
                    padding: 0,
                    ui: 'reordergrid-editor-combo',
                    bind: {
                        store: '{attriubteGroupingDisplayModeStore}'
                    }
                },
                renderer: function (value) {
                    var vm = this.lookupViewModel();
                    var store = vm.get('attriubteGroupingDisplayModeStore');
                    if (store) {
                        var record = store.findRecord('value', value, 0, false, true);
                        if (record) {
                            return record.get('label');
                        }
                    }
                    return Ext.String.capitalize(value);
                },
                variableRowHeight: true
            }, {
                xtype: 'actioncolumn',
                itemId: 'actionColumnEdit',
                bind: {
                    hidden: '{actions.view}'
                },
                hidden: true,
                width: 30,
                minWidth: 30, // width property not works. Use minWidth.
                maxWidth: 30,
                align: 'center',
                items: [{
                    bind: {
                        hidden: '{actions.view}'
                    },
                    handler: function (grid, rowIndex, colIndex, item, e, record) {
                        grid.editingPlugin.startEdit(record, 0);
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
                    bind: {
                        hidden: '{actions.view}'
                    },
                    iconCls: 'x-fa fa-arrow-up',
                    tooltip: CMDBuildUI.locales.Locales.administration.common.actions.moveup, // Move up
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.moveup'
                    },
                    handler: 'moveUp',
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
                    tooltip: CMDBuildUI.locales.Locales.administration.common.actions.movedown, // Move down
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.movedown'
                    },
                    handler: 'moveDown',
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
                    tooltip: CMDBuildUI.locales.Locales.administration.common.actions.remove, // Remove
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.remove'
                    },
                    handler: 'deleteRow',
                    isDisabled: function (view, rowIndex, colIndex, item, record) {
                        if (!record.get('editing')) {
                            return false;
                        } else {
                            return true;
                        }
                    },
                    getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                        metadata.value = Ext.String.insert(metadata.value, Ext.String.format(' data-testid="administration-reordergrid-removeBtn-{0}"', rowIndex), -7);
                        if (record.get('editing')) {
                            return 'x-fa fa-ellipsis-h';
                        }
                        return 'x-fa  fa-times';
                    }
                }]
            }, {
                xtype: 'actioncolumn',
                itemId: 'actionLocalize',
                bind: {
                    hidden: '{actions.view || !isAdministrationModule}'
                },
                width: 30,
                minWidth: 30, // width property not works. Use minWidth.
                maxWidth: 30,
                align: 'center',
                items: [{
                    bind: {
                        hidden: '{actions.view || !isAdministrationModule}'
                    },
                    iconCls: 'x-fa fa-times',
                    tooltip: CMDBuildUI.locales.Locales.administration.common.actions.localize,
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.localize'
                    },
                    handler: function (grid, rowIndex, colIndex) {
                        var mainView = this.getView().up('joinviews-configuration-main');
                        var mainVm = mainView.getViewModel();
                        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfViewGroupingsDescription(mainVm.get('theView.name'), grid.getStore().getAt(rowIndex).get('name'));
                        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, mainVm.get('action'), Ext.String.format('theGroupingDescriptionTranslation_{0}', CMDBuildUI.util.Utilities.stringToHex(grid.getStore().getAt(rowIndex).get('name'))), mainVm, true);


                    },
                    isDisabled: function (view, rowIndex, colIndex, item, record) {
                        if (!record.get('editing')) {
                            return false;
                        } else {
                            return true;
                        }

                    },
                    getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                        metadata.value = Ext.String.insert(metadata.value, Ext.String.format(' data-testid="administration-reordergrid-removeBtn-{0}"', rowIndex), -7);
                        if (record.get('editing')) {
                            return 'x-fa fa-ellipsis-h';
                        }
                        return 'x-fa  fa-flag';
                    }
                }]
            },  {
                xtype: 'actioncolumn',                
                hidden: true,
                bind: {
                    hidden: '{isAdministrationModule}'
                },
                width: 30,
                minWidth: 30, // width property not works. Use minWidth.
                maxWidth: 30,
                align: 'center',
                items: []
            }]
        }, {
            xtype: 'components-grid-reorder-grid',
            margin: '20 0 20 0',
            bind: {
                store: '{attributeGroupsStoreNew}',
                hidden: '{actions.view}'
            },
            viewConfig: {
                markDirty: false
            },
            columns: [{
                flex: 2,
                text: CMDBuildUI.locales.Locales.administration.attributes.strings.createnewgroup,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.attributes.strings.createnewgroup'
                },
                xtype: 'widgetcolumn',
                align: 'left',
                dataIndex: 'description',
                widget: {
                    xtype: 'textfield',
                    value: '',
                    bind: {
                        value: '{record.description}'
                    }
                }
            }, {
                flex: 1,
                text: CMDBuildUI.locales.Locales.administration.attributes.texts.displaymode,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.attributes.texts.displaymode'
                },
                xtype: 'widgetcolumn',
                dataIndex: 'defaultDisplayMode',
                align: 'left',
                widget: {
                    xtype: 'combo',
                    displayField: 'label',
                    valueField: 'value',
                    bind: {
                        value: '{record.defaultDisplayMode}',
                        store: '{attriubteGroupingDisplayModeStore}'
                    }

                },
                variableRowHeight: true
            }, {
                xtype: 'actioncolumn',
                minWidth: 150,
                maxWidth: 150,
                bind: {
                    hidden: '{actions.view}'
                },
                align: 'center',
                items: [{
                    iconCls: 'x-fa fa-ellipsis-h',
                    disabled: true
                }, {
                    iconCls: 'x-fa fa-ellipsis-h',
                    disabled: true
                }, {
                    iconCls: 'x-fa fa-ellipsis-h',
                    disabled: true
                }, {
                    iconCls: 'x-fa fa-ellipsis-h',
                    disabled: true
                }, {
                    iconCls: 'x-fa fa-plus',
                    tooltip: CMDBuildUI.locales.Locales.administration.attributes.strings.addnewgroup,
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.administration.attributes.strings.addnewgroup'
                    },
                    isDisabled: function (view, rowIndex, colIndex, item, record) {
                        if (Ext.String.trim(record.get('description')).length) {
                            return false;
                        } else {
                            return true;
                        }
                    },
                    handler: 'onAddGroupClick'
                }]
            }]
        }]
    }]
});