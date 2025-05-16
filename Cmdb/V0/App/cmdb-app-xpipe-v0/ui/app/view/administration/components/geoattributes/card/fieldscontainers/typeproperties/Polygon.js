Ext.define('CMDBuildUI.view.administration.components.geoattributes.card.fieldscontainers.typeproperties.Polygon', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-geoattributes-typeproperties-polygon',
    layout: 'fit',
    config: {

    },
    viewModel: {

    },
    items: [
        CMDBuildUI.view.administration.components.geoattributes.card.FieldsHelper.getPolygonInputs()
    ]
});