Ext.define('CMDBuildUI.view.events.event.EditModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.events-event-edit',

    formulas: {
        theObject: {
            bind: '{events-event-edit.theEvent}',
            get: function (object) {
                return object;
            }
        },

        updateCounter: {
            bind: {
                theEvent: '{events-event-edit.theEvent}'
            },
            get: function (data) {
                if (data.theEvent) {
                    var vm = this.getParent();

                    vm.set('tabcounters.attachments', data.theEvent.get('_attachment_count'));
                    vm.set('tabcounters.emails', data.theEvent.get('_email_count'));
                    vm.set('tabcounters.notes', CMDBuildUI.util.Utilities.extractTextFromHTML(data.theEvent.get('notes') || null));
                }
            }
        }
    }

});
