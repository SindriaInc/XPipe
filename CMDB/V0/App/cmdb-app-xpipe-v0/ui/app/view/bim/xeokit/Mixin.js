Ext.define('CMDBuildUI.view.bim.xeokit.Mixin', {
    mixinId: 'bim-mixin',

    /**
     * 
     * @returns the container view
     */
    getContainer: function () {
        return this.up("bim-xeokit-container");
    }

});