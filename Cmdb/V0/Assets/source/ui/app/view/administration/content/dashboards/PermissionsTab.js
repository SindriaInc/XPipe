

Ext.define('CMDBuildUI.view.administration.content.dashboards.PermissionsTab', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-dashboards-permissionstab',
    requires: [
        'CMDBuildUI.view.administration.content.dashboards.PermissionsTabController',
        'CMDBuildUI.view.administration.content.dashboards.PermissionsTabModel'
    ],

    controller: 'administration-content-dashboards-permissionstab',
    viewModel: {
        type: 'administration-content-dashboards-permissionstab'
    },
    scrollable: 'y',
    loadMask: true,
    defaults: {
        textAlign: 'left',
        scrollable: true
    },

    items: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',
        objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.dashboard,
        gridType: 'object',
        bind: {
            store: '{grantsChainedStore}'
        }
    }],
    dockedItems: [{
        xtype: 'components-administration-toolbars-formtoolbar',
        items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
            edit: true
        })
    }, {
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view }'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons({ formBind: false })
    }]
});