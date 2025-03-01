
Ext.define('CMDBuildUI.view.login.Container', {
    extend: 'Ext.container.Container',

    requires: [
        'CMDBuildUI.view.login.ContainerController',
        'CMDBuildUI.view.login.ContainerModel'
    ],

    alias: 'widget.login-container',
    controller: 'login-container',
    viewModel: {
        type: 'login-container'
    },

    layout: {
        type: 'vbox',
        align: 'center',
        pack: 'center'
    },

    cls: Ext.baseCSSPrefix + 'login-main-container',

    items: [{
        xtype: 'container',
        width: 450,
        items: [{
            xtype: 'container',
            layout: {
                type: 'hbox',
                align: 'center',
                pack: 'center'
            },
            items: [{
                xtype: 'main-header-logodark',
                height: 60,
                width: "45%",
                margin: '0 0 50 0'
            }],
        }, {
            xtype: 'container',
            cls: Ext.baseCSSPrefix + 'login-form-container',
            padding: 30,
            items: [{
                hidden: true,
                autoEl: {
                    'data-testid': 'login-text'
                },
                bind: {
                    hidden: '{!loginText}',
                    html: '{loginText}'
                }
            }, {
                xtype: 'login-formpanel'
            }, {
                xtype: 'button',
                text: CMDBuildUI.locales.Locales.main.password.forgotten,
                ui: 'link',
                cls: Ext.baseCSSPrefix + 'mt-2',
                bind: {
                    hidden: '{disablechangepassword || disabledfields.password}'
                },
                itemId: 'pwdforgottenbtn',
                autoEl: {
                    'data-testid': 'login-pwdforgottenbtn'
                },
                localized: {
                    text: 'CMDBuildUI.locales.Locales.main.password.forgotten'
                }
            }, {
                xtype: 'login-ssopanel'
            }]
        }]
    }, {
        xtype: 'container',
        width: 450,
        cls: Ext.baseCSSPrefix + 'login-bottom-container',
        layout: {
            type: 'hbox',
            pack: 'center',
            align: 'middle'
        },
        items: [
            {
                xtype: 'container',
                flex: 1,
                items: {
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('globe', 'solid'),
                    cls: Ext.baseCSSPrefix + 'language-selector',
                    ui: 'language-selector',
                    xtype: 'button',
                    itemId: 'languageselector',
                    hidden: false,
                    autoEl: {
                        'data-testid': 'header-languageselector'
                    }
                }
            },
            {
                xtype: 'container',
                flex: 1,
                layout: {
                    type: 'vbox',
                    align: 'right',
                    pack: 'center'
                },
                items: [
                    {
                        xtype: 'component',
                        bind: {
                            html: '<small><a href="https://www.tecnoteca.com" target="_blank">Tecnoteca srl</a> © {currentYear}</small>'
                        }

                    }
                ]
            }
        ]
    }]
});
