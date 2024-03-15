Ext.define('CMDBuildUI.view.events.history.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.events-history-grid',

    data: {
        storedata: {
            autoload: false
        }
    },

    formulas: {
        /**
         * Update store data
         */
        updateStoreData: {
            bind: {
                objectid: '{events-tabpanel.eventId}'
            },
            get: function (data) {
                if (data.objectid) {
                    var vm = this;
                    CMDBuildUI.util.helper.ModelHelper.getModel(CMDBuildUI.util.helper.ModelHelper.objecttypes.calendar, CMDBuildUI.util.helper.ModelHelper.objecttypes.event).then(function (model) {
                        // set store model name
                        var historymodel = CMDBuildUI.util.helper.ModelHelper.getHistoryModel(CMDBuildUI.util.helper.ModelHelper.objecttypes.calendar, CMDBuildUI.util.helper.ModelHelper.objecttypes.event);
                        vm.set("storedata.modelname", historymodel.getName());
                        // set store proxy url
                        vm.set("storedata.proxyurl", Ext.String.format("{0}/{1}/history", model.getProxy().getUrl(), data.objectid));
                        // set store auto load
                        vm.set("storedata.autoload", true);
                    });
                    // set isProcess variable
                    vm.set("isProcess", false);
                }
            }
        }
    },

    stores: {
        objects: {
            type: 'history',
            model: '{storedata.modelname}',
            proxy: {
                url: '{storedata.proxyurl}',
                type: 'baseproxy'
            },
            autoLoad: '{storedata.autoload}',
            autoDestroy: true
        }
    }
});
