Ext.define('CMDBuildUI.view.administration.content.setup.elements.LoginSettingsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-loginsettings',
    data: {
        actions: {
            view: true,
            edit: false
        }
    },
    formulas: {
        action: {
            get: function () {
                return this.get('actions.view') ? CMDBuildUI.util.administration.helper.FormHelper.formActions.view : CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
            }
        },
        ssoautoLogin: {
            bind: ' {theSetup.org__DOT__cmdbuild__DOT__auth__DOT__sso__DOT__redirect__DOT__enabled}',
            get: function (enabled) {
                return enabled.trim() !== 'true';
            },
            set: function (value) {
                this.set('theSetup.org__DOT__cmdbuild__DOT__auth__DOT__sso__DOT__redirect__DOT__enabled', !value);
            }
        }
    }
});