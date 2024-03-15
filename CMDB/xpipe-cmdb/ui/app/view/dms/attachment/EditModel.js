Ext.define('CMDBuildUI.view.dms.attachment.EditModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.dms-attachment-edit',

    formulas: {
        DMSModelClassNameCalculation: {
            bind: {
                DMSCategoryTypeName: '{dms-attachment-edit.DMSCategoryTypeName}',
                DMSCategoryTypeValue: '{dms-attachment-edit.DMSCategoryValue}'
            },
            get: function (data) {
                if (data.DMSCategoryTypeName && data.DMSCategoryTypeValue) {
                    var DMSCatygoryType = CMDBuildUI.model.dms.DMSCategoryType.getCategoryTypeFromName(data.DMSCategoryTypeName);

                    DMSCatygoryType.getCategoryValues().then(function (categoryValues) {
                        // this.getView().setDMSCategoryType(DMSCatygoryType);
                        var r = categoryValues.findRecord('_id', data.DMSCategoryTypeValue);

                        if (r) {
                            var DMSModelClassName = r.get('modelClass') || 'BaseDocument';
                            this.getView().setDMSModelClassName(DMSModelClassName);
                        }

                    }, Ext.emptyFn, Ext.emptyFn, this);
                }
            }
        },

        DMSClassCalculation: {
            bind: '{dms-attachment-edit.DMSModelClassName}',
            get: function (DMSModelClassName) {

                if (DMSModelClassName) {
                    return CMDBuildUI.util.helper.ModelHelper.getDMSModelFromName(DMSModelClassName);
                }
            }
        },

        DMSmodelClassCalculation: {
            bind: '{dms-attachment-edit.DMSModelClassName}',
            get: function (modelClassName) {
                if (modelClassName) {
                    CMDBuildUI.util.helper.ModelHelper.getModel(
                        CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel,
                        modelClassName
                    ).then(function (model) {
                        this.getView().setDMSModelClass(model);
                    }, Ext.emptyFn, Ext.emptyFn, this);
                }
            }
        },

        updatTheObject: {
            bind: {
                objectTypeName: '{dms-attachment-edit.objectTypeName}',
                objectId: '{dms-attachment-edit.objectId}',
                objectType: '{dms-attachment-edit.objectType}',
                DMSModelClass: '{dms-attachment-edit.DMSModelClass}',
                attachmentId: '{dms-attachment-edit.attachmentId}'
            },
            get: function (data) {
                if (!this.get("dms-attachment-edit.theObject") && data.objectTypeName && data.objectId && data.objectType && data.DMSModelClass && data.attachmentId) {

                    var proxyUrl;
                    switch (data.objectType) {
                        case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                            proxyUrl = CMDBuildUI.util.api.Classes.getAttachments(data.objectTypeName, data.objectId);
                            break;
                        case CMDBuildUI.util.helper.ModelHelper.objecttypes.calendar:
                            proxyUrl = CMDBuildUI.util.api.Calendar.getAttachmentsUrl(data.objectId);
                            break;
                        case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                            proxyUrl = CMDBuildUI.util.api.Processes.getAttachmentsUrl(data.objectTypeName, data.objectId);
                            break;
                        default:
                            CMDBuildUI.util.Notifier.showErrorMessage(Ext.String.format('No attachments url for {0} object', data.objectType));
                            return;
                    }

                    var theObject = Ext.create(data.DMSModelClass.getName(), {
                        _id: data.attachmentId,
                        majorVersion: false
                    });
                    theObject.getProxy().setUrl(proxyUrl);

                    theObject.load({
                        callback: function (record, operation, success) {
                            theObject.isLocked().then(function (locked) {
                                if ((Ext.isBoolean(locked) && locked == false) || (Ext.isObject(locked) && (locked.found == false || (locked.data && locked.data._owned_by_current_session == true)))) {
                                    this.getView().setTheObject(record);
                                } else {
                                    this.getView().closePanel();
                                    var usr;
                                    if (Ext.isObject(locked)) {
                                        usr = locked.user;
                                    }
                                    theObject.showLockMessage(usr);
                                }
                            }, Ext.emptyFn, Ext.emptyFn, this);
                        },
                        scope: this
                    });
                }
            }
        },

        DMSWidgets: {
            bind: '{dms-attachment-edit.theObject.widgets}',
            get: function(widgets) {
                return widgets;
            }
        }
    }

});
