Ext.define('CMDBuildUI.view.events.TabPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.events-tabpanel',
    data: {
        action: undefined,
        basepermissions: {
            delete: false,
            edit: false
        },
        disabled: {
            attachments: true,
            email: true,
            history: true,
            notes: true
        },
        permissions: {
            delete: false,
            edit: false
        },
        hiddentools: {
            delete: true,
            edit: true,
            open: true
        },
        tabcounters: {
            attachments: undefined,
            emails: undefined,
            notes: null
        },
        'events-tabpanel': {
            eventId: undefined
        }
    },

    formulas: {
        createLink: {
            bind: '{events-tabpanel.eventId}',
            get: function (eventId) {
                var parentModel = Ext.ClassManager.get('CMDBuildUI.model.calendar.Event');
                parentModel.setProxy({
                    type: 'baseproxy',
                    url: '/calendar/events'
                });
                if (eventId) {

                    this.setLinks({
                        theLink: {
                            type: 'CMDBuildUI.model.calendar.Event',
                            id: eventId
                        }
                    });
                } else {
                    this.setLinks({
                        theLink: {
                            type: 'CMDBuildUI.model.calendar.Event',
                            create: {
                                _id: null,
                                source: CMDBuildUI.model.calendar.Sequence.source.user,
                                eventEditMode: CMDBuildUI.model.calendar.Event.eventEditMode.write,
                                onCardDeleteAction: CMDBuildUI.model.calendar.Calendar.onCardDeleteAction.clear,
                                type: CMDBuildUI.model.calendar.Trigger.eventtypes.instant,
                                status: CMDBuildUI.model.calendar.Event.status.active
                            }
                            // create: true
                        }
                    })
                }
            }
        },

        updatePermissions: {
            bind: {
                action: '{action}',
                theLink: '{theLink}'
                // objectTypeName: '{objectTypeName}'
            },
            get: function (data) {
                if (
                    data.action !== CMDBuildUI.mixins.DetailsTabPanel.actions.create
                ) {
                    this.set("disabled.attachments", false);
                    this.set("disabled.email", false);
                    this.set("disabled.history", false);
                    this.set("disabled.notes", false);
                }
                // var item = CMDBuildUI.util.helper.ModelHelper.getClassFromName(data.objectTypeName);
                this.set("basepermissions", {
                    delete: data.theLink.get('_can_write'),//item.get(CMDBuildUI.model.base.Base.permissions.delete),
                    edit: data.theLink.get('_can_write')//item.get(CMDBuildUI.model.base.Base.permissions.edit)
                });
            }
        },

        updateWindowTitle: {
            bind: '{theLink.description}',
            get: function (description) {
                this.set('titledata.type', CMDBuildUI.locales.Locales.calendar.event);
                this.set('titledata.item', CMDBuildUI.util.helper.FieldsHelper.renderTextField(description, {
                    skipnewline: true
                }));
            }
        },

        updateToolsVisibility: {
            bind: {
                action: '{action}',
                activetab: '{activetab}'
            },
            get: function (data) {
                if (data.action === CMDBuildUI.mixins.DetailsTabPanel.actions.readonly) {
                    var isview = data.activetab === CMDBuildUI.mixins.DetailsTabPanel.actions.view,
                        permissionTools = this.get("permissionTools") ? this.get("permissionTools") : { edit: true, delete: true };
                    this.set("hiddentools", {
                        edit: !isview || !permissionTools.edit,
                        delete: !isview || !permissionTools.delete,
                        open: false
                    });
                } else {
                    this.set("hiddentools", {
                        edit: false,
                        delete: false,
                        open: true
                    });
                }
            }
        }
    }
});
