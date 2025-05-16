Ext.define('CMDBuildUI.view.widgets.notewidget.Panel', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.widgets.notewidget.PanelController'
    ],
    mixins: [
        'CMDBuildUI.view.widgets.Mixin'
    ],

    alias: 'widget.widgets-notewidget-panel',
    controller: 'widgets-notewidget-panel',
    viewModel: {
        data: {
            editmode: false,
            notes: null
        }
    },

    layout: 'fit',
    bodyPadding: 15,

    defaults: {
        textAlign: 'left',
        scrollable: true,
        border: false
    },

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{theWidget._inline}'
        },
        items: [{
            xtype: 'tbfill'
        }, {
            ui: 'secondary-action-small',
            itemId: 'closebtn',
            hidden: true,
            text: CMDBuildUI.locales.Locales.common.actions.close,
            bind: {
                hidden: '{editmode}'
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.common.actions.close'
            },
            autoEl: {
                'data-testid': 'widgets-notewidget-close'
            }
        }, {
            text: CMDBuildUI.locales.Locales.common.actions.save,
            itemId: 'savebtn',
            ui: 'management-action-small',
            formBind: true, //only enabled once the form is valid
            disabled: true,
            hidden: true,
            bind: {
                hidden: '{!editmode}'
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.common.actions.save'
            },
            autoEl: {
                'data-testid': 'widgets-notewidget-save'
            }
        }, {
            text: CMDBuildUI.locales.Locales.common.actions.cancel,
            itemId: 'cancelbtn',
            ui: 'secondary-action-small',
            hidden: true,
            bind: {
                hidden: '{!editmode}'
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
            },
            autoEl: {
                'data-testid': 'widgets-notewidget-cancel'
            }
        }]
    }]
});