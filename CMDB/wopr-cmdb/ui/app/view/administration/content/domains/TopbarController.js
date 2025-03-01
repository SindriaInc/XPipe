Ext.define('CMDBuildUI.view.administration.content.domains.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-domains-topbar',

    control: {        
        '#adddomain': {
            click: 'onAddDomainClick'
        }
    },    
    onAddDomainClick: function () {
        this.redirectTo('administration/domains', true);
        var vm = Ext.getCmp('administrationNavigationTree').getViewModel();
        vm.set('selected', null);
        this.getViewModel().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
    }
});
