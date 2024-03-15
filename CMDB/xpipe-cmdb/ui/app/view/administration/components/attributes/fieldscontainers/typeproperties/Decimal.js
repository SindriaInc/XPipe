Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.Decimal', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-attribute-decimalfields',

    items: [{
        xtype: 'container',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'numberfield',
                itemId: 'precisionAttributeField',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.precision,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.precision'
                },
                name: 'precision',
                step: 1,
                autoEl: {
                    'data-testid': 'attribute-precisionDecimal_input'
                },
                bind: {
                    value: '{theAttribute.precision}',
                    disabled: '{theAttribute.inherited}'
                },
                validator: function (field) {
                    var form = this.up('form');
                    var precisionAttributeField = form.down('#scaleAttributeField');
                    if (typeof this.getValue !== 'undefined' && this.getValue() <= precisionAttributeField.getValue()) {
                        return CMDBuildUI.locales.Locales.administration.attributes.strings.precisionmustbebiggerthanscale;
                    }
                    return true;
                }
            }, {
                columnWidth: 0.5,
                xtype: 'numberfield',
                itemId: 'scaleAttributeField',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.scale,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.scale'
                },
                autoEl: {
                    'data-testid': 'attribute-scaleDecimal_input'
                },
                name: 'scale',
                step: 1,
                bind: {
                    value: "{theAttribute.scale}",
                    disabled: '{theAttribute.inherited}'
                },
                validator: function (field) {
                    var form = this.up('form');
                    var precisionAttributeField = form.down('#precisionAttributeField');
                    if (typeof this.getValue !== 'undefined' && this.getValue() >= precisionAttributeField.getValue()) {
                        return CMDBuildUI.locales.Locales.administration.attributes.strings.scalemustbesmallerthanprecision;
                    }
                    return true;
                }
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'textfield',
                itemId: 'unitOfMeasureField',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasure,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasure'
                },
                name: 'precision',
                maxLength: 10,
                bind: {
                    value: '{theAttribute.unitOfMeasure}'
                },
                autoEl: {
                    'data-testid': 'attribute-unitOfMeasureDecimal_input'
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var form = this.up('form');
                        var precisionAttributeLocationField = form.down('#unitOfMeasureLocationField');
                        if (!newValue) {
                            precisionAttributeLocationField.setValue(null);
                        }
                        precisionAttributeLocationField.validate();
                    }
                }
            }, {
                columnWidth: 0.5,
                xtype: 'combo',
                itemId: 'unitOfMeasureLocationField',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.positioningofum,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.positioningofum'
                },
                name: 'unitOfMeasureLocation',
                clearFilterOnBlur: false,
                anyMatch: true,
                autoSelect: true,
                forceSelection: true,
                typeAhead: true,
                queryMode: 'local',
                displayField: 'label',
                valueField: 'value',
                hidden: true,
                autoEl: {
                    'data-testid': 'attribute-positionOfUmDecimal_input'
                },
                bind: {
                    store: '{unitOfMeasuresStore}',
                    value: '{theAttribute.unitOfMeasureLocation}',
                    hidden: '{!theAttribute.unitOfMeasure}'
                },
                validator: function (field) {
                    var form = this.up('form');
                    var precisionAttributeField = form.down('#unitOfMeasureField');
                    if (precisionAttributeField.getValue() && !this.getValue()) {
                        return CMDBuildUI.locales.Locales.administration.attributes.strings.positioningofumrequired;
                    }
                    return true;
                }
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showseparator,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showseparator'
                },
                autoEl: {
                    'data-testid': 'attribute-showSeparatorDecimal_input'
                },
                itemId: 'attribute-showseparator',
                name: 'showSeparator',
                bind: {
                    value: '{theAttribute.showThousandsSeparator}'
                }
            }]
        }]
    }, {
        bind: {
            hidden: '{!actions.view}'
        },
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.precision,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.precision'
                },
                autoEl: {
                    'data-testid': 'attribute-precisionDecimal_display'
                },
                bind: {
                    value: '{theAttribute.precision}'
                }
            }, {
                columnWidth: 0.5,
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.scale,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.scale'
                },
                autoEl: {
                    'data-testid': 'attribute-scaleDecimal_display'
                },
                bind: {
                    value: "{theAttribute.scale}"
                }
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasure,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasure'
                },
                autoEl: {
                    'data-testid': 'attribute-unitOfMeasureDecimal_display'
                },
                bind: {
                    value: '{theAttribute.unitOfMeasure}'
                }
            }, {
                columnWidth: 0.5,
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.positioningofum,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.positioningofum'
                },
                hidden: true,
                bind: {
                    value: '{theAttribute.unitOfMeasureLocation}',
                    hidden: '{!theAttribute.unitOfMeasure}'
                },
                autoEl: {
                    'data-testid': 'attribute-positionOfUmDecimal_display'
                },
                renderer: function (value) {
                    if (value) {
                        var store = Ext.getStore('attributes.UnifOfMeasureLocations');
                        if (store) {
                            var record = store.findRecord('value', value);
                            return record && record.get('label');
                        }
                    }
                    return value;
                }
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showseparator,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showseparator'
                },
                itemId: 'attribute-showseparator',
                name: 'showSeparator',
                disabled: true,
                autoEl: {
                    'data-testid': 'attribute-showSeparatorDecimal_display'
                },
                bind: {
                    value: '{theAttribute.showThousandsSeparator}'
                }
            }]
        }]
    }]
});