Ext.define('CMDBuildUI.view.joinviews.items.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.joinviews-items-grid',

    data: {
        title: null,
        objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.view,
        objectTypeName: null,
        menuType: CMDBuildUI.model.menu.MenuItem.types.view,
        isUserView: false,
        storeinfo: {
            autoLoad: false
        },
        search: {
            disabled: false
        }
    },

    formulas: {
        updateData: {
            bind: {
                objectTypeName: '{objectTypeName}'
            },
            get: function (data) {
                if (data.objectTypeName) {
                    var me = this;
                    // view data
                    var viewdata = CMDBuildUI.util.helper.ModelHelper.getViewFromName(data.objectTypeName);
                    this.set("title", viewdata.get("description"));
                    this.set("search.disabled", !viewdata.get("_can_search"));

                    // enable or disable print button
                    var canprint = !Ext.isEmpty(viewdata.get("_can_print")) ? viewdata.get("_can_print") : true;
                    this.set("disabledbuttons.print", !canprint);

                    // check if is shared view or user view
                    this.set("isUserView", !viewdata.get("shared"));

                    // get model
                    CMDBuildUI.util.helper.ModelHelper.getModel(
                        CMDBuildUI.util.helper.ModelHelper.objecttypes.view,
                        data.objectTypeName
                    ).then(function (model) {
                        me.set("storeinfo.modelname", model.getName());
                        me.set("storeinfo.url", model.getProxy().getUrl());

                        // sort
                        var sorters = [];
                        var preferences = CMDBuildUI.util.helper.UserPreferences.getGridPreferences(
                            CMDBuildUI.util.helper.ModelHelper.objecttypes.view,
                            data.objectTypeName
                        );

                        if (preferences && !Ext.isEmpty(preferences.defaultOrder)) {
                            preferences.defaultOrder.forEach(function (o) {
                                sorters.push({
                                    property: o.attribute,
                                    direction: o.direction === "descending" ? "DESC" : 'ASC'
                                });
                            });
                        } else if (viewdata.get("sorter")) {
                            sorters = viewdata.get("sorter");
                        }
                        me.set("storeinfo.sorters", sorters);

                        // default filter
                        var autoload = true;
                        if (viewdata.getCurrentFilter && viewdata.getCurrentFilter()) {
                            me.set("defaultfilter", viewdata.getCurrentFilter());
                            autoload = false;
                        }

                        // auto load
                        me.set("storeinfo.autoload", autoload);
                    });

                }
            }
        }
    },

    stores: {
        items: {
            type: 'buffered',
            model: '{storeinfo.modelname}',
            autoLoad: '{storeinfo.autoload}',
            proxy: {
                type: 'baseproxy',
                url: '{storeinfo.url}'
            },
            pageSize: 50,
            leadingBufferZone: 100,
            sorters: '{storeinfo.sorters}',
            autoLoad: '{storeinfo.autoload}',
            autoDestroy: true
        },
        involvedCards: {
            proxy: {
                type: 'memory'
            },
            data: [],
            sorters: {
                property: 'typeName',
                direction: 'ASC'
            }
        }
    }

});