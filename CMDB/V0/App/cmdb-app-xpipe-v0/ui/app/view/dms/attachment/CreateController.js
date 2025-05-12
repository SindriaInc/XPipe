Ext.define('CMDBuildUI.view.dms.attachment.CreateController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.dms-attachment-create',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view) {
        var vm = this.getViewModel();

        vm.bind({
            DMSCategoryTypeName: '{dms-attachment-create.DMSCategoryTypeName}',
            DMSCategoryValue: '{dms-attachment-create.DMSCategoryValue}',
            DMSModelClass: '{dms-attachment-create.DMSModelClass}',
            DMSClass: '{dms-attachment-create.DMSClass}',
            DMSWidgets: '{DMSWidgets}'
        }, this.itemsUpdate, this);
    },

    itemsUpdate: function (data) {

        var view = this.getView();

        if (data.DMSModelClass && data.DMSCategoryValue && data.DMSCategoryTypeName && data.DMSClass && data.DMSWidgets) {
            CMDBuildUI.util.helper.FormHelper.renderFormForType(CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel,
                data.DMSClass.getId(), {
                mode: CMDBuildUI.util.helper.FormHelper.formmodes.create,
                linkName: 'dms-attachment-create.theObject',
                showAsFieldsets: true, //important
                readonly: true,
                ignoreSchedules: view.getIgnoreSchedules()
            }).then(function (items) {

                //enables or disables multi upload whene the DMS Model has some form triggers afterInsert
                var allowMultiUpload = true;
                var DMSClass = CMDBuildUI.util.helper.ModelHelper.getDMSModelFromName(data.DMSModelClass.objectTypeName);
                var triggers = DMSClass.getFormTriggersForAction(CMDBuildUI.util.helper.FormHelper.formtriggeractions.afterInsert);
                if (triggers && triggers.length) {
                    allowMultiUpload = false;
                }

                //adds the upload field
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
                                allowBlank: false,
                                allowMultiUpload: allowMultiUpload,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.attachments.file'
                                },
                                allowedExtensions: CMDBuildUI.util.helper.AttachmentsHelper.getAllowedExtensions(data.DMSCategoryTypeName, data.DMSCategoryValue, data.DMSModelClass),
                                maxFileSize: view.getMaxFileSize(data.DMSCategoryTypeName, data.DMSCategoryValue, data.DMSModelClass),
                                invalidFileNames: view.getInvalidFileNames()
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
                    CMDBuildUI.util.helper.FormHelper.formtriggeractions.beforeInsert,
                    view.getApiForTrigger(CMDBuildUI.util.api.Client.getApiForFormBeforeCreate())
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
            var field = this.lookupReference("file"),
                theObject = view.getTheObject(),
                metadata = theObject.getData(),
                files = field.getValue(),
                sequences = theObject.sequences().getRange(),
                categoryDescription = view.lookupViewModel().get("dms-attachment-create.DMSCategoryDescription"),
                widgets = vm.get("DMSWidgets").getRange(),
                records = [];

            // execute widgets before save actions
            CMDBuildUI.util.helper.WidgetsHelper.executeBeforeTargetSave(
                theObject,
                widgets, {
                formmode: view.formmode
            }).then(function () {
                // create one record for each file
                files.forEach(function (file) {
                    // create the record
                    // var record = theObject.copy();
                    var record = Ext.create(theObject.$className, Ext.applyIf({
                        _id: null,
                        _filedata: file,
                        _category_description_translation: categoryDescription,
                        _can_update: true,
                        _can_delete: true,
                        name: file.get("name"),
                        version: "1.0"
                    }, metadata));

                    if (sequences.length) {
                        var sequencesCopy = [];
                        // var sequencesCopy = record.sequences();
                        sequences.forEach(function (s) {
                            // create a copy of the schedule
                            var scopy = Ext.create(s.$className, Ext.applyIf({
                                _id: null
                            }, s.getData()));
                            // copy of the events
                            scopy.events().setData(s.events().getRange());
                            // add the copy of the sequence to the array
                            sequencesCopy.push(scopy);
                        });
                        record.sequences().add(sequencesCopy);
                    }
                    records.push(record);
                });

                if (view.isAsyncSave()) {
                    // execute widgets after save actions
                    CMDBuildUI.util.helper.WidgetsHelper.executeAfterTargetSave(
                        theObject,
                        widgets, {
                        formmode: view.formmode
                    }).then(function () {
                        // add records on store
                        view.getAsyncStore().add(records);
                        CMDBuildUI.util.helper.FormHelper.endSavingForm();
                        view.up("panel").close();
                    });
                } else {
                    var loadmask = CMDBuildUI.util.Utilities.addLoadMask(view),
                        success = 0,
                        errors = 0;

                    // finish function
                    var finish = function () {
                        CMDBuildUI.util.helper.FormHelper.endSavingForm();
                        // if all files are uploaded
                        switch (files.length) {
                            case success:
                                // execute widgets after save actions
                                CMDBuildUI.util.helper.WidgetsHelper.executeAfterTargetSave(
                                    theObject,
                                    widgets, {
                                    formmode: view.formmode
                                }).then(function () {
                                    view.up("panel").fireEvent('popupsave');
                                    view.up("panel").close();
                                });
                                break;
                            case (success + errors):
                                CMDBuildUI.util.Utilities.removeLoadMask(loadmask);
                                view.isValid();
                                break;
                        }
                    };

                    // in case of many files, to avoid that the saves go in error, we divide the saves into blocks of 10
                    // issue #6358 uploading a lot of documents causes cmdbuild to freeze
                    var i, j, tempArray = [],
                        chunk = 10;

                    var checkCompleted = function (chunkIndex) {
                        if (tempArray.length - 1 >= chunkIndex) {
                            var nextChunkIndex = chunkIndex + 1;
                            if (tempArray[nextChunkIndex]) {
                                uploadChunk(tempArray[nextChunkIndex], nextChunkIndex);
                            } else {
                                finish();
                            }
                        } else {
                            finish();
                        }
                    };

                    var uploadChunk = function (filesArray, chunkIndex) {
                        var currentCompleted = 0,
                            currentErrors = 0;

                        filesArray.forEach(function (record) {
                            record.saveAttachmentAndSequences(record.get("_filedata").get("file"), {
                                type: view.getObjectType(),
                                typeName: view.getObjectTypeName(),
                                id: view.getObjectId()
                            }).then(function () {
                                success++;
                                currentCompleted++;
                                if (currentCompleted + currentErrors === filesArray.length) {
                                    checkCompleted(chunkIndex);
                                }
                            }).otherwise(function (error) {
                                record.get("_filedata").set("status", CMDBuildUI.model.dms.File.statuses.error);
                                errors++;
                                currentErrors++;

                                var response = {
                                    responseText: error
                                };
                                CMDBuildUI.util.Ajax.showMessages(response, {
                                    hideErrorNotification: false
                                });
                                if (currentCompleted + currentErrors === filesArray.length) {
                                    checkCompleted(chunkIndex);
                                }
                            });
                        });
                    };

                    if (records.length) {
                        for (i = 0, j = records.length; i < j; i += chunk) {
                            tempArray.push(records.slice(i, i + chunk));
                        }
                        uploadChunk(tempArray[0], 0);
                    }
                }
            });
        }
    },

    onCancelButton: function (button, e) {
        var view = this.getView();
        var upPanel = view.up("panel");

        view.up("panel").fireEvent('popupcancel');
        upPanel.close();
    }
});