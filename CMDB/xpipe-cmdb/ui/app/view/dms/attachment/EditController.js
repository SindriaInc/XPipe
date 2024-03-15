Ext.define('CMDBuildUI.view.dms.attachment.EditController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.dms-attachment-edit',
    control: {
        '#': {
            beforerender: 'onBeforeRender',
            beforedestroy: 'onBeforeDestroy'
        }
    },

    onBeforeRender: function (view) {
        var vm = this.getViewModel();

        vm.bind({
            DMSCategoryTypeName: '{dms-attachment-edit.DMSCategoryTypeName}',
            DMSCategoryValue: '{dms-attachment-edit.DMSCategoryValue}',
            DMSModelClass: '{dms-attachment-edit.DMSModelClass}',
            DMSClass: '{dms-attachment-edit.DMSClass}',
            DMSWidgets: '{DMSWidgets}'
        }, this.itemsUpdate, this);
    },

    itemsUpdate: function (data) {

        var view = this.getView();

        if (data.DMSModelClass && data.DMSCategoryValue && data.DMSCategoryTypeName && data.DMSClass && data.DMSWidgets) {
            CMDBuildUI.util.helper.FormHelper.renderFormForType(CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel,
                data.DMSClass.getId(), {
                mode: CMDBuildUI.util.helper.FormHelper.formmodes.update,
                linkName: 'dms-attachment-edit.theObject',
                showAsFieldsets: true, //important
                readonly: true,
                ignoreSchedules: view.getIgnoreSchedules()
            }).then(function (items) {

                //adds the upload  field
                items.push({
                    title: CMDBuildUI.locales.Locales.attachments.file,
                    groupId: CMDBuildUI.locales.Locales.attachments.file,
                    xtype: "formpaginationfieldset",
                    collapsed: false,
                    hidden: false,
                    items: [{
                        xtype: 'container',
                        layout: 'column',
                        defaults: {
                            xtype: "fieldcontainer",
                            columnWidth: 0.5,
                            flex: 0.5,
                            padding: "0 15 0 15",
                            layout: "anchor",
                            minHeight: 1
                        },
                        items: [{
                            layout: 'fit',
                            items: [{
                                xtype: 'draganddropfilefield',
                                reference: 'file',
                                fieldLabel: CMDBuildUI.locales.Locales.attachments.file,
                                allowBlank: true,
                                allowMultiUpload: false,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.attachments.file'
                                },
                                allowedExtensions: CMDBuildUI.util.helper.AttachmentsHelper.getAllowedExtensions(data.DMSCategoryTypeName, data.DMSCategoryValue, data.DMSModelClass),
                                maxFileSize: view.getMaxFileSize(data.DMSCategoryTypeName, data.DMSCategoryValue, data.DMSModelClass),
                                invalidFileNames: view.getInvalidFileNames(),
                                currentFileName: view.getCurrentFileName()
                            }]
                        }, {
                            layout: 'fit',
                            items: [{
                                xtype: 'checkboxfield',
                                reference: 'majorversion',
                                fieldLabel: CMDBuildUI.locales.Locales.attachments.majorversion,
                                autoEl: {
                                    'data-testid': 'attachmentform-majorversion'
                                },
                                bind: {
                                    value: '{dms-attachment-edit.theObject.majorVersion}'
                                },
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.attachments.majorversion'
                                }
                            }]
                        }]
                    }]
                });

                var theObject = view.getTheObject(),
                    formView = view.down('#formpanel'),
                    widgets = data.DMSWidgets,
                    widgetsInline = widgets.query("_inline", true);

                formView.removeAll(true);

                if (widgetsInline) {
                    // add inline widgets
                    CMDBuildUI.view.widgets.Launchers.addInlineWidgets(theObject, widgetsInline, view, items);
                }

                formView.add(view.getFormItems(items));

                // init before edit triggers
                view.initBeforeActionFormTriggers(
                    CMDBuildUI.util.helper.FormHelper.formtriggeractions.beforeEdit,
                    view.getApiForTrigger(CMDBuildUI.util.api.Client.getApiForFormBeforeEdit())
                );

                // execute widgets on target form open
                CMDBuildUI.util.helper.WidgetsHelper.executeOnTargetFormOpen(
                    theObject,
                    widgets.getRange(), {
                    formmode: view.formmode
                });
            });
        }
    },

    onSaveButton: function (button, e) {
        var view = this.getView(),
            vm = this.getViewModel();

        if (view.isValid()) {
            CMDBuildUI.util.helper.FormHelper.startSavingForm();

            var theObject = view.getTheObject(),
                widgets = vm.get("DMSWidgets").getRange(),
                field = this.lookupReference("file"),
                files = field.getValue(),
                file = files[0],
                loadmask = CMDBuildUI.util.Utilities.addLoadMask(view),
                success = 0,
                errors = 0;

            // execute widgets before save actions
            CMDBuildUI.util.helper.WidgetsHelper.executeBeforeTargetSave(
                theObject,
                widgets, {
                formmode: view.formmode
            }).then(function () {
                if (file) {
                    theObject.set("_filedata", file);
                }

                // finish function
                function finish() {
                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                    // if all files are uploaded
                    if (success) {
                        // execute widgets after save actions
                        CMDBuildUI.util.helper.WidgetsHelper.executeAfterTargetSave(
                            theObject,
                            widgets, {
                            formmode: view.formmode
                        }).then(function () {
                            view.up("panel").fireEvent('popupsave');
                            view.up("panel").close();
                        });
                    } else if (errors) {
                        CMDBuildUI.util.Utilities.removeLoadMask(loadmask);
                    }
                }

                if (view.isAsyncSave()) {
                    var store = view.getAsyncStore();
                    store.remove(store.getById(theObject.getId()))
                    store.addSorted(theObject);
                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                    view.up("panel").close();
                } else {
                    theObject.saveAttachmentAndSequences(file ? file.get('file') : null, {
                        type: view.getObjectType(),
                        typeName: view.getObjectTypeName(),
                        id: view.getObjectId()
                    }).then(function () {
                        success++;
                        finish();
                    }).otherwise(function (error) {
                        file ? file.set("status", CMDBuildUI.model.dms.File.statuses.error) : null;
                        errors++;

                        var response = {
                            responseText: error
                        };
                        CMDBuildUI.util.Ajax.showMessages(response, {
                            hideErrorNotification: false
                        });

                        finish();
                    });
                }
            });
        }
    },

    onCancelButton: function (button, e) {
        var view = this.getView();
        view.closePanel();
    },

    /**
     * Unlock card on management details window close.
     * @param {CMDBuildUI.view.classes.cards.card.Edit} view 
     * @param {Object} eOpts 
     */
    onBeforeDestroy: function (view, eOpts) {
        var theObject = view.getTheObject();
        if (theObject) {
            theObject.removeLock();
        }
    }
});