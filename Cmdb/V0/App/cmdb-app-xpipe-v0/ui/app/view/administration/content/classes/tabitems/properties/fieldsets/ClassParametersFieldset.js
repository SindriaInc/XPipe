Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.ClassParamentersFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-classes-tabitems-properties-fieldsets-classparametersfieldset',

    items: [{
        xtype: 'fieldset',
        layout: 'column',
        title: CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.classParameters, // Class Parameters
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.classParameters'
        },
        ui: 'administration-formpagination',
        collapsible: true,
        items: [{
            columnWidth: 1,
            items: [{
                width: '50%',
                items: [{
                    // create / edit 
                    xtype: 'combobox',
                    queryMode: 'local',
                    displayField: 'description',
                    valueField: '_id',
                    name: 'defaultFilter',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.defaultfilter, // Default filter
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.defaultfilter'
                    },
                    allowBlank: true,
                    hidden: true,
                    bind: {
                        store: '{defaultFilterStore}',
                        value: '{theObject.defaultFilter}',
                        hidden: '{actions.view}'
                    },
                    triggers: {
                        clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                    }
                }, {
                    // view
                    xtype: 'displayfieldwithtriggers',
                    name: 'defaultFilter',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.defaultfilter, // Default filter
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.defaultfilter'
                    },
                    hidden: true,
                    bind: {
                        value: '{theObject._defaultFilter_description}',
                        hidden: '{!actions.view}',
                        hideTrigger: '{!theObject.defaultFilter}'
                    },
                    triggers: {
                        open: {
                            cls: 'x-fa fa-external-link',
                            handler: function (f, trigger, eOpts) {
                                var value = f.lookupViewModel().get('theObject.defaultFilter'),
                                    filter = Ext.getStore('searchfilters.Searchfilters').findRecord('_id', value),
                                    url = CMDBuildUI.util.administration.helper.ApiHelper.client.getTheViewFilterUrl(filter.get('name'), true);
                                CMDBuildUI.util.Utilities.closeAllPopups();
                                CMDBuildUI.util.Utilities.redirectTo(url);
                            }
                        }
                    },
                    renderer: function (value) {
                        var vm = this.lookupViewModel();
                        vm.bind({
                            bindTo: {
                                store: '{defaultFilterStore}',
                                itemId: '{theObject.defaultFilter}'
                            },
                            single: true
                        }, function (data) {
                            if (data.store && data.itemId) {
                                var record = data.store.findRecord('_id', data.itemId);
                                if (record) {
                                    var _value = record.get('description');
                                    if (value !== _value) {
                                        this.set('theObject._defaultFilter_description', _value);
                                    }
                                }
                            }
                        });
                        return value;
                    }
                }]
            }]
        }, {
            columnWidth: 0.5,
            bind: {
                hidden: '{actions.add}'
            },
            items: [{
                // edit 
                xtype: 'groupedcombo',
                queryMode: 'local',
                displayField: 'description',
                valueField: '_id',
                name: 'defaultimporttemplate',
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.defaultimporttemplate, // Default template for data import
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.defaultimporttemplate'
                },
                allowBlank: true,
                hidden: true,
                bind: {
                    store: '{defaultImportTemplateStore}',
                    value: '{theObject.defaultImportTemplate}',
                    hidden: '{actions.view}'
                },
                triggers: {
                    clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                }
            }, {
                // view
                xtype: 'displayfieldwithtriggers',
                name: 'defaultimporttemplate',
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.defaultimporttemplate, // Default template for data import
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.defaultimporttemplate'
                },
                hidden: true,
                bind: {
                    value: '{theObject._defaultImportTemplate_description}',
                    hidden: '{!actions.view}',
                    hideTrigger: '{!theObject.defaultImportTemplate}'
                },
                triggers: {
                    open: {
                        cls: 'x-fa fa-external-link',
                        handler: function (f, trigger, eOpts) {
                            var value = f.lookupViewModel().get('theObject.defaultImportTemplate'),
                                url = CMDBuildUI.util.administration.helper.ApiHelper.client.getImportExportDataTemplatesUrl(value);
                            CMDBuildUI.util.Utilities.closeAllPopups();
                            CMDBuildUI.util.Utilities.redirectTo(url);
                        }
                    }
                },
                renderer: function (value) {
                    var vm = this.lookupViewModel();
                    vm.bind({
                        bindTo: {
                            store: '{defaultImportTemplateStore}',
                            itemId: '{theObject.defaultImportTemplate}'
                        },
                        single: true
                    }, function (data) {
                        if (data.store && data.itemId) {
                            var record = data.store.findRecord('_id', data.itemId);
                            if (record) {
                                var _value = record.get('description');
                                if (value !== _value) {
                                    this.set('theObject._defaultImportTemplate_description', _value);
                                }
                            }
                        }
                    });
                    return value;
                }
            }]
        }, {
            columnWidth: 0.5,
            bind: {
                hidden: '{actions.add}'
            },
            style: {
                paddingLeft: '15px'
            },
            items: [{
                // edit 
                xtype: 'groupedcombo',
                queryMode: 'local',
                displayField: 'description',
                valueField: '_id',
                name: 'defaultexporttemplate',
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.defaultexporttemplate, // Default template for data export
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.defaultexporttemplate'
                },
                allowBlank: true,
                hidden: true,
                bind: {
                    store: '{defaultExportTemplateStore}',
                    value: '{theObject.defaultExportTemplate}',
                    hidden: '{actions.view}'
                },
                triggers: {
                    clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                }
            }, {
                // view
                xtype: 'displayfieldwithtriggers',
                name: 'defaultexporttemplate',
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.defaultexporttemplate, // Default template for data export
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.defaultexporttemplate'
                },
                hidden: true,
                bind: {
                    value: '{theObject._defaultExportTemplate_description}',
                    hidden: '{!actions.view}',
                    hideTrigger: '{!theObject.defaultExportTemplate}'
                },
                triggers: {
                    open: {
                        cls: 'x-fa fa-external-link',
                        handler: function (f, trigger, eOpts) {
                            var value = f.lookupViewModel().get('theObject.defaultExportTemplate'),
                                url = CMDBuildUI.util.administration.helper.ApiHelper.client.getImportExportDataTemplatesUrl(value);
                            CMDBuildUI.util.Utilities.closeAllPopups();
                            CMDBuildUI.util.Utilities.redirectTo(url);
                        }
                    }
                },
                renderer: function (value) {
                    var vm = this.lookupViewModel();
                    vm.bind({
                        bindTo: {
                            store: '{defaultExportTemplateStore}',
                            itemId: '{theObject.defaultExportTemplate}'
                        },
                        single: true
                    }, function (data) {
                        if (data.store && data.itemId) {
                            var record = data.store.findRecord('_id', data.itemId);
                            if (record) {
                                var _value = record.get('description');
                                if (value !== _value) {
                                    this.set('theObject._defaultExportTemplate_description', _value);
                                }
                            }
                        }
                    });
                    return value;
                }
            }]
        }, {
            columnWidth: 0.5,
            items: [{
                /********************* Inline notes **********************/
                // create / edit / view
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.noteinline, // Inline notes
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.noteinline'
                },
                name: 'noteinline',
                hidden: true,
                bind: {
                    value: '{theObject.noteInline}',
                    readOnly: '{actions.view}',
                    hidden: '{!theObject}'
                }
            }]
        }, {
            columnWidth: 0.5,
            style: {
                paddingLeft: '15px'
            },
            items: [{
                /********************* Closed inline notes **********************/
                // create / edit / view
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.noteinlineclosed, // Closed inline notes
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.noteinlineclosed'
                },
                name: 'noteinlineclosed',
                hidden: true,
                bind: {
                    value: '{theObject.noteInlineClosed}',
                    readOnly: '{actions.view}',
                    hidden: '{!theObject}',
                    disabled: '{checkboxNoteInlineClosed.disabled}'
                }
            }]
        }, {
            columnWidth: 1,
            layout: 'column',
            items: [{
                xtype: 'fieldcontainer',
                layout: 'column',
                style: {
                    paddingRight: 0
                },
                columnWidth: 1,
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.helptext,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.helptext'
                },
                labelToolIconCls: 'fa-flag',
                labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                labelToolIconClick: 'onTranslateHelpClick',

                items: [CMDBuildUI.util.helper.FieldsHelper.getHTMLEditor({
                        columnWidth: 1,
                        bind: {
                            value: '{theObject.help}',
                            hidden: '{actions.view}'
                        }
                    }),
                    {
                        // view
                        xtype: 'displayfield',
                        name: 'defaultexporttemplate',
                        hidden: true,
                        bind: {
                            value: '{theObject.help}',
                            hidden: '{!actions.view}'
                        }
                    }
                ]
            }]
        }]
    }]
});