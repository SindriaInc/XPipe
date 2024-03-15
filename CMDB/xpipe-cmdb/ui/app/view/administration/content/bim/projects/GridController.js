Ext.define('CMDBuildUI.view.administration.content.bim.projects.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-bim-projects-grid',

    control: {
        '#': {
            rowdblclick: 'onRowDblclick'
        }
    },

    onRowDblclick: function (row, record, element, rowIndex, e, eOpts) {
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);

        var formInRow = row.ownerGrid.getPlugin('administration-forminrowwidget');
        formInRow.removeAllExpanded(record);
        row.setSelection(record);

        
        container.removeAll();
        container.add({
            xtype: 'administration-content-bim-projects-card-viewedit',
            viewModel: {
                data: {
                    theProject: record,
                    actions: {
                        edit: true,
                        view: false,
                        add: false
                    }
                }
            }
        });
    }
});