Ext.define('CMDBuildUI.view.dms.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.dms-tabpanel',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },
    onBeforeRender: function (view) {
        var height = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.inlinecard.height);
        view.setHeight(view.up('dms-container').getHeight() * .5);
    },

    onEditToolClick: function () {
        var view = this.getView();
        var model = view.getDMSModelClass();

        this.openEditPopup(model);
    },

    /**
     * 
     * @param {*} tool 
     * @param {*} e 
     */
    onDeleteToolClick: function (tool, e) {
        var vm = this.getViewModel();
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.attachments.deleteattachment,
            CMDBuildUI.locales.Locales.attachments.deleteattachment_confirmation,
            function (action) {
                if (action === "yes") {
                    CMDBuildUI.util.helper.FormHelper.startSavingForm();
                    var view = this.getView().lookupReference('dms-attachment-view');
                    var gridContainer = view.up('dms-container');
                    if (vm.get('dms-container.isAsyncSave')) {
                        var store = gridContainer.lookupViewModel().get('attachments');
                        store.remove(store.getById(view.getAttachmentId()));
                        CMDBuildUI.util.helper.FormHelper.endSavingForm();
                    } else {
                        var url = Ext.String.format('{0}{1}/{2}', CMDBuildUI.util.Config.baseUrl, vm.get('proxyUrl'), view.getAttachmentId());

                        Ext.Ajax.request({
                            url: url,
                            method: 'DELETE',
                            success: function (response) {
                                gridContainer.getViewModel().getStore('attachments').load();

                                var theObject = view.getTheObject();
                                if (theObject) {

                                    // execute form trigger
                                    view.executeAfterActionFormTriggers(
                                        CMDBuildUI.util.helper.FormHelper.formtriggeractions.afterDelete,
                                        view.getTheObject(),
                                        view.getApiForTrigger(CMDBuildUI.util.api.Client.getApiForFormAfterDelete())
                                    );
                                }
                            },
                            callback: function (options, success, response) {
                                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                            }
                        });
                    }
                }
            }, this
        );
    },

    /**
     * 
     * @param {*} tool 
     * @param {*} e 
     */
    onDownloadToolClick: function (tool, e) {
        var tabPanel = this.getView();

        var filename = this.getViewModel().get('record.name');
        var url = Ext.String.format('{0}{1}/{2}/{3}',
            CMDBuildUI.util.Config.baseUrl,
            this.getViewModel().get('proxyUrl'), //specificated in CMDBuildUI.view.dms.GridModel
            tabPanel.getAttachmentId(),
            filename
        );

        CMDBuildUI.util.File.download(url, filename);
    },

    openEditPopup: function (model) {
        var view = this.getView(),
            vm = view.lookupViewModel(),
            gridContainer = view.up('dms-container'),
            title = CMDBuildUI.locales.Locales.attachments.editattachment + ' ' + view.getDMSCategoryDescription(),
            attachmentsStore = gridContainer.lookupViewModel().get('attachments'),
            attachmentName = view.getTheObject().get("name"),
            invalidFileNames = Ext.Array.remove(attachmentsStore.collect("name"), attachmentName),
            theObject;

        if (view.getTheObject().phantom || view.getTheObject().dirty) {
            theObject = view.getTheObject();
        }

        CMDBuildUI.util.Utilities.openPopup('popup-edit-attachment-form', title, {
            xtype: 'dms-attachment-edit',
            objectType: gridContainer.getObjectType(),
            objectTypeName: gridContainer.getObjectTypeName(),
            objectId: gridContainer.getObjectId(),
            attachmentId: view.getAttachmentId(),
            DMSCategoryTypeName: gridContainer.getDMSCategoryTypeName(),
            DMSCategoryValue: view.getDMSCategoryValue(),
            ignoreSchedules: gridContainer.getIgnoreSchedules(),
            theObject: theObject,
            asyncStore: vm.get('dms-container.isAsyncSave') ? attachmentsStore : null,
            invalidFileNames: invalidFileNames,
            currentFileName: attachmentName
        }, {
            popupsave: {
                fn: function () {
                    gridContainer.getViewModel().getStore('attachments').load();
                },
                scope: this
            },
            popupcancel: function () { }
        });
    }
});
