Ext.define('CMDBuildUI.view.administration.content.dashboards.card.builder.Row', {
    extend: 'Ext.panel.Panel',
    requires: [
        'CMDBuildUI.view.administration.content.dashboards.card.builder.RowController',
        'CMDBuildUI.view.administration.content.dashboards.card.builder.RowModel'
    ],
    alias: 'widget.administration-content-dashboards-card-builder-row',
    controller: 'administration-content-dashboards-card-builder-row',
    viewModel: {
        type: 'administration-content-dashboards-card-builder-row'
    },
    config: {
        columns: []
    },

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    userCls: 'cmdbuild-fieldsmanagements-row',
    items: [],

    addColumn: function (column, colIndex, rowIndex) {
        return {
            xtype: 'administration-content-dashboards-card-builder-column',
            flex: column.width || 1,
            colIndex: colIndex,
            rowIndex: rowIndex,
            minWidth: 150,
            autoEl: {
                'data-testid': Ext.String.format('administration-dashboards-row-{0}-column-{1}', rowIndex, colIndex)
            },
            viewConfig: {
                getRowClass: function (record, rowIndex, rowParams, store) {
                    return '';
                },
                rowLines: false,
                overItemCls: 'null',
                headerBorders: false,
                header: false
            },
            store: Ext.create('Ext.data.Store', {
                fields: ['columns', 'width'],
                proxy: {
                    type: 'memory'
                },
                data: column && column.charts ? column.charts : [],
                autoDestroy: true,
                autoLoad: false
            }),
            listeners: {

                beforerender: function () {
                    var me = this;
                    Ext.asap(function () {
                        if (!me.destroyed) {

                            var charts = me.getStore().getRange();
                            var grid = me.up('components-grid-reorder-grid');
                            Ext.Array.forEach(charts, function (chart) {

                                var el = Ext.query('#ph_' + chart.data);
                                var charts = me.lookupViewModel().get('theDashboard').charts();
                                var _chart = charts.getById(chart.data);

                                if (el && _chart.get('name')) {
                                    // get chart from charts
                                    var chartHeight = _chart.get('height') ? _chart.get('height') : '400';

                                    var chartComp = Ext.create('CMDBuildUI.view.administration.content.dashboards.card.chart.PreviewContainer', {
                                        margin: '-5 0 5 0',
                                        userCls: 'cmdbuild-chartpreview',
                                        minHeight: 42,
                                        viewConfig: {
                                            navigationModel: {}
                                        },
                                        padding: 0,
                                        listeners: {
                                            afterrender: function () {
                                                me.up('components-grid-reorder-grid').getView().refresh();
                                            }
                                        },

                                        renderTo: el[0].id
                                    });
                                    var onChartCompExpandToggle = function () {
                                        me.up('fieldset').updateLayout();
                                    };

                                    Ext.asap(function (_chartComp, __chart) {
                                        if (!_chartComp.destroyed) {
                                            _chartComp.add({
                                                margin: '0 0 0 0',
                                                padding: 0,
                                                minHeight: parseInt(chartHeight) + 40,
                                                collapsed: true,
                                                xtype: "dashboards-chart",
                                                userCls: 'administration-chart-preview',
                                                viewConfig: {
                                                    navigationModel: {}
                                                },
                                                viewModel: {
                                                    data: {
                                                        chart: __chart
                                                    }
                                                },
                                                listeners: {
                                                    expand: onChartCompExpandToggle,
                                                    collapse: onChartCompExpandToggle,
                                                    showhidetable: function (isShow) {                                                        
                                                        this.setMinHeight(isShow ? parseInt(this.getHeight()) + 40 : parseInt(this.getHeight()) - 40);
                                                        me.up('fieldset').updateLayout({
                                                            isRoot: false
                                                        });
                                                    }
                                                }
                                            });
                                        }

                                    }, this, [chartComp, _chart]);

                                    me.on('destroy', function () {
                                        chartComp.destroy();
                                    }, me);

                                    var onViewResize = function () {
                                        chartComp.updateLayout();
                                    };
                                    me.on('resize', onViewResize, me);

                                    var minHeight = el[0].offsetHeight + el[0].offsetTop + 25;
                                    if (minHeight > me.up().getHeight()) {
                                        me.up('form').setHeight(minHeight);
                                    }

                                }
                            });
                        }
                    });
                }
            }
        };
    }

});