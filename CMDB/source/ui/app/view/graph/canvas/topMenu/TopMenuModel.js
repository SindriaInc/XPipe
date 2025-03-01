Ext.define('CMDBuildUI.view.graph.canvas.topMenu.TopMenuModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.graph-canvas-topmenu-topmenu',

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
                return data.navTreesLoaded && data.menuLenght !== 0 ? false : true;
            }
        },

        updateNavTreeMenu: {
            bind: {
                selectedNode: '{selectedNode}',
                navTreesLoaded: '{navTreesLoaded}'
            },
            get: function (data) {
                if (!data.navTreesLoaded || !data.selectedNode || data.selectedNode.length !== 1) {
                    this.set('menuLength', 0);
                    this.set('menu', []);
                    return;
                }

                var index = -1;
                const items = [],
                    me = this;

                Ext.Array.forEach(Ext.getStore('navigationtrees.NavigationTrees').getRange(), function (record, i, allrecords) {
                    const navid = record.get('_id');
                    const tmpStore = record.nodes();

                    if (Ext.Array.contains(['gisnavigation', 'bimnavigation'], navid)) {
                        return;
                    }

                    if (tmpStore) { //TODO: remove and delete single combobox instead of the whole menu component
                        var checked = false;
                        try {
                            const klass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(data.selectedNode[0].type),
                                hierarchy = klass.getHierarchy();

                            for (var j = hierarchy.length; j > 0 && index == -1; j--) {
                                index = tmpStore.find('targetClass', hierarchy[j - 1]);
                            }
                        } catch (err) {
                            console.error('Sometimes it breaks here');
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
                                    me.set("lastCheckedValue", item.checked ? item.name : null);
                                },
                                checked: checked,
                                inputValue: navid,
                                boxLabel: navid
                            });
                        }
                    }
                });

                if (items.length == 0) {
                    this.set('menuLength', 0);
                    this.set('menu', []);
                } else {
                    this.set('menuLength', items.length);
                    this.set('menu', items);
                }
            }
        }
    }
});