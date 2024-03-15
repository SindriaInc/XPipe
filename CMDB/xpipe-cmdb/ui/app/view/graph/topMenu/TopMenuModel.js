Ext.define('CMDBuildUI.view.graph.topMenu.TopMenuModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.graph-topmenu-topmenu',
    data: {
        lastCheckedValue: 'init',
        menu: [],
        menuLength: 0
    },

    formulas: {
        chooseNavTreeEnable: {
            bind: {
                menuLenght: '{menuLength}',
                navTreesLoaded: '{navTreesLoaded}'
            },
            get: function (data) {
                if (data.navTreesLoaded == true && data.menuLenght !== 0) return false;
                return true;
            }
        },
        /**
         * This sets the viewModel variable {menu} ,{menuLength}
         */
        updateNavTreeMenu: {
            bind: {
                selectedNode: '{selectedNode}',
                navTreesLoaded: '{navTreesLoaded}'
            },
            get: function (data) {
                if (data.navTreesLoaded == false || data.selectedNode == null || data.selectedNode.length !== 1) {
                    this.set('menuLength', 0);
                    this.set('menu', []);
                    return;
                }

                var treeList = Ext.getStore('navigationtrees.NavigationTrees').getRange();
                var navid;
                var tmpStore;
                var checked;
                var index = -1;
                var items = [];
                var me = this;

                treeList.forEach(function (record) {
                    navid = record.get('_id');
                    tmpStore = record.nodes();

                    if (Ext.Array.contains(['gisnavigation', 'bimnavigation'], navid)) {
                        return;
                    }

                    if (tmpStore) { //TODO: remove and delete single combobox instead of the whole menu component
                        checked = false;
                        try {
                            // index = this.findRecursive(tmpStore, 'targetClass', data.selectedNode[0].type);
                            var klass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(data.selectedNode[0].type);
                            var hierarchy = klass.getHierarchy();

                            for (var i = hierarchy.length; i > 0 && index == -1; i--) {
                                index = this.findInStore(tmpStore, 'targetClass', hierarchy[i - 1]);
                            }

                        } catch (err) {
                            console.error('Sometimet it breaks here');
                        }

                        if (index !== -1) {
                            if (me.get('lastCheckedValue') == navid) {
                                checked = true;
                            }

                            items.push({
                                xtype: 'menucheckitem',
                                text: record.get('_description_translation') || record.get('description'),
                                name: navid, //important, used for the this.areEquals function
                                /**
                                 * @param {Ext.menu.Item} item
                                 * @param {ext.event.Event} eOpts
                                 * this function sets the value of the viewModel variable {lastCheckedValue}
                                 */
                                handler: function (item, eOpts) {
                                    if (item.checked == true) {
                                        me.set('lastCheckedValue', item.name);
                                    } else {
                                        me.set('lastCheckedValue', null);
                                    }
                                },
                                checked: checked,
                                inputValue: navid,
                                boxLabel: navid
                            });
                        }
                    }
                }, this);

                if (items.length == 0) {
                    this.set('menuLength', 0);
                    this.set('menu', []);
                    return;
                } else {
                    this.set('menuLength', items.length);
                    this.set('menu', items);
                    return;
                }
            }
        },
        lastCheckedValueChange: {
            bind: {
                lastCheckedValue: '{lastCheckedValue}'
            },
            get: function (data) {
                switch (data.lastCheckedValue) {
                    case 'init': {
                        return;
                    }
                    case null: {
                        this.getView().getController().onReopenGraphButtonClick();
                        return;
                    }
                    default: {
                        //apply navigationTree
                        this.getView().getController().onReopenGraphButtonClick();
                        return;
                    }
                }
            }
        }
    },

    /**
     * This function search in store if there is a record with property = value;
     * If the record is not found it searches for a different value until the value is empty.stores
     * NOTE: The different value is the parent CMDBuild Class of the current one.
     * @param {Ext.data.Data} store
     * @param {String} property
     * @param {String} value 
     * @returns {Number} the index in wich the value is stored
     */
    findRecursive: function (store, property, value) {
        if (value === "") return -1;

        var index = store.find('targetClass', value);
        if (index == -1) {
            var parentClass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(value).get('parent');
            return this.findRecursive(store, property, parentClass);
        } else {
            return index;
        }
    },

    /**
     * This function finds in the store for the record containing a property with a specified value
     * @param {Ext.data.Data} store
     * @param {String} property
     * @param {String} value 
     * @returns {Number} the index in wich the value is stored
     */
    findInStore: function (store, property, value) {
        return store.find('targetClass', value);
    }


});
