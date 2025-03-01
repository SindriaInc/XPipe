Ext.define('CMDBuildUI.view.administration.content.dashboards.card.chart.FormModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-dashboards-card-chart-form',
    data: {
        action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
        hiddenTypeProperty: {
            legend: true,
            labelField: true,
            singleSeriesField: true,
            maximum: true,
            minimum: true,
            steps: true,
            fgcolor: true,
            bgcolor: true,
            chartOrientation: true,
            valueAxisFields: true,
            valueAxisLabel: true,
            categoryAxisLabel: true,
            categoryAxisField: true
        },
        hideToolbar: false
    },

    formulas: {
        chartTypeFieldsetTitle: {
            bind: '{theChart._type_description}',
            get: function (typeDescription) {
                return Ext.String.format(CMDBuildUI.locales.Locales.administration.dashboards.chartnamecharttypeproperties, typeDescription)
            }
        },
        chartTypeManager: {
            bind: {
                type: '{theChart.type}'
            },
            get: function (data) {
                var hiddenTypeProperty;
                switch (data.type) {
                    case 'pie':
                        hiddenTypeProperty = {
                            legend: false,
                            labelField: false,
                            singleSeriesField: false,
                            maximum: true,
                            minimum: true,
                            steps: true,
                            fgcolor: true,
                            bgcolor: true,
                            chartOrientation: true,
                            valueAxisFields: true,
                            valueAxisLabel: true,
                            categoryAxisLabel: true,
                            categoryAxisField: true
                        };
                        break;
                    case 'gauge':
                        hiddenTypeProperty = {
                            legend: false,
                            labelField: true,
                            singleSeriesField: false,
                            maximum: false,
                            minimum: false,
                            steps: false,
                            fgcolor: false,
                            bgcolor: false,
                            chartOrientation: true,
                            valueAxisFields: true,
                            valueAxisLabel: true,
                            categoryAxisLabel: true,
                            categoryAxisField: true
                        };
                        break;
                    case 'bar':
                        hiddenTypeProperty = {
                            legend: false,
                            labelField: true,
                            singleSeriesField: true,
                            maximum: true,
                            minimum: true,
                            steps: true,
                            fgcolor: true,
                            bgcolor: true,
                            chartOrientation: false,
                            valueAxisFields: false,
                            valueAxisLabel: false,
                            categoryAxisLabel: false,
                            categoryAxisField: false
                        };
                        break;
                    case 'line':
                        hiddenTypeProperty = {
                            legend: false,
                            labelField: true,
                            singleSeriesField: true,
                            maximum: true,
                            minimum: true,
                            steps: true,
                            fgcolor: true,
                            bgcolor: true,
                            chartOrientation: true,
                            valueAxisFields: false,
                            valueAxisLabel: false,
                            categoryAxisLabel: false,
                            categoryAxisField: false
                        };
                        break;
                    case 'table':
                        hiddenTypeProperty = {
                            legend: true,
                            labelField: true,
                            singleSeriesField: true,
                            maximum: true,
                            minimum: true,
                            steps: true,
                            fgcolor: true,
                            bgcolor: true,
                            chartOrientation: true,
                            valueAxisFields: true,
                            valueAxisLabel: true,
                            categoryAxisLabel: true,
                            categoryAxisField: true
                        };
                        break;
                    case 'text':
                        hiddenTypeProperty = {
                            legend: true,
                            labelField: true,
                            singleSeriesField: true,
                            maximum: true,
                            minimum: true,
                            steps: true,
                            fgcolor: true,
                            bgcolor: true,
                            chartOrientation: true,
                            valueAxisFields: true,
                            valueAxisLabel: true,
                            categoryAxisLabel: true,
                            categoryAxisField: true
                        };
                        break;
                    default:
                        break;

                }
                this.set('hiddenTypeProperty', hiddenTypeProperty);
            }
        },
        titleManager: {
            bind: {
                description: '{theChart.description}',
                action: '{action}'
            },
            get: function (data) {
                var title,
                    _type_description = CMDBuildUI.model.dashboards.Chart.getChartTypes(this.get('theChart.type')).label,
                    description = data.action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add ? CMDBuildUI.locales.Locales.administration.dashboards.new : data.description;
                title = Ext.String.format(
                    '{0}{1}{2}',
                    _type_description,
                    ' - ',
                    description);
                this.set('theChart._type_description', _type_description);
                this.getParent().set('title', title);
            }
        },

        actions: {
            bind: '{action}',
            get: function (action) {
                return {
                    add: action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                    edit: action === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                    view: action === CMDBuildUI.util.administration.helper.FormHelper.formActions.view
                };
            }
        },
        getAllViews: function (get) {
            return Ext.getStore('views.Views').getRange();
        },
        getAllLookupTypesManager: {
            get: function (data) {
                var store = Ext.getStore('lookups.LookupTypes');
                if (store.loaded) {
                    return store.getRange();
                }
            }
        },
        integerFieldTypes: function () {
            return CMDBuildUI.model.dashboards.DataSourceParameter.getIntegerFieldTypes();
        },
        stringFieldTypes: function () {
            return CMDBuildUI.model.dashboards.DataSourceParameter.getStringFieldTypes();
        },
        dataSourceTypes: function () {
            return CMDBuildUI.model.dashboards.Chart.getDataSourceTypes();
        },
        chartOrientations: function(){
            return CMDBuildUI.model.dashboards.Chart.getChartOrientations();
        }
    },
    stores: {
        dataSourceTypeStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{dataSourceTypes}',
            pageSize: 0, // disable pagination
            proxy: {
                type: 'memory'
            }
            // autoDestroy: true
        },
        getAllViewsStore: {
            model: 'CMDBuildUI.model.views.View',
            data: '{getAllViews}',
            pageSize: 0, // disable pagination
            proxy: {
                type: 'memory'
            },
            filters: [function (item) {
                return [CMDBuildUI.model.views.View.types.sql, CMDBuildUI.model.views.View.types.filter].indexOf(item.get('type')) > -1;
            }]
            // autoDestroy: true
        },
        functionsStore: {
            model: 'CMDBuildUI.model.Function',
            sorters: ['description'],
            pageSize: 0, // disable pagination
            autoLoad: true,
            remoteFilter: false
            // autoDestroy: true
        },

        integerFieldTypesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{integerFieldTypes}',
            autoLoad: true,
            proxy: {
                type: 'memory'
            }
        },
        stringFieldTypesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{stringFieldTypes}',
            //  autoDestroy: true, // if true the ui will break
            autoLoad: true,
            proxy: {
                type: 'memory'
            }
        },

        integerLookupTypesStore: {
            source: 'lookups.LookupTypes'
        },

        functionAttributesStore: {
            model: 'CMDBuildUI.model.Attribute',
            data: '{getAllFunctionAttributes}'
        },
        functionAttributesStoreNumeric: {
            source: '{functionAttributesStore}',
            filters: [function (item) {
                return item.get('type') === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer ||
                    item.get('type') === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint ||
                    item.get('type') === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double ||
                    item.get('type') === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal;
            }]
        },

        chartOrientationStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{chartOrientations}',
            // autoDestroy: true, // if true the ui will break
            autoLoad: true,
            proxy: {
                type: 'memory'
            }
        }

    }

});