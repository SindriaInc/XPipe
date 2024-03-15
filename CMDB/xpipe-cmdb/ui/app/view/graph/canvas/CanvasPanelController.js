Ext.define('CMDBuildUI.view.graph.canvas.CanvasPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.graph-canvas-canvaspanel',
    control: {
        '#': {
            afterrender: 'onAfterRenderPanel'
        }
    },
    onAfterRenderPanel: function (panel, eOpts) {
        var div = document.getElementById('cy');
        div.style.height = 'inherit';
    }

});
