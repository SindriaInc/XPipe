Ext.define('CMDBuildUI.view.dms.Util', {
    singleton: true,

    getTools: function () {
        return [{
            // edit tool
            xtype: 'tool',
            itemId: 'editBtn',
            iconCls: 'x-fa fa-pencil',
            cls: 'management-tool',
            hidden: true,
            disabled: true,
            tooltip: CMDBuildUI.locales.Locales.attachments.editattachment,
            autoEl: {
                'data-testid': 'dms-container-tool-edit'
            },
            handler: 'onEditToolClick',
            bind: {
                hidden: '{dms-container.readOnly}',
                disabled: '{!basepermissions.edit || !record._can_update}'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.attachments.editattachment'
            }
        }, {
            // delete tool
            xtype: 'tool',
            iconCls: 'x-fa fa-trash',
            cls: 'management-tool',
            hidden: true,
            disabled: true,
            tooltip: CMDBuildUI.locales.Locales.attachments.deleteattachment,
            autoEl: {
                'data-testid': 'dms-container-tool-delete'
            },
            handler: 'onDeleteToolClick',
            bind: {
                hidden: '{dms-container.readOnly}',
                disabled: '{!record._can_delete}'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.attachments.deleteattachment'
            }
        }, {
            // download tool
            xtype: 'tool',
            iconCls: 'x-fa fa-download',
            cls: 'management-tool',
            hidden: true,
            // disabled: true,
            tooltip: CMDBuildUI.locales.Locales.attachments.download,
            autoEl: {
                'data-testid': 'dms-container-tool-delete'
            },
            handler: 'onDownloadToolClick',
            bind: {
                hidden: '{dms-container.readOnly}',
                disabled: '{isRecordPhantom}'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.attachments.download'
            }
        }];
    },

    getHelpTool: function (config) {
        return Ext.merge({
            // help tool
            xtype: 'tool',
            helpValue: null,
            iconCls: 'x-fa fa-question-circle',
            itemId: 'helpBtn',
            cls: 'management-tool no-action',
            tooltip: CMDBuildUI.locales.Locales.common.actions.help,
            autoEl: {
                'data-testid': 'dms-container-tool-help'
            },
            handler: function () {
                CMDBuildUI.util.Utilities.openPopup(
                    null,
                    CMDBuildUI.locales.Locales.common.actions.help, {
                        xtype: 'panel',
                        html: this.helpValue,
                        layout: 'fit',
                        padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                        cls: 'x-selectable',
                        scrollable: true
                    });
            },
            // hidden: true,
            bind: {
                helpValue: 'default'
            },
            setHelpValue: function (value) {
                this.helpValue = value;

                this.setVisible(!Ext.isEmpty(value));
                this.setZIndex(1);
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.common.actions.help'
            }
        }, config);
    }
});