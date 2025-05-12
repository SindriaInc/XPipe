Ext.define('CMDBuildUI.graph.util.topMenu', {
    singleton: true,
    _topMenu: null,

    _init: function () {
        this._topMenu = Ext.ComponentQuery.query('graph-topmenu-topmenu')[0];

        if (this._topMenu != null) {
            this._isInitialized = true;
        }
    },

    _reset: function () {
        this._topMenu = null;
    },

    getTopMenu: function () {
        return this._topMenu;
    },

    /**
     * @param {String} id not containing the '#' characther
     * @returns {Ext.component} The component in the topMenu with that id
     */
    getComponent: function (id) {
        var cmp;

        try {
            cmp = this.getTopMenu().down(id);
        } catch (err) {
            this._reset();
            this._init();

            cmp = this.getTopMenu().down(id);
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
            // case 'tooltip':
            //     return '#enableTooltip';
            default:
                return 'null';
        }
    }
});