Ext.define('CMDBuildUI.view.administration.content.schedules.settings.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-schedules-settings-view',

    formulas: {
        activeTab: function () {
            return this.get('activeTabs.dmssettings') || 0;
        },
        disabledLookupTabs: {
            bind: {
                disabled: '{!theSetup.org__DOT__cmdbuild__DOT__scheduler__DOT__active}'
            },
            get: function (data) {
                return !!data.disabled;
            }
        },
        selectedClasses: {
            bind: '{theSetup.org__DOT__cmdbuild__DOT__scheduler__DOT__selectableclasses}',
            get: function (selectableclasses) {
                return selectableclasses;
            }
        }
    }

});