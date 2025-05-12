Ext.define('CMDBuildUI.view.administration.content.schedules.settings.SchedulerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-schedules-settings-scheduler',
    formulas: {
        disabledLookupTabs: {
            bind: {
                disabled: '{!theSetup.org__DOT__cmdbuild__DOT__scheduler__DOT__active}'
            },
            get: function (data) {
                return !!data.disabled;
            }
        }
    }
});