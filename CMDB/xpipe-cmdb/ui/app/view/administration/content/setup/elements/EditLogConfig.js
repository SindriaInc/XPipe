Ext.define('CMDBuildUI.view.administration.content.setup.elements.EditLogConfig', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.EditLogConfigController',
        'CMDBuildUI.view.administration.content.setup.elements.EditLogConfigModel'
    ],

    alias: 'widget.administration-content-setup-elements-editlogconfig',
    controller: 'administration-content-setup-elements-editlogconfig',
    viewModel: {
        type: 'administration-content-setup-elements-editlogconfig'
    },
    layout: 'card',
    forceFit: 'true',
    scrollable: true,
    reserveScrollbar: true,
    plugins: {
        pluginId: 'cellediting',
        ptype: 'cellediting',
        clicksToEdit: 1,
        listeners: {
            beforeedit: 'onBeforeCellEdit'
        }
    },
    viewConfig: {
        markDirty: false
    },
    sortable: false,
    bind: {
        store: '{configKeysStore}'
    },
    columns: [{
        text: CMDBuildUI.locales.Locales.administration.systemconfig.logcategory,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.systemconfig.logcategory'
        },
        dataIndex: 'category',
        sortable: false,
        editor: {
            xtype: 'textfield'
        }
    }, {
        text: CMDBuildUI.locales.Locales.administration.systemconfig.content,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.systemconfig.content'
        },
        dataIndex: 'description',
        sortable: false,
        editor: {
            xtype: 'textfield'
        }
    }, {
        text: CMDBuildUI.locales.Locales.administration.systemconfig.value,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.systemconfig.value'
        },
        dataIndex: 'level',
        sortable: false,
        editor: {
            xtype: 'combo',
            queryMode: 'local',
            displayField: 'label',
            valueField: 'value',
            bind: {
                store: '{logSettingValuesStore}'
            }
        },
        renderer: function (value) {
            var store = this.lookupViewModel().get('logSettingValuesStore');
            var record = store.findRecord('value', value);
            if (record) {
                return record.get('label');
            }
            return value;
        }
    }],
    tbar: [{
        ui: 'administration-action-small',
        xtype: 'button',
        iconCls: 'x-fa fa-plus',
        text: CMDBuildUI.locales.Locales.administration.systemconfig.addcustomconfig,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.systemconfig.addcustomconfig'
        },
        disabled: true,
        bind: {
            disabled: '{!theSession.rolePrivileges.admin_sysconfig_modify}'
        },
        itemId: 'addConfigRow'
    }, {
        xtype: 'button',
        ui: 'administration-action-small',
        text: CMDBuildUI.locales.Locales.administration.systemconfig.viewlogs,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.systemconfig.viewlogs'
        },
        iconCls: 'x-fa fa-th-list',
        handler: 'onViewLogsBtnClick',
        autoEl: {
            'data-testid': 'administration-systemconfig-system-viewlogs_button'
        }
    }, {
        xtype: 'button',
        ui: 'administration-action-small',
        text: CMDBuildUI.locales.Locales.administration.systemconfig.downloadlogs,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.systemconfig.downloadlogs'
        },
        iconCls: 'x-fa fa-download',
        handler: 'onDownloadLogsBtnClick',
        autoEl: {
            'data-testid': 'administration-systemconfig-system-downloadlogs_button'
        }
    }, {
        xtype: 'tbfill'
    }, {
        xtype: 'tool',
        itemId: 'editAttributeBtn',
        iconCls: 'x-fa fa-pencil',
        tooltip: CMDBuildUI.locales.Locales.administration.common.actions.edit,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.edit'
        },
        callback: 'onEditSetupBtnClick',
        cls: 'administration-tool',
        hidden: true,
        disabled: true,
        bind: {
            hidden: '{!actions.view}',
            disabled: '{!theSession.rolePrivileges.admin_sysconfig_modify}'
        },
        autoEl: {
            'data-testid': 'administration-setup-view-editBtn'
        }
    }],
    dockedItems: [{
        xtype: 'toolbar',
        itemId: 'bottomtoolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(false)
    }]
});