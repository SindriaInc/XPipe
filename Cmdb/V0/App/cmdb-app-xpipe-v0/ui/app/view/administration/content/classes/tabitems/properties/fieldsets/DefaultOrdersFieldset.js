Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.DefaultOrdersFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-classes-tabitems-properties-fieldsets-defaultordersfieldset',
    controller: 'administration-content-classes-tabitems-properties-fieldsets-defaultordersfieldset',
    viewModel: {
        type: 'administration-content-classes-tabitems-properties-fieldsets-defaultordersfieldset'
    },
    items: [{
        xtype: 'fieldset',
        layout: 'column',
        bind: {
            hidden: '{actions.add}'
        },
        title: CMDBuildUI.locales.Locales.administration.classes.strings.datacardsorting, // Data cards sorting
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.classes.strings.datacardsorting'
        },
        collapsible: true,
        ui: 'administration-formpagination',
        items: [{
            columnWidth: 0.75,
            items: [{
                xtype: 'components-grid-reorder-grid',
                reference: 'defaultOrderGrid',
                itemId: 'defaultOrderGrid',
                bind: {
                    store: '{theObject.defaultOrder}'
                },
                viewConfig: {
                    markDirty: false
                },

                columns: [{
                    flex: 2,
                    text: CMDBuildUI.locales.Locales.administration.common.strings.attribute, // Attribute
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.strings.attribute'
                    },
                    dataIndex: '_attribute_description',
                    align: 'left',
                    renderer: function (value, cell, record) {
                        var vm = this.lookupViewModel();
                        if (record.get('attribute') === 'IdClass') {
                            record.set('_attribute_description', 'Subtype');
                        } else {
                            if (!value) {
                                vm.bind({
                                    bindTo: {
                                        attributesStore: '{attributesStore}'
                                    },
                                    single: true
                                }, function (data) {
                                    var _record = data.attributesStore.findRecord('name', record.get('attribute'));
                                    if (_record) {
                                        record.set('_attribute_description', _record.get('description'));
                                    }
                                });
                            }
                        }
                        return value;
                    }
                }, {
                    flex: 1,
                    text: CMDBuildUI.locales.Locales.administration.classes.texts.direction,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.classes.texts.direction'
                    },
                    dataIndex: 'direction',
                    align: 'left',
                    renderer: function (value) {
                        var vm = this.lookupViewModel();
                        var store = vm.get('defaultOrderDirectionsStore');
                        if (store) {
                            var record = store.findRecord('value', value);
                            if (record) {
                                return record.get('label');
                            }
                        }
                        return Ext.String.capitalize(value);
                    },
                    height: 19,
                    variableRowHeight: true,
                    editor: {
                        xtype: 'combo',
                        allowBlank: false,
                        editable: false,
                        height: 19,
                        minHeight: 19,
                        maxHeight: 19,
                        displayField: 'label',
                        valueField: 'value',
                        queryMode: 'local',
                        ui: 'reordergrid-editor-combo',
                        bind: {
                            value: '{record.direction}',
                            store: '{defaultOrderDirectionsStore}'
                        },
                        listeners: {
                            focus: function (combo, event, eOpts) {
                                combo.expand();
                                Ext.suspendLayouts();
                                var view = this.up();
                                var record = view.editingPlugin.context.record;
                                record.set('editing', true);
                            },
                            blur: function (textfield, event, eOpts) {
                                var view = this.up();
                                var record = view.editingPlugin.context.record;
                                record.set('editing', false);
                                record.commit();
                                Ext.resumeLayouts(true);
                            }
                        }
                    }
                }, {
                    xtype: 'actioncolumn',
                    minWidth: 150,
                    maxWidth: 150,
                    bind: {
                        hidden: '{!actions.edit}'
                    },
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
                    }, {
                        iconCls: 'x-fa fa-arrow-up',
                        handler: 'moveUp',
                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                            return CMDBuildUI.locales.Locales.administration.common.actions.moveup;
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
                        },
                        style: {
                            margin: '10'
                        }
                    }, {
                        iconCls: 'x-fa fa-arrow-down',
                        handler: 'moveDown',
                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                            return CMDBuildUI.locales.Locales.administration.common.actions.movedown;
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
                        },
                        margin: '0 10 0 10'
                    }, {
                        iconCls: 'x-fa fa-times',
                        handler: 'deleteRow',
                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                            return CMDBuildUI.locales.Locales.administration.common.actions.remove;
                        },
                        isDisabled: function (view, rowIndex, colIndex, item, record) {
                            if (!record.get('editing')) {
                                return false;
                            } else {
                                return true;
                            }

                        },
                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                            if (record.get('editing')) {
                                return 'x-fa fa-ellipsis-h';
                            }
                            return 'x-fa  fa-times';
                        },
                        margin: '0 10 0 10'
                    }]
                }]
            }, {
                flex: 1,
                margin: '20 0 20 0',
                items: [{
                    xtype: 'components-grid-reorder-grid',
                    reference: 'grid-reorder-gridForm',
                    bind: {
                        store: '{defaultOrderStoreNew}',
                        hidden: '{!actions.edit}'
                    },
                    viewConfig: {
                        markDirty: false
                    },
                    columns: [{
                        flex: 2,
                        text: '',
                        xtype: 'widgetcolumn',
                        align: 'left',
                        dataIndex: 'attribute',
                        widget: {
                            xtype: 'combobox',
                            queryMode: 'local',
                            typeAhead: true,
                            reference: 'defaultOrderAttribute',
                            displayField: 'description',
                            valueField: 'name',
                            value: '',
                            bind: {
                                value: '{record.attribute}',
                                store: '{allAttributesForSorting}'
                            },
                            listeners: {
                                beforequery: function (queryEv) {
                                    var allAttrStore = this.lookupViewModel().get('theObject.defaultOrder');
                                    if (this.getStore() && this.getStore()) {
                                        this.getStore().rejectChanges();
                                        this.getStore().clearFilter();
                                        if (allAttrStore.getRange()) {
                                            this.getStore().addFilter(function (item) {
                                                return !allAttrStore.findRecord('attribute', item.get('name'));
                                            });
                                        }
                                    }
                                    return true;
                                }
                            }
                        }
                    }, {
                        flex: 1,
                        text: '',
                        xtype: 'widgetcolumn',
                        align: 'left',
                        dataIndex: 'direction',
                        widget: {
                            xtype: 'combobox',
                            reference: 'defaultOrderDirection',
                            editable: false,
                            displayField: 'label',
                            valueField: 'value',
                            queryMode: 'local',
                            value: '',
                            bind: {
                                value: '{record.direction}',
                                store: '{defaultOrderDirectionsStore}'
                            },
                            listeners: {
                                change: function (combo, newValue, oldValue, eOpts) {
                                    if (!newValue) {
                                        return false;
                                    }
                                }
                            }
                        }
                    }, {
                        xtype: 'actioncolumn',
                        minWidth: 150,
                        maxWidth: 150,

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
                            iconCls: 'x-fa fa-plus',
                            handler: 'onAddNewDefaultOrderBtn',
                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                return CMDBuildUI.locales.Locales.administration.common.actions.add;
                            },
                            isDisabled: function (view, rowIndex, colIndex, item, record) {
                                if (Ext.String.trim(record.get('attribute')).length) {
                                    return false;
                                } else {
                                    return true;
                                }
                            }
                        }]
                    }]
                }]
            }]
        }]
    }]
});