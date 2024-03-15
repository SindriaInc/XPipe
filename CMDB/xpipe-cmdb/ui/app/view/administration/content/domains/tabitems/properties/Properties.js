Ext.define('CMDBuildUI.view.administration.content.domains.tabitems.properties.Properties', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.domains.tabitems.properties.PropertiesController'
    ],

    alias: 'widget.administration-content-domains-tabitems-properties-properties',
    controller: 'administration-content-domains-tabitems-properties-properties',
    viewModel: {

    },
    config: {
        theDomain: null
    },
    autoScroll: true,
    modelValidation: true,


    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    items: [{
        xtype: 'panel',
        region: 'center',
        scrollable: 'y',
        items: [{
            xtype: 'administration-content-domains-tabitems-properties-fieldsets-generaldatafieldset'
        }, {
            xtype: 'administration-content-domains-tabitems-properties-fieldsets-masterdetailfieldset'
        }, {
            xtype: 'administration-content-domains-tabitems-properties-fieldsets-filtersfieldset',
            bind: {
                hidden: '{theDomain.cardinality === "N:N" ||theDomain.cardinality === "1:1" }'
            }
        }]
    }],
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons()
    }],

    listeners: {

        afterlayout: function (panel) {

            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
        }
    },

    initComponent: function () {
        var vm = this.getViewModel();
        var cardView = this.up('administration-detailswindow');
        if (cardView) {
            switch (vm.get('action')) {
                case CMDBuildUI.util.administration.helper.FormHelper.formActions.add:
                    vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.domains.texts.newdomain);
                    break;
                case CMDBuildUI.util.administration.helper.FormHelper.formActions.edit:
                case CMDBuildUI.util.administration.helper.FormHelper.formActions.view:
                    vm.getParent().set('title', Ext.String.format('{0} - {1}', CMDBuildUI.locales.Locales.administration.domains.domain, vm.get('objectTypeName')));
                    break;
            }
        }

        this.callParent(arguments);
    }

});