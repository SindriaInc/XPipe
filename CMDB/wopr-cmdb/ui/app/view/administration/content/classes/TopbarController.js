Ext.define('CMDBuildUI.view.administration.content.classes.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-classes-topbar',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#addclass': {
            click: 'onAddClassClick'
        },
        '#printschema': {
            click: 'onPrintSchemaClick'
        }
    },

    onBeforeRender: function (view) {
        view.up('administration-content').getViewModel().set('title', CMDBuildUI.locales.Locales.administration.navigation.classes);
    },
    onAddClassClick: function () {
        this.redirectTo('administration/classes', true);
        var vm = Ext.getCmp('administrationNavigationTree').getViewModel();
        vm.set('selected', null);
    },
    onPrintSchemaClick: function (button, event, eOpts) {
        var url = Ext.String.format('{0}/classes/print_schema/schema.pdf?extension=pdf', CMDBuildUI.util.Config.baseUrl);
        CMDBuildUI.util.File.download(url, 'pdf');
    }
});
