Ext.define('CMDBuildUI.view.classes.cards.clonerelations.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.classes-cards-clonerelations-container',
    control: {
        '#cancelbtn': {
            click: 'onCancelBtnClick'
        },
        '#saveandclosebtn': {
            click: 'onSaveAndCloseBtnClick'
        },
        '#savebtn': {
            click: 'onSaveBtnClick'
        },
        'form': {
            validitychange: 'onValidityChange'
        }
    },

    listen: {
        store: {
            '#relations-clone': {
                update: 'onStoreUpdate'
            }
        }
    },

    /**
     * Cancel button
     * @param {Ext.button.Button} button 
     * @param {Event} event
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, event, eOpts) {
        this.getView().up("#CMDBuildManagementDetailsWindow").close();
    },

    /**
     * Save and Close button
     * @param {Ext.button.Button} button 
     * @param {Event} event
     * @param {Object} eOpts
     */
    onSaveAndCloseBtnClick: function (button, event, eOpts) {
        var saveBtn = this.getView().down("#savebtn");
        this.saveAction(button, saveBtn);
    },

    /**
     * Save button
     * @param {Ext.button.Button} button 
     * @param {Event} event
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, event, eOpts) {
        var saveAndCloseBtn = this.getView().down("#saveandclosebtn");
        this.saveAction(button, saveAndCloseBtn);
    },

    /**
     * 
     * @param {Ext.button.Button} button 
     * @param {Ext.button.Button} otherButton 
     */
    saveAction: function (button, otherButton) {
        var me = this,
            view = this.getView(),
            vm = this.getViewModel(),
            domains = vm.get('relations').getRange(),
            objectId = vm.get('objectId'),
            BreakException = {},
            cancelBtn = view.down("#cancelbtn");

        button.showSpinner = true;
        CMDBuildUI.util.Utilities.disableFormButtons([button, otherButton, cancelBtn]);

        try {
            domains.forEach(function (domain) {
                var mode = domain.get('mode');
                if (!mode) {
                    CMDBuildUI.util.Notifier.showWarningMessage(
                        "Cannot save data, please make sure you selected an action for every domain"
                    );
                    BreakException[0] = 'error';
                    throw BreakException;
                }
            });
        } catch (e) {
            if (e !== BreakException) throw e;
        }

        if (!BreakException[0]) {
            var formcontroller = view.down('#classes-cards-card-create').getController();
            formcontroller.saveForm({
                failure: function () {
                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                    CMDBuildUI.util.Utilities.enableFormButtons([button, otherButton, cancelBtn]);
                }
            }).then(function (record) {
                CMDBuildUI.util.helper.FormHelper.startSavingForm();
                var clonedDomains = me.domainsFilter(domains, 'clone'),
                    migratesDomains = me.domainsFilter(domains, 'migrates'),
                    urlClone = Ext.String.format(CMDBuildUI.util.Config.baseUrl + '/domains/_ANY/relations/_ANY/copy'),
                    urlMigrate = Ext.String.format(CMDBuildUI.util.Config.baseUrl + '/domains/_ANY/relations/_ANY/move'),
                    destination = record.getId();

                Ext.Promise.all([
                    me.saveDomains(objectId, destination, clonedDomains, urlClone),
                    me.saveDomains(objectId, destination, migratesDomains, urlMigrate)
                ]).then(function () {
                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                    if (button.getItemId()) {
                        var url;
                        switch (button.getItemId()) {
                            case 'savebtn':
                                url = CMDBuildUI.util.Navigation.getClassBaseUrl(record.get("_type"), record.getId(), 'view');
                                me.redirectTo(url);
                                break;
                            case 'saveandclosebtn':
                                // close details window
                                CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
                                // redirect to the card

                                url = CMDBuildUI.util.Navigation.getClassBaseUrl(record.get("_type"), record.getId());
                                me.redirectTo(url);
                                break;
                        }
                    }
                });
            }).otherwise(function () {
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                CMDBuildUI.util.Utilities.enableFormButtons([button, otherButton, cancelBtn]);
            });
        }
    },

    /**
     * Filter domain array with given key
     * @param {Array} domain 
     * @param {String} filter
     * 
     * @returns {filtered Array} 
     */
    domainsFilter: function (domains, filter) {
        var result = [];
        domains.forEach(function (domain) {
            var mode = domain.get('mode');
            if (mode == filter) {
                var element = {
                    _id: domain.get('domain'),
                    direction: domain.get('direction')
                };
                result.push(element);
            }
        });
        return result;
    },

    /**
     * Async save call for domains
     * @param {String} source
     * @param {String} destination
     * @param {Array} domains
     * @param {String} url
     * 
     * @returns {Ext.Ajax.request} 
     */
    saveDomains: function (source, destination, domains, url) {
        if (Ext.isEmpty(domains)) {
            var deferred = new Ext.Deferred();
            deferred.resolve();
            return deferred;
        }
        return Ext.Ajax.request({
            url: url,
            method: "POST",
            jsonData: {
                source: source,
                destination: destination,
                domains: domains
            }
        });
    },

    onValidityChange: function (form, valid, eOpts) {
        var store = this.getView().down('#classes-cards-clonerelations-panel').getStore();
        this.aux(form, store);
    },

    onStoreUpdate: function (store, record, operation, modifiedFieldNames, details, eOpts) {
        var form = this.getView().down('#classes-cards-card-create');
        this.aux(form, store);
    },

    aux: function (form, store) {
        var formValid = !form.hasInvalidField();
        var storeValid = this._isStoreValid(store);

        this.getViewModel().set('saveButtonDisabled', !(formValid && storeValid));
    },

    _isStoreValid: function (store) {
        var records = store.getRange();
        for (var i = 0; i < records.length; i++) {
            var record = records[i];

            if (!record.hasChecks()) {
                return false;
            }
        }

        return true;
    }
});