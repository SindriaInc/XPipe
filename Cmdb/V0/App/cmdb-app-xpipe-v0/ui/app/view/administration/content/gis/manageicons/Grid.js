Ext.define('CMDBuildUI.view.administration.content.gis.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.gis.GridController',
        // plugins
        'Ext.grid.filters.Filters'
    ],

    alias: 'widget.administration-content-gis-grid',
    controller: 'administration-content-gis-grid',
    viewModel: {},

    forceFit: true,
    columns: [{
            text: CMDBuildUI.locales.Locales.administration.gis.icon,
            dataIndex: 'iconelement',
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.gis.icon'
            },
            align: 'left'
        },
        {
            text: CMDBuildUI.locales.Locales.administration.gis.description,
            dataIndex: 'description',
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.gis.description'
            },
            align: 'left'
        },
        {
            xtype: 'actioncolumn',
            minWidth: 104, // width property not works. Use minWidth.
            items: [{
                iconCls: 'attachments-grid-action x-fa fa-pencil',
                getTip: function () {
                    return CMDBuildUI.locales.Locales.administration.gis.editicon;
                },
                handler: function (grid, rowIndex, colIndex) {
                    var record = grid.getStore().getAt(rowIndex);
                    grid.fireEvent("actionedit", grid, record, rowIndex, colIndex);
                }
            }, {
                iconCls: 'attachments-grid-action x-fa fa-trash',
                getTip: function () {
                    return CMDBuildUI.locales.Locales.administration.gis.deleteicon;
                },
                handler: function (grid, rowIndex, colIndex) {
                    var record = grid.getStore().getAt(rowIndex);
                    grid.fireEvent("actiondelete", grid, record, rowIndex, colIndex);
                }
            }]
        }
    ],
    bind: {
        store: '{icons}'
    }
});