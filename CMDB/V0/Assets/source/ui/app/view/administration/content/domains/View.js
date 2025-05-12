(function () {
    var elementId = 'CMDBuildAdministrationContentDomainsView';
    Ext.define('CMDBuildUI.view.administration.content.domains.View', {
        extend: 'Ext.panel.Panel',

        alias: 'widget.administration-content-domains-view',

        requires: [
            'CMDBuildUI.view.administration.content.domains.ViewController',
            'CMDBuildUI.view.administration.content.domains.ViewModel'
        ],

        controller: 'administration-content-domains-view',
        viewModel: {
            type: 'administration-content-domains-view'
        },
        id: elementId,
        statics: {
            elementId: elementId
        },
        loadMask: true,
        defaults: {
            textAlign: 'left',
            scrollable: true
        },
        layout: 'border',
        items: [{
            xtype: 'administration-content-domains-topbar',
            region: 'north'
        }, {
            xtype: 'administration-content-domains-tabpanel',
            region: 'center',
            hidden: true,
            bind: {
                hidden: '{!theDomain}'
            }

        }],

        listeners: {
            afterlayout: function (panel) {
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
            }
        },
        initComponent: function () {
            var vm = this.getViewModel();            
            if(vm && vm.getParent){
                vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.navigation.domains);
            }
            this.callParent(arguments);

            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        }
    });
})();