Ext.define('CMDBuildUI.view.administration.content.dashboards.card.chart.FormHelper', {

    singleton: true,

    getGeneralPropertiesFieldset: function () {
        var fieldset = {
            ui: 'administration-formpagination',
            xtype: "fieldset",
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
            },
            items: this.getGeneralPropertiesFields()
        };

        return fieldset;
    },

    getDataSourcePropertiesFieldset: function () {
        var fieldset = {
            ui: 'administration-formpagination',
            xtype: "fieldset",
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.dashboards.datasourceproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.dashboards.datasourceproperties'
            },
            layout: 'column',
            items: this.getDataSourcePropertiesFields(),
            hidden: true,
            bind: {
                hidden: '{theChart.type === "text"}'
            }
        };

        return fieldset;
    },

    getChartTypePropertiesFieldset: function () {
        var fieldset = {
            ui: 'administration-formpagination',
            xtype: "fieldset",
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.dashboards.charttypeproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.dashboards.charttypeproperties'
            },
            hidden: true,
            bind: {
                title: '{chartTypeFieldsetTitle}',
                hidden: '{theChart.type === "table"}'
            },
            items: this.getChartTypePropertiesFields()
        };

        return fieldset;
    },

    getIntegerFreeNumberfField: function () {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('defaultValue', {
            defaultValue: {
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.defaultvalue,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.defaultvalue'
                },
                step: 0,
                minValue: 0,
                hideTrigger: true,
                bind: {
                    value: '{theParameter.defaultValue}'
                }
            }
        });
    },
    getIntegerFreeFieldContainer: function () {
        var me = this;

        return me.getRow([{
            xtype: 'container',
            itemId: 'integerfreecontainer',
            columnWidth: 0.5,
            items: [me.getIntegerFreeNumberfField()]
        }], {
            hidden: true,
            bind: {
                hidden: '{hiddenfields.integerfree}'
            }
        });
    },
    getIntegerLookupFieldContainer: function () {
        var me = this;

        return me.getRow(me.getIntegerLookupTypeFields(), {
            itemId: 'integerlookupcontainer',
            hidden: true,
            bind: {
                hidden: '{hiddenfields.lookup}'
            }

        });
    },
    getIntegerCardFieldContainer: function () {
        var me = this;
        return me.getRow([
            me.getRow([
                me.getClassesCombo(),
                {
                    xtype: 'container',
                    columnWidth: 0.5,
                    itemId: 'carddefaultvaluecontainer',
                    items: []
                }

            ])
        ], {
            hidden: true,
            bind: {
                hidden: '{hiddenfields.card}'
            }
        });

    },
    getParameterDefaultValueThreestateCheckbox: function () {

        var me = this;
        return me.getRow([
            me.getRow([{
                xtype: 'threestatecheckboxfield',
                disableLabelClick: true,
                fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.defaultvalue,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.defaultvalue'
                },
                bind: {
                    value: '{theParameter.defaultValue}'
                }
            }])
        ]);
    },
    privates: {
        getGeneralPropertiesFields: function () {
            var items = [
                this.getRow([
                    // name
                    CMDBuildUI.util.administration.helper.FieldsHelper.getNameInput({
                        name: {
                            allowBlank: false,
                            bind: {
                                value: '{theChart.name}'
                            }
                        }
                    }, true, '[name="description"]'),
                    // description
                    CMDBuildUI.util.administration.helper.FieldsHelper.getDescriptionInput({
                        description: {
                            allowBlank: false,
                            bind: {
                                value: '{theChart.description}'
                            },
                            fieldcontainer: {
                                userCls: 'with-tool',
                                labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                                labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                                labelToolIconClick: 'onTranslateChartDescriptionClick'
                            }
                        }
                    })
                ]),
                this.getRow([
                    // height               
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('headerRow', {
                        headerRow: {
                            fieldcontainer: {
                                fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.height,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.height'
                                }
                            },
                            allowBlank: true,
                            minValue: 0,
                            bind: {
                                value: '{theChart.height}'
                            }
                        }
                    }),
                    // autoLoad                
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput('autoLoad', {
                        autoLoad: {
                            fieldcontainer: {
                                fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.autoload,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.autoload'
                                }
                            },
                            bind: {
                                disabled: '{actions.view}',
                                value: '{theChart.autoLoad}'
                            }
                        }
                    })
                ]),
                this.getRow([

                    // active            
                    CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput({
                        active: {
                            bind: {
                                disabled: '{actions.view}',
                                value: '{theChart.active}'
                            }
                        }
                    })
                ])
            ];

            return items;
        },

        getDataSourcePropertiesFields: function () {

            var items = [
                this.getRow([
                    this.getDataSourceTypeInput()
                ], {
                    hidden: true,
                    bind: {
                        hidden: '{theChart.type !== "table"}'
                    }
                }),

                this.getRow([
                    this.getAllClassesDataSourceInput()
                ], {
                    hidden: true,
                    bind: {
                        hidden: Ext.String.format('{theChart.dataSourceType !== "{0}"}', CMDBuildUI.model.dashboards.Chart.dataSourceTypes.klass)
                    },
                    listeners: {
                        hide: function (component, eOpts) {
                            var input = component.down('#dataSourceClassName_input');
                            input.setValue('');
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, true, input.up('form'));
                        },
                        show: function (component, eOpts) {
                            var input = component.down('#dataSourceClassName_input');
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, false, input.up('form'));
                        }
                    }
                }),

                this.getRow([{
                    xtype: 'fieldcontainer',
                    columnWidth: 0.5,
                    items: [this.getAllViewsDataSourceInput()]
                }], {
                    hidden: true,
                    bind: {
                        hidden: Ext.String.format('{theChart.dataSourceType !== "{0}"}', CMDBuildUI.model.dashboards.Chart.dataSourceTypes.view)
                    },
                    listeners: {
                        hide: function (component, eOpts) {
                            var input = component.down('#dataSourceViewName_input');
                            input.setValue('');
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, true, input.up('form'));
                        },
                        show: function (component, eOpts) {
                            var input = component.down('#dataSourceViewName_input');
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, false, input.up('form'));
                        }
                    }
                }),

                this.getRow([{
                    xtype: 'fieldcontainer',
                    columnWidth: 0.5,
                    items: [this.getDataSourceLimitInput()]
                }, {
                    xtype: 'fieldcontainer',
                    columnWidth: 0.5,
                    items: [this.getDataSourceFilterTextarea()]
                }], {
                    hidden: true,
                    bind: {
                        hidden: Ext.String.format('{theChart.dataSourceType !== "{0}"}', CMDBuildUI.model.dashboards.Chart.dataSourceTypes.klass)
                    }
                }),

                this.getRow([
                    this.getRow([
                        // if dataSourceType = FUNCTION and  dataSourceName, dataSourceFilter, dataSourceLimit are unused
                        CMDBuildUI.util.administration.helper.FieldsHelper.getFunctionsInput({
                            dataSourceFuncktionName: {
                                fieldcontainer: {
                                    allowBlank: true
                                },
                                forceSelection: true,
                                allowBlank: true,
                                displayField: 'description',
                                valueField: 'name',
                                bind: {
                                    value: '{theChart.dataSourceName}',
                                    store: '{functionsStore}'
                                }
                            }
                        }, 'dataSourceFuncktionName')
                    ]),
                    this.getRow([{
                        columnWidth: 1,
                        xtype: 'container',
                        itemId: 'functionparameterscontiner'
                    }])
                ], {
                    hidden: true,
                    bind: {
                        hidden: '{theChart.dataSourceType !== "function"}'
                    },
                    listeners: {
                        hide: function (component, eOpts) {
                            var input = component.down('#dataSourceFuncktionName_input');
                            input.setValue('');
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, true, input.up('form'));
                        },
                        show: function (component, eOpts) {
                            var input = component.down('#dataSourceFuncktionName_input');
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, false, input.up('form'));
                        }
                    }
                })

            ];
            // if type is TABLE, show dataSourceType, dataSourceName, dataSourceFilter, dataSourceLimit


            // *dynamic generation of function arguments fields

            // dataSourceName: "dashboard_tickets_for_service_desk_processes"
            // dataSourceParameters: [{…}]

            return items;
        },
        getChartTypePropertiesFields: function () {
            // type (hidden) BAR/PIE/GAUGE/LINE/TABLE/TEXT (preset from dropped chart)         

            // legend (boolean) show in BAR/PIE/LINE

            // chartOrientation (combo)(horizontal, vertical) show in BAR

            // categoryAxis (fieldset) show in BAR/LINE
            //// categoryAxisLabel (textfield)
            //// categoryAxisValue (combo) (dynamic by function response)

            // valueAxis (fieldset) show in BAR/LINE
            //// valueAxisLabel (textfield)
            //// valueAxisValue (combo) (dynamic multiselect by function response)
            /////////////////////////BAR & LINE DONE////////////////////////////

            // labelField: "Process" PIE
            // singleSeriesField: "Counter" PIE/GAUGE

            // maximum: 0 GAUGE
            // minimum: 0 GAUGE
            // steps: 0 GAUGE
            // fgcolor: #761238 GAUGE
            // bgcolor: #987131 GAUGE
            var items = [
                this.getRow([
                    this.getShowLegendField()

                ]),
                this.getRow([
                    this.getOrientationField()
                ]),
                this.getRow([
                    this.getMinimumField(),
                    this.getMaximumField()
                ]),
                this.getRow([
                    this.stepsField()
                ]),
                this.getRow([
                    this.getFgcolorField(),
                    this.getBgcolorField()
                ]),
                this.getCategoryAxisFiledset(),
                this.getValueAxisFiledset(),
                this.getRow([
                    this.getLabelField(),
                    this.singleSeriesField()
                ]),
                this.getRow([
                    this.getTextTextarea()
                ])
            ];

            return items;
        },

        getRow: function (items, config) {
            return CMDBuildUI.view.administration.content.dashboards.card.FormHelper.getRow(items, config);
        },

        getDataSourceTypeInput: function () {
            var fieldName = 'dataSourceType';
            var config = {};
            config[fieldName] = {

                fieldcontainer: {
                    layout: 'column',
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.sourcetype,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.sourcetype'
                    }
                },
                displayField: 'label',
                valueField: 'value',
                bind: {
                    value: '{theChart.dataSourceType}',
                    store: '{dataSourceTypeStore}'
                }
            };

            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(fieldName, config);
        },

        getAllClassesDataSourceInput: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getAllClassesInput({
                dataSourceClassName: {
                    fieldcontainer: {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.localizations['class'],
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.class'
                        }
                    },
                    withClasses: true,
                    withProcesses: true,
                    bind: {
                        value: '{theChart.dataSourceName}'
                    }
                }
            }, 'dataSourceClassName');
        },

        getAllViewsDataSourceInput: function () {
            var fieldName = 'dataSourceViewName';
            var config = {};
            config[fieldName] = {
                columnWidth: 1,
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.view,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.view'
                    }
                },
                displayField: 'description',
                valueField: 'name',

                bind: {
                    store: '{getAllViewsStore}',
                    value: '{theChart.dataSourceName}'
                }

            };

            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(fieldName, config);

        },
        getDataSourceLimitInput: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('dataSourceLimit', {
                dataSourceLimit: {
                    fieldcontainer: {
                        columnWidth: 1,
                        allowBlank: true,
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.rowlimit,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.rowlimit'
                        }
                    },
                    minValue: 0,
                    columnWidth: 0.5,
                    bind: {
                        value: '{theChart.dataSourceLimit}'
                    }
                }
            });
        },
        getDataSourceFilterTextarea: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextareaInput('dataSourceFilter', {
                dataSourceFilter: {
                    xtype: 'textarea',
                    itemId: 'filter',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.filter,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.filter'
                    },
                    resizable: {
                        handles: "s"
                    },
                    name: 'filter',
                    bind: {
                        readOnly: '{actions.view}',
                        value: '{theChart.dataSourceFilter}'
                    }
                }
            });
        },
        getClassesCombo: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getAllClassesInput({
                classToUseForReferenceWidget: {
                    fieldcontainer: {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.fromclass,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.fromclass'
                        }
                    },
                    withClasses: true,
                    withProcesses: true,
                    bind: {
                        value: '{theParameter.classToUseForReferenceWidget}'
                    },

                    listeners: {
                        change: function (combo, newValue, oldValue) {

                            var deafultValueContainer = combo.up('fieldset').down('#carddefaultvaluecontainer');
                            deafultValueContainer.removeAll();
                            if (newValue) {
                                deafultValueContainer.add({
                                    xtype: 'referencecombofield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.defaultvalue,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.defaultvalue'
                                    },
                                    displayField: 'Description',
                                    itemId: 'defaultValue_input',
                                    name: 'defaultValue',
                                    valueField: '_id',
                                    style: 'padding-right: 15px',
                                    metadata: {
                                        targetType: CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(newValue),
                                        targetClass: newValue
                                    },
                                    hidden: true,
                                    bind: {
                                        hidden: '{actions.view}',
                                        value: '{theParameter.defaultValue}'
                                    }
                                });
                            }
                        }
                    }
                }
            }, 'classToUseForReferenceWidget');
        },
        getTextTextarea: function () {
            return CMDBuildUI.util.helper.FieldsHelper.getHTMLEditor({
                columnWidth: 1,
                hidden: true,
                bind: {
                    value: '{theChart.text}',
                    hidden: '{theChart.type !== "text"}'
                }
            });
        },
        getParameterFilterTextarea: function (parameter) {
            return {
                columnWidth: 1,
                xtype: 'textarea',
                itemId: 'filter',
                fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.filter,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.filter'
                },
                resizable: {
                    handles: "s"
                },
                name: 'filter',
                hidden: true,
                bind: {
                    readOnly: '{actions.view}',
                    value: '{theParameter.filter.expression}',
                    hidden: '{hiddenfields.cqlfilter}'
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get('theParameter.filter.expression') !== newValue) {
                            vm.get('theParameter.filter').expression = newValue;
                        }
                    }
                }
            };
        },

        getIntegerLookupTypeFields: function () {
            var me = this;
            var comboFieldName = 'lookupType';
            var comboConfig = {};
            comboConfig[comboFieldName] = {
                columnWidth: 1,
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.lookuptype,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.lookuptype'
                    }
                },
                displayField: 'description',
                valueField: 'name',
                forceSelection: true,
                bind: {
                    value: '{theParameter.lookupType}',
                    store: '{integerLookupTypesStore}'
                },
                listeners: {
                    change: function (_combo, newValue, oldValue) {
                        if (oldValue !== newValue) {
                            var valueContainer = _combo.up('fieldset').down('#lookupValuecontainer');
                            valueContainer.removeAll();
                            valueContainer.add(me.getIntegerLookupValueFields(newValue));
                        }
                    }
                }

            };

            var combo = CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(comboFieldName, comboConfig);
            return [{
                xtype: 'container',
                itemId: 'lookupTypecontainer',
                columnWidth: 0.5,
                items: [combo]
            }, {
                xtype: 'container',
                itemId: 'lookupValuecontainer',
                columnWidth: 0.5,
                items: []

            }];
        },

        getPreselectIfUnique: function () {
            var preselectIfUniqueFieldName = 'preselectIfUnique';
            var preselectIfUniqueConfig = {};
            preselectIfUniqueConfig[preselectIfUniqueFieldName] = {
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.preselectifunique,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.preselectifunique'
                    },
                    hidden: true,
                    bind: {
                        hidden: '{theParameter.fieldType !== "card" && theParameter.fieldType !== "lookup"}'
                    }
                },
                bind: {
                    value: '{theParameter.preselectIfUnique}'
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput(preselectIfUniqueFieldName, preselectIfUniqueConfig);
        },
        getIntegerLookupValueFields: function (lookupType) {
            return {
                xtype: 'fieldcontainer',
                fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.defaultvalue,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.defaultvalue'
                },
                items: [{
                    xtype: 'lookupfield',
                    metadata: {
                        lookupType: lookupType
                    },
                    hidden: true,
                    bind: {
                        hidden: '{actions.view}',
                        value: '{theParameter.defaultValue}'
                    }
                }, {
                    xtype: 'displayfield',
                    bind: {
                        value: '{theParameter.__description}',
                        hidden: '{!actions.view}'
                    }
                }]
            };
        },

        getShowLegendField: function (theChart) {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput('legend', {
                legend: {
                    fieldcontainer: {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.showlegend,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.showlegend'
                        },
                        hidden: true,
                        bind: {
                            hidden: '{theChart.type === "text"}'
                        }
                    },

                    bind: {
                        disabled: '{actions.view}',
                        value: '{theChart.legend}'
                    }
                }
            });

        },
        singleSeriesField: function (theChart) {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('singleSeriesField', {
                singleSeriesField: {
                    fieldcontainer: {
                        hidden: true,
                        bind: {
                            hidden: '{hiddenTypeProperty.singleSeriesField}'
                        },
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.valuefield,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.valuefield'
                        }
                    },
                    displayField: 'name',
                    valueField: '_id',
                    bind: {

                        value: '{theChart.singleSeriesField}',
                        store: '{functionAttributesStoreNumeric}'
                    }
                }
            });
        },
        getLabelField: function (theChart) {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('labelField', {
                labelField: {
                    fieldcontainer: {
                        hidden: true,
                        bind: {
                            hidden: '{hiddenTypeProperty.labelField}'
                        },
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.labelfield,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.labelfield'
                        },
                        labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                        labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                        labelToolIconClick: 'onTranslateChartLabelFieldClick'
                    },

                    displayField: 'name',
                    valueField: '_id',
                    bind: {
                        value: '{theChart.labelField}',
                        store: '{functionAttributesStore}'
                    }
                }
            });
        },
        getMaximumField: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('maximum', {
                maximum: {
                    fieldcontainer: {
                        columnWidth: 0.5,
                        allowBlank: true,
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.maximum,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.maximum'
                        },
                        hidden: true,
                        bind: {
                            hidden: '{hiddenTypeProperty.maximum}'
                        }
                    },
                    columnWidth: 1,
                    bind: {
                        value: '{theChart.maximum}'
                    }

                }
            });
        },
        getMinimumField: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('minimum', {
                minimum: {
                    fieldcontainer: {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.minimum,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.minimum'
                        },
                        hidden: true,
                        bind: {
                            hidden: '{hiddenTypeProperty.maximum}'
                        },
                        columnWidth: 0.5
                    },
                    allowBlank: true,

                    bind: {
                        value: '{theChart.minimum}'
                    }

                }
            });
        },
        stepsField: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('steps', {
                steps: {
                    fieldcontainer: {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.steps,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.steps'
                        },
                        hidden: true,
                        bind: {
                            hidden: '{hiddenTypeProperty.steps}'
                        },
                        columnWidth: 1
                    },
                    columnWidth: 0.5,
                    allowBlank: true,

                    bind: {
                        value: '{theChart.steps}'
                    }

                }
            });
        },
        getFgcolorField: function () {
            return {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                hidden: true,
                bind: {
                    hidden: '{hiddenTypeProperty.fgcolor}'
                },
                fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.foregroundcolor,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.foregroundcolor'
                },
                items: [CMDBuildUI.util.helper.FieldsHelper.getColorpickerField({
                    alt: CMDBuildUI.locales.Locales.administration.dashboards.foregroundcolor,
                    localized: {
                        alt: 'CMDBuildUI.locales.Locales.administration.dashboards.foregroundcolor'
                    },
                    hidden: true,
                    bind: {
                        hidden: '{actions.view}',
                        value: '{theChart.fgcolor}'
                    }
                })]
            };
        },
        getBgcolorField: function () {
            return {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                hidden: true,
                bind: {
                    hidden: '{hiddenTypeProperty.bgcolor}'
                },
                fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.backgroundcolor,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.backgroundcolor'
                },
                items: [CMDBuildUI.util.helper.FieldsHelper.getColorpickerField({
                    alt: CMDBuildUI.locales.Locales.administration.dashboards.backgroundcolor,
                    localized: {
                        alt: 'CMDBuildUI.locales.Locales.administration.dashboards.backgroundcolor'
                    },
                    hidden: true,
                    bind: {
                        hidden: '{actions.view}',
                        value: '{theChart.bgcolor}'
                    }
                })]
            };
        },
        getCategoryAxisFiledset: function () {
            return {
                ui: 'administration-formpagination',
                xtype: "fieldset",
                collapsible: true,
                title: CMDBuildUI.locales.Locales.administration.dashboards.categoryaxis,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.dashboards.categoryaxis'
                },
                hidden: true,
                bind: {
                    hidden: '{hiddenTypeProperty.categoryAxisLabel || !theChart.dataSourceName}'
                },
                items: [this.getRow([
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput('categoryAxisLabel', {
                        categoryAxisLabel: {
                            fieldcontainer: {
                                bind: {
                                    hidden: '{hiddenTypeProperty.categoryAxisLabel}'
                                },
                                fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.title,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.title'
                                },
                                labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                                labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                                labelToolIconClick: 'onTranslateCategoryAxisTitleClick'
                            },

                            allowBlank: true,
                            bind: {
                                value: '{theChart.categoryAxisLabel}'
                            }
                        }
                    }),

                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('categoryAxisField', {
                        categoryAxisField: {
                            fieldcontainer: {
                                hidden: true,
                                bind: {
                                    hidden: '{hiddenTypeProperty.categoryAxisField}'
                                },
                                fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.valuefield,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.valuefield'
                                }
                            },

                            displayField: 'name',
                            valueField: '_id',
                            bind: {
                                value: '{theChart.categoryAxisField}',
                                store: '{functionAttributesStore}'
                            }
                        }
                    })
                ])]
            };
        },
        getValueAxisFiledset: function () {
            return {
                ui: 'administration-formpagination',
                xtype: "fieldset",
                collapsible: true,
                title: CMDBuildUI.locales.Locales.administration.dashboards.valueaxis,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.dashboards.valueaxis'
                },
                hidden: true,
                bind: {
                    hidden: '{hiddenTypeProperty.valueAxisLabel || !theChart.dataSourceName}'
                },
                items: [this.getRow([
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput('valueAxisLabel', {
                        valueAxisLabel: {
                            fieldcontainer: {
                                hidden: true,
                                bind: {
                                    hidden: '{hiddenTypeProperty.valueAxisLabel}'
                                },
                                fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.title,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.title'
                                },
                                labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                                labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                                labelToolIconClick: 'onTranslateValueAxisLabelClick'
                            },

                            allowBlank: true,
                            bind: {
                                value: '{theChart.valueAxisLabel}'
                            }
                        }
                    }),
                    {
                        xtype: 'fieldcontainer',
                        layout: 'column',
                        columnWidth: 0.5,
                        hidden: true,
                        bind: {
                            hidden: '{hiddenTypeProperty.valueAxisFields}'
                        },
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.valuefield,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.valuefield'
                        },
                        items: [{
                            xtype: 'multiselectfield',
                            hidden: true,
                            width: '100%',
                            columnWidth: 1,
                            itemId: 'valueAxisFields',
                            bind: {
                                hidden: '{actions.view}',
                                store: '{functionAttributesStore}'
                            },
                            displayField: 'name',
                            valueField: '_id',
                            listeners: {
                                change: function (input, newValue, oldValue) {
                                    var newSortedValue = [];
                                    input.getStore().each(function (storeItem) {
                                        if (newValue.indexOf(storeItem.get('name')) > -1) {
                                            newSortedValue.push(storeItem.get('name'));
                                        }
                                    });
                                    input.setValue(newSortedValue);
                                }
                            }
                        }]
                    }
                ])]
            };
        },
        getOrientationField: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('chartOrientation', {
                chartOrientation: {
                    fieldcontainer: {
                        hidden: true,
                        bind: {
                            hidden: '{hiddenTypeProperty.chartOrientation}'
                        },
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.chartorientation,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.chartorientation'
                        }
                    },
                    bind: {
                        value: '{theChart.chartOrientation}',
                        store: '{chartOrientationStore}'

                    }
                }
            });
        },

        getParameterDescriptionField: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput('description', {
                description: {
                    bind: {
                        value: '{theParameter.description}'
                    },
                    fieldcontainer: {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.labeldescription,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.labeldescription'
                        },
                        labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                        labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                        labelToolIconClick: 'onParameterNameTranslateClick'
                    }
                }
            });
        },
        getRequiredField: function (parameter) {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput('required', {
                required: {
                    fieldcontainer: {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.required,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.required'
                        }
                    },
                    bind: {
                        disabled: '{actions.view}',
                        value: '{theParameter.required}'
                    }
                }
            });
        },
        getIntegerFieldTypeField: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('fieldType', {
                fieldType: {
                    fieldcontainer: {}, // config for fieldcontainer
                    fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.fieldtype,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.fieldtype'
                    },
                    bind: {
                        store: '{integerFieldTypesStore}',
                        value: '{theParameter.fieldType}'
                    },
                    triggers: {
                        clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                    }
                }
            });
        },

        getStringFieldTypeField: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('fieldType', {
                fieldType: {
                    fieldcontainer: {}, // config for fieldcontainer
                    fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.fieldtype,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.fieldtype'
                    },
                    bind: {
                        store: '{stringFieldTypesStore}',
                        value: '{theParameter.fieldType}'
                    },
                    triggers: {
                        clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                    }
                }
            });
        },


        getAllClassesFieldContainer: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getAllClassesInput({
                defaultValue: {
                    fieldcontainer: {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.fromclass,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.fromclass'
                        }
                    },
                    withClasses: true,
                    withProcesses: true,
                    bind: {
                        value: '{theParameter.defaultValue}'
                    }
                }
            }, 'defaultValue');
        },
        getCqlFilterFieldContainer: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextareaInput('parameterFilter', {
                parameterFilter: {
                    xtype: 'textarea',
                    itemId: 'parameterFilter',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.filter,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.filter'
                    },
                    hidden: true,
                    name: 'parameterFilter',
                    bind: {
                        readOnly: '{actions.view}',
                        value: '{theParameter.cqlfilter}',
                        hidden: '{hiddenfields.cqlfilter}'
                    }
                }
            });
        },
        getStringFreeFieldContainer: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput('defaultValue', {
                defaultValue: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.defaultvalue,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.defaultvalue'
                    },
                    bind: {
                        value: '{theParameter.defaultValue}'
                    }
                }
            });
        }
    }



});