Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.Reference', {

    required: ['CMDuildUI.util.administration.helper.FieldsHelper'],
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-attribute-referencefields',
    listeners: {
        hide: function (component, eOpts) {
            var input = component.down('#attributedomain');
            input.setValue('');
            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, true, input.up('form'));
        },
        show: function (component, eOpts) {
            var input = component.down('#attributedomain');
            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, false, input.up('form'));
        }
    },
    items: [{
        // add / edit
        xtype: 'container',
        bind: {
            hidden: '{actions.view}'
        },
        layout: 'column',
        columnWidth: 1,
        items: [{
            layout: 'column',
            columnWidth: 1,
            items: [{
                xtype: 'container',
                columnWidth: 0.5,
                layout: 'column',
                items: [{
                    xtype: 'fieldcontainer',
                    columnWidth: 1,
                    width: '100%',
                    layout: 'column',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.domain,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.domain'
                    },
                    items: [{
                        columnWidth: 1,
                        width: '100%',
                        xtype: 'combobox',
                        name: 'domain',
                        itemId: 'attributedomain',
                        clearFilterOnBlur: true,
                        queryMode: 'local',
                        displayField: 'description',
                        valueField: 'name',
                        forceSelection: true,
                        bind: {
                            value: '{theAttribute.domain}',
                            store: '{domainsStore}',
                            disabled: '{actions.edit}'
                        },

                        /**
                         * @override
                         * Uses {@link #getErrors} to build an array of validation errors. If any errors are found, they are passed to
                         * {@link #markInvalid} and false is returned, otherwise true is returned.
                         *
                         * Previously, subclasses were invited to provide an implementation of this to process validations - from 3.2
                         * onwards {@link #getErrors} should be overridden instead.
                         *
                         * @param {Object} value The value to validate
                         * @return {Boolean} True if all validations passed, false if one or more failed
                         */
                        validateValue: function (value) {
                            var me = this,
                                errors = me.getErrors(value),
                                vm = this.lookupViewModel(),
                                isValid;

                            var type = this.up('fieldset').down('[name="type"]').getValue();
                            if (type === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference) {

                                this.allowBlank = false;
                                var referenceExist = Ext.Array.findBy(vm.get('attributes'), function (attribute, index) {
                                    return attribute.getId() !== vm.get('theAttribute').getId() && attribute.get('domain') === value &&
                                        attribute.get('type') === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference && !Ext.isEmpty(vm.get('theAttribute.direction')) && vm.get('theAttribute.direction').toLowerCase() === attribute.get('direction').toLowerCase();
                                });
                                if (referenceExist) {
                                    var store = this.getStore();
                                    var description = value;
                                    if (store) {
                                        var record = store.findRecord('name', value);

                                        description = record && record.get('description');
                                    }
                                    var error = Ext.String.format(CMDBuildUI.locales.Locales.administration.domains.strings.referencealreadydefined,
                                        description,
                                        referenceExist.get('description')
                                    );
                                    errors.push(error);
                                }
                                isValid = Ext.isEmpty(errors);
                            } else {
                                this.allowBlank = true;
                                isValid = true;
                            }

                            if (!me.preventMark) {
                                if (isValid) {
                                    me.clearInvalid();
                                } else {
                                    me.markInvalid(errors);
                                }
                            }
                            return isValid;
                        }
                    }]
                }, {

                    columnWidth: 1,
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.preselectifunique,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.preselectifunique'
                    },
                    name: 'preselectIfUnique',
                    bind: {
                        value: '{theAttribute.preselectIfUnique}'
                    }
                }]
            },
            {
                columnWidth: 0.5,
                items: [{
                    xtype: 'fieldcontainer',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.filter,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.filter'
                    },
                    labelToolIconCls: 'fa-list',
                    labelToolIconQtip: 'Edit metadata',
                    labelToolIconClick: 'onEditMetadataClickBtn',

                    items: [{
                        columnWidth: 0.5,
                        xtype: 'textarea',
                        name: 'filter',
                        hidden: true,
                        bind: {
                            hidden: '{theAttribute.useDomainFilter}',
                            value: "{theAttribute.filter}"
                        },
                        resizable: {
                            handles: "s"
                        }
                    }]
                }, {
                    columnWidth: 0.5,
                    xtype: 'checkbox',
                    margin: '-10px 0 0 0',
                    boxLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.usedomainfilter,
                    localized: {
                        boxLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.usedomainfilter'
                    },
                    name: 'useDomainFilter',
                    bind: {
                        value: '{theAttribute.useDomainFilter}'
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'combobox',
                name: 'domain',
                clearFilterOnBlur: true,
                queryMode: 'local',
                displayField: 'label',
                valueField: 'value',
                itemId: 'domaindirection',
                forceSelection: true,
                hidden: true,
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.texts.direction,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.texts.direction'
                },
                bind: {
                    value: '{theAttribute.direction}',
                    store: '{directionStore}',
                    disabled: '{actions.edit}'
                },
                /**
                 * @override
                 * Uses {@link #getErrors} to build an array of validation errors. If any errors are found, they are passed to
                 * {@link #markInvalid} and false is returned, otherwise true is returned.
                 *
                 * Previously, subclasses were invited to provide an implementation of this to process validations - from 3.2
                 * onwards {@link #getErrors} should be overridden instead.
                 *
                 * @param {Object} value The value to validate
                 * @return {Boolean} True if all validations passed, false if one or more failed
                 */
                validateValue: function (value) {
                    var me = this,
                        errors = me.getErrors(value),
                        vm = this.lookupViewModel(),
                        isValid;
                    if (vm.get('theAttribute.type') !== CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference) {
                        return true;
                    }
                    this.allowBlank = false;
                    var referenceExist = Ext.Array.findBy(vm.get('attributes'), function (attribute, index) {
                        return attribute.get('domain') === vm.get('theAttribute.domain') && value && value.toLowerCase() === attribute.get('direction').toLowerCase();
                    });
                    var domainField = me.up('fieldset').down('[name="domain"]');
                    if (referenceExist) {
                        var store = domainField.getStore();
                        var description = value;
                        if (store) {
                            var record = store.findRecord('name', vm.get('theAttribute.domain'));
                            if (record) {
                                description = record && record.get('description');
                            }
                        }
                        var error = Ext.String.format(CMDBuildUI.locales.Locales.administration.domains.strings.referencealreadydefined,
                            description,
                            referenceExist.get('description')
                        );
                        errors.push(error);
                    }
                    isValid = Ext.isEmpty(errors);

                    if (!me.preventMark) {
                        if (isValid) {
                            me.clearInvalid();
                        } else {
                            me.markInvalid(errors);
                        }
                    }
                    domainField.validateValue(vm.get('theAttribute.domain'));
                    return isValid;
                }
            }]
        }]
    }, {
        // add / edit
        xtype: 'container',
        bind: {
            hidden: '{!actions.view}'
        },
        layout: 'column',
        columnWidth: 1,
        items: [{
            layout: 'column',
            columnWidth: 1,
            items: [{
                xtype: 'container',
                columnWidth: 0.5,
                layout: 'column',
                items: [{
                    xtype: 'fieldcontainer',
                    columnWidth: 1,
                    width: '100%',
                    layout: 'column',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.domain,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.domain'
                    },
                    items: [{
                        columnWidth: 0.5,
                        xtype: 'displayfieldwithtriggers',
                        bind: {
                            value: '{theAttribute.domain}'
                        },
                        triggers: {
                            open: {
                                cls: 'x-fa fa-external-link',
                                handler: function (f, trigger, eOpts) {
                                    var value = f.getValue(),
                                        url = CMDBuildUI.util.administration.helper.ApiHelper.client.getDomainUrl(value);
                                    CMDBuildUI.util.Utilities.closeAllPopups();
                                    CMDBuildUI.util.Utilities.redirectTo(url);
                                }
                            }
                        },
                        renderer: function (value) {
                            var domainStore = Ext.getStore('domains.Domains');
                            if (domainStore) {
                                var domain = domainStore.getById(value);
                                if (domain) {
                                    return domain.get('description');
                                }
                                return value;
                            }
                        }
                    }]
                }, {

                    columnWidth: 1,
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.preselectifunique,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.preselectifunique'
                    },
                    name: 'preselectIfUnique',
                    readOnly: true,
                    bind: {
                        value: '{theAttribute.preselectIfUnique}'
                    }
                }]
            },
            {
                columnWidth: 0.5,
                items: [{
                    xtype: 'fieldcontainer',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.filter,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.filter'
                    },
                    items: [{
                        columnWidth: 0.5,
                        xtype: 'textarea',
                        itemId: 'attribute-filterField',

                        name: 'filter',
                        readOnly: true,
                        hidden: true,
                        bind: {
                            value: "{theAttribute.filter}",
                            hidden: "{theAttribute.useDomainFilter}"
                        },
                        resizable: {
                            handles: "s"
                        }
                    }]
                }]
            }, {
                columnWidth: 0.5,
                xtype: 'checkbox',
                margin: '-10px 0 0 0',
                boxLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.usedomainfilter,
                localized: {
                    boxLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.usedomainfilter'
                },
                name: 'useDomainFilter',
                readOnly: true,
                bind: {
                    value: '{theAttribute.useDomainFilter}'
                }
            }]
        }]
    }]
});