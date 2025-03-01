Ext.define('CMDBuildUI.view.management.TitleController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.management-title',

    listen: {
        global: {
            modifyfavouriteicontitle: 'onModifyFavouriteIconTitle'
        }
    },

    /**
     * On favourites icon click.
     *
     * @param {Event} event 
     * @param {HTMLElement} item 
     * @param {Object} eOpts 
     */
    onFavouritesIconClick: function (event, item, eOpts) {
        var view = this.getView();
        // get favourites list
        var favourites = CMDBuildUI.util.helper.UserPreferences.getFavouritesMenuItems() || [];
        var inFavourites = CMDBuildUI.util.helper.UserPreferences.isItemInFavourites(view.getMenuType(), view.getObjectTypeName());
        if (inFavourites) {
            // remove item
            Ext.Array.remove(favourites, inFavourites);
        } else {
            // add item
            Ext.Array.push(favourites, {
                menuType: view.getMenuType(),
                objectTypeName: view.getObjectTypeName()
            });
        }
        // save changes
        CMDBuildUI.util.helper.UserPreferences.updateFavouritesMenuItems(favourites);
        Ext.GlobalEvents.fireEventArgs("favouritesmenuchange", [favourites]);

        // update icon in title bar
        view.updateFavouritesIcon();
    },

    /**
    * Modify favourite icon on title
    */
    onModifyFavouriteIconTitle: function () {
        this.getView().updateFavouritesIcon();
    }

});