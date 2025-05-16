Ext.define('CMDBuildUI.view.administration.content.lookuptypes.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-lookuptypes-topbar',

    control: {
        '#addlookuptype': {
            click: 'onAddLookupTypeClick'
        }
    },
    onAddLookupTypeClick: function () {
        this.redirectTo('administration/lookup_types', true);
        var vm = Ext.getCmp('administrationNavigationTree').getViewModel();        
        vm.set('selected', null);
    }
});