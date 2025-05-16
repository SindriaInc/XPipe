Ext.define('CMDBuildUI.view.administration.content.setup.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-view',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.setup.View} view
     */
    onBeforeRender: function (view) {
        var vm = this.getView().getViewModel();
        vm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        this.loadData();
        view.down('panel').add({
            xtype: Ext.String.format('administration-content-setup-elements-{0}', vm.get('currentPage'))
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditSetupBtnClick: function (button, e, eOpts) {
        this.getView().getViewModel().setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        this.loadData();

    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var me = this;
        button.disable();
        var currentPage = button.up().up();
        var vm = currentPage.getViewModel();
        var currentPageType = vm.get('currentPage');
        if (currentPageType === 'multitenant') {
            if (vm.get('multitenantFieldsDisabled')) {
                me.saveMultitenantData(button);
            } else {
                CMDBuildUI.util.Msg.confirm(
                    CMDBuildUI.locales.Locales.administration.common.messages.attention,
                    Ext.String.format('{0}</br>{1}',
                        CMDBuildUI.locales.Locales.administration.systemconfig.multitenantactivationmessage,
                        CMDBuildUI.locales.Locales.administration.systemconfig.multitenantapllychangesquest
                    ),
                    function (btnText) {
                        if (btnText.toLowerCase() === 'yes') {
                            me.saveMultitenantData(button);
                        } else {
                            button.enable();
                        }
                    }, this);
            }
        } else {
            var vm = button.lookupViewModel();
            me.uploadIcon(vm, button);
        }
    },

    privates: {
        /**
         * Load data from server, format keys and set vm data for binding
         * @private
         */
        loadData: function () {
            var vm = this.getView().getViewModel();
            CMDBuildUI.util.administration.helper.ConfigHelper.getConfigs().then(
                function (configs) {
                    if (!vm.destroyed) {
                        configs.forEach(function (key) {
                            vm.set(Ext.String.format('theSetup.{0}', key._key), (key.hasValue) ? key.value : key['default']);
                        });
                        vm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                    }
                }
            );
        },
        /**
         * Save the configuration
         * 
         * @param {Ext.button.Button} button 
         */
        saveData: function (button) {
            var vm = button.lookupViewModel();
            Ext.getBody().mask(CMDBuildUI.locales.Locales.administration.common.messages.saving);
            // TODO: workaround #1051

            var setData = CMDBuildUI.util.administration.helper.ConfigHelper.setConfigs(
                /** theSetup */
                vm.get('theSetup'),
                /** reloadOnSucces */
                true,
                /** forceDropCache */
                false,
                this
            );

            setData.then(function (transport) {
                if (!vm.destroyed) {
                    vm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                }
            });
            setData.always(function () {
                if (!button.destroyed) {
                    button.enable();
                }
                if (Ext.getBody().isMasked()) {
                    Ext.getBody().unmask();
                }
            });

        },

        /**
         * Save the configuration of multitenant
         * 
         * @param {Ext.button.Button} button 
         */
        saveMultitenantData: function (button) {
            var vm = this.getView().getViewModel();
            var setData = CMDBuildUI.util.administration.helper.ConfigHelper.setMultinantData(vm.get('theSetup'), vm.get('multitenantFieldsDisabled'));
            setData.then(
                function (transport) {
                    if (!vm.destroyed) {
                        vm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                        window.location.reload();
                    }
                }
            );
            setData.always(function () {
                if (!button.destroyed && button.enable) {
                    button.enable();
                }
            });
        },

        /**
         * @private
         * @param {CMDBuildUI.view.administration.content.processes.ViewModel} vm 
         * @param {Function} successCb 
         * @param {Function} errorCb 
         */
        uploadIcon: function (vm, button) {

            var me = this;
            CMDBuildUI.util.Ajax.setActionId('config.logo.upload');
            // define method
            var method = "POST";
            var generalForm = this.getView().down("administration-content-setup-elements-generaloptions");
            var input;
            if (generalForm) {
                input = generalForm.down('#iconFile').extractFileInput();
            }

            if (!generalForm || !input || !input.files.length) {
                me.saveData(button);
            } else {
                // init formData
                var formData = new FormData();
                // get url
                var url = Ext.String.format('{0}/uploads?overwrite_existing=true&path=images/companylogo/', CMDBuildUI.util.Config.baseUrl);
                // upload 
                CMDBuildUI.util.administration.File.upload(method, formData, input, url, {
                    success: function (response) {
                        if (typeof response === 'string') {
                            response = Ext.JSON.decode(response);
                        }
                        if (response && response.data) {
                            vm.set('theSetup.org__DOT__cmdbuild__DOT__core__DOT__companyLogo', response.data._id);
                            me.saveData(button);
                        }
                    },
                    failure: function (error) {
                        if (typeof error === 'string') {
                            error = Ext.JSON.decode(error);
                            CMDBuildUI.util.Logger.log(error, CMDBuildUI.util.Logger.levels.error);
                        }
                        me.saveData(button);
                    }
                });
            }
        }
    }
});