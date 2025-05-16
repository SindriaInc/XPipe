
Ext.define('CMDBuildUI.view.graph.canvas.CanvasPanel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.graph.canvas.CanvasPanelController',
        'CMDBuildUI.view.graph.canvas.CanvasPanelModel'
    ],

    controller: 'graph-canvas-canvaspanel',
    viewModel: {
        type: 'graph-canvas-canvaspanel'
    },
    alias: 'widget.graph-canvas-canvaspanel',


    html: '<div id="cy"></div>', // style="height: inherit; width: inherit;
    //Bottom Menu
    dockedItems: [{
        xtype: 'graph-canvas-bottommenu-canvasmenu',
        dock: 'bottom',
        reference: 'canvas-menu'
    }, {
        xtype: 'graph-topmenu-topmenu',
        dock: 'top',
        reference: 'tab-menu'
    }]
});
