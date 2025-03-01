
Ext.define('CMDBuildUI.view.graph.canvas.bottomMenu.canvasMenu', {
    extend: 'Ext.toolbar.Toolbar',

    requires: [
        'CMDBuildUI.view.graph.canvas.bottomMenu.canvasMenuController'
    ],

    controller: 'graph-canvas-bottommenu-canvasmenu',
    alias: 'widget.graph-canvas-bottommenu-canvasmenu',

    items: [{
        xtype: 'tbtext',
        html: CMDBuildUI.locales.Locales.relationGraph.level,
        localized: {
            html: 'CMDBuildUI.locales.Locales.relationGraph.level'
        }
    }, {
        xtype: 'slider',
        id: 'sliderLevel',
        width: 200,
        increment: 1,
        minValue: 1,
        maxValue: 10,
        listeners: {
            change: function (slider, newValue, thumb, eOpts) {
                slider.lookupViewModel().set("pointerExternalCanvas", false);
            }
        }
    }, {
        xtype: 'tbtext',
        id: 'sliderValue'
    }, {
        xtype: 'tbseparator'
    }, {
        xtype: 'button',
        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('comment', 'solid') + ' enableTooltip',
        cls: 'management-tool',
        itemId: 'enableTooltip',
        enableToggle: true,
        bind: {
            disabled: '{enableAllTooltip.pressed}'
        }
    }, {
        xtype: 'tbseparator'
    }, {
        xtype: 'button',
        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('comments', 'solid'),
        cls: 'management-tool',
        itemId: 'enableAllTooltip',
        enableToggle: true
    }]
});