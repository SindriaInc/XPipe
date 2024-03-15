Ext.define('CMDBuildUI.view.management.navigation.TreeItemController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.management-treelistitem',

    /**
     * On resize
     * @param {HTMLElement} item
     */
    onResize: function (item) {
        var textEl = item.component.textElement,
            div = item.el.child("div");
        if (div && textEl.dom.clientWidth - textEl.dom.scrollWidth < 0) {
            var tooltip = item.component.getNode().get('objectdescription_translation') || item.component.getNode().get('objectdescription');
            if (tooltip) {
                div.dom.dataset.qtip = tooltip;
                div.dom.dataset.qalign = 'tl-tr';
            }
        } else if (div && div.dom.dataset.qtip) {
            delete div.dom.dataset.qtip;
        }
    },

    /**
     * On Context Menu
     * @param {Event} event
     * @param {HTMLElement} item
     * @param {Object} eOpts
     */
    onContextMenu: function (event, item, eOpts) {
        var view = this.getView(),
            node = view.getNode();

        if (!Ext.Array.contains([CMDBuildUI.model.menu.MenuItem.types.folder, CMDBuildUI.model.menu.MenuItem.types.navtree, CMDBuildUI.model.menu.MenuItem.types.navtreeitem], node.get("menutype"))) {
            var items = CMDBuildUI.util.helper.UserPreferences.getFavouritesMenuItems(),
                old_items = Ext.Array.clone(items),
                current_item = Ext.Array.findBy(items, function (item, index) {
                    return item.objectTypeName === node.get('objecttypename') && item.menuType === node.get("menutype");
                });
            // create menu
            var menu_grid = new Ext.menu.Menu({
                listeners: {
                    hide: function () {
                        // remove class and destroy the menu on menu hide
                        view.toggleCls(view.contextmenuCls);
                        Ext.asap(function () {
                            menu_grid.destroy();
                        });
                    }
                },
                items: [{
                    text: CMDBuildUI.locales.Locales.menu.favouritesadd,
                    iconCls: Ext.baseCSSPrefix + 'fa fa-star-o',
                    hidden: !!current_item,
                    handler: function () {
                        // add item to favourites list
                        items.push({
                            menuType: node.get('menutype'),
                            objectTypeName: node.get('objecttypename')
                        });
                        // save and fire event
                        CMDBuildUI.util.helper.UserPreferences.updateFavouritesMenuItems(items);
                        Ext.GlobalEvents.fireEventArgs("favouritesmenuchange", [items, old_items]);
                        Ext.GlobalEvents.fireEvent("modifyfavouriteicontitle");
                    }
                }, {
                    text: CMDBuildUI.locales.Locales.menu.favouritesremove,
                    iconCls: Ext.baseCSSPrefix + 'fa fa-star',
                    hidden: !current_item,
                    handler: function () {
                        // remove item from favourites list
                        Ext.Array.remove(items, current_item);
                        // save and fire event
                        CMDBuildUI.util.helper.UserPreferences.updateFavouritesMenuItems(items);
                        Ext.GlobalEvents.fireEventArgs("favouritesmenuchange", [items, old_items]);
                        Ext.GlobalEvents.fireEvent("modifyfavouriteicontitle");
                    }
                }]
            });

            // show menu
            menu_grid.showAt(event.getXY());
            // add context menu class on row
            view.toggleCls(view.contextmenuCls);
        }

        // prevent event propagation
        event.stopEvent();
    }
});