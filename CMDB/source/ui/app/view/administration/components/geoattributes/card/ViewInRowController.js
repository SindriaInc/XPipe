Ext.define('CMDBuildUI.view.administration.components.geoattributes.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-components-geoattributes-card-viewinrow',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#openBtn': {
            click: 'onOpenBtnClick'
        },
        '#cloneBtn': {
            click: 'onCloneBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#enableBtn': {
            click: 'onToggleActiveBtnClick'
        },
        '#disableBtn': {
            click: 'onToggleActiveBtnClick'
        }

    },

    onBeforeRender: function (view) {
        var vm = view.getViewModel();

        if (view && view._rowContext) {
            CMDBuildUI.model.map.GeoAttribute.setProxy({
                url: view._rowContext.ownerGrid.getViewModel().get('storedata.url'),
                type: 'baseproxy'
            });
            vm.linkTo('theGeoAttribute', {
                type: 'CMDBuildUI.model.map.GeoAttribute',
                id: view._rowContext.record.get('_id')
            });
            vm.bind({
                bindTo: '{theGeoAttribute.type}'
            }, function (geoType) {
                if (geoType === CMDBuildUI.model.map.GeoAttribute.type.geometry) {
                    view.child('#typepropertiestab').tab.show();
                    view.child('#infowindowtab').tab.show();
                }
            })
        }
    },

    onEditBtnClick: function (button, event, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        CMDBuildUI.model.map.GeoAttribute.setProxy({
            url: view.up().grid.getViewModel().get('storedata.url'),
            type: 'baseproxy'
        });
        container.add({
            xtype: 'administration-components-geoattributes-card-form',
            viewModel: {
                links: {
                    theGeoAttribute: {
                        type: 'CMDBuildUI.model.map.GeoAttribute',
                        id: vm.get('theGeoAttribute._id')
                    }
                },

                data: {
                    actions: {
                        view: false,
                        edit: true,
                        add: false
                    },
                    grid: this.getView().up()
                }
            }
        });
    },
    /**
     * this is needed but unused
     * @param {*} store 
     */
    onTreeStoreDataChanged: function (store) {},

    onCloneBtnClick: function (button, event, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();

        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        CMDBuildUI.model.map.GeoAttribute.setProxy({
            url: view.up().grid.getViewModel().get('storedata.url'),
            type: 'baseproxy'
        });
        container.add({
            xtype: 'administration-components-geoattributes-card-form',
            viewModel: {

                data: {
                    theGeoAttribute: vm.get('theGeoAttribute').clone(),
                    actions: {
                        view: false,
                        edit: false,
                        add: true
                    },
                    grid: this.getView().up()
                }
            }
        });
    },

    onOpenBtnClick: function (button, event, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();

        CMDBuildUI.model.map.GeoAttribute.setProxy({
            url: view.up().grid.getViewModel().get('storedata.url'),
            type: 'baseproxy'
        });

        container.add({
            xtype: 'administration-components-geoattributes-card-form',
            viewModel: {
                links: {
                    theGeoAttribute: {
                        type: 'CMDBuildUI.model.map.GeoAttribute',
                        id: vm.get('theGeoAttribute._id')
                    }
                },

                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    },
                    grid: this.getView().up()
                }
            }
        });
    },

    onDeleteBtnClick: function () {
        var me = this;
        var view = me.getView();
        var vm = me.getViewModel();
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText.toLowerCase() === 'yes') {
                    CMDBuildUI.util.Utilities.showLoader(true, view);
                    CMDBuildUI.util.Ajax.setActionId('delete-geoattribute');
                    var store = view._rowContext.ownerGrid.getStore();

                    vm.get('theGeoAttribute').erase({
                        success: function (record, operation) {
                            CMDBuildUI.util.Utilities.showLoader(false, view);
                            CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                            store.load();
                        },
                        failure: function () {
                            CMDBuildUI.util.Utilities.showLoader(false, view);
                            vm.get('theGeoAttribute').reject();
                        }
                    });
                }
            }, this);
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onToggleActiveBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var grid = view.up('grid');
        var vm = view.getViewModel();
        var theGeoAttribute = vm.get('theGeoAttribute');
        theGeoAttribute.set('active', !theGeoAttribute.get('active'));
        theGeoAttribute.set('style', theGeoAttribute.getAssociatedData().style);
        delete theGeoAttribute.data.style.id;
        theGeoAttribute.save({
            success: function (record, operation) {
                view.up('administration-components-geoattributes-grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [grid, record, this]);
            },
            failure: function (record, reason) {
                record.reject();
                view.up('administration-components-geoattributes-grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('togglerow', [grid, record, vm.get('recordIndex')]);
                view.up('administration-components-geoattributes-grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('togglerow', [grid, record, vm.get('recordIndex')]);
            }
        });

    },


    onTranslateClick: function (event, button, eOpts) {
        var me = this;
        var vm = me.getViewModel();
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfGisAttributeClass(vm.get('theObject.name'), vm.get('theGeoAttribute.name'));
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, vm.get('action'), 'theDescriptionTranslation', vm);
    }

});