Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.Lookup', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-attribute-lookupfields',
    listeners: {
        hide: function (component, eOpts) {
            var input = component.down('#lookupTypeName_input');
            input.setValue('');
            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, true, input.up('form'));
        },
        show: function (component, eOpts) {
            var input = component.down('#lookupTypeName_input');
            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, false, input.up('form'));
        }
    },
    items: [{
        // add - edit
        xtype: 'container',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },

        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'fieldcontainer',
                layout: 'column',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.lookup,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.lookup'
                },
                autoEl: {
                    'data-testid': 'attribute-lookupTypeName_input'
                },
                items: [{
                    // ADD / EDIT
                    columnWidth: 1,
                    xtype: 'combo',
                    itemId: 'lookupTypeName_input',
                    name: 'lookup',
                    clearFilterOnBlur: false,
                    anyMatch: true,
                    queryMode: 'local',
                    autoSelect: true,
                    forceSelection: true,
                    displayField: 'name',
                    valueField: '_id',
                    bind: {
                        value: '{theAttribute.lookupType}',
                        store: '{lookupStore}',
                        disabled: '{actions.edit}',
                        hidden: '{actions.view}'
                    }
                }]
            }, {
                columnWidth: 0.5,
                xtype: 'textarea',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.filter,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.filter'
                },
                autoEl: {
                    'data-testid': 'attribute-filter_input'
                },
                name: 'filter',
                bind: {
                    value: "{theAttribute.filter}"
                },
                resizable: {
                    handles: "s"
                },
                labelToolIconCls: 'fa-list',
                labelToolIconQtip: 'Edit metadata',
                labelToolIconClick: 'onEditMetadataClickBtn'
            }]
        }, {
            layout: 'column',
            hidden: true,
            bind: {
                hidden: Ext.String.format('{theAttribute.type === "{0}"}', CMDBuildUI.model.Attribute.types.lookuparray)
            },
            listeners: {
                hide: function () {
                    var vm = this.lookupViewModel();
                    vm.set('theAttribute.preselectIfUnique', false);
                }
            },
            items: [{
                columnWidth: 0.5,
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.preselectifunique,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.preselectifunique'
                },
                name: 'preselectIfUnique',
                autoEl: {
                    'data-testid': 'attribute-preselectIfUnique_input'
                },
                bind: {
                    value: '{theAttribute.preselectIfUnique}'
                }
            }]

        }]
    }, {
        // view
        xtype: 'container',
        bind: {
            hidden: '{!actions.view}'
        },
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'displayfieldwithtriggers',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.lookup,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.lookup'
                },
                autoEl: {
                    'data-testid': 'attribute-lookupTypeName_display'
                },
                bind: {
                    value: '{theAttribute.lookupType}',
                    hidden: '{!actions.view}'
                },
                triggers: {
                    open: {
                        cls: 'x-fa fa-external-link',
                        handler: function (f, trigger, eOpts) {
                            var value = f.getValue(),
                                url = CMDBuildUI.util.administration.helper.ApiHelper.client.getTheLookupTypeUrl(CMDBuildUI.util.Utilities.stringToHex(value));
                            CMDBuildUI.util.Utilities.closeAllPopups();
                            CMDBuildUI.util.Utilities.redirectTo(url);
                        }
                    }
                }
            }, {
                columnWidth: 0.5,
                xtype: 'textarea',
                itemId: 'attribute-filterField',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.filter,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.filter'
                },
                name: 'filter',
                readOnly: true,
                autoEl: {
                    'data-testid': 'attribute-filter_display'
                },
                bind: {
                    value: "{theAttribute.filter}"
                },
                resizable: {
                    handles: "s"
                },
                labelToolIconCls: 'fa-list',
                labelToolIconQtip: 'Show metadata',
                labelToolIconClick: 'onViewMetadataClick'
            }]
        }, {
            layout: 'column',
            hidden: true,
            bind: {
                hidden: Ext.String.format('{theAttribute.type === "{0}"}', CMDBuildUI.model.Attribute.types.lookuparray)
            },
            items: [{
                columnWidth: 0.5,
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.preselectifunique,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.preselectifunique'
                },
                autoEl: {
                    'data-testid': 'attribute-preselectIfUnique_display'
                },
                name: 'preselectIfUnique',
                bind: {
                    value: '{theAttribute.preselectIfUnique}',
                    readOnly: '{actions.view}'
                }
            }]
        }]
    }]
});