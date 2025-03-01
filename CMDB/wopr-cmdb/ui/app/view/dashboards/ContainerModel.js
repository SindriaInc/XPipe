Ext.define('CMDBuildUI.view.dashboards.ContainerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.dashboards-container',

    data: {
        title: null,
        objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.dashboard,
        objectTypeName: null,
        menuType: CMDBuildUI.model.menu.MenuItem.types.dashboard
    },

    formulas: {
        updateData: {
            bind: {
                objecttypename: '{objectTypeName}'
            },
            get: function (data) {
                if (data.objecttypename) {
                    var obj = CMDBuildUI.util.helper.ModelHelper.getDashboardFromName(data.objecttypename);

                    this.set("object", obj);
                    this.set("title", obj.getTranslatedDescription());
                }
            }
        }
    },

    stores: {
        classes: {
            type: 'chained',
            source: 'classes.Classes',
            autoDestroy: true
        }
    }

});
