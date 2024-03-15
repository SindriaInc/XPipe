Ext.define('CMDBuildUI.view.administration.components.geoattributes.card.FieldsHelper', {
    requires: [
        'Ext.util.Format',
        'CMDBuildUI.util.helper.FieldsHelper'
    ],
    // extend: 'CMDBuildUI.util.administration.helper.FieldsHelper',
    singleton: true,

    getPointInputs: function (action) {

        var config = Ext.merge({}, this.fieldsConfig, {
            fillColor: {
                input: {},
                preview: {}
            },
            fillOpacity: {},
            pointRadius: {},
            pointColor: {
                input: {},
                preview: {}
            },
            icon: {
                input: {},
                preview: {}
            },
            strokeDashstyle: {},
            strokeColor: {},
            strokeOpacity: {},
            strokeWidth: {}

        });

        var container = {
            xtype: 'container',
            viewModel: {},
            bind: {
                hidden: '{!actions.' + action + '}'
            },
            items: Ext.Array.merge(
                [
                    this.getFillFields(config),
                    this.getPointRadiusAndIconFields(config)
                ],
                this.getStrokeFields(config)
            )
        };
        return container;
    },

    getLineInputs: function () {
        var config = Ext.merge({}, this.fieldsConfig, {
            strokeDashstyle: {},
            strokeColor: {},
            strokeOpacity: {},
            strokeWidth: {}

        });
        return {
            xtype: 'container',
            items: this.getStrokeFields(config)
        };
    },
    getPolygonInputs: function () {
        var config = Ext.merge({}, this.fieldsConfig, {
            fillColor: {},
            fillOpacity: {},
            strokeDashstyle: {},
            strokeColor: {},
            strokeOpacity: {},
            strokeWidth: {}

        });
        var fillFields = this.getFillFields(config);
        var strokeFields = this.getStrokeFields(config);

        var fields = Ext.Array.merge(
            [fillFields], strokeFields
        );
        return {
            xtype: 'container',
            items: fields
        };
    },

    privates: {
        fieldsConfig: {
            fillColor: {
                bind: {
                    value: '{theGeoAttribute.style.fillColor}'
                }
            },
            fillOpacity: {
                bind: {
                    value: '{theGeoAttribute.style.fillOpacity}'
                }
            },
            pointRadius: {
                bind: {
                    value: '{theGeoAttribute.style.pointRadius}'
                }
            },
            pointColor: {},
            icon: {
                input: {
                    bind: {
                        value: '{theGeoAttribute._icon}'
                    },
                    tpl: [
                        '<tpl for=".">',
                        '<div class="x-boundlist-item">',
                        '{iconelement} &nbsp;{description}',
                        '</div>',
                        '</tpl>'
                    ],
                    listeners: {
                        change: function (combo, newValue, oldValue) {
                            var iconpath;
                            if (newValue) {
                                iconpath = Ext.String.format('{0}/uploads/{1}/download', CMDBuildUI.util.Config.baseUrl, newValue);
                            } else {
                                iconpath = 'data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==';
                            }
                            this.up().down('image').setSrc(iconpath);
                        }
                    },
                    triggers: {
                        clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                    }
                },
                preview: {
                    bind: {
                        src: '{theGeoAttribute._iconPath}',
                        alt: '{theGeoAttribute.description}'
                    },
                    itemId: 'geoAttributeIconPreview',
                    viewModel: {
                        data: {
                            theGeoAttribute: null,
                            vmKey: 'theGeoAttribute'
                        }
                    }
                }
            },
            strokeDashstyle: {
                bind: {
                    value: '{theGeoAttribute.style.strokeDashstyle}',
                    store: '{strokeDashStyleStore}'
                }
            },
            strokeColor: {
                preview: {
                    bind: {
                        style: {
                            color: '{theGeoAttribute.style.strokeColor}'
                        }
                    }
                },
                input: {
                    bind: {
                        value: '{theGeoAttribute.style.strokeColor}'
                    }
                }
            },
            strokeOpacity: {
                bind: {
                    value: '{theGeoAttribute.style.strokeOpacity}'
                }
            },
            strokeWidth: {
                bind: {
                    value: '{theGeoAttribute.style.strokeWidth}'
                }
            }
        },
        /**
         * 
         * @param {String} action 
         */
        getFillFields: function (config) {
            return {
                layout: 'column',
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getFillOpacityInput(config),
                    CMDBuildUI.util.administration.helper.FieldsHelper.getFillColorInput(config)
                ]
            };
        },
        /**
         * 
         * @param {String} action 
         */
        getStrokeFields: function (config) {
            return [
                this.getStrokeStyleAndColorFields(config),
                this.getStrokeOpacityAndWidthFields(config)
            ];
        },
        /**
         * 
         * @param {String} action 
         */
        getStrokeOpacityAndWidthFields: function (config) {
            return {
                layout: 'column',
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getStrokeOpacityInput(config),
                    CMDBuildUI.util.administration.helper.FieldsHelper.getStrokeWidthInput(config)
                ]
            };
        },

        /**
         * 
         * @param {String} action 
         */
        getStrokeStyleAndColorFields: function (config) {
            return {
                layout: 'column',
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getStrokeDashStyleInput(config),
                    CMDBuildUI.util.administration.helper.FieldsHelper.getStrokeColorInput(config)
                ]
            };
        },
        /**
         * 
         * @param {String} action 
         */
        getPointRadiusAndIconFields: function (config) {
            return {
                layout: 'column',
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getPointRadiusInput(config),
                    CMDBuildUI.util.administration.helper.FieldsHelper.getIconComboInput(config)
                ]
            };
        },
        /**
         * 
         * @param {String} action 
         */
        getTypeField: function (config) {
            return {
                layout: 'column',
                columnWidth: 0.5,
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('type', {
                        type: {
                            columnWidth: 1,
                            fieldcontainer: {
                                allowBlank: false,
                                fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.type
                            },
                            bind: {
                                store: '{typesStore}',
                                value: '{theGeoAttribute.type}'
                            }
                        }
                    }, true)
                ]
            };
        }
    }
});