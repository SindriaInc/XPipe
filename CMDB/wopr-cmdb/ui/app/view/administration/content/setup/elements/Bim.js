Ext.define('CMDBuildUI.view.administration.content.setup.elements.Bim', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.BimController',
        'CMDBuildUI.view.administration.content.setup.elements.BimModel'
    ],

    alias: 'widget.administration-content-setup-elements-bim',
    controller: 'administration-content-setup-elements-bim',
    viewModel: {
        type: 'administration-content-setup-elements-bim'
    },
    items: [{
        xtype: "fieldset",
        ui: 'administration-formpagination',
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.systemconfig.generals,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.systemconfig.generals'
        },
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    /********************* org.cmdbuild.bim.enabled **********************/
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                    },
                    name: 'isEnabled',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__bim__DOT__enabled}',
                        readOnly: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-bim-active_input'
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    /********************* org.cmdbuild.bim.viewer **********************/
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.viewer,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.viewer'
                    },
                    hidden: false,
                    bind: {
                        value: '{viewerDescription}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-bim-viewer_display'
                    }
                }, {
                    xtype: 'combobox',
                    name: 'viewers',
                    itemId: 'attributedomain',
                    clearFilterOnBlur: true,
                    queryMode: 'local',
                    displayField: 'label',
                    valueField: 'value',
                    forceSelection: true,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__bim__DOT__viewer}',
                        store: '{viewersStore}'
                    },
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.viewer,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.viewer'
                    },
                    hidden: true,
                    autoEl: {
                        'data-testid': 'administration-systemconfig-bim-viewer_input'
                    }
                }, {
                    /********************* org.cmdbuild.bim.conversiontimeout **********************/
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.xktconversiontimeout,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.xktconversiontimeout'
                    },
                    hidden: true,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__bim__DOT__conversiontimeout}',
                        hidden: '{!actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-bim-xktconversiontimeout_display'
                    }
                }, {
                    xtype: 'numberfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.xktconversiontimeout,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.xktconversiontimeout'
                    },
                    hideTrigger: true,
                    hidden: true,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__bim__DOT__conversiontimeout}',
                        hidden: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-bim-xktconversiontimeout_display'
                    }
                }]
            }]
        }]
    }]
});