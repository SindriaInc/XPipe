Ext.define('CMDBuildUI.view.administration.navigation.ContainerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-navigation-container',
    formulas: {
        navTitle: function() {
            return CMDBuildUI.locales.Locales.main.navigation;
        }
    }

});
