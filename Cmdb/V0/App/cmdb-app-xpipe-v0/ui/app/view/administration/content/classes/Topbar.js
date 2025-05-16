Ext.define('CMDBuildUI.view.administration.content.classes.Topbar', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.classes.TopbarController'
    ],

    alias: 'widget.administration-content-classes-topbar',
    controller: 'administration-content-classes-topbar',

    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true
    },

    forceFit: true,
    loadMask: true,

    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.administration.classes.toolbar.addClassBtn.text,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.classes.toolbar.addClassBtn.text'
        },
        ui: 'administration-action-small',
        reference: 'addclass',
        itemId: 'addclass',
        iconCls: 'x-fa fa-plus',
        autoEl: {
            'data-testid': 'administration-class-toolbar-addClassBtn'
        },
        listeners: {
            render: function () {
                this.setDisabled(!this.lookupViewModel().get('theSession.rolePrivileges.admin_classes_modify'));
            }
        }
    }, {
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.administration.classes.toolbar.printSchemaBtn.text,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.classes.toolbar.printSchemaBtn.text'
        },
        ui: 'administration-action-small',
        itemId: 'printschema',
        iconCls: 'x-fa fa-print',
        bind: {
            disabled: '{printButtonDisabled}',
            hidden: '{printButtonHidden}'
        },
        autoEl: {
            'data-testid': 'administration-class-toolbar-printSchemaBtn'
        }
    }, {
        xtype: 'admin-globalsearchfield',
        objectType: 'classes'
    }, {
        xtype: 'tbfill'
    }, {
        xtype: 'tbtext',
        dock: 'right',
        bind: {
            html: '{classLabel}: <b data-testid="administration-class-toolbar-className">{theObject.name}</b>'
        }
    }]
});