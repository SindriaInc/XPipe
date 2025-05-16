Ext.define('CMDBuildUI.view.main.header.Header', {
    extend: 'Ext.toolbar.Toolbar',

    requires: [
        'CMDBuildUI.view.main.header.HeaderController',
        'CMDBuildUI.view.main.header.HeaderModel'
    ],

    xtype: 'main-header-header',
    controller: 'main-header-header',
    viewModel: {
        type: 'main-header-header'
    },

    padding: "10px 15px",

    // add data-testid attribute to element
    autoEl: {
        'data-testid': 'main-header-header'
    },

    items: [{
        xtype: 'main-header-logo',
        height: 30,
        cls: 'logo',
        reference: 'cmdbuildLogo',
        itemId: 'cmdbuildLogo',
        autoEl: {
            'data-testid': 'header-cmdbuildlogo'
        }
    }, {
        xtype: 'tbspacer'
    }, {
        xtype: 'container',
        reference: 'companylogocontainer',
        itemId: 'companylogocontainer',
        bind: {
            hidden: '{companylogoinfo.hidden}'
        }
    }, {
        xtype: 'tbspacer',
        hidden: true,
        bind: {
            hidden: '{companylogoinfo.hidden}'
        }
    }, {
        xtype: 'tbtext',
        reference: 'instanceName',
        itemId: 'instanceName',
        cls: 'instancename',
        autoEl: {
            'data-testid': 'header-instancename'
        },
        bind: {
            text: '{instancename}'
        }
    }, {
        xtype: 'tbfill'
        // }, {
        //     iconCls: 'x-fa fa-search',
        //     ui: 'header',
        //     tooltip: CMDBuildUI.locales.Locales.main.searchinallitems,
        //     reference: 'globalsearch',
        //     itemId: 'globalsearch',
        //     hidden: true,
        //     autoEl: {
        //         'data-testid': 'header-globalsearch'
        //     },
        //     bind: {
        //         hidden: '{!isAuthenticated}'
        //     },
        //     localized: {
        //         tooltip: 'CMDBuildUI.locales.Locales.main.searchinallitems'
        //     }
        // }, {
        //     xtype: 'tbseparator',
        //     hidden: true,
        //     bind: {
        //         hidden: '{!isAuthenticated}'
        //     }
    }, {
        iconCls: 'x-fa fa-calendar',
        ui: 'header',
        tooltipLabel: "CMDBuildUI.locales.Locales.calendar.scheduler",
        itemId: 'schedulerbtn',
        hidden: true,
        autoEl: {
            'data-testid': 'header-schedulerbtn'
        },
        bind: {
            hidden: '{calendarbtnhidden || isAdministrationModule}'
        }
    }, {
        xtype: 'main-header-usermenu',
        iconCls: 'x-fa fa-users',
        hidden: true,
        autoEl: {
            'data-testid': 'header-usermenu'
        },
        bind: {
            hidden: '{!isAuthenticated}'
        }
    }, {
        iconCls: 'x-fa fa-bell-o',
        ui: 'header',
        tooltipLabel: "CMDBuildUI.locales.Locales.notifications.label",
        itemId: 'notificationsBtn',
        hidden: true,
        autoEl: {
            'data-testid': 'header-notifications'
        },
        bind: {
            hidden: '{!isAuthenticated}',
            userCls: '{notifications.hasUnread ? "unreadNotificationsMarker" : ""}'
        }
    }, {
        iconCls: 'x-fa fa-cog',
        ui: 'header',
        tooltipLabel: "CMDBuildUI.locales.Locales.main.administrationmodule",
        reference: 'administrationbtn',
        itemId: 'administrationbtn',
        href: '#administration/home',
        hidden: true,
        autoEl: {
            'data-testid': 'header-administration'
        },
        bind: {
            hidden: '{!isAdministrator}'
        }
    }, {
        iconCls: 'x-fa fa-table',
        ui: 'header',
        tooltipLabel: "CMDBuildUI.locales.Locales.main.managementmodule",
        reference: 'managementbtn',
        itemId: 'managementbtn',
        href: '#management',
        hidden: true,
        autoEl: {
            'data-testid': 'header-management'
        },
        bind: {
            hidden: '{!isAdministrationModule}'
        }
    }, {
        iconCls: 'x-fa fa-sign-out',
        ui: 'header',
        tooltipLabel: "CMDBuildUI.locales.Locales.main.logout",
        reference: 'logoutbtn',
        itemId: 'logoutbtn',
        hidden: true,
        autoEl: {
            'data-testid': 'header-logout'
        },
        bind: {
            hidden: '{!isAuthenticated}'
        }
    }, {
        iconCls: 'x-fa fa-flag',
        ui: 'header',
        itemId: 'languageselector',
        hidden: true,
        autoEl: {
            'data-testid': 'header-languageselector'
        },
        bind: {
            text: '{languageSelectorText}',
            hidden: '{isAuthenticated || !language.showselector}',
            menu: '{languagesMenu}'
        }
    }]
});