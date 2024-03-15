Ext.define('CMDBuildUI.view.login.Container', {
    extend: 'Ext.container.Container',

    requires: [
        'CMDBuildUI.view.login.ContainerController',
        'CMDBuildUI.view.login.ContainerModel',

        'CMDBuildUI.view.login.FormPanel'
    ],

    xtype: 'login-container',
    controller: 'login-container',
    viewModel: {
        type: 'login-container'
    },

    config: {
        /**
         * @cfg {Boolean} showChangePassword
         */
        showChangePassword: false
    },

    publish: ["showChangePassword"],
    twoWayBindable: ["showChangePassword"],

    bind: {
        showChangePassword: '{showChangePassword}'
    },

    defaults: {
        border: true,
        bodyPadding: 15,
        width: 300
    },

    scrollable: true,

    // add data-testid attribute to element
    autoEl: {
        'data-testid': 'login-container'
    },

    layout: {
        type: 'vbox',
        align: 'center',
        pack: 'center'
    },
    padding: 15,

    items: [{
        width: '50%',
        hidden: true,
        autoEl: {
            'data-testid': 'login-text'
        },
        bind: {
            hidden: '{!loginText}',
            html: '{loginText}'
        }
    }, {
        xtype: 'login-formpanel',
        hidden: true,
        itemId: 'logingform',
        bind: {
            hidden: '{showChangePassword||sso.hiddendefaultlogin}'
        }
    }, {
        xtype: 'login-changepassword-form',
        hidden: true,
        title: CMDBuildUI.locales.Locales.main.password.change,
        itemId: 'changepasswordform',
        ui: "management",
        viewModel: {
            data: {
                username: null,
                oldpassword: null
            }
        },
        bind: {
            hidden: '{!showChangePassword}'
        },
        localized: {
            title: 'CMDBuildUI.locales.Locales.main.password.change'
        }
    }, {
        xtype: 'login-ssopanel'
    }]
});