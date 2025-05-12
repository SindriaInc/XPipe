Ext.define('CMDBuildUI.view.administration.home.widgets.systemstatus.NodeStatusController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-home-widgets-systemstatus-nodestatus',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#memoryLoadButton': {
            click: 'onClickMemoryLoadButton'
        },
        '#systemLoadButton': {
            click: 'onClickSystemLoadButton'
        },
        '#activeSessionsButton': {
            click: 'onClickActiveSessionsButton'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.home.widgets.systemstatus.NodeStatus} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = view.lookupViewModel();
        vm.set('nodeId', view.getItemId());

        vm.bind({
            proxy: '{proxyUrl}'
        }, function (data) {
            vm.get("systemStatus").load();
        });

        this.onClickMemoryLoadButton();
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onClickMemoryLoadButton: function (button, event, eOpts) {
        var config = {
            legend: {
                type: 'sprite',
                padding: 5,
                docked: 'top'
            },
            fields: ['system_memory_total', 'system_memory_used', 'java_memory_total'],
            renderer: 'onMemoryLabelRender',
            chartType: 'area',
            title: CMDBuildUI.locales.Locales.administration.home.java,
            yField: 'java_memory_total',
            otherSeries: [{
                type: 'area',
                xField: 'date',
                yField: 'system_memory_total',
                showInLegend: false,
                style: {
                    opacity: 0.40
                },
                colors: ["#A1AEB5"]
            }, {
                type: 'area',
                title: CMDBuildUI.locales.Locales.administration.home.system,
                xField: 'date',
                yField: 'system_memory_used',
                colors: ["#005CA9"],
                style: {
                    opacity: 0.80
                },
                tooltip: {
                    trackMouse: true,
                    renderer: 'onDataTooltipRender'
                }
            }]
        };

        this.getViewModel().set("typeChart", "memoryLoad");
        this.createChart(config);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onClickActiveSessionsButton: function (button, event, eOpts) {
        var config = {
            fields: 'active_session',
            chartType: 'line'
        };

        this.getViewModel().set("typeChart", "activeSessions");
        this.createChart(config);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onClickSystemLoadButton: function (button, event, eOpts) {
        var config = {
            fields: 'system_load',
            chartType: 'line'
        };

        this.getViewModel().set("typeChart", "systemLoad");
        this.createChart(config);
    },

    privates: {

        /**
         * Create chart
         * @param {Object} config
         */
        createChart: function (config) {
            var view = this.getView(),
                series = [{
                    type: config.chartType,
                    title: config.title,
                    xField: 'date',
                    yField: config.yField || config.fields,
                    colors: ["#6291a7"],
                    tooltip: {
                        trackMouse: true,
                        renderer: 'onDataTooltipRender'
                    }
                }],
                chart = {
                    xtype: 'cartesian',
                    width: '100%',
                    height: 300,
                    legend: config.legend,
                    bind: {
                        store: '{systemStatus}'
                    },
                    insetPadding: '10 20',
                    axes: [{
                        type: 'category',
                        fields: 'date',
                        position: 'bottom',
                        grid: true,
                        renderer: function (axis, label, layoutContext, lastLabel) {
                            var d = new Date(label);
                            return Ext.Date.format(d, 'G:i');
                        }
                    }, {
                        type: 'numeric',
                        fields: config.fields,
                        position: 'left',
                        grid: true,
                        minimum: 0,
                        renderer: config.renderer || 'onNumericAxisRender'
                    }],
                    series: series
                };

            if (config.otherSeries) {
                chart.series = Ext.Array.union(config.otherSeries, series);
            }

            view.down("#chart").removeAll();
            view.down("#chart").add(chart);
        },

        /**
         * Render the label for memory axis
         * @param {Ext.chart.axis.Axis} axis
         * @param {Number} label
         * @param {Object} layoutContext
         * @returns {String}
         */
        onMemoryLabelRender: function (axis, label, layoutContext) {
            return Number.isInteger(label) ? Ext.String.format("{0} GB", label / 1000) : "";
        },

        /**
        * Render the label for numeric axis
        * @param {Ext.chart.axis.Axis} axis 
        * @param {Number} label 
        * @param {Object} layoutContext 
        * @returns {String}
        */
        onNumericAxisRender: function (axis, label, layoutContext) {
            return Number.isInteger(label) ? label : "";
        },

        /**
         * Render data on tooltip
         * @param {Ext.tip.ToolTip} tooltip 
         * @param {Ext.data.Model} record 
         * @param {Object} item 
         */
        onDataTooltipRender: function (tooltip, record, item) {
            var value = record.get(item.field);
            if (['system_memory_total', 'system_memory_used', 'java_memory_total'].indexOf(item.field) > -1) {
                value = Ext.util.Format.fileSize(value * 1024 * 1024);
            }
            tooltip.setHtml(Ext.String.format("{0}: <b>{1}<b>", CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(record.get("timestamp")), value));
        }

    }

});