
Ext.define('CMDBuildUI.view.administration.content.setup.elements.StatusGrid',{
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.StatusGridController',
        'CMDBuildUI.view.administration.content.setup.elements.StatusGridModel'
    ],
    alias: 'widget.administration-content-setup-elements-statusgrid',
    controller: 'administration-content-setup-elements-statusgrid',
    viewModel: {
        type: 'administration-content-setup-elements-statusgrid'
    },

    statics: {
        statusicons: {
            error: 'x-fa fa-times-circle',
            ready: 'x-fa fa-check-circle',
            disabled: 'x-fa fa-minus-circle',
            notrunning: 'cmdbuildicon-stop-circle'
        },
        statuscolors: {
            error: '#FF0000',
            ready: '#33cc00'
        }
    },

    forceFit: true,
    scrollable: true,
    reserveScrollbar: true,    
    sortable: false,    
    columns: []    
});
