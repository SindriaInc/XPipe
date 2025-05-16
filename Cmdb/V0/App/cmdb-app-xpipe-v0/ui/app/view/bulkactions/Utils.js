Ext.define('CMDBuildUI.view.bulkactions.Util', {
    singleton: true,

    /**
     * 
     * @param {Ext.grid.Panel} grid 
     * @return {Object} Object with `url`, `advancedFitler` and `count`.
     */
    getRequestInfo: function (grid) {
        var store = grid.getStore(),
            selection = grid.getSelection(),
            resp = {
                url: store.getProxy().getUrl(),
                advancedFitler: undefined,
                count: undefined
            };

        // evaluate advanced filter and count
        if (grid.isSelectAllPressed) {
            // if select all advanced fitler becomes store filter 
            // and count becomes store total count
            resp.advancedFitler = store.getAdvancedFilter();
            resp.count = store.getTotalCount();
        } else {
            // if not select all create a filter with ID attribute 
            // and value the list of selected items. Count is the 
            // lenght of the array
            var selectedids = [],
                lengthRequest = 50;

            selection.forEach(function (sel) {
                selectedids.push(sel.getId());
            });

            var selectedLength = selectedids.length;
            if (selectedLength > lengthRequest) {
                var filters = [],
                    numberRequests = Math.ceil(selectedLength / lengthRequest);
                for (var i = 0; i < numberRequests; i++) {
                    var advancedFitler = new CMDBuildUI.util.AdvancedFilter();
                    advancedFitler.addAttributeFilter('Id', 'in', Ext.Array.slice(selectedids, i * lengthRequest, (i + 1) * lengthRequest));
                    filters.push(advancedFitler.encode());
                }
                resp.advancedFitler = filters;
            } else {
                var advancedFitler = new CMDBuildUI.util.AdvancedFilter();
                advancedFitler.addAttributeFilter('Id', 'in', selectedids);
                resp.advancedFitler = advancedFitler.encode();
            }

            resp.count = selectedLength;
        }
        return resp;
    },

    delete: function (grid) {
        // get request info
        var me = this,
            requestinfo = CMDBuildUI.view.bulkactions.Util.getRequestInfo(grid),
            type = grid.lookupViewModel().get("objectTypeName");

        // create confirm message
        var message = Ext.String.format(
            CMDBuildUI.locales.Locales.bulkactions.confirmdelete,
            requestinfo.count
        );

        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        CMDBuildUI.view.classes.cards.Util.getDeleteMsg(type, requestinfo.advancedFitler, message).then(function () {
            var loadMask = CMDBuildUI.util.Utilities.addLoadMask(grid),
                allPromises = [];

            if (Ext.isArray(requestinfo.advancedFitler)) {
                Ext.Array.forEach(requestinfo.advancedFitler, function (item, index, allitems) {
                    allPromises.push(me.makeDeleteRequest(requestinfo.url, item));
                });
            } else {
                allPromises.push(me.makeDeleteRequest(requestinfo.url, requestinfo.advancedFitler));
            }

            Ext.Promise.all(allPromises).then(function (responses, eOpts) {
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                CMDBuildUI.util.Utilities.removeLoadMask(loadMask);
                grid.setSelection();
                // reload store
                grid.getStore().load();
                var viewMap = grid.getGridContainer().getViewMap();
                if (viewMap) {
                    viewMap.fireEvent("lablelvisibilitychange");
                    var interaction = viewMap.getOl_interaction_select(viewMap.getOlMap());
                    interaction.dispatchEvent(new ol.interaction.Select.Event('select', [], [], {
                        deselectAll: true
                    }));
                }
            }, function () {
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                CMDBuildUI.util.Utilities.removeLoadMask(loadMask);
            });
        });
    },

    abort: function (grid) {
        // get request info
        var me = this,
            requestinfo = CMDBuildUI.view.bulkactions.Util.getRequestInfo(grid);

        // create confirm message
        var message = Ext.String.format(
            CMDBuildUI.locales.Locales.bulkactions.confirmabort,
            requestinfo.count
        );

        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.notifier.attention,
            message,
            function (btn) {
                if (btn === "yes") {
                    CMDBuildUI.util.helper.FormHelper.startSavingForm();
                    var loadMask = CMDBuildUI.util.Utilities.addLoadMask(grid),
                        allPromises = [];

                    if (Ext.isArray(requestinfo.advancedFitler)) {
                        Ext.Array.forEach(requestinfo.advancedFitler, function (item, index, allitems) {
                            allPromises.push(me.makeDeleteRequest(requestinfo.url, item));
                        });
                    } else {
                        allPromises.push(me.makeDeleteRequest(requestinfo.url, requestinfo.advancedFitler));
                    }

                    Ext.Promise.all(allPromises).then(function (responses, eOpts) {
                        CMDBuildUI.util.helper.FormHelper.endSavingForm();
                        CMDBuildUI.util.Utilities.removeLoadMask(loadMask);
                        grid.setSelection();
                        // reload store
                        grid.getStore().load();
                    }, function () {
                        CMDBuildUI.util.helper.FormHelper.endSavingForm();
                        CMDBuildUI.util.Utilities.removeLoadMask(loadMask);
                    });
                }
            }
        );
    },

    privates: {

        /**
         * Make delete request
         * @param {String} url 
         * @param {String} filter 
         * @returns {Ext.promise.Promise} 
         */
        makeDeleteRequest: function (url, filter) {
            var deferred = new Ext.Deferred();

            // make ajax request
            Ext.Ajax.request({
                url: url,
                method: 'DELETE',
                jsonData: {},
                params: {
                    filter: filter
                },
                callback: function (request, success, response) {
                    if (success) {
                        deferred.resolve();
                    } else {
                        deferred.reject();
                    }
                }
            });

            return deferred.promise;
        }

    }
});