(function () {

    var elementId = 'CMDBuildAdministrationContentLookupTypesView';
    Ext.define('CMDBuildUI.view.administration.content.lookuptypes.View', {
        extend: 'Ext.container.Container',

        requires: [
            'CMDBuildUI.view.administration.content.lookuptypes.ViewController',
            'CMDBuildUI.view.administration.content.lookuptypes.ViewModel'
        ],

        alias: 'widget.administration-content-lookuptypes-view',
        controller: 'administration-content-lookuptypes-view',
        viewModel: {
            type: 'administration-content-lookuptypes-view'
        },
        title: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.lookup,
        localized:{
            title: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.lookup'
        },
        itemId: elementId,
        reference: elementId,
        id: elementId,
        config: {
            objectTypeName: null,
            allowFilter: true,
            showAddButton: true,
            action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
            title: null
        },
        loadMask: true,
        defaults: {
            textAlign: 'left',
            scrollable: true
        },
        layout: 'border',
        style: 'background-color:#fff',
        items: [{
            xtype: 'administration-content-lookuptypes-topbar',
            region: 'north'
        }, {
            xtype: 'administration-content-lookuptypes-tabpanel',
            region: 'center',
            hidden: true,
            bind:{
                hidden: '{!theLookupType}'
            }
        }],
        listeners: {
            afterlayout: function (panel) {
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
            }
        },
        initComponent: function () {
            this.callParent(arguments);
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        }
    });
})();