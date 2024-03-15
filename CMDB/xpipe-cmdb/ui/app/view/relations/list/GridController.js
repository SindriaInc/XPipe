Ext.define('CMDBuildUI.view.relations.list.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.relations-list-grid',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            itemdblclick: 'onItemDblClick'
        },
        'tableview': {
            actionopencard: 'onActionOpenCard',
            actioneditrelation: 'onActionEditRelation',
            actiondeleterelation: 'onActionDeleteRelation',
            actioneditcard: 'onActionEditCard'
        }
    },

    onBeforeRender: function (view) {
        //sets the height of the grid;
        view.calculateHeight();

        view.lookupViewModel().bind({
            bindTo: '{allRelations}'
        }, function (store) {
            var loadmask = CMDBuildUI.util.Utilities.addLoadMask(view);
            // enable remote sort here because
            // autoLoad=false is ignored when grouping is actived
            // and remoteSort is set to true. See EXTJS-19781.
            store.setRemoteSort(true);
            // load store
            store.load({
                callback: function () {
                    CMDBuildUI.util.Utilities.removeLoadMask(loadmask);
                },
                scope: this
            });
        });
    },

    /**
    * @param {CMDBuildUI.view.attachments.Grid} grid
    * @param {Ext.data.Model} record
    * @param {Number} rowIndex
    * @param {Number} colIndex
    * @param {Boolean} openInGrid
    *
    */
    onActionOpenCard: function (grid, record, rowIndex, colIndex, openInGrid) {
        var path,
            destinationType = record.get("_destinationType"),
            destinationId = record.get("_destinationId");
        switch (CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(destinationType)) {
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                path = CMDBuildUI.util.Navigation.getClassBaseUrl(destinationType, destinationId, null, true);
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                path = CMDBuildUI.util.Navigation.getProcessBaseUrl(destinationType, destinationId, null, null, true);
                break;
        }
        if (!openInGrid) {
            path += '/view';
        }
        if (path) {
            CMDBuildUI.util.Utilities.redirectTo(path);
        }
    },

    /**
    * @param {CMDBuildUI.view.relations.list.Grid} grid
    * @param {Ext.data.Model} record
    * @param {Number} rowIndex
    * @param {Number} colIndex
    *
    */
    onActionEditRelation: function (grid, record, rowIndex, colIndex) {
        var vm = grid.lookupViewModel();
        CMDBuildUI.view.relations.Utils.editRelation(record, {
            proxyurl: vm.get("storedata.proxyurl"),
            objecttypename: vm.get("objectTypeName"),
            objectid: vm.get("objectId")
        }).then(function () {
            grid.getStore().reload();
            Ext.GlobalEvents.fireEventArgs("updateMasterDetailStore", [record.get("_type")]);
        });
    },

    /**
    * @param {CMDBuildUI.view.relations.list.Grid} grid
    * @param {Ext.data.Model} record
    * @param {Number} rowIndex
    * @param {Number} colIndex
    *
    */
    onActionDeleteRelation: function (grid, record, rowIndex, colIndex) {
        CMDBuildUI.view.relations.Utils.deleteRelation(record).then(function () {
            Ext.GlobalEvents.fireEventArgs("updateMasterDetailStore", [record.get("_type")]);
        }).otherwise(function () {
            grid.getStore().reload();
        });
    },

    /**
    * @param {CMDBuildUI.view.relations.list.Grid} grid
    * @param {Ext.data.Model} record
    * @param {Number} rowIndex
    * @param {Number} colIndex
    *
    */
    onActionEditCard: function (grid, record, rowIndex, colIndex) {
        var popup,
            config = {
                xtype: 'classes-cards-card-edit',
                padding: 10,
                viewModel: {
                    data: {
                        objectTypeName: record.get("_destinationType"),
                        objectId: record.get("_destinationId")
                    }
                },
                hideInlineElements: false,
                buttons: [{
                    ui: 'management-action',
                    itemId: 'detailsavebtn',
                    text: CMDBuildUI.locales.Locales.common.actions.save,
                    autoEl: {
                        'data-testid': 'relations-list-grid-editcard-save'
                    },
                    formBind: true,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.common.actions.save'
                    },
                    handler: function (btn, event) {
                        CMDBuildUI.util.helper.FormHelper.startSavingForm();
                        var cancelBtn = this.up().down("#detailclosebtn");
                        btn.showSpinner = true;
                        CMDBuildUI.util.Utilities.disableFormButtons([btn, cancelBtn]);

                        popup.down("classes-cards-card-edit").getController().saveForm().then(function () {
                            grid.getStore().load();
                            Ext.GlobalEvents.fireEventArgs("updateMasterDetailStore", [record.get("_type")]);
                            CMDBuildUI.util.helper.FormHelper.endSavingForm();
                            popup.destroy();
                        }).otherwise(function () {
                            CMDBuildUI.util.helper.FormHelper.endSavingForm();
                            CMDBuildUI.util.Utilities.enableFormButtons([btn, cancelBtn]);
                        });
                    }
                }, {
                    ui: 'secondary-action',
                    itemId: 'detailclosebtn',
                    text: CMDBuildUI.locales.Locales.common.actions.close,
                    autoEl: {
                        'data-testid': 'relations-list-grid-editcard-cancel'
                    },
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.common.actions.close'
                    },
                    handler: function (btn, event) {
                        popup.destroy();
                    }
                }],

                listeners: {
                    itemupdated: function () {
                        popup.close();
                        grid.getStore().load();
                    },
                    cancelupdating: function () {
                        popup.close();
                    }
                }
            };

        // open popup
        popup = CMDBuildUI.util.Utilities.openPopup(null, record.get("_destinationDescription"), config);
    },

    /**
     * @param {CMDBuildUI.view.relations.list.Grid} grid
     * @param {Ext.data.Model} record
     * @param {HTMLElement} item
     * @param {Number} index
     * @param {Ext.event.Event} e
     * @param {Object} eOpts
     */
    onItemDblClick: function (grid, record, item, index, e, eOpts) {
        this.onActionOpenCard(grid, record, null, null, grid.lookupViewModel().get("readonly"));
    }
});
