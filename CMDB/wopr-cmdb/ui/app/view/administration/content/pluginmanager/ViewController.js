Ext.define('CMDBuildUI.view.administration.content.pluginmanager.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-pluginmanager-view',

    control: {
        '#': {
            afterlayout: 'onAfterLayout',
            beforerender: 'onBeforeRender'
        },
        '#addplugin': {
            click: 'onAddPluginClick'
        },
        '#applyPatchesBtn': {
            click: 'onApplyPatchesBtnClick'
        },
        '#patchesBtn': {
            click: 'onPatchesBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#reloadButton': {
            click: 'onReloadBtnClick'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.content.pluginmanager.View} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = this.getViewModel();
        const thePlugin = vm.get('thePlugin');
        const configs = thePlugin ? thePlugin.get('configs') : {};
        const custompage = thePlugin ? thePlugin.get('custompage') : null;

        vm.set('isPatchesAvailable', thePlugin ? thePlugin.get('_hasPatches') : false);

        if (!Ext.Object.isEmpty(configs) && !Ext.isEmpty(configs._model.attributes)) {
            const leftColumns = [];
            const rightColumns = [];

            Ext.Array.forEach(configs._model.attributes, function (item, index, allitems) {
                const column = {
                    xtype: 'displayfield',
                    fieldLabel: item._description_translation,
                    hidden: true,
                    bind: {
                        hidden: '{actions.edit}',
                        value: '{pluginConfigs.' + item._id.replace(/\./g, '__DOT__') + '}'
                    }
                };

                const fieldColumn = Ext.clone(column);
                fieldColumn.bind.hidden = '{actions.view}';
                switch (item.type) {
                    case 'integer':
                        fieldColumn.allowDecimals = false;
                    case 'float':
                        fieldColumn.xtype = 'numberfield';
                        break;
                    case 'select':
                        fieldColumn.xtype = 'combo';
                        const comboStore = [];
                        Ext.Array.forEach(item.options, function (combo, ind, allcombos) {
                            comboStore.push({
                                "text": combo
                            });
                        })
                        fieldColumn.store = Ext.create('Ext.data.Store', {
                            fields: ['text'],
                            data: comboStore
                        });
                        fieldColumn.valueField = 'text';
                        break;
                    case 'password':
                        fieldColumn.xtype = 'passwordfield';
                        break;
                    case 'text':
                        fieldColumn.xtype = 'textareafield';
                        break;
                    case 'string':
                    default:
                        fieldColumn.xtype = 'textfield';
                        break;
                }

                if (index % 2 == 0) {
                    leftColumns.push(column);
                    leftColumns.push(fieldColumn);
                } else {
                    rightColumns.push(column);
                    rightColumns.push(fieldColumn);
                }
            });

            view.add({
                xtype: 'fieldset',
                collapsible: true,
                layout: 'hbox',
                ui: 'administration-formpagination',
                title: CMDBuildUI.locales.Locales.administration.plugin.pluginconfig,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.plugin.pluginconfig'
                },
                items: [{
                    flex: 1,
                    padding: '0 20 0 0',
                    items: leftColumns
                }, {
                    flex: 1,
                    padding: '0 20 0 0',
                    items: rightColumns
                }]
            });
        }

        if (custompage) {
            view.add({
                xtype: 'fieldset',
                collapsible: true,
                ui: 'administration-formpagination',
                title: CMDBuildUI.locales.Locales.administration.plugin.pluginconfig,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.plugin.pluginconfig'
                },
                items: [{
                    xtype: custompage.alias.replace("widget.", "")
                }]
            });
        }
    },

    /**
     *
     * @param {Ext.form.Panel} panel
     * @param {Ext.layout.form.Panel} layout
     * @param {Object} eOpts
     */
    onAfterLayout: function (panel, layout, eOpts) {
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onAddPluginClick: function (button, event, eOpts) {
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', 'administration/pluginmanager_empty/true', this);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, event, eOpts) {
        const vm = this.getViewModel();
        if (vm.get("action") == CMDBuildUI.util.administration.helper.FormHelper.formActions.add) {
            const nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getPluginManagerUrl();
            CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, this);
        } else {
            vm.set('recalculateConfigs', true);
        }
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, event, eOpts) {
        const vm = this.getViewModel();
        if (vm.get("action") == CMDBuildUI.util.administration.helper.FormHelper.formActions.add) {
            const formData = new FormData();
            const url = Ext.String.format('{0}/system/plugins', CMDBuildUI.util.Config.baseUrl);
            const input = this.getView().down("#pluginFile").extractFileInput();
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
            CMDBuildUI.util.administration.File.upload('POST', formData, input, url, {
                success: function () {
                    CMDBuildUI.util.Utilities.checkBootStatus().then(function () {
                        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                    });
                },
                callback: function () {
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                }
            });
        } else {
            const configData = {};
            Ext.Object.each(vm.get("pluginConfigs"), function (key, value, myself) {
                configData[key.replaceAll('__DOT__', ".")] = value;
            });

            Ext.Ajax.request({
                method: 'PUT',
                url: CMDBuildUI.util.Config.baseUrl + '/plugin/config/_MANY',
                jsonData: configData,
                success: function (response, eOpts) {
                    const store = Ext.getStore('pluginmanager.Plugins');
                    const record = store.getById(vm.get('thePlugin').getId());
                    Ext.Object.each(configData, function (key, value, myself) {
                        record.get("configs")[key] = value;
                    });
                    vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                }
            });
        }
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onApplyPatchesBtnClick: function (button, event, eOpts) {
        this.onOpenPatchesPopup(true);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onPatchesBtnClick: function (button, event, eOpts) {
        this.onOpenPatchesPopup();
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onReloadBtnClick: function (button, event, eOpts) {
        const view = this.getView();
        const reloadApp = function () {
            Ext.Ajax.request({
                url: CMDBuildUI.util.Config.baseUrl + CMDBuildUI.util.api.Common.getBootStatusUrl(),
                method: 'GET',
                callback: function (records, operation, success) {
                    if (success.status === 200 && success.responseText) {
                        const jsonresponse = Ext.JSON.decode(success.responseText);
                        const status = jsonresponse.status;
                        if (status === 'READY') {
                            window.location.reload();
                        }
                    }

                    setTimeout(function () {
                        reloadApp();
                    }, 2000);
                }
            });
        }

        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.notifier.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousurerebootapplication,
            function (action) {
                if (action === "yes") {
                    CMDBuildUI.util.Navigation.getMainContainer().getViewModel().set("disableAlertBtn", true);
                    CMDBuildUI.util.Utilities.addLoadMask(view);
                    Ext.Ajax.request({
                        method: 'POST',
                        url: CMDBuildUI.util.Config.baseUrl + "/system/restart",
                        callback: function () {
                            reloadApp();
                        }
                    });
                }
            }
        );
    },

    privates: {
        /**
         *
         * @param {Boolean} showOnlyApplyPatches
         */
        onOpenPatchesPopup: function (showOnlyApplyPatches) {
            const me = this;
            const vm = this.getViewModel();
            const content = {
                xtype: 'panel',
                items: [{
                    xtype: 'container',
                    layout: 'hbox',
                    items: [{
                        xtype: 'tbfill'
                    }, {
                        xtype: 'checkbox',
                        itemId: 'showOnlyApplyPatchesCheckbox',
                        padding: '10 20',
                        boxLabel: CMDBuildUI.locales.Locales.administration.plugin.showonlyapplypatches,
                        localized: {
                            boxLabel: 'CMDBuildUI.locales.Locales.administration.plugin.showonlyapplypatches'
                        },
                        checked: showOnlyApplyPatches,
                        listeners: {
                            change: function (field, newValue, oldValue, eOpts) {
                                const store = field.lookupViewModel().get("patchesStore");
                                if (newValue) {
                                    store.filterBy(function (record) {
                                        return !record.get("date");
                                    });
                                } else {
                                    store.clearFilter();
                                }
                            }
                        }
                    }]
                }, {
                    xtype: 'grid',
                    ui: 'cmdbuildgrouping',
                    scrollable: true,
                    forceFit: true,
                    features: [{
                        ftype: 'grouping',
                        collapsible: true,
                        enableGroupingMenu: false,
                        groupHeaderTpl: [
                            '{name} ({children:this.childrenNumber})', {
                                childrenNumber: function (children) {
                                    return children.length;
                                }
                            }
                        ]
                    }],
                    bind: {
                        store: '{patchesStore}'
                    },
                    columns: [{
                        text: CMDBuildUI.locales.Locales.administration.plugin.fieldlabels.name,
                        localized: {
                            text: 'CMDBuildUI.locales.Locales.administration.plugin.fieldlabels.name'
                        },
                        dataIndex: '_id'
                    }, {
                        text: CMDBuildUI.locales.Locales.administration.plugin.appliedon,
                        localized: {
                            text: 'CMDBuildUI.locales.Locales.administration.plugin.appliedon'
                        },
                        dataIndex: 'date',
                        renderer: function (value) {
                            return CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(value);
                        }
                    }]
                }],
                dockedItems: {
                    xtype: 'toolbar',
                    itemId: 'bottomToolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    items: [{
                        xtype: 'tbfill'
                    }, {
                        xtype: 'button',
                        itemId: 'cancelBtn',
                        text: CMDBuildUI.locales.Locales.administration.common.actions.cancel,
                        localized: {
                            text: 'CMDBuildUI.locales.Locales.administration.common.actions.cancel'
                        },
                        ui: 'administration-secondary-action-small',
                        listeners: {
                            click: function (button, event, eOpts) {
                                popup.close();
                            }
                        }
                    }, {
                        xtype: 'button',
                        itemId: 'applyPatches',
                        text: CMDBuildUI.locales.Locales.administration.plugin.applypatches,
                        localized: {
                            text: 'CMDBuildUI.locales.Locales.administration.plugin.applypatches'
                        },
                        ui: 'administration-action-small',
                        bind: {
                            disabled: '{!isPatchesAvailable}'
                        },
                        listeners: {
                            click: function (button, event, eOpts) {
                                const pluginName = vm.get('thePlugin').get('name');
                                const cancelBtn = button.up("#bottomToolbar").down("#cancelBtn");
                                button.showSpinner = true;
                                CMDBuildUI.util.Utilities.disableFormButtons([button, cancelBtn]);

                                Ext.Ajax.request({
                                    method: 'POST',
                                    url: Ext.String.format('{0}/system/plugins/{1}/patch', CMDBuildUI.util.Config.baseUrl, pluginName),
                                    success: function () {
                                        Ext.getStore('pluginmanager.Plugins').load({
                                            callback: function (records, operation, success) {
                                                CMDBuildUI.util.Utilities.enableFormButtons([button, cancelBtn]);
                                                if (success) {
                                                    const plugin = Ext.Array.findBy(records, function (item, index) {
                                                        return item.get("name") == pluginName;
                                                    });
                                                    const popupViewModel = button.lookupViewModel();
                                                    const store = popupViewModel.get('patchesStore');

                                                    CMDBuildUI.util.Notifier.showInfoMessage(CMDBuildUI.locales.Locales.administration.plugin.patchesapplied);
                                                    button.up('#plugin-manager-patches').down('#showOnlyApplyPatchesCheckbox').setValue(false);
                                                    popupViewModel.set('isPatchesAvailable', plugin.get('_hasPatches'));
                                                    store.removeAll();
                                                    store.add(plugin.get("patches"));

                                                    const nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getPluginManagerUrl(pluginName);
                                                    CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                                                }
                                            }
                                        })
                                    }
                                });
                            }
                        }
                    }]
                }
            }

            const patchesStore = {
                autoDestroy: true,
                proxy: {
                    type: 'memory'
                },
                data: vm.get('thePlugin').get("patches"),
                sorters: {
                    property: 'date',
                    direction: 'DESC'
                },
                grouper: {
                    groupFn: function (item) {
                        return item.get("date") ? CMDBuildUI.locales.Locales.administration.plugin.applied : CMDBuildUI.locales.Locales.administration.plugin.tobeapplied;
                    }
                }
            }

            if (showOnlyApplyPatches) {
                patchesStore.filters = function (record) {
                    return !record.get("date");
                }
            }

            const popup = CMDBuildUI.util.Utilities.openPopup(
                "plugin-manager-patches",
                CMDBuildUI.locales.Locales.administration.plugin.pluginpatcheslist,
                content,
                null,
                {
                    ui: 'administration',
                    width: '60%',
                    height: '60%',
                    viewModel: {
                        data: {
                            isPatchesAvailable: vm.get('isPatchesAvailable')
                        },
                        stores: {
                            patchesStore: patchesStore
                        }
                    }
                }
            );
        }
    }
});