
Ext.define('CMDBuildUI.view.graph.topMenu.TopMenu', {
    extend: 'Ext.toolbar.Toolbar',

    requires: [
        'CMDBuildUI.view.graph.topMenu.TopMenuController',
        'CMDBuildUI.view.graph.topMenu.TopMenuModel'
    ],

    controller: 'graph-topmenu-topmenu',
    viewModel: {
        type: 'graph-topmenu-topmenu'
    },
    alias: 'widget.graph-topmenu-topmenu',

    items: [{
        iconCls: 'x-fa fa-refresh',
        tooltip: CMDBuildUI.locales.Locales.relationGraph.refresh,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.relationGraph.refresh'
        },
        cls: 'management-tool',
        xtype: 'tool',
        itemId: 'refresh'
    }, {
        iconCls: 'cmdbuildicon-relgraph',
        tooltip: CMDBuildUI.locales.Locales.relationGraph.reopengraph,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.relationGraph.reopengraph'
        },
        cls: 'management-tool',
        xtype: 'tool',
        itemId: 'reopengraph'
    }, {
        iconCls: 'x-fa fa-folder-open folder-TopMenu',
        tooltip: CMDBuildUI.locales.Locales.relationGraph.choosenaviagationtree,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.relationGraph.choosenaviagationtree'
        },
        cls: 'management-tool',
        xtype: 'button',
        itemId: 'chooseNavTree',
        bind: {
            disabled: '{chooseNavTreeEnable}',
            menu: '{menu}'
        }
    }]
});
