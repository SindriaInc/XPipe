Ext.define('CMDBuildUI.view.login.changepassword.FormController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.login-changepassword-form',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        'textfield': {
            specialkey: 'onSpecialKey'
        }
    },

    /**
     * 
     * @param {*} view 
     * @param {*} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        view.add([{
            xtype: 'textfield', //https://www.chromium.org/developers/design-documents/create-amazing-password-forms
            hidden: true,
            listeners: {
                afterrender: function (field) {
                    field.inputEl.set({
                        autocomplete: CMDBuildUI.view.fields.password.Password.username
                    });
                }
            }
        }, {
            xtype: 'passwordfield',
            fieldLabel: CMDBuildUI.locales.Locales.main.password.old,
            autocomplete: CMDBuildUI.view.fields.password.Password.currentPassword,
            allowBlank: false,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.main.password.old'
            },
            autoEl: {
                'data-testid': 'login-changepassword-form-oldpassword'
            },
            itemId: 'oldpassword',
            reference: 'oldpassword',
            bind: {
                value: '{oldpassword}'
            },
            name: 'pastpassword'
        },
        {
            xtype: 'passwordfield',
            fieldLabel: CMDBuildUI.locales.Locales.main.password.new,
            allowBlank: false,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.main.password.new'
            },
            autocomplete: CMDBuildUI.view.fields.password.Password.newPassword,
            autoEl: {
                'data-testid': 'login-changepassword-form-newpassword'
            },
            itemId: 'password',
            reference: 'password',
            bind: {
                value: '{newpassword}',
                validation: '{isvalidpassword}'
            },
            name: 'newpassword',
            labelToolIconQtip: CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.passwordrules.enabled) ? this.getLabelTooltip() : null,
            labelToolIconCls: CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.passwordrules.enabled) ? 'fa-question-circle' : null
        }, {
            xtype: 'passwordfield',
            fieldLabel: CMDBuildUI.locales.Locales.main.password.confirm,
            allowBlank: false,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.main.password.confirm'
            },
            autocomplete: CMDBuildUI.view.fields.password.Password.newPassword,
            autoEl: {
                'data-testid': 'login-changepassword-form-confirmpassword'
            },
            itemId: 'confirmpassword',
            reference: 'confirmpassword',
            bind: {
                value: '{confirmpassword}',
                validation: '{isvalidconfirmpassword}'
            },
            name: 'confirmpassword'
        }]);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var vm = button.lookupViewModel();
        var form = vm.getView();
        if (form.isValid()) {
            CMDBuildUI.util.helper.FormHelper.startSavingForm();
            var newpsw = vm.get('newpassword');
            var oldpsw = vm.get('oldpassword');
            var username = vm.get('username');

            var loadmask = CMDBuildUI.util.Utilities.addLoadMask(form);
            Ext.Ajax.request({
                url: CMDBuildUI.util.Config.baseUrl + '/users/' + username + '/password',
                method: 'PUT',
                jsonData: {
                    password: newpsw,
                    oldpassword: oldpsw
                },
                success: function () {
                    CMDBuildUI.util.Notifier.showSuccessMessage(CMDBuildUI.locales.Locales.main.password.saved);
                    form.fireEvent("passwordchange", form);
                },
                callback: function (options, success, response) {
                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                    CMDBuildUI.util.Utilities.removeLoadMask(loadmask);
                }

            });
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        CMDBuildUI.util.Utilities.closePopup('popup-change-password');
    },

    /**
     * @param {Ext.field.Text} field
     * @param {Event} e
     */
    onSpecialKey: function (field, e) {
        var me = this;
        if (e.getKey() == e.ENTER) {
            me.onSaveBtnClick(this.lookupReference('saveBtn'));
        }
    },
    privates: {
        getLabelTooltip: function () {
            var errors = [];

            // get configuration
            var minlength = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.passwordrules.minlength),
                diffprevious = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.passwordrules.diffprevious),
                diffusername = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.passwordrules.diffusername),
                reqdigit = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.passwordrules.reqdigit),
                reqlowercase = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.passwordrules.reqlowercase),
                requppercase = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.passwordrules.requppercase);


            // check minimum length
            if (minlength) {
                errors.push(Ext.String.format(CMDBuildUI.locales.Locales.main.password.err_length, minlength));
            }

            // check different from previous
            if (diffprevious) {
                errors.push(CMDBuildUI.locales.Locales.main.password.err_diffprevious);
            }

            // check different from previous
            if (diffusername) {
                errors.push(CMDBuildUI.locales.Locales.main.password.err_diffusername);
            }

            // check required digit
            if (reqdigit) {
                errors.push(CMDBuildUI.locales.Locales.main.password.err_reqdigit);
            }

            // check required lowercase
            if (reqlowercase) {
                errors.push(CMDBuildUI.locales.Locales.main.password.err_reqlowercase);
            }

            // check required uppercase
            if (requppercase) {
                errors.push(CMDBuildUI.locales.Locales.main.password.err_requppercase);
            }
            return errors.join("<br />")
        }
    }

});