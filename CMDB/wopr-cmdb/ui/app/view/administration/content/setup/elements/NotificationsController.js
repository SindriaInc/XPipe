Ext.define('CMDBuildUI.view.administration.content.setup.elements.NotificationsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-notifications',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view) {
        view.up('administration-content').getViewModel().set('title', Ext.String.format('{0} - {1}', CMDBuildUI.locales.Locales.administration.tasks.notifications, CMDBuildUI.locales.Locales.administration.navigation.settings));
        view.up('administration-content-setup-view').getViewModel().setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
    }

});