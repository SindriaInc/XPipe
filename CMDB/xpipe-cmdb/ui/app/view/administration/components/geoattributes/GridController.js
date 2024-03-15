Ext.define('CMDBuildUI.view.administration.components.geoattributes.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-components-geoattributes-grid',
    listen: {
        global: {
            geoattributeupdated: 'onGeoAttributeUpdated'
        }
    },

    control: {

        '#': {
            deselect: 'onDeselect',
            select: 'onSelect',
            rowdblclick: 'onRowDblclick'
        },
        '#addattribute': {
            click: 'onAddClickBtn'
        }
    },
    /**
     * 
     * @param {CMDBuildUI.model.map.GeoAttribute} record 
     */
    onGeoAttributeUpdated: function (record) {
        var view = this.getView();
        var plugin = view.getPlugin('administration-forminrowwidget');
        if (plugin) {
            plugin.view.fireEventArgs('itemupdated', [view, record, this]);
        }
    },
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
    },

    onAddClickBtn: function (button, event, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();

        CMDBuildUI.model.map.GeoAttribute.setProxy({
            url: vm.get('geoattributesStoreProxy'),
            type: 'baseproxy'
        });
        container.add({
            xtype: 'administration-components-geoattributes-card-form',
            viewModel: {
                links: {
                    theGeoAttribute: {
                        type: 'CMDBuildUI.model.map.GeoAttribute',
                        create: {
                            owner_type: vm.get('objectTypeName'),
                            visibility: [vm.get('objectTypeName')]
                        }
                    }
                },

                data: {
                    actions: {
                        view: false,
                        edit: false,
                        add: true
                    },
                    grid: this.getView().up()
                }
            }
        });
    },

    onRowDblclick: function (row, record, element, rowIndex, e, eOpts) {

        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var vm = row.grid.getViewModel();
        var formInRow = row.ownerGrid.getPlugin('administration-forminrowwidget');
        formInRow.removeAllExpanded(record);
        row.setSelection(record);
        
        
        container.removeAll();


        CMDBuildUI.model.map.GeoAttribute.setProxy({
            url: vm.get('storedata.url'),
            type: 'baseproxy'
        });

        container.add({
            xtype: 'administration-components-geoattributes-card-form',
            viewModel: {
                links: {
                    theGeoAttribute: {
                        type: 'CMDBuildUI.model.map.GeoAttribute',
                        id: record.get('_id')
                    }
                },
                data: {
                    actions: {
                        view: false,
                        edit: true,
                        add: false
                    },
                    grid: row.grid
                }
            }
        });
    }
});