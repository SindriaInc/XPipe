Ext.define('CMDBuildUI.view.emails.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.emails-grid',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#composeemail': {
            click: 'onComposeEmail'
        },
        '#regenerateallemails': {
            click: 'onRegenerateAllEmails'
        },
        '#gridrefresh': {
            click: 'onGridRefresh'
        },
        'tableview': {
            actionview: 'onActionView',
            actiondelete: 'onActionDelete',
            actionedit: 'onActionEdit',
            actionsend: 'onActionSend',
            actionreply: 'onActionReply',
            actionregenerate: 'onActionRegenerate',
            afterrender: 'onAfterRenderTableView'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.emails.Grid} grid 
     * @param {Object} eOpts 
     */
    onBeforeRender: function (grid, eOpts) {
        var vm = grid.lookupViewModel();
        vm.bind({
            groupByStatus: '{groupByStatus}'
        }, function (data) {
            var grouping = grid.getView().findFeature('grouping'),
                emails = grid.getStore();
            if (data.groupByStatus) {
                if (emails && Ext.isEmpty(emails.getGrouper())) {
                    emails.setGrouper({
                        property: "status"
                    });
                }
                grouping.enable();
                grid.getColumns()[0].hide();
                CMDBuildUI.util.Navigation.setGroupEmailByStatus(true);
            } else {
                grouping.disable();
                grid.getColumns()[0].show();
                CMDBuildUI.util.Navigation.setGroupEmailByStatus(false);
            }
        });
        vm.set('groupByStatus', CMDBuildUI.util.Navigation.getGroupEmailByStatus());
    },

    /**
     * 
     * @param {Ext.View.Table} tableView 
     * @param {Object} eOpts 
     */
    onAfterRenderTableView: function (tableView, eOpts) {
        this.getView().loadMaskTableView = CMDBuildUI.util.Utilities.addLoadMask(tableView);
    },

    /**
     * @param {CMDBuildUI.view.emails.Container.button} view
     * @param {Object} eOpts
     */
    onComposeEmail: function (view, eOpts) {
        var me = this,
            vm = this.getViewModel(),
            emailsStore = vm.get("emails"),
            email = Ext.create("CMDBuildUI.model.emails.Email");

        email.getProxy().setUrl(emailsStore.getProxy().getUrl());

        var object = vm.get("theTarget"),
            objectdata = object && object.getCleanData() || {};

        CMDBuildUI.util.Utilities.openPopup(
            'popup-compose-email',
            CMDBuildUI.locales.Locales.emails.composeemail,
            {
                xtype: 'emails-create',
                viewModel: {
                    data: {
                        objectdata: objectdata,
                        theEmail: email,
                        isAsync: vm.get("isAsync")
                    }
                },
                listeners: {
                    itemcreated: function () {
                        me.getView().getStore().reload();
                    }
                },
                /**
                 *
                 * @param {CMDBuildUI.model.emails.Email} newEmail
                 */
                asyncSave: function (newEmail) {
                    email.set("date", new Date());
                    emailsStore.add(newEmail);
                }
            });
    },

    /**
     * @param {CMDBuildUI.view.emails.Container.button} view
     * @param {Object} eOpts
     */
    onRegenerateAllEmails: function (view, eOpts) {
        var vm = this.getViewModel(),
            theTarget = vm.get('theTarget'),
            loadMask = CMDBuildUI.util.Utilities.addLoadMask(this.getView().view);
        CMDBuildUI.util.helper.FormHelper.startSavingForm();

        if (theTarget._templatestoevaluate.length != 0) {

            theTarget.loadTemplates(true).then(function (templates) {
                theTarget.updateObjEmailsFromTemplates(false, true);
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                CMDBuildUI.util.Utilities.removeLoadMask(loadMask);
            });

        } else {
            var emails = vm.get('emails').getRange(),
                requests = [];

            emails.forEach(function (item, index, array) {
                if (item.get('status') == CMDBuildUI.model.emails.Email.statuses.draft && !Ext.isEmpty(item.get('template'))) {

                    function regenerateEmail(email) {
                        var deferred = new Ext.Deferred(),
                            url;

                        if (vm.get("objectType") == CMDBuildUI.util.helper.ModelHelper.objecttypes.calendar) {
                            url = CMDBuildUI.util.api.Emails.getCalendarEmailUrl(theTarget.getId());
                        } else {
                            url = CMDBuildUI.util.api.Emails.getCardEmailsUrl(theTarget.get('_type'), theTarget.getId());
                        }

                        // update card data
                        email.set("_card", theTarget.getData());
                        email.save({
                            proxy: {
                                url: url
                            },
                            url: url,
                            params: {
                                apply_template: true
                            },
                            callback: function () {
                                deferred.resolve();
                            }
                        });
                        return deferred;
                    }

                    requests.push(regenerateEmail(item));

                }
            });

            Ext.Promise.all(requests).then(function () {
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                CMDBuildUI.util.Utilities.removeLoadMask(loadMask);
            });
        }
    },

    /**
     * Refresh grid handler
     *
     * @param {Ext.button.Button} btn
     * @param {Object} epts
     */
    onGridRefresh: function (btn, epts) {
        var store = this.getView().getStore(),
            emailDraft = store.findBy(function (record, id) {
                return record.get("status") == CMDBuildUI.model.emails.Email.statuses.draft && record.phantom;
            });
        if (emailDraft !== -1) {
            // open confirm message
            CMDBuildUI.util.Msg.confirm(
                CMDBuildUI.locales.Locales.notifier.attention,
                CMDBuildUI.locales.Locales.emails.reloadconfirmationmessage,
                function (action) {
                    if (action === "yes") {
                        store.reload();
                    }
                }
            );
        } else {
            store.reload();
        }
    },

    /**
     * @param {CMDBuildUI.view.attachments.Grid} grid
     * @param {Ext.data.Model} record
     * @param {Number} rowIndex
     * @param {Number} colIndex
     *
     */
    onActionView: function (grid, record, rowIndex, colIndex) {
        var vm = this.getViewModel(),
            object = vm.get("theTarget"),
            objectdata = object && object.getCleanData() || {};

        record.getProxy().setUrl(vm.get("emails").getProxy().getUrl());

        CMDBuildUI.util.Utilities.openPopup(
            'popup-view-email',
            CMDBuildUI.locales.Locales.emails.view,
            {
                xtype: 'emails-view',
                viewModel: {
                    data: {
                        objectdata: objectdata,
                        theEmail: record
                    }
                }
            });
    },

    /**
     * @param {CMDBuildUI.view.attachments.Grid} grid
     * @param {Ext.data.Model} record
     * @param {Number} rowIndex
     * @param {Number} colIndex
     *
     */
    onActionDelete: function (grid, record, rowIndex, colIndex) {
        var vm = this.getViewModel();
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.emails.remove,
            CMDBuildUI.locales.Locales.emails.remove_confirmation,
            function (action) {
                if (action === "yes") {
                    CMDBuildUI.util.helper.FormHelper.startSavingForm();
                    record.getProxy().setUrl(vm.get("emails").getProxy().getUrl());
                    CMDBuildUI.util.Ajax.setActionId('emails.delete');
                    record.erase();
                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                }
            }
        );
    },

    /**
     * @param {CMDBuildUI.view.attachments.Grid} grid
     * @param {Ext.data.Model} record
     * @param {Number} rowIndex
     * @param {Number} colIndex
     *
     */
    onActionEdit: function (grid, record, rowIndex, colIndex) {
        var vm = this.getViewModel(),
            object = vm.get("theTarget"),
            objectdata = object && object.getCleanData() || {};

        record.getProxy().setUrl(vm.get("emails").getProxy().getUrl());

        CMDBuildUI.util.Utilities.openPopup(
            'popup-edit-email',
            CMDBuildUI.locales.Locales.emails.edit,
            {
                xtype: 'emails-edit',
                viewModel: {
                    data: {
                        objectdata: objectdata,
                        theEmail: record,
                        isAsync: vm.get("isAsync")
                    }
                }
            });
    },

    /**
     * @param {CMDBuildUI.view.attachments.Grid} grid
     * @param {Ext.data.Model} record
     * @param {Number} rowIndex
     * @param {Number} colIndex
     *
     */
    onActionSend: function (grid, record, rowIndex, colIndex) {
        var me = this,
            vm = this.getViewModel();
        if (record && record.getData()) {
            CMDBuildUI.util.helper.FormHelper.startSavingForm();
            var theEmail = record;
            theEmail.set('status', 'outgoing');
            // save the email only if not async mode
            if (!vm.get("isAsync")) {
                theEmail.getProxy().setUrl(vm.get("emails").getProxy().getUrl());

                theEmail.send();
            }
            CMDBuildUI.util.helper.FormHelper.endSavingForm();
        }
    },

    /**
     * @param {CMDBuildUI.view.attachments.Grid} grid
     * @param {Ext.data.Model} record
     * @param {Number} rowIndex
     * @param {Number} colIndex
     *
     */
    onActionReply: function (grid, record, rowIndex, colIndex) {
        var view = this.getView(),
            vm = this.getViewModel(),
            urlEmail = vm.get("emails").getProxy().getUrl();

        record.getProxy().setUrl(urlEmail);

        if (record && record.get('status') === CMDBuildUI.model.emails.Email.statuses.received && !record.get('isReadByUser')) {
            record.set('isReadByUser', true);
            record.save();
        }

        function replyEmail(emailLoaded) {
            // email configuration
            var emailconf = {
                cc: emailLoaded.get('cc'),
                bcc: emailLoaded.get('bcc'),
                account: emailLoaded.get("account")
            };

            // calculate receiver address
            if (emailLoaded.get("status") === CMDBuildUI.model.emails.Email.statuses.received) {
                emailconf.to = emailLoaded.get("from");
            } else if (emailLoaded.get("status") === CMDBuildUI.model.emails.Email.statuses.sent) {
                emailconf.to = emailLoaded.get("to");
            }

            // calculate prefix
            var subjectprefix = 'Re: ';
            if (Ext.String.startsWith(emailLoaded.get('subject'), subjectprefix)) {
                emailconf.subject = emailLoaded.get('subject');
            } else {
                emailconf.subject = subjectprefix + emailLoaded.get('subject');
            }

            // calculate body
            var bodyprefix = Ext.String.format(
                CMDBuildUI.locales.Locales.emails.replyprefix,
                CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(emailLoaded.get("date")),
                emailLoaded.get("from")
            );

            // TODO: workaround - check if correct
            emailconf.body = emailconf._content_html = Ext.String.format(
                "<p>&nbsp;</p><p>&nbsp;</p><p>{0}</p><blockquote>{1}</blockquote>",
                bodyprefix,
                emailLoaded.get("_content_html").replace('data-type="signature"', '')
            );

            // generate email
            var email = Ext.create("CMDBuildUI.model.emails.Email", emailconf);
            email.getProxy().setUrl(urlEmail);

            var object = vm.get("theTarget"),
                objectdata = object && object.getCleanData() || {};

            CMDBuildUI.util.Utilities.openPopup(
                'popup-compose-email',
                CMDBuildUI.locales.Locales.emails.composeemail,
                {
                    xtype: 'emails-create',
                    viewModel: {
                        data: {
                            objectdata: objectdata,
                            theEmail: email
                        }
                    },
                    listeners: {
                        itemcreated: function () {
                            view.getStore().reload();
                        }
                    }
                });
        }

        if (record && !(record.phantom || record.get('card'))) {
            record.load({
                callback: function (item, operation, success) {
                    if (vm && !vm.destroyed) {
                        vm.set('emailloaded', true);
                        item.beginEdit();
                        replyEmail(item);
                    }
                }
            });
        } else {
            vm.set('emailloaded', true);
            record.beginEdit();
            replyEmail(record);
        }
    },

    /**
     * @param {CMDBuildUI.view.attachments.Grid} grid
     * @param {Ext.data.Model} record
     * @param {Number} rowIndex
     * @param {Number} colIndex
     *
     */
    onActionRegenerate: function (grid, record, rowIndex, colIndex) {
        var view = this.getView(),
            vm = view.getViewModel(),
            theTarget = vm.get('theTarget');

        if (
            record.get('status') == CMDBuildUI.model.emails.Email.statuses.draft &&
            !Ext.isEmpty(record.get('template'))
        ) {
            CMDBuildUI.util.helper.FormHelper.startSavingForm();
            var loadMask = CMDBuildUI.util.Utilities.addLoadMask(grid);
            if (vm.get("isAsync")) {
                // get target data
                var targetData = theTarget.getData();
                delete targetData._id;

                // generate email
                var email = record.copy();
                email.set("_card", targetData);
                email.getProxy().setUrl(theTarget.getEmailsProxyUrl());
                email.phantom = true;

                // evaluate template
                email.save({
                    params: {
                        apply_template: true,
                        template_only: true
                    },
                    success: function (e) {
                        record.set(e.getData());
                    },
                    callback: function () {
                        CMDBuildUI.util.helper.FormHelper.endSavingForm();
                        CMDBuildUI.util.Utilities.removeLoadMask(loadMask);
                    }
                });
            } else {
                var type = theTarget.get("_type"),
                    id = theTarget.getId(),
                    objectType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(type),
                    url;
                // update card data
                if (objectType == CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
                    url = CMDBuildUI.util.api.Emails.getProcessInstanceEmailsUrl(type, id);
                } else {
                    url = CMDBuildUI.util.api.Emails.getCardEmailsUrl(type, id);
                }
                record.set("_card", theTarget.getData());
                record.save({
                    proxy: {
                        url: url
                    },
                    url: url,
                    params: {
                        apply_template: true
                    },
                    callback: function () {
                        CMDBuildUI.util.helper.FormHelper.endSavingForm();
                        CMDBuildUI.util.Utilities.removeLoadMask(loadMask);
                    }
                });
            }
        }
    }

});