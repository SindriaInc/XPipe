Ext.define('CMDBuildUI.view.graph.canvas.topMenu.TopMenu', {
    extend: 'Ext.toolbar.Toolbar',

    requires: [
        'CMDBuildUI.view.graph.canvas.topMenu.TopMenuController',
        'CMDBuildUI.view.graph.canvas.topMenu.TopMenuModel'
    ],

    controller: 'graph-canvas-topmenu-topmenu',
    viewModel: {
        type: 'graph-canvas-topmenu-topmenu'
    },

    alias: 'widget.graph-canvas-topmenu-topmenu',

    items: [{
        xtype: 'tool',
        itemId: 'refresh',
        cls: 'management-tool',
        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sync-alt', 'solid'),
        tooltip: CMDBuildUI.locales.Locales.relationGraph.refresh,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.relationGraph.refresh'
        }
    }, {
        xtype: 'tool',
        itemId: 'reopengraph',
        cls: 'management-tool cmdbuildicon-fix', // needed for fix custom icon overflow
        iconCls: 'cmdbuildicon-relgraph',
        style: 'margin-right: 10px;',
        tooltip: CMDBuildUI.locales.Locales.relationGraph.reopengraph,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.relationGraph.reopengraph'
        }
    }, {
        xtype: 'button',
        itemId: 'chooseNavTree',
        cls: 'management-tool',
        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('folder-open', 'solid') + ' folder-TopMenu',
        tooltip: CMDBuildUI.locales.Locales.relationGraph.choosenaviagationtree,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.relationGraph.choosenaviagationtree'
        },
        bind: {
            disabled: '{chooseNavTreeEnable}',
            menu: '{menu}'
        }
    }]
});