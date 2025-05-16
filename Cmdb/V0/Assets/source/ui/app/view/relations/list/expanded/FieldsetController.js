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

        var sorters = CMDBuildUI.util.helper.GridHelper.getStoreSorters(
            CMDBuildUI.util.helper.ModelHelper.getObjectFromName(dtypename, dtype),
            true
        );
        vm.set("storeinfo.sorters", sorters);

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
                domain.getAttributes().then(function (attrs) {
                    // get column defs for relation attributes
                    attrs.getRange().forEach(function (attr) {
                        var field = CMDBuildUI.util.helper.ModelHelper.getModelFieldFromAttribute(attr);
                        if (field && field.cmdbuildtype !== CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula) {
                            var col = CMDBuildUI.util.helper.GridHelper.getColumn(field);
                            if (col) {
                                col.hidden = false;
                                col.dataIndex = "_relAttr_" + attr.get("name");
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
                            stripeRows: false,
                            markDirty: false
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
        var vm = this.getViewModel(),
            allRelations = vm.get("allRelations");

        function setAttributesRelation(rec, relation) {
            Ext.Object.each(relation.get("_relationAttributes"), function (key, value, allobjects) {
                rec.set("_relAttr_" + key, relation.get(key));
            });
        }

        Ext.Array.forEach(records, function (record, indexRecord, allrecords) {
            var relationsSameRecord = allRelations.queryBy(function (item, id) {
                return item.get("_destinationId") == record.getId() && item.get("_type") == vm.get("domain").getId();
            });
            Ext.Array.forEach(relationsSameRecord.getRange(), function (relation, index, allitems) {
                if (!Ext.Object.isEmpty(relation.get("_relationAttributes"))) {
                    if (!index) {
                        setAttributesRelation(record, relation);
                    } else {
                        var recordClone = record.clone();
                        recordClone.setId(record.getId() + "_" + relation.getId());
                        setAttributesRelation(recordClone, relation);
                        store.add(recordClone);
                    }
                }
            });
        });

        vm.set("recordsCount", store.getRange().length);
        if (store.loadCount === 0 || store.loadCount === 1) {
            vm.set("counters.stores", vm.get("counters.stores") + 1);
        }

        this.getView().calculateHeight();
    },

    privates: {
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
                    iconCls: 'relations-grid-action ' + CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
                    tooltip: CMDBuildUI.locales.Locales.relations.opencard,
                    handler: function (grid, rowIndex, colIndex, item, eOpts, record) {
                        me.openRelatedItem(record, CMDBuildUI.mixins.DetailsTabPanel.actions.view);
                    },
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.relations.opencard'
                    }
                }, {
                    iconCls: 'relations-grid-action ' + CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
                    tooltip: CMDBuildUI.locales.Locales.relations.editrelation,
                    handler: function (grid, rowIndex, colIndex, item, eOpts, record) {
                        var relationitem = me.findRelationItem(record.getId());
                        if (relationitem && relationitem.get("_can_update")) {
                            var vm = grid.lookupViewModel();
                            CMDBuildUI.view.relations.Utils.editRelation(relationitem, {
                                theObject: vm.get("theObject"),
                                proxyurl: vm.get("storedata.proxyurl"),
                                objecttypename: vm.get("objectTypeName"),
                                objectid: vm.get("objectId")
                            }).then(function () {
                                grid.getStore().load({
                                    callback: function () {
                                        Ext.GlobalEvents.fireEventArgs("updateMasterDetailStore", [relationitem.get("_type")]);
                                        Ext.GlobalEvents.fireEventArgs("updateRelationStore", [vm.get("domain").getId()]);
                                    }
                                })
                            });
                        } else {
                            // disable if user can not edit relation
                            item.disable();
                        }
                    },
                    isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                        const vm = view.lookupViewModel();
                        const typeRelation = me.findRelationItem(record.getId()).get("_type");
                        return record.isProcessInstance || vm.get("referenceDomains").includes(typeRelation) || !vm.get('editableDomains')[typeRelation];
                    },
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.relations.editrelation'
                    }
                }, {
                    iconCls: 'relations-grid-action ' + CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'),
                    tooltip: CMDBuildUI.locales.Locales.relations.deleterelation,
                    handler: function (grid, rowIndex, colIndex, item, eOpts, record) {
                        var relationitem = me.findRelationItem(record.getId());
                        if (relationitem && relationitem.get("_can_delete")) {
                            // delete relation
                            CMDBuildUI.view.relations.Utils.deleteRelation(relationitem).then(function () {
                                Ext.GlobalEvents.fireEventArgs("updateMasterDetailStore", [relationitem.get("_type")]);
                                grid.getStore().load();
                            });
                        } else {
                            // disable if user can not delete relation
                            item.disable();
                        }
                    },
                    isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                        const vm = view.lookupViewModel();
                        const typeRelation = me.findRelationItem(record.getId()).get("_type");
                        return record.isProcessInstance || vm.get("referenceDomains").includes(typeRelation) || !vm.get('editableDomains')[typeRelation];
                    },
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.relations.deleterelation'
                    }
                }, {
                    iconCls: 'relations-grid-action ' + CMDBuildUI.util.helper.IconHelper.getIconId('edit', 'regular'),
                    tooltip: CMDBuildUI.locales.Locales.relations.editcard,
                    handler: function (grid, rowIndex, colIndex, item, eOpts, record) {
                        me.openRelatedItem(record, CMDBuildUI.mixins.DetailsTabPanel.actions.edit);
                    },
                    isActionDisabled: function (view, rowIndex, colIndex, item, record) {
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
                    iconCls: 'relations-grid-action ' + CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
                    tooltip: CMDBuildUI.locales.Locales.relations.opencard,
                    handler: function (grid, rowIndex, colIndex, item, eOpts, record) {
                        me.openRelatedItem(record);
                    },
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.relations.opencard'
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
                    iconCls: 'relations-grid-action ' + CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
                    tooltip: CMDBuildUI.locales.Locales.relations.opencard,
                    handler: function (grid, rowIndex, colIndex, item, eOpts, record) {
                        me.openRelatedItem(record);
                    },
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.relations.opencard'
                    }
                }, {
                    iconCls: 'relations-grid-action ' + CMDBuildUI.util.helper.IconHelper.getIconId('edit', 'regular'),
                    tooltip: CMDBuildUI.locales.Locales.relations.editcard,
                    handler: function (grid, rowIndex, colIndex, item, eOpts, record) {
                        me.openRelatedItem(record, CMDBuildUI.mixins.DetailsTabPanel.actions.edit);
                    },
                    isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                        var titem = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(record.get("_type"));
                        return record.isProcessInstance ||
                            !(titem.get(CMDBuildUI.model.base.Base.permissions.edit)) &&
                            view.ownerGrid.lookupViewModel().get("basepermissions.edit");
                    },
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.relations.editcard'
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
                path = CMDBuildUI.util.Navigation.getProcessBaseUrl(record.get("_type"), record.get("_id").split("_")[0]);
            } else if (record.isCard) {
                path = CMDBuildUI.util.Navigation.getClassBaseUrl(record.get("_type"), record.get("_id").split("_")[0]);
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
                    if (destination.split("_").length == 1) {
                        return relItem.get("_type") === domainname && relItem.get("_destinationId") == destination.split("_")[0];
                    } else {
                        return relItem.getId() == destination.split("_")[1];
                    }
                });
            if (relPos !== -1) {
                return rels.getAt(relPos);
            }
        }
    }
});