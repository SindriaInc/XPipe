Ext.define('CMDBuildUI.mixins.routes.Management', {
    mixinId: 'managementroutes-mixin',

    mixins: [
        'CMDBuildUI.mixins.routes.management.Classes',
        'CMDBuildUI.mixins.routes.management.Custompages',
        'CMDBuildUI.mixins.routes.management.Processes',
        'CMDBuildUI.mixins.routes.management.Reports',
        'CMDBuildUI.mixins.routes.management.Views',
        'CMDBuildUI.mixins.routes.management.Dashboards',
        'CMDBuildUI.mixins.routes.management.Events',
        'CMDBuildUI.mixins.routes.management.NavigationTree'
    ],

    /******************* CONFIGUREDB ********************/
    onBeforeConfigureDB: function (action) {
        if (CMDBuildUI.util.Config.bootstatus === "WAITING_FOR_DATABASE_CONFIGURATION") {
            action.resume();
        } else {
            action.stop();
            this.goToManagement();
        }
    },

    showConfigureDB: function () {
        CMDBuildUI.util.Navigation.addIntoMainContainer('boot-configuredb-panel');
    },

    /******************* PATCHES ********************/
    onBeforeConfigurePatches: function (action) {
        if (CMDBuildUI.util.Config.bootstatus === "WAITING_FOR_PATCH_MANAGER") {
            action.resume();
        } else {
            action.stop();
            this.goToManagement();
        }
    },

    showPatches: function () {
        CMDBuildUI.util.Navigation.addIntoMainContainer('boot-patches-panel');
    },

    /**
     *  redirect to management and refresh the window
     */
    goToManagement: function () {
        this.redirectTo('', true);
        window.location.reload();
    },
    /******************* LOGIN ********************/
    /**
     * Show login form
     */
    onBeforeShowLogin: function (action) {
        var me = this;

        /**
         * this avoids launching the function twice due to onBeforeShowLogin fired twice: one by the routing rules and one by the application.js beforelaunch
         */
        if (me._lastLoginEnded === false) {
            action.stop();
            return;
        }
        me._lastLoginEnded = false;

        // redirect to patch manager if needed
        CMDBuildUI.util.Utilities.checkBootStatus().then(function () {
            // close details window
            CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
            // check session validity
            CMDBuildUI.util.helper.SessionHelper.checkSessionValidity().then(function () {
                me._lastLoginEnded = true;
                action.stop();
                var url = CMDBuildUI.util.helper.SessionHelper.getStartingUrl();
                if (url && Ext.String.startsWith(url, "administration")) {
                    me.redirectTo('administration');
                } else {
                    me.redirectTo('management');
                }
            }).otherwise(function (err) {
                me._lastLoginEnded = true;
                action.resume();
            });
            // status is OK
        }).otherwise(function () {
            me._lastLoginEnded = true;

            // need patchs
            action.stop();
            switch (CMDBuildUI.util.Config.bootstatus) {
                case ("WAITING_FOR_DATABASE_CONFIGURATION"):
                    me.redirectTo('configuredb');
                    break;
                case ("WAITING_FOR_PATCH_MANAGER"):
                    me.redirectTo('patches');
                    break;
                default:
                    // redirect at login after 500 milliseconds
                    setTimeout(function () {
                        me.redirectTo('login', true);
                    }, 500);
                    break;
            }
        });
    },
    showLogin: function () {
        if (!CMDBuildUI.util.helper.SessionHelper.logging) {
            CMDBuildUI.util.helper.SessionHelper.logging = true;
            CMDBuildUI.util.Navigation.addIntoMainContainer('login-container');
        }
    },

    /******************* LOGOUT ********************/
    /**
     * Do logout
     */
    doLogout: function () {
        var session = this.getViewModel().get("theSession");
        session.phantom = false;
        session.crudState = 'R';
        session.set("username", null); // used to refresh session status
        session.set("_id", 'current');

        // set action id
        CMDBuildUI.util.Ajax.setActionId('logout');
        // delete session
        session.erase({
            success: function (record, operation) {
                var response = operation.getResponse(),
                    redirectToUrl;

                // get redirect property from response, if exists
                if (response && response.status === 200) {
                    var responsedata = Ext.decode(response.responseText);
                    redirectToUrl = responsedata.redirect;
                }

                // set redirect from global configs
                redirectToUrl = redirectToUrl || CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.common.redirectonlogout);

                if (redirectToUrl) {
                    // redirect to external url
                    window.location.replace(redirectToUrl);
                } else {
                    // redirect to login page
                    CMDBuildUI.util.Utilities.redirectTo("login", true);
                    window.location.reload();
                }
            },
            failure: function () {
                window.location.reload();
            }
        });
    },

    /******************* MANAGEMENT ********************/
    /**
     * Show management page
     */
    onBeforeShowManagement: function (action) {
        var me = this;
        this.getViewModel().set('isAdministrationModule', false);
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);
        CMDBuildUI.util.Navigation.clearCurrentContext();
        // remove all from main container
        var container = CMDBuildUI.util.Navigation.getMainContainer(true);
        // add load mask
        var loadmask = new Ext.LoadMask({
            target: container
        });
        loadmask.show();

        CMDBuildUI.util.helper.SessionHelper.checkSessionValidity().then(function () {

            // if grouping email by status is null set it with user preference or system comnfig
            if (!CMDBuildUI.util.Navigation.getGroupEmailByStatus()) {
                var groupingUserPref = CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.notifications.groupEmailByStatus),
                    groupingConfig = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.email.groupByStatus);
                /**
                 * groupingUserPref value can be null(default)|true|false
                 * groupingConfig value can be true | false
                 */
                CMDBuildUI.util.Navigation.setGroupEmailByStatus(groupingUserPref !== null ? groupingUserPref : groupingConfig);
            }

            CMDBuildUI.util.Ajax.setActionId(null);
            Ext.Promise.all([
                CMDBuildUI.util.Stores.loadClassesStore(),
                CMDBuildUI.util.Stores.loadProcessesStore(),
                CMDBuildUI.util.Stores.loadReportsStore(),
                CMDBuildUI.util.Stores.loadDashboardsStore(),
                CMDBuildUI.util.Stores.loadViewsStore(),
                CMDBuildUI.util.Stores.loadCustomPagesStore(),
                CMDBuildUI.util.Stores.loadCustomWidgetsStore(),
                CMDBuildUI.util.Stores.loadLookupTypesStore(),
                CMDBuildUI.util.Stores.loadDMSLookupTypesStore(),
                CMDBuildUI.util.Stores.loadDomainsStore(),
                CMDBuildUI.util.Stores.loadNavigationTreesStore(),
                CMDBuildUI.util.Stores.loadGroupsStore(),
                CMDBuildUI.util.Stores.loadDmsModelsStore()
            ]).then(function () {
                CMDBuildUI.util.Stores.loadMenuStore().then(function () {
                    // laod mask
                    CMDBuildUI.util.MenuStoreBuilder.initialize();
                    Ext.getBody().removeCls('administration');
                    Ext.getBody().addCls('management');
                    // resume action
                    action.resume();
                    // destroy load mask
                    loadmask.destroy();
                }, function (err) {
                    action.stop();
                    // redirect to login
                    me.redirectTo('login');
                    // destroy load mask
                    loadmask.destroy();
                });
            });
        }, function (err) {
            action.stop();
            // redirect to login
            me.redirectTo('login');
            // destroy load mask
            loadmask.destroy();
        });
    },
    showManagement: function () {
        CMDBuildUI.util.Navigation.addIntoMainContainer('management-maincontainer');
        // CMDBuildUI.util.helper.SessionHelper.getViewportVM().set('isAuthenticated', true);
        this.redirectToStartingUrl();
    },

    privates: {
        /**
         * This variable avoids launcing the 
         */
        _lastLoginEnded: true,

        redirectToStartingUrl: function () {
            var startingurl = CMDBuildUI.util.helper.SessionHelper.getStartingUrl();
            if (startingurl && startingurl !== "management" && startingurl !== "administration" && startingurl !== 'login') {
                this.redirectTo(CMDBuildUI.util.helper.SessionHelper.getStartingUrl());
                CMDBuildUI.util.helper.SessionHelper.clearStartingUrl();
            } else if (startingurl) {
                CMDBuildUI.util.helper.SessionHelper.clearStartingUrl();
            }
        },
        shouldUseCustomRouting: function (className) {
            var klass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(className);
            if (klass && klass.get('uiRouting_mode') !== 'default') {
                var url = Ext.String.format(
                    "{0}/{1}/{2}",
                    Ext.util.Inflector.pluralize(klass.get('uiRouting_mode')),
                    klass.get('uiRouting_target'),
                    Ext.History.getToken());
                this.redirectTo(url);
                return true;
            }
        }
    }
});