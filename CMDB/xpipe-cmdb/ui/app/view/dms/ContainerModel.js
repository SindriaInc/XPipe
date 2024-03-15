Ext.define('CMDBuildUI.view.dms.ContainerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.dms-container',
    data: {
        'dms-container': {
            objectType: null,
            objectTypeName: null,
            objectId: null,
            DMSCategoryTypeName: null,
            DMSCategoryType: null,
            readOnly: null,
            ignoreSchedules: true
        },
        enableExtendedGrid: false,
        disabledbulkactions: true,
        textAlert: null,
        autoLoad: false
    },

    formulas: {

        DMSCategoryTypeNameCalculation: {
            bind: {
                objectTypeName: '{dms-container.objectTypeName}',
                objectType: '{dms-container.objectType}'
            },
            get: function (data) {
                if (data.objectType && data.objectTypeName) {
                    var klass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.objectTypeName, data.objectType);
                    var DMSCategoryTypeName = klass.get('dmsCategory') || klass.get('attachmentTypeLookup') || CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.category); //FIXME: In future here should be only attachmentTypeLookup
                    this.getView().setDMSCategoryTypeName(DMSCategoryTypeName);
                }
            }
        },

        DMSCategoryTypeCalculation: {
            bind: '{dms-container.DMSCategoryTypeName}',
            get: function (DMSCategoryTypeName) {
                if (DMSCategoryTypeName) {
                    var DMSCatygoryType = CMDBuildUI.model.dms.DMSCategoryType.getCategoryTypeFromName(DMSCategoryTypeName);

                    if (DMSCatygoryType) {
                        DMSCatygoryType.getCategoryValues().then(function (categoryValues) {
                            this.getView().setDMSCategoryType(DMSCatygoryType);
                        }, Ext.emptyFn, Ext.emptyFn, this);
                    }
                }
            }
        },

        proxyUrl: {
            bind: {
                objectTypeName: '{dms-container.objectTypeName}',
                objectType: '{dms-container.objectType}',
                objectId: '{dms-container.objectId}'
            },
            get: function (data) {
                if (data.objectType && data.objectTypeName && data.objectId) {
                    this.set('autoLoad', true);

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
                    return proxyUrl;
                }
                return "";
            }
        },

        disableRemoteActions: {
            bind: {
                proxyUrl: '{proxyUrl}'
            },
            get: function (data) {
                return !data.proxyUrl;
            }
        }
    },

    stores: {
        attachments: {
            model: 'CMDBuildUI.model.dms.DMSAttachment',
            proxy: {
                url: '{proxyUrl}',
                type: 'baseproxy',
                extraParams: {
                    limit: 0,
                    detailed: true
                }
            },
            listeners: {
                load: 'onAttachmentsLoad'
            },
            groupField: '_category_description_translation',
            autoLoad: '{autoLoad}'
        }
    }
});
