Ext.define('CMDBuildUI.view.administration.content.emails.signatures.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-emails-signatures-card-viewinrow',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#setDefaultSignature': {
            click: 'onToggleDefaultBtnClick'
        },
        '#defaultSignature': {
            click: 'onToggleDefaultBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.classes.tabitems.users.card.ViewInRow} view
     * @param {Object} eOpts
     */

    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        var record = view.getInitialConfig()._rowContext.record;
        vm.linkTo("theSignature", {
            type: 'CMDBuildUI.model.emails.Signature',
            id: record.get('_id')
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        this.openDetailWindow(CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onOpenBtnClick: function (button, e, eOpts) {
        this.openDetailWindow(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (button, e, eOpts) {
        var me = this;
        var view = me.getView();
        var vm = view.lookupViewModel();
        var store = view._rowContext.ownerGrid.getStore();
        CMDBuildUI.util.Utilities.showLoader(true, view);
        var callback = function (btnText) {
            if (btnText === "yes") {
                CMDBuildUI.util.Ajax.setActionId('delete-signature');
               vm.get('theSignature').erase({
                    success: function (record, operation) {
                        CMDBuildUI.util.Utilities.showLoader(false, view);
                        CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                        store.load();
                    },
                    failure: function () {
                        CMDBuildUI.util.Utilities.showLoader(false, view);
                        vm.get('theSignature').reject();                           
                    }                    
                });
            }else{
                CMDBuildUI.util.Utilities.showLoader(false, view);
            }
        };

        CMDBuildUI.util.administration.helper.ConfirmMessageHelper.showDeleteItemMessage(null, null, callback, this);

    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onToggleDefaultBtnClick: function (button, e, eOpts) {
        var me = this;
        var view = this.getView();
        var vm = view.getViewModel();
        var theSignature = vm.get('theSignature');
        var theSignatureId = theSignature.get('code');
        var ajaxconf = {
            url: Ext.String.format('{0}/system/config/_MANY', CMDBuildUI.util.Config.baseUrl),
            method: 'PUT',
            jsonData: {
                'org.cmdbuild.email.signatureDefault': theSignature.get('_default') ? null : theSignatureId
            },
            success: function (transport) {
                view.up('grid').getStore().load({
                    callback: function (data, operation) {
                        view.up('administration-content-emails-signatures-grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('togglerow', [view.up('grid'), this.findRecord('_id', vm.get('theSignature').get('_id')), this.findExact('_id', vm.get('theSignature').get('_id'))]);
                    }
                });
            }
        };

        /**
         * save configuration via custom ajax call
         */
        Ext.Ajax.request(ajaxconf);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onToggleActiveBtnClick: function (button, e, eOpts) {
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true, null, CMDBuildUI.locales.Locales.administration.common.messages.saving]);
        var me = this;
        var view = me.getView();
        var grid = view.up('administration-content-emails-signatures-grid');
        var vm = view.lookupViewModel();
        var theSignature = vm.get('theSignature');
        theSignature.set('active', !theSignature.get('active'));
        theSignature.save({
            callback: function (record) {
                CMDBuildUI.util.administration.Utilities.showToggleActiveMessage(record);
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                grid.getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [grid, record, this]);
            }
        });
    },

    privates: {
        openDetailWindow: function (mode) {
            var vm = this.getViewModel();
            var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
            container.removeAll();
            container.add({
                xtype: 'administration-content-emails-signatures-card-card',
                viewModel: {
                    links: {
                        theSignature: {
                            type: 'CMDBuildUI.model.emails.Signature',
                            id: vm.get('theSignature._id')
                        }
                    },
                    data: {
                        actions: {
                            view: CMDBuildUI.util.administration.helper.FormHelper.formActions.view === mode,
                            edit: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit === mode,
                            add: CMDBuildUI.util.administration.helper.FormHelper.formActions.add === mode
                        }
                    }
                }
            });
        }
    }

});