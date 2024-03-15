Ext.define('CMDBuildUI.view.map.tab.cards.geoattributesGrid.geoattributesGridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.map-tab-cards-geoattributesgrid-geoattributesgrid',

    //TODO: create a better interface for this events
    onAddGeoValue: function (view, rowIndex, colIndex, item, e, record, row) {
        var me = this,
            vm = this.getViewModel(),
            record_type = record.get('_type'),
            tabpanel = this.getMapPanelTab();

        if (record_type === CMDBuildUI.model.gis.GeoAttribute.subtype.shape || record_type === CMDBuildUI.model.gis.GeoAttribute.subtype.geotiff) {
            var title = CMDBuildUI.locales.Locales.common.actions.add + ' ' + record.get('text'),
                popup = CMDBuildUI.util.Utilities.openPopup(
                    null,
                    title, {
                    xtype: 'geoserverlayer',
                    viewModel: {
                        data: {
                            record: record
                        }
                    },
                    listeners: {
                        closepopup: function (form, response) {
                            if (response.name) {
                                var viewMap = view.up().getMapContainerView().getViewMap();
                                me.onViewGeoValue(view, null, null, null, null, response, null);
                                viewMap.fireEvent("lablelvisibilitychange");
                                record.set('x', response.x);
                                record.set('y', response.y);
                            }
                            popup.close();
                        }
                    }
                },
                    null,
                    {
                        width: 400,
                        height: 250
                    }
                )

        } else {
            tabpanel.setDrawmode(true);
            vm.set('buttonsSaveCancel.hidden', false);
            tabpanel.fireEventArgs('addgeovalue', [{
                record: record,
                listeners: {
                    drawend: {
                        fn: function (points) {
                            this.setPoints(record, points);
                            vm.set('buttonsSaveCancel.saveDisabled', false);
                        },
                        scope: this
                    },
                    drawstart: {
                        fn: function (points) { },
                        scope: this
                    }
                },
                operation: 'add'
            }]);
        }
    },

    onEditGeoValue: function (view, rowIndex, colIndex, item, e, geovalue, row) {
        var me = this,
            vm = this.getViewModel(),
            record_type = geovalue.get('_type'),
            tabpanel = this.getMapPanelTab();

        if (record_type === CMDBuildUI.model.gis.GeoAttribute.subtype.shape || record_type === CMDBuildUI.model.gis.GeoAttribute.subtype.geotiff) {
            var title = CMDBuildUI.locales.Locales.common.actions.edit + ' ' + geovalue.get('text'),
                popup = CMDBuildUI.util.Utilities.openPopup(
                    null,
                    title,
                    {
                        xtype: 'geoserverlayer',
                        viewModel: {
                            data: {
                                record: geovalue
                            }
                        },
                        listeners: {
                            closepopup: function (form, response) {
                                if (response.name) {
                                    var viewMap = view.up().getMapContainerView().getViewMap();
                                    me.onViewGeoValue(view, null, null, null, null, response, null);
                                    viewMap.fireEvent("lablelvisibilitychange");
                                    geovalue.set('x', response.x);
                                    geovalue.set('y', response.y);
                                }
                                popup.close();
                            }
                        }
                    },
                    null,
                    {
                        width: 400,
                        height: 250
                    }
                );

        } else {
            tabpanel.setDrawmode(true);
            vm.set('buttonsSaveCancel.hidden', false);

            tabpanel.fireEventArgs('editgeovalue', [{
                record: geovalue,
                listeners: {
                    modifystart: {
                        fn: function (points) {
                            vm.set('buttonsSaveCancel.saveDisabled', false);
                        },
                        scope: this
                    },
                    modifyend: {
                        fn: function (points) {
                            this.setPoints(geovalue, points);
                        },
                        scope: this
                    }
                },
                operation: 'edit'
            }]);
        }
    },

    onRemoveGeoValue: function (view, rowIndex, colIndex, item, e, geovalue, row) {
        var vm = this.getViewModel(),
            record_type = geovalue.get('_type'),
            tabpanel = this.getMapPanelTab();
        vm.set('buttonsSaveCancel.hidden', false);
        vm.set('buttonsSaveCancel.saveDisabled', false);

        if (record_type !== CMDBuildUI.model.gis.GeoAttribute.subtype.shape && record_type !== CMDBuildUI.model.gis.GeoAttribute.subtype.geotiff) {
            tabpanel.setDrawmode(true);
            var event = 'removegeovalue',
                show = null;
        } else {
            var event = 'hideshowlayer',
                show = false;
        }

        tabpanel.fireEventArgs(event, [{
            record: geovalue,
            show: show,
            listeners: {
                clear: {
                    fn: function () {
                        geovalue.clearValues();
                    },
                    scope: this
                }
            }
        }]);
    },

    onViewGeoValue: function (view, rowIndex, colIndex, item, e, geovalue, row) {
        this.getMapPanelTab().fireEventArgs('animategeovalue', [{
            geovalue: geovalue
        }]);
    },

    onCancelButtonClick: function () {
        var view = this.getView(),
            store = view.getStore(),
            tabpanel = this.getMapPanelTab();

        this.getViewModel().set('buttonsSaveCancel', {
            hidden: true,
            saveDisabled: true
        });

        store.getRange().forEach(function (item, index, array) {

            var type = item.get('_type');

            if (type !== CMDBuildUI.model.gis.GeoAttribute.subtype.shape && type !== CMDBuildUI.model.gis.GeoAttribute.subtype.geotiff) {

                if (item.dirty) {
                    // Viene eseguito quando si preme cancella su un elemento salvato
                    if (!item.hasValues() && (item.getPrevious('x') || (item.getPrevious('points') && item.getPrevious('points')[0].x))) {
                        switch (type) {
                            case 'point':
                                var values = [item.getPrevious('x'), item.getPrevious('y')];
                                break;
                            case 'linestring':
                                var values = item.getPrevious('points');
                                break;
                            case 'polygon':
                                var values = [item.getPrevious('points')];
                                break;
                        }
                        var points = CMDBuildUI.map.util.Util.olCoordinatesToObject(type, values);
                        this.setPoints(item, points);

                    } else if (item.phantom) {
                        // Viene eseguito quando si preme cancella su un elemento appena inserito ma non ancora salvato
                        item.clearValues();
                    }
                }

                tabpanel.fireEventArgs('cleanmap', [item]);

            } else if (item.getPrevious('x') && !item.hasValues()) {

                tabpanel.fireEventArgs('hideshowlayer', [{
                    record: item,
                    show: true,
                    listeners: {
                        set: {
                            fn: function () {
                                this.setPoints(item, {
                                    x: item.getPrevious('x'),
                                    y: item.getPrevious('y')
                                });
                            },
                            scope: this
                        }
                    }
                }]);

            }
        }, this);

        tabpanel.setDrawmode(false);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveButtonClick: function (button, e, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        var promises = [],
            view = this.getView(),
            vm = this.getViewModel(),
            store = view.getStore(),
            tabpanel = this.getMapPanelTab(),
            cancelBtn = view.down("#cancelButton")

        button.showSpinner = true;
        CMDBuildUI.util.Utilities.disableFormButtons([button, cancelBtn]);

        store.getModifiedRecords().forEach(function (item, index, array) {
            var deferred = new Ext.Deferred();

            if (item.get("_type") === CMDBuildUI.model.gis.GeoAttribute.subtype.shape || item.get("_type") === CMDBuildUI.model.gis.GeoAttribute.subtype.geotiff) {

                if (item.getPrevious('x') && !item.hasValues()) {

                    var name = item.get("name") || item.get("_attr"),
                        url = CMDBuildUI.util.api.Classes.getGeoLayersUrl(
                            item.get('_owner_type'),
                            item.get('_owner_id')) + Ext.String.format('/{0}', name);

                    Ext.Ajax.request({
                        url: url,
                        method: 'DELETE',
                        success: function () {
                            item.set('x', null);
                            item.set('y', null);
                            deferred.resolve();
                        },
                        failure: function () {
                            deferred.reject();
                        }
                    });

                    Ext.Deferred.all([deferred.promise]).then(function () {

                        vm.set('buttonsSaveCancel', {
                            hidden: true,
                            saveDisabled: true
                        });

                        var viewMap = view.getMapContainerView().getViewMap();
                        viewMap.fireEvent("lablelvisibilitychange");
                        CMDBuildUI.util.helper.FormHelper.endSavingForm();

                    });
                }

            } else {
                var url = CMDBuildUI.util.api.Classes.getGeoValuesUrl(
                    item.get('_owner_type'),
                    item.get('_owner_id')) + Ext.String.format('/{0}', item.get('_attr'));

                //the new records
                if (item.phantom) {

                    if (item.hasValues()) {

                        //creates new geovalues
                        Ext.Ajax.request({
                            url: url,
                            method: 'PUT',
                            jsonData: item.getJsonData(),
                            success: function (response, option) {
                                item.commit(); //should change the id with the new one sent from the server
                                deferred.resolve(item);
                            },
                            failure: function (response, option) {
                                var errorMessage = JSON.parse(response.responseText).messages[0].message;
                                deferred.reject(errorMessage);
                            }
                        });

                        promises.push(deferred.promise);
                    }
                } else {
                    //modify existings geoValues
                    if (item.hasValues()) {
                        Ext.Ajax.request({
                            url: url,
                            method: 'PUT',
                            jsonData: item.getJsonData(),
                            success: function (response, option) {
                                item.commit();
                                deferred.resolve(item);
                            },
                            failure: function (response, option) {
                                var errorMessage = JSON.parse(response.responseText).messages[0].message;
                                deferred.reject(errorMessage);
                            }
                        });
                    } else {
                        //erases existing geovalues
                        Ext.Ajax.request({
                            url: url,
                            method: 'DELETE',
                            success: function (response, option) {
                                item.commit(); //should change the id erasing the old one
                                view.lookupViewModel().get("map-geoattributes-grid.theObject").getGeoValues().then(function (geovalues) {
                                    geovalues.remove(geovalues.getById(item.getId()));
                                });
                                deferred.resolve(item);
                                view.getViewModel().set("settingsMap.geometryDelete", true);
                            },
                            failure: function (response, option) {
                                var errorMessage = JSON.parse(response.responseText).messages[0].message;
                                deferred.reject(errorMessage);
                            }
                        });
                    }
                    promises.push(deferred.promise);
                }
            }
        }, this);

        Ext.Deferred.all(promises).then(
            //onFulfill
            function (resolved) {
                CMDBuildUI.util.Utilities.enableFormButtons([button, cancelBtn]);
                vm.set('buttonsSaveCancel', {
                    hidden: true,
                    saveDisabled: true
                });
                resolved.forEach(function (item, index, array) {
                    tabpanel.fireEventArgs('cleanmap', [item]);
                }, this);

                tabpanel.setDrawmode(false);
                Ext.GlobalEvents.fireEventArgs("refreshCard", [tabpanel, true]);
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
            },

            //onRejected
            function (rejected) {
                CMDBuildUI.util.Utilities.enableFormButtons([button, cancelBtn]);
                CMDBuildUI.util.Logger.log(
                    rejected,
                    CMDBuildUI.util.Logger.levels.error);
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
            },
            Ext.emptyFn, this);
    },

    privates: {

        setPoints: function (record, points) {
            var type = record.get("_type");
            switch (type) {
                case 'point':
                case 'shape':
                case 'geotiff':
                    record.set('x', points.x);
                    record.set('y', points.y);
                    break;
                case 'linestring':
                case 'polygon':
                    record.set('points', points);
                    break;
            }
        },

        /**
         * 
         * @returns {Ext.tab.Panel}
         */
        getMapPanelTab: function () {
            return this.getView().up('map-tab-tabpanel');
        }

    }

});