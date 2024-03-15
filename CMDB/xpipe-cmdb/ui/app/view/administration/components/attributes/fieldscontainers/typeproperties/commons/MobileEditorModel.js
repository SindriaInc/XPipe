Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.commons.MobileEditorModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-attribute-commons-mobileedtor',
    formulas: {
        canShowMobileAttributes: {
            bind: '{theAttribute.type}',
            get: function (attributeType) {
                var mobileEditorTypes = [
                    CMDBuildUI.model.Attribute.types.bigInteger,
                    CMDBuildUI.model.Attribute.types.decimal,
                    CMDBuildUI.model.Attribute.types.double,
                    CMDBuildUI.model.Attribute.types.integer,
                    CMDBuildUI.model.Attribute.types.string,
                    CMDBuildUI.model.Attribute.types.text
                ];
                return attributeType && mobileEditorTypes.indexOf(attributeType) > -1 && CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.mobile.enabled);
            }
        },
        mobileEditors: {
            bind: '{theAttribute.type}',
            get: function (attributeType) {
                return CMDBuildUI.model.Attribute.getMobileEditors(attributeType);
            }
        }

    },

    stores: {
        mobileEditorsStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: "memory",
            data: '{mobileEditors}',
            autoDestroy: true
        }
    }
});