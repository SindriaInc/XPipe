Ext.define('CMDBuildUI.mixins.grids.AddButtonMixin', {
    mixinId: 'grids-addbutton-mixin',

    /**
     * @property {String} typeicon
     * 
     * The icon to use in add button menu.
     */
    typeicon: null,

    /**
     * Updates add button by adding handler 
     * or menu with addable sub-types.
     * 
     * @param {Ext.button.Button} button
     * @param {String} handler
     * @param {String} objectTypeName
     */
    updateAddButton: function (button, handler, objectTypeName, objectType, disableItems) {
        var me = this;
        var object = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName, objectType);

        var permission = CMDBuildUI.model.base.Base.permissions.add;
        if (objectType === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
            permission = CMDBuildUI.model.base.Base.permissions.start;
        }

        // default value 
        var hasPermissions = false;

        // disable add button if class is undefined
        if (!object) {
            button.setDisabled(true);
            return;
        }

        if (object.get("prototype")) {
            var menu = [];
            var children = object.getChildren(true);

            if (children.length) {
                hasPermissions = true;
            }

            // create menu definition by adding non-prototype classes
            children.forEach(function (child) {
                var forceDisable = !Ext.isEmpty(disableItems) ? Ext.Array.contains(disableItems, child.get("name")) : false;
                menu.push({
                    text: child.getTranslatedDescription(),
                    iconCls: me.typeicon,
                    disabled: !child.get(permission) || forceDisable,
                    listeners: {
                        click: handler
                    },
                    objectTypeName: child.get("name")
                });
            });

            // sort menu by description
            Ext.Array.sort(menu, function (a, b) {
                return a.text === b.text ? 0 : (a.text < b.text ? -1 : 1);
            });

            // add menu to button
            button.setMenu(menu);
        } else {
            button.objectTypeName = objectTypeName;
            button.addListener("click", handler, this);
            hasPermissions = object.get(permission);
        }

        // enable button if can add

        button.setDisabled(!hasPermissions);
    }
});