Ext.define('CMDBuildUI.view.administration.content.domains.tabitems.properties.PropertiesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-domains-tabitems-properties-properties',


    require: [
        'CMDBuildUI.util.administration.helper.FormHelper'
    ],
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#openBtn': {
            click: 'onOpenBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#enableBtn': {
            click: 'onToggleActiveBtnClick'
        },
        '#disableBtn': {
            click: 'onToggleActiveBtnClick'
        },
        '#linkToContextBtn': {
            click: 'onLinkToContextBtn'
        }
    },

    onBeforeRender: function (view) {
        var vm = view.getViewModel();
        view.addDocked({
            xtype: 'components-administration-toolbars-formtoolbar',
            dock: 'top',
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                edit: true,
                'delete': true,
                view: (view.config._rowContext && view.config._rowContext.record) ? true : false,
                activeToggle: true,
                linkToContextTool: ((view.config._rowContext && view.config._rowContext.record) || view.up('administration-detailswindow'))
            }, 'domains', 'theDomain'),
            bind: {
                hidden: '{!actions.view}'
            }
        }, 0);
        if (view.config._rowContext && view.config._rowContext.record) {

            var viewInRow = view.down('#domain-generaldatafieldset');
            viewInRow.setTitle(null);
            viewInRow.setStyle('pading-top: 0;border-width: 0 !important;margin-bottom: 10px!important;');
            vm.linkTo('theDomain', {
                type: 'CMDBuildUI.model.domains.Domain',
                id: view.config._rowContext.record.get('_id')
            });
        } else {
            if (!vm.get('actions.add')) {
                vm.linkTo('theDomain', {
                    type: 'CMDBuildUI.model.domains.Domain',
                    id: vm.get('objectTypeName')
                });
            } else {
                vm.linkTo('theDomain', {
                    type: 'CMDBuildUI.model.domains.Domain',
                    create: true
                });
            }
        }
    },

    onEditBtnClick: function (button) {
        var view = this.getView();
        var vm = view.getViewModel();
        try {
            if (
                view && view.container && view.container.component &&
                view.container.component.getXType() !== CMDBuildUI.view.administration.DetailsWindow.xtype
            ) {
                vm.getParent().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
            } else {
                var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
                var theDomain = vm.get('theDomain');
                var grid = vm.get('grid') || view.config._rowContext.ownerGrid;

                container.removeAll();
                container.add({
                    xtype: 'administration-content-domains-tabitems-properties-properties',
                    viewModel: {
                        data: {
                            theDomain: theDomain,
                            title: Ext.String.format('{0} - {1}',
                                CMDBuildUI.locales.Locales.administration.localizations.domain,
                                theDomain.get('name')),
                            grid: grid,
                            actions: {
                                view: false,
                                edit: true,
                                add: false
                            },
                            action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                            objectTypeName: theDomain.get('name')
                        }
                    }
                });
            }
        } catch (error) {
            CMDBuildUI.util.Logger.log("unable to enter in edit mode", CMDBuildUI.util.Logger.levels.debug);
        }

    },

    onOpenBtnClick: function (button) {
        if (!this.getView()._rowContext) {
            this.getViewModel().getParent().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        } else {
            var view = this.getView();
            var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
            container.removeAll();
            var theDomain = view.getViewModel().get('theDomain');
            container.add({
                xtype: 'administration-content-domains-tabitems-properties-properties',
                viewModel: {
                    data: {
                        theDomain: theDomain,
                        title: Ext.String.format('{0} - {1}',
                            CMDBuildUI.locales.Locales.administration.localizations.domain,
                            theDomain.get('name')),
                        grid: view.config._rowContext.ownerGrid,
                        actions: {
                            view: true,
                            edit: false,
                            add: false
                        },
                        action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                        objectTypeName: theDomain.get('name'),
                        toolAction: {
                            _canAdd: false,
                            _canUpdate: false,
                            _canDelete: false,
                            _canActiveToggle: false
                        }
                    },
                    formulas: {
                        toolsManager: {
                            bind: {
                                canModify: '{theSession.rolePrivileges.admin_domains_modify}'
                            },
                            get: function (data) {
                                this.set('toolAction._canAdd', data.canModify === true);
                                this.set('toolAction._canUpdate', data.canModify === true);
                                this.set('toolAction._canDelete', data.canModify === true);
                                this.set('toolAction._canActiveToggle', data.canModify === true);
                            }
                        }
                    }
                }
            });
        }

    },

    onDeleteBtnClick: function (button) {
        var me = this;
        var view = this.getView();
        var isFormInRow = view._rowContext && view._rowContext.record;
        var vm = me.getViewModel();
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (action) {
                if (action === "yes") {
                    button.setDisabled(true);
                    CMDBuildUI.util.Utilities.showLoader(true);
                    var theDomain = vm.get('theDomain');
                    CMDBuildUI.util.Ajax.setActionId('delete-domain');

                    theDomain.erase({
                        success: function (record, operation) {
                            var itemToRemove = CMDBuildUI.util.administration.helper.ApiHelper.client.getDomainUrl(record.getId());
                            if (!isFormInRow) {
                                var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getDomainUrl();
                                CMDBuildUI.util.administration.MenuStoreBuilder.removeRecordBy('href', itemToRemove, nextUrl, me);
                            } else {
                                view._rowContext.ownerGrid.getStore().load();
                                CMDBuildUI.util.administration.MenuStoreBuilder.removeRecordBy('href', itemToRemove);
                            }
                        },
                        callback: function (record, reason) {
                            if (button.el.dom) {
                                button.setDisabled(false);
                            }
                            CMDBuildUI.util.Utilities.showLoader(false);
                        }
                    });
                }
            });
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onToggleActiveBtnClick: function (button, e, eOpts) {
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        var me = this;
        var view = me.getView();
        var vm = view.getViewModel();
        var theDomain = vm.get('theDomain');
        theDomain.set('active', !theDomain.get('active'));
        Ext.apply(theDomain.data, theDomain.getAssociatedData());
        theDomain.save({
            success: function (record, operation) {
                var grid = view.up('grid');
                if (grid) {
                    var plugin = grid.getPlugin('administration-forminrowwidget');
                    if (plugin) {
                        plugin.view.fireEventArgs('itemupdated', [grid, record, me]);
                    }
                }
                CMDBuildUI.util.administration.Utilities.showToggleActiveMessage(record);
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
            },
            failure: function (record, reason) {
                record.reject();
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onLinkToContextBtn: function (button, e, eOpts) {
        var vm = button.lookupViewModel();
        var theDomain = vm.get('theDomain');
        var url = CMDBuildUI.util.administration.helper.ApiHelper.client.getDomainUrl(theDomain.get('name'));
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', url, this);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var me = this;
        var vm = this.getViewModel();
        var sourceClassView, klass, objectType;
        CMDBuildUI.util.Utilities.showLoader(true);
        button.setDisabled(true);

        if (!vm.get('theDomain').isValid()) {
            var validatorResult = vm.get('theDomain').validate();
            var errors = validatorResult.items;
            for (var i = 0; i < errors.length; i++) {
                // console.log('Key :' + errors[i].field + ' , Message :' + errors[i].msg);
            }
        } else {
            var theDomain = vm.get('theDomain');
            delete theDomain.data.system;

            if (vm.get('grid')) {
                sourceClassView = vm.get('grid').getViewModel().get('objectTypeName');
                if (sourceClassView) {
                    klass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(sourceClassView);
                    objectType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(sourceClassView);
                    var hierarchy = klass.getHierarchy();
                    if (hierarchy.indexOf(theDomain.get('source')) < 0 && hierarchy.indexOf(theDomain.get('destination')) < 0) {
                        CMDBuildUI.util.Notifier.showErrorMessage(
                            Ext.String.format(
                                CMDBuildUI.locales.Locales.administration.domains.strings.classshoulbeoriginordestination,
                                klass.get('description'))
                        );
                        CMDBuildUI.util.Utilities.showLoader(false);
                        button.setDisabled(false);
                        return false;
                    }
                }
            }
            // save the domain
            theDomain.save({
                success: function (record, operation) {
                    var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getDomainUrl(record.get('_id'));
                    var cardView = me.getView().up('administration-detailswindow');
                    me.saveLocales(Ext.copy(vm), record);
                    if (cardView) {
                        vm.get('grid').getStore().load(function () {
                            var plugin = vm.get('grid').getPlugin('administration-forminrowwidget');
                            plugin.view.fireEventArgs('itemupdated', [vm.get('grid'), record, me]);

                            CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
                                function () {
                                    switch (objectType) {
                                        case CMDBuildUI.model.administration.MenuItem.types.klass:
                                            nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getClassUrl(klass.get('_id'));
                                            break;
                                        case CMDBuildUI.model.administration.MenuItem.types.process:
                                        case 'process':
                                            nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getProcessUrl(klass.get('_id'));
                                            break;
                                        default:
                                            break;
                                    }
                                    var treestore = Ext.getCmp('administrationNavigationTree');
                                    var selected = treestore.getStore().findNode("href", nextUrl);
                                    treestore.setSelection(selected);
                                });
                            if (button.el.dom) {
                                button.setDisabled(false);
                            }
                            CMDBuildUI.util.Utilities.showLoader(false);
                            CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                        });

                    } else {
                        if (vm.get('actions.edit')) {
                            var treestore = Ext.getCmp('administrationNavigationTree').getStore();
                            var selected = treestore.findNode("href", nextUrl);
                            selected.set('text', record.get('description'));

                            if (vm.get('grid')) {
                                vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                                vm.set('actions.view', true);
                                vm.set('actions.add', false);
                                vm.set('actions.edit', false);
                                var store = vm.get('grid').getStore();
                                store.load({
                                    callback: function () {
                                        var gridView = vm.get('grid').getView();
                                        gridView.refresh();

                                        var index = vm.get('grid').getStore().findExact("_id", record.getId());
                                        var storeItem = vm.get('grid').getStore().getAt(index);

                                        vm.get('grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [vm.get('grid'), storeItem, index]);
                                        vm.get('grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [vm.get('grid'), storeItem, index]);
                                        if (button.el.dom) {
                                            button.setDisabled(false);
                                        }
                                        CMDBuildUI.util.Utilities.showLoader(false);
                                        CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                                    }
                                });
                            } else {
                                if (button.el.dom) {
                                    button.setDisabled(false);
                                }
                                CMDBuildUI.util.Utilities.showLoader(false);
                                me.redirectTo(nextUrl, true);
                            }
                        } else {
                            CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
                                function () {
                                    var treeComponent = Ext.getCmp('administrationNavigationTree');
                                    var treeComponentStore = treeComponent.getStore();
                                    var selected = treeComponentStore.findNode("href", nextUrl);

                                    treeComponent.setSelection(selected);
                                });
                            if (button.el.dom) {
                                button.setDisabled(false);
                            }
                            CMDBuildUI.util.Utilities.showLoader(false);
                            me.getViewModel().getParent().set('actionManager', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                            me.redirectTo(nextUrl, true);
                        }
                    }
                },
                failure: function (record, reason) {
                    if (button.el.dom) {
                        button.setDisabled(false);
                    }
                    CMDBuildUI.util.Utilities.showLoader(false);
                }
            });
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var detailsWindow = button.up('#CMDBuildAdministrationDetailsWindow');
        var vm;
        if (detailsWindow) {
            vm = button.lookupViewModel();
            vm.get('theDomain').reject();
            vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
            vm.set('actions.view', true);
            vm.set('actions.edit', false);
            vm.set('actions.add', false);
            detailsWindow.fireEvent("closed");
        } else {
            if (this.getViewModel().get('actions.edit')) {
                this.redirectTo(Ext.String.format('administration/domains/{0}', this.getViewModel().get('theDomain._id')), true);
            } else if (this.getViewModel().get('actions.add')) {
                var store = Ext.getStore('administration.MenuAdministration');
                vm = Ext.getCmp('administrationNavigationTree').getViewModel();
                var currentNode = store.findNode("objecttype", CMDBuildUI.model.administration.MenuItem.types.domain);
                vm.set('selected', currentNode);
                this.redirectTo('administration/domains_empty', true);
            }
        }
    },

    privates: {
        saveLocales: function (vm, record) {
            var translations = [
                'theDomainDescriptionTranslation',
                'theDirectDescriptionTranslation',
                'theInverseDescriptionTranslation',
                'theMasterDetailTranslation'
            ];
            var keyFunction = [
                'getLocaleKeyOfDomainDescription',
                'getLocaleKeyOfDomainDirectDescription',
                'getLocaleKeyOfDomainInverseDescription',
                'getLocaleKeyOfDomainMasterDetail'
            ];
            Ext.Array.forEach(translations, function (item, index) {
                if (vm.get(item)) {
                    var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper[keyFunction[index]](record.get('name'));
                    vm.get(item).crudState = 'U';
                    vm.get(item).crudStateWas = 'U';
                    vm.get(item).phantom = false;
                    vm.get(item).set('_id', translationCode);
                    vm.get(item).save({
                        success: function (translations, operation) {
                            CMDBuildUI.util.Logger.log(item + " localization was saved", CMDBuildUI.util.Logger.levels.debug);
                        }
                    });
                }
            });
        }
    }
});