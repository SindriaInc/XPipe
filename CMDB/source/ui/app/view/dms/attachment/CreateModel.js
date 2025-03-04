Ext.define('CMDBuildUI.view.dms.attachment.CreateModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.dms-attachment-create',

    data: {
        objectType: undefined,
        objectTypeName: undefined,
        objectId: undefined,
        DMSCategoryTypeName: undefined,
        DMSCategoryValue: undefined,
        DMSCategoryDescription: undefined,
        DMSModelClassName: undefined,
        DMSModelClass: undefined,
        DMSWidgets: undefined,
        DMSClass: undefined,
        theObject: undefined
    },

    formulas: {
        DMSCalculations: {
            bind: {
                DMSCategoryTypeName: '{DMSCategoryTypeName}',
                DMSCategoryTypeValue: '{DMSCategoryValue}'
            },
            get: function (data) {
                if (data.DMSCategoryTypeName && data.DMSCategoryTypeValue) {
                    const DMSCatygoryType = CMDBuildUI.model.dms.DMSCategoryType.getCategoryTypeFromName(data.DMSCategoryTypeName);

                    DMSCatygoryType.getCategoryValues().then(function (categoryValues) {
                        const r = categoryValues.findRecord('_id', data.DMSCategoryTypeValue);

                        if (r) {
                            const DMSModelClassName = r.get('modelClass') || 'BaseDocument';
                            this.set("DMSModelClassName", DMSModelClassName);

                            if (DMSModelClassName) {
                                const DMSClass = CMDBuildUI.util.helper.ModelHelper.getDMSModelFromName(DMSModelClassName)
                                this.set("DMSClass", DMSClass);
                                this.set("DMSWidgets", DMSClass.widgets());

                                CMDBuildUI.util.helper.ModelHelper.getModel(
                                    CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel,
                                    DMSModelClassName
                                ).then(function (model) {
                                    this.set("DMSModelClass", model);
                                }, Ext.emptyFn, Ext.emptyFn, this);
                            }
                        }

                    }, Ext.emptyFn, Ext.emptyFn, this);
                }
            }
        },

        updateTheObject: {
            bind: {
                objectTypeName: '{objectTypeName}',
                objectId: '{objectId}',
                objectType: '{objectType}',
                DMSModelClass: '{DMSModelClass}',
                DMSCategoryValue: '{DMSCategoryValue}'
            },
            get: function (data) {
                if (data.DMSModelClass && data.DMSCategoryValue) {
                    const theObject = Ext.create(data.DMSModelClass.getName(), {
                        _id: null,//seems it doesn't work. Autogenerated id is created even with this config
                        category: data.DMSCategoryValue
                    });
                    this.set("theObject", theObject);

                    if (data.objectTypeName && data.objectId && data.objectType) {
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

                        theObject.getProxy().setUrl(proxyUrl);
                    }
                }
            }
        }
    }

});
