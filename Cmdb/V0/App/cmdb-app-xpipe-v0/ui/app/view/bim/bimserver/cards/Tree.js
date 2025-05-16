
Ext.define('CMDBuildUI.view.bim.bimserver.tab.cards.Tree', {
    extend: 'Ext.tree.Panel',

    requires: [
        'CMDBuildUI.view.bim.bimserver.tab.cards.TreeController'
    ],

    alias: 'widget.bim-bimserver-tab-cards-tree',

    controller: 'bim-bimserver-tab-cards-tree',

    //the store is binded by it's parent
    root: {
        text: CMDBuildUI.locales.Locales.bim.tree.root,
        children: []
    },

    rootVisible: false,
    layout: 'fit',
    config: {
        selectedId: {
            $value: undefined,
            evented: true
        },
        hiddenNodes: {
            $value: null,
            evented: true
        }
    },

    hideHeaders: true,

    reference: 'bim-bimserver-tab-cards-tree',

    columns: [{
        xtype: 'treecolumn',
        dataIndex: 'text',
        flex: 1
    }, {
        xtype: 'actioncolumn',
        arrowTree: 'arrowTree',
        align: 'center',
        tooltip: CMDBuildUI.locales.Locales.bim.tree.arrowTooltip,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.bim.tree.arrowTooltip'
        },
        width: 30,
        getClass: function (v, meta, row, rowIndex, colIndex, store) {
            var leaf = row.get('leaf');

            if (leaf || row.get('ifcName') == 'IfcSpace') {
                return 'x-fa fa-arrow-right arrowTree';
            }
            return null;
        },
        handler: function (v, rowIndex, colIndex, item, e, record, row) {
            var object = record.get('object');
            if (object) {
                CMDBuildUI.util.bim.Viewer.select(object);
            }
        }

    }]

    /**
     * NOTE:
     * The tree is popolated in the controller. The function wich popolates the tree
     * is called after a global event is fired
     */
});
