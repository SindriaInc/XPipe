Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.group.Properties', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.group.PropertiesController',
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.group.fieldsets.LimitedAdminPermissionsFieldset'
    ],

    alias: 'widget.administration-content-groupsandpermissions-tabitems-group-properties',
    controller: 'administration-content-groupsandpermissions-tabitems-group-properties',
    autoScroll: false,
    modelValidation: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    layout: 'border',
    items: [{
        xtype: 'components-administration-toolbars-formtoolbar',
        region: 'north',
        bind: {
            hidden: '{actions.view}'
        },
        items: [{
            xtype: 'button',
            itemId: 'spacer',
            style: {
                "visibility": "hidden"
            }
        }]
    }, {
        xtype: 'components-administration-toolbars-formtoolbar',
        region: 'north',
        bind: {
            hidden: '{!actions.view}'
        },
        items: [{
            xtype: 'button',
            itemId: 'spacer',
            style: {
                "visibility": "hidden"
            }
        }, {
            xtype: 'tbfill'
        }, {
            xtype: 'tool',
            align: 'right',
            itemId: 'editBtn',
            cls: 'administration-tool',
            iconCls: 'x-fa fa-pencil',
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.edit,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.edit'
            },
            callback: 'onEditBtnClick',
            hidden: true,
            autoEl: {
                'data-testid': 'administration-groupandpermission-group-tool-editbtn'
            },
            bind: {
                hidden: '{toolbarHiddenButtons.edit}',
                disabled: '{!toolAction._canUpdate}'
            }
        }, {
            xtype: 'tool',
            align: 'right',
            itemId: 'disableBtn',
            cls: 'administration-tool',
            iconCls: 'x-fa fa-ban',
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.disable,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.disable'
            },
            callback: 'onToggleEnableBtnClick',
            hidden: true,
            autoEl: {
                'data-testid': 'administration-groupandpermission-group-tool-disablebtn'
            },
            bind: {
                hidden: '{toolbarHiddenButtons.disable}',
                disabled: '{!toolAction._canActiveToggle}'
            }
        }, {

            xtype: 'tool',
            align: 'right',
            itemId: 'enableBtn',
            hidden: true,
            cls: 'administration-tool',
            iconCls: 'x-fa fa-check-circle-o',
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.enable,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.enable'
            },
            callback: 'onToggleEnableBtnClick',
            autoEl: {
                'data-testid': 'administration-groupandpermission-group-tool-enablebtn'
            },
            bind: {
                hidden: '{toolbarHiddenButtons.enable}'
            }
        }]
    }, {
        xtype: 'panel',
        region: 'center',
        scrollable: 'y',
        items: [{
            xtype: 'administration-content-groupsandpermissions-tabitems-group-fieldsets-generaldatafieldset'
        }, {
            xtype: 'administration-content-groupsandpermissions-tabitems-group-fieldsets-limitedadminpermissionsfieldset',
            itemId: 'limitedadminpermissionsfieldset',
            hidden: true,
            bind: {
                hidden: '{theGroup.type != \'admin_limited\'}'
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
    }]
});