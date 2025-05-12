Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.Boolean', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-attribute-booleanfields',
    layout: 'column',
    items: [{
        columnWidth: 0.5,
        xtype: 'fieldcontainer',
        items: [{
            xtype: 'checkbox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.defaultfalse,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.defaultfalse'
            },
            bind: {                
                disabled: '{actions.view}'
            },
            autoEl: {
                'data-testid': 'attribute-boolean_default_value_input'
            },
            listeners: {
                beforerender: function (checkbox) {
                    var vm = checkbox.lookupViewModel();
                    vm.bind({
                        bindTo: '{theAttribute.defaultValue}',
                        single: true
                    }, function (defaultValue) {
                        checkbox.setValue(defaultValue === 'false' ? true : false);
                    });
                },
                change: function (checkbox, newValue, oldValue) {
                    var vm = checkbox.lookupViewModel();
                    vm.set('theAttribute.defaultValue', newValue ? 'false' : null);
                }
            }
        }]
    }]
});