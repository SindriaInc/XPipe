Ext.define('CMDBuildUI.view.fields.schedulerdate.SequenceFieldSetController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.fields-schedulerdate-sequencefieldset',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        vm.bind('{theSequence}', this.formRender, view, { single: true });
    },

    /**
     * 
     * @param {Ext.data.Model} theSequence  the sequence vm variable
     */
    formRender: function (theSequence) {
        var view = this;
        var readonly;

        switch (theSequence.get('sequenceParamsEditMode')) {
            case CMDBuildUI.model.calendar.Sequence.sequenceParamsEditMode.read:
                readonly = true;
                break;

            case CMDBuildUI.model.calendar.Sequence.sequenceParamsEditMode.write:
                readonly = false;
                break;
            default:
                CMDBuildUI.util.Notifier.showErrorMessage("can't reconize sequenceParamsEditMode value");
                break;
        }

        var model = Ext.ClassManager.get('CMDBuildUI.model.calendar.Sequence');
        var form = CMDBuildUI.util.helper.FormHelper.renderForm(model, {
            readonly: readonly,
            linkName: 'theSequence',
            layout: CMDBuildUI.model.calendar.Sequence.getFormLayout()
        });

        //special function for the endType lookup
        if (!readonly) {
            view.getController().formManipulation(form);
        }
        view.lookupReference('form').insert(0, form);
    },

    recalculateEvents: function () {
        var vm = this.getViewModel();
        var theSequence = vm.get('theSequence');
        var data = theSequence.getData();

        if (Ext.isEmpty(data.notifications___0___template)) {
            delete data.notifications___0___template;
            delete data.notifications___0___delay;
            delete data.notifications___0___content;
        }

        Ext.Ajax.request({
            method: 'POST',
            jsonData: data,
            url: Ext.String.format('{0}/calendar/sequences/_ANY/generate-events', CMDBuildUI.util.Config.baseUrl),
            callback: function (options, success, response) {
                var eventsData = JSON.parse(response.responseText).data;
                //sets the _dirty_recalculate so that the dirty propery can't change no more
                theSequence._dirty_recalculate = true;
                theSequence.events().setData(eventsData);
            }
        })
    },

    /**
     * 
     * @param {*} button 
     * @param {*} eOpts 
     */
    onAddButtonClick: function (button, eOpts) {
        var view = this.getView();
        var vm = view.lookupViewModel();
        var sequence = vm.get('theSequence');

        //generate the new event from the sequence (client side)
        var newEvent = this.generateEvent(sequence);

        //open the createpopup
        var popup = CMDBuildUI.util.Utilities.openPopup(
            null,
            CMDBuildUI.locales.Locales.calendar.sequencepaneltitle,
            {
                xtype: 'events-event-create',
                controller: 'fields-schedulerdate-create',
                theEvent: newEvent,
                listeners: {
                    popupclose: function () {
                        popup.close();
                    },
                    popupsave: function (theEvent) {
                        var events = sequence.events();
                        events.add(theEvent);
                        sequence._dirty_create = true;
                        popup.close();
                    }
                }
            }
        );

    },

    privates: {

        /**
         * 
         * @param {CMDBuildUI.model.calendar.Sequence} sequence
         * @returns {CMDBuildUI.model.calendar.Event} 
         */
        generateEvent: function (sequence) {
            return Ext.create('CMDBuildUI.model.calendar.Event', {
                type: sequence.get('eventType'),
                source: CMDBuildUI.model.calendar.Sequence.source.system,
                begin: sequence.get('firstEvent'),
                date: sequence.get('firstEvent'),
                end: sequence.get('firstEvent'),
                timeZone: sequence.get('timeZone'),
                card: sequence.get('card'),
                description: sequence.get('description'),
                eventEditMode: sequence.get('eventEditMode'),
                job: sequence.get('job'),
                onCardDeleteAction: sequence.get('onCardDeleteAction'),
                status: CMDBuildUI.model.calendar.Event.status.active,
                participants: sequence.get('participants'),
                _participant_users: sequence.get('_participant_users'),
                _participant_groups: sequence.get('_participant_groups'),
                category: sequence.get('category'),
                priority: sequence.get('priority'),

                notifications___0____id: sequence.get('notifications___0____id'),
                notifications___0___template: sequence.get('notifications___0___template'),
                notifications___0___content: sequence.get('notifications___0___content'),
                notifications___0___delay: sequence.get('notifications___0___delay'),

                _can_write: true
                // _notification_reports: sequence.get('_notification_reports'),
                // _notification__report: sequence.get('_notification__report'),
                // _notification__content_preview: sequence.get('_notification__content_preview')

            })
        },

        formManipulation: function (form) {
            var lookupfield = form.items[0].items[2].items[0].items[0];

            lookupfield.controller = {
                type: 'fields-lookupfield',
                control: {
                    '#combo0': {
                        beforerender: function (combobox, eOpts) {
                            combobox.onStoreLoaded = function (store, records, successful, operation, eOpts) {
                                var filtered = records.filter(function (value, index, records) {
                                    if (value.get('code') != 'auto') {
                                        return true;
                                    }
                                });
                                store.setData(filtered);
                            }
                        }
                    }
                }
            }
        }
    }
});
