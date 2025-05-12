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
        },
        currentYear: function () {
            return new Date().getFullYear();
        }
    },

    stores: {
        languages: {
            model: 'CMDBuildUI.model.Language',
            sorters: 'description',
            pageSize: 0,
            autoLoad: '{language.showselector}',
            autoDestroy: true
        }
    }
});
