Ext.define('CMDBuildUI.view.joinviews.configuration.ConfigurationController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.joinviews-configuration-configuration',
    control: {
        '#joinviewtabpanel': {
            beforerender: "onBeforeRender",
            tabchange: 'onTabChage'
        },
        '#addBtn': {
            click: 'onAddClickBtn'
        },

        '#joinviews-configuration-main': {
            saved: 'onSaved',
            cancel: 'onCancel',
            deletejoinview: 'onDelete'
        }
    },

    /**
     * @param {CMDBuildUI.view.joinviews.configuration.Configuration} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        if (!vm.get('actions.empty')) {
            vm.bind({
                bindTo: '{theView}'
            }, function (theView) {
                var tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;
                tabPanelHelper.addTab(
                    view,
                    "properties",
                    CMDBuildUI.locales.Locales.administration.classes.properties.title,
                    [{
                        xtype: 'joinviews-configuration-main',
                        autoScroll: true,
                        theView: vm.get('theView'),
                        viewModel: {
                            data: {
                                actions: vm.get('actions')
                            }
                        }
                    }], 0,
                    {
                        disabled: '{disabledTabs.properties}'
                    });

                tabPanelHelper.addTab(
                    view,
                    "fieldsmanagement",
                    CMDBuildUI.locales.Locales.administration.forms.form,
                    [{
                        xtype: 'administration-attributes-fieldsmanagement-panel',
                        itemId: 'form',
                        theView: vm.get('theView.formStructure'),
                        viewModel: {
                            data: {
                                _can_modify: vm.get('theSession.rolePrivileges.admin_views_modify'),
                                objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.view
                            }
                        }
                    }], 1,
                    {
                        disabled: '{disabledTabs.fieldsmanagement}'
                    });
                tabPanelHelper.addTab(
                    view,
                    "permissions",
                    CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.permissions,
                    [{
                        xtype: 'joinviews-configuration-permissions-permissions',
                        itemId: 'form',
                        theView: vm.get('theView'),
                        objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.view,
                        viewModel: {
                            data: {
                                objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.view,
                                objectTypeName: vm.get('theView.code')
                            }
                        }
                    }], 2,
                    {
                        disabled: '{disabledTabs.permissions}'
                    });
            });
        }
    },

    /**
     * @param {CMDBuildUI.view.joinviews.configuration.Configuration} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChage: function (view, newtab, oldtab, eOpts) {
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage('activeTabs.joinView', this, view, newtab, oldtab, eOpts);
    },

    /**
     * @event
     * @param {Ext.button.Button} button 
     */
    onAddClickBtn: function (button) {
        this.redirectTo('administration/joinviews_empty/true');
    },

    /**
     * @event
     * @param {String} action ADD|EDIT|VIEW|EMPTY
     * @param {CMDBuildUI.model.views.View} record 
     * @param {Object} operation 
     */
    onSaved: function (action, record, operation) {
        var me = this;
        var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getJoinViewUrl(record.get('_id'));
        if (action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add) {
            CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
                function () {
                    CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                });
        } else {
            CMDBuildUI.util.Stores.loadClassesStore().then(function () {
                CMDBuildUI.util.administration.MenuStoreBuilder.changeRecordBy('href', nextUrl, record.getTranslatedDescription(), me);
                CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false, true]);
            });
        }

    },
    /**
     * @event
     * @param {String} action ADD|EDIT|VIEW|EMPTY
     * @param {CMDBuildUI.model.views.View} record 
     */
    onCancel: function (action, record) {
        var me = this;
        var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getJoinViewUrl(record.phantom ? null : record.get('_id'));
        if (action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add) {
            CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
                function () {
                    CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                });
        } else {
            CMDBuildUI.util.Stores.loadClassesStore().then(function () {
                CMDBuildUI.util.administration.MenuStoreBuilder.changeRecordBy('href', nextUrl, record.getTranslatedDescription(), me);
                CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false, true]);
            });
        }
    },

    /**
     * @event
     * @param {Ext.button.Button} button 
     */
    onDelete: function (button) {
        button.setDisabled(true);
        CMDBuildUI.util.Utilities.showLoader(true);
        var me = this,
            view = me.getView(),
            vm = view.lookupViewModel(),
            theView = vm.get('theView');
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (action) {
                if (action === "yes") {
                    CMDBuildUI.util.helper.FormHelper.startSavingForm();
                    CMDBuildUI.util.Ajax.setActionId('delete-joinview');
                    theView.erase({
                        success: function (record, operation) {
                            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getJoinViewUrl();
                            CMDBuildUI.util.administration.MenuStoreBuilder.removeRecordBy('href', Ext.util.History.getToken(), nextUrl, me);
                            CMDBuildUI.util.Utilities.showLoader(false);
                        },
                        failure: function (record, operation) {
                            if (button && !button.destroyed) {
                                button.setDisabled(false);
                                CMDBuildUI.util.Utilities.showLoader(false);
                            }
                        },
                        callback: function (record, operation, success) {
                            CMDBuildUI.util.helper.FormHelper.endSavingForm();
                        }
                    });
                } else {
                    if (button && !button.destroyed) {
                        button.setDisabled(false);
                        CMDBuildUI.util.Utilities.showLoader(false);
                    }
                }
            });
    }
});