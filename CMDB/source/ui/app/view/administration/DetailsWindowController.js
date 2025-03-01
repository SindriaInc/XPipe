Ext.define('CMDBuildUI.view.administration.DetailsWindowController', {
    extend: 'CMDBuildUI.view.management.DetailsWindowController',
    alias: 'controller.administration-detailswindow',

    control: {
        '#': {
            boxready: 'onBoxReady'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.DetailsWindow} window
     * @param {Number} width
     * @param {Number} height
     * @param {Object} eOpts
     */
    onBoxReady: function (view, width, height, eOpts) {
        this.getView().anchorTo(Ext.getBody(), 'br-br', {
            x: -30,
            y: 0
        });
    },

    onClosed: function (recordType, eOpts) {

        this.destroyDetailsWindow();
        this.fireEvent('close');
    },
    onClose: function (recordType, eOpts) {

    },
    
    /**
     * @private
     */
    destroyDetailsWindow: function () {
        var view = this.getView();
        if (view) {
            view.close();
        }
    }
});