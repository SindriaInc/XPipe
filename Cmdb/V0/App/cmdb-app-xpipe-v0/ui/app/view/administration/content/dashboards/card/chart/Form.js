
Ext.define('CMDBuildUI.view.administration.content.dashboards.card.chart.Form', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-content-dashboards-card-chart-form',

    requires: [
        'CMDBuildUI.view.administration.content.dashboards.card.chart.FormController',
        'CMDBuildUI.view.administration.content.dashboards.card.chart.FormModel',
        'CMDBuildUI.view.administration.content.dashboards.card.chart.FormHelper'
    ],

    controller: 'administration-content-dashboards-card-chart-form',
    viewModel: {
        type: 'administration-content-dashboards-card-chart-form'
    },
    scrollable: 'y',
    modelValidation: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    items: [
        CMDBuildUI.view.administration.content.dashboards.card.chart.FormHelper.getGeneralPropertiesFieldset(),
        CMDBuildUI.view.administration.content.dashboards.card.chart.FormHelper.getDataSourcePropertiesFieldset(),
        CMDBuildUI.view.administration.content.dashboards.card.chart.FormHelper.getChartTypePropertiesFieldset()
    ],

    dockedItems: [{
        dock: 'top',
        xtype: 'container',
        bind: {

        },
        items: [{
            xtype: 'components-administration-toolbars-formtoolbar',
            hidden: true,
            bind: {
                hidden: '{hideToolbar}'
            },
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                edit: true,
                activeToggle: true,
                delete: true,
                clone: true
            }, 'dashboard-chart', 'theChart')
        }]
    }, {
        xtype: 'toolbar',
        itemId: 'bottomtoolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(true, { testid: 'dashboard-chart' }, { testid: 'dashboard-chart' })
    }]
});
