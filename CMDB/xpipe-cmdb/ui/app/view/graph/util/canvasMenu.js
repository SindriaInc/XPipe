Ext.define('CMDBuildUI.graph.util.canvasMenu', {
    singleton: true,
    _canvasMenu: null,

    _init: function () {
        this._canvasMenu = Ext.ComponentQuery.query('graph-canvas-bottommenu-canvasmenu')[0];
    },

    _reset: function () {
        this._canvasMenu = null;
    },

    getCanvasMenu: function () {
        return this._canvasMenu;
    },

    /**
     * @param {String} id not containing the '#' characther
     * @returns {Ext.component} The component in the topMenu with that id
     */
    getComponent: function (id) {
        var cmp;

        try {
            cmp = this.getCanvasMenu().down(id);
        } catch (err) {
            this._reset();
            this._init();

            cmp = this.getCanvasMenu().down(id);
        } finally {
            return cmp;
        }

    },

    /**
     * @param {String} component
     * @return {String} The id of the component
     */
    getIdComponent: function (component) {
        switch (component) {
            case 'tooltip':
                return '#enableTooltip';
            default:
                return 'null';
        }
    }
});