
Ext.define('CMDBuildUI.view.events.event.Edit', {
    extend: 'Ext.form.Panel',
    alias: 'widget.events-event-edit',

    requires: [
        'CMDBuildUI.view.events.event.EditController',
        'CMDBuildUI.view.events.event.EditModel'
    ],

    mixins: [
        'CMDBuildUI.view.events.event.Mixin'
    ],

    controller: 'events-event-edit',
    viewModel: {
        type: 'events-event-edit'
    },

    config: {
        theEvent: null,
        hideTools: false
    },
    publishes: 'theEvent',
    reference: 'events-event-edit',

    layout: {
        type: 'vbox',
        align: 'stretch' //stretch vertically to parent
    },

    formmode: CMDBuildUI.util.helper.FormHelper.formmodes.update,

    modelValidation: true,
    autoScroll: true,
    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,

    buttons: [{
        text: CMDBuildUI.locales.Locales.common.actions.save,
        disabled: true,
        formBind: true,
        ui: 'management-action-small',
        itemId: 'savebtn',
        autoEl: {
            'data-testid': 'event-edit-save'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.save'
        }
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.saveandclose,
        disabled: true,
        formBind: true,
        ui: 'management-action-small',
        itemId: 'saveandclosebtn',
        autoEl: {
            'data-testid': 'event-edit-saveandclose'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.saveandclose'
        }
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        ui: 'secondary-action-small',
        itemId: 'cancelbtn',
        autoEl: {
            'data-testid': 'event-edit-cancel'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        }
    }],

    /**
    *
    */
    generateForm: function (extraCols) {
        var view = this,
            model = Ext.ClassManager.get('CMDBuildUI.model.calendar.Event');
        this.readonly = view.getTheEvent().get('_can_write') ? false : true;

        var panel = CMDBuildUI.util.helper.FormHelper.renderForm(model, {
            readonly: this.readonly,
            linkName: 'events-event-edit.theEvent',
            showAsFieldsets: true,
            layout: this.getFormLayout()
        });

        if (!view.getHideTools()) {
            Ext.apply(panel, {
                tools: view.tabpaneltools
            });
        }

        /**
         * this is an extra field added to make possible change the status of the event
         */
        if (extraCols) {
            panel[0]._cmdbuildFields.splice(6, 0, extraCols);
        }

        var missingDays = panel[0]._cmdbuildFields[this._missingdays_row_index].items[1].items[0]
        Ext.apply(missingDays, this.getMissingDaysExtraConf());

        // adds the partecipants combobox
        panel[0]._cmdbuildFields.splice(this._partecipantGroup_row_index, 0, this.getUserGroupParticipantsFields());

        // adds the notification_template combo
        panel[0]._cmdbuildFields[this._notification_delay_row_index].items[0].items.push(this.getNotificationTemplateComboField());

        // adds the notification text textfield
        panel[0]._cmdbuildFields[this._notificationText_row_index].items[0].items.push(this.getNotificationContentField());

        // adds the daysAdvanceNotification text
        var daysAdvanceNotification = this.getDaysAdvanceNofificationField();
        if (this.readonly == false) {
            panel[0]._cmdbuildFields[this._notification_delay_row_index].items[1].items.push(daysAdvanceNotification);
        } else if (this.readonly == true) {
            panel[0]._cmdbuildFields[this._notification_delay_row_index].items[0].items.push(daysAdvanceNotification);
        }

        // adds the operation combo
        panel[0]._cmdbuildFields[this._operation_row_index].items[0].items.push(this.getOperationField());

        var theEvent = this.getTheEvent();
        if (theEvent.isManual() && !Ext.isEmpty(CMDBuildUI.util.helper.Configurations.get('cm_system_scheduler_selectableclasses'))) {
            //adds class combobox
            panel[0]._cmdbuildFields[this._class_row_index].items[0].items.push(this.getClassCombobox());

            if (theEvent.get('card')) {
                panel[0]._cmdbuildFields[this._class_row_index].items[1].items.push(this.getCardCombobox());
            }
        }

        if (this.readonly) {

            //add editor for calculate field
            var completitionField = panel[0]._cmdbuildFields[this._operation_row_index].items[1].items[0];
            Ext.merge(completitionField, CMDBuildUI.util.helper.FormHelper.getEditorForField(
                CMDBuildUI.model.calendar.Event.getField('completion'), {
                linkName: 'events-event-edit.theEvent'
            }));

            if (view.getTheEvent().get("status") != "completed" && view.getTheEvent().get("status") != "canceled") {
                //hide status field
                delete panel[0]._cmdbuildFields[this._operation_row_index].items[0].items[0].updateFieldVisibility;
                panel[0]._cmdbuildFields[this._operation_row_index].items[0].items[0].hidden = true;
            }
        }
        view.removeAll();
        view.add(this.getMainPanelForm(panel));
    }
});
