Ext.define('CMDBuildUI.mixins.routes.management.Events', {
    mixinId: 'managementroutes-events-mixin',


    beforeShowEvents: function (action) {
        if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.scheduler.enabled) == false) {
            CMDBuildUI.util.Utilities.redirectTo("management");
            action.stop();
        }

        //removes the detail window
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);

        //removes action from the context
        CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(null);

        var type = CMDBuildUI.util.helper.ModelHelper.objecttypes.event;
        if (CMDBuildUI.util.Navigation.checkCurrentContext(type, type)) {

            //check if the objectid is null
            if (!CMDBuildUI.util.Navigation.checkCurrentManagementContextObjectId(null)) {

                //fires global event
                Ext.GlobalEvents.fireEventArgs("objectidchanged", [null]);

                //updates the context variables
                CMDBuildUI.util.Navigation.updateCurrentManagementContextObjectId(null);
            }

            action.stop();
        } else {
            action.resume();
        }
    },

    /**
     * 
     * @param {String} eventId 
     */
    showEvents: function (eventId) {
        var type = CMDBuildUI.util.helper.ModelHelper.objecttypes.event;
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);

        CMDBuildUI.util.Navigation.addIntoManagemenetContainer('events-container', {
            selectedId: eventId
        });

        // update current context
        CMDBuildUI.util.Navigation.updateCurrentManagementContext(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.event,
            CMDBuildUI.util.helper.ModelHelper.objecttypes.event,
            eventId
        );

        // fire global event objecttypechanged
        Ext.GlobalEvents.fireEventArgs("objecttypechanged", [type]);
    },


    beforeShowEvent: function (eventId, action) {
        //removes the detail window
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);

        //removes action from the context
        CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(null);

        var type = CMDBuildUI.util.helper.ModelHelper.objecttypes.event;
        if (CMDBuildUI.util.Navigation.checkCurrentContext(type, type)) {

            if (!CMDBuildUI.util.Navigation.checkCurrentManagementContextObjectId(eventId)) {

                //updates the context variables
                CMDBuildUI.util.Navigation.updateCurrentManagementContextObjectId(eventId);

                //fires global event
                Ext.GlobalEvents.fireEventArgs("objectidchanged", [eventId]); //it's not corect firing this event allways
            }
            action.stop();
        } else {
            action.resume();
        }
    },
    /**
     * 
     * @param {String} eventId 
     */
    showEvent: function (eventId) {
        this.showEvents(eventId)
    },

    onBeforeShowEventWindow: function (eventId, action) {
        if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.scheduler.enabled) == false) {
            CMDBuildUI.util.Utilities.redirectTo("management");
            action.stop();
        }

        // fix variables for create form
        if (!action) {
            action = eventId;
            eventId = null;
        }

        var type = CMDBuildUI.util.helper.ModelHelper.objecttypes.event;
        if (!CMDBuildUI.util.Navigation.checkCurrentContext(type, type)) {
            this.showEvents(eventId);

        } else {

            //check if the objectid is null
            if (!CMDBuildUI.util.Navigation.checkCurrentManagementContextObjectId(eventId)) {

                //fires global event
                Ext.GlobalEvents.fireEventArgs("objectidchanged", [eventId]);

                //updates the context variables
                CMDBuildUI.util.Navigation.updateCurrentManagementContextObjectId(eventId);
            }
        }
        action.resume();
    },
    showEventView: function (eventId) {
        this.showEventTabPanel(eventId, CMDBuildUI.mixins.DetailsTabPanel.actions.view)
    },

    showEventEdit: function (eventId) {
        this.showEventTabPanel(eventId, CMDBuildUI.mixins.DetailsTabPanel.actions.edit);
    },

    showEventNotes: function (eventId) {
        var privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");
        if (privileges.card_tab_note_access_read) {
            this.showEventTabPanel(eventId, CMDBuildUI.mixins.DetailsTabPanel.actions.notes);
        } else {
            this.redirectTo(Ext.String.format("events/{0}", eventId));
        }
    },
    showEventHistory: function (eventId) {
        var privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");
        if (privileges.card_tab_history_access_read) {
            this.showEventTabPanel(eventId, CMDBuildUI.mixins.DetailsTabPanel.actions.history);
        } else {
            this.redirectTo(Ext.String.format("events/{0}", eventId));
        }
    },

    showEventAttachments: function (eventId) {
        var privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");
        if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.enabled) && privileges.card_tab_attachment_access_read) {
            this.showEventTabPanel(eventId, CMDBuildUI.mixins.DetailsTabPanel.actions.attachments);
        } else {
            this.redirectTo(Ext.String.format("events/{0}", eventId), eventId);
        }
    },
    showEventCreate: function (eventId) {
        this.showEventTabPanel(null, CMDBuildUI.mixins.DetailsTabPanel.actions.create);
    },
    showEventEmails: function (eventId) {
        var privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");
        if (privileges.card_tab_email_access_read) {
            this.showEventTabPanel(eventId, CMDBuildUI.mixins.DetailsTabPanel.actions.emails);
        } else {
            this.redirectTo(Ext.String.format("events/{0}", eventId));
        }
    },
    // showEventDetails: function (eventid) {},
    privates: {
        showEventTabPanel: function (eventId, action) {
            if (!CMDBuildUI.util.Navigation.checkCurrentManagementContextAction(action)) {
                CMDBuildUI.util.Navigation.addIntoManagementDetailsWindow('events-tabpanel', {
                    eventId: eventId,
                    viewModel: {
                        data: {
                            action: action,
                            objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.calendar,
                            objectTypeName: CMDBuildUI.util.helper.ModelHelper.objecttypes.event
                        }
                    }
                });
                CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(action);
            }
        }
    }
})