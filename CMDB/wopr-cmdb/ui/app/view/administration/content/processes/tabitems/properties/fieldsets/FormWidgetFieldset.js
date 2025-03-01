Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.properties.fieldsets.FormWidgetFieldset', {
    extend: 'Ext.panel.Panel',
    controller: 'administration-content-processes-tabitems-properties-fieldsets-formwidgetfieldset',

    alias: 'widget.administration-content-processes-tabitems-properties-fieldsets-formwidgetfieldset',
    viewModel: {
    },
    items: [{
        xtype: 'fieldset',
        collapsible: true,
        collapsed: true,
        layout: 'column',
        bind: {
            title: CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formWidgets + ' ({formWidgetCount})'
        },
        ui: 'administration-formpagination',
        items: [{
            columnWidth: 1,
            items: [{
                xtype: 'components-grid-reorder-grid',
                bind: {
                    store: '{theProcess.widgets}'
                },
                columnWidth: 0.5,
                reference: 'formWidgetGrid',
                flex: 1,
                viewConfig: {
                    markDirty: false
                },
                columns: [{
                    flex: 1,
                    text: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.widgetname, // Widget Name
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.widgetname'
                    },
                    align: 'left',
                    dataIndex: '_label'
                }, {
                    flex: 1,
                    text: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.guicustom, // GUI custom
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.guicustom'
                    },
                    align: 'left',
                    dataIndex: '_type',
                    renderer: function (value) {
                        if (value) {
                            var store = this.lookupViewModel().get('widgetTypesStore');
                            if (store) {
                                var record = store.findRecord('value', value);
                                if (record) {
                                    return record.get('label');
                                }
                            }
                            return value;
                        }
                        return '';
                    }
                }, {
                    flex: 2,
                    text: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.guicustomparameters, // GUI custom
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.guicustomparameters'
                    },
                    xtype: 'widgetcolumn',
                    align: 'left',
                    widget: {
                        xtype: 'aceeditortextarea',
                        inputField: '_config',
                        vmObjectName: 'record',
                        config: {
                            options: {
                                readOnly: true
                            }
                        },
                        theMainObject: 'theWidget',
                        bind: {
                            value: '{record._config}'
                        }
                    },
                    // called when the widget is initially instantiated
                    // on the widget column
                    onWidgetAttach: function (col, widget, rec) {
                        widget.aceEditor.setValue(widget.$widgetRecord.get('_config'));
                        widget.aceEditor.moveCursorTo(0);
                        // put the widget inside record for later use in controller
                        // on edit btn click event. Needed for value change in widget.
                        rec.widget = widget;
                    }
                }, {
                    flex: 1,
                    text: CMDBuildUI.locales.Locales.administration.common.labels.status,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.labels.status'
                    },
                    xtype: 'widgetcolumn',
                    align: 'left',
                    dataIndex: '_active',
                    widget: {
                        xtype: 'checkbox', // textfield | combo | radio
                        bind: '{record._active}',
                        boxLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                        localized: {
                            boxLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                        },
                        readOnly: true
                    }
                }, {
                    xtype: 'actioncolumn',
                    minWidth: 150,
                    maxWidth: 150,
                    bind: {
                        hidden: '{actions.view}'
                    },
                    align: 'center',
                    items: [{
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil', 'solid'),
                        handler: 'onEditBtn',
                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                            return CMDBuildUI.locales.Locales.administration.common.tooltips.edittrigger;
                        }
                    }, {
                        xtype: 'actioncolumn',
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('arrow-up', 'solid'),
                        handler: 'moveUp',
                        isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                            return rowIndex === 0;
                        },
                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                            return CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.actions.moveUp.tooltip;
                        }
                    }, {
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('arrow-down', 'solid'),
                        handler: 'moveDown',
                        isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                            return rowIndex >= view.store.getCount() - 1;
                        },
                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                            return CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.actions.moveDown.tooltip;
                        }
                    }, {
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),
                        handler: 'deleteRow',
                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                            return CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.actions.deleteTrigger.tooltip;
                        }
                    }, {
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                            return CMDBuildUI.locales.Locales.administration.common.tooltips.localize;
                        },
                        handler: function (grid, rowIndex, colIndex, item, event, record) {
                            var vm = grid.up('administration-content-processes-tabitems-properties-properties').lookupViewModel();
                            var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfClassFormWidgetItem(vm.get('theProcess.name'), grid.getStore().getAt(rowIndex).get('WidgetId'));

                            CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, CMDBuildUI.util.administration.helper.FormHelper.formActions.edit, 'theFormWidgetTranslation_' + grid.getStore().getAt(rowIndex).get('WidgetId'), vm, true);
                        },
                        isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                            return record.get('type') === 'separator' ? true : false;
                        },
                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                            return record.get('editing') ? CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid') : CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid');
                        },
                        margin: '0 10 0 10'
                    }]
                }]
            }, {
                columnWidth: 1,
                items: [{

                    xtype: 'components-grid-reorder-grid',
                    bind: {
                        store: '{formWidgetsStoreNew}',
                        hidden: '{actions.view}'
                    },
                    columnWidth: 0.5,
                    flex: 1,
                    viewConfig: {
                        markDirty: false
                    },
                    columns: [{
                        flex: 1,
                        text: CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.createnewwidget,
                        localized: {
                            text: 'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.createnewwidget'
                        },
                        xtype: 'widgetcolumn',
                        align: 'left',
                        dataIndex: '_label',
                        widget: {
                            xtype: 'textfield',
                            label: '',
                            inputField: '_label',
                            itemId: 'widgetLabel',
                            bind: {
                                value: '{record._label}'
                            },
                            listeners: {
                                change: function (element, newValue, oldValue) {
                                    element.up('administration-content-processes-tabitems-properties-fieldsets-formwidgetfieldset').getViewModel().getParent().get('formWidgetsStoreNew').getData().items[0].set('_label', newValue);

                                }
                            }
                        }
                    }, {
                        flex: 1,
                        text: '',
                        xtype: 'widgetcolumn',
                        align: 'left',
                        dataIndex: '_type',
                        widget: {
                            xtype: 'combobox',
                            inputField: '_type',
                            editable: false,
                            forceSelection: true,
                            // allowBlank: false,
                            displayField: 'label',
                            valueField: 'value',
                            itemId: 'widgetType',
                            bind: {
                                store: '{widgetTypesStore}',
                                value: '{record._type}'
                            },
                            listeners: {
                                change: function (element, newValue, oldValue) {
                                    element.up('administration-content-processes-tabitems-properties-fieldsets-formwidgetfieldset').getViewModel().getParent().get('formWidgetsStoreNew').getData().items[0].set('_type', newValue);
                                }
                            }
                        }
                    },
                    {
                        flex: 2,
                        text: '',
                        xtype: 'widgetcolumn',
                        align: 'left',
                        widget: {
                            xtype: 'component',
                            html: '<div id="newFormWidgetScriptField" style="min-height:58px;height:100%;min-width:20px; width:100%"></div>',
                            listeners: {
                                afterrender: function (cmp) {
                                    var me = this;
                                    var editor = window.newFormWidgetScriptField = ace.edit('newFormWidgetScriptField');

                                    //set the theme
                                    //
                                    editor.setTheme('ace/theme/chrome');

                                    //set the mode
                                    //
                                    editor.getSession().setMode('ace/mode/javascript');

                                    //set some options
                                    //
                                    editor.setOptions({
                                        showLineNumbers: true,
                                        showPrintMargin: false
                                    });

                                    //set a value
                                    //
                                    editor.setValue('');

                                    editor.getSession().on('change', function (event, _editor) {
                                        var vm = me.up('administration-content-processes-tabitems-properties-fieldsets-formwidgetfieldset').getViewModel().getParent();
                                        vm.get('formWidgetsStoreNew').getData().items[0].set('_config', _editor.getValue());
                                    });
                                }
                            }
                        }
                    },
                    {
                        flex: 1,
                        text: '',
                        xtype: 'widgetcolumn',
                        align: 'left',
                        dataIndex: '_active',
                        widget: {
                            xtype: 'checkbox', // textfield | combo | radio
                            bind: {
                                value: '{record._active}'
                            },
                            boxLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                            localized: {
                                boxLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                            }
                        }
                    },
                    {
                        xtype: 'actioncolumn',
                        minWidth: 150,
                        maxWidth: 150,

                        align: 'center',
                        items: [{
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid'),
                            disabled: true

                        }, {
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid'),
                            disabled: true

                        }, {
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid'),
                            disabled: true
                        }, {
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid'),
                            disabled: true
                        }, {
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid'),
                            handler: 'onAddNewWidgetMenuBtn',
                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                return CMDBuildUI.locales.Locales.administration.common.actions.add;
                            }
                        }]
                    }
                    ]
                }]
            }]
        }]
    }]
});