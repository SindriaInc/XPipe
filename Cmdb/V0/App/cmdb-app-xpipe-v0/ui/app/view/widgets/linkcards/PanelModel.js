Ext.define('CMDBuildUI.view.widgets.linkcards.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.widgets-linkcards-panel',

    data: {
        objectType: null,
        objectTypeName: null,
        disablegridfilter: false,
        selection: null,

        storeinfo: {
            modelname: undefined,
            storetype: undefined,
            proxyurl: undefined,
            extraparams: undefined,
            advancedfilter: undefined,
            sorters: undefined,
            autoload: undefined
        },
        search: {
            value: null
        }
    },

    formulas: {
        updateStoreData: {
            bind: {
                model: '{model}',
                objecttype: '{objectType}',
                objecttypename: '{objectTypeName}',
                filter: '{theWidget._Filter_ecql}',
                target: '{theTarget}',
                defaults: '{defaultsLoaded}'
            },
            get: function (data) {
                var model = data.model;
                if (model) {
                    // store type
                    switch (data.objecttype) {
                        case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                            this.set('storeinfo.storetype', 'classes-cards');
                            break;
                        case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                            this.set('storeinfo.storetype', 'processes-instances');
                            break;
                        default:
                            CMDBuildUI.util.Logger.log(
                                'Object type ' + data.objecttype + ' non supported for LinkCard widget.',
                                CMDBuildUI.util.Logger.levels.warn
                            );
                    }

                    // model name
                    this.set('storeinfo.modelname', model.getName());

                    // proxy url
                    this.set('store.proxyurl', model.getProxy().getUrl());

                    // extra params
                    this.set('store.extraparams', model.getProxy().getExtraParams());

                    // filter
                    if (data.filter) {
                        // calculate ecql
                        var ecql = CMDBuildUI.util.ecql.Resolver.resolve(
                            data.filter,
                            data.target
                        );

                        if (ecql) {
                            this.set("storeinfo.advancedfilter", {
                                ecql: ecql
                            });
                        }
                    }

                    // sorters
                    var sorters = [],
                        klass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.objecttypename, data.objecttype),
                        preferences = CMDBuildUI.util.helper.UserPreferences.getGridPreferences(
                            data.objecttype,
                            data.objecttypename
                        );

                    if (preferences && !Ext.isEmpty(preferences.defaultOrder)) {
                        preferences.defaultOrder.forEach(function (o) {
                            sorters.push({
                                property: o.attribute,
                                direction: o.direction === "descending" ? "DESC" : 'ASC'
                            });
                        });
                    } else if (klass && klass.defaultOrder().getCount()) {
                        klass.defaultOrder().getRange().forEach(function (o) {
                            sorters.push({
                                property: o.get("attribute"),
                                direction: o.get("direction") === "descending" ? "DESC" : 'ASC'
                            });
                        });
                    } else if (!klass.isSimpleClass || !klass.isSimpleClass()) {
                        sorters.push({
                            property: 'Description'
                        });
                    }
                    this.set('store.sorters', sorters);
                }
                if (data.defaults) {
                    this.set('store.autoload', true);
                }
            }
        },

        /**
         * Disable button view card/process instance.
         */
        disableViewAction: {
            bind: '{theSession}',
            get: function () {
                return false;
            }
        },

        /**
         * Disable action edit card/process instance.
         */
        disableEditAction: {
            bind: {
                canedit: '{theWidget.AllowCardEditing}',
                objecttype: '{objectType}',
                objecttypename: '{objectTypeName}'
            },
            get: function (data) {
                if (data.objecttypename && data.objecttype) {
                    var obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.objecttypename, data.objecttype);
                    return !obj.get(CMDBuildUI.model.base.Base.permissions.edit) || (data.canedit === undefined || data.canedit === null ||
                        data.canedit === "0" || data.canedit === 0 || data.canedit === "false" || data.canedit === false);
                }
                return true;
            }
        },

        textTogglefilter: {
            bind: {
                disablegridfilter: '{disablegridfilter}'
            },
            get: function (data) {
                if (data.disablegridfilter) {
                    return CMDBuildUI.locales.Locales.widgets.linkcards.togglefilterenabled;
                }
                return CMDBuildUI.locales.Locales.widgets.linkcards.togglefilterdisabled;
            }
        },

        /**
         * Disable toggle filter button.
         */
        disableTogglefilter: {
            bind: {
                disable: '{theWidget.DisableGridFilterToggler}',
                filter: '{theWidget._Filter_ecql}'
            },
            get: function (data) {
                return data.disable || !data.filter ? true : false;
            }
        },

        /**
         * Text refresh selection
         */
        textRefreshselection: {
            bind: {},
            get: function (data) {
                return CMDBuildUI.locales.Locales.widgets.linkcards.refreshselection;
            }
        },

        /**
         * Disable refresh selection button.
         */
        disableRefreshselection: {
            bind: {
                defaultselection: '{theWidget._DefaultSelection_ecql}',
                noselect: '{theWidget.NoSelect}'
            },
            get: function (data) {
                return !data.defaultselection || data.noselect ? true : false;
            }
        },

        /**
         * Get store autoLoad value.
         */
        storeAutoLoad: {
            bind: {
                proxy: '{storeProxy}',
                defaultsLoaded: '{defaultsLoaded}'
            },
            get: function (data) {
                return data.proxy && data.defaultsLoaded;
            }
        }

    },

    stores: {
        // grid data
        gridrows: {
            type: '{storeinfo.storetype}',
            model: '{storeinfo.modelname}',
            proxy: {
                type: 'baseproxy',
                url: '{store.proxyurl}',
                extraParams: '{store.extraparams}'
            },
            advancedFilter: '{storeinfo.advancedfilter}',
            sorters: '{store.sorters}',
            autoLoad: '{store.autoload}',
            autoDestroy: true,
            listeners: {
                prefetch: 'onGridrowsLoad'
            }
        }
    }

});