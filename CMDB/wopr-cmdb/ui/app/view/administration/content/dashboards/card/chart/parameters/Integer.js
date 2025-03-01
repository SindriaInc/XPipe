Ext.define('CMDBuildUI.view.administration.content.dashboards.card.chart.parameters.Integer', {
    extend: 'Ext.panel.Panel',
    viewModel: {},
    alias: 'widget.administration-dashboards-parameters-integer',
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
        me.add(formHelper.getRow([
            formHelper.getIntegerFieldTypeField()
        ]));
        me.add(formHelper.getRow([
            formHelper.getIntegerCardFieldContainer()
        ]));
        me.add(formHelper.getRow([
            formHelper.getIntegerFreeFieldContainer()
        ]));
        me.add(formHelper.getRow([
            formHelper.getIntegerLookupFieldContainer()
        ]));
        me.add(formHelper.getRow([{
                xtype: 'container',
                itemId: 'filtercontainer',
                columnWidth: 1,
                items: [
                    formHelper.getParameterFilterTextarea()
                ]
            }

        ]));
        me.add(formHelper.getRow([
            formHelper.getPreselectIfUnique()
        ]));
    }
});