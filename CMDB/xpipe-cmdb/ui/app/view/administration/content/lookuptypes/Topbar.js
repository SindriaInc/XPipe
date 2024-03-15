Ext.define('CMDBuildUI.view.administration.content.lookuptypes.Topbar', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.lookuptypes.TopbarController',
        'CMDBuildUI.view.administration.content.lookuptypes.TopbarModel'
    ],

    alias: 'widget.administration-content-lookuptypes-topbar',
    controller: 'administration-content-lookuptypes-topbar',
    viewModel: {
        type: 'administration-content-lookuptypes-topbar'
    },
    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true
    },

    forceFit: true,
    loadMask: true,

    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.administration.lookuptypes.toolbar.addClassBtn.text,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.lookuptypes.toolbar.addClassBtn.text'
        },
        ui: 'administration-action-small',
        reference: 'addlookuptype',
        itemId: 'addlookuptype',
        iconCls: 'x-fa fa-plus',
        autoEl: {
            'data-testid': 'administration-class-toolbar-addLookupTypeBtn'
        },
        bind: {
            disabled: '{!toolAction._canAdd}'
        }
    }, {
        xtype: 'admin-globalsearchfield',
        objectType: 'lookup_types'
    }, {
        xtype: 'tbfill'
    }, {
        xtype: 'tbtext',
        dock: 'right',
        bind: {
            html: '{lookupLabel}: <b data-testid="administration-lookuptypes-toolbar-className">{theLookupType.name}</b>'
        }
    }],

    initComponent: function () {
        var vm = this.lookupViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.navigation.lookuptypes);
        this.callParent(arguments);
    }
});