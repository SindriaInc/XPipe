Ext.define('CMDBuildUI.view.administration.content.importexport.datatemplates.card.CardMixin', {

    mixinId: 'administration-importexportmixin',

    onEditBtnClick: function () {
        var view = this.getView();
        var vm = view.getViewModel();
        if (vm.get("showInMainPanel")) {
            vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
            view.up('#tabpanel').getViewModel().toggleEnableTabs(0);
            CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-importexport-datatemplates-view', {
                viewModel: {
                    data: {
                        showInMainPanel: true,
                        templateId: vm.get("templateId"),
                        action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                        actions: {
                            view: false,
                            edit: true,
                            add: false
                        }
                    }
                }
            });
        } else {
            var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
            var viewModel = {
                data: {
                    theGateTemplate: view.getViewModel().get('selected') || view.getViewModel().get('theGateTemplate'),
                    grid: vm.get('grid') || this.getView().up().grid,
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                    actions: {
                        edit: true,
                        view: false,
                        add: false
                    }
                }
            };

            container.removeAll();
            container.add({
                xtype: 'administration-content-importexport-datatemplates-card',
                viewModel: viewModel
            });
        }
    },

    onDeleteBtnClick: function (button) {
        var me = this;
        var vm = button.lookupViewModel();
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === "yes") {
                    if (button.el.dom) {
                        button.setDisabled(true);
                    }
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
                    var theGateTemplate = me.getViewModel().get('theGateTemplate');
                    CMDBuildUI.util.Ajax.setActionId('delete-importexporttemplate');
                    var grid = CMDBuildUI.util.Navigation.getMainAdministrationContainer().down('administration-content-importexport-datatemplates-grid') || me.getView().up('grid') || me.getView().lookupViewModel().get('grid');
                    var recordHref = CMDBuildUI.util.administration.helper.ApiHelper.client.getImportExportDataTemplatesUrl(theGateTemplate.get('_id'));

                    if (!vm.get('showInMainPanel') || grid) {
                        if (grid.getStore().source) {
                            grid.getStore().remove(theGateTemplate);
                            grid.getStore().source.sync();
                        } else {
                            grid.fireEventArgs("removetemplate", [theGateTemplate, grid]);
                        }
                        CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                        CMDBuildUI.util.administration.MenuStoreBuilder.removeRecordBy('href', recordHref);
                        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);

                    } else {

                        theGateTemplate.erase({
                            failure: function (error) {
                                theGateTemplate.reject();
                                var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getImportExportDataTemplatesUrl(theGateTemplate.get('_id'));
                                me.redirectTo(nextUrl, true);
                            },
                            success: function (record, operation) {
                                var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getImportExportDataTemplatesUrl();
                                CMDBuildUI.util.Stores.loadImportExportTemplatesStore().then(function () {
                                    CMDBuildUI.util.administration.MenuStoreBuilder.removeRecordBy('href', Ext.util.History.getToken(), nextUrl, me);
                                });
                            },
                            callback: function (record, reason) {
                                if (button.el.dom) {
                                    button.setDisabled(false);
                                }
                                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                            }
                        });
                    }
                }
            }, this);
    },


    onViewBtnClick: function () {
        var view = this.getView();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var vm = view.getViewModel();
        var viewModel = {
            data: {
                showInMainPanel: false,
                theGateTemplate: view.getViewModel().get('selected') || view.getViewModel().get('theGateTemplate'),
                grid: vm.get('grid') || this.getView().up().grid,
                action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
                actions: {
                    edit: false,
                    view: true,
                    add: false
                }
            }
        };

        container.removeAll();
        container.add({
            xtype: 'administration-content-importexport-datatemplates-card',
            viewModel: viewModel
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onActiveToggleBtnClick: function (button, e, eOpts) {
        var me = this;
        var view = this.getView();
        var vm = view.getViewModel();
        var theGateTemplate = vm.get('theGateTemplate');
        theGateTemplate.set('active', !theGateTemplate.get('active'));

        me.setColumnsData();
        if (!theGateTemplate.get('columns').length) {
            CMDBuildUI.util.Notifier.showWarningMessage(
                CMDBuildUI.locales.Locales.administration.importexport.texts.emptyattributegridmessage
            );
            CMDBuildUI.util.Utilities.showLoader(false, button.up('panel'));
        } else if (theGateTemplate.isValid()) {
            if (theGateTemplate.get('type') === CMDBuildUI.model.importexports.Template.types['export']) {

                Ext.Array.forEach(theGateTemplate.get('columns'), function (item) {
                    item['default'] = '';
                });
            }

            var mergeMode = theGateTemplate.get('mergeMode_when_missing_update_value');
            if (mergeMode instanceof Array) {
                var lookupArraySeparator = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.lookuparray.separator);
                theGateTemplate.set('mergeMode_when_missing_update_value', mergeMode.join(lookupArraySeparator));
            }

            theGateTemplate.save({
                success: function (record, operation) {
                    var grid = vm.get('grid') ? vm.get('grid') : (view.up('administration-content-importexport-datatemplates-grid') || view.up('grid'));
                    CMDBuildUI.util.Stores.loadImportExportTemplatesStore().then(function () {
                        if (!vm.get('showInMainPanel') || grid) {
                            var regex = new RegExp(Ext.String.format(".*{0}.*", lookupArraySeparator));
                            try {
                                if (record && regex.test(record.get('mergeMode_when_missing_update_value'))) {
                                    record.set('mergeMode_when_missing_update_value', record.get('mergeMode_when_missing_update_value').split(lookupArraySeparator));
                                }
                            } catch (e) {

                            }
                            grid.getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [grid, record, me]);
                        }
                    });
                },
                failure: function () {
                    CMDBuildUI.util.Utilities.showLoader(false, button.up('panel'));
                }
            });
        }

    },

    onCloneBtnClick: function () {
        var view = this.getView();
        var vm = view.getViewModel();
        if (!vm.get("showInMainPanel")) {
            var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
            var newImportExportTemplate = vm.get('theGateTemplate').copyForClone();
            var viewModel = {
                data: {
                    theGateTemplate: newImportExportTemplate,
                    showInMainPanel: false,
                    grid: vm.get('grid') || view.up().grid,
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                    actions: {
                        edit: false,
                        view: false,
                        add: false,
                        clone: true
                    }
                }
            };

            container.removeAll();
            container.add({
                xtype: 'administration-content-importexport-datatemplates-card',
                viewModel: viewModel,
                listeners: {
                    savesuccess: function (record, operation, eOpts) {
                        var me = this;
                        var grid = vm.get('grid');
                        var theGate = vm.get('theGate');
                        if (theGate) {
                            var handler = theGate.handlers().first();
                            handler.addTemplate(record.get('code'));
                            theGate.save({
                                success: function (gate, gateOperation) {
                                    var eventToCall = (operation.getRequest().getMethod() === 'PUT') ? 'itemupdated' : 'itemcreated';
                                    handler.getTemplates().then(function (templatesStore) {
                                        vm.get('allGateTemplates').setData(templatesStore.getRange());
                                        view.up().grid.getPlugin('administration-forminrowwidget').view.fireEventArgs(eventToCall, [grid, record, me]);
                                    });
                                }
                            });
                        }
                    }
                }
            });
        } else {
            var me = this;
            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getCloneImportExportDataTemplatesUrl(vm.get('theGateTemplate._id'));
            me.redirectTo(nextUrl);
        }
    },

    /**
     * 
     * @param {*} row 
     * @param {*} record 
     * @param {*} element 
     * @param {*} rowIndex 
     * @param {*} e 
     * @param {*} eOpts 
     */
    onRowDblclick: function (row, record, element, rowIndex, e, eOpts) {
        var view = this.getView(),
            container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);

        var formInRow = view.ownerGrid.getPlugin('administration-forminrowwidget');
        formInRow.removeAllExpanded(record);
        view.setSelection(record);
        container.removeAll();
        container.add({
            xtype: 'administration-content-importexport-datatemplates-card',
            viewModel: {
                data: {
                    showInMainPanel: false,
                    theGateTemplate: record,
                    grid: view.ownerGrid,
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                    actions: {
                        view: false,
                        edit: true,
                        add: false
                    }
                }
            }
        });
    },


    onAllSelectedAttributesStoreDatachanged: function (store) {
        var vm = this.getViewModel();
        vm.set('dataFormatHidden', true);
        vm.set('dataFormatTimeHidden', true);
        vm.set('dataFormatDateHidden', true);
        vm.set('dataFormatDateTimeHidden', true);
        vm.set('dataFormatDecimalHidden', true);
        vm.bind({
            bindTo: {
                allAttrStore: '{theGateTemplate.columns}'
            },
            single: true
        }, function (data) {
            store.each(function (item) {
                var templateAttribute = data.allAttrStore.findRecord('attribute', item.get('name'), false, false, true, true);
                if (templateAttribute) {
                    templateAttribute.set('_attribute_description', item.get('description'));
                }
                vm.manageDataFormatFieldset(item);
            });
        });
    },
    privates: {
        setColumnsData: function () {
            var vm = this.getViewModel();
            var theGateTemplate = vm.get('theGateTemplate');
            var columns = [];

            Ext.Array.forEach(vm.get('allSelectedAttributesStore').getRange(), function (item) {
                columns.push(item.getData());
            });
            theGateTemplate.set('columns', columns);
        }
    }
});