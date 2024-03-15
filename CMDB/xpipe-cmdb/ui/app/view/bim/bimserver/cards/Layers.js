
Ext.define('CMDBuildUI.view.bim.bimserver.tab.cards.Layers', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.bim.bimserver.tab.cards.LayersController'
    ],

    alias: 'widget.bim-bimserver-tab-cards-layers',
    controller: 'bim-bimserver-tab-cards-layers',

    config: {
        hiddenTypes: {
            $value: null,
            evented: true
        }
    },

    //store is binded in parent component

    reference: 'bim-bimserver-tab-cards-layers',
    columns: [{
        text: CMDBuildUI.locales.Locales.bim.layers.name,
        localized: {
            text: 'CMDBuildUI.locales.Locales.bim.layers.name'
        },
        dataIndex: 'name',
        flex: 5,
        align: 'left'
    }, {
        text: CMDBuildUI.locales.Locales.bim.layers.quantity,
        localized: {
            text: 'CMDBuildUI.locales.Locales.bim.layers.quantity'
        },
        dataIndex: 'qt',
        flex: 1.75,
        align: 'center'

    }, {
        xtype: 'actioncolumn',
        itemId: 'gridActionColumn',
        flex: 1.25,
        dataIndex: 'clicks',
        align: 'center',
        menuDisabled: true,
        hideable: false,
        text: CMDBuildUI.locales.Locales.bim.layers.visibility,
        localized: {
            text: 'CMDBuildUI.locales.Locales.bim.layers.visibility'
        },
        getClass: function (v, meta, row, rowIndex, colIndex, store) {
            switch (row.get('clicks')) {
                case 0:
                    return 'x-fa fa-eye';
                case 1:
                case 2:
                    return 'x-fa fa-eye-slash';
            }
        }
    }],

    viewConfig: {
        markDirty: false
    },

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        items: [{
            xtype: 'tbfill'
        }, {
            xtype: 'tool',
            itemId: 'topMenuShowAll',
            iconCls: 'x-fa fa-eye',
            cls: 'management-tool',
            tooltip: CMDBuildUI.locales.Locales.bim.layers.menu.showAll,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.bim.layers.menu.showAll'
            }
        }, {
            xtype: 'tool',
            itemId: 'topMenuHideAll',
            iconCls: 'x-fa fa-eye-slash',
            cls: 'management-tool',
            tooltip: CMDBuildUI.locales.Locales.bim.layers.menu.hideAll,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.bim.layers.menu.hideAll'
            }
        }]
    }]
});
