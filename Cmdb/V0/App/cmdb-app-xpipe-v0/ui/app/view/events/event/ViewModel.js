Ext.define('CMDBuildUI.view.events.event.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.events-event-view',
    data: {
        hiddenbtns: {
            open: false
        },
        title: null
    },

    formulas: {
        updatePermissions: {
            bind: {
                can_write: '{events-event-view.theEvent._can_write}'
            },
            get: function (data) {
                if (this.getParent().type === "events-grid") {
                    this.set("hiddentools", {
                        edit: false,
                        delete: false,
                        open: false
                    });
                    this.set("permissions", {
                        delete: data.can_write,
                        edit: data.can_write
                    });
                } else {
                    this.getParent().set("permissions", {
                        delete: data.can_write,
                        edit: data.can_write
                    });
                }
            }
        },
        theObject: {
            bind: '{events-event-view.theEvent}',
            get: function (object) {
                return object;
            }
        },

        updateCounter: {
            bind: {
                theEvent: '{events-event-view.theEvent}'
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
