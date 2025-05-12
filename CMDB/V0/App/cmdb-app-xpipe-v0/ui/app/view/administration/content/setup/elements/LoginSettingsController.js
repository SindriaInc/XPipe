Ext.define('CMDBuildUI.view.administration.content.setup.elements.LoginSettingsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-loginsettings',

    control: {
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    onEditBtnClick: function (button, e, eOpts) {
        var vm = button.lookupViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },

    onSaveBtnClick: function (button) {
        var vm = button.lookupViewModel();
        Ext.getBody().mask(CMDBuildUI.locales.Locales.administration.common.messages.saving);

        var setData = CMDBuildUI.util.administration.helper.ConfigHelper.setConfigs(
            /** theSetup */
            vm.get('theSetup'),
            /** reloadOnSucces */
            true,
            /** forceDropCache */
            false,
            this
        );

        setData.then(function (transport) {
            if (!vm.destroyed) {
                vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
            }
        });
        setData.always(function () {
            if (!button.destroyed) {
                button.enable();
            }
            if (Ext.getBody().isMasked()) {
                Ext.getBody().unmask();
            }
        });
    },

    onCancelBtnClick: function () {
        this.redirectTo('administration/setup/authentication', true);
    }
});