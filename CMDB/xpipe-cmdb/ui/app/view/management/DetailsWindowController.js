Ext.define('CMDBuildUI.view.management.DetailsWindowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.management-detailswindow',

    control: {
        '#': {
            boxready: 'onBoxReady',
            closed: 'onClosed'
        }
    },

    /**
     * @param {CMDBuildUI.view.management.DetailsWindow} window
     * @param {Number} width
     * @param {Number} height
     * @param {Object} eOpts
     */
    onBoxReady: function (window, width, height, eOpts) {
        this.getView().anchorTo(Ext.getBody(), 'br-br', { x: -30, y: 0 });
    },

    onClosed: function (recordType,eOpts) {
        this.destroyDetailsWindow();
        this.fireEvent('close');
    },

    /**
     * @private
     */
    destroyDetailsWindow: function () {
        var view = this.getView().close();
    }
});
