Ext.define('CMDBuildUI.view.login.ContainerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.login-container',
    data: {
        showChangePassword: false,
        sso: {
            hidden: true,
            hiddendefaultlogin: true
        }
    },

    formulas: {
        loginText: function () {
            return CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.login.text);
        }
    }
});