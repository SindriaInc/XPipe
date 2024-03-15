Ext.define('CMDBuildUI.view.widgets.gotocard.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.widgets-gotocard-panel',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        }
    },

    /**
     * @param {CMDBuildUI.view.widgets.gotocard.Panel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var theWidget = view.lookupViewModel().get("theWidget"),
            url = CMDBuildUI.util.Navigation.getClassBaseUrl(theWidget.get("ClassName"), theWidget.get("ObjId"), null, true);
        if (theWidget.get("IsAttachment")) {
            url += "/attachments"
        }
        CMDBuildUI.util.Utilities.redirectTo(url);
    },

    /**
     * @param {CMDBuildUI.view.widgets.gotocard.Panel} view
     * @param {Object} eOpts
     */
    onAfterRender: function (view, eOpts) {
        view.ownerCt.close();
    }
});