Ext.define('CMDBuildUI.view.administration.content.schedules.settings.SchedulerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-schedules-settings-scheduler',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view) {
        view.up('administration-content').getViewModel().set('title', Ext.String.format('{0} - {1}', CMDBuildUI.locales.Locales.administration.navigation.schedules, CMDBuildUI.locales.Locales.administration.navigation.settings));
        view.up('administration-content-setup-view').getViewModel().setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);


        
    }


});