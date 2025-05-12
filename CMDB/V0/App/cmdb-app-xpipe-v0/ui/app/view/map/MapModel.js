Ext.define('CMDBuildUI.view.map.MapModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.map-map',

    formulas: {
        updateFilter: {
            bind: {
                layerStore: '{layerStore}',
                zoom: '{map.zoom}',
                objectTypeName: '{map.objectTypeName}',
                drawmode: '{map.drawmode}',
                start: '{map.start}'
            }, get: function (data) {
                if (data.layerStore && Ext.isNumber(data.zoom) && data.objectTypeName && !data.drawmode && data.start && CMDBuildUI.util.Navigation.getCurrentContext().objectType !== CMDBuildUI.util.helper.ModelHelper.objecttypes.navtreecontent) {
                    data.layerStore.setFilters([{
                        id: CMDBuildUI.view.map.Map.filterZoomMaxId,
                        property: 'zoomMax',
                        operator: '>=',
                        value: data.zoom
                    }, {
                        id: CMDBuildUI.view.map.Map.filterZoomMinId,
                        property: 'zoomMin',
                        operator: '<=',
                        value: data.zoom
                    }, {
                        id: CMDBuildUI.view.map.Map.filterVisibility,
                        filterFn: function (item) {
                            return Ext.Array.contains(item.get('visibility'), data.objectTypeName);
                        },
                        scope: this
                    }]);
                }
            }
        },

        updateOlSelectInteractionSelected: {
            bind: {
                olMap: '{map.olMap}',
                theObject: '{map.theObject}',
                layerStore: '{layerStore}',
                drawmode: '{map.drawmode}'
            }, get: function (data) {
                var view = this.getView(),
                    geometryDelete = this.get("settingsMap.geometryDelete"),
                    multiselection = this.get("contextmenu.multiselection.enabled");
                if (data.theObject && data.olMap && data.layerStore && data.drawmode == false && !geometryDelete) {
                    data.theObject.getGeoValues().then(function (geovalues) {
                        if (!view.destroyed) {

                            var geoval = [];
                            Ext.Array.forEach(geovalues.getRange(), function (geovalue, index) {

                                var geovalueklassname = geovalue.get('_owner_type'),
                                    geovalueklass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(geovalueklassname),
                                    geovaluehierarchy = geovalueklass.getHierarchy();

                                if (geovalueklassname == view.getObjectTypeName()) {
                                    //the geovalue is on a geoattribute defined in the same card class
                                    geoval.push(geovalue);
                                }
                                else if (Ext.Array.contains(geovaluehierarchy, view.getObjectTypeName())) {
                                    //the geovalue is on a geoattribute defined in a subClass of the card
                                    geoval.push(geovalue);
                                }

                            }, this);

                            var idObject = multiselection ? Ext.num(data.theObject.getId()) : null
                            view.ol_interaction_select_select(
                                data.olMap,
                                !Ext.isEmpty(geoval) ? geoval : [],
                                true, //silent
                                true,  //animate
                                idObject
                            );
                        }
                    }, Ext.emptyFn, Ext.emptyFn, this);
                } else {
                    view.ol_interaction_select_select(data.olMap, [], true);
                }
            }
        },

        updateTheThematism: {
            bind: {
                objectType: '{map.objectType}',
                objectTypeName: '{map.objectTypeName}',
                themathismId: '{map.thematismId}'
            },
            get: function (data) {
                if (data.objectType && data.objectTypeName) {
                    if (Ext.isEmpty(data.themathismId)) {
                        this.getView().setTheThematism(null);
                    } else {

                        //get the class instance
                        var theKlass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(data.objectTypeName);

                        //load the thematisms
                        theKlass.getThematisms().then(function (thematisms) {
                            if (this.getView() && !this.getView().destroyed) {

                                //find the target thematism
                                var theThematism = thematisms.getById(data.themathismId);

                                //sets the configuration in the view
                                theThematism.calculateResults(function () {
                                    this.getView().setTheThematism(theThematism)
                                }, this);
                            }
                        }, Ext.emptyFn, Ext.emptyFn, this);
                    }
                }
            }
        },

        updateDrawMode: {
            bind: {
                drawmode: '{map.drawmode}'
            },
            get: function (data) {
                if (Ext.isBoolean(data.drawmode)) {
                    this.getView().setDrawmode(data.drawmode);
                }
            }
        }
    },

    _last_load: null
});