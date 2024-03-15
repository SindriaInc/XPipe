Ext.define('CMDBuildUI.view.management.navigation.FavouritesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.management-navigation-favourites',

    control: {
        '#': {
            itemclick: 'onItemClick',
            itemcontextmenu: 'onItemContextMenu',
            selectionchange: 'onSelectionChange'
        }
    },

    /**
     * On navigation tree item click.
     *
     * @param {CMDBuildUI.view.management.navigation.Favourites} view
     * @param {CMDBuildUI.model.menu.MenuItem} record
     * @param {HTMLElement} item
     * @param {Number} index
     * @param {Ext.event.Event} e
     * @param {Object} eOpts
     */
    onItemClick: function (view, record, item, index, e, eOpts) {
        view.up('management-navigation-container').openResourceByNode(record);

        // clear selection in navigation menu to prevent double selction in menu
        view.lookupViewModel().set('selected.navigation', null);
    },

    /**
     * On mouse right click.
     *
     * @param {CMDBuildUI.view.management.navigation.Favourites} view
     * @param {CMDBuildUI.model.menu.MenuItem} record
     * @param {HTMLElement} item
     * @param {Number} index
     * @param {Ext.event.Event} e
     * @param {Object} eOpts
     */
    onItemContextMenu: function (view, record, item, index, e, eOpts) {
        var cls = Ext.baseCSSPrefix + 'grid-item-contextmenu';

        if (!record.get("root")) {
            // create menu
            var menuItems = [{
                text: CMDBuildUI.locales.Locales.menu.favouritesremove,
                iconCls: Ext.baseCSSPrefix + 'fa fa-star',
                handler: function () {
                    var items = CMDBuildUI.util.helper.UserPreferences.getFavouritesMenuItems(),
                        current_item = Ext.Array.findBy(items, function (item, index) {
                            return item.objectTypeName === record.get('objecttypename') && item.menuType === record.get('menutype');
                        });
                    Ext.Array.remove(items, current_item);
                    // save and fire event
                    CMDBuildUI.util.helper.UserPreferences.updateFavouritesMenuItems(items);
                    // remove item from menu
                    var vm = view.lookupViewModel();
                    var root = vm.get('favourites').getRoot();
                    root.removeChild(record);
                    // update menu visibility
                    vm.set('visibility.favourites', root.hasChildNodes());
                    Ext.GlobalEvents.fireEvent("modifyfavouriteicontitle");
                }
            }, {
                text: CMDBuildUI.locales.Locales.menu.setasinitialpage,
                iconCls: Ext.baseCSSPrefix + 'fa fa-home',
                disabled: CMDBuildUI.util.helper.UserPreferences.getPreferences().get(CMDBuildUI.model.users.Preference.startingpage_actual) === record.get('findcriteria'),
                handler: function () {
                    var jsonData = {};
                    jsonData[CMDBuildUI.model.users.Preference.startingpage] = record.get('findcriteria');
                    CMDBuildUI.util.helper.UserPreferences.updatePreferences(jsonData).then(function () {
                        CMDBuildUI.util.helper.UserPreferences.load().then(function (record) {
                            CMDBuildUI.util.Logger.log("preferred initial page is now: " + record.get(CMDBuildUI.model.users.Preference.startingpage_actual), CMDBuildUI.util.Logger.levels.debug);
                        });
                    });

                }
            }];

            var menu_grid = new Ext.menu.Menu({
                listeners: {
                    hide: function () {
                        // remove class and destroy the menu on menu hide
                        view.toggleCls(cls);
                        Ext.asap(function () {
                            menu_grid.destroy();
                        });
                    }
                },
                items: menuItems
            });

            // show menu
            menu_grid.showAt(e.getXY());
            // add context menu class on row
            view.toggleCls(cls);
        }

        // prevent event propagation
        e.stopEvent();
    },

    /**
     * On selected item change.
     *
     * @param {CMDBuildUI.view.management.navigation.Favourites} view
     * @param {CMDBuildUI.model.menu.MenuItem} record
     * @param {Object} eOpts
     */
    onSelectionChange: function (view, records, eOpts) {
        function expandNode(r) {
            if (r) {
                r.expand();
                expandNode(r.parentNode);
            }
        }
        if (records.length) {
            expandNode(records[0]);
        }
    },

    /**
     * 
     * @param {HTMLElement} node 
     * @param {Object} data 
     * @param {Ext.data.TreeModel} overModel 
     * @param {String} dropPosition 
     * @param {Object} eOpts 
     */
    onDropRecords: function (node, data, overModel, dropPosition, eOpts) {
        var items = [];
        Ext.Array.forEach(data.view.getStore().getRootNode().childNodes, function (node, index, allitems) {
            items.push({
                menuType: node.get('menutype'),
                objectTypeName: node.get('objecttypename')
            });
        })
        CMDBuildUI.util.helper.UserPreferences.updateFavouritesMenuItems(items);
    }

});
