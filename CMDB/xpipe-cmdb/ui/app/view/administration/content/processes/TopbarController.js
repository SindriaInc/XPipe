Ext.define('CMDBuildUI.view.administration.content.processes.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-processes-topbar',

    control:{
        '#addprocess':{
            click: 'onAddProcessClick'
        },
        '#printschema': {
            click: 'onPrintSchemaClick'
        }
    },
    

    onAddProcessClick: function(){
        this.redirectTo('administration/processes',true);
        var vm = Ext.getCmp('administrationNavigationTree').getViewModel();
        vm.set('selected', null);
    },    
    onPrintSchemaClick: function(button, event, eOpts){
        var url = Ext.String.format('{0}/classes/print_schema/schema.pdf?extension=pdf', CMDBuildUI.util.Config.baseUrl);
        CMDBuildUI.util.File.download(url, 'pdf');
    }
});
