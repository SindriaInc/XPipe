Ext.define('CMDBuildUI.view.administration.content.dashboards.card.chart.parameters.Parameter', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.dashboards.card.chart.parameters.ParameterController',
        'CMDBuildUI.view.administration.content.dashboards.card.chart.parameters.ParameterModel'
    ],

    controller: 'administration-content-dashboards-card-chart-parameters-parameter',
    viewModel: {
        type: 'administration-content-dashboards-card-chart-parameters-parameter'
    },
    alias: 'widget.administration-dashboards-parameters-parameter',
    config: {
        parameter: null
    },
    items: [{
        ui: 'administration-formpagination',
        xtype: 'fieldset',
        layout: 'column',
        bind: {
            title: '{fieldsetTitle}'
        },
        collapsible: false,
        items: []
    }],
    initComponent: function () {
        this.callParent(arguments);
        var view = this,
            vm = view.getViewModel(),
            theParameter = vm.get('theParameter'),
            fieldset = view.down('fieldset');

        vm.bind({
            bindTo: {
                theParameterType: '{theParameter.type}'
            }
        }, function (data) {
            fieldset.removeAll();
            if (data.theParameterType) {
                switch (theParameter.get('type').toLowerCase()) {
                    case 'long':
                    case 'integer':
                    case 'double':
                    case 'decimal':
                    case 'float4':
                        fieldset.add({
                            xtype: 'administration-dashboards-parameters-integer'
                        });
                        break;
                    case 'string':
                    case 'text':
                        fieldset.add({
                            xtype: 'administration-dashboards-parameters-string'
                        });
                        break;
                    case 'date':
                    case 'datetime':
                        fieldset.add({
                            xtype: 'administration-dashboards-parameters-date'
                        });
                        break;
                    case 'boolean':
                        fieldset.add({
                            xtype: 'administration-dashboards-parameters-boolean'
                        });
                        break;
                    default:
                        break;
                }
            }

        });
    }
});