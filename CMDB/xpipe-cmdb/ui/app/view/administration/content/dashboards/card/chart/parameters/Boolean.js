Ext.define('CMDBuildUI.view.administration.content.dashboards.card.chart.parameters.Boolean', {
    extend: 'Ext.panel.Panel',
    viewModel: {},
    alias: 'widget.administration-dashboards-parameters-boolean',
    layout: 'column',
    columnWidth: 1,
    items: [],
    initComponent: function () {
        var me = this,
            formHelper = CMDBuildUI.view.administration.content.dashboards.card.chart.FormHelper;
        me.callParent(arguments);
        me.add(formHelper.getRow([
            formHelper.getParameterDescriptionField(),
            formHelper.getParameterDefaultValueThreestateCheckbox()
        ]));
        me.add(formHelper.getRow([
            formHelper.getRequiredField()
        ]));        
    }
});