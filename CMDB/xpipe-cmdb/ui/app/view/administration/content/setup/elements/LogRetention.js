Ext.define('CMDBuildUI.view.administration.content.setup.elements.LogRetention', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.LogRetentionController',
        'CMDBuildUI.view.administration.content.setup.elements.LogRetentionModel'
    ],

    alias: 'widget.administration-content-setup-elements-logretention',
    controller: 'administration-content-setup-elements-logretention',
    viewModel: {
        type: 'administration-content-setup-elements-logretention'
    },

    layout: 'column',
    scrollable: 'y',
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    statics: {
        modes: {
            custom: 'systemstatuslog,request,eventlog,jobrun,etlmessage'
        }
    },
    items: [{
        xtype: 'fieldcontainer',
        columnWidth: 0.5,
        padding: '0 0 0 15',
        items: [CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('mode', {
            mode: {
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.strings.mode,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.strings.mode'
                    }
                },
                displayfield: {
                    bind: {
                        value: '{_mode_description}'
                    },
                    listeners: {
                        beforerender: function () {
                            var me = this;
                            var vm = me.lookupViewModel();
                            vm.bind({
                                value: '{mode}'
                            }, function (data) {
                                vm.set('_mode_description', data.value.selection ? data.value.selection.get('label') : data.value);
                            });
                        }
                    }
                },
                combofield: {
                    displayField: 'label',
                    valueField: 'value',
                    bind: {
                        value: '{mode}',
                        store: '{modesStore}'
                    },
                    forceSelection: true,
                    listeners: {
                        change: function () {
                            var me = this;
                            Ext.asap(function () {
                                me.up('form').isValid();
                            });
                        }
                    }
                }
            }
        })]
    }, {
        xtype: 'container',
        itemId: 'fieldsetscontainer',
        hidden: true,
        bind: {
            hidden: '{isDefault}'
        },
        layout: 'column',
        columnWidth: 1,
        items: []
    }],
    dockedItems: [{
        dock: 'top',
        xtype: 'components-administration-toolbars-formtoolbar',
        items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
            edit: {
                disabled: true,
                bind: {
                    disabled: '{!theSession.rolePrivileges.admin_sysconfig_modify}'
                }
            }
        }, 'logretention', 'theSetup',
            [],
            [])
    }, {
        xtype: 'toolbar',
        itemId: 'bottomtoolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(true, {
            testid: 'logretention'
        }, {
            testid: 'logretention'
        })
    }]
});