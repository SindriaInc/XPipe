Ext.define('CMDBuildUI.view.graph.canvas.bottomMenu.canvasMenuController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.graph-canvas-bottommenu-canvasmenu',
    listen: {
        component: {
            '#': {
                beforerender: 'onBeforeRender',
                destroy: 'onDestroy'
            },
            "#enableTooltip": {
                toggle: 'onEnableDisableTooltipToggle'
            },

            "#enableAllTooltip": {
                toggle: 'onEnableDisableAllTooltipToggle'
            }
        }
    },

    /**
     * @param {Ext.Component} toolbar
     * @param {Object} eOpts
     */
    onBeforeRender: function (toolbar, eOpts) {
        CMDBuildUI.graph.util.canvasMenu._init();
    },

    /**
     * @param {Ext.Component} toolbar
     * @param {Object} eOpts
     */
    onDestroy: function () {
        CMDBuildUI.graph.util.canvasMenu._reset();
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Boolean} pressed
     * @param {Object} eOpts
     */
    onEnableDisableTooltipToggle: function (button, pressed, eOpts) {
        CMDBuildUI.graph.threejs.SceneUtils.tooltip.setEnable(pressed);
    },

    /**
     * handles the toggle action
     * @param {*} check 
     * @param {*} newValue 
     * @param {*} oldValue 
     * @param {*} eOpts 
     */
    onEnableDisableAllTooltipToggle: function (button, pressed, eOptss) {
        CMDBuildUI.graph.threejs.SceneUtils.enableLabels(pressed);
    }
});