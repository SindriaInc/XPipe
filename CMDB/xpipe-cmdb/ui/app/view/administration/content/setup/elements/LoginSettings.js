Ext.define('CMDBuildUI.view.administration.content.setup.elements.LoginSettings', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.LoginSettingsController'
    ],

    alias: 'widget.administration-content-setup-elements-loginsettings',
    controller: 'administration-content-setup-elements-loginsettings',
    viewModel: {
        data: {
            actions: {
                view: true,
                edit: false
            }
        },
        formulas: {
            action: {
                get: function () {
                    return this.get('actions.view') ? CMDBuildUI.util.administration.helper.FormHelper.formActions.view : CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                },
                set: function (value) {
                    this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                    this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                }
            }
        }
    },

    scrollable: 'y',
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    dockedItems: [{
        xtype: 'components-administration-toolbars-formtoolbar',
        region: 'north',
        borderBottom: 1,
        items: [{

            xtype: 'button',
            itemId: 'spacer',
            style: {
                "visibility": "hidden"
            }
        }, {
            xtype: 'tbfill'
        }, {
            xtype: 'tool',
            itemId: 'editBtn',
            iconCls: 'x-fa fa-pencil',
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.edit,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.edit'
            },
            cls: 'administration-tool',
            bind: {
                hidden: "{!actions.view}"

            },
            autoEl: {
                'data-testid': 'administration-setup-view-editBtn'
            }
        }]
    }, {
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(true)
    }],
    items: [{
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        padding: 0,
        title: CMDBuildUI.locales.Locales.administration.systemconfig.generals,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.systemconfig.generals'
        },
        defaults: {
            xtype: 'fieldcontainer',
            layout: 'column',
            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
            defaults: {
                columnWidth: 0.5
            }
        },
        items: [{
            /********************* org.cmdbuild.auth.helpText **********************/
            fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.loginpagetext,
            items: [CMDBuildUI.util.helper.FieldsHelper.getHTMLEditor({
                    columnWidth: 1,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__auth__DOT__helpText}',
                        hidden: '{actions.view}'
                    }
                }),
                {
                    // view
                    xtype: 'displayfield',
                    columnWidth: 1,
                    hidden: true,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__auth__DOT__helpText}',
                        hidden: '{!actions.view}'
                    }
                }
            ]
        }, {
            /********************* org.cmdbuild.auth.sso.redirect.enabled **********************/
            xtype: 'checkbox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.disableautologinsso,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.disableautologinsso'
            },
            name: 'isEnabled',
            bind: {
                value: '{theSetup.org__DOT__cmdbuild__DOT__auth__DOT__sso__DOT__redirect__DOT__enabled}',
                readOnly: '{actions.view}'
            },
            autoEl: {
                'data-testid': 'administration-systemconfig-login-disable-ssoautologin'
            }
        }]
    }]
});