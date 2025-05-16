Ext.define('CMDBuildUI.view.map.tab.cards.geoattributesGrid.geoserverLayers.GeoserverLayers', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.map.tab.cards.geoattributesGrid.geoserverLayers.GeoserverLayersController'
    ],

    controller: 'map-tab-cards-geoattributesgrid-geoserverlayers',

    alias: 'widget.geoserverlayer',

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    bodyPadding: CMDBuildUI.util.helper.FormHelper.properties.padding,

    items: [{
        xtype: 'filefield',
        fieldLabel: CMDBuildUI.locales.Locales.attachments.file,
        allowBlank: false,
        localized: {
            fieldLabel: 'CMDBuildUI.locales.Locales.attachments.file'
        }
    }],

    buttons: [{
        text: CMDBuildUI.locales.Locales.common.actions.save,
        formBind: true, //only enabled once the form is valid
        disabled: true,
        itemId: 'savebtn',
        ui: 'management-primary',
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.save'
        }
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        itemId: 'cancelbtn',
        ui: 'secondary-action',
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        }
    }]

});