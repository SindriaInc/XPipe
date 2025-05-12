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
                    /********************* org.cmdbuild.gis.initialLatitude **********************/
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.viewer,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.viewer'
                    },
                    hidden: true,
                    bind: {
                        value: '{viewerDescription}',
                        hidden: '{!actions.view}'
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
                        store: '{viewersStore}',
                        hidden: '{actions.view}'
                    },
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.viewer,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.viewer'
                    },
                    hidden: true,
                    autoEl: {
                        'data-testid': 'administration-systemconfig-bim-viewer_input'
                    }
                }]
            }]
        }]
    }, {
        hidden: true,
        bind: {
            hidden: Ext.String.format('{defaultViewer !== "{0}"}', 'xeokit')
        },
        layout: 'column',
        xtype: 'fieldset',
        ui: 'administration-formpagination',
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.systemconfig.xeokitconfigs,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.systemconfig.xeokitconfigs'
        },
        items: [{
           
            layout: 'column',
            columnWidth: 0.5,
            items: [{
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
    }, {
        xtype: 'fieldset',
        ui: 'administration-formpagination',
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.systemconfig.bimserverconfigs,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.systemconfig.bimserverconfigs'
        },
        layout: 'column',
        items: [{
            columnWidth: 1,
            items: [{
                /********************* org.cmdbuild.bim.enabled **********************/
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                },
                name: 'isEnabled',
                disabled: true,
                bind: {
                    disabled: Ext.String.format('{defaultViewer === "{0}"}', 'xeokit'),
                    value: '{theSetup.org__DOT__cmdbuild__DOT__bim__DOT__bimserver__DOT__enabled}',
                    readOnly: '{actions.view}'
                },
                autoEl: {
                    'data-testid': 'administration-systemconfig-bim-bimserverenabled_input'
                }
            }]
        }, {
            columnWidth: 1,
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    /********************* org.cmdbuild.gis.initialLatitude **********************/
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.url,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.url'
                    },
                    hidden: true,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__bim__DOT__bimserver__DOT__url}',
                        hidden: '{!actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-bim-bimerverurl_display'
                    }
                }, {
                    xtype: 'textfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.url,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.url'
                    },
                    hidden: true,
                    placeholder: 'http:// or https://',
                    disabled: true,
                    bind: {
                        disabled: Ext.String.format('{defaultViewer === "{0}"}', 'xeokit'),
                        value: '{theSetup.org__DOT__cmdbuild__DOT__bim__DOT__bimserver__DOT__url}',
                        hidden: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-bim-bimerverurl_input'
                    }
                }]
            }]
        }, {
            layout: 'column',
            columnWidth: 1,
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'displayfield',
                    name: 'username',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.username,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.username'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__bim__DOT__bimserver__DOT__username}',
                        hidden: '{!actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-bim-bimerverusename_display'
                    }
                }, {
                    xtype: 'textfield',
                    name: 'username',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.username,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.username'
                    },
                    listeners: {
                        afterrender: function (cmp) {
                            cmp.inputEl.set({
                                autocomplete: 'new-username'
                            });
                        }
                    },
                    disabled: true,
                    bind: {
                        disabled: Ext.String.format('{defaultViewer === "{0}"}', 'xeokit'),
                        value: '{theSetup.org__DOT__cmdbuild__DOT__bim__DOT__bimserver__DOT__username}',
                        hidden: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-bim-bimerverusename_input'
                    }
                }]
            }, {
                style: {
                    paddingLeft: '15px'
                },
                layout: 'column',
                columnWidth: 0.5,
                items: [{
                    xtype: 'displayfield',
                    name: 'password',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.password,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.password'
                    },
                    bind: {
                        hidden: '{!actions.view}',
                        value: '{hiddenPassword}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-bim-password_display'
                    }
                }, {
                    xtype: 'passwordfield',
                    name: 'password',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.password,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.password'
                    },
                    disabled: true,
                    bind: {
                        disabled: Ext.String.format('{defaultViewer === "{0}"}', 'xeokit'),
                        value: '{theSetup.org__DOT__cmdbuild__DOT__bim__DOT__bimserver__DOT__password}',
                        hidden: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-bim-password_input'
                    }
                }]
            }]
        }]
    }]
});