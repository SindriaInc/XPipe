
Ext.define('CMDBuildUI.view.graph.canvas.bottomMenu.canvasMenu', {
    extend: 'Ext.toolbar.Toolbar',
    requires: [
        'CMDBuildUI.view.graph.canvas.bottomMenu.canvasMenuController',
        'CMDBuildUI.view.graph.canvas.bottomMenu.canvasMenuModel'
    ],

    controller: 'graph-canvas-bottommenu-canvasmenu',
    viewModel: {
        type: 'graph-canvas-bottommenu-canvasmenu'
    },
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
                slider.up("graph-graphcontainer").getViewModel().set("pointerExternalCanvas", false);
            }
        }
    }, {
        xtype: 'tbtext',
        id: 'sliderValue'
    },
        '-', //same as {xtype: 'tbseparator'}
    {
        iconCls: 'x-fa fa-comment enableTooltip',
        cls: 'management-tool',
        xtype: 'button',
        itemId: 'enableTooltip',
        reference: 'enableTooltip',
        bind: {
            disabled: '{enableAllTooltip.pressed}'
        },
        enableToggle: true,
        listeners: {

            /**
             * 
             * @param {*} button 
             * @param {*} pressed 
             * @param {*} eOpts 
             */
            toggle: function (button, pressed, eOpts) {

                var tooltip = Ext.String.format('{0} {1}',
                    pressed ? CMDBuildUI.locales.Locales.relationGraph.disable : CMDBuildUI.locales.Locales.relationGraph.enable,
                    CMDBuildUI.locales.Locales.relationGraph.labelsOnGraph
                );

                button.setTooltip(tooltip);
            },

            /**
             * 
             * @param {*} button 
             * @param {*} eOpt 
             */
            beforerender: function (button, eOpt) {

                //sets initial toggle value
                var enabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.relgraph.node.tooltipEnabled) && !CMDBuildUI.graph.threejs.SceneUtils.allLabelsEnabled;
                button.toggle(enabled, true);

                //sets initial tooltip
                var tooltip = Ext.String.format('{0} {1}',
                    enabled ? CMDBuildUI.locales.Locales.relationGraph.disable : CMDBuildUI.locales.Locales.relationGraph.enable,
                    CMDBuildUI.locales.Locales.relationGraph.labelsOnGraph
                );

                button.setTooltip(tooltip);
            }
        }
    },
        '-', //same as {xtype: 'tbseparator'}
    {
        iconCls: 'x-fa fa-comments',
        cls: 'management-tool',
        xtype: 'button',
        itemId: 'enableAllTooltip',
        reference: 'enableAllTooltip',
        enableToggle: true,
        listeners: {

            /**
             * Change tooltip when toggling
             * @param {*} button 
             * @param {*} pressed 
             * @param {*} eOpts 
             */
            toggle: function (button, pressed, eOpts) {

                var tooltip = Ext.String.format('{0} {1}',
                    pressed ? CMDBuildUI.locales.Locales.relationGraph.disable : CMDBuildUI.locales.Locales.relationGraph.enable,
                    CMDBuildUI.locales.Locales.relationGraph.allLabelsOnGraph
                );

                button.setTooltip(tooltip);
            },

            /**
             * @param {*} button 
             * @param {*} eopts 
             */
            beforerender: function (button, eopts) {

                //sets the initial toggle state
                var enabled = CMDBuildUI.graph.threejs.SceneUtils.allLabelsEnabled;
                button.toggle(enabled, true);

                // Sets the initial tooltip
                var tooltip = Ext.String.format('{0} {1}',
                    enabled ? CMDBuildUI.locales.Locales.relationGraph.disable : CMDBuildUI.locales.Locales.relationGraph.enable,
                    CMDBuildUI.locales.Locales.relationGraph.allLabelsOnGraph
                );

                button.setTooltip(tooltip);
            }
        }
    }]
});
