Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.Foreignkey', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-attribute-foreignkeyfields',
    items: [{
        xtype: 'container',
        bind: {
            //hidden: '{actions.view}'
        },
        items: [{
            layout: 'column',
            columnWidth: 1,
            items: [{
                columnWidth: 0.5,
                xtype: 'allelementscombo',
                fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination,
                name: 'initialPage',
                withClasses: true,
                withProcesses: true,
                hidden: true,
                bind: {
                    value: '{theAttribute.targetClass}',
                    hidden: '{actions.view}'
                }
            }, CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('cascadeAction', {
                cascadeAction: {
                    layout: 'column',
                    columnWidth: 1,
                    userCls: 'with-tool-nomargin',
                    fieldcontainer: {
                        columnWidth: 0.5,
                        fieldLabel: CMDBuildUI.locales.Locales.administration.domains.texts.destinationcarddelete,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.texts.destinationcarddelete'
                        },
                        hidden: true,
                        allowBlank: false,
                        bind: {
                            hidden: '{actions.view}'
                        }
                    },
                    displayfield: {
                        hidden: true
                    },
                    combofield: {
                        hidden: true,
                        bind: {
                            store: '{cascadeActionsStore}',
                            value: '{theAttribute.cascadeAction}',
                            hidden: '{!types.isForeignkey}'
                        },
                        listeners: {
                            hide: function (input, eOpts) {
                                var vm = input.lookupViewModel();
                                vm.set('theAttribute.cascadeAction', null);
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, true, input.up('form'));
                            },
                            show: function (input, eOpts) {
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, false, input.up('form'));
                            }
                        }
                    },
                    triggers: {
                        clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                    }
                }

            })]

        }]
    }, {
        xtype: 'container',
        hidden: true,
        bind: {
            hidden: '{actions.view || targetClassIsProcess || !theAttribute.targetClass}'
        },
        items: [{
            layout: 'column',
            bind: {
                hidden: '{objectType !== "Class"}'
            },
            items: [{
                columnWidth: 0.5,
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.masterdetail,
                name: 'isMasterDetail',
                bind: {
                    value: '{theAttribute.isMasterDetail}'
                }
            }, {
                columnWidth: 0.5,
                xtype: 'textfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.labelmasterdataillong,
                name: 'masterDetailDescription',
                bind: {
                    value: '{theAttribute.masterDetailDescription}'
                },
                labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                labelToolIconClick: 'onTranslateClickMasterDetail'
            }]

        }]
    }, {
        xtype: 'fieldcontainer',
        hidden: true,
        bind: {
            hidden: '{!actions.view || targetClassIsProcess || !theAttribute.targetClass}'
        },
        items: [{
            layout: 'column',
            columnWidth: 1,
            items: [{
                columnWidth: 0.5,
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination,
                name: 'targetClass',
                withClasses: true,
                withProcesses: true,
                onlyDisplay: true,
                bind: {
                    value: '{theAttribute.targetClassDescription}',
                    disabled: '{actions.edit}'
                }
            }, CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('cascadeAction', {
                cascadeAction: {
                    layout: 'column',
                    columnWidth: 1,
                    userCls: 'with-tool-nomargin',
                    fieldcontainer: {
                        columnWidth: 0.5,
                        fieldLabel: CMDBuildUI.locales.Locales.administration.domains.texts.destinationcarddelete,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.texts.destinationcarddelete'
                        }
                    },
                    displayfield: {
                        bind: {
                            value: '{theAttribute._cascadeAction_description}'
                        },
                        listeners: {
                            afterrender: function (view) {
                                var me = this,
                                    vm = me.lookupViewModel();
                                vm.bind({
                                    bindTo: {
                                        value: '{theAttribute.cascadeAction}',
                                        store: '{cascadeActionsStore}'
                                    },
                                    single: true
                                }, function (data) {
                                    if (data.store && !vm.isDestroyed) {
                                        var record = data.store.findRecord('value', data.value);
                                        if (record) {
                                            vm.set('theAttribute._cascadeAction_description', record.get('label'));
                                        }
                                    }
                                });
                            }
                        }
                    },
                    combofield: {
                        hidden: true
                    }
                }

            })]

        }, {
            layout: 'column',
            hidden: true,
            bind: {
                hidden: '{objectType !== "Class"}'
            },
            items: [{
                columnWidth: 0.5,
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.masterdetail,
                readOnly: true,
                bind: {
                    value: '{theAttribute.isMasterDetail}'
                }
            }, {
                columnWidth: 0.5,
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.labelmasterdataillong,
                bind: {
                    value: '{theAttribute.masterDetailDescription}'
                }
            }]

        }]
    }]
});