Ext.define('CMDBuildUI.view.login.SsoPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.login-ssopanel',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.login.SsoPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = view.lookupViewModel(),
            modules = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.login.modules) || [];

        Ext.Array.forEach(modules, function (module) {
            var icon = CMDBuildUI.util.helper.Configurations.get(Ext.String.format(
                CMDBuildUI.model.Configuration.login.module_icon,
                module
            ));
            view.add({
                xtype: 'button',
                ui: 'management-primary-outline-small',
                text: Ext.String.format(
                    CMDBuildUI.locales.Locales.login.sso.loginwith,
                    CMDBuildUI.util.helper.Configurations.get(Ext.String.format(
                        CMDBuildUI.model.Configuration.login.module_description,
                        module
                    ))
                ),
                flex: 1,
                width: '100%',
                icon: icon ? icon : null,
                // show default icon if there is not an icon
                iconCls: !icon ? CMDBuildUI.util.helper.IconHelper.getIconId('key', 'solid') : null,
                handler: function () {
                    var startingurl = CMDBuildUI.util.helper.SessionHelper.getStartingUrl();
                    startingurl = startingurl ? startingurl : 'management';
                    window.open(Ext.String.format(
                        '{0}?cm_login_module={1}&cm_login_referer_fragment={2}',
                        window.location.origin + window.location.pathname,
                        module,
                        startingurl
                    ), '_self');
                }
            });
        });
        var session = CMDBuildUI.util.helper.SessionHelper.getCurrentSession();
        if (session && session.crudState === "D") {
            session = null;
        }
        vm.set('sso.hidden', modules.length < 1 || !!session);
    }
});