Ext.define('CMDBuildUI.view.login.FormPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.login-formpanel',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        'textfield': {
            specialkey: 'onSpecialKey'
        },
        '#loginbtn': {
            click: 'onLoginBtnClick'
        },
        '#logoutbtn': {
            click: 'onLogoutBtnClick'
        },
        '#pwdforgottenbtn': {
            click: 'onPwdForgottenBtnClick'
        }
    },

    onBeforeRender: function (view, eOpts) {
        this.checkUserMessage();
        // clear unused cookie
        Ext.util.Cookies.clear("CMDBuild-Authorization");
        // get current session
        var currentsession = CMDBuildUI.util.helper.SessionHelper.getCurrentSession(),
            vm = view.lookupViewModel();
        vm.set("disablechangepassword", !CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.systempasswordchangeenabled));
        if (currentsession && currentsession.crudState !== "D") {
            // set session in viewmodel
            vm.set("theSession", currentsession);
            vm.set("hasRole", !Ext.isEmpty(currentsession.get("role")));
            // hide password
            vm.set("hiddenfields.password", true);

            // add binding for tenants field
            vm.bind({
                bindTo: {
                    tenantsany: '{tenantsany}',
                    tenantsone: '{tenantsone}'
                },
                single: true
            }, function (data) {
                if (data.tenantsany) {
                    view.lookupReference('activeTenantsField').setBind({
                        value: '{theSession.activeTenants}'
                    });
                } else if (data.tenantsone) {
                    view.lookupReference('activeTenantsFieldone').setBind({
                        value: '{theSession.activeTenants}'
                    });
                }
            });

            vm.set('loggedIn', true);
            CMDBuildUI.util.helper.SessionHelper.logging = false;
        }
    },

    /**
     * @param {Ext.form.field.Text} textfield
     * @param {Event} e The click event
     */
    onSpecialKey: function (textfield, e) {
        var vm = this.getViewModel();
        if (e.getKey() == e.ENTER && !vm.get("disableLogginButton")) {
            var me = this;
            setTimeout(function () {
                me.onLoginBtnClick(me.lookupReference('loginbtn'));
            }, 200);
        }
    },
    /**
     * @param {Ext.button.Button} btn
     * @param {Event} e The click event
     */
    onLoginBtnClick: function (btn, e) {
        var me = this,
            vm = this.getViewModel(),
            form = this.getView();

        if (form.isValid() && !vm.get("disableLogginButton")) {
            CMDBuildUI.util.helper.FormHelper.startSavingForm();
            vm.set("disableLogginButton", true);
            btn.mask();
            var theSession = vm.get("theSession");
            theSession.set("password", vm.get("password"));
            // set action id
            CMDBuildUI.util.Ajax.setActionId('login');
            // save session
            if (typeof theSession.get('activeTenants') == 'string') {
                theSession.set('activeTenants', [theSession.get('activeTenants')]);
            }
            theSession.save({
                failure: function (record, operation) {
                    if (!vm.get("loggedIn")) {
                        var error = operation.getError();
                        if (error && error.status == 401) {
                            var response = JSON.parse(operation.getError().response.responseText),
                                message = CMDBuildUI.locales.Locales.errors.autherror;
                            if (
                                response.messages &&
                                response.messages.length &&
                                response.messages[0].message &&
                                response.messages[0].message !== "access denied"
                            ) {
                                message = response.messages[0].message;
                            }
                            if (response.passwordResetRequired) {
                                message = CMDBuildUI.locales.Locales.main.password.expired;
                                vm.getParent().set("showChangePassword", true);
                                // get change password form
                                var cpviewmodel = form.nextSibling().lookupViewModel();
                                cpviewmodel.set("username", theSession.get("username"));
                                cpviewmodel.set("oldpassword", theSession.get("password"));
                            }
                            CMDBuildUI.util.Notifier.showErrorMessage(message);
                        }
                    }
                },
                success: function (record, operation) {
                    CMDBuildUI.util.helper.SessionHelper.logging = false;
                    CMDBuildUI.util.helper.SessionHelper.initSession(record.getId());

                    var rolePresence = Ext.Array.indexOf(record.get("availableRoles"), record.get("role"));
                    if (rolePresence == -1 && record.get("role")) {
                        record.set('role', null);
                    }

                    if (record.get("role") && (!record.get('availableTenants') || record.get('activeTenants'))) {
                        CMDBuildUI.util.helper.SessionHelper.setSessionIntoViewport(record);

                        // remove previous error messages
                        CMDBuildUI.util.Notifier.closeAll();

                        // show logged in messages
                        CMDBuildUI.util.Notifier.showMessage(
                            Ext.String.format(CMDBuildUI.locales.Locales.login.welcome, record.get('userDescription')),
                            {
                                title: CMDBuildUI.locales.Locales.login.loggedin,
                                icon: 'fa-check-circle'
                            });

                        // load configs and preferences
                        Ext.Promise.all([
                            CMDBuildUI.util.helper.Configurations.loadSystemConfs(),
                            CMDBuildUI.util.helper.UserPreferences.load()
                        ]).then(function (responses) {
                            CMDBuildUI.util.helper.Configurations.updateConfigsInViewport();
                            CMDBuildUI.util.helper.Configurations.updateEnabledFeatures();

                            // redirect to management or administration
                            var path = Ext.String.startsWith(CMDBuildUI.util.helper.SessionHelper.getStartingUrl(), 'administration') ? 'administration' : 'management',
                                loginLanguage = CMDBuildUI.util.helper.SessionHelper.getLanguage(false),
                                userLanguage = CMDBuildUI.util.helper.SessionHelper.getLanguage(true);

                            me.redirectTo(path, true);

                            // if login language is different from user preferences language reload the page
                            if (loginLanguage !== userLanguage) {
                                if (!responses[1].get(CMDBuildUI.model.users.Preference.language)) {
                                    var languagePreferences = {};
                                    languagePreferences[CMDBuildUI.model.users.Preference.language] = loginLanguage;
                                    CMDBuildUI.util.helper.UserPreferences.updatePreferences(languagePreferences).then(function () {
                                        window.location.reload();
                                    });
                                } else {
                                    window.location.reload();
                                }
                            }
                        });
                    } else {
                        vm.set('loggedIn', true);
                    }

                },
                callback: function (record, operation, success) {
                    btn.unmask();
                    vm.set("disableLogginButton", false);
                    // hide error message
                    vm.set('showErrorMessage', false);
                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                }
            });
        } else if (!vm.get("disableLogginButton")) {
            vm.set('showErrorMessage', true);
        }
    },

    /**
     * @param {Ext.button.Button} btn
     * @param {Event} e The click event
     */
    onLogoutBtnClick: function (btn, e) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        var vm = this.getViewModel();
        // set action id
        CMDBuildUI.util.Ajax.setActionId('logout');
        // erase session
        vm.get("theSession").erase({
            success: function (record, operation) {
                // blank session token
                CMDBuildUI.util.Utilities.redirectTo("login", true);
            },
            callback: function (record, operation, success) {
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
            }
        });
    },

    /**
     *
     * @param {Ext.button.Button} btn
     * @param {Event} e
     */
    onPwdForgottenBtnClick: function (btn, e) {
        var popup = CMDBuildUI.util.Utilities.openPopup(
            null,
            CMDBuildUI.locales.Locales.main.password.forgotten,
            {
                xtype: 'login-passwordforgotten-panel',
                viewModel: {
                    data: {
                        username: btn.lookupViewModel().get("theSession.username")
                    }
                },
                listeners: {
                    closepopup: function () {
                        popup.close();
                    }
                }
            },
            null,
            {
                width: 400,
                height: 250
            }
        );
    },

    checkUserMessage: function () {
        var params = Ext.urlDecode(location.search.substring(1));
        if (params && params['CMDBuild-Messages']) {
            Ext.asap(function () {
                window.history.pushState({}, document.title, Ext.String.format("{0}#login", CMDBuildUI.util.Config.uiBaseUrl));
                // currently there is only on message
                CMDBuildUI.util.Notifier.showErrorMessage(CMDBuildUI.locales.Locales.login.sso.usernotenabled);
            });
        }
    }
});