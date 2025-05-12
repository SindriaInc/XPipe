Ext.define('CMDBuildUI.view.administration.content.setup.elements.PasswordPolicyController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-passwordpolicy',

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

    onEditBtnClick: function () {
        var vm = this.getViewModel();
        vm.set('actions.view', false);
        vm.set('actions.edit', true);
    },

    onCancelBtnClick: function(){
        this.redirectTo('administration/setup/authentication', true);
    },

    onSaveBtnClick: function(button){       
        var vm = button.lookupViewModel();
            Ext.getBody().mask(CMDBuildUI.locales.Locales.administration.common.messages.saving);
            // TODO: workaround #1051

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
                    vm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
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
    }
});