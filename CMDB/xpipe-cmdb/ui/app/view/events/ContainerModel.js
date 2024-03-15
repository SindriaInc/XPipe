Ext.define('CMDBuildUI.view.events.ContainerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.events-container',
    data: {
        objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.calendar,
        objectTypeName: CMDBuildUI.util.helper.ModelHelper.objecttypes.event,
        menuType: CMDBuildUI.model.menu.MenuItem.types.calendar,
        title: null,
        activeview: 'grid',

        search: {
            value: null
        },

        statuscombo: {
            value: null
        },

        categorycombo: {
            value: null
        },

        datecombo: {
            value: null
        }
    },

    formulas: {
        changeActiveView: {
            bind: '{activeview}',
            get: function (activeView) {
                switch (activeView) {
                    case CMDBuildUI.view.events.Container.grid:
                        this.set("buttonText", CMDBuildUI.locales.Locales.calendar.calendar);
                        this.set("datecomboHidden", false);
                        break;
                    case CMDBuildUI.view.events.Container.calendar:
                        this.set("buttonText", CMDBuildUI.locales.Locales.calendar.grid);
                        this.set("datecomboHidden", true);
                        break;
                }
            }
        },

        'update-events-container-selectedId': {
            bind: {
                selectedId: '{events-container.selectedId}'
            },
            get: function (data) {
                this.getView().setSelectedId(data.selectedId);
            }
        }
    },

    stores: {

        statuscombostore: {
            model: 'CMDBuildUI.model.lookups.Lookup',
            proxy: {
                type: 'baseproxy',
                url: CMDBuildUI.util.api.Lookups.getLookupValues('CalendarEventStatus')
            },
            autoLoad: true,
            remoteFilter: false,
            autoDestroy: true
        },

        categorycombostore: {
            model: 'CMDBuildUI.model.lookups.Lookup',
            proxy: {
                type: 'baseproxy',
                url: CMDBuildUI.util.api.Lookups.getLookupValues('CalendarCategory')
            },
            autoLoad: true,
            remoteFilter: false,
            autoDestroy: true
        },

        datecombostore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{datecombostoredata}'
        }
    }

});
