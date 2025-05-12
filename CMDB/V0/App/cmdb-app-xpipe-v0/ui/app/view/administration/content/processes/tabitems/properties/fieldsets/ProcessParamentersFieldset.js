Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.properties.fieldsets.ProcessParamentersFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-processes-tabitems-properties-fieldsets-processparametersfieldset',
    viewModel: {},
    bind: {
        hidden: '{actions.add}'
    },
    items: [{
        xtype: 'fieldset',
        layout: 'column',
        title: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.title,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.title'
        },
        ui: 'administration-formpagination',
        collapsible: true,
        items: [{
            columnWidth: 0.5,
            items: [{
                /********************* description **********************/
                // create / edit
                xtype: 'combo',
                typeAhead: true,
                queryMode: 'local',
                reference: 'flowStatusAttr',
                displayField: 'description',
                valueField: 'name',
                name: 'flowStatusAttr',
                fieldLabel: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.inputs.flowStatusAttr.label,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.inputs.flowStatusAttr.label'
                },
                hidden: true,
                bind: {
                    store: '{allLookupAttributesStore}',
                    value: '{theProcess.flowStatusAttr}',
                    hidden: '{actions.view}'
                },
                triggers: {
                    clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                }
            }, {
                // view
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.inputs.flowStatusAttr.label,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.inputs.flowStatusAttr.label'
                },
                hidden: true,
                bind: {
                    value: '{theProcess._flowStatusAttr_description}',
                    hidden: '{!actions.view}'
                }
            }, {
                /********************* Default Filter **********************/
                // create / edit
                xtype: 'combobox',
                editable: false,
                reference: 'defaultFilter',
                displayField: 'name',
                valueField: '_id',
                name: 'defaultFilter',
                fieldLabel: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.inputs.defaultFilter.label,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.inputs.defaultFilter.label'
                },
                hidden: true,
                bind: {
                    store: '{defaultFilterStore}',
                    value: '{theProcess.defaultFilter}',
                    hidden: '{actions.view}'
                },
                triggers: {
                    clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                }

            }, {
                // view
                xtype: 'displayfieldwithtriggers',
                name: 'defaultFilter',
                fieldLabel: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.inputs.defaultFilter.label,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.inputs.defaultFilter.label'
                },
                hidden: true,
                bind: {
                    value: '{theProcess.defaultFilter}', // TODO: this work when #619 is fixed
                    hidden: '{!actions.view}',
                    hideTrigger: '{!theProcess.defaultFilter}'
                },
                triggers: {
                    open: {
                        cls: 'x-fa fa-external-link',
                        handler: function (f, trigger, eOpts) {
                            var value = f.lookupViewModel().get('theProcess.defaultFilter'),
                                filter = Ext.getStore('searchfilters.Searchfilters').findRecord('_id', value),
                                url = CMDBuildUI.util.administration.helper.ApiHelper.client.getTheViewFilterUrl(filter.get('name'), true);
                            CMDBuildUI.util.Utilities.closeAllPopups();
                            CMDBuildUI.util.Utilities.redirectTo(url);
                        }
                    }
                },
                renderer: function (value) {
                    var defaultFilterStore = Ext.getStore('searchfilters.Searchfilters');
                    if (defaultFilterStore) {
                        var record = defaultFilterStore.findRecord('_id', value);
                        if (record) {
                            return record.get('description');
                        }
                    }
                    return value;
                }
            }]
        }, {
            columnWidth: 0.5,
            style: {
                paddingLeft: '15px'
            },
            items: [{
                /********************* messageAttr **********************/
                // create / edit
                xtype: 'combobox',
                typeAhead: true,
                queryMode: 'local',
                reference: 'messageAttr',
                displayField: 'description',
                valueField: '_id',
                name: 'messageAttr',
                fieldLabel: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.inputs.messageAttr.label,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.inputs.messageAttr.label'
                },
                hidden: true,
                bind: {
                    store: '{messageAttributesStore}',
                    value: '{theProcess.messageAttr}',
                    hidden: '{actions.view}'
                },
                triggers: {
                    clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                }
            }, {
                // view
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.inputs.messageAttr.label,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.inputs.messageAttr.label'
                },
                hidden: true,
                bind: {
                    value: '{theProcess._messageAttr_description}', // TODO: this work when #619 #621 is fixed
                    hidden: '{!actions.view}'
                }
            }, {
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
                    value: '{theProcess.defaultExportTemplate}',
                    hidden: '{actions.view}'
                },
                triggers: {
                    clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                }
            }, {
                // view
                xtype: 'displayfield',
                name: 'defaultexporttemplate',
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.defaultexporttemplate, // Default template for data export
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.defaultexporttemplate'
                },
                hidden: true,
                bind: {
                    value: '{theProcess._defaultExportTemplate_description}',
                    hidden: '{!actions.view}'
                },
                renderer: function (value) {
                    var vm = this.lookupViewModel();
                    vm.bind({
                        bindTo: {
                            store: '{defaultExportTemplateStore}',
                            itemId: '{theProcess.defaultExportTemplate}'
                        },
                        single: true
                    }, function (data) {
                        if (data.store && data.itemId) {
                            var record = data.store.findRecord('_id', data.itemId);
                            if (record) {
                                var _value = record.get('description');
                                if (value !== _value) {
                                    this.set('theProcess._defaultExportTemplate_description', _value);
                                }
                            }
                        }
                    });
                    return value;
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
                            value: '{theProcess.help}',
                            hidden: '{actions.view}'
                        }
                    }),
                    {
                        // view
                        xtype: 'displayfield',
                        name: 'defaultexporttemplate',
                        hidden: true,
                        bind: {
                            value: '{theProcess.help}',
                            hidden: '{!actions.view}'
                        }
                    }
                ]
            }]
        }]
    }]
});