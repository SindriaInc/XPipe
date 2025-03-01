Ext.define('CMDBuildUI.view.administration.content.pluginmanager.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.pluginmanager.ViewController',
        'CMDBuildUI.view.administration.content.pluginmanager.ViewModel'
    ],

    alias: 'widget.administration-content-pluginmanager-view',
    controller: 'administration-content-pluginmanager-view',
    viewModel: {
        type: 'administration-content-pluginmanager-view'
    },

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    scrollable: true,

    items: [{
        xtype: 'container',
        layout: 'column',
        hidden: true,
        bind: {
            hidden: '{!actions.add}'
        },
        items: [{
            xtype: 'filefield',
            itemId: 'pluginFile',
            columnWidth: 0.5,
            padding: '10',
            fieldLabel: CMDBuildUI.locales.Locales.administration.plugin.addplugin,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.plugin.addplugin'
            }
        }],
        listeners: {
            show: function (container, eOpts) {
                const fileField = container.down("#pluginFile");
                fileField.allowBlank = false;
                fileField.validate();
            }
        }
    }, {
        margin: "0 10 10 10",
        ui: 'messagewarning',
        xtype: 'container',
        layout: {
            type: 'hbox',
            align: 'stretch'
        },
        items: [{
            flex: 1,
            ui: 'custom',
            xtype: 'panel',
            html: CMDBuildUI.locales.Locales.administration.plugin.availablepatches,
            localized: {
                html: 'CMDBuildUI.locales.Locales.administration.plugin.availablepatches'
            }
        }, {
            xtype: 'button',
            itemId: 'applyPatchesBtn',
            ui: 'administration-warning-action-small',
            text: CMDBuildUI.locales.Locales.administration.plugin.applypatches,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.plugin.applypatches'
            }
        }],
        hidden: true,
        bind: {
            hidden: '{!isPatchesAvailable}'
        }
    }, {
        xtype: 'fieldset',
        collapsible: true,
        layout: 'hbox',
        hidden: true,
        bind: {
            hidden: '{!thePlugin}'
        },
        ui: 'administration-formpagination',
        title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
        },
        items: [{
            flex: 1,
            items: [{
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.plugin.fieldlabels.name,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.plugin.fieldlabels.name'
                },
                bind: {
                    value: '{thePlugin.name}'
                }
            }, {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.plugin.fieldlabels.status,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.plugin.fieldlabels.status'
                },
                bind: {
                    value: '{thePlugin.status}'
                }
            }, {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.plugin.fieldlabels.version,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.plugin.fieldlabels.version'
                },
                bind: {
                    value: '{thePlugin.version}'
                }
            }]
        }, {
            flex: 1,
            items: [{
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.plugin.fieldlabels.description,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.plugin.fieldlabels.description'
                },
                bind: {
                    value: '{thePlugin.description}'
                }
            }, {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.plugin.fieldlabels.tag,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.plugin.fieldlabels.tag'
                },
                bind: {
                    value: '{thePlugin.tag}'
                }
            }, {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.plugin.fieldlabels.requiredcoreversion,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.plugin.fieldlabels.requiredcoreversion'
                },
                bind: {
                    value: '{thePlugin.requiredCoreVersion}'
                }
            }]
        }]
    }],

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        items: [{
            margin: "0 10 10 0",
            ui: 'messagewarning',
            xtype: 'container',
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            items: [{
                flex: 1,
                ui: 'custom',
                xtype: 'panel',
                html: CMDBuildUI.locales.Locales.administration.common.messages.reloadsystem,
                localized: {
                    html: 'CMDBuildUI.locales.Locales.administration.common.messages.reloadsystem'
                }
            }, {
                xtype: 'button',
                itemId: 'reloadButton',
                ui: 'administration-warning-action-small',
                text: CMDBuildUI.locales.Locales.administration.common.messages.reload,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.common.messages.reload'
                }
            }],
            hidden: true,
            bind: {
                hidden: '{!isNecessaryReload}'
            }
        }, {
            xtype: 'container',
            layout: 'hbox',
            items: [{
                xtype: 'button',
                ui: 'administration-action-small',
                itemId: 'addplugin',
                text: CMDBuildUI.locales.Locales.administration.plugin.addplugin,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.plugin.addplugin'
                }
            }, {
                xtype: 'tbfill'
            }, {
                xtype: 'tbtext',
                dock: 'right',
                hidden: true,
                bind: {
                    hidden: '{!thePlugin}',
                    html: '{pluginLabel}: <b>{thePlugin.name}</b>'
                }
            }]
        }, {
            xtype: 'components-administration-toolbars-formtoolbar',
            hidden: true,
            bind: {
                hidden: '{actions.edit || !thePlugin}'
            },
            items: [{
                xtype: 'tbfill'
            }, {
                xtype: 'tool',
                align: 'right',
                cls: 'administration-tool',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
                tooltip: CMDBuildUI.locales.Locales.administration.common.actions.edit,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.edit'
                },
                callback: function (owner, tool, event) {
                    owner.lookupViewModel().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                }
            }, {
                xtype: 'tool',
                align: 'right',
                itemId: 'patchesBtn',
                cls: 'administration-tool',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('clipboard-list', 'solid'),
                tooltip: CMDBuildUI.locales.Locales.administration.plugin.pluginpatches,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.administration.plugin.pluginpatches'
                }
            }]
        }]
    }, {
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons()
    }],

    initComponent: function () {
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        const vm = this.getViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.navigation.plugin);
        this.callParent(arguments);
    }

});