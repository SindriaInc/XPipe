Ext.define('CMDBuildUI.view.administration.components.geoattributes.card.fieldscontainers.typeproperties.Point', {
    extend: 'Ext.panel.Panel',
    requires: [
        'CMDBuildUI.view.administration.components.geoattributes.card.FieldsHelper'
    ],
    alias: 'widget.administration-geoattributes-typeproperties-point',
    layout: 'fit',
    config: {

    },
    viewModel: {

    },
    items: CMDBuildUI.view.administration.components.geoattributes.card.FieldsHelper.getPointInputs()
});