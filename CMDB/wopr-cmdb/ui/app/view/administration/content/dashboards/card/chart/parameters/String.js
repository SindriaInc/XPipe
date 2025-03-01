
Ext.define('CMDBuildUI.view.administration.content.dashboards.card.chart.parameters.String',{
    extend: 'Ext.panel.Panel', 
    viewModel: {},
    alias: 'widget.administration-dashboards-parameters-string',
    layout: 'column',
    columnWidth: 1,
    items: [],
    initComponent : function(){
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
            formHelper.getStringFieldTypeField()
        ]));                
        me.add(formHelper.getRow([
            formHelper.getAllClassesFieldContainer()           
        ], {
            bind: {
                hidden: '{hiddenfields.classes}'
            }
        }));
        me.add(formHelper.getRow([
            formHelper.getStringFreeFieldContainer()
        ], {
            bind: {
                hidden: '{hiddenfields.stringfree}'
            }
        }));

    }
});
