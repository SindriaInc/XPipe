
Ext.define('CMDBuildUI.view.events.event.Create', {
    extend: 'Ext.form.Panel',
    alias: 'widget.events-event-create',
    requires: [
        'CMDBuildUI.view.events.event.CreateController',
        'CMDBuildUI.view.events.event.CreateModel'
    ],

    mixins: [
        'CMDBuildUI.view.events.event.Mixin'
    ],

    config: {
        theEvent: null
    },
    publishes: 'theEvent',
    reference: 'events-event-create',

    controller: 'events-event-create',
    viewModel: {
        type: 'events-event-create'
    },

    modelValidation: true,
    autoScroll: true,
    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,

    formmode: CMDBuildUI.util.helper.FormHelper.formmodes.create,

    buttons: [{
        text: CMDBuildUI.locales.Locales.common.actions.save,
        formBind: true, //only enabled once the form is valid
        disabled: true,
        itemId: 'savebtn',
        ui: 'management-action-small',
        autoEl: {
            'data-testid': 'card-create-save'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.save'
        }
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.saveandclose,
        formBind: true, //only enabled once the form is valid
        disabled: true,
        itemId: 'saveandclosebtn',
        ui: 'management-action-small',
        autoEl: {
            'data-testid': 'card-create-saveandclose'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.saveandclose'
        }
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        itemId: 'cancelbtn',
        ui: 'secondary-action-small',
        autoEl: {
            'data-testid': 'card-create-cancel'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        }
    }],

    /**
    * same function in CMDBuildUI.view.fields.schedulerdate.ViewController
    */
    generateForm: function () {
        var view = this,
            model = Ext.ClassManager.get('CMDBuildUI.model.calendar.Event');
        this.readonly = false;

        var panel = CMDBuildUI.util.helper.FormHelper.renderForm(model, {
            readonly: false,
            linkName: 'events-event-create.theEvent',
            showAsFieldsets: true,
            layout: this.getFormLayout(),
            mode: CMDBuildUI.util.helper.FormHelper.formmodes.create
        });

        var missingDays = panel[0]._cmdbuildFields[this._missingdays_row_index].items[1].items[0];
        Ext.apply(missingDays, this.getMissingDaysExtraConf());

        // adds the partecipants combobox
        panel[0]._cmdbuildFields.splice(this._partecipantGroup_row_index, 0, this.getUserGroupParticipantsFields());

        // adds the notification_template combo
        panel[0]._cmdbuildFields[this._notification_delay_row_index].items[0].items.push(this.getNotificationTemplateComboField());

        // adds the notification text textfield
        panel[0]._cmdbuildFields[this._notificationText_row_index].items[0].items.push(this.getNotificationContentField());

        // adds the daysAdvanceNotification text
        panel[0]._cmdbuildFields[this._notification_delay_row_index].items[1].items.push(this.getDaysAdvanceNofificationField());

        if (this.getTheEvent().isManual() && !Ext.isEmpty(CMDBuildUI.util.helper.Configurations.get('cm_system_scheduler_selectableclasses'))) {
            //adds class combobox
            panel[0]._cmdbuildFields[this._class_row_index].items[0].items.push(this.getClassCombobox());
        }

        view.removeAll();
        view.add(this.getMainPanelForm(panel));
    }
});
