Ext.define('CMDBuildUI.view.widgets.attachmentwidget.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.widgets-attachmentwidget-panel',
    control: {
        '#': {
            beforerender: 'onBeforeRender',
            beforedestroy: 'onBeforeDestroy'
        },
        '#closebtn': {
            click: 'onCloseBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.widgets.createmodifycard.Panel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var btnVm = view._widgetOwner.getViewModel(),
            theWidget = view.lookupViewModel().get("theWidget"),
            owner = theWidget.getOwner(),
            store = theWidget.get("_attachmentsStore"),
            objectType = btnVm.get('objectType'),
            objectTypeName = btnVm.get('objectTypeName'),
            objectId = btnVm.get('objectId');

        var conf = {
            xtype: 'dms-container',
            objectType: objectType,
            objectTypeName: objectTypeName,
            objectId: objectId,
            isAsyncSave: true,
            viewModel: {
                data: {
                    basepermissions: {
                        edit: btnVm.get('basepermissions.edit'),
                        delete: btnVm.get('basepermissions.delete')
                    },
                    objectType: objectType,
                    objectTypeName: objectTypeName,
                    objectId: objectId
                },
                formulas: {
                    changeData: {
                        bind: '{attachments}',
                        get: function (store) {
                            if (!store.hasListener("datachanged")) {
                                store.addListener("datachanged", function (store, eOpts) {
                                    if (theWidget.get("_required")) {
                                        owner.setValue(store.getCount() > 0 ? true : null);
                                        owner.validate();
                                    }
                                });
                            }
                        }
                    }
                }
            },
            readOnly: view.getFormMode() === CMDBuildUI.util.helper.FormHelper.formmodes.read
        };

        // configure grid height if needed
        if (theWidget.get('_inline')) {
            conf.height = view.up("form").getHeight() * 0.5;
        }
        // add existing store
        if (store) {
            conf.viewModel.stores = {
                attachments: store
            }
        }

        // add attachmnets grid to the widget
        view.add(conf);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCloseBtnClick: function (button, e, eOpts) {
        this.getView().fireEvent("popupclose");
    },

    /**
     * 
     * @param {Ext.panel.Panel} popup 
     * @param {Object} eOpts 
     */
    onBeforeDestroy: function (view, eOpts) {
        // get widget definition and attachments store
        var theWidget = view.lookupViewModel().get("theWidget"),
            attachmentsStore = view.down("dms-container").lookupViewModel().get("attachments");

        // save the store to the widget definition
        if (attachmentsStore) {
            attachmentsStore.setAutoDestroy(false);
            theWidget.set("_attachmentsStore", attachmentsStore);
        }
    }

});