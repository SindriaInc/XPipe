/**
 * @file CMDBuildUI.util.Navigation
 * @module CMDBuildUI.util.Navigation
 * @author Tecnoteca srl 
 * @access public
 */
Ext.define("CMDBuildUI.util.Navigation", {
    singleton: true,

    /**
     * Available contexts.
     * 
     * @private
     * 
     * @constant {Object} contexts
     * @property {String} administration
     * @property {String} management
     */
    contexts: {
        administration: 'administration',
        management: 'management'
    },

    /**
     * Default management content title
     * 
     * @private
     * 
     * @constant {String} contexts
     */
    defaultManagementContentTitle: '<i class="fa fa-spinner fa-spin"></i>',

    /**
     * Returns main container.
     * 
     * @param {Boolean} [create=false] Create the container if not exists.
     * 
     * @returns {CMDBuildUI.view.main.content.Container}
     * 
     */
    getMainContainer: function (create) {
        var container = Ext.getCmp(CMDBuildUI.view.main.content.Container.elementId);
        if (!container && create) {
            container = Ext.create(CMDBuildUI.view.main.content.Container);
        }
        return container;
    },

    /**
     * Remove all contents from main container.
     * 
     * @param {Boolean} [create=false] Create the container if not exists.
     * 
     * @returns {CMDBuildUI.view.main.content.Container}
     * 
     */
    clearMainContainer: function (create) {
        var container = this.getMainContainer(create);
        if (container) {
            container.removeAll(true);
        }
        var administrationContent = this.getMainAdministrationContainer();
        if (administrationContent) {
            administrationContent.destroy();
        }
        return container;
    },

    /**
     * Add a component into main container.
     * 
     * @param {String} xtype The component xtype
     * @param {Object} [parameters] Component configuration.
     * 
     * @returns {CMDBuildUI.view.main.content.Container}
     * 
     */
    addIntoMainContainer: function (xtype, parameters) {
        var config = Ext.applyIf({
            xtype: xtype
        }, parameters);

        // get and clear container
        var container = this.clearMainContainer(true);
        // add component
        container.add(config);
        return container;
    },

    /**
     * Returns the management navigation tree.
     * 
     * @returns {CMDBuildUI.view.management.navigation.Tree}
     * 
     */
    getManagementNavigation: function () {
        var main = CMDBuildUI.util.Navigation.getMainContainer();
        var nav = main.query('management-navigation-tree');
        if (nav.length) {
            return nav[0];
        }
    },

    /**
     * Refresh navigation tree
     * 
     */
    refreshNavigationTree: function () {
        var selection = this.getManagementNavigation().getSelection();
        selection.collapse();
        selection._childrenloaded = false;
        selection.removeAll();
        selection.expand();
    },

    /**
     * Returns management content container.
     * 
     * @param {Boolean} [create=false] Create the container if not exists.
     * 
     * @returns {CMDBuildUI.view.management.Content}
     * 
     */
    getManagementContainer: function (create) {
        var container = Ext.getCmp(CMDBuildUI.view.management.Content.elementId);
        if (!container && create) {
            container = Ext.create(CMDBuildUI.view.management.Content);
        }
        return container;
    },

    /**
     * Clear main container.
     * 
     * @param {Boolean} [create=false] Create the container if not exists.
     * 
     * @returns {CMDBuildUI.view.management.Content}
     */
    clearManagementContainer: function (create) {
        var container = this.getManagementContainer(create);
        if (container) {
            container.removeAll(true);
        }
        return container;
    },

    /**
     * Add a component into management content container.
     * 
     * @param {String} xtype
     * @param {Object} [parameters]
     * 
     * @returns {CMDBuildUI.view.management.Content}
     * 
     */
    addIntoManagemenetContainer: function (xtype, parameters) {
        var config = Ext.applyIf({
            xtype: xtype
        }, parameters);

        // get and clear container
        var container = this.clearManagementContainer(true);
        // add component
        container.add(config);
        return container;
    },


    /**
     * Return details window container.
     * 
     * @param {Boolean} [create=false] Create the container if not exists.
     * 
     * @returns {CMDBuildUI.view.management.DetailsWindow}
     * 
     */
    getManagementDetailsWindow: function (create) {
        var container = Ext.getCmp(CMDBuildUI.view.management.DetailsWindow.elementId);
        if (!container && create) {
            container = Ext.create(CMDBuildUI.view.management.DetailsWindow);
        }
        return container;
    },

    /**
     * Clear details window container.
     * 
     * @param {Boolean} [create=false] Create the container if not exists.
     * 
     * @returns {CMDBuildUI.view.management.DetailsWindow}
     * 
     */
    clearManagementDetailsWindow: function (create) {
        var container = this.getManagementDetailsWindow(create);
        if (container) {
            container.removeAll(true);
        }
        return container;
    },

    /**
     * Removes detail window container.
     * 
     * @param {Boolead} [suspendEvents=false] If true suspend events before the window removal.
     * 
     */
    removeManagementDetailsWindow: function (suspendEvents) {
        var container = this.getManagementDetailsWindow(false);
        if (container) {
            if (suspendEvents) {
                container.suspendEvents();
            }
            container.close();
        }
    },

    /**
     * Add a component into management container.
     * 
     * @param {String} xtype
     * @param {Object} [parameters]
     * 
     * @returns {CMDBuildUI.view.management.DetailsWindow}
     * 
     */
    addIntoManagementDetailsWindow: function (xtype, parameters) {
        var config = Ext.applyIf({
            xtype: xtype
        }, parameters);

        // get and clear container
        var container = this.clearManagementDetailsWindow(true);
        // add component
        container.add(config);
        return container;
    },

    /**
     * Update the title of detail window container.
     * 
     * @private
     * 
     * @param {String} newtitle
     */
    updateTitleOfManagementDetailsWindow: function (newtitle) {
        var container = this.getManagementDetailsWindow(false);
        if (container) {
            container.setTitle(newtitle);
        }
    },


    /**
     * Return administration container.
     * 
     * @private
     * 
     * @param {Boolean} [create=false] Create the container if not exists.
     * 
     * @returns {CMDBuildUI.view.administration.Content}
     */
    getMainAdministrationContainer: function (create) {
        // var container = Ext.getBody().down('administration-content');
        var container = Ext.getCmp(CMDBuildUI.view.administration.Content.elementId);
        if (!container && create) {

            container = Ext.create(CMDBuildUI.view.administration.Content);
        }
        return container;
    },

    /**
     * Clear administration container.
     * 
     * @private
     * 
     * @param {Boolean} [create=false] Create the container if not exists.
     * 
     * @returns {CMDBuildUI.view.administration.Content}
     */
    clearMainAdministrationContainer: function (create) {
        var container = this.getMainAdministrationContainer(create);
        if (container) {
            container.removeAll(true);
        }
        return container;
    },

    /**
     * Add a component into administration container.
     * 
     * @private
     * 
     * @param {String} xtype
     * @param {Object} [parameters]
     * 
     * @returns {CMDBuildUI.view.administration.Content}
     */
    addIntoMainAdministrationContent: function (xtype, parameters) {
        var config = Ext.applyIf({
            xtype: xtype
        }, parameters);

        // get and clear container
        var container = this.clearMainAdministrationContainer(true);
        // add component
        container.add(config);
        return container;
    },


    /**
     * Return administration details window container.
     * 
     * @private
     * 
     * @param {Boolean} [create] Create the container if not exists.
     * 
     * @returns {CMDBuildUI.view.administration.DetailsWindow}
     */
    getAdministrationDetailsWindow: function (create) {
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId);
        if (!container && create) {
            container = Ext.create(CMDBuildUI.view.administration.DetailsWindow.elementId);
        }
        return container;
    },

    /**
     * Clear administration container.
     * 
     * @private
     * 
     * @param {Boolean} [create] Create the container if not exists.
     * 
     * @returns {CMDBuildUI.view.administration.DetailsWindow}
     */
    clearAdministrationDetailsWindow: function (create) {
        var container = this.getAdministrationDetailsWindow(create);
        if (container) {
            container.removeAll(true);
        }
        return container;
    },

    /**
     * Removes administration detail window container.
     * 
     * @private
     */
    removeAdministrationDetailsWindow: function () {
        var panel = this.clearAdministrationDetailsWindow(false);
        if (panel) {
            panel.destroy();
        }
    },

    /**
     * Add a component into administration details window container.
     * 
     * @private
     * 
     * @param {String} xtype
     * @param {Object} [parameters]
     * 
     * @returns {CMDBuildUI.view.administration.DetailsWindow}
     */
    addIntoAdministrationDetailsWindow: function (xtype, parameters) {
        var config = Ext.applyIf({
            xtype: xtype
        }, parameters);

        // get and clear container
        var container = this.clearAdministrationDetailsWindow(true);
        // add component
        container.add(config);
        return container;
    },


    /**
     * Return current main navigation context.
     * 
     * @returns {Object} Current main context info.
     * 
     */
    getCurrentContext: function () {
        return this._currentcontext;
    },

    /**
     * Update current main navigation context.
     * 
     * @private
     * 
     * @param {String} context One of {@link CMDBuildUI.util.Navigation#contexts CMDBuildUI.util.Navigation.contexts} property.
     * @param {String} objectType Object type. One of {@link CMDBuildUI.util.helper.ModelHelper#objecttypes CMDBuildUI.util.helper.ModelHelper.objecttypes} properties.
     * @param {String} objectTypeName Class/Process/View/... name.
     * @param {Number|String} [objectId] Card/Process instance/... id.
     * @param {Object} [other] Other context info.
     * 
     * @returns {Object} Current main navigation context info.
     */
    updateCurrentContext: function (context, objectType, objectTypeName, objectId, other) {
        this._currentcontext = Ext.applyIf({
            context: context,
            objectType: objectType,
            objectTypeName: objectTypeName,
            objectId: objectId
        }, other);
        return this.getCurrentContext();
    },

    /**
     * Clear current main navigation context.
     * 
     */
    clearCurrentContext: function () {
        this._currentcontext = {};
        return this.getCurrentContext();
    },

    /**
     * Update current management main navigation context.
     * 
     * @param {String} objectType Object type. One of {@link CMDBuildUI.util.helper.ModelHelper#objecttypes CMDBuildUI.util.helper.ModelHelper.objecttypes} properties.
     * @param {String} objectTypeName Class/Process/View/... name.
     * @param {Number|String} [objectId] Card/Process instance/... id.
     * @param {Object} [other] Other context info.
     * 
     * @returns {Object} Current main navigation context info.
     * 
     */
    updateCurrentManagementContext: function (objectType, objectTypeName, objectId, other) {
        return this.updateCurrentContext(
            this.contexts.management,
            objectType,
            objectTypeName,
            objectId,
            other
        );
    },

    /**
     * Update current Card or Process instance id in management main navigation context.
     * 
     * @param {Number|String} objectId Card or Process id
     * 
     */
    updateCurrentManagementContextObjectId: function (objectId) {
        switch (CMDBuildUI.util.Navigation._currentcontext.objectType) {
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.custompage:
                this.updateCurrentManagementContextCustomPageObjectId(objectId);
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.view:
                this.updateCurrentManagementContextViewObjectId(objectId);
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                this._currentcontext.objectId = objectId;
                break;
            default:
                this._currentcontext.objectId = objectId;
                break;
        }
    },

    /**
     * Update current action in management main navigation context.
     * 
     * @param {String} action
     * 
     */
    updateCurrentManagementContextAction: function (action) {
        this._currentcontext.currentaction = action;
    },

    /**
     * Update current activity id in management main navigation context. Used for process instances.
     * 
     * @param {String} activity
     * 
     */
    updateCurrentManagementContextActivity: function (activity) {
        this._currentcontext.currentactivity = activity;
    },

    /**
     * Update current object type used in a Custom Page in management main navigation context.
     * 
     * @param {String} cpObjectType Object type. One of {@link CMDBuildUI.util.helper.ModelHelper#objecttypes CMDBuildUI.util.helper.ModelHelper.objecttypes} properties.
     * 
     */
    udateCurrentManagementContextCustomPageObjectType: function (cpObjectType) {
        this._currentcontext.cpObjectType = cpObjectType;
    },

    /**
     * Update current object type used in a View in management main navigation context.
     * 
     * @param {String} vwObjectType Object type. One of {@link CMDBuildUI.util.helper.ModelHelper#objecttypes CMDBuildUI.util.helper.ModelHelper.objecttypes} properties.
     * 
     */
    udateCurrentManagementContextViewObjectType: function (vwObjectType) {
        this._currentcontext.vwObjectType = vwObjectType;
    },

    /**
     * Update current Class or Process name used in a Custom Page in management main navigation context.
     * 
     * @param {String} cpObjectTypeName Class or Process name.
     * 
     */
    updateCurrentManagementContextCustomPageObjectTypeName: function (cpObjectTypeName) {
        this._currentcontext.cpObjectTypeName = cpObjectTypeName;
    },

    /**
     * Update current Class or Process name used in a View in management main navigation context.
     * 
     * @param {String} vwObjectTypeName Class or Process name.
     * 
     */
    updateCurrentManagementContextViewObjectTypeName: function (vwObjectTypeName) {
        this._currentcontext.vwObjectTypeName = vwObjectTypeName;
    },

    /**
     * Update current Card or Process instance id used in a Custom Page in management main navigation context.
     * 
     * @param {String} cpObjectId Card or Process instance id.
     * 
     */
    updateCurrentManagementContextCustomPageObjectId: function (cpObjectId) {
        this._currentcontext.cpObjectId = cpObjectId;
    },

    /**
     * Update current Card or Process instance id used in a View in management main navigation context.
     * 
     * @param {String} vwObjectId Card or Process instance id.
     * 
     */
    updateCurrentManagementContextViewObjectId: function (vwObjectId) {
        this._currentcontext.vwObjectId = vwObjectId;
    },

    /**
     * Update current administration main context.
     * 
     * @private
     * 
     * @param {String} objectType Object type. One of {@link CMDBuildUI.util.helper.ModelHelper#objecttypes CMDBuildUI.util.helper.ModelHelper.objecttypes} properties.
     * @param {String} objectTypeName Class/Process/View/... name.
     * @param {String} [objectId] Card or Process instance id.
     * @param {Object} [other] Other context indo.
     * 
     * @returns {Object} Current main context navigation info.
     */
    updateCurrentAdministrationContext: function (objectType, objectTypeName, objectId, other) {
        return this.updateCurrentContext(
            this.contexts.administration,
            objectType,
            objectTypeName,
            objectId,
            other
        );
    },

    /**
     * Check the consistency of the current context.
     * 
     * @param {String} objectType Object type. One of {@link CMDBuildUI.util.helper.ModelHelper#objecttypes CMDBuildUI.util.helper.ModelHelper.objecttypes} properties.
     * @param {String} objectTypeName Class/Process/View/... name.
     * @param {Boolean} [checkHierarchy=false] Check the objectTypeName also in the item hierarchy.
     * 
     * @returns {Boolean} Return true if main content is consistent with the selection.
     * 
     */
    checkCurrentContext: function (objectType, objectTypeName, checkHierarchy) {
        var result = false;

        var context = this.getCurrentContext();
        if (!context) {
            result = false;
        } else {
            if (context.objectType === objectType && context.objectTypeName === objectTypeName) {
                result = true;
            } else if (!checkHierarchy) {
                result = false;
            } else {
                // check hierarchy
                var item;
                switch (objectType) {
                    case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                        item = CMDBuildUI.util.helper.ModelHelper.getClassFromName(objectTypeName);
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                        item = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(objectTypeName);
                        break;
                }
                if (item) {
                    result = Ext.Array.contains(item.getHierarchy(), context.objectTypeName);
                }
            }
        }

        if (!result) {
            this.clearCurrentRowTab();
        }

        return result;
    },

    /**
     * Check the consistency of the object id in current context.
     * 
     * @param {Number|String} objectId  The object id to check.
     * 
     * @returns {Boolean}
     */
    checkCurrentManagementContextObjectId: function (objectId) {
        return this._currentcontext.objectId == objectId;
    },

    /**
     * Check the consistency of the action in current context.
     * 
     * @param {String} action
     * 
     * @returns {Boolean}
     */
    checkCurrentManagementContextAction: function (action) {
        return this._currentcontext.currentaction === action;
    },

    /**
     * Check the consistency of the actovity id in current context.
     * 
     * @param {String} activity
     * 
     * @returns {Boolean}
     */
    checkCurrentManagementContextActivity: function (activity) {
        return this._currentcontext.currentactivity === activity;
    },

    /**
     * Check the consistency of the Custom page object type in current context.
     * 
     * @param {String} cpObjectType
     * 
     * @returns {Boolean}
     */
    checkCurrentManagementContextCustomPageObjectType: function (cpObjectType) {
        return this._currentcontext.cpObjectType === cpObjectType;
    },

    /**
     * Check the consistency of the View object type in current context.
     * 
     * @param {String} vwObjectType
     * 
     * @returns {Boolean}
     */
    checkCurrentManagementContextViewObjectType: function (vwObjectType) {
        return this._currentcontext.vwObjectType === vwObjectType;
    },

    /**
     * Check the consistency of the Custom page object type name in current context.
     * 
     * @param {String} cpObjectTypeName
     * 
     * @returns {Boolean}
     */
    checkCurrentManagementContextCustomPageObjectTypeName: function (cpObjectTypeName) {
        return this._currentcontext.cpObjectTypeName === cpObjectTypeName;
    },

    /**
     * Check the consistency of the View object type name in current context.
     * 
     * @param {String} cpObjectTypeName
     * 
     * @returns {Boolean}
     */
    checkCurrentManagementContextViewObjectTypeName: function (vwObjectTypeName) {
        return this._currentcontext.vwObjectTypeName === vwObjectTypeName;
    },

    /**
     * Check the consistency of the Custom page object id in current context.
     * 
     * @param {String} cpObjectId
     * 
     * @returns {Boolean}
     */
    checkCurrentManagementContextCustomPageObjectId: function (cpObjectId) {
        return this._currentcontext.cpObjectId === cpObjectId;
    },

    /**
     * Check the consistency of the View object id in current context.
     * 
     * @param {String} vwObjectId
     * 
     * @returns {Boolean}
     */
    checkCurrentManagementContextViewObjectId: function (vwObjectId) {
        return this._currentcontext.vwObjectId === vwObjectId;
    },

    /**
     * Get cards routes.
     * 
     * @param {String} className
     * @param {Number|String} [classId]
     * @param {String} [action] 
     * @param {Boolean} [skipContext=false] If true skips the context check.
     * 
     */
    getClassBaseUrl: function (className, classId, action, skipContext) {
        skipContext = skipContext == null ? false : skipContext;
        var url = 'classes';
        var actionUrl;
        if (className) {
            var klass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(className);
            var uiRouting_custom = klass.get('uiRouting_custom') || {};
            actionUrl = uiRouting_custom.showGrid;

            url = Ext.String.format("{0}/{1}/cards", url, className);

            if (action == CMDBuildUI.mixins.DetailsTabPanel.actions.create) {
                actionUrl = uiRouting_custom.addCard;
                url = Ext.String.format('{0}/{1}', url, action);

            } else if (classId) {
                // viewInGrid
                url = Ext.String.format("{0}/{1}", url, classId);

                if (action) {
                    // view/edit/...
                    var actions = CMDBuildUI.util.Navigation.getClassActionsMap();
                    actionUrl = uiRouting_custom[actions[action]];
                    url = Ext.String.format("{0}/{1}", url, action);
                }
            }
            if (!Ext.isEmpty(actionUrl)) {
                actionUrl = actionUrl.replace(':className', className);
                actionUrl = actionUrl.replace(':idCard', classId);
                return actionUrl;
            }
            if (klass && klass.get('uiRouting_mode') !== 'default' && klass.get('uiRouting_target')) {
                return Ext.String.format(
                    "{0}/{1}/{2}",
                    Ext.util.Inflector.pluralize(klass.get('uiRouting_mode')),
                    klass.get('uiRouting_target'),
                    url);
            }
        }

        if (!skipContext) {
            switch (CMDBuildUI.util.Navigation._currentcontext.objectType) {
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.custompage:
                    url = Ext.String.format(
                        "{0}/{1}/{2}",
                        'custompages',
                        CMDBuildUI.util.Navigation._currentcontext.objectTypeName,
                        url);
                    break;
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.view:
                    url = Ext.String.format(
                        '{0}/{1}/{2}',
                        'views',
                        CMDBuildUI.util.Navigation._currentcontext.objectTypeName,
                        url
                    );
                    break;
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.navtreecontent:
                    url = Ext.String.format(
                        '{0}/{1}/{2}',
                        'navigation',
                        CMDBuildUI.util.Navigation._currentcontext.objectTypeName,
                        url
                    );
                    break;
            }
        }

        return url;
    },

    /**
     * Get process instances routes.
     * 
     * @param {String} processName 
     * @param {Number|String} [instanceId] 
     * @param {String} [activityId] 
     * @param {String} [action] 
     * @param {Boolean} [skipAction=false] If true skips the context check.
     * 
     */
    getProcessBaseUrl: function (processName, instanceId, activityId, action, skipContext) {
        skipContext = skipContext == null ? false : skipContext;
        var url = '';
        var actionUrl;
        if (processName) {
            var process = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(processName);
            var uiRouting_custom = process ? process.get('uiRouting_custom') : {};
            actionUrl = uiRouting_custom.showGrid;

            url = Ext.String.format('processes/{0}/instances', processName);

            if (action == CMDBuildUI.mixins.DetailsTabPanel.actions.create) {
                actionUrl = uiRouting_custom.addCard;
                url = Ext.String.format('{0}/{1}', url, action);

            } else if (instanceId) {
                url = Ext.String.format('{0}/{1}', url, instanceId);

                if (activityId) {
                    url = Ext.String.format('{0}/activities/{1}', url, activityId);

                    if (action) {
                        var actions = CMDBuildUI.util.Navigation.getProcessActionsMap();
                        actionUrl = uiRouting_custom[actions[action]];
                        url = Ext.String.format('{0}/{1}', url, action);
                    }
                }
            }

            if (!Ext.isEmpty(actionUrl)) {
                actionUrl = actionUrl.replace(':processName', processName);
                actionUrl = actionUrl.replace(':idInstance', instanceId);
                actionUrl = actionUrl.replace(':activityId', activityId);
                return actionUrl;
            }
            if (process && process.get('uiRouting_mode') !== 'default' && process.get('uiRouting_target')) {
                return Ext.String.format(
                    "{0}/{1}/{2}",
                    Ext.util.Inflector.pluralize(process.get('uiRouting_mode')),
                    process.get('uiRouting_target'),
                    url);
            }
        }
        if (!skipContext) {
            switch (CMDBuildUI.util.Navigation._currentcontext.objectType) {
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.custompage:
                    url = Ext.String.format(
                        "{0}/{1}/{2}",
                        'custompages',
                        CMDBuildUI.util.Navigation._currentcontext.objectTypeName,
                        url);
                    break;
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.view:
                    url = Ext.String.format(
                        '{0}/{1}/{2}',
                        'views',
                        CMDBuildUI.util.Navigation._currentcontext.objectTypeName,
                        url
                    );
                    break;
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.navtreecontent:
                    url = Ext.String.format(
                        '{0}/{1}/{2}',
                        'navigation',
                        CMDBuildUI.util.Navigation._currentcontext.objectTypeName,
                        url
                    );
                    break;
            }
        }

        return url;
    },

    /**
     * Get scheduler routes.
     * 
     * @param {Number|String} [scheduleId] 
     * @param {String} [action] 
     * @param {Boolean} [skipContext=false] If true skips the context check.
     * 
     */
    getScheduleBaseUrl: function (scheduleId, action, skipContext) {
        skipContext = skipContext == null ? false : true;

        var url = 'events';


        if (action == CMDBuildUI.mixins.DetailsTabPanel.actions.create) {

            url = Ext.String.format('{0}/{1}', url, action);
        } else {

            if (scheduleId) {
                url = Ext.String.format('{0}/{1}', url, scheduleId);

                if (action) {
                    url = Ext.String.format('{0}/{1}', url, action);
                }

            }
        }

        if (!skipContext) {
            switch (CMDBuildUI.util.Navigation._currentcontext.objectType) {
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.custompage:
                    url = Ext.String.format(
                        "{0}/{1}/{2}",
                        'custompages',
                        CMDBuildUI.util.Navigation._currentcontext.objectTypeName,
                        url);
                    break;
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.view:
                    url = Ext.String.format(
                        '{0}/{1}/{2}',
                        'views',
                        CMDBuildUI.util.Navigation._currentcontext.objectTypeName,
                        url
                    );
                    break;
            }
        }

        return url;
    },

    /**
     * Returns the active tab in row expander.
     * 
     * @returns {String} Current row tab.
     * 
     */
    getCurrentRowTab: function () {
        return this._currentrowtab;
    },

    /**
     * Update the active tab in row expander info.
     * 
     * @param {String} tab selected tab action in row.
     * 
     */
    updateCurrentRowTab: function (tab) {
        this._currentrowtab = tab;
    },

    /**
     * Clears the active tab in row expander info.
     * 
     */
    clearCurrentRowTab: function () {
        if (
            this._currentrowtab !== CMDBuildUI.mixins.DetailsTabPanel.actions.view &&
            this._currentrowtab !== CMDBuildUI.mixins.DetailsTabPanel.actions.relations
        ) {
            this.updateCurrentRowTab();
        }
    },

    /**
     * 
     * @param {String} objectTypeName 
     * 
     * @return {Boolean} 
     */
    shouldUseCustomRouting: function (objectTypeName) {
        var klass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName);
        if (klass && klass.get('uiRouting_mode') !== 'default' && klass.get('uiRouting_target')) {
            var url = Ext.String.format(
                "{0}/{1}/{2}",
                Ext.util.Inflector.pluralize(klass.get('uiRouting_mode')),
                klass.get('uiRouting_target'),
                Ext.History.getToken());
            this.redirectTo(url, true);
            return true;
        }
        return false;
    },

    /**
     * create map for classes custom routes
     * 
     * @return {Object}
     */
    getClassActionsMap: function () {
        var actions = {};
        actions.showGrid = 'showGrid';
        actions.viewInRow = 'viewInRow';
        actions[CMDBuildUI.mixins.DetailsTabPanel.actions.create] = 'addCard';
        actions[CMDBuildUI.mixins.DetailsTabPanel.actions.view] = 'viewCard';
        actions[CMDBuildUI.mixins.DetailsTabPanel.actions.clone] = 'cloneCard';
        actions[CMDBuildUI.mixins.DetailsTabPanel.actions.clonecardandrelations] = 'cloneCardRelations';
        actions[CMDBuildUI.mixins.DetailsTabPanel.actions.edit] = 'modifyCard';
        actions[CMDBuildUI.mixins.DetailsTabPanel.actions.details] = 'detailsTab';
        actions[CMDBuildUI.mixins.DetailsTabPanel.actions.notes] = 'notesTab';
        actions[CMDBuildUI.mixins.DetailsTabPanel.actions.relations] = 'relationsTab';
        actions[CMDBuildUI.mixins.DetailsTabPanel.actions.history] = 'historyTab';
        actions[CMDBuildUI.mixins.DetailsTabPanel.actions.emails] = 'emailsTab';
        actions[CMDBuildUI.mixins.DetailsTabPanel.actions.attachments] = 'attachmentsTab';
        actions[CMDBuildUI.mixins.DetailsTabPanel.actions.schedules] = 'schedulesTab';
        return actions;
    },

    /**
     * create map for processes custom routes
     * 
     * @return {Object}
     */
    getProcessActionsMap: function () {
        var actions = {};
        actions.showGrid = 'showGrid';
        actions.viewInRow = 'viewInRow';
        actions[CMDBuildUI.mixins.DetailsTabPanel.actions.create] = 'addProcessInstance';
        actions[CMDBuildUI.mixins.DetailsTabPanel.actions.view] = 'viewProcessInstance';
        actions[CMDBuildUI.mixins.DetailsTabPanel.actions.edit] = 'modifyProcessInstance';
        actions[CMDBuildUI.mixins.DetailsTabPanel.actions.notes] = 'notesProcessInstanceTab';
        actions[CMDBuildUI.mixins.DetailsTabPanel.actions.relations] = 'relationsProcessInstanceTab';
        actions[CMDBuildUI.mixins.DetailsTabPanel.actions.history] = 'historyProcessInstanceTab';
        actions[CMDBuildUI.mixins.DetailsTabPanel.actions.emails] = 'emailsProcessInstanceTab';
        actions[CMDBuildUI.mixins.DetailsTabPanel.actions.attachments] = 'attachmentsProcessInstanceTab';
        return actions;

    },

    /**
     * Update the value of groupByStatus checkbox on emails.
     * 
     * @param {Boolean} value status of checkbox.
     * 
     */
    setGroupEmailByStatus: function (value) {
        this._groupEmailByStatus = value;
    },

    /**
     * Get the value of groupByStatus checkbox on emails.
     *      
     * @return {Boolean}
     * 
     */
    getGroupEmailByStatus: function () {
        return this._groupEmailByStatus;
    },

    privates: {
        /**
         * @property {Object} _currentcontext
         * Current application context.
         */
        _currentcontext: {},

        /**
         * @property {String} _currentrowtab
         * Current row tab.
         */
        _currentrowtab: null,

        /**
         * @property {Boolean} _groupEmailByStatus
         * Group emails by status.
         */
        _groupEmailByStatus: null

    }
});