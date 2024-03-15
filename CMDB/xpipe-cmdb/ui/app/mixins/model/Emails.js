Ext.define('CMDBuildUI.mixins.model.Emails', {
    mixinId: 'model-emails-mixin',

    /**
     * @property {Ext.data.Store} _emailsstore
     * Store of `CMDBuildUI.model.emails.Email`.
     */
    _emailsstore: null,

    /**
     * @property {Ext.data.Store} _templatesstore
     * Store of `CMDBuildUI.model.emails.Template`.
     */
    _templatesstore: null,

    /**
     * @property {[]} _templatestoevaluate
     */
    _templatestoevaluate: [],

    /**
     * 
     * @return {Ext.data.Store}
     */
    emails: function () {
        if (!this._emailsstore) {
            this._emailsstore = Ext.create("Ext.data.Store", {
                model: 'CMDBuildUI.model.emails.Email',
                grouper: CMDBuildUI.util.Navigation.getGroupEmailByStatus() ? { property: "status" } : null,
                proxy: {
                    type: 'baseproxy',
                    url: this.getProxy().getUrl() + "/" + this.getId() + "/emails"
                },
                autoLoad: !this.phantom,
                sorters: [{ 
                    property: 'date', 
                    direction: 'DESC' 
                }],
                pageSize: 0
            });
        }
        return this._emailsstore;
    },

    /**
     * 
     * @return {Ext.data.Store}
     */
    templates: function () {
        if (!this._templatesstore) {
            this._templatesstore = Ext.create("Ext.data.Store", {
                model: 'CMDBuildUI.model.emails.Template',
                proxy: {
                    type: 'baseproxy',
                    url: CMDBuildUI.util.api.Emails.getTemplatesUrl(),
                    extraParams: {
                        detailed: true,
                        includeBindings: true
                    }
                },
                advancedFilter: {
                    attributes: {
                        provider: [{
                            operator: CMDBuildUI.util.helper.FiltersHelper.operators.equal,
                            value: ['email']
                        }]
                    }
                },
                // advancedFilter: '{templatesstoredata.advancedfilter}',
                autoLoad: false,
                autoDestroy: false,
                pageSize: 0 // disable pagination
            });
        }
        return this._templatesstore;
    },

    /**
     * @param {Boolean} force If true force store load
     * @return {Function} function load
     */
    loadTemplates: function (force) {
        var deferred = new Ext.Deferred();
        var tpls = this._templatestoevaluate;
        var store = this.templates();
        if (Ext.isEmpty(tpls)) {
            // return empty records if there are 
            // no templates to evaluate
            deferred.resolve([]);
        } else if (!store.isLoaded() || force) {
            // add filter
            var tpls_names = [];
            tpls.forEach(function (t) {
                tpls_names.push(t.name);
            })
            var advancedFilter = store.getAdvancedFilter();
            advancedFilter.removeAttributeFitler("name");
            advancedFilter.addAttributeFilter("name", "in", tpls_names);
            // load templates
            store.load(function (records, operation, success) {
                if (success) {
                    records.forEach(function (record) {
                        // update condition
                        var tpl = Ext.Array.findBy(tpls, function (t) {
                            return record.get("name") === t.name;
                        });
                        if (tpl) {
                            record.set("_condition", tpl.condition);
                            record.set("notifyWith", tpl.notifywith);
                        }
                    });
                    deferred.resolve(records);
                } else {
                    deferred.reject();
                }
            });
        } else {
            // return store data
            deferred.resolve(store.getRange());
        }
        return deferred.promise;
    },

    _lastcheckdata: {},

    /**
     * 
     * @param {function} syncstore
     * @param {Boolean} force
     */
    updateObjEmailsFromTemplates: function (syncstore, force) {
        var deferred = new Ext.Deferred();

        var me = this;

        // get clean data
        var objectdata = me.getCleanData();
        if (me.phantom) {
            delete objectdata._id;
        }

        Ext.Object.each(objectdata, function (key, value, alldata) {
            if (Ext.isDate(value)) {
                objectdata[key] = value.toJSON();
            }
        });

        // get changes from last update
        var changed = CMDBuildUI.util.Utilities.getObjectChanges(objectdata, me._lastcheckdata);

        // update last check data
        me._lastcheckdata = objectdata;

        // get drafts emails
        var emails = me.emails();

        /**
         * Update emails
         */
        function updateEmails() {
            emails.filter({
                property: "status",
                value: CMDBuildUI.model.emails.Email.statuses.draft,
                exactMatch: true
            });
            var drafts = emails.getRange();
            emails.clearFilter();

            var templates = me.templates().getRange();
            var porcessestemplates = 0;

            if (templates.length === 0) {
                deferred.resolve(true);
            }

            function finishEmailUpdating(success) {
                if (success && syncstore) {
                    if (!emails.needsSync || me.phantom) {
                        deferred.resolve(success);
                    } else {
                        var newRecs = emails.getNewRecords(),
                            modRecs = emails.getModifiedRecords(),
                            delRecs = emails.getRemovedRecords();
                        if (modRecs.length || newRecs.length || delRecs.length) {
                            emails.sync({
                                params: {
                                    upload_template_attachments: true
                                },
                                callback: function () {
                                    var promises = [];
                                    newRecs.forEach(function (e) {
                                        promises.push(e.saveAttachments(e._attachments));
                                    });

                                    if (promises.length) {
                                        Ext.Promise.all(promises).then(function () {
                                            deferred.resolve(true);
                                        });
                                    } else {
                                        deferred.resolve(true);
                                    }
                                }
                            });
                        } else {
                            deferred.resolve(success);
                        }
                    }
                } else {
                    deferred.resolve(success);
                }
            }

            // get enabled templates
            var wasSuccessful = true;
            templates.forEach(function (template) {
                // get bindings
                var tplbindings = template.get("_bindings") && template.get("_bindings").client || [];
                var bindings = [];
                tplbindings.forEach(function (b) {
                    var sb = b && b.split(".") || "";
                    if (sb && sb.length) {
                        bindings.push(sb[0]);
                    }
                });

                // check bindings changes
                var haschanges = false;
                bindings.forEach(function (b) {
                    if (Ext.Array.contains(Object.keys(changed), b)) {
                        haschanges = true;
                    }
                });

                // get email for this template
                var email = Ext.Array.findBy(drafts, function (draft) {
                    return draft.get("template") == template.getId();
                });

                if (email) {
                    // delete email
                    if ((haschanges && !Ext.isEmpty(email) && email.get("keepSynchronization")) || force) {
                        email.erase();
                    }
                }

                // create email
                if (
                    (Ext.isEmpty(bindings) && Ext.isEmpty(email)) || // no bindings and email not exists
                    (haschanges && Ext.isEmpty(email)) || // has changes on binding fields and email not exists
                    (haschanges && !Ext.isEmpty(email) && email.get("keepSynchronization") || // has changes on binding fields and email and keep synk is active
                        (force == true)) //forces the calculation of the template
                ) {
                    // create temporary email to generate email from template
                    var newemail = Ext.create("CMDBuildUI.model.emails.Email", {
                        template: template.getId(),
                        notifyWith: template.get("notifyWith"),
                        _card: objectdata
                    });
                    if (template.get("_condition")) {
                        var condition = template.get("_condition");
                        if (!/^{\w+:\S+}$/.test(condition)) {
                            condition = Ext.String.format("{js:{0}}", template.get("_condition"));
                        }
                        newemail.set("_expr", condition);
                    }
                    newemail.getProxy().setUrl(me.getEmailsProxyUrl());

                    // generate email from template
                    newemail.save({
                        params: {
                            apply_template: true,
                            template_only: true
                        },
                        callback: function (record, operation, success) {
                            porcessestemplates++;
                            if (success) {
                                // create email new email with given data
                                if (!template.get("_condition") || record.get("_expr") === "true" || record.get("_expr") === true) {
                                    emails.add([record.getCleanData()]);
                                }
                            } else {
                                // mark as failure
                                wasSuccessful = false;
                            }
                            if (porcessestemplates === templates.length) {
                                finishEmailUpdating(wasSuccessful);
                            }
                        }
                    });
                } else {
                    porcessestemplates++;
                    if (porcessestemplates === templates.length) {
                        finishEmailUpdating(wasSuccessful);
                    }
                }
            });
        }

        // on emails store loaded
        if (emails.isLoaded() || me.phantom) {
            updateEmails();
        } else {
            emails.on({
                load: {
                    fn: function (records) {
                        updateEmails();
                    },
                    single: true
                }
            });
        }

        return deferred.promise;
    },

    /**
     * @deprecated This method is deprecated. Please use updateObjEmailsFromTemplates.
     * 
     * @param {Function} callback 
     * @param {Boolean} syncstore
     * @param {Boolean} force
     */
    updateEmailsFromTemplates: function (callback, syncstore, force) {
        // log deprecated warning
        CMDBuildUI.util.Logger.log(
            "updateEmailsFromTemplates is deprecated. Please use updateObjEmailsFromTemplates",
            CMDBuildUI.util.Logger.levels.warn
        );
        // execute promise and then execute callback
        this.updateObjEmailsFromTemplates(syncstore, force).then(function (success) {
            Ext.callback(callback, null, [success]);
        });
    },

    getEmailsProxyUrl: function () {
        return Ext.String.format("{0}/{1}/emails",
            this.getProxy().getUrl(),
            this.phantom ? "_ANY" : this.getId()
        );
    }
});