Ext.define('CMDBuildUI.util.bimsurfer.util', {
    singleton: true,

    reset: function () {
        delete this._containerBim;
    },

    init: function () {
        this.reset();
        this._containerBim = Ext.ComponentQuery.query('bim-bimserver-container')[0];
        this._modeButton = this._containerBim.lookupReference('rightPanel').lookupReference('mode');

    },

    setMinimal: function (minimal) {
        this._minimal = minimal;
    },

    getMinimal: function () {
        return this._minimal;
    },

    /**
     * @param {Minimal} minimalInstance the instance of the class minimal
     */
    setMinimalInstance: function (minimalInstance) {
        this._minimalInstance = minimalInstance;
    },

    getMinimalInstance: function () {
        return this._minimalInstance;
    },

    /**
     * Get's the mode between 'pan' and 'rotate' looking at the component
     * that stores this information
     */
    getMode: function (mouseWich) {
        var returned;
        switch (this._modeButton.mode) {
            case 'rotate':

                mouseWich == 1 ? returned = 1 : returned = 2;
                break;
            case 'pan':
                mouseWich == 1 ? returned = 2 : returned = 1;
                break;
        }
        return returned;
    },

    /**
     * Resets the values of the dimension of the viewer
     */
    resize: function () {

    },

    privates: {

        /**
         * This contains the exported class Minimal in BIMsurfer project
         */
        _minimal: null
    }
});
