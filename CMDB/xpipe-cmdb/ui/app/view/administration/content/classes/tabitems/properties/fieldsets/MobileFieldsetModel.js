Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.MobileFieldsetModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-classes-tabitems-properties-fieldsets-mobilefieldset',

    formulas: {
        canShowMobileAttributes: {
            get: function () {
                return CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.mobile.enabled);
            }
        }
    },
    stores: {
        mobileSearchAttributesStore: {
            source: '{attributesStore}',
            filters: [function (item) {
                var mobileEditorTypes = [
                    CMDBuildUI.model.Attribute.types.bigInteger,
                    CMDBuildUI.model.Attribute.types.decimal,
                    CMDBuildUI.model.Attribute.types.double,
                    CMDBuildUI.model.Attribute.types.integer,
                    CMDBuildUI.model.Attribute.types.string,
                    CMDBuildUI.model.Attribute.types.text
                ];
                return item.get('type') && mobileEditorTypes.indexOf(item.get('type')) > -1;
            }]
        }
    }
});