Ext.define('CMDBuildUI.view.dashboards.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.dashboards-container',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.dashboards.Container} view 
     * @param {Object} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        var vm = view.lookupViewModel();

        vm.bind('{object}', function (obj) {
            var layout = obj.get("layout");
            if (layout) {
                var items = [];
                var allcharts = obj.charts();
                // get rows
                var rows = layout.rows;
                if (!rows || !Ext.isArray(rows)) {
                    rows = [];
                }

                rows.forEach(function (row) {
                    var r = {
                        xtype: 'container',
                        layout: 'hbox',
                        items: []
                    };

                    // get row columns
                    var columns = row.columns;
                    if (!columns || !Ext.isArray(columns)) {
                        columns = [];
                    }
                    var base_width = 1 / columns.length;

                    columns.forEach(function (column) {
                        var c = {
                            xtype: 'container',
                            layout: {
                                type: 'vbox',
                                align: 'stretch'
                            },
                            flex: column.width || base_width,
                            items: []
                        };

                        var charts = column.charts;
                        if (!charts || !Ext.isArray(charts)) {
                            charts = [];
                        }

                        charts.forEach(function (chartid) {
                            var chart = allcharts.getById(chartid);
                            if (chart.get("active")) {
                                c.items.push({
                                    xtype: "dashboards-chart",
                                    viewModel: {
                                        data: {
                                            chart: chart
                                        }
                                    }
                                });
                            }
                        });

                        r.items.push(c);
                    });

                    items.push(r);
                });
                view.add(items);
            }
        });
    }

});
