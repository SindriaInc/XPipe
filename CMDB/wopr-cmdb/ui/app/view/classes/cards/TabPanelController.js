Ext.define('CMDBuildUI.view.classes.cards.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.classes-cards-tabpanel',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            tabchange: 'onTabChange'
        },
        '#opentool': {
            click: 'onOpenToolClick'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#cloneMenuBtn': {
            click: 'onCloneMenuBtnClick',
            destroy: 'onDestroyTool'
        },
        '#printBtn': {
            click: 'onPrintBtnClick',
            destroy: 'onDestroyTool'
        },
        '#relgraphBtn': {
            click: 'onRelationGraphBtnClick'
        },
        '#bimBtn': {
            click: 'onBimButtonClick'
        },
        '#helpBtn': {
            click: 'onHelpBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.TabPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        // set view model variables
        var me = this,
            vm = view.lookupViewModel(),
            readonly = view.getReadOnlyTabs();

        // set objectTypeName and objectId for inline-view
        if (!vm.get("objectTypeName") && !vm.get("objectId")) {
            var config = view.getInitialConfig();
            if (!Ext.isEmpty(config._rowContext)) {
                var record = config._rowContext.record; // get widget record
                if (record && record.getData()) {
                    vm.set("objectTypeName", record.getRecordType());
                    vm.set("objectId", record.getRecordId());
                }
            }
        }

        // define action
        var action;
        if (readonly) {
            vm.set("action", CMDBuildUI.mixins.DetailsTabPanel.actions.readonly);
            action = CMDBuildUI.util.Navigation.getCurrentRowTab();
        } else {
            action = vm.get("action");
        }

        // load the object
        var objectTypeName = vm.get("objectTypeName"),
            objectId = vm.get("objectId");
        if (objectTypeName) {
            CMDBuildUI.util.helper.ModelHelper.getModel(
                CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
                objectTypeName
            ).then(function (model) {
                vm.set("objectModel", model);

                if (
                    objectId &&
                    action !== CMDBuildUI.mixins.DetailsTabPanel.actions.clone &&
                    action !== CMDBuildUI.mixins.DetailsTabPanel.actions.clonecardandrelations
                ) {
                    vm.linkTo("theObject", {
                        type: model.getName(),
                        id: objectId
                    });
                } else {
                    vm.linkTo("theObject", {
                        type: model.getName(),
                        create: {
                            _type: objectTypeName
                        }
                    });
                }
            });
        }

        // if panel is shown in detail window
        if (view.isInDetailWindow()) {
            // update url on window close
            view.mon(
                CMDBuildUI.util.Navigation.getManagementDetailsWindow(),
                'beforeclose',
                this.onManagementDetailsWindowBeforeClose,
                this
            );
        } else {
            var height = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.inlinecard.height);
            view.setHeight(view.up().getHeight() * height / 100);
        }

        var singleinit = false;
        vm.bind('{theObject}',
            function (theObject) {
                if (!Ext.isEmpty(theObject) && !singleinit) {
                    Ext.asap(function () {
                        me.addTabs(view, theObject, action, readonly);
                    })
                    singleinit = true;
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
        var tabAction,
            vm = view.lookupViewModel(),
            tool = view.down("#opentool");

        if (newtab && newtab.tabAction) {
            tabAction = newtab.tabAction;
            vm.set("activetab", tabAction);
        }

        if (view.isInDetailWindow()) {
            CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(tabAction);
            if (tabAction !== CMDBuildUI.mixins.DetailsTabPanel.actions.clonecardandrelations) {
                vm.set("titledata.action", newtab.tabConfig.tooltip);
            }
            vm.set("titledata.operation", newtab.operationMode);
            CMDBuildUI.util.Ajax.setActionId("class.card." + tabAction + ".open");
            CMDBuildUI.util.Utilities.redirectTo(this.getBasePath(tabAction));
        } else if (tool) {

            // update action
            tool.action = tabAction;
            CMDBuildUI.util.Navigation.updateCurrentRowTab(tabAction);

            // update tooltip
            var toolel = tool.getEl();
            if (toolel) {
                var tooltip = Ext.String.format(
                    "{0} {1}",
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
     * On management details windows close
     */
    onManagementDetailsWindowBeforeClose: function () {
        CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(undefined);
        CMDBuildUI.util.Navigation.updateCurrentManagementContextObjectId(undefined);
        CMDBuildUI.util.Utilities.redirectTo(this.getBasePath());
    },

    /**
     * On close tool click
     */
    onClooseToolClick: function () {
        CMDBuildUI.util.Utilities.redirectTo(this.getBasePath());
    },

    /**
     *
     * @param {Ext.panel.Tool} tool
     * @param {Ext.event.Event} e
     * @param {Ext.Component} owner
     * @param {Object} eOpts
     */
    onOpenToolClick: function (tool, e, owner, eOpts) {
        var view = this.getView(),
            vm = view.lookupViewModel(),
            objectTypeName = vm.get("objectTypeName"),
            objectId = vm.get("objectId");
        CMDBuildUI.view.classes.cards.Util.doOpenCard(objectTypeName, objectId, tool.action);
    },

    /**
     * Triggered on edit tool click.
     *
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */
    onEditBtnClick: function (tool, event, eOpts) {
        var view = this.getView(),
            vm = view.lookupViewModel(),
            objectTypeName = vm.get("objectTypeName"),
            objectId = vm.get("objectId");
        CMDBuildUI.view.classes.cards.Util.doEditCard(objectTypeName, objectId);
    },

    /**
     * Triggered on delete tool click.
     *
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (tool, event, eOpts) {
        var view = this.getView(),
            vm = view.lookupViewModel(),
            objectTypeName = vm.get("objectTypeName"),
            objectType = vm.get("objectType");
        CMDBuildUI.view.classes.cards.Util.doDeleteCard(objectType, objectTypeName, view.getFormObject()).then(function () {
            // close detail window
            if (view && !view.destroyed && view.isInDetailWindow && view.isInDetailWindow()) {
                vm.set("objectId", null);
                CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
            }
            if (CMDBuildUI.util.Navigation.getCurrentContext().objectType === CMDBuildUI.util.helper.ModelHelper.objecttypes.navtreecontent) {
                CMDBuildUI.util.Navigation.refreshNavigationTree();
            }
        });
    },

    /**
     *
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */
    onCloneMenuBtnClick: function (tool, event, eOpts) {
        var me = this,
            vm = me.getViewModel(),
            objectTypeName = vm.get("objectTypeName"),
            objectId = vm.get("objectId"),
            isSimpleClass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(objectTypeName).isSimpleClass();

        function onCloneMenuItemClick() {
            CMDBuildUI.view.classes.cards.Util.doCloneCard(objectTypeName, objectId);
        }

        function onCloneWithRelationsMenuItemClick() {
            CMDBuildUI.view.classes.cards.Util.doCloneCardWithRelation(objectTypeName, objectId);
        }

        if (isSimpleClass) {
            onCloneMenuItemClick();
        } else {
            var menu = Ext.create('Ext.menu.Menu', {
                autoShow: true,
                items: [{
                    tooltip: CMDBuildUI.locales.Locales.classes.cards.clone,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('file', 'regular'),
                    height: 32,
                    autoEl: {
                        'data-testid': 'cards-card-view-cloneCardBtn'
                    },
                    handler: onCloneMenuItemClick
                }, {
                    tooltip: CMDBuildUI.locales.Locales.classes.cards.clonewithrelations,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('file-alt', 'regular'),
                    height: 32,
                    autoEl: {
                        'data-testid': 'cards-card-view-cloneRelationsBtn'
                    },
                    handler: onCloneWithRelationsMenuItemClick
                }]
            });
            menu.setMinWidth(35);
            menu.setWidth(35);
            menu.alignTo(tool.el.id, 't-b?');
        }
    },

    /**
     *
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */
    onPrintBtnClick: function (tool, event, eOpts) {
        function printCard(format) {
            var vm = tool.lookupViewModel();

            // url and format
            var url = CMDBuildUI.util.api.Classes.getPrintCardUrl(
                vm.get("objectTypeName"),
                vm.get("objectId"),
                format
            );
            url += "?extension=" + format;

            // open file in popup
            CMDBuildUI.util.Utilities.openPrintPopup(url, format);
        }

        var menu = Ext.create('Ext.menu.Menu', {
            autoShow: true,
            items: [{
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('file-pdf', 'regular'),
                itemId: 'printPdfBtn',
                tooltip: CMDBuildUI.locales.Locales.common.grid.printpdf,
                text: CMDBuildUI.locales.Locales.common.grid.printpdf,
                printformat: 'pdf',
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.common.grid.printpdf',
                    text: 'CMDBuildUI.locales.Locales.common.grid.printpdf'
                },
                handler: function () {
                    printCard("pdf");
                }
            }, {
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('file-word', 'regular'),
                itemId: 'printOdtBtn',
                tooltip: CMDBuildUI.locales.Locales.common.grid.printodt,
                text: CMDBuildUI.locales.Locales.common.grid.printodt,
                printformat: 'odt',
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.common.grid.printodt',
                    text: 'CMDBuildUI.locales.Locales.common.grid.printodt'
                },
                handler: function () {
                    printCard("odt");
                }
            }]
        });
        menu.setMinWidth(35);
        menu.setWidth(35);
        menu.alignTo(tool.el.id, 't-b?');
    },

    /**
     *
     * @param {Ext.panel.Tool} tool
     * @param {Object} eOpts
     */
    onDestroyTool: function (tool, eOpts) {
        if (tool.menu) {
            tool.menu.destroy();
        }
    },

    /**
     * triggered on the relation graph btn click
     *
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */
    onRelationGraphBtnClick: function (tool, event, eOpts) {
        var obj = this.getView().getFormObject();
        CMDBuildUI.util.Ajax.setActionId("class.card.relgraph.open");
        CMDBuildUI.util.Utilities.openPopup('graphPopup', CMDBuildUI.locales.Locales.relationGraph.relationGraph, {
            xtype: 'graph-graphcontainer',
            _id: obj.get('_id'),
            _type: obj.get('_type'),
            _code: Ext.String.htmlEncode(obj.get('Code')),
            _description: Ext.String.htmlEncode(obj.get('Description'))
        });
    },

    /**
     *
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */
    onBimButtonClick: function (tool, event, eOpts) {
        var vm = this.getViewModel();
        CMDBuildUI.util.bim.Util.openBimPopup(
            CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.bim.viewer),
            vm.get('bim.projectid'),
            vm.get('bim.selectedid')
        );
    },

    /**
     *
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */
    onHelpBtnClick: function (tool, event, eOpts) {
        var vm = tool.lookupViewModel();
        var item = CMDBuildUI.util.helper.ModelHelper.getClassFromName(vm.get("objectTypeName"));
        CMDBuildUI.util.Utilities.openPopup(
            null,
            CMDBuildUI.locales.Locales.common.actions.help,
            {
                xtype: 'panel',
                html: item.get("_help_translation"),
                layout: 'fit',
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                cls: 'x-selectable',
                scrollable: true
            }
        );
    },

    onActivate: function (view, prevtabpanel, action) {
        var grids = Ext.ComponentQuery.query('relations-fieldset-grid');
        if (grids.length) {
            grids.forEach(function (grid) {
                grid.getStore().load();
            });
        }
    },

    privates: {
        addTabs: function (view, theObject, action, readonly) {
            var objectTypeName = theObject.get('_type'),
                classItem = CMDBuildUI.util.helper.ModelHelper.getClassFromName(objectTypeName),
                classDef = theObject.get('_model') || (classItem && classItem.getData() || {}),
                isSimpleClass = classDef.type === CMDBuildUI.model.classes.Class.classtypes.simple,
                disabletabs = false,
                enabledservices = CMDBuildUI.util.helper.Configurations.getEnabledFeatures(),
                card, tabcard, tabmasterdetail, tabnotes, tabrelations,
                tabhistory, tabemails, tabattachments, tabschedules, operationMode;

            // define card tab content
            switch (action) {
                case CMDBuildUI.mixins.DetailsTabPanel.actions.edit:
                    card = Ext.apply(view.getEditTabConfig(), {
                        tab: CMDBuildUI.mixins.DetailsTabPanel.actions.edit,
                        tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.edit
                    });
                    operationMode = CMDBuildUI.locales.Locales.common.actions.edit;
                    break;
                case CMDBuildUI.mixins.DetailsTabPanel.actions.create:
                    disabletabs = true;
                    card = Ext.apply(view.getCreateTabConfig(), {
                        tab: CMDBuildUI.mixins.DetailsTabPanel.actions.create,
                        tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.create
                    });
                    operationMode = CMDBuildUI.locales.Locales.common.actions.add;
                    break;
                case CMDBuildUI.mixins.DetailsTabPanel.actions.clone:
                    disabletabs = true;
                    card = Ext.apply(view.getCloneTabConfig(), {
                        tab: CMDBuildUI.mixins.DetailsTabPanel.actions.clone,
                        tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.clone
                    });
                    operationMode = CMDBuildUI.locales.Locales.classes.cards.clone;
                    break;
                case CMDBuildUI.mixins.DetailsTabPanel.actions.clonecardandrelations:
                    disabletabs = true;
                    card = Ext.apply(view.getCloneRelationsTabConfig(), {
                        tab: CMDBuildUI.mixins.DetailsTabPanel.actions.clonecardandrelations,
                        tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.clonecardandrelations
                    });
                    operationMode = CMDBuildUI.locales.Locales.classes.cards.clonewithrelations;
                    break;
                default:
                    card = Ext.apply(view.getViewTabConfig(), {
                        tab: CMDBuildUI.mixins.DetailsTabPanel.actions.view,
                        tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.view
                    });
                    operationMode = CMDBuildUI.locales.Locales.gis.view;
            }

            // Card tab
            tabcard = view.add({
                xtype: 'panel',
                iconCls: readonly ? undefined : CMDBuildUI.util.helper.IconHelper.getIconId('file-alt', 'solid'),
                items: [Ext.apply(card, view.getObjectFormBaseConfig())],
                reference: card.tab,
                bodyPadding: 0,
                layout: 'fit',
                autoScroll: true,
                padding: 0,
                tabAction: card.tabAction,
                operationMode: operationMode,
                tabConfig: {
                    tabIndex: 0,
                    title: readonly ? CMDBuildUI.locales.Locales.common.tabs.card : undefined,
                    tooltip: !readonly ? (CMDBuildUI.locales.Locales.common.tabs.card).toLowerCase() : undefined
                },
                listeners: {
                    activate: 'onActivate'
                }
            });

            // Master/Detail tab
            if (!isSimpleClass && classDef[CMDBuildUI.model.users.Grant.permissions.detail_read]) {
                tabmasterdetail = view.add({
                    xtype: 'panel',
                    iconCls: readonly ? undefined : CMDBuildUI.util.helper.IconHelper.getIconId('th-list', 'solid'),
                    items: [view.getRelationsMasterDetailTabConfig(classDef)],
                    reference: 'details',
                    bodyPadding: 0,
                    layout: 'fit',
                    autoScroll: true,
                    padding: 0,
                    tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.masterdetail,
                    operationMode: operationMode,
                    disabled: disabletabs,
                    tabConfig: {
                        tabIndex: 1,
                        title: readonly ? CMDBuildUI.locales.Locales.common.tabs.details : undefined,
                        tooltip: !readonly ? (CMDBuildUI.locales.Locales.common.tabs.details).toLowerCase() : undefined
                    }
                });
            }

            // Notes tab
            if (!isSimpleClass && classDef[CMDBuildUI.model.users.Grant.permissions.note_read]) {
                tabnotes = view.add({
                    xtype: 'panel',
                    iconCls: readonly ? undefined : CMDBuildUI.util.helper.IconHelper.getIconId('sticky-note', 'solid'),
                    items: [view.getNotesTabConfig(classDef)],
                    reference: 'notes',
                    bodyPadding: 0,
                    layout: 'fit',
                    autoScroll: true,
                    padding: 0,
                    tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.notes,
                    operationMode: operationMode,
                    tabConfig: {
                        tabIndex: 2,
                        title: readonly ? CMDBuildUI.locales.Locales.common.tabs.notes : undefined,
                        tooltip: !readonly ? (CMDBuildUI.locales.Locales.common.tabs.notes).toLowerCase() : undefined,
                        bind: {
                            text: (readonly ? CMDBuildUI.locales.Locales.common.tabs.notes : '') + '{tabcounters.notes ? \'<span class="badge">\' + 1 + "</span>" : null}'
                        }
                    },
                    disabled: disabletabs
                });
            }

            // Relations tab
            if (!isSimpleClass && classDef[CMDBuildUI.model.users.Grant.permissions.relation_read]) {
                tabrelations = view.add({
                    xtype: 'panel',
                    iconCls: readonly ? undefined : CMDBuildUI.util.helper.IconHelper.getIconId('link', 'solid'),
                    items: [view.getRelationsTabConfig(classDef)],
                    reference: 'relations',
                    bodyPadding: 0,
                    layout: 'fit',
                    autoScroll: true,
                    padding: 0,
                    tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.relations,
                    operationMode: operationMode,
                    tabConfig: {
                        tabIndex: 3,
                        title: readonly ? CMDBuildUI.locales.Locales.common.tabs.relations : undefined,
                        tooltip: !readonly ? (CMDBuildUI.locales.Locales.common.tabs.relations).toLowerCase() : undefined
                    },
                    disabled: disabletabs
                });
            }


            // History tab
            if (!isSimpleClass && classDef[CMDBuildUI.model.users.Grant.permissions.history_read]) {
                tabhistory = view.add({
                    xtype: 'panel',
                    iconCls: readonly ? undefined : CMDBuildUI.util.helper.IconHelper.getIconId('history', 'solid'),
                    items: [view.getHistoryTabConfig()],
                    reference: 'history',
                    bodyPadding: 0,
                    layout: 'fit',
                    autoScroll: true,
                    padding: 0,
                    tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.history,
                    operationMode: operationMode,
                    tabConfig: {
                        tabIndex: 4,
                        title: readonly ? CMDBuildUI.locales.Locales.common.tabs.history : undefined,
                        tooltip: !readonly ? (CMDBuildUI.locales.Locales.common.tabs.history).toLowerCase() : undefined
                    },
                    disabled: disabletabs
                });
            }

            // Email tab
            if (!isSimpleClass && classDef[CMDBuildUI.model.users.Grant.permissions.email_read]) {
                tabemails = view.add({
                    xtype: 'panel',
                    iconCls: readonly ? undefined : CMDBuildUI.util.helper.IconHelper.getIconId('envelope', 'solid'),
                    items: [view.getEmailsTabConfig(classDef)],
                    reference: view._emailReference,
                    bodyPadding: 0,
                    layout: 'fit',
                    autoScroll: true,
                    padding: 0,
                    tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.emails,
                    operationMode: operationMode,
                    tabConfig: {
                        tabIndex: 5,
                        title: readonly ? CMDBuildUI.locales.Locales.common.tabs.emails : undefined,
                        tooltip: !readonly ? (CMDBuildUI.locales.Locales.common.tabs.emails).toLowerCase() : undefined,
                        bind: {
                            text: (readonly ? CMDBuildUI.locales.Locales.common.tabs.emails : '' ) + '{tabcounters.emails ? \'<span class="badge">\' + tabcounters.emails + "</span>" : null}'
                        }
                    },
                    disabled: disabletabs
                });
            }

            // Attachments tab
            if (!isSimpleClass && enabledservices.dms && classDef[CMDBuildUI.model.users.Grant.permissions.attachment_read]) {
                tabattachments = view.add({
                    xtype: 'panel',
                    iconCls: readonly ? undefined : CMDBuildUI.util.helper.IconHelper.getIconId('paperclip', 'solid'),
                    items: [view.getDmsTabConfig(classDef)],
                    bodyPadding: 0,
                    layout: 'fit',
                    autoScroll: true,
                    padding: 0,
                    tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.attachments,
                    operationMode: operationMode,
                    tabConfig: {
                        tabIndex: 6,
                        title: readonly ? CMDBuildUI.locales.Locales.common.tabs.attachments : undefined,
                        tooltip: !readonly ? (CMDBuildUI.locales.Locales.common.tabs.attachments).toLowerCase() : undefined,
                        bind: {
                            text: (readonly ? CMDBuildUI.locales.Locales.common.tabs.attachments : '') + '{tabcounters.attachments ? \'<span class="badge">\' + tabcounters.attachments + "</span>" : null}'
                        }
                    },
                    disabled: disabletabs
                });
            }

            //Schedules
            if (!isSimpleClass && enabledservices.scheduler && classDef[CMDBuildUI.model.users.Grant.permissions.schedule_read]) {
                var haschedules;
                if (classDef.attributes) {
                    haschedules = Ext.Array.filter(classDef.attributes, function (item) {
                        return item.calendarTriggers && item.calendarTriggers.length;
                    });
                    haschedules = haschedules.length > 0;
                } else {
                    haschedules = classDef._hasTriggers; // if is create
                }
                if (haschedules) {
                    var eventsTabConfig = view.getEventsTabConfig(classDef);
                    if (eventsTabConfig.eventsStore) {
                        eventsTabConfig.eventsStore.autoLoad = !disabletabs;
                    }
                    tabschedules = view.add({
                        xtype: 'panel',
                        itemId: CMDBuildUI.mixins.DetailsTabPanel.actions.schedules, //used by {activeItem}
                        iconCls: readonly ? undefined : CMDBuildUI.util.helper.IconHelper.getIconId('calendar-alt', 'solid'),
                        items: [eventsTabConfig],
                        tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.schedules,
                        operationMode: operationMode,
                        tabConfig: {
                            tabIndex: 7,
                            title: readonly ? CMDBuildUI.locales.Locales.common.tabs.schedules : undefined,
                            tooltip: !readonly ? (CMDBuildUI.locales.Locales.common.tabs.schedules).toLowerCase() : undefined,
                            autoEl: {
                                'data-testid': 'schedules-tab'
                            }
                        },
                        disabled: disabletabs,
                        layout: 'fit',
                        autoScroll: true,
                        padding: 0
                    });
                }
            }

            // set view active tab
            var activetab;
            switch (action) {
                case CMDBuildUI.mixins.DetailsTabPanel.actions.masterdetail:
                    activetab = tabmasterdetail;
                    break;
                case CMDBuildUI.mixins.DetailsTabPanel.actions.notes:
                    activetab = tabnotes;
                    break;
                case CMDBuildUI.mixins.DetailsTabPanel.actions.relations:
                    activetab = tabrelations;
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
                case CMDBuildUI.mixins.DetailsTabPanel.actions.schedules:
                    activetab = tabschedules;
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
         * Get resource base path for routing.
         *
         * @param {String} action
         * @return {String}
         */
        getBasePath: function (action) {
            var vm = this.getViewModel();
            var typeName = vm.get("objectTypeName");
            var objectId = vm.get("objectId");
            var url = CMDBuildUI.view.classes.cards.Util.getBasePath(typeName, objectId, action);
            return url;
        }
    }
});