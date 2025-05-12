Ext.define('CMDBuildUI.view.dashboards.ChartModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.dashboards-chart',

    data: {
        title: null,
        chart: null,
        paramsnames: {},
        toolsdisabled: false,
        storedata: {
            autoload: false,
            pagesize: 0,
            extraparams: {}
        },
        openinpopupbtn: {
            hidden: false
        },
        showhideparamsbtn: {
            hidden: true
        },
        showhidetablebtn: {
            hidden: true
        },
        refreshbtn: {
            hidden: true
        },
        downloadbtn: {
            hidden: true
        },
        form: {
            hidden: false
        },
        grid: {
            hidden: true
        }
    },

    formulas: {
        updateData: {
            bind: {
                chart: '{chart}'
            },
            get: function (data) {
                if (data.chart) {
                    var charttype = data.chart.get("type"),
                        isText = charttype === CMDBuildUI.model.dashboards.Chart.charttypes.text,
                        isTable = charttype === CMDBuildUI.model.dashboards.Chart.charttypes.table;
                    this.set("title", data.chart.get("_description_translation") || data.chart.get("description"));
                    if (!isText) {
                        var sourcetype = data.chart.get("dataSourceType");
                        var storeinfo = data.chart.getSourceStoreParameters();
                        var config = {
                            type: 'store',
                            proxyurl: storeinfo.proxyurl,
                            autoload: data.chart.get("autoLoad"),
                            filter: storeinfo.filter
                        };
                        if (sourcetype !== CMDBuildUI.model.dashboards.Chart.dataSourceTypes.funktion) {
                            if (data.chart.get("dataSourceLimit") == 0) {
                                config.type = 'buffered';
                                config.pagesize = 50;
                            } else {
                                config.pagesize = data.chart.get("dataSourceLimit");
                            }
                        } else {
                            // TODO: check if parameter is mandatory
                            data.chart.dataSourceParameters().each(function (item) {
                                if (item.get("required") && Ext.isEmpty(item.get('defaultValue'))) {
                                    config.autoload = false;
                                }
                            });
                        }
                        this.set("storedata", config);

                        if (sourcetype !== CMDBuildUI.model.dashboards.Chart.dataSourceTypes.funktion) {
                            this.set("parameters", {});
                        }
                    }

                    // update btns visibility
                    var showInPopup = this.getView().getShowInPopup()
                    this.set("openinpopupbtn.hidden", showInPopup);
                    this.set("downloadbtn.hidden", Ext.isIE8 || !showInPopup || isText || isTable);
                }
            }
        },
        updateExtraParams: {
            bind: {
                bindTo: '{parameters}',
                deep: true
            },
            get: function (params) {
                var extraparams = {};
                var paramsnames = this.get("paramsnames");
                for (var key in paramsnames) {
                    var paramname = paramsnames[key];
                    extraparams[paramname] = params.get(key);
                }
                this.set("storedata.extraparams", {
                    filter: this.get("storedata.filter"),
                    parameters: Ext.JSON.encode(extraparams)
                });
            }
        },
        applyFilterManager: {
            bind: {
                bindTo: {
                    records: '{records}',
                    filter: '{storedata.filter}'
                }
            },
            get: function (data) {
                data.records.getAdvancedFilter().addCustomFilter(data.filter);
            }
        },

        paramsToolTip: {
            bind: '{form.hidden}',
            get: function (hidden) {
                return hidden ?
                    CMDBuildUI.locales.Locales.dashboards.tools.parametersshow :
                    CMDBuildUI.locales.Locales.dashboards.tools.parametershide;
            }
        },

        tableToolTip: {
            bind: '{grid.hidden}',
            get: function (hidden) {
                return hidden ?
                    CMDBuildUI.locales.Locales.dashboards.tools.gridshow :
                    CMDBuildUI.locales.Locales.dashboards.tools.gridhide;
            }
        },

        adminCls: {
            bind: '{isAdministrationModule}',
            get: function (isAdministrationModule) {
                return isAdministrationModule ? 'administration-tool' : Ext.baseCSSPrefix + 'actiontool';
            }
        }
    },

    stores: {
        records: {
            type: '{storedata.type}',
            proxy: {
                type: 'baseproxy',
                url: '{storedata.proxyurl}',
                extraParams: '{storedata.extraparams}'
            },
            pageSize: '{storedata.pagesize}',
            autoLoad: '{storedata.autoload}',
            autoDestroy: true,
            listeners: {
                beforeload: 'onStoreBeforeLoad',
                load: 'onStoreLoad'
            }
        }
    }

});