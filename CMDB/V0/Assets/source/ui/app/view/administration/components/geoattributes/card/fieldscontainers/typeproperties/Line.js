Ext.define('CMDBuildUI.view.administration.components.geoattributes.card.fieldscontainers.typeproperties.Line', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-geoattributes-typeproperties-line',
    layout: 'fit',
    config: {

    },
    viewModel: {

    },
    items: [
        CMDBuildUI.view.administration.components.geoattributes.card.FieldsHelper.getLineInputs()
    ]
});