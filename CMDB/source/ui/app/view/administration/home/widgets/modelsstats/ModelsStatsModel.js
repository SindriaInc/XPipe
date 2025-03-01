Ext.define('CMDBuildUI.view.administration.home.widgets.modelsstats.ModelsStatsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-home-widgets-modelsstats-modelsstats',
    data: {
        axes: [],
        series: [],
        data: []
    },
    formulas: {
        axes: function () {
            return [{
                type: 'numeric',
                position: 'bottom',
                fields: 'count',
                grid: true,
                majorTickSteps: 10,
                increment: 10,
                title: CMDBuildUI.locales.Locales.administration.home.count,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.home.count'
                }
            }, {
                type: 'category',
                position: 'left',
                fields: 'description',
                grid: true
            }];
        },
        series: function () {
            return [{
                type: 'bar',
                xField: 'description',
                yField: 'count',
                style: {
                    opacity: 0.80,
                    minGapWidth: 5
                },
                highlightCfg: {
                    opacity: 0.95
                },
                label: {
                    field: 'count',
                    display: 'insideEnd',
                    font: '12px'
                }
            }];
        },
        initData: function (get) {
            var me = this;
            me.set('showLoader', true);
            me.set('countLabel', CMDBuildUI.locales.Locales.administration.home.count);
            Ext.Ajax.request({
                url: CMDBuildUI.util.Config.baseUrl + '/functions/_cm3_dashboard_model_stats/outputs',
                method: "GET",
                timeout: 0
            }).then(function (response, opts) {
                if (!me.destroyed) {

                    var responseJson = Ext.JSON.decode(response.responseText, true);
                    var _data = [];
                    Ext.Array.forEach(responseJson.data || [], function (model) {
                        switch (model.type) {
                            case 'class':
                                model.index = 7;
                                model.description = CMDBuildUI.locales.Locales.administration.navigation.classes;
                                break;
                            case 'processclass':
                                model.index = 6;
                                model.description = CMDBuildUI.locales.Locales.administration.navigation.processes;
                                break;
                            case 'domain':
                                model.index = 5;
                                model.description = CMDBuildUI.locales.Locales.administration.navigation.domains;
                                break;
                            case 'view':
                                model.index = 4;
                                model.description = CMDBuildUI.locales.Locales.administration.navigation.views;
                                break;
                            case 'report':
                                model.index = 3;
                                model.description = CMDBuildUI.locales.Locales.administration.navigation.reports;
                                break;
                            case 'dashboard':
                                model.index = 2;
                                model.description = CMDBuildUI.locales.Locales.administration.navigation.dashboards;
                                break;
                            case 'custompage':
                                model.index = 1;
                                model.description = CMDBuildUI.locales.Locales.administration.navigation.custompages;
                                break;
                            case 'busdescriptor':
                                model.index = 0;
                                model.description = CMDBuildUI.locales.Locales.administration.navigation.busdescriptors;
                                break;
                            default:
                                break;
                        }
                        _data.push(model);
                    });

                    me.set('data', _data);
                    me.set('showLoader', false);
                }
            }, function () {
                if (!me.destroyed) {
                    me.set('showLoader', false);
                }
            });
        }
    },
    stores: {
        modelsStats: {
            proxy: 'memory',
            sorters: ['index'],
            fields: [{
                type: 'string',
                name: 'type'
            }, {
                type: 'integer',
                name: 'count'
            }, {
                type: 'string',
                name: 'description'
            }, {
                type: 'integer',
                name: 'index'
            }],
            data: '{data}'
        }
    }

});