
Ext.define('CMDBuildUI.view.events.notes.Notes', {
    extend: 'Ext.form.Panel',
    alias: 'widget.events-notes-notes',
    requires: [
        'CMDBuildUI.view.events.notes.NotesController'
    ],

    controller: 'events-notes-notes',
    viewModel: {
        type: 'notes-panel'
    },

    modelValidation: true,

    config: {
        /**
         * @cfg {Boolean} editMode
         */
        editMode: false,

        /**
         * @cfg {readOnly} 
         * 
         * Set to `true` to shwow details tabs in read-only mode.
         */
        readOnly: false,

        theEvent: null
    },

    publishes: ['theEvent'],
    layout: 'fit',

    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.notes.edit,
        itemId: 'editbtn',
        iconCls: 'x-fa fa-pencil',
        ui: 'management-action-small',
        hidden: true,
        disabled: true,
        bind: {
            hidden: '{hiddenbtns.edit}',
            disabled: '{!basepermissions.edit}'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.notes.edit'
        },
        autoEl: {
            'data-testid': 'notes-panel-editbtn'
        }
    }],

    reference: 'events-notes-notes',

    items: [
        CMDBuildUI.util.helper.FieldsHelper.getHTMLEditor({
            flex: 1,
            bind: {
                value: '{events-notes-notes.theEvent.notes}',
                hidden: '{!editmode}'
            }
        }), {
            xtype: 'panel',
            scrollable: true,
            cls: 'x-selectable',
            bind: {
                html: '{events-notes-notes.theEvent.notes}',
                hidden: '{editmode}'
            }
        }
    ],

    buttons: [{
        text: CMDBuildUI.locales.Locales.common.actions.save,
        itemId: 'savebtn',
        ui: 'management-action-small',
        formBind: true, //only enabled once the form is valid
        disabled: true,
        bind: {
            hidden: '{hiddenbtns.save}'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.save'
        },
        autoEl: {
            'data-testid': 'notes-panel-savebtn'
        }
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        itemId: 'cancelbtn',
        ui: 'secondary-action-small',
        bind: {
            hidden: '{hiddenbtns.cancel}'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        },
        autoEl: {
            'data-testid': 'notes-panel-cancelbtn'
        }
    }]
});
