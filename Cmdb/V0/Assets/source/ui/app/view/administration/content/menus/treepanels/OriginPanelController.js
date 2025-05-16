Ext.define('CMDBuildUI.view.administration.content.menus.treepanels.OriginPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-menus-treepanels-originpanel',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            generateoriginpanel: 'onGenerateOriginPanel'
        }
    },

    onBeforeRender: function (view) {
        view.fireEventArgs('generateoriginpanel', [view]);
    },

    /**
     * 
     * @param {*} node 
     * @param {*} data 
     * @param {*} overModel 
     * @param {*} dropPosition 
     * @param {*} dropHandlers 
     */
    onBeforeDrop: function (node, data, overModel, dropPosition, dropHandlers) {
        var view = this.getView();
        dropHandlers.wait = true;

        data.records.forEach(function (record) {
            var destination = view.up('administration-content-menus-mainpanel').down('#treepaneldestination');
            var store = destination.getStore();

            if (!record.get('root')) {
                record.remove();
                store.sync();
            }
            var origin = view.up('administration-content-menus-mainpanel').down('#treepanelorigin');
            var expandedNodes = origin.getStore().queryRecords('expanded', true);
            origin.getController().generateMenu(origin).then(function () {
                Ext.Array.forEach(expandedNodes, function (node) {
                    origin.expandNode(origin.getStore().findNode('text', node.get('text')), true);
                });
                if (record.get('menutype') !== CMDBuildUI.model.menu.MenuItem.types.folder) {
                    origin.expandNode(origin.getStore().findNode('menutype', record.get('menutype')), true);
                    var originNode = origin.getStore().findNode('objecttype', record.get('objecttype'));

                    origin.ensureVisible(originNode.getPath());
                }
            });
        });
        dropHandlers.processDrop();
    },

    onGenerateOriginPanel: function (view) {
        var me = this,
            storesArray = [
                'classes.Classes',
                'reports.Reports',
                'views.Views',
                'dashboards.Dashboards',
                'custompages.CustomPages'
            ];
        var wfEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.processes.enabled);
        if (wfEnabled) {
            storesArray.push('processes.Processes');
        }

        Ext.Array.forEach(storesArray, function (storeId) {
            //For checking store is created or not
            //if store is not created we can create dyanamically using passing storeId/alias
            var store = Ext.getStore(storeId);
            if (Ext.isDefined(store) === false) {
                store = Ext.create(storeId);
            }
            store.load({
                callback: function () {
                    //On every store call back we can remove data from storesArray or maintain a veribale for checking.
                    Ext.Array.remove(storesArray, this.storeId);
                    if (storesArray.length === 0) {
                        me.generateMenu(view);
                    }
                }
            });
        });
    },
    /** 
     * @param {*} view 
     */
    generateMenu: function (view) {
        var deferred = new Ext.Deferred();
        var me = this;
        view.lookupViewModel().bind({
            bindTo: '{theMenu.device}'
        }, function () {
            var classesItems = Ext.getStore('classes.Classes').getData().getRange();
            var processesItems = Ext.getStore('processes.Processes').getData().getRange();
            var reportsItems = Ext.getStore('reports.Reports').getData().getRange();
            var viewsItems = Ext.getStore('views.Views').getData().getRange();
            var dashboardsItems = Ext.getStore('dashboards.Dashboards').getData().getRange();
            var custompagesItems = Ext.getStore('custompages.CustomPages').getData().getRange();
            var navtreeItems = Ext.getStore('menu.NavigationTrees').getData().getRange();
            var store = view.getStore();
            if (!store) {
                return;
            }
            store.clearFilter();
            var root = store.getRootNode();
            root.removeAll();
            var i = 0;

            root.appendChild({
                menutype: CMDBuildUI.model.menu.MenuItem.types.folder,
                index: i++,
                objecttype: CMDBuildUI.model.menu.MenuItem.types.klass,
                objectdescription: CMDBuildUI.locales.Locales.administration.navigation.classes, // Classes
                objectDescription: CMDBuildUI.locales.Locales.administration.navigation.classes, // Classes            
                _targetDescription: CMDBuildUI.locales.Locales.administration.navigation.classes,
                leaf: false,
                allowDrag: false,
                expanded: false,
                children: me.generateChildren(classesItems, CMDBuildUI.model.menu.MenuItem.types.klass)
            });
            var wfEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.processes.enabled);
            if (wfEnabled) {
                root.appendChild({
                    menutype: CMDBuildUI.model.menu.MenuItem.types.folder,
                    index: i++,
                    objecttype: CMDBuildUI.model.menu.MenuItem.types.process,
                    objectdescription: CMDBuildUI.locales.Locales.administration.navigation.processes, // Processes
                    objectDescription: CMDBuildUI.locales.Locales.administration.navigation.processes, // Processes
                    _targetDescription: CMDBuildUI.locales.Locales.administration.navigation.processes,
                    leaf: false,
                    allowDrag: false,
                    expanded: false,
                    children: me.generateChildren(processesItems, CMDBuildUI.model.menu.MenuItem.types.process)
                });
            }

            root.appendChild({
                menutype: CMDBuildUI.model.menu.MenuItem.types.folder,
                index: i++,
                objecttype: CMDBuildUI.model.menu.MenuItem.types.report,
                objectdescription: CMDBuildUI.locales.Locales.administration.navigation.reports, // Reports
                objectDescription: CMDBuildUI.locales.Locales.administration.navigation.reports, // Reports
                _targetDescription: CMDBuildUI.locales.Locales.administration.navigation.reports, // Reports
                leaf: false,
                allowDrag: false,
                expanded: false,
                children: me.generateChildren(reportsItems, CMDBuildUI.model.menu.MenuItem.types.report)
            });


            root.appendChild({
                menutype: CMDBuildUI.model.menu.MenuItem.types.folder,
                index: i++,
                objecttype: CMDBuildUI.model.menu.MenuItem.types.view,
                objectdescription: CMDBuildUI.locales.Locales.administration.navigation.views, // Views
                objectDescription: CMDBuildUI.locales.Locales.administration.navigation.views, // Dashboards
                _targetDescription: CMDBuildUI.locales.Locales.administration.navigation.views, // Dashboards
                leaf: false,
                allowDrag: false,
                expanded: false,
                children: me.generateChildren(viewsItems.sort(function (a, b) {
                    if ((a && a.get('description')) && (b && b.get('description'))) {
                        var nameA = a.get('description').toUpperCase(); // ignore upper and lowercase
                        var nameB = b.get('description').toUpperCase(); // ignore upper and lowercase
                        if (nameA < nameB) {
                            return -1;
                        }
                        if (nameA > nameB) {
                            return 1;
                        }
                        // if descriptions are same
                        return 0;
                    }
                }), CMDBuildUI.model.menu.MenuItem.types.view)
            });
            if (view.lookupViewModel().get('theMenu.device') === CMDBuildUI.model.menu.Menu.device['default']) {
                root.appendChild({
                    menutype: CMDBuildUI.model.menu.MenuItem.types.folder,
                    index: i++,
                    objecttype: CMDBuildUI.model.menu.MenuItem.types.dashboard,
                    objectdescription: CMDBuildUI.locales.Locales.administration.navigation.dashboards, // Dashboards
                    objectDescription: CMDBuildUI.locales.Locales.administration.navigation.dashboards, // Dashboards
                    _targetDescription: CMDBuildUI.locales.Locales.administration.navigation.dashboards, // Dashboards
                    leaf: false,
                    allowDrag: false,
                    expanded: false,
                    children: me.generateChildren(dashboardsItems, CMDBuildUI.model.menu.MenuItem.types.dashboard)
                });
            }

            root.appendChild({
                menutype: CMDBuildUI.model.menu.MenuItem.types.folder,
                index: i++,
                objecttype: CMDBuildUI.model.menu.MenuItem.types.custompage,
                objectdescription: CMDBuildUI.locales.Locales.administration.navigation.custompages, // Custom pages
                objectDescription: CMDBuildUI.locales.Locales.administration.navigation.custompages, // Custom pages
                _targetDescription: CMDBuildUI.locales.Locales.administration.navigation.custompages, // Custom pages
                leaf: false,
                allowDrag: false,
                expanded: false,
                children: me.generateChildren(Ext.Array.filter(custompagesItems, function (custompage) {
                    if (view.lookupViewModel().get('theMenu.device') === CMDBuildUI.model.menu.Menu.device['default']) {
                        return custompage.get('devices').indexOf(CMDBuildUI.model.custompages.CustomPage.device['default']) > -1;
                    } else {
                        return custompage.get('devices').indexOf(CMDBuildUI.model.custompages.CustomPage.device.mobile) > -1;
                    }
                }), CMDBuildUI.model.menu.MenuItem.types.custompage)
            });

            root.appendChild({
                menutype: CMDBuildUI.model.menu.MenuItem.types.folder,
                index: i++,
                objecttype: CMDBuildUI.model.menu.MenuItem.types.navtree,
                objectdescription: CMDBuildUI.locales.Locales.administration.navigation.navigationtrees,
                objectDescription: CMDBuildUI.locales.Locales.administration.navigation.navigationtrees,
                _targetDescription: CMDBuildUI.locales.Locales.administration.navigation.navigationtrees,
                leaf: false,
                allowDrag: false,
                expanded: false,
                children: me.generateChildren(navtreeItems, CMDBuildUI.model.menu.MenuItem.types.navtree)
            });
            deferred.resolve(true);
        });
        return deferred.promise;
    },
    privates: {

        /**
         * 
         * @private
         */
        generateChildren: function (items, type) {
            var me = this;
            var _items = [];
            var destination = this.getView().up('administration-content-menus-mainpanel').down('#treepaneldestination');
            items.forEach(function (element, index) {
                var elementIsPresent = function (element, type) {
                    return destination.getStore().getData().findBy(function (item) {
                        return (item.get('objecttype') === element.get('_id') || item.get('objecttype') === element.get('name') || item.get('objecttype') === element.get('code')) && item.get('menutype') === type;
                    });
                };

                switch (type) {
                    case CMDBuildUI.model.menu.MenuItem.types.klass:
                        if (!elementIsPresent(element, type)) {
                            _items.push(me.generateClassChildren(type, element, index, CMDBuildUI.locales.Locales.administration.localizations.class));
                        }
                        break;
                    case CMDBuildUI.model.menu.MenuItem.types.process:
                        if (!elementIsPresent(element, type)) {
                            _items.push(me.generateClassChildren(type, element, index, CMDBuildUI.locales.Locales.administration.localizations.process));
                        }
                        break;
                    case CMDBuildUI.model.menu.MenuItem.types.custompage:
                        if (!elementIsPresent(element, type)) {
                            _items.push(me.generateCustompageChildren(type, element, index, CMDBuildUI.locales.Locales.administration.localizations.custompage));
                        }
                        break;
                    case CMDBuildUI.model.menu.MenuItem.types.navtree:
                        if (!elementIsPresent(element, type)) {
                            _items.push(me.generateNavtreeChildren(type, element, index, CMDBuildUI.locales.Locales.administration.navigation.navigationtrees));
                        }
                        break;
                    case CMDBuildUI.model.menu.MenuItem.types.report:
                    case CMDBuildUI.model.menu.MenuItem.types.reportcsv:
                    case CMDBuildUI.model.menu.MenuItem.types.reportodt:
                    case CMDBuildUI.model.menu.MenuItem.types.reportpdf:
                    case CMDBuildUI.model.menu.MenuItem.types.reportrtf:
                        if (!elementIsPresent(element, CMDBuildUI.model.menu.MenuItem.types.reportpdf)) {
                            _items.push(me.generateReportChildren(CMDBuildUI.model.menu.MenuItem.types.reportpdf, element, index, Ext.String.format('{0} {1}', CMDBuildUI.locales.Locales.administration.localizations.report, CMDBuildUI.locales.Locales.administration.localizations.pdf)));
                        }
                        if (!elementIsPresent(element, CMDBuildUI.model.menu.MenuItem.types.reportcsv)) {
                            _items.push(me.generateReportChildren(CMDBuildUI.model.menu.MenuItem.types.reportcsv, element, index, Ext.String.format('{0} {1}', CMDBuildUI.locales.Locales.administration.localizations.report, CMDBuildUI.locales.Locales.administration.localizations.csv)));
                        }
                        break;

                    case CMDBuildUI.model.menu.MenuItem.types.dashboard:
                        if (!elementIsPresent(element, type)) {
                            _items.push(me.generateClassChildren(type, element, index, CMDBuildUI.locales.Locales.administration.localizations.dashboard));
                        }
                        break;

                    case CMDBuildUI.model.menu.MenuItem.types.view:
                        if (!elementIsPresent(element, type)) {
                            _items.push(me.generateClassChildren(type, element, index, CMDBuildUI.locales.Locales.administration.localizations.view));
                        }
                        break;
                    default:
                        if (!elementIsPresent(element, type)) {
                            _items.push(me.generateClassChildren(type, element, index));
                        }
                        break;
                }
            });
            return _items;
        },
        generateClassChildren: function (type, element, index, qtip) {
            // ok
            var uuid = CMDBuildUI.util.Utilities.generateUUID();
            var leaf = {
                _id: uuid,
                id: uuid,
                menutype: type,
                index: index,
                objecttype: element.get('name'),
                text: element.get('description'),
                _actualDescription: element.get('description'),
                _targetDescription: element.get('description'),
                leaf: true,
                qtip: qtip
            };
            leaf = Ext.create('CMDBuildUI.model.menu.MenuItem', leaf);
            if (element.get('prototype')) {
                switch (type) {
                    case CMDBuildUI.model.menu.MenuItem.types.klass:
                        leaf.data.iconCls = CMDBuildUI.model.menu.MenuItem.icons.klassparent;
                        break;
                    case CMDBuildUI.model.menu.MenuItem.types.process:
                        leaf.data.iconCls = CMDBuildUI.model.menu.MenuItem.icons.processparent;
                        break;
                    default:
                        break;
                }
            }
            return leaf;
        },
        generateCustompageChildren: function (type, element, index, qtip) {
            // ok
            var uuid = CMDBuildUI.util.Utilities.generateUUID();
            var leaf = {
                _id: uuid,
                id: uuid,
                menutype: type,
                index: index,
                objecttype: element.get('name'),
                objectid: element.get('_id'),
                text: element.get('description'),
                _actualDescription: element.get('description'),
                _targetDescription: element.get('description'),
                leaf: true,
                qtip: qtip
            };
            leaf = Ext.create('CMDBuildUI.model.menu.MenuItem', leaf);
            return leaf;
        },
        generateReportChildren: function (type, element, index, qtip) {

            var uuid = CMDBuildUI.util.Utilities.generateUUID();
            var leaf = {
                _id: uuid,
                id: uuid,
                menutype: type,
                index: index,
                objecttype: element.get('code'),
                objectid: element.get('code'),
                text: element.get('description'),
                _actualDescription: element.get('description'),
                _targetDescription: element.get('description'),
                leaf: true,
                qtip: qtip
            };

            leaf = Ext.create('CMDBuildUI.model.menu.MenuItem', leaf);
            return leaf;
        },

        generateNavtreeChildren: function (type, element, index, qtip) {

            var uuid = CMDBuildUI.util.Utilities.generateUUID();
            var leaf = {
                _id: uuid,
                id: uuid,
                menutype: type,
                index: index,
                objecttype: element.get('_id'),
                objectid: element.get('_id'),
                text: element.get('description'),
                _actualDescription: element.get('description'),
                _targetDescription: element.get('description'),
                _forAdmin: true,
                leaf: true,
                qtip: qtip
            };

            leaf = Ext.create('CMDBuildUI.model.menu.MenuItem', leaf);
            return leaf;
        }
    }
});