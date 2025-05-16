/**
 * @file CMDBuildUI.util.Stores
 * @module CMDBuildUI.util.Stores
 * @author Tecnoteca srl 
 * @access private
 */
Ext.define("CMDBuildUI.util.Stores", {
    singleton: true,
    // collection of non global stores

    stores: {
        busdescriptors: null
    },

    loaded: {
        classes: false,
        processes: false,
        reports: false,
        dashboards: false,
        views: false,
        searchfilters: false,
        custompages: false,
        customcomponents: false,
        customwidgets: false,
        scripts: false,
        widgets: false,
        menu: false,
        menunavtree: false,
        administrationmenus: false,
        lookuptypes: false,
        dmsmodels: false,
        categorytypes: true,
        domains: false,
        navtree: false,
        groups: false,
        emailaccounts: false,
        emailtemplates: false,
        importexporttemplates: false,
        etlgates: false,
        busdescriptors: false
    },

    /**
     * Load classes store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadClassesStore: function () {
        var deferred = new Ext.Deferred();
        var store = Ext.getStore('classes.Classes');
        // load classes store
        store.load({
            params: {
                detailed: true // load full data
            },
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.classes = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading Classes store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
     * Load processes store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadProcessesStore: function () {
        var workflowEnabled = CMDBuildUI.util.helper.Configurations.get('cm_system_workflow_enabled');
        var deferred = new Ext.Deferred();

        var store = Ext.getStore('processes.Processes');
        if (workflowEnabled) {
            // load processes store
            store.load({
                params: {
                    detailed: true // load full data
                },
                callback: function (records, operation, success) {
                    if (success) {
                        CMDBuildUI.util.Stores.loaded.processes = true;
                        deferred.resolve(records);
                    } else {
                        CMDBuildUI.util.Logger.log(
                            "Error loading Processes store",
                            CMDBuildUI.util.Logger.levels.error
                        );
                        deferred.resolve([]);
                    }
                }
            });
        } else {
            deferred.resolve([]);
        }
        return deferred.promise;
    },

    /**
     * Load reprts store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadReportsStore: function () {
        var deferred = new Ext.Deferred();
        var store = Ext.getStore('reports.Reports');
        // load reports store
        store.load({
            params: {
                detailed: true // load full data
            },
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.reports = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading Reports store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
     * Load dashboards store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadDashboardsStore: function () {
        var deferred = new Ext.Deferred();
        var store = Ext.getStore('dashboards.Dashboards');
        // load reports store
        store.load({
            params: {
                detailed: true // load full data
            },
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.dashboards = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading Dashboards store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
     * Load views store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadViewsStore: function () {
        var deferred = new Ext.Deferred();
        var store = Ext.getStore('views.Views');
        // load views store
        store.load({
            params: {
                detailed: true // load full data
            },
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.views = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading Views store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
     * Load searchfilters store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadSearchfiltersStore: function () {
        var deferred = new Ext.Deferred();
        var store = Ext.getStore('searchfilters.Searchfilters');
        // load serachfilters store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.searchfilters = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading Searchfilters store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
     * Load custompages store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadCustomPagesStore: function () {
        var deferred = new Ext.Deferred();
        var store = Ext.getStore('custompages.CustomPages');
        // load reports store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.custompages = true;
                    var count = records.length;
                    if (CMDBuildUI.util.Ajax.getViewContext() === "default" && count) {
                        records.forEach(function (r) {
                            var path = Ext.Loader.getPath(Ext.String.format("CMDBuildUI.{0}",
                                r.get("componentId")
                            ));
                            Ext.Loader.loadScript({
                                url: path,
                                onLoad: function () {
                                    --count;
                                    if (!count) {
                                        deferred.resolve(records);
                                    }
                                },
                                onError: function () {
                                    --count;
                                    if (!count) {
                                        deferred.resolve(records);
                                    }
                                },
                                scope: this
                            });
                        });
                    } else {
                        deferred.resolve(records);
                    }
                    // deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading CustomPages store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
     * Load customcomponents store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadCustomContextMenuStore: function () {
        var deferred = new Ext.Deferred();
        var store = Ext.getStore('customcomponents.ContextMenus');
        // load CustomComponents store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.customcomponents = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading CustomComponents store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
     * Load customcomponents store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadCustomWidgetsStore: function () {
        var deferred = new Ext.Deferred();
        var store = Ext.StoreManager.get("customcomponents.Widgets");
        // load customcomponents.Widgets store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    if (CMDBuildUI.util.Ajax.getViewContext() === "default" && !Ext.isEmpty(records)) {
                        var files = [];
                        records.forEach(function (r) {
                            files.push(Ext.String.format("CMDBuildUI.{0}",
                                r.get("componentId")
                            ));
                        });
                        Ext.syncRequire(files);
                    }
                    CMDBuildUI.util.Stores.loaded.customwidgets = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading custom widgets store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
     * Load Widgets store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadWidgetsStore: function () {
        var deferred = new Ext.Deferred();
        var store = Ext.getStore('customcomponents.Widgets');
        // load Widgets store        
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.widgets = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading Widgets store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
     * Load Scripts store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadScriptsStore: function () {
        var deferred = new Ext.Deferred();
        var store = Ext.getStore('customcomponents.Scripts');
        // load Widgets store        
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.scripts = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading Scripts store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },
    /**
     * Load menu store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadMenuStore: function () {
        var deferred = new Ext.Deferred();

        // load classes store
        var store = Ext.getStore('menu.Menu');
        store.setRoot({
            expanded: false
        });
        store.load(function (records, operation, success) {
            if (success) {
                CMDBuildUI.util.Stores.loaded.menu = true;
                deferred.resolve(records);
            } else {
                CMDBuildUI.util.Logger.log(
                    "Error loading Menu items store",
                    CMDBuildUI.util.Logger.levels.error
                );
                deferred.resolve([]);
            }
        });

        return deferred.promise;
    },

    /**
     * Load menu navigation trees
     */
    loadMenuNavigationTreesStore: function () {
        var deferred = new Ext.Deferred();
        var store = Ext.getStore('menu.NavigationTrees');
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.menunavtree = true;
                    /// Temporary until server makes services
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading menu NavTree store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },


    /**
     * Load menu store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadAdministrationMenusStore: function () {
        var deferred = new Ext.Deferred();

        // load classes store
        var store = Ext.create('Ext.data.Store', {
            model: 'CMDBuildUI.model.menu.Menu',

            pageSize: 0, // disable pagination

            sorters: [
                'group'
            ]
        });

        store.load(function (records, operation, success) {
            if (success) {
                CMDBuildUI.util.Stores.loaded.administrationmenus = true;
                deferred.resolve(records);
            } else {
                CMDBuildUI.util.Logger.log(
                    "Error loading administration Menu items store",
                    CMDBuildUI.util.Logger.levels.error
                );
                deferred.resolve([]);
            }
        });

        return deferred.promise;
    },

    /**
     * Load lookup types store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadLookupTypesStore: function () {
        var deferred = new Ext.Deferred();

        // load classes store
        var store = Ext.getStore('lookups.LookupTypes');
        // load reports store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.lookuptypes = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading LookupTypes store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
     * Load dms lookup types store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadDMSLookupTypesStore: function () {
        var deferred = new Ext.Deferred();

        // load classes store
        var store = Ext.getStore('dms.DMSCategoryTypes');
        // load reports store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.lookuptypes = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading LookupTypes store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
     * Load dms models store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadDmsModelsStore: function () {
        var deferred = new Ext.Deferred();
        // load classes store
        var store = Ext.getStore('dms.DMSModels');
        // load reports store
        store.load({
            params: {
                detailed: true // load full data
            },
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.dmsmodels = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading DmsModels store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
     * Load lookup types store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadDmsCategoryTypesStore: function () {
        var deferred = new Ext.Deferred();

        // load classes store
        var store = Ext.getStore('dms.DMSCategoryTypes');
        // load reports store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.categorytypes = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading CategoryTypes store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
     * Load domains store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadDomainsStore: function () {
        var deferred = new Ext.Deferred();

        // load classes store
        var store = Ext.getStore('domains.Domains');
        // load reports store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.domains = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading Domains store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
     * Load navigation trees
     */
    loadNavigationTreesStore: function () {
        var deferred = new Ext.Deferred();
        var store = Ext.getStore('navigationtrees.NavigationTrees');
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.navtree = true;
                    /// Temporary until server makes services
                    var gisNavigationCallback = function (record, operation, success) {
                        if (success) {
                            store.add([record]);
                        }
                    };
                    for (var i = 0; i < records.length; i++) {
                        if (records[i].get('_id') === 'gisnavigation') {
                            CMDBuildUI.model.navigationTrees.DomainTree.load('gisnavigation', {
                                callback: gisNavigationCallback
                            });
                        }
                    }
                    /// Temporary until server makes services
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading NavTree store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
     * Load groups store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadGroupsStore: function () {
        var deferred = new Ext.Deferred();
        var store = Ext.getStore('groups.Groups');
        // load groups store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.groups = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading Groups store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
     * Load flow statuses store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadFlowStatuses: function () {
        // load classes store
        var type = CMDBuildUI.model.lookups.LookupType.getLookupTypeFromName(CMDBuildUI.model.processes.Process.flowstatus.lookuptype);
        return type.getLookupValues();
    },

    /**
     * Load email templates store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadEmailAccountsStore: function () {

        var deferred = new Ext.Deferred();
        var store = Ext.getStore('emails.Accounts');
        // load groups store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.emailaccounts = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading email Accounts store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
     * Load email templates store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadEmailTemplatesStore: function () {

        var deferred = new Ext.Deferred();
        var store = Ext.getStore('emails.Templates');
        // load groups store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.emailtemplates = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading email Templates store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },
    /**
     * Load import export templates store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadImportExportTemplatesStore: function () {

        var deferred = new Ext.Deferred();
        var store = Ext.getStore('importexports.Templates');
        // load importexports.Templates store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.importexporttemplates = true;
                    deferred.resolve(this.getRange());
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading import/export Templates store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },
    /**
     * Load etl gates templates store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadBusDescriptorStoreStore: function () {

        var deferred = new Ext.Deferred();
        if (!this.stores.busdescriptors) {
            this.stores.busdescriptors = Ext.create('Ext.data.Store', {
                model: 'CMDBuildUI.model.administration.BusDescriptor',
                pageSize: 0,
                proxy: {
                    type: 'baseproxy',
                    url: '/etl/configs',
                    extrParams: {
                        detailed: true
                    }
                },
                advancedFilter: {
                    attributes: {
                        tag: [{
                            operator: CMDBuildUI.model.base.Filter.operators.null,
                            value: []
                        }]
                    }
                }
            });
        }

        // load bus descriptors store
        this.stores.busdescriptors.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.busdescriptors = true;
                    deferred.resolve(this.getRange());
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading bus descriptors store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    loadETLGatesStore: function () {

        var deferred = new Ext.Deferred();
        var store = Ext.getStore('importexports.Gates');
        // load importexports.Templates store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.etlgates = true;
                    deferred.resolve(this.getRange());
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading ETL Gates Templates store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },
    /**
     * 
     * @param {String} store the id of the Store
     * @param {Boolean} [soft] if true and store contains data, 
     *                         the data will be resolved without store reload
     * 
     * @return {Ext.promise.Promise}
     */
    load: function (store, soft) {
        var deferred = new Ext.Deferred();
        var _store = Ext.getStore(store);
        if (_store && _store.getRange && _store.getRange().length && soft) {
            deferred.resolve(_store.getRange());
        } else {
            switch (store) {
                case 'classes.Classes':
                    CMDBuildUI.util.Stores.loadClassesStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'processes.Processes':
                    CMDBuildUI.util.Stores.loadProcessesStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'reports.Reports':
                    CMDBuildUI.util.Stores.loadReportsStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'dashboards.Dashboards':
                    CMDBuildUI.util.Stores.loadDashboardsStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'views.Views':
                    CMDBuildUI.util.Stores.loadViewsStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'searchfilters.Searchfilters':
                    CMDBuildUI.util.Stores.loadSearchfiltersStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'custompages.CustomPages':
                    CMDBuildUI.util.Stores.loadCustomPagesStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'customcomponents.ContextMenus':
                    CMDBuildUI.util.Stores.loadCustomContextMenuStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'customcomponents.Widgets':
                    CMDBuildUI.util.Stores.loadWidgetsStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'customcomponents.Scripts':
                    CMDBuildUI.util.Stores.loadScriptsStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'menu.Menu':
                    CMDBuildUI.util.Stores.loadMenuStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'menu.NavigationTrees':
                    CMDBuildUI.util.Stores.loadMenuNavigationTreesStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'lookups.LookupTypes':
                    CMDBuildUI.util.Stores.loadLookupTypesStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'dms.DMSCategoryTypes':
                    CMDBuildUI.util.Stores.loadCategoryTypesStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'dms.DMSModels':
                    CMDBuildUI.util.Stores.loadDmsModelsStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'domains.Domains':
                    CMDBuildUI.util.Stores.loadDomainsStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'navigationtrees.NavigationTrees':
                    CMDBuildUI.util.Stores.loadNavigationTreesStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'groups.Groups':
                    CMDBuildUI.util.Stores.loadGroupsStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'emails.Accounts':
                    CMDBuildUI.util.Stores.loadEmailAccountsStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'emails.Templates':
                    CMDBuildUI.util.Stores.loadEmailTemplatesStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'importexports.Templates':
                    CMDBuildUI.util.Stores.loadImportExportTemplatesStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                default:
                    CMDBuildUI.util.Logger.log("Store id not managed by Stores Util", CMDBuildUI.util.Logger.levels.debug);
                    deferred.resolve([]);
                    break;
            }
        }
        return deferred.promise;
    }



});