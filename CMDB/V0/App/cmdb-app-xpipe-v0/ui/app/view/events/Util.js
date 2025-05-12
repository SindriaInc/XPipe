Ext.define('CMDBuildUI.view.events.Util', {
    singleton: true,

    getTools: function () {
        return [{
            // open tool
            xtype: 'tool',
            itemId: 'opentool',
            reference: 'opentool',
            iconCls: 'x-fa fa-external-link',
            cls: 'management-tool',
            action: 'view',
            hidden: true,
            tooltip: CMDBuildUI.locales.Locales.classes.cards.opencard,
            autoEl: {
                'data-testid': 'events-event-tool-open'
            },
            bind: {
                hidden: '{hiddentools.open}'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.classes.cards.opencard'
            }
        }, {
            // edit tool
            xtype: 'tool',
            itemId: 'editBtn',
            iconCls: 'x-fa fa-pencil',
            cls: 'management-tool',
            hidden: true,
            // disabled: true,
            tooltip: CMDBuildUI.locales.Locales.classes.cards.modifycard,
            autoEl: {
                'data-testid': 'events-event-tool-edit'
            },
            bind: {
                hidden: '{hiddentools.edit}'
                // disabled: '{!permissions.edit}'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.classes.cards.modifycard'
            }
        }, {
            // delete tool
            xtype: 'tool',
            itemId: 'deleteBtn',
            iconCls: 'x-fa fa-trash',
            cls: 'management-tool',
            hidden: true,
            disabled: true,
            tooltip: CMDBuildUI.locales.Locales.classes.cards.deletecard,
            autoEl: {
                'data-testid': 'events-event-tool-delete'
            },
            bind: {
                hidden: '{hiddentools.delete}',
                disabled: '{!permissions.delete}'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.classes.cards.deletecard'
            }
        }];
    }
});