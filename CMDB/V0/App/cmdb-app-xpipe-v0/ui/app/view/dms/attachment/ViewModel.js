Ext.define('CMDBuildUI.view.dms.attachment.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.dms-attachment-view',

    formulas: {
        DMSModelClassNameCalculation: {
            bind: {
                DMSCategoryTypeName: '{dms-attachment-view.DMSCategoryTypeName}',
                DMSCategoryTypeValue: '{dms-attachment-view.DMSCategoryValue}'
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
            bind: '{dms-attachment-view.DMSModelClassName}',
            get: function (DMSModelClassName) {

                if (DMSModelClassName) {
                    return CMDBuildUI.util.helper.ModelHelper.getDMSModelFromName(DMSModelClassName);
                }
            }
        },

        DMSmodelClassCalculation: {
            bind: '{dms-attachment-view.DMSModelClassName}',
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

        DMSWidgets: {
            bind: '{dms-attachment-view.theObject.widgets}',
            get: function(widgets) {
                return widgets;
            }
        }
    }
});
