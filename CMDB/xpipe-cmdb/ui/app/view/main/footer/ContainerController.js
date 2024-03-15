Ext.define('CMDBuildUI.view.main.footer.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.main-footer-container',

    control: {
        '#': {
            afterrender: 'onBeforeRender'
        }
    },

    /**
     * On before render listener.
     *
     * @param {CMDBuildUI.view.main.footer.Container} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var linkCmp = view.down('#urlComponent');
        linkCmp.setHtml(view.getApplicationUrlHTML());
    },

    /**
     * On info component click
     */
    onInfoComponentClick: function () {
        var view = this.getView();
        var version = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.common.fullversion);
        CMDBuildUI.util.Utilities.openPopup(
            null,
            CMDBuildUI.locales.Locales.main.info, {
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [{
                xtype: 'main-header-logo',
                padding: "10 15",
                clickCounter: 0,
                listeners: {
                    click: {
                        fn: function () {
                            var view = Ext.get(this);
                            view.component.clickCounter += 1;
                            if (view.component.clickCounter === 10) {
                                view.component.clickCounter = 0;
                                CMDBuildUI.util.Msg.openDialog('Project contributors', {html: '@@CMDBUILD_CONTRIBUTORS'})
                            }
                        },
                        element: 'el'
                    }
                }
            }, {
                html: Ext.String.format(view.getPopupHTML(), version),
                padding: "10 15"
            }]
        }, null, {
            width: 400,
            height: view.getPopupHeight(),
            ui: 'managementlighttabpanel'
        });
    }
});
