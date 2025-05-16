Ext.define('CMDBuildUI.view.administration.home.widgets.modelsstats.ModelsStats', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.home.widgets.modelsstats.ModelsStatsController',
        'CMDBuildUI.view.administration.home.widgets.modelsstats.ModelsStatsModel'
    ],

    controller: 'administration-home-widgets-modelsstats-modelsstats',
    viewModel: {
        type: 'administration-home-widgets-modelsstats-modelsstats'
    },
    alias: 'widget.administration-home-widgets-modelsstats-modelsstats',
    title: CMDBuildUI.locales.Locales.administration.home.modelstats,
    localized: {
        title: 'CMDBuildUI.locales.Locales.administration.home.modelstats'
    },
    tools: [{
        iconCls: 'x-fa fa-plus',
        itemId: 'addModelTool',
        tooltip: CMDBuildUI.locales.Locales.administration.common.actions.add,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.add'
        },
        hidden: true,
        bind: {
            hidden: '{!theSession.rolePrivileges.admin_all}'
        }
    }],

    ui: 'admindashboard',
    items: [],
    initComponent: function () {
        this.callParent(arguments);
        this.add({
            xtype: 'cartesian',
            reference: 'chart',
            width: '100%',
            height: 313,
            insetPadding: '50 20',
            flipXY: true,
            theme: 'admindashboard',
            animation: {
                easing: 'easeOut',
                duration: 500
            },
            bind: {
                store: '{modelsStats}'
            },
            axes: [{
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
            }],
            series: [{
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
            }]
        });
    }
});