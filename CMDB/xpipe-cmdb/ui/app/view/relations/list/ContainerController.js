Ext.define('CMDBuildUI.view.relations.list.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.relations-list-container',

    listen: {
        global: {
            updateRelationStore: 'reloadStore'
        }
    },

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            activate: 'onActivate',
            resize: 'onResize'
        },
        '#addrelationbtn': {
            beforerender: 'onAddRelationBtnBeforeRender'
        },
        '#openrelgraphbtn': {
            click: 'onOpenRelgrapBtnClick'
        },
        '#showextendedfield': {
            beforerender: 'onShowExtendedFieldBeforeRender',
            change: 'onShowExtendedFieldChange'
        }
    },

    /**
     * Refresh data on tab activate event
     *
     * @param {CMDBuildUI.view.relations.list.Container} view
     * @param {Object} eOpts
     */
    onActivate: function (view, eOpts) {
        var vm = view.lookupViewModel(),
            store = vm.get("allRelations");
        if (store && !store.isLoading() && store.isLoaded()) {
            var loadmask = CMDBuildUI.util.Utilities.addLoadMask(view);
            // enable remote sort here because
            // autoLoad=false is ignored when grouping is actived
            // and remoteSort is set to true. See EXTJS-19781.
            store.setRemoteSort(true);
            // load store
            store.load({
                callback: function () {
                    CMDBuildUI.util.Utilities.removeLoadMask(loadmask);
                },
                scope: this
            });
        }
    },

    /**
     * @param {CMDBuildUI.view.relations.list.Container} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var me = this;
        view.lookupViewModel().bind({
            domain: '{domains}',
            store: '{allRelations}'
        }, function (data) {
            var store = data.store;
            if (data.domain && store) {
                if (CMDBuildUI.util.Navigation.getCurrentContext().extendedrels) {
                    if (store && !store.isLoading() && !store.isLoaded()) {
                        store.load({
                            callback: function () {
                                me.addFieldsets();
                            }
                        })
                    } else {
                        me.addFieldsets();
                    }
                } else {
                    me.addRelationsGrid();
                }
            }
        });
    },

    onResize: function (view, width, height, oldWidth, oldHeight, eOpts) {
        var realallrelgrids = view.lookupReference('relallrelsgrid');
        if (realallrelgrids && realallrelgrids.down('grid')) {
            realallrelgrids.down('grid').calculateHeight();
        }

        var relfieldsetscontainer = view.lookupReference('relfieldsetscontainer');
        if (relfieldsetscontainer) {
            var items = relfieldsetscontainer.items.getRange()
            Ext.Array.forEach(items, function (item, index, array) {
                if (item.isVisible()) {
                    item.calculateHeight();
                }
            }, this)
        }
    },

    /**
     * @param {Ext.button.Button} button Add relation button
     * @param {Object} eOpts
     */
    onAddRelationBtnBeforeRender: function (button) {
        var vm = this.getViewModel(),
            objectTypeName = vm.get("objectTypeName"),
            object = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName, vm.get("objectType")),
            objectHierarchy = object.getHierarchy();

        /**
         *
         * @param {String} description Relation description
         * @param {String} type The name of the target type
         * @param {Object} domain Domain definition
         * @param {String} direction forward|backward
         */
        function createMenuItem(description, type, domain, direction) {
            var item = CMDBuildUI.util.helper.ModelHelper.getClassFromName(type);
            if (item) {
                return {
                    text: Ext.String.format('{0} ({1})', description, item.getTranslatedDescription()),
                    iconCls: 'x-fa fa-file-text-o',
                    listeners: {
                        click: 'onAddRelationMenuItemClick'
                    },
                    type: type,
                    domain: domain,
                    disabled: !domain.get(CMDBuildUI.model.base.Base.permissions.add),
                    direction: direction
                };
            }
        }

        object.getDomains().then(function (domains) {
            var menu = [],
                addbtnDisabled = true;

            vm.setStores({
                domains: domains
            });

            domains.each(function (domain) {
                if (
                    Ext.Array.contains(objectHierarchy, domain.get("source")) &&
                    !Ext.Array.contains(domain.get('disabledSourceDescendants'), vm.get("objectTypeName")) &&
                    !domain.get("destinationProcess")
                ) {
                    menu.push(createMenuItem(domain.getTranslatedDescriptionDirect(), domain.get("destination"), domain, 'direct'));
                }

                if (
                    Ext.Array.contains(objectHierarchy, domain.get("destination")) &&
                    !Ext.Array.contains(domain.get('disabledDestinationDescendants'), vm.get("objectTypeName")) &&
                    !domain.get("sourceProcess")
                ) {
                    menu.push(createMenuItem(domain.getTranslatedDescriptionInverse(), domain.get("source"), domain, 'inverse'));
                }
                // enable add relation button if there is at least one domain that can be added
                if (addbtnDisabled && domain.get(CMDBuildUI.model.base.Base.permissions.add)) {
                    addbtnDisabled = false;
                }
            });
            vm.set("addbtn.disabled", addbtnDisabled);
            button.setMenu(menu);
        });

    },

    /**
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} e
     * @param {Object} eOpts
     */
    onAddRelationMenuItemClick: function (item, e, eOpts) {
        var vm = this.getViewModel(),
            me = this,
            view = this.getView(),
            multiselect = item.domain.get("cardinality") === CMDBuildUI.model.domains.Domain.cardinalities.manytomany || (item.direction == 'inverse' && item.domain.get("cardinality") == CMDBuildUI.model.domains.Domain.cardinalities.manytoone) || (item.direction == 'direct' && item.domain.get("cardinality") == CMDBuildUI.model.domains.Domain.cardinalities.onetomany);
        CMDBuildUI.util.helper.ModelHelper.getModel('class', item.type).then(function (model) {
            var popup;
            var title = item.text;
            var config = {
                xtype: 'relations-list-add-container',
                originTypeName: vm.get("objectTypeName"),
                originId: vm.get("objectId"),
                multiSelect: multiselect,
                viewModel: {
                    data: {
                        objectTypeName: item.type,
                        relationDirection: item.direction,
                        theDomain: item.domain
                    }
                },
                listeners: {
                    popupclose: function () {
                        popup.removeAll(true);
                        popup.close();
                    }
                },
                onSaveSuccess: function () {
                    vm.get("allRelations").reload();
                    Ext.GlobalEvents.fireEventArgs("updateMasterDetailStore", [item.domain.getId()]);
                    me.reloadFieldsetsGrids();
                }
            };

            popup = CMDBuildUI.util.Utilities.openPopup('popup-add-relation', title, config, null);

        });
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Ext.event.Event} event
     * @param {Object} e
     */
    onOpenRelgrapBtnClick: function (button, event, e) {
        var vm = button.lookupViewModel();
        CMDBuildUI.util.Ajax.setActionId("class.card.relgraph.open");
        CMDBuildUI.util.Utilities.openPopup('graphPopup', CMDBuildUI.locales.Locales.relationGraph.relationGraph, {
            xtype: 'graph-graphcontainer',
            _id: vm.get("objectId"),
            _type: vm.get("objectTypeName")
        });
    },

    /**
     *
     * @param {Ext.form.field.CheckBox} field
     * @param {*} eOpts
     */
    onShowExtendedFieldBeforeRender: function (field, eOpts) {
        field.setValue(CMDBuildUI.util.Navigation.getCurrentContext().extendedrels);
    },
    /**
     *
     * @param {Ext.form.field.CheckBox} field
     * @param {Boolean} value
     * @param {Object} eOpts
     */
    onShowExtendedFieldChange: function (field, value, eOpts) {
        if (value) {
            this.addFieldsets();
        } else {
            this.addRelationsGrid();
        }
        CMDBuildUI.util.Navigation.getCurrentContext().extendedrels = value;
    },

    /**
     * Reload relation store
     */
    reloadStore: function () {
        var store = this.getView().getViewModel().get("allRelations");
        if (store) {
            store.load();
        }
    },

    privates: {
        addRelationsGrid: function () {
            var view = this.getView(),
                panelref = 'relallrelsgrid',
                panel = view.lookupReference(panelref);
            if (!panel) {
                panel = view.add({
                    xtype: 'container',
                    reference: panelref
                })
                panel.add({
                    xtype: 'relations-list-grid'
                });
            }
            view.setActiveItem(panel);
        },

        addFieldsets: function () {
            var view = this.getView(),
                vm = view.lookupViewModel(),
                panelref = 'relfieldsetscontainer',
                panel = view.lookupReference(panelref),
                objectTypeName = vm.get("objectTypeName");

            if (!panel) {
                var loader,
                    object = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName, vm.get("objectType")),
                    objectHierarchy = object.getHierarchy();

                panel = view.add({
                    xtype: 'container',
                    reference: panelref,
                    scrollable: true
                });

                Ext.asap(function () {
                    loader = CMDBuildUI.util.Utilities.addLoadMask(panel);
                });

                object.getDomains().then(function (domains) {
                    vm.set("counters.domains", domains.getTotalCount());
                    domains.getRange().forEach(function (domain) {
                        var added;
                        function addfieldset(direction) {
                            panel.add(Ext.applyIf({
                                xtype: 'relations-list-expanded-fieldset',
                                viewModel: {
                                    data: {
                                        domain: domain,
                                        direction: direction
                                    }
                                }
                            }));
                        }

                        if (Ext.Array.contains(objectHierarchy, domain.get("source")) &&
                            !Ext.Array.contains(domain.get('disabledSourceDescendants'), objectTypeName)) {
                            addfieldset("_2");
                            added = true;
                        }
                        if (Ext.Array.contains(objectHierarchy, domain.get("destination")) &&
                            !Ext.Array.contains(domain.get('disabledDestinationDescendants'), objectTypeName)) {
                            addfieldset("_1");
                            added = true;
                        }
                        if (!added) {
                            vm.set("counters.domains", vm.get("counters.domains") - 1);
                        }

                    });
                });

                var bind = vm.bind({
                    bindTo: {
                        domainscount: '{counters.domains}',
                        storescounter: '{counters.stores}'
                    }
                }, function () {
                    Ext.asap(function () {
                        CMDBuildUI.util.Utilities.removeLoadMask(loader);
                    });
                    bind.destroy();
                });
            }
            view.setActiveItem(panel);
        },

        /**
         * Reload relation grids on fieldsets
         *
         */
        reloadFieldsetsGrids: function () {
            var view = this.getView(),
                panel = view.lookupReference('relfieldsetscontainer');
            if (panel) {
                panel.items.items.forEach(function (fieldset) {
                    var records = fieldset.lookupViewModel().get("records");
                    if (records) {
                        records.load();
                    }
                });
            }
        }
    }

});