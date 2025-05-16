Ext.define('CMDBuildUI.view.administration.content.setup.elements.GeneralOptionsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-generaloptions',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view) {
        view.up('administration-content').getViewModel().set('title', CMDBuildUI.locales.Locales.administration.navigation.generaloptions);
        var vm = view.up('administration-content-setup-view').getViewModel();
        vm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);

        /**
         * validate the field after the value changes
         */
        this.getViewModel().bind({
            value: '{inactiveusers_value}'
        }, function (data) {
            Ext.asap(function () {
                if (!view.destroyed) {
                    view.lookupReference('inactiveusers').validate(data.value);
                }
            }, this);
        }, this);
    }

});
