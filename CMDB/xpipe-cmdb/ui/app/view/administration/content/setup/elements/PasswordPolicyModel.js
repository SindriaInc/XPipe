Ext.define('CMDBuildUI.view.administration.content.setup.elements.PasswordPolicyModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-passwordpolicy',
    data: {
        actions: {
            view: true,
            edit: false
        }

    },
    formulas: {
        // configManager: function () {
        //     var me = this;

        //     CMDBuildUI.util.administration.helper.ConfigHelper.getConfigs().then(
        //         function (configs) {
        //             if (!me.destroyed) {
        //                 configs.forEach(function (key) {
        //                     me.set(Ext.String.format('theSetup.{0}', key._key), (key.hasValue) ? key.value : key.default);
        //                 });
        //             }
        //         }
        //     );
        // }
    }
});