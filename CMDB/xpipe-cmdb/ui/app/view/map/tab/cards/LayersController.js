Ext.define('CMDBuildUI.view.map.tab.cards.LayersController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.map-tab-cards-layers',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },

        '#saveLayersPreferencesBtn': {
            click: 'onSaveLayersPreferencesBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.map.tab.cards.Layers} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = view.lookupViewModel();

        var prefs = CMDBuildUI.util.helper.UserPreferences.getGridPreferences("gis", "layers"),
            unchecked = prefs && prefs.uncheckedLayers ? prefs.uncheckedLayers : [],
            toolSave = view.down("#saveLayersPreferencesBtn");

        if (Ext.Object.isEmpty(prefs)) {
            toolSave.setIconCls("x-fa fa-save");
        } else {
            toolSave.setIconCls("cmdbuildicon-save");
        }

        CMDBuildUI.util.helper.MapHelper.getLayersMenu().then(function (tree) {
            vm.bind({
                bindTo: '{geoAttributesStore}',
                single: true
            }, function (layers) {
                var treeRoot = tree.getRoot().getChildAt(0),
                    root = {
                        id: 'root',
                        skipnode: true,
                        text: CMDBuildUI.locales.Locales.gis.root,
                        expanded: true,
                        checked: true,
                        leaf: false,
                        zoomMax: Ext.Number.MAX_SAFE_INTEGER,
                        zoomMin: Ext.Number.MIN_SAFE_INTEGER,
                        children: []
                    },
                    addedLayers = [];

                function addLeaf(rootNode, layer) {
                    layer.set('checked', !Ext.Array.contains(unchecked, layer.get("composed_name")));
                    rootNode.children.push({
                        text: layer.get('text'),
                        zoomMax: layer.get('zoomMax'),
                        zoomMin: layer.get('zoomMin'),
                        checked: layer.get('checked'),
                        ollayername: layer.get('ollayername'),
                        geoattribute: layer,
                        leaf: true
                    });
                }

                function hasCheckedChild(childItems) {
                    var hasChecked = Ext.Array.findBy(childItems, function (item) {
                        if (item.get("menuType") === CMDBuildUI.model.menu.MenuItem.types.folder) {
                            return hasCheckedChild(item.childNodes);
                        } else {
                            var layer = layers.findRecord("composed_name", item.get("objectTypeName"));
                            if (layer) {
                                return !Ext.Array.contains(unchecked, layer.get("composed_name"));
                            }
                            return false;
                        }
                    });
                    return hasChecked !== null;
                }

                function addNodes(rootNode, items) {
                    items.forEach(function (node) {
                        if (node.get("menuType") === CMDBuildUI.model.menu.MenuItem.types.folder) {
                            var folder = {
                                text: node.get("_objectDescription_translation"),
                                expanded: true,
                                checked: hasCheckedChild(node.childNodes), // only if there is at least one child checked
                                leaf: false,
                                skipnode: true,
                                children: []
                            };
                            addNodes(folder, node.childNodes);

                            if (folder.children.length) {
                                // add folder to root node
                                rootNode.children.push(folder);
                                // set min and max zoom
                                folder.zoomMin = Ext.Array.min(Ext.Array.pluck(folder.children, "zoomMin"));
                                folder.zoomMax = Ext.Array.max(Ext.Array.pluck(folder.children, "zoomMax"));
                            }
                        } else {
                            var layer = layers.findRecord("composed_name", node.get("objectTypeName"));
                            if (layer) {
                                addLeaf(rootNode, layer);
                                addedLayers.push(layer.get("_id"));
                            }
                        }
                    });
                }

                if (treeRoot) {
                    // add nodes in the tree
                    addNodes(root, treeRoot.childNodes);
                }

                // add missing layers
                if (addedLayers.length !== layers.getTotalCount()) {
                    layers.getRange().forEach(function (layer) {
                        if (!Ext.Array.contains(addedLayers, layer.get("_id"))) {
                            addLeaf(root, layer);
                        }
                    });
                }

                vm.set("treeRootItem", root);
            });
        });
    },

    /**
     * @param {Ext.store.Tree} store
     * @param {Ext.data.Model} record
     * @param {String} operation
     * @param {String[]} modifiedFieldNames
     * @param {Object} details
     * @param {Object} eOpts
     */
    onLayersTreeStoreUpdate: function (store, record, operation, modifiedFieldNames, details, eOpts) {
        var checked = record.get("checked");
        if (record.isLeaf()) {
            record.get("geoattribute").set("checked", checked);
            if (checked && !record.parentNode.get('checked')) {
                store.suspendEvent('update');
                record.parentNode.set('checked', checked);
                store.resumeEvent('update');
            } else if (!checked && record.parentNode.get('checked')) {
                // check if parentNode has checked childs
                var hasChecked = Ext.Array.findBy(record.parentNode.childNodes, function (node) {
                    return node.get('checked') === true;
                });
                store.suspendEvent('update');
                record.parentNode.set('checked', hasChecked !== null);
                store.resumeEvent('update');

            }

        } else {
            Ext.Array.forEach(record.childNodes, function (child, index) {
                child.set("checked", checked);
            });
        }
    },

    /**
     * @param {Ext.panel.Tool} tool
     * @param {Event} event
     */
    onSaveLayersPreferencesBtnClick: function (tool, event) {
        function savePreferences() {
            var vm = tool.lookupViewModel(),
                allLayers = vm.get("layerStore").getSource(),
                unchecked = [];

            allLayers.getRange().forEach(function (layer) {
                if (!layer.get("checked")) {
                    unchecked.push(layer.get("composed_name"));
                }
            });
            CMDBuildUI.util.helper.UserPreferences.updateGridPreferences("gis", "layers", {
                uncheckedLayers: unchecked
            }).then(function () {
                CMDBuildUI.util.Notifier.showSuccessMessage(CMDBuildUI.locales.Locales.gis.layersTab.preferencesSaved);
                tool.setIconCls("cmdbuildicon-save");
            });
        }

        function clearPreferences() {
            CMDBuildUI.util.helper.UserPreferences.updateGridPreferences("gis", "layers", {
                uncheckedLayers: undefined
            }).then(function () {
                CMDBuildUI.util.Notifier.showSuccessMessage(CMDBuildUI.locales.Locales.gis.layersTab.preferencesCleared);
                tool.setIconCls("x-fa fa-save");
            });
        }

        if (Ext.Object.isEmpty(CMDBuildUI.util.helper.UserPreferences.getGridPreferences("gis", "layers"))) {
            // save preferences if not saved yet
            savePreferences();
        } else {
            // show menu to chose action update or clear
            if (tool.menu) {
                tool.menu.show();
            } else {
                tool.menu = Ext.create('Ext.menu.Menu', {
                    autoShow: true,
                    items: [{
                        text: CMDBuildUI.locales.Locales.gis.layersTab.updatePreferences,
                        iconCls: 'x-fa fa-save',
                        handler: function () {
                            savePreferences();
                        }
                    }, {
                        text: CMDBuildUI.locales.Locales.gis.layersTab.clearPreferences,
                        iconCls: 'x-fa fa-remove',
                        handler: function () {
                            clearPreferences();
                        }
                    }]
                });
                tool.menu.alignTo(tool.el.id, 't-b?');
            }
        }
    }
});