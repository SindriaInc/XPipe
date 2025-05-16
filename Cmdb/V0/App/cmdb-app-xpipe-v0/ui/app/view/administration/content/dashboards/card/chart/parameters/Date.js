Ext.define('CMDBuildUI.view.administration.content.dashboards.card.chart.parameters.Date', {
    extend: 'Ext.panel.Panel',
    viewModel: {},
    alias: 'widget.administration-dashboards-parameters-date',
    layout: 'column',
    columnWidth: 1,
    items: [],
    initComponent: function () {
        var me = this,
            formHelper = CMDBuildUI.view.administration.content.dashboards.card.chart.FormHelper;
        me.callParent(arguments);
        me.add(formHelper.getRow([
            formHelper.getParameterDescriptionField()
        ]));
        me.add(formHelper.getRow([
            formHelper.getRequiredField()
        ]));        
    }
});