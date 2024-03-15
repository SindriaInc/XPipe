Ext.define('CMDBuildUI.view.login.SsoPanel', {
    extend: 'CMDBuildUI.components.tab.FieldSet',

    requires: [
        'CMDBuildUI.view.login.SsoPanelController',
        'CMDBuildUI.view.login.SsoPanelModel'
    ],

    xtype: 'login-ssopanel',
    controller: 'login-ssopanel',
    viewModel: {
        type: 'login-ssopanel'
    },

    title: CMDBuildUI.locales.Locales.login.sso.or,
    localized: {
        title: 'CMDBuildUI.locales.Locales.login.sso.or'
    },
    collapsible: false,
    cls: Ext.baseCSSPrefix + 'sso-fieldset',
    hidden: true,

    defaults: {
        ui: 'sso'
    },

    items: [],

    bind: {
        title: '{sso.title}',
        hidden: '{sso.hidden}',
        margin: '{sso.margin}'
    }
});