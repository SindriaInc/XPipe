Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.domains.grids.ViewDomainsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-classes-tabitems-domains-grids-viewdomains',
    requires: ['CMDBuildUI.model.domains.Domain'],
    control: {
        '#': {
            // afterrender: 'onBeforeRender',
            deselect: 'onDeselect',
            select: 'onSelect',
            
            rowdblclick: 'onRowDblclick'
        }
    },

    // onBeforeRender: function (view) {
    //     var objectTypeName = view.getViewModel().get('objectTypeName');
    //     if(!Ext.isEmpty(objectTypeName)){
    //         var filter = Ext.JSON.encode({
    //             "attribute": {
    //                 "or": [{
    //                     "simple": {
    //                         "attribute": "source",
    //                         "operator": "contain",
    //                         "value": [objectTypeName]
    //                     }
    //                 }, {
    //                     "simple": {
    //                         "attribute": "destination",
    //                         "operator": "contain",
    //                         "value": [objectTypeName]
    //                     }
    //                 }]
    //             }
    //         });
    
    
    
    //         view.setStore(Ext.create('Ext.data.Store', {
    //             model: 'CMDBuildUI.model.domains.Domain',
    //             alias: 'store.classdomain-store',
    //             proxy: {
    //                 type: 'baseproxy',
    //                 url: '/domains',
    //                 extraParams: {
    //                     ext: true,
    //                     filter: filter
    //                 }
    //             },
    //             autoLoad: true,
    //             autoDestroy: true
    //         }).load());
    //     }

    // },
    /**
     * @param {Ext.selection.RowModel} row
     * @param {Ext.data.Model} record
     * @param {Number} index
     * @param {Object} eOpts
     */
    onDeselect: function (row, record, index, eOpts) {

    },

    /**
     * @param {Ext.selection.RowModel} row
     * @param {Ext.data.Model} record
     * @param {Number} index
     * @param {Object} eOpts
     */
    onSelect: function (row, record, index, eOpts) {
        
        this.view.setSelection(record);
        Ext.GlobalEvents.fireEventArgs('selecteddomain', [record]);
    },

    onRowDblclick: function (row, record, element, rowIndex, e, eOpts) {
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var vm = row.grid.getViewModel();

        var formInRow = row.ownerGrid.getPlugin('administration-forminrowwidget');
        formInRow.removeAllExpanded(record);
        row.setSelection(record);
        
        var view = this.getView();
        
        container.removeAll();
        var theDomain = vm.get('theDomain');
        container.add({
            xtype: 'administration-content-domains-tabitems-properties-properties',
            viewModel: {
                data: {
                    theDomain: theDomain,
                    title: Ext.String.format('{0} - {1}',
                        CMDBuildUI.locales.Locales.administration.localizations.domain,
                        theDomain.get('name')),
                    grid: view.config._rowContext.ownerGrid,
                    actions: {
                        view: false,
                        edit: true,
                        add: false
                    },
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                    objectTypeName: theDomain.get('name')
                }
            }
        });
    }
});