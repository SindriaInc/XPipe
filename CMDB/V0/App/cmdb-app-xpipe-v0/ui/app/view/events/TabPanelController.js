Ext.define('CMDBuildUI.view.events.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.events-tabpanel',
    control: {
        '#': {
            beforerender: 'onBeforeRender',
            tabchange: 'onTabChange'
        },
        '#opentool': {
            click: 'onOpenToolClick'
        },
        '#editBtn': {
            click: 'onEditButton'
        },
        '#deleteBtn': {
            click: 'onDeleteBtn'
        }
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.TabPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        // set view model variables
        var vm = this.getViewModel();
        var readonly = view.getReadOnlyTabs();
        var privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");

        // set objectTypeName and objectId for inline-view
        if (!vm.get("events-grid.selectedId")) {
            var config = view.getInitialConfig();
            if (!Ext.isEmpty(config._rowContext)) {
                var record = config._rowContext.record; // get widget record
                if (record && record.getData()) {
                    vm.set("events-grid.selectedId", record.getId());
                }
            }
        }

        var action;
        if (readonly) {
            action = CMDBuildUI.util.Navigation.getCurrentRowTab();
            vm.set("action", CMDBuildUI.mixins.DetailsTabPanel.actions.readonly);
        } else {
            action = vm.get('action')
        }

        if (view.isInDetailWindow()) {
            view.mon(CMDBuildUI.util.Navigation.getManagementDetailsWindow(), 'beforeclose', this.onManagementDetailsWindowBeforeClose, this);
        } else {
            var height = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.inlinecard.height);
            view.setHeight(view.up().getHeight() * height / 100);
        }

        //init tabs
        var configAttachments = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.enabled);
        var card, tabcard, tabnotes, tabhistory, tabemails, tabattachments, operationMode;

        switch (action) {
            case CMDBuildUI.mixins.DetailsTabPanel.actions.edit:
                card = {
                    xtype: 'events-event-edit',
                    tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.edit,
                    bind: {
                        theEvent: '{theLink}'
                    }
                };
                operationMode = CMDBuildUI.locales.Locales.common.actions.edit;
                view._objectFormReference = 'events-event-edit';
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.create:
                card = {
                    xtype: 'events-event-create',
                    tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.create,
                    bind: {
                        theEvent: '{theLink}'
                    }
                };
                operationMode = CMDBuildUI.locales.Locales.common.actions.add;
                view._objectFormReference = 'events-event-create';
                break;
            default: // CMDBuildUI.mixins.DetailsTabPanel.actions.view:
                card = {
                    xtype: 'events-event-view',
                    tab: 'view',
                    tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.view,
                    hideTools: readonly,
                    bind: {
                        theEvent: '{theLink}'
                    }
                };
                operationMode = CMDBuildUI.locales.Locales.gis.view;
                view._objectFormReference = 'events-event-view';
                break;
        }
        view._objectLinkName = Ext.String.format('{0}.{1}', view._objectFormReference, 'theEvent');

        var baseConfig = view.getObjectFormBaseConfig();
        // delete baseConfig.reference;

        tabcard = view.add({
            xtype: "panel",
            itemId: card.tabAction, //used by {activeItem}
            iconCls: 'x-fa fa-file-text',
            items: [Ext.applyIf(card, baseConfig)],
            reference: card.tab,
            bodyPadding: 0,
            layout: 'fit',
            autoScroll: true,
            padding: 0,
            tabAction: card.tabAction,
            operationMode: operationMode,
            tabConfig: {
                tabIndex: 0,
                title: readonly ? CMDBuildUI.locales.Locales.calendar.event : undefined,
                tooltip: !readonly ? CMDBuildUI.locales.Locales.calendar.event : undefined
            }
        });

        // Notes tab
        if (privileges.card_tab_note_access_read) {
            var bindtext;
            if (readonly) {
                bindtext = CMDBuildUI.locales.Locales.common.tabs.notes + ' {tabcounters.notes ? "(" + 1 + ")" : null}';
            } else {
                bindtext = '{tabcounters.notes ? \'<span class="badge">\' + 1 + "</span>" : null}';
            }
            tabnotes = view.add({
                xtype: 'events-notes-notes',
                iconCls: 'x-fa fa-sticky-note',
                itemId: CMDBuildUI.mixins.DetailsTabPanel.actions.notes, //used by {activeItem}
                readOnly: readonly || !privileges.card_tab_note_access_write,
                autoScroll: true,
                tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.notes,
                operationMode: operationMode,
                tabConfig: {
                    tabIndex: 1,
                    title: readonly ? CMDBuildUI.locales.Locales.common.tabs.notes : undefined,
                    tooltip: !readonly ? CMDBuildUI.locales.Locales.common.tabs.notes : undefined,
                    bind: {
                        text: bindtext
                    }
                },
                bind: {
                    disabled: '{disabled.notes}',
                    theEvent: '{theLink}'
                }
            });
        }

        // History tab
        if (privileges.card_tab_history_access_read) {
            tabhistory = view.add({
                xtype: "panel",
                itemId: CMDBuildUI.mixins.DetailsTabPanel.actions.history, //used by {activeItem}
                iconCls: 'x-fa fa-history',
                items: [{
                    xtype: 'history-grid',
                    autoScroll: true,
                    viewModel: {
                        type: 'events-history-grid'
                    }
                }],
                tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.history,
                operationMode: operationMode,
                tabConfig: {
                    tabIndex: 2,
                    title: readonly ? CMDBuildUI.locales.Locales.common.tabs.history : undefined,
                    tooltip: !readonly ? CMDBuildUI.locales.Locales.common.tabs.history : undefined
                },
                layout: 'fit',
                autoScroll: true,
                padding: 0,
                bind: {
                    disabled: '{disabled.history}'
                }
            });
        };

        // Email tab
        if (privileges.card_tab_email_access_read) {
            var bindtext;
            if (readonly) {
                bindtext = CMDBuildUI.locales.Locales.common.tabs.emails + ' {tabcounters.emails ? "(" + tabcounters.emails + ")" : null}';
            } else {
                bindtext = '{tabcounters.emails ? \'<span class="badge">\' + tabcounters.emails + "</span>" : null}';
            }
            tabemails = view.add({
                xtype: 'emails-container',
                iconCls: 'x-fa fa-envelope',
                itemId: CMDBuildUI.mixins.DetailsTabPanel.actions.emails, //used by {activeItem}
                autoScroll: true,
                tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.emails,
                operationMode: operationMode,
                bodyPadding: 0,
                readOnly: readonly || !privileges.card_tab_email_access_write,
                tabConfig: {
                    tabIndex: 3,
                    title: readonly ? CMDBuildUI.locales.Locales.common.tabs.emails : undefined,
                    tooltip: !readonly ? CMDBuildUI.locales.Locales.common.tabs.emails : undefined,
                    bind: {
                        text: bindtext
                    }
                },
                bind: {
                    disabled: '{disabled.email}'
                }
            });
        };

        // Attachments tab 
        if (configAttachments && privileges.card_tab_attachment_access_read) {
            var bindtext;
            if (readonly) {
                bindtext = CMDBuildUI.locales.Locales.common.tabs.attachments + ' {tabcounters.attachments ? "(" + tabcounters.attachments + ")" : null}';
            } else {
                bindtext = '{tabcounters.attachments ? \'<span class="badge">\' + tabcounters.attachments + "</span>" : null}';
            }
            tabattachments = view.add({
                xtype: 'dms-container',
                iconCls: 'x-fa fa-paperclip',
                autoScroll: true,
                //Don't set layout fit,
                tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.attachments,
                operationMode: operationMode,
                tabConfig: {
                    tabIndex: 4,
                    title: readonly ? CMDBuildUI.locales.Locales.common.tabs.attachments : undefined,
                    tooltip: !readonly ? CMDBuildUI.locales.Locales.common.tabs.attachments : undefined,
                    bind: {
                        text: bindtext
                    }
                },
                bind: {
                    disabled: '{disabled.attachments}',
                    objectId: '{theLink._id}'
                },
                objectType: CMDBuildUI.model.calendar.Event.calendar,
                objectTypeName: 'Event',
                readOnly: readonly || !privileges.card_tab_attachment_access_write,
                ignoreSchedules: true
            });
        }

        var activetab;
        switch (action) {
            case CMDBuildUI.mixins.DetailsTabPanel.actions.notes:
                activetab = tabnotes;
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.history:
                activetab = tabhistory;
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.emails:
                activetab = tabemails;
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.attachments:
                activetab = tabattachments;
                break;
        }
        if (!activetab) {
            activetab = tabcard;
        }
        Ext.asap(function () {
            if (!view.destroyed) {
                view.setActiveTab(activetab);
            }
        });
    },

    /**
    * @param {CMDBuildUI.view.classes.cards.TabPanel} view
    * @param {Ext.Component} newtab
    * @param {Ext.Component} oldtab
    * @param {Object} eOpts
    */
    onTabChange: function (view, newtab, oldtab, eOpts) {
        var vm = view.lookupViewModel();

        if (newtab && newtab.tabAction) {
            vm.set("activetab", newtab.tabAction)
        }

        if (view.isInDetailWindow()) {
            CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(newtab.tabAction);
            this.redirectTo(CMDBuildUI.util.Navigation.getScheduleBaseUrl(this.getView().getEventId(), newtab.tabAction));
            vm.set("titledata.operation", newtab.operationMode);
        } else if (this.getOpenTool()) {
            var tool = this.getOpenTool();

            //update action
            tool.action = newtab.tabAction
            CMDBuildUI.util.Navigation.updateCurrentRowTab(newtab.tabAction);

            //update tooltip
            var toolel = tool.getEl();
            if (toolel) {
                var tooltip = Ext.String.format(
                    '{0} {1}',
                    CMDBuildUI.locales.Locales.common.actions.open,
                    newtab.tabConfig.title
                );

                toolel.set({
                    "data-qtip": tooltip,
                    "aria-label": tooltip
                });
            }
        }
    },

    /**
     * 
     * @param {Ext.panel.Tool} tool 
     * @param {Ext.event.Event} e 
     * @param {Ext.Component} owner 
     * @param {Object} eOpts 
     */
    onOpenToolClick: function (tool, e, owner, eOpts) {
        this.redirectTo(CMDBuildUI.util.Navigation.getScheduleBaseUrl(this.getView().getEventId(), tool.action));
    },

    /**
     * Triggered on edit tool click.
     * 
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */
    onEditButton: function () {
        CMDBuildUI.util.Ajax.setActionId("calendar.event.edit");
        this.redirectTo(
            CMDBuildUI.util.Navigation.getScheduleBaseUrl(
                this.getView().getEventId(),
                CMDBuildUI.mixins.DetailsTabPanel.actions.edit),
            true);
    },

    /**
    * Triggered on open tool click.
    * 
    * @param {Ext.panel.Tool} tool
    * @param {Ext.Event} event
    * @param {Object} eOpts
    */
    onDeleteBtn: function (tool, event, eOpts) {
        var view = this.getView();

        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.notifier.attention,
            CMDBuildUI.locales.Locales.classes.cards.deleteconfirmation,
            function (btnText) {
                if (btnText === "yes") {
                    CMDBuildUI.util.helper.FormHelper.startSavingForm();
                    // CMDBuildUI.util.Ajax.setActionId('class.card.delete');
                    // get the object
                    view.getFormObject().erase({
                        url: CMDBuildUI.util.api.Calendar.getEventsUrl(),
                        success: function (record, operation) {

                            // fire global card deleted event
                            Ext.GlobalEvents.fireEventArgs("carddeleted", [record]);

                            if (view.isInDetailWindow()) {
                                CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
                            }
                        },
                        callback: function (record, operation, success) {
                            CMDBuildUI.util.helper.FormHelper.endSavingForm();
                        }
                    });
                }
            }, this);
    },

    onManagementDetailsWindowBeforeClose: function () {
        //deletes the context action
        CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(undefined);

        //deletes the context associatedId;
        CMDBuildUI.util.Navigation.updateCurrentManagementContextObjectId(undefined);

        //FIXME: could be delated due to http://gitlab.tecnoteca.com/cmdbuild/cmdbuild/issues/5231 improvements
        //deletes the customPage context object id associated. Is for custom pages
        CMDBuildUI.util.Navigation.updateCurrentManagementContextCustomPageObjectId(undefined);

        //FIXME: could be delated due to http://gitlab.tecnoteca.com/cmdbuild/cmdbuild/issues/5231 improvements
        //deletes the view context object id. Is for views
        CMDBuildUI.util.Navigation.updateCurrentManagementContextViewObjectId(undefined);

        CMDBuildUI.util.Utilities.redirectTo(CMDBuildUI.util.Navigation.getScheduleBaseUrl(
            this.getView().getEventId()), true);
    },

    privates: {

        /**
         * 
         * @return {Ext.panel.Tool}
         */
        getOpenTool: function () {
            return this.getView().lookupReference("opentool");
        }
    }
});
