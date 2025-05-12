Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.String', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-attribute-stringfields',
    config: {
        theAttribute: null,
        actions: {}
    },
    items: [{
        // add // edit
        xtype: 'container',
        bind: {
            hidden: '{actions.view}'
        },
        items: [{
            layout: 'column',

            items: [{
                columnWidth: 0.5,
                xtype: 'combo',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.editortype,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.editortype'
                },
                name: 'password',
                autoEl: {
                    'data-testid': 'attribute-password_input'
                },
                allowBlank: false,
                queryMode: "local",
                displayField: 'label',
                valueField: 'value',
                disabled: true,
                bind: {
                    disabled: '{actions.edit}',
                    value: '{theAttribute.password}',
                    store: '{stringEditorTypesStore}'
                },
                listeners: {
                    change: function (combo, newValue, oldValue) {
                        var vm = combo.lookupViewModel();
                        if (newValue === 'true' && oldValue) {
                            vm.set('theAttribute.textContentSecurity', CMDBuildUI.model.Attribute.textContentSecurity.plaintext);
                            vm.set('theAttribute.showPassword', CMDBuildUI.model.Attribute.showPassword.always);
                        }
                    }
                }
            }, {
                columnWidth: 0.5,
                xtype: 'combo',
                itemId: 'attribute-showPassword',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showPassword,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showPassword'
                },
                valueField: 'value',
                displayField: 'label',
                name: 'showPassword',
                autoEl: {
                    'data-testid': 'attribute-showPassword_input'
                },
                hidden: true,
                queryMode: 'local',
                forceSelection: true,
                bind: {                    
                    hidden: '{!theAttribute.password}',
                    value: '{theAttribute.showPassword}',
                    store: '{showPasswordStore}'
                }
            }]
        }, {
            layout: 'column',
            hidden: true,
            bind: {
                hidden: '{theAttribute.password}'
            },
            items: [{
                columnWidth: 0.5,
                xtype: 'numberfield',
                step: 1,
                minValue: 1,
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.maxlength,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.maxlength'
                },
                autoEl: {
                    'data-testid': 'attribute-maxLengthString_input'
                },
                name: 'maxLength',
                bind: {
                    value: '{theAttribute.maxLength}',
                    disabled: '{theAttribute.inherited}'
                },
                listeners: {
                    afterrender: function (input) {
                        var vm = input.lookupViewModel();
                        vm.bind({
                            bindTo: '{theAttribute.password}'
                        }, function (password) {
                            if (password) {
                                input.setMinValue(null);
                                vm.set('theAttribute.maxLength', null);
                            } else {
                                if (!vm.get('theAttribute.maxLength')) {
                                    var defaultValue = input.bind.value.stub.parent.boundValue.getField('maxLength').getDefaultValue();
                                    input.setMinValue(1);
                                    vm.set('theAttribute.maxLength', defaultValue);
                                }
                            }
                        });
                    }
                }
            }, {
                columnWidth: 0.5,
                xtype: 'combo',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.contentsecurity,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.contentsecurity'
                },
                valueField: 'value',
                displayField: 'label',
                name: 'textContentSecurity',
                autoEl: {
                    'data-testid': 'attribute-textContentSecurityString_input'
                },
                queryMode: 'local',
                forceSelection: true,
                bind: {
                    value: '{theAttribute.textContentSecurity}',
                    store: '{textContentSecurityStore}'
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
                xtype: 'displayfield',
                step: 1,
                minValue: 1,
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.editortype,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.editortype'
                },
                autoEl: {
                    'data-testid': 'attribute-password_display'
                },
                bind: {
                    value: '{theAttribute._password_description}'
                },
                listeners: {
                    afterrender: function () {
                        var vm = this.lookupViewModel();
                        vm.bind({
                            bindTo: '{theAttribute.password}'
                        }, function (password) {
                            var stringEditorTypesStore = vm.get('stringEditorTypesStore');
                            if (stringEditorTypesStore) {
                                var record = stringEditorTypesStore.findRecord('value', password + '');
                                if (record) {
                                    vm.set('theAttribute._password_description', record.get('label'));
                                }
                            }
                        });
                    }
                }
            }, {
                columnWidth: 0.5,
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showPassword,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showPassword'
                },
                autoEl: {
                    'data-testid': 'attribute-showPassword_display'
                },
                queryMode: 'local',
                hidden: true,
                bind: {
                    hidden: '{!theAttribute.password}',
                    value: '{theAttribute._showPassword_description}'
                },
                listeners: {
                    afterrender: function () {
                        var vm = this.lookupViewModel();
                        vm.bind({
                            bindTo: '{theAttribute.showPassword}'
                        }, function (showPassword) {
                            var showPasswordStore = vm.get('showPasswordStore');
                            if (showPasswordStore) {
                                var record = showPasswordStore.findRecord('value', showPassword);
                                if (record) {
                                    vm.set('theAttribute._showPassword_description', record.get('label'));
                                }
                            }
                        });
                    }
                }
            }]
        }, {
            layout: 'column',
            hidden: true,
            bind: {
                hidden: '{theAttribute.password}'
            },
            items: [{
                columnWidth: 0.5,
                xtype: 'displayfield',
                step: 1,
                minValue: 1,
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.maxlength,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.maxlength'
                },
                name: 'maxLength',
                autoEl: {
                    'data-testid': 'attribute-maxLengthString_display'
                },
                bind: {
                    value: '{theAttribute.maxLength}'
                }
            }, {
                // textContentSecurity
                columnWidth: 0.5,
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.contentsecurity,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.contentsecurity'
                },
                autoEl: {
                    'data-testid': 'attribute-textContentSecurityString_display'
                },
                bind: {
                    value: '{theAttribute.textContentSecurity}'
                },
                renderer: function (value) {
                    var vm = this.lookupViewModel();
                    var store = vm.get('textContentSecurityStore');
                    if (store) {
                        var record = store.findRecord('value', value);
                        if (record) {
                            return record.get('label');
                        }
                    }
                    return value;
                }
            }, {
                columnWidth: 0.5,
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showPassword,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showPassword'
                },
                autoEl: {
                    'data-testid': 'attribute-showPassword_display'
                },
                queryMode: 'local',
                hidden: true,
                bind: {
                    hidden: '{!theAttribute.password}',
                    value: '{theAttribute._showPassword_description}'
                },
                listeners: {
                    afterrender: function () {
                        var vm = this.lookupViewModel();
                        vm.bind({
                            bindTo: '{theAttribute.showPassword}'
                        }, function (showPassword) {
                            var showPasswordStore = vm.get('showPasswordStore');
                            if (showPasswordStore) {
                                var record = showPasswordStore.findRecord('value', showPassword);
                                if (record) {
                                    vm.set('theAttribute._showPassword_description', record.get('label'));
                                }
                            }
                        });
                    }
                }
            }]
        }]
    }]
});