Ext.define('CMDBuildUI.view.relations.list.expanded.FieldsetController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.relations-list-expanded-fieldset',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.relations.list.expanded.Fieldset} view 
     * @param {Object} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        var me = this,
            vm = view.lookupViewModel(),
            direction = vm.get("direction"),
            domain = vm.get("domain"),
            dtype,
            dtypename,
            stypename;

        // get destination class/process
        if (direction === "_1") {
            vm.set("basetitle", domain.get("_descriptionInverse_translation"));
            dtype = domain.get("sourceProcess") ?
                CMDBuildUI.util.helper.ModelHelper.objecttypes.process :
                CMDBuildUI.util.helper.ModelHelper.objecttypes.klass;
            dtypename = domain.get("source");
            stypename = domain.get("destination");
        } else if (direction === "_2") {
            vm.set("basetitle", domain.get("_descriptionDirect_translation"));
            dtype = domain.get("destinationProcess") ?
                CMDBuildUI.util.helper.ModelHelper.objecttypes.process :
                CMDBuildUI.util.helper.ModelHelper.objecttypes.klass;
            dtypename = domain.get("destination");
            stypename = domain.get("source");
        }

        // get model
        var item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(dtypename, dtype);
        vm.set("storeinfo.sorters", me.getSorters(item, dtype, dtypename));

        CMDBuildUI.util.helper.ModelHelper.getModel(dtype, dtypename).then(function (model) {
            // set model name
            vm.set("storeinfo.model", model.getName());

            // set advanced filter
            vm.set("storeinfo.advancedfilter", {
                relation: [{
                    domain: domain.getId(),
                    type: "oneof",
                    destination: dtypename,
                    source: stypename,
                    direction: direction,
                    cards: [{
                        className: vm.get("objectTypeName"),
                        id: vm.get("objectId")
                    }]
                }]
            });

            // set auto load to true
            vm.set("storeinfo.autoload", true);

            // get columns
            CMDBuildUI.util.helper.GridHelper.getColumnsForType(
                dtype,
                dtypename,
                {
                    addTypeColumn: true
                }
            ).then(function (cols) {
                var domainname = domain.getId();
                domain.getAttributes().then(function (attrs) {
                    // get column defs for relation attributes
                    attrs.getRange().forEach(function (attr) {
                        var field = CMDBuildUI.util.helper.ModelHelper.getModelFieldFromAttribute(attr);
                        if (field && field.cmdbuildtype !== CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula) {
                            var col = CMDBuildUI.util.helper.GridHelper.getColumn(field);
                            if (col) {
                                var baserenderer = col.renderer;
                                col.hidden = false;
                                col.renderer = function (value, metaData, record, rowindex, colindex, store, view) {
                                    // find relation item
                                    var rels = view.lookupViewModel().get("allRelations");
                                    if (rels) {
                                        var relPos = rels.findBy(function (relItem) {
                                            return relItem.get("_type") === domainname && relItem.get("_destinationId") == record.get("_id");
                                        });
                                        if (relPos !== -1) {
                                            var relItem = rels.getAt(relPos);
                                            // return base render
                                            return baserenderer(relItem.get(attr.get("name")), metaData, relItem, rowindex, colindex, store, view);
                                        }
                                    }
                                }
                                cols.push(col);
                            }
                        }
                    });

                    // add grid
                    view.add({
                        xtype: 'grid',
                        columns: Ext.Array.merge(cols, me.getActionColumns()),
                        forceFit: true,
                        viewConfig: {
                            stripeRows: false
                        },
                        bind: {
                            store: '{records}',
                            hidden: '{!recordsCount}'
                        },
                        hidden: true,
                        height: 0,
                        listeners: {
                            show: function () {
                                view.calculateHeight();
                            }
                        }
                    });
                });
            });
        }, function () {
            vm.set("counters.stores", vm.get("counters.stores") + 1);
        });
    },

    /**
     * 
     * @param {Ext.data.Store} store 
     * @param {Ext.data.Model[]} records 
     */
    onStoreLoad: function (store, records) {
        var vm = this.getViewModel();
        vm.set("recordsCount", store.getTotalCount());
        if (store.loadCount === 0 || store.loadCount === 1) {
            vm.set("counters.stores", vm.get("counters.stores") + 1);
        }
    },

    privates: {
        /**
         * 
         * @param {CMDBuildUI.model.classes.Class|CMDBuildUI.model.processes.Process} item 
         * @param {String} type 
         * @param {String} typename 
         * 
         * @return {Ext.util.Sorter[]}
         */
        getSorters: function (item, type, typename) {
            var sorters = [];
            var preferences = CMDBuildUI.util.helper.UserPreferences.getGridPreferences(
                type,
                typename
            );
            if (preferences && !Ext.isEmpty(preferences.defaultOrder)) {
                preferences.defaultOrder.forEach(function (o) {
                    sorters.push({
                        property: o.attribute,
                        direction: o.direction === "descending" ? "DESC" : 'ASC'
                    });
                });
            } else if (item && item.defaultOrder().getCount()) {
                item.defaultOrder().getRange().forEach(function (o) {
                    sorters.push({
                        property: o.get("attribute"),
                        direction: o.get("direction") === "descending" ? "DESC" : 'ASC'
                    });
                });
            } else {
                sorters.push({
                    property: 'Description',
                    direction: 'ASC'
                });
            }
            return sorters;
        },

        /**
         * Return action columns
         * @return {Ext.grid.column.Action[]}
         */
        getActionColumns: function () {
            var me = this;
            return [{
                // non READ-ONLY action column
                xtype: 'actioncolumn',
                minWidth: 104, // width property not works. Use minWidth.
                maxWidth: 104,
                hidden: true,
                hideable: false,
                bind: {
                    hidden: '{readonly}'
                },
                items: [{
                    iconCls: 'relations-grid-action x-fa fa-external-link',
                    tooltip: CMDBuildUI.locales.Locales.relations.opencard,
                    handler: function (grid, rowIndex, colIndex, item, eOpts, record) {
                        me.openRelatedItem(record, CMDBuildUI.mixins.DetailsTabPanel.actions.view);
                    },
                    localized: {
                        toolitp: 'CMDBuildUI.locales.Locales.relations.opencard'
                    }
                }, {
                    iconCls: 'relations-grid-action x-fa fa-pencil',
                    tooltip: CMDBuildUI.locales.Locales.relations.editrelation,
                    handler: function (grid, rowIndex, colIndex, item, eOpts, record) {
                        var relationitem = me.findRelationItem(record.getId());
                        if (relationitem && relationitem.get("_can_update")) {
                            var vm = grid.lookupViewModel();
                            CMDBuildUI.view.relations.Utils.editRelation(relationitem, {
                                proxyurl: vm.get("storedata.proxyurl"),
                                objecttypename: vm.get("objectTypeName"),
                                objectid: vm.get("objectId")
                            }).then(function () {
                                grid.getStore().load();
                            });
                        } else {
                            // disable if user can not edit relation
                            item.disable();
                        }
                    },
                    isDisabled: function (view, rowIndex, colIndex, item, record) {
                        return record.isProcessInstance;
                    },
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.relations.editrelation'
                    }
                }, {
                    iconCls: 'relations-grid-action x-fa fa-trash',
                    tooltip: CMDBuildUI.locales.Locales.relations.deleterelation,
                    handler: function (grid, rowIndex, colIndex, item, eOpts, record) {
                        var relationitem = me.findRelationItem(record.getId());
                        if (relationitem && relationitem.get("_can_delete")) {
                            // delete relation
                            CMDBuildUI.view.relations.Utils.deleteRelation(relationitem).then(function () {
                                grid.getStore().load();
                            });
                        } else {
                            // disable if user can not delete relation
                            item.disable();
                        }
                    },
                    isDisabled: function (view, rowIndex, colIndex, item, record) {
                        return record.isProcessInstance;
                    },
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.relations.deleterelation'
                    }
                }, {
                    iconCls: 'relations-grid-action x-fa fa-pencil-square-o',
                    tooltip: CMDBuildUI.locales.Locales.relations.editcard,
                    handler: function (grid, rowIndex, colIndex, item, eOpts, record) {
                        me.openRelatedItem(record, CMDBuildUI.mixins.DetailsTabPanel.actions.edit);
                    },
                    isDisabled: function (view, rowIndex, colIndex, item, record) {
                        var titem = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(record.get("_type"));
                        return record.isProcessInstance ||
                            !(titem.get(CMDBuildUI.model.base.Base.permissions.edit)) &&
                            view.ownerGrid.lookupViewModel().get("basepermissions.edit");
                    },
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.relations.editcard'
                    }
                }]
            }, {
                // READ-ONLY action column
                xtype: 'actioncolumn',
                minWidth: 30, // width property not works. Use minWidth.
                maxWidth: 30,
                hidden: true,
                hideable: false,
                bind: {
                    hidden: '{!(readonly && !readOnlyAllowCardEdit)}'
                },
                items: [{
                    iconCls: 'relations-grid-action x-fa fa-external-link',
                    tooltip: CMDBuildUI.locales.Locales.relations.opencard,
                    handler: function (grid, rowIndex, colIndex, item, eOpts, record) {
                        me.openRelatedItem(record);
                    },
                    localized: {
                        toolitp: 'CMDBuildUI.locales.Locales.relations.opencard'
                    }
                }]
            }, {
                // READ-ONLY action column with edit button
                xtype: 'actioncolumn',
                minWidth: 60, // width property not works. Use minWidth.
                maxWidth: 60,
                hidden: true,
                hideable: false,
                bind: {
                    hidden: '{!(readonly && readOnlyAllowCardEdit)}'
                },
                items: [{
                    iconCls: 'relations-grid-action x-fa fa-external-link',
                    tooltip: CMDBuildUI.locales.Locales.relations.opencard,
                    handler: function (grid, rowIndex, colIndex, item, eOpts, record) {
                        me.openRelatedItem(record);
                    },
                    localized: {
                        toolitp: 'CMDBuildUI.locales.Locales.relations.opencard'
                    }
                }, {
                    iconCls: 'relations-grid-action x-fa fa-pencil',
                    tooltip: CMDBuildUI.locales.Locales.relations.editrelation,
                    handler: function (grid, rowIndex, colIndex, item, eOpts, record) {
                        var relationitem = me.findRelationItem(record.getId());
                        if (relationitem && relationitem.get("_can_update")) {
                            var vm = grid.lookupViewModel();
                            CMDBuildUI.view.relations.Utils.editRelation(relationitem, {
                                proxyurl: vm.get("storedata.proxyurl"),
                                objecttypename: vm.get("objectTypeName"),
                                objectid: vm.get("objectId")
                            }).then(function () {
                                grid.getStore().load();
                            });
                        } else {
                            // disable if user can not edit relation
                            item.disable();
                        }
                    },
                    isDisabled: function (view, rowIndex, colIndex, item, record) {
                        return record.isProcessInstance;
                    },
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.relations.editrelation'
                    }
                }]
            }];
        },

        /**
         * 
         * @param {CMDBuildUI.model.classes.Card|CMDBuildUI.model.processes.Instance} record 
         * @param {String} action 
         */
        openRelatedItem: function (record, action) {
            var path;
            if (record.isProcessInstance) {
                path = CMDBuildUI.util.Navigation.getProcessBaseUrl(record.get("_type"), record.get("_id"));
            } else if (record.isCard) {
                path = CMDBuildUI.util.Navigation.getClassBaseUrl(record.get("_type"), record.get("_id"));
            }
            if (action) {
                path += '/' + action;
            }
            if (path) {
                this.redirectTo(path);
            }
        },

        /**
         * 
         * @param {Numeric} destination 
         */
        findRelationItem: function (destination) {
            var vm = this.getViewModel(),
                domainname = vm.get("domain._id"),
                rels = vm.get("allRelations"),
                relPos = rels.findBy(function (relItem) {
                    return relItem.get("_type") === domainname && relItem.get("_destinationId") == destination;
                });
            if (relPos !== -1) {
                return rels.getAt(relPos);
            }
        }
    }
});