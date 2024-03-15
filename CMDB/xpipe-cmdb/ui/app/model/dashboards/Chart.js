Ext.define('CMDBuildUI.model.dashboards.Chart', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        chartOrientation: {
            horizontal: 'horizontal',
            vertical: 'vertical'
        },
        getChartOrientations: function () {
            return [{
                value: CMDBuildUI.model.dashboards.Chart.chartOrientation.horizontal,
                label: CMDBuildUI.locales.Locales.administration.dashboards.horizontal
            }, {
                value: CMDBuildUI.model.dashboards.Chart.chartOrientation.vertical,
                label: CMDBuildUI.locales.Locales.administration.dashboards.vertical
            }];
        },
        dataSourceTypes: {
            funktion: 'function',
            klass: 'class',
            view: 'view',
            text: 'text'
        },
        getDataSourceTypes: function () {
            return [{
                value: CMDBuildUI.model.dashboards.Chart.dataSourceTypes.funktion,
                label: CMDBuildUI.locales.Locales.administration.common.labels.funktion
            }, {
                value: CMDBuildUI.model.dashboards.Chart.dataSourceTypes.klass,
                label: CMDBuildUI.locales.Locales.administration.schedules.klass
            }, {
                value: CMDBuildUI.model.dashboards.Chart.dataSourceTypes.view,
                label: CMDBuildUI.locales.Locales.administration.localizations.view
            }];
        },
        charttypes: {
            bar: 'bar',
            gauge: 'gauge',
            line: 'line',
            pie: 'pie',
            table: 'table',
            text: 'text'
        },
        getChartTypes: function (type) {
            var types = [{
                label: CMDBuildUI.locales.Locales.administration.dashboards.bar,
                value: CMDBuildUI.model.dashboards.Chart.charttypes.bar,
                iconCls: 'fa fa-bar-chart'
            }, {
                label: CMDBuildUI.locales.Locales.administration.dashboards.pie,
                value: CMDBuildUI.model.dashboards.Chart.charttypes.pie,
                iconCls: 'fa fa-pie-chart'
            }, {
                label: CMDBuildUI.locales.Locales.administration.dashboards.gauge,
                value: CMDBuildUI.model.dashboards.Chart.charttypes.gauge,
                iconCls: 'fa fa-tachometer'
            }, {
                label: CMDBuildUI.locales.Locales.administration.dashboards.line,
                value: CMDBuildUI.model.dashboards.Chart.charttypes.line,
                iconCls: 'fa fa-line-chart'
            }, {
                label: CMDBuildUI.locales.Locales.administration.dashboards.table,
                value: CMDBuildUI.model.dashboards.Chart.charttypes.table,
                iconCls: 'fa fa-table'
            }, {
                label: CMDBuildUI.locales.Locales.administration.dashboards.text,
                value: CMDBuildUI.model.dashboards.Chart.charttypes.text,
                iconCls: 'fa fa-file-text-o'
            }];
            if (!type) {
                return types;
            }
            return Ext.Array.findBy(types, function (_type) {
                return _type.value === type;
            });
        }
    },

    fields: [{
        name: 'name',
        description: CMDBuildUI.locales.Locales.administration.common.labels.name,
        localized: {
            description: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
        },
        type: 'string',
        critical: true,
        showInGrid: true
    }, {
        name: 'description',
        description: CMDBuildUI.locales.Locales.administration.common.labels.description,
        localized: {
            description: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
        },
        type: 'string',
        critical: true,
        showInGrid: true
    }, {
        name: 'active',
        description: CMDBuildUI.locales.Locales.administration.common.labels.active,
        localized: {
            description: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
        },
        type: 'boolean',
        critical: true,
        defaultValue: true,
        showInGrid: true
    }, {
        name: 'dataSourceType', // used in table type
        type: 'string',
        critical: true,
        defaultValue: 'function' // function|class|process
    }, {
        name: 'dataSourceFilter', // used in table type
        type: 'string',
        critical: true,
        defaultValue: ''
    }, {
        name: 'dataSourceLimit', // used in table type
        type: 'number',
        critical: true,
        defaultValue: 0 // 0 == infinite
    }, {
        name: 'dataSourceName',
        type: 'string',
        critical: true
    }, {
        name: 'type',
        type: 'string', // enum charttypes
        critical: true
    }, {
        name: 'categoryAxisField',
        type: 'string',
        critical: true
    }, {
        name: 'categoryAxisLabel',
        type: 'string',
        critical: true
    }, {
        name: 'valueAxisLabel',
        type: 'string',
        critical: true
    }, {
        name: 'valueAxisFields',
        type: 'auto',
        critical: true,
        defaultValue: [] // string
    }, {
        name: 'chartOrientation',
        type: 'string',
        critical: true,
        defaultValue: 'horizontal'
    }, {
        name: 'autoLoad',
        type: 'boolean',
        critical: true
    }, {
        name: 'legend',
        type: 'boolean',
        critical: true
    }, {
        name: 'height',
        type: 'string',
        critical: true,
        defaultValue: null
    }, {
        name: 'maximum',
        type: 'number',
        critical: true
    }, {
        name: 'minimum',
        type: 'number',
        critical: true
    }, {
        name: 'steps',
        type: 'number',
        critical: true
    }, {
        name: 'dataSourceParameters',
        type: 'auto',
        critical: true
    }, {
        name: 'fgcolor',
        type: 'string',
        critical: true
    }, {
        name: 'bgcolor',
        type: 'string',
        critical: true
    }, {
        name: 'singleSeriesField',
        type: 'string',
        critical: true
    }, {
        name: 'labelField',
        type: 'string',
        critical: true
    }],

    hasMany: [{
        model: 'CMDBuildUI.model.dashboards.DataSourceParameter',
        name: 'dataSourceParameters',
        associationKey: 'dataSourceParameters'
    }],

    proxy: {
        type: 'memory'
    },

    /**
     * Get translated description
     * @param {Boolean} [force] default null (if true return always the translation even if exist,
     *  otherwise if viewContext is 'admin' return the original description)
     * @return {String} The translated description if exists. Otherwise the description.
     */
    getTranslatedDescription: function (force) {
        if (!force && CMDBuildUI.util.Ajax.getViewContext() === 'admin') {
            return this.get("description");
        }
        return this.get("_description_translation") || this.get("description");
    },

    /**
     * Get source attributes
     * 
     * @return {Ext.Deferred}
     */
    getSourceAttributes: function () {
        var deferred = new Ext.Deferred();
        // get source parameters
        var sourcetype = this.get("dataSourceType"),
            sourcename = this.get("dataSourceName");
        var sourceobj, isloadingfn = false;

        // if is view
        if (sourcetype === CMDBuildUI.model.dashboards.Chart.dataSourceTypes.view) {
            var v = CMDBuildUI.util.helper.ModelHelper.getViewFromName(sourcename);
            if (v.get("sourceClassName")) {
                sourceobj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(v.get("sourceClassName"));
            } else {
                sourcetype = CMDBuildUI.model.dashboards.Chart.dataSourceTypes.funktion;
                sourcename = v.get("sourceFunction");
            }
        }

        // get source obj for source type
        switch (sourcetype) {
            case CMDBuildUI.model.dashboards.Chart.dataSourceTypes.klass:
                sourceobj = CMDBuildUI.util.helper.ModelHelper.getClassFromName(sourcename);
                break;
            case CMDBuildUI.model.dashboards.Chart.dataSourceTypes.process:
                sourceobj = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(sourcename);
                break;
            case CMDBuildUI.model.dashboards.Chart.dataSourceTypes.funktion:
                var fnstore = Ext.getStore("Functions");
                sourceobj = fnstore.findRecord("name", sourcename);
                // load single function if not already loaded
                if (!sourceobj) {
                    isloadingfn = true;
                    fnstore.loadSingleFunction(sourcename).then(function (fn) {
                        fn.getAttributes().then(function (attributes) {
                            deferred.resolve(attributes);
                        });
                    }, function () {
                        deferred.reject();
                    });
                }
                break;
        }

        // if source object is not empty and is not loading the function
        // get source attributes
        if (sourceobj) {
            sourceobj.getAttributes().then(function (attributes) {
                deferred.resolve(attributes);
            });
        } else if (!isloadingfn) {
            deferred.reject();
        }
        return deferred.promise;
    },

    /**
     * @return {Object}
     */
    getSourceStoreParameters: function () {
        var sourcetype = this.get("dataSourceType"),
            sourcename = this.get("dataSourceName"),
            sourcefilter,
            proxyurl;

        if (sourcetype === CMDBuildUI.model.dashboards.Chart.dataSourceTypes.view) {
            var v = CMDBuildUI.util.helper.ModelHelper.getViewFromName(sourcename);
            if (v.get("sourceClassName")) {
                sourcename = v.get("sourceClassName");
                sourcetype = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(sourcename);
                sourcefilter = v.get("filter");
            } else {
                sourcename = v.get("sourceFunction");
                sourcetype = CMDBuildUI.model.dashboards.Chart.dataSourceTypes.funktion;
            }
        } else if (this.get("ecqlDataSourceFilter")) {
            sourcefilter = Ext.encode({
                ecql: {
                    id: this.get("ecqlDataSourceFilter").id,
                    context: {
                        server: {},
                        client: {}
                    }
                }
            });
        }
        switch (sourcetype) {
            case CMDBuildUI.model.dashboards.Chart.dataSourceTypes.klass:
                proxyurl = CMDBuildUI.util.api.Classes.getCardsUrl(sourcename);
                break;
            case CMDBuildUI.model.dashboards.Chart.dataSourceTypes.process:
                proxyurl = CMDBuildUI.util.api.Processes.getInstancesUrl(sourcename);
                break;
            case CMDBuildUI.model.dashboards.Chart.dataSourceTypes.funktion:
                proxyurl = CMDBuildUI.util.api.Functions.getOutputsUrlByFunctionName(sourcename);
                break;
        }
        return {
            proxyurl: proxyurl,
            filter: sourcefilter
        };
    },

    copyForClone: function () {
        var newRecord = this.clone();
        newRecord.set('_id', CMDBuildUI.util.Utilities.generateUUID());
        newRecord.set('name', Ext.String.format('{0}_{1}', newRecord.get('name'), 'copy'));
        newRecord.set('description', Ext.String.format('{0}_{1}', newRecord.get('description'), 'copy'));
        newRecord.set('dataSourceParameters', this.getAssociatedData().dataSourceParameters);
        newRecord._dataSourceParameters = this._dataSourceParameters;
        newRecord.crudState = "C";
        newRecord.phantom = true;
        newRecord.isClone = true;
        delete newRecord.crudStateWas;
        delete newRecord.previousValues;
        delete newRecord.modified;

        return newRecord;
    }
});