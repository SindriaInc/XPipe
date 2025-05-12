Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.properties.fieldsets.DefaultOrdersFieldsetController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-processes-tabitems-properties-fieldsets-defaultordersfieldset',

    mixins: ['CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.SorterGridsMixin'],
    
    control: {        
        '#defaultOrderGrid': {
            edit: function (editor, context, eOpts) {                          
                context.record.set('direction', editor.editors.items[0].getValue());
            },
            beforeedit: function (editor, context, eOpts) {
                var vm = editor.view.lookupViewModel();
                if (vm.get('actions.view')) {
                    return false;
                }
            }
        }
    },
    
    /**
     * 
     * @param {*} view 
     * @param {*} rowIndex 
     * @param {*} colIndex 
     */
    onAddNewDefaultOrderBtn: function (view, rowIndex, colIndex) {
        var attribute = view.lookupReferenceHolder().lookupReference("defaultOrderAttribute");
        var direction = view.lookupReferenceHolder().lookupReference("defaultOrderDirection");
        var orderGrid = view.lookupReferenceHolder().lookupReference("defaultOrderGrid");
        var orderStore = orderGrid.getStore();
        var newRecordStore = view.getStore();
        var required = [CMDBuildUI.locales.Locales.administration.common.messages.thisfieldisrequired];
        if (!direction.getValue()) {
            direction.markInvalid(required);
        }

        if (!attribute.getValue()) {
            attribute.markInvalid(required);
        }
        if (attribute.getValue() && direction.getValue()) {
            Ext.suspendLayouts();
            orderStore.add(CMDBuildUI.model.AttributeOrder.create({
                attribute: attribute.getValue(),
                direction: direction.getValue()
            }));            
            newRecordStore.removeAll();
            newRecordStore.add(CMDBuildUI.model.AttributeOrder.create({direction: 'ascending'}));
            orderGrid.getView().refresh();
            Ext.resumeLayouts();
        }
    }

});