Ext.define('CMDBuildUI.view.administration.components.geoattributes.card.fieldscontainers.SpecificProperties', {
    extend: 'Ext.form.Panel',
    requires: [
        'CMDBuildUI.view.administration.components.geoattributes.card.fieldscontainers.typeproperties.Point'
    ],
    alias: 'widget.administration-components-geoattributes-card-fieldscontainers-typeproperties',

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    viewModel: {},
    items: [{
            layout: 'column',
            items: [CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('subtype', {
                        subtype: {
                            fieldcontainer: {
                                fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.subtype, // the localized object for label of field
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.subtype'
                                }
                            }, // config for fieldcontainer
                            displayField: 'label',
                            valueField: 'value',
                            bind: {
                                store: '{subtypesStore}',
                                value: '{theGeoAttribute.subtype}'
                            }
                        }
                    },
                    true, // disabledOnEdit
                    false) // onlyCombo
            ]
        },
        {
            xtype: 'administration-geoattributes-typeproperties-line',
            bind: {
                hidden: '{!type.isLine}'
            }
        }, {
            xtype: 'administration-geoattributes-typeproperties-point',
            bind: {
                hidden: '{!type.isPoint}'
            }
        }, {
            xtype: 'administration-geoattributes-typeproperties-polygon',
            bind: {
                hidden: '{!type.isPolygon}'
            }
        }
    ]

});