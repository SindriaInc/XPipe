Ext.define('CMDBuildUI.view.administration.content.setup.elements.LoginSettings', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.LoginSettingsController',
        'CMDBuildUI.view.administration.content.setup.elements.LoginSettingsModel'
    ],

    alias: 'widget.administration-content-setup-elements-loginsettings',
    controller: 'administration-content-setup-elements-loginsettings',
    viewModel: {
        type: 'administration-content-setup-elements-loginsettings'
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
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
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
            name: 'isDisabled',
            bind: {
                value: '{ssoautoLogin}',
                readOnly: '{actions.view}'
            },
            autoEl: {
                'data-testid': 'administration-systemconfig-login-disable-ssoautologin'
            }
        }]
    }]
});