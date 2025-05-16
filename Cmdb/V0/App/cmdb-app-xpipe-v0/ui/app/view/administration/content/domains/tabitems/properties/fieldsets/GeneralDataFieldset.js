Ext.define('CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.GeneralDataFieldset', {
    extend: 'Ext.panel.Panel',
    requires: [
        'CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.GeneralDataFieldsetController',
        'CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.GeneralDataFieldsetModel'
    ],

    alias: 'widget.administration-content-domains-tabitems-properties-fieldsets-generaldatafieldset',

    controller: 'administration-content-domains-tabitems-properties-fieldsets-generaldatafieldset',
    viewModel: {
        type: 'administration-content-domains-tabitems-properties-fieldsets-generaldatafieldset'
    },
    ui: 'administration-formpagination',
    statics: {
        onHideInlineOptions: function (view) {
            var vm = view.lookupViewModel();
            vm.set('theDomain.isMasterDetail', false);
            vm.set('theDomain.descriptionMasterDetail', '');
            vm.set('theDomain.sourceInline', false);
            vm.set('theDomain.sourceDefaultClosed', false);
            vm.set('theDomain.destinationInline', false);
            vm.set('theDomain.destinationDefaultClosed', false);
        }
    },
    items: [{
        xtype: 'fieldset',
        title: CMDBuildUI.locales.Locales.administration.groupandpermissions.titles.generalattributes,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.titles.generalattributes'
        },
        layout: 'anchor',
        itemId: 'domain-generaldatafieldset',
        ui: 'administration-formpagination',
        items: [{
            layout: 'column',

            items: [{
                columnWidth: 0.5,
                /********************* Name **********************/
                items: [{
                    // create / edit
                    xtype: 'textfield',
                    vtype: 'alphanum',
                    reference: 'domainname',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                    },
                    name: 'name',
                    enforceMaxLength: true,
                    hidden: true,
                    allowBlank: false,
                    bind: {
                        value: '{theDomain.name}',
                        hidden: '{!actions.add}'
                    },
                    listeners: {
                        change: function (input, newVal, oldVal) {
                            CMDBuildUI.util.administration.helper.FieldsHelper.copyTo(input, newVal, oldVal, '[name="description"]');
                        }
                    }
                }, {
                    // view
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                    },
                    name: 'name',
                    hidden: true,
                    bind: {
                        value: '{theDomain.name}',
                        hidden: '{!actions.view}'
                    }
                }, {
                    // edit
                    xtype: 'textfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                    },
                    name: 'name',
                    hidden: true,
                    disabled: true,
                    allowBlank: false,
                    bind: {
                        value: '{theDomain.name}',
                        hidden: '{!actions.edit}'
                    }
                }]
            }, {
                columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                /********************* description **********************/
                items: [{
                    columnWidth: 0.5,
                    xtype: 'fieldcontainer',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description',
                        labelToolIconQtip: 'CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate'
                    },
                    labelToolIconCls: 'fa-flag',
                    labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                    labelToolIconClick: 'onTranslateClickDescription',
                    items: [{
                        xtype: 'textfield',
                        name: 'description',
                        bind: {
                            value: '{theDomain.description}',
                            hidden: '{actions.view}'
                        },
                        allowBlank: false
                    }, {
                        xtype: 'displayfield',
                        hidden: true,
                        bind: {
                            value: '{theDomain.description}',
                            hidden: '{!actions.view}'
                        }
                    }]
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                /********************* Source **********************/
                items: [{
                    // create
                    xtype: 'combobox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.origin,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.origin'
                    },
                    valueField: '_id',
                    displayField: 'label',
                    queryMode: 'local',
                    forceSelection: true,
                    typeAhead: true,
                    allowBlank: false,
                    name: 'source',
                    hidden: true,
                    bind: {
                        value: '{theDomain.source}',
                        hidden: '{actions.view}',
                        disabled: '{actions.edit}',
                        store: '{sourceClassStore}'
                    },

                    triggers: {
                        clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                    },
                    tpl: new Ext.XTemplate(
                        '<tpl for=".">',
                        '<tpl for="group" if="this.shouldShowHeader(group)"><div class="group-header">{[this.showHeader(values.group)]}</div></tpl>',
                        '<div class="x-boundlist-item">{label}</div>',
                        '</tpl>', {
                        shouldShowHeader: function (group) {
                            return this.currentGroup !== group;
                        },
                        showHeader: function (group) {
                            this.currentGroup = group;
                            return group;
                        }
                    }),
                    listeners: {
                        change: 'onSourceChange'
                    }
                }, {
                    // view
                    xtype: 'displayfieldwithtriggers',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.origin,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.origin'
                    },
                    hidden: true,
                    bind: {
                        value: '{theDomain.source}',
                        hidden: '{!actions.view}',
                        hideTrigger: '{!theDomain.source || theDomain.source == "Class" || theDomain.source == "Activity"}'
                    },
                    triggers: {
                        open: {
                            cls: 'x-fa fa-external-link',
                            handler: function (f, trigger, eOpts) {
                                var url,
                                    targetType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(f.getValue()),
                                    target = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(f.getValue());
                                switch (targetType) {
                                    case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                                        url = CMDBuildUI.util.administration.helper.ApiHelper.client.getClassUrl(target.get('name'));
                                        break;
                                    case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                                        url = CMDBuildUI.util.administration.helper.ApiHelper.client.getProcessUrl(target.get('name'));
                                        break;
                                    default:
                                        return;
                                }
                                CMDBuildUI.util.Utilities.closeAllPopups();
                                CMDBuildUI.util.Utilities.redirectTo(url);
                            }
                        }
                    },
                    renderer: function (value, input) {
                        if (value) {
                            var vm = input.lookupViewModel();
                            var storeId = vm.get('theDomain.sourceProcess') ? 'processes.Processes' : 'classes.Classes';
                            var record = Ext.getStore(storeId).getById(vm.get('theDomain.source'));
                            return record && record.get('description');
                        }

                    }
                }]
            }, {
                columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                /********************* destination **********************/
                items: [{
                    xtype: 'combobox',
                    valueField: '_id',
                    displayField: 'label',
                    queryMode: 'local',
                    forceSelection: true,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination'
                    },
                    typeAhead: true,
                    allowBlank: false,
                    name: 'destination',
                    hidden: true,
                    bind: {
                        value: '{theDomain.destination}',
                        hidden: '{actions.view}',
                        disabled: '{actions.edit}',
                        store: '{destinationClassStore}'
                    },

                    triggers: {
                        clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                    },
                    tpl: new Ext.XTemplate(
                        '<tpl for=".">',
                        '<tpl for="group" if="this.shouldShowHeader(group)"><div class="group-header">{[this.showHeader(values.group)]}</div></tpl>',
                        '<div class="x-boundlist-item">{label}</div>',
                        '</tpl>', {
                        shouldShowHeader: function (group) {
                            return this.currentGroup !== group;
                        },
                        showHeader: function (group) {
                            this.currentGroup = group;
                            return group;
                        }
                    }),
                    listeners: {
                        change: 'onDestinationChange'
                    }
                }, {
                    // view
                    xtype: 'displayfieldwithtriggers',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination'
                    },
                    name: 'destination',
                    hidden: true,
                    bind: {
                        value: '{theDomain.destination}',
                        hidden: '{!actions.view}',
                        hideTrigger: '{!theDomain.destination || theDomain.destination == "Class" || theDomain.destination == "Activity"}'
                    },
                    triggers: {
                        open: {
                            cls: 'x-fa fa-external-link',
                            handler: function (f, trigger, eOpts) {
                                var url,
                                    targetType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(f.getValue()),
                                    target = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(f.getValue());
                                switch (targetType) {
                                    case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                                        url = CMDBuildUI.util.administration.helper.ApiHelper.client.getClassUrl(target.get('name'));
                                        break;
                                    case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                                        url = CMDBuildUI.util.administration.helper.ApiHelper.client.getProcessUrl(target.get('name'));
                                        break;
                                    default:
                                        return;
                                }
                                CMDBuildUI.util.Utilities.closeAllPopups();
                                CMDBuildUI.util.Utilities.redirectTo(url);
                            }
                        }
                    },
                    renderer: function (value, input) {
                        if (value) {
                            var vm = input.lookupViewModel();
                            var storeId = vm.get('theDomain.destinationProcess') ? 'processes.Processes' : 'classes.Classes';
                            var record = Ext.getStore(storeId).getById(vm.get('theDomain.destination'));
                            return record && record.get('description');
                        }

                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                /********************* direct description **********************/
                items: [{
                    xtype: 'fieldcontainer',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.directdescription,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.directdescription',
                        labelToolIconQtip: 'CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate'
                    },
                    labelToolIconCls: 'fa-flag',
                    labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                    labelToolIconClick: 'onTranslateClickDirect',
                    items: [{
                        xtype: 'textfield',
                        name: 'descriptionDirect',
                        allowBlank: false,
                        hidden: true,
                        bind: {
                            value: '{theDomain.descriptionDirect}',
                            hidden: '{actions.view}'
                        }
                    }, {
                        xtype: 'displayfield',
                        hidden: true,
                        bind: {
                            value: '{theDomain.descriptionDirect}',
                            hidden: '{!actions.view}'
                        }
                    }]
                }]
            }, {
                columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                /********************* inverse description **********************/
                items: [{
                    xtype: 'fieldcontainer',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.inversedescription,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.inversedescription',
                        labelToolIconQtip: 'CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate'
                    },
                    labelToolIconCls: 'fa-flag',
                    labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                    labelToolIconClick: 'onTranslateClickInverse',
                    items: [{
                        xtype: 'textfield',
                        name: 'descriptionInverse',
                        allowBlank: false,
                        hidden: true,
                        bind: {
                            value: '{theDomain.descriptionInverse}',
                            hidden: '{actions.view}'
                        }
                    }, {
                        xtype: 'displayfield',
                        hidden: true,
                        bind: {
                            value: '{theDomain.descriptionInverse}',
                            hidden: '{!actions.view}'
                        }
                    }]
                }]
            }]
        }, {
            layout: 'column',
            columnWidth: 1,
            items: [{
                layout: 'column',
                columnWidth: 0.5,
                /********************* direct cascade action **********************/
                items: [{
                    // placeholder element
                    xtype: 'fieldcontainer',
                    hidden: true,
                    bind: {
                        hidden: '{!theDomain.sourceProcess}'
                    },
                    items: [{
                        xtype: 'displayfield'
                    }]
                }, {
                    hidden: true,
                    bind: {
                        hidden: '{theDomain.sourceProcess}'
                    },
                    columnWidth: 0.75,
                    layout: 'column',
                    items: [CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('cascadeActionDirect', {
                        cascadeActionDirect: {
                            columnWidth: 1,
                            fieldcontainer: {
                                fieldLabel: CMDBuildUI.locales.Locales.administration.domains.texts.onorigincarddelete,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.texts.onorigincarddelete'
                                },
                                disabledCls: 'x-item-disabled-forced',
                                bind: {
                                    disabled: '{!theDomain.source || theDomain.sourceProcess}'
                                }
                            },
                            disabled: true,
                            bind: {
                                disabled: '{!theDomain.source || theDomain.sourceProcess}',
                                store: '{cascadeActionsDirectStore}',
                                value: '{theDomain.cascadeActionDirect}'
                            },

                            combofield: {
                                listeners: {
                                    disable: function (input, eOpts) {
                                        input.setValue(null);
                                        CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, true, input.up('form'));
                                    },
                                    enable: function (input, eOpts) {
                                        input.setValue(null);
                                        CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, false, input.up('form'));
                                    },
                                    change: function (combo, newValue, oldValue) {
                                        var vm = combo.lookupViewModel();
                                        if (newValue === CMDBuildUI.model.domains.Domain.cascadeAction.restrict) {
                                            vm.set('theDomain.cascadeActionDirect_askConfirm', false);
                                        }
                                    }
                                }
                            },
                            triggers: {
                                clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                            }
                        }
                    })]
                }, {
                    hidden: true,
                    bind: {
                        hidden: '{theDomain.sourceProcess}'
                    },
                    columnWidth: 0.25,
                    layout: 'column',
                    items: [CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput('cascadeActionDirect_askConfirm', {
                        cascadeActionDirect_askConfirm: {
                            columnWidth: 1,
                            fieldcontainer: {
                                fieldLabel: CMDBuildUI.locales.Locales.administration.domains.texts.askconfirm,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.texts.askconfirm'
                                },
                                disabledCls: 'x-item-disabled-forced',
                                bind: {
                                    disabled: '{cascadeActionDirect_askConfirm_disabled}'
                                }
                            },
                            disabledCls: 'x-item-disabled-forced',
                            disabled: true,
                            bind: {
                                value: '{theDomain.cascadeActionDirect_askConfirm}',
                                disabled: '{!theDomain.source || theDomain.sourceProcess || actions.view  ||  cascadeActionDirect_askConfirm_disabled}'
                            },
                            listeners: {
                                disable: function (input, eOpts) {
                                    input.setValue(false);
                                }
                            }
                        }
                    })]
                }]
            }, {
                margin: '0 0 0 15',
                layout: 'column',
                columnWidth: 0.5,
                /********************* direct cascade action **********************/
                items: [{
                    hidden: true,
                    bind: {
                        hidden: '{theDomain.destinationProcess}'
                    },
                    columnWidth: 0.75,
                    layout: 'column',
                    items: [CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('cascadeActionInverse', {
                        cascadeActionInverse: {
                            columnWidth: 1,
                            fieldcontainer: {
                                fieldLabel: CMDBuildUI.locales.Locales.administration.domains.texts.destinationcarddelete,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.texts.destinationcarddelete'
                                },
                                disabledCls: 'x-item-disabled-forced',
                                bind: {
                                    disabled: '{!theDomain.destination || theDomain.destinationProcess}'
                                }
                            },
                            disabled: true,
                            bind: {
                                store: '{cascadeActionsInverseStore}',
                                value: '{theDomain.cascadeActionInverse}',
                                disabled: '{!theDomain.destination || theDomain.destinationProcess}'
                            },
                            combofield: {
                                listeners: {
                                    disable: function (input, eOpts) {
                                        var vm = input.lookupViewModel();
                                        vm.set('theDomain.cascadeActionInverse', null);
                                        CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, true, input.up('form'));
                                    },
                                    enable: function (input, eOpts) {
                                        CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, false, input.up('form'));
                                    },
                                    change: function (combo, newValue, oldValue) {
                                        var vm = combo.lookupViewModel();
                                        if (newValue === CMDBuildUI.model.domains.Domain.cascadeAction.restrict) {
                                            vm.set('theDomain.cascadeActionInverse_askConfirm', false);
                                        }
                                    }
                                }
                            },
                            triggers: {
                                clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                            }
                        }
                    })]
                }, {
                    hidden: true,
                    bind: {
                        hidden: '{theDomain.destinationProcess}'
                    },
                    columnWidth: 0.25,
                    layout: 'column',
                    items: [CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput('cascadeActionInverse_askConfirm', {
                        cascadeActionInverse_askConfirm: {
                            columnWidth: 1,
                            fieldcontainer: {
                                fieldLabel: CMDBuildUI.locales.Locales.administration.domains.texts.askconfirm,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.texts.askconfirm'
                                },
                                disabledCls: 'x-item-disabled-forced',
                                bind: {
                                    disabled: '{cascadeActionInverse_askConfirm_disabled}'
                                }
                            },
                            disabledCls: 'x-item-disabled-forced',
                            disabled: true,
                            bind: {
                                disabled: '{!theDomain.destination || theDomain.destinationProcess || actions.view  || cascadeActionInverse_askConfirm_disabled}',
                                value: '{theDomain.cascadeActionInverse_askConfirm}'
                            },
                            listeners: {
                                disable: function (input, eOpts) {
                                    input.setValue(false);
                                }
                            }
                        }
                    })]
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                /********************* cardinality **********************/
                items: [{
                    // create
                    xtype: 'combobox',
                    queryMode: 'local',
                    forceSelection: true,
                    displayField: 'label',
                    valueField: 'value',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.cardinality,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.cardinality'
                    },
                    name: 'cardinality',
                    allowBlank: false,
                    hidden: true,
                    bind: {
                        store: '{cardinalityStore}',
                        value: '{theDomain.cardinality}',
                        hidden: '{actions.view}',
                        disabled: '{actions.edit}'
                    },
                    listeners: {
                        change: 'resetSummaryGrid'
                    }
                }, {
                    // view
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.cardinality,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.cardinality'
                    },
                    name: 'cardinality',
                    hidden: true,
                    bind: {
                        value: '{theDomain.cardinality}',
                        hidden: '{!actions.view}'
                    }
                }]
            }]
        }, {
            layout: 'column',
            columnWidth: 1,
            hidden: true,
            bind: {
                hidden: '{ theDomain.cardinality === "1:1"}'
            },
            listeners: {
                hide: function (view) {
                    CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.GeneralDataFieldset.onHideInlineOptions(view);
                }
            },
            items: [{
                layout: 'column',
                columnWidth: 0.5,
                minHeight: '1',
                items: [{
                    columnWidth: 0.5,
                    /********************* Inline **********************/
                    items: [CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput('sourceInline', {
                        sourceInline: {
                            columnWidth: 1,
                            fieldcontainer: {
                                fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.origininline,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.origininline'
                                },
                                disabledCls: 'x-item-disabled-forced',
                                disabled: true,
                                bind: {
                                    disabled: '{!theDomain.source || theDomain.sourceProcess|| !theDomain.cardinality || theDomain.cardinality === "N:1"}'
                                },
                                listeners: {
                                    disable: function (fieldcontainer, eOpts) {
                                        fieldcontainer.lookupViewModel().set('theDomain.sourceInline', false);
                                    }
                                }
                            },
                            bind: {
                                value: '{theDomain.sourceInline}',
                                readOnly: '{actions.view}',
                                disabled: '{!theDomain.source || theDomain.sourceProcess|| !theDomain.cardinality || theDomain.cardinality === "N:1"}'
                            },
                            listeners: {
                                change: function (checkbox, newValue, oldValue) {
                                    if (!newValue) {
                                        checkbox.lookupViewModel().set('theDomain.sourceDefaultClosed', false);
                                    }
                                }
                            }
                        }
                    })]
                }, {
                    columnWidth: 0.5,
                    /********************* Default closed **********************/
                    items: [CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput('sourceDefaultClosed', {
                        sourceDefaultClosed: {
                            columnWidth: 1,
                            fieldcontainer: {
                                fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.closedorigininline,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.closedorigininline'
                                },
                                disabledCls: 'x-item-disabled-forced',
                                disabled: true,
                                bind: {
                                    disabled: '{!theDomain.source || theDomain.sourceProcess || !theDomain.cardinality || theDomain.cardinality === "N:1" || !theDomain.sourceInline}'
                                },
                                listeners: {
                                    disable: function (fieldcontainer, eOpts) {
                                        fieldcontainer.lookupViewModel().set('theDomain.sourceDefaultClosed', false);
                                    }
                                }
                            },
                            bind: {
                                value: '{theDomain.sourceDefaultClosed}',
                                readOnly: '{actions.view}',
                                disabled: '{!theDomain.source || theDomain.sourceProcess || !theDomain.cardinality || theDomain.cardinality === "N:1" || !theDomain.sourceInline}'
                            }
                        }
                    })]
                }]
            }, {
                layout: 'column',
                columnWidth: 0.5,

                items: [{
                    layout: 'column',
                    columnWidth: 0.5,
                    style: {
                        paddingLeft: '15px'
                    },
                    /********************* Inline **********************/
                    items: [CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput('destinationInline', {
                        destinationInline: {
                            columnWidth: 1,
                            fieldcontainer: {
                                fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destinationinline,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destinationinline'
                                },
                                disabledCls: 'x-item-disabled-forced',
                                disabled: true,
                                bind: {
                                    disabled: '{!theDomain.destination || theDomain.destinationProcess || !theDomain.cardinality || theDomain.cardinality === "1:N"}'
                                },
                                listeners: {
                                    disable: function (fieldcontainer, eOpts) {
                                        fieldcontainer.lookupViewModel().set('theDomain.destinationInline', false);
                                    }
                                }
                            },
                            bind: {
                                value: '{theDomain.destinationInline}',
                                readOnly: '{actions.view}',
                                disabled: '{!theDomain.destination || theDomain.destinationProcess || !theDomain.cardinality || theDomain.cardinality === "1:N"}'
                            },
                            listeners: {
                                change: function (checkbox, newValue, oldValue) {
                                    if (!newValue) {
                                        checkbox.lookupViewModel().set('theDomain.destinationDefaultClosed', false);
                                    }
                                }
                            }
                        }
                    })]
                }, {
                    columnWidth: 0.5,
                    /********************* Default closed **********************/
                    items: [CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput('destinationDefaultClosed', {
                        destinationDefaultClosed: {
                            columnWidth: 1,
                            fieldcontainer: {
                                fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.closeddestinationinline,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.closeddestinationinline'
                                },
                                disabledCls: 'x-item-disabled-forced',
                                disabled: true,
                                bind: {
                                    disabled: '{!theDomain.destination || theDomain.destinationProcess || !theDomain.cardinality || theDomain.cardinality === "1:N" || !theDomain.destinationInline}'
                                },
                                listeners: {
                                    disable: function (fieldcontainer, eOpts) {
                                        fieldcontainer.lookupViewModel().set('theDomain.destinationDefaultClosed', false);
                                    }
                                }
                            },
                            bind: {
                                value: '{theDomain.destinationDefaultClosed}',
                                readOnly: '{actions.view}',
                                disabled: '{!theDomain.destination || theDomain.destinationProcess || !theDomain.cardinality || theDomain.cardinality === "1:N" || !theDomain.destinationInline}'
                            }
                        }
                    })]
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                /********************* Active **********************/
                items: [{
                    // create / edit / view
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                    },
                    name: 'active',
                    hidden: true,
                    bind: {
                        value: '{theDomain.active}',
                        readOnly: '{actions.view}',
                        hidden: '{!theDomain}'
                    }
                }]
            }]
        }]
    }]
});