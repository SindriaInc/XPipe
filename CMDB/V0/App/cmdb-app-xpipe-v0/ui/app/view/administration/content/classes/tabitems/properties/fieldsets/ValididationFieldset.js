Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.ValididationFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-classes-tabitems-properties-fieldsets-valididationfieldset',
    controller: 'administration-content-classes-tabitems-properties-fieldsets-valididationfieldset',
    items: [{
        xtype: 'fieldset',
        collapsible: true,
        collapsed: true,
        layout: 'column',
        title: CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.validation.title,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.validation.title'
        },
        ui: 'administration-formpagination',
        bind: {
            hidden: '{!theObject}'
        },
        items: [{
            columnWidth: 0.5,
            items: [CMDBuildUI.util.administration.helper.FieldsHelper.getValidationRule({
                vmObjectName: 'theObject',
                inputField: 'validationRule'
            })]
        }]
    }]
});