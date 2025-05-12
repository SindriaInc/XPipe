Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.domains.grids.EditDomainsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-classes-tabitems-domains-grids-editdomains',
    requires: ['CMDBuildUI.model.domains.Domain'],
    control: {
        '#': {
            // afterrender: 'onBeforeRender',
            deselect: 'onDeselect',
            select: 'onSelect'
            // TODO
            //rowdblclick: 'onRowDblclick'
        }
    },


    // onBeforeRender: function (view) {
    //     var objectTypeName = view.getViewModel().get('objectTypeName');
    //     if (!Ext.isEmpty(objectTypeName)) {
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
    }
});