
Ext.define('CMDBuildUI.view.bim.bimserver.tab.cards.Controls', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.bim-bimserver-tab-cards-controls',

    items: [
        {
            xtype: 'container',
            layout: {
                type: 'hbox',
                pack: 'start',
                align: 'stretch'
            },
            items: [{
                xtype: 'button',
                text: CMDBuildUI.locales.Locales.bim.menu.resetView,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.bim.menu.resetView'
                },
                flex: 1,
                handler: function () {
                    CMDBuildUI.util.bim.Viewer.defaultView();
                }
            }, {
                xtype: 'button',
                text: CMDBuildUI.locales.Locales.bim.menu.frontView,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.bim.menu.frontView'
                },
                flex: 1,
                handler: function () {
                    CMDBuildUI.util.bim.Viewer.frontView();
                }
            }, {
                xtype: 'button',
                text: CMDBuildUI.locales.Locales.bim.menu.sideView,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.bim.menu.sideView'
                },
                flex: 1,
                handler: function () {
                    CMDBuildUI.util.bim.Viewer.sideView();
                }
            }, {
                xtype: 'button',
                text: CMDBuildUI.locales.Locales.bim.menu.topView,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.bim.menu.topView'
                },
                flex: 1,
                handler: function () {
                    CMDBuildUI.util.bim.Viewer.topView();
                }
            }]
        }
    ]
});
