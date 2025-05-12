Ext.define('CMDBuildUI.view.administration.content.domains.Topbar', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.domains.TopbarController'
    ],

    alias: 'widget.administration-content-domains-topbar',
    controller: 'administration-content-domains-topbar',
    viewModel: {},

    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true
    },

    forceFit: true,
    loadMask: true,

    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.administration.domains.toolbar.addBtn.text,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.domains.toolbar.addBtn.text'
        },
        ui: 'administration-action-small',
        reference: 'adddomain',
        itemId: 'adddomain',
        autoEl: {
            'data-testid': 'administration-domain-toolbar-addDomainBtn'
        },
        listeners: {
            render: function () {
                this.setDisabled(!this.lookupViewModel().get('theSession.rolePrivileges.admin_domains_modify'));
            }
        }
        // }, {
        //      /**
        //      * This button is actually not implemented
        //      */
        //     xtype: 'button',
        //     text:  CMDBuildUI.locales.Locales.administration.lookuptypes.toolbar.printSchemaBtn.text,
        //     ui:'administration-action-small',
        //     reference: 'printdomain',
        //     itemId: 'printdomain',
        //     iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('print', 'solid'),
        //     bind: {
        //         disabled: '{printButtonDisabled}',
        //         hidden: '{printButtonHidden}'
        //     },
        //     autoEl: {
        //         'data-testid': 'administration-domain-toolbar-printDomainBtn'
        //     }
    }, {
        xtype: 'admin-globalsearchfield',
        objectType: 'domains'
    }, {
        xtype: 'tbfill'
    }, {
        xtype: 'tbtext',
        dock: 'right',
        bind: {
            html: '{domainLabel}: <b data-testid="administration-domain-toolbar-domainName">{theDomain.name}</b>'
        }
    }],
    initComponent: function () {
        var vm = this.lookupViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.navigation.domains);
        this.callParent(arguments);
    }
});