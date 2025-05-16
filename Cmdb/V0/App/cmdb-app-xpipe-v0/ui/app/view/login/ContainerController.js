Ext.define('CMDBuildUI.view.login.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.login-container',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#changepasswordform': {
            passwordchange: 'onPasswordChange'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.login.Container} view
     * @param {Object} eOpts
     */
    onBeforeRender: function(view, eOpts) {
        var enabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.login.default),
            hidden = CMDBuildUI.util.helper.Configurations.get('cm_system_login_default_hidden') || false,
            forceDefault = window.location.search.indexOf('cm_login_module=default') > -1,
            vm = view.lookupViewModel();
        vm.set('sso.hiddendefaultlogin', !enabled || (hidden && !forceDefault));

        // force show default login when user is authenticated to choose group or tenant
        vm.bind({
            bindTo: "{theSession}",
            single: true
        }, function(session) {
            if (session && session.crudState === "R") {
                vm.set('sso.hiddendefaultlogin', false);
                vm.set('sso.hidden', true);
            }
        });
    },

    /**
     *
     * @param {CMDBuildUI.view.login.changepassword.Form} view
     */
    onPasswordChange: function(view) {
        this.getViewModel().set("showChangePassword", false);

        view.previousSibling().lookupViewModel().set("password", null);
        // window.location.reload();
    }
});