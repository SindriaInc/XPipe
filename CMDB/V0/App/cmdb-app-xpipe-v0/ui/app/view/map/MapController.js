Ext.define('CMDBuildUI.view.map.MapController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.map-map',
    control: {
        '#': {
            beforerender: 'onBeforeRender',
            lablelvisibilitychange: 'onLabelVisibilityChange'
        }
    },

    /**
     *
     * @param {*} view
     * @param {*} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        vm.bind({
            olMap: '{map.olMap}',
            initialized: '{map.initialized}'
        }, function (data) {
            if (data.olMap && data.initialized) {

                view.addListener('mapcenterchange', this.mapCenterChange, this);
                view.getLayerStore().addListener('filterchange', this.onFilterChange, this);
                view.getLayerStore().addListener('update', this.onLayerStoreUpdate, this);
                view.addListener('resize', this.onResize, this);
                view.addListener('drawmodechange', this.onDrawmodeChange, this);
                view.addListener('refreshbtnclick', this.onRefreshBtn, this);

                view.setLoadedExtentsRtree(new ol.structs.RBush());

                view.setStart(true);
            }
        }, this);

        vm.bind({
            initialized: '{map.initialized}',
            advancedfilter: '{map.advancedfilter}'
        }, function (data) {
            if (data.initialized && data.advancedfilter) {
                data.advancedfilter.addListener('change', this.onAdvancedfilterChange, this, {
                    args: [this]
                });
            }
        }, this);

        vm.bind({
            objectType: '{map.objectType}',
            objectTypeName: '{map.objectTypeName}',
            layerStore: '{layerStore}',
            featuresStore: '{map.featureStore}'
        }, function (data) {
            if (data.objectTypeName && data.objectType && data.layerStore && data.featuresStore) {
                this.createOlMap();
            }
        }, this);

        vm.bind({
            olMap: '{map.olMap}',
            theThematism: '{map.theThematism}',
            objectTypeName: '{map.objectTypeName}',
            layerStore: '{layerStore}'
        }, function (data) {
            if (data.objectTypeName && data.layerStore && data.olMap) {
                var layer;
                var index;

                if (data.theThematism) {

                    index = data.layerStore.getSource().find('ollayername', data.theThematism.get('ollayername'));
                    layer = data.layerStore.getSource().getAt(index);

                    view.applyThematism(data.olMap, layer, data.theThematism);
                } else {

                    index = data.layerStore.getSource().find('owner_type', data.objectTypeName, 0);
                    while (index != -1) {
                        layer = data.layerStore.getSource().getAt(index);

                        if (layer.get('thematism')) {
                            view.applyThematism(data.olMap, layer, null);
                        }

                        index = data.layerStore.getSource().find('owner_type', data.objectTypeName, ++index);
                    }
                }
            }
        }, this);

        vm.bind({
            olMap: '{map.olMap}',
            layerStore: '{layerStore}',
            objectTypeName: '{map.objectTypeName}'
        }, function (data) {
            if (data.olMap && data.layerStore && data.objectTypeName) {
                var vm = this.getViewModel();
                var geoattributeindex = data.layerStore.getSource().findBy(function (geoattribute) {
                    return (geoattribute.get('owner_type') == data.objectTypeName && geoattribute.get('type') == CMDBuildUI.model.gis.GeoAttribute.type.geometry);
                });

                if (geoattributeindex == -1) {
                    geoattributeindex = data.layerStore.getSource().findBy(function (geoattribute) {
                        return geoattribute.get('type') == CMDBuildUI.model.gis.GeoAttribute.type.geometry;
                    }, this);
                }

                var geoattribute = data.layerStore.getSource().getAt(geoattributeindex);
                if (geoattribute) {

                    //this bind is needed beacuse the
                    vm.bind({
                        advancedFilter: '{map-container.advancedfilter}'
                    }, function (data) {
                        if (data.advancedFilter) {
                            var filter = null;
                            if (!Ext.isEmpty(data.advancedFilter)) {
                                // http://gitlab.tecnoteca.com/cmdbuild/cmdbuild/issues/5003
                                // add config term
                                filter = data.advancedFilter.encode();
                            }

                            vm.bind({
                                objectId: '{map.objectId}'
                            }, function (data) {
                                if (Ext.isEmpty(data.objectId) && CMDBuildUI.util.Navigation.getCurrentContext().objectType !== CMDBuildUI.util.helper.ModelHelper.objecttypes.navtreecontent) {

                                    Ext.Ajax.request({
                                        url: Ext.String.format('{0}/classes/_ANY/cards/_ANY/geovalues/center', CMDBuildUI.util.Config.baseUrl),
                                        method: 'GET',
                                        params: {
                                            attribute: geoattribute.getId(),
                                            filter: filter,
                                            forOwner: view.lookupViewModel().get("objectTypeName")
                                        },
                                        scope: this
                                    }).then(function (response) {
                                        if (view) {
                                            var responseText = JSON.parse(response.responseText);
                                            if (responseText && responseText.found) {
                                                var data = responseText.data;
                                                view.setMapCenter([data.x, data.y]);
                                            }
                                            view.setZoom(geoattribute.get("zoomDef"));
                                            view.setInitialized(true);
                                        }
                                    }, Ext.emptyFn, Ext.emptyFn, this);
                                } else {
                                    view.setZoom(geoattribute.get("zoomDef"));
                                    view.setInitialized(true);
                                }
                            }, this, {
                                single: true
                            });
                        }
                    }, this);
                } else {
                    view.setInitialized(true);
                }
            }
        }, this);
    },

    /**
     *
     * @param {CMDBuildUI.view.map.Map} view
     * @param {String} visibility
     */
    onLabelVisibilityChange: function (view, visibility) {
        this.delayloadfeatures(true);
    },

    /**
     * This function loads the features on the map
     */
    mapCenterChange: function (view, newValue, oldValue) {
        this.delayloadfeatures();
    },

    onDrawmodeChange: function (view, newValue, oldValue) {
        if (newValue == false) {
            this.delayloadfeatures(true);
        }
    },

    onAdvancedfilterChange: function (view, newValue) {
        this.delayloadfeatures(true);
    },


    onRefreshBtn: function (view) {
        var layerStore = view.getLayerStore();
        //not filtered records
        var dataSource = layerStore.getDataSource();

        //filtered record
        var olMap = view.getOlMap();

        dataSource.getRange().forEach(function (item, index, array) {
            if (item.get('type') == CMDBuildUI.model.gis.GeoAttribute.type.shape) {
                //add olLayer, doesn't add if already exist
                if (view.getOlLayer(olMap, item.get('ollayername'))) {
                    view.removeOlLayer(olMap, item.get('ollayername'))
                }
            }
        }, this);

        this.delayloadfeatures(true);
    },

    /**
     *
     * @param {*} geovalue
     */
    animateMap: function (geovalue) {
        var view = this.getView(),
            zoom,
            typeshapetiff = geovalue._type === CMDBuildUI.model.gis.GeoAttribute.subtype.shape || geovalue._type === CMDBuildUI.model.gis.GeoAttribute.subtype.geotiff,
            center = typeshapetiff ? [geovalue.x, geovalue.y] : geovalue.getCenter(),
            ollayername = typeshapetiff ? Ext.String.format('{0}_{1}_{2}', CMDBuildUI.model.gis.GeoAttribute.GEOATTRIBUTE, geovalue.name, geovalue._owner_type) : geovalue.get('ollayername');

        //gets't the zoom
        var layer = view.getLayerStore().find('ollayername', ollayername);
        if (layer == -1) {
            //if layer is filtered --> the olLayer is not visible

            var layerrecord = view.getLayerStore().getSource().findRecord('ollayername', ollayername);
            if (layerrecord) {
                zoom = layerrecord.get('zoomDef');
            }
        } else {
            //if layer is not filtered --> the layer is visible

            zoom = view.getZoom();
        }

        view.animatemap(
            view.getOlMap().getView(),
            center,
            zoom
        );
    },

    /**
     * This function loads the features on the map
     */
    delayloadfeatures: function (replace) {
        if (this.getView().getDrawmode() == false) {
            var t = this.getDelayedTask();
            var args;

            if (Ext.isEmpty(t.id)) {
                //there are not yet pending tasks
                args = [replace];
            } else {
                //there was a task before
                args = replace == true ? [replace] : undefined;
            }

            t.delay(this._delayvalue, this.loadfeatures, this, args);
        }
    },

    /**
     * This function loads the features on the map
     */
    loadfeatures: function (replace) {
        var view = this.getView();
        // replace = true;
        if (view) {
            var olMapView = view.getOlMap().getView(),
                extent = olMapView.calculateExtent(),
                geometryVisibleattributesId = [],
                geometryOwningattributesId = [],

                shapeattributesNames = [],
                geoserverEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.geoserverEnabled),

                objecttypenameklass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(view.getObjectTypeName()),
                objecttypenamehierarchy = objecttypenameklass.getHierarchy(),

                resolution = olMapView.getResolution(),
                projection = olMapView.getProjection(),
                vmGridContainer = view.getCardView().getViewModel(),
                importDWG = vmGridContainer.get("importDWG");

            if (!importDWG && !this.strategy(extent, resolution, projection, replace)) {
                return
            }
            extent = this.enlargeExtent(extent);

            //gets the geoattributes ids
            view.getLayerStore().getRange().forEach(function (geoattribute, index, array) {
                if (geoattribute.get('type') == CMDBuildUI.model.gis.GeoAttribute.type.geometry) {

                    var geoattributeklassname = geoattribute.get('owner_type');
                    var geoattributeklass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(geoattributeklassname);
                    var geoattributehierarchy = geoattributeklass.getHierarchy();

                    if (Ext.Array.contains(geoattributehierarchy, view.getObjectTypeName())) {
                        //the geoatttribute is defined in the same class or in a subClass of objecttypename
                        geometryOwningattributesId.push(geoattribute.getId());

                    } /*
                        commenting this block avoids appying the advanced filter on geoattributes defined in superClasses
                    else if (Ext.Array.contains(objecttypenamehierarchy, geoattributeklassname)) {
                        //the geoattribute is defined in a superClass of the objecttypename
                        geometryOwningattributesId.push(geoattribute.getId());

                    } */ else {

                        //attributes visible by the current objectTypeName
                        geometryVisibleattributesId.push(geoattribute.getId());
                    }
                } else if (geoattribute.get('type') == CMDBuildUI.model.gis.GeoAttribute.type.shape && geoserverEnabled) {
                    shapeattributesNames.push(geoattribute.getId());
                }
            }, this);

            var promises = [];
            //makes the appropriate calls for getting the geovalues
            if (!Ext.isEmpty(geometryVisibleattributesId) || !Ext.isEmpty(geometryOwningattributesId)) {

                //adds load mask
                var mask = CMDBuildUI.util.Utilities.addLoadMask(view);

                //get advanced filter and query filter
                var advancedfilter = view.getAdvancedfilter();

                if (advancedfilter && !advancedfilter.isEmpty()) {

                    //get geovalues appying the filter. Only for geoattributes wich are owned by the class on wich is applied the filter
                    if (!Ext.isEmpty(geometryOwningattributesId)) {
                        promises.push(this.loadFeaturesStore({
                            url: CMDBuildUI.util.api.Classes.getGeoValuesUrl('_ANY', '_ANY'),
                            params: {
                                attribute: geometryOwningattributesId,
                                area: extent.toString(),
                                limit: 0,
                                attach_nav_tree: view.getNavigationTree() ? true : false,
                                forOwner: view.lookupViewModel().get("objectTypeName")
                            }
                        }, advancedfilter));
                    }

                    //Doesn't need to apply the filter on those class
                    if (!Ext.isEmpty(geometryVisibleattributesId)) {
                        promises.push(this.loadFeaturesStore({
                            url: CMDBuildUI.util.api.Classes.getGeoValuesUrl('_ANY', '_ANY'),
                            params: {
                                attribute: geometryVisibleattributesId,
                                area: extent.toString(),
                                limit: 0,
                                attach_nav_tree: view.getNavigationTree() ? true : false
                            }
                        }));
                    }

                } else {
                    promises.push(this.loadFeaturesStore({
                        url: CMDBuildUI.util.api.Classes.getGeoValuesUrl('_ANY', '_ANY'),
                        params: {
                            attribute: Ext.Array.merge(geometryOwningattributesId, geometryVisibleattributesId),
                            area: extent.toString(),
                            limit: 0,
                            attach_nav_tree: view.getNavigationTree() ? true : false
                        }
                    }));
                }
            }

            var shapepromises = [];
            //makes the appropriate calls for getting the geolayers values
            if (!Ext.isEmpty(shapeattributesNames)) {
                shapepromises.push(this.loadShapeFeatureStore({
                    url: CMDBuildUI.util.api.Classes.getGeoLayersUrl('_ANY', '_ANY')
                }, new CMDBuildUI.util.AdvancedFilter({
                    attributes: {
                        'attribute_id': [{
                            attribute: 'attribute_id',
                            operator: 'IN',
                            value: Ext.Array.map(shapeattributesNames, function (item, index, array) {
                                return item;
                            }, this)
                        }]
                    }
                })));
            }

            if (importDWG) {
                replace = true;
                vmGridContainer.set("importDWG", false);
            }

            if (!Ext.isEmpty(promises)) {
                /**
                 * Manipulates the geovalues setting the right values for visibility, and bim Values.
                 */
                Ext.Deferred.all(promises).then(function (results) {
                    results = Ext.Array.merge.apply(this, results);

                    if (view) {
                        view.geovaluesVisibility(results);
                        view.geovaluesBim(results);

                        //adds the record in view memory store
                        view.getFeatureStore().loadRecords(results, {
                            addRecords: !replace
                        });

                        //adds the geovalues on the map
                        this.onFeaturesLoad.call(this, replace);


                    }

                    //removes load mask
                    CMDBuildUI.util.Utilities.removeLoadMask(mask);
                }, function () {
                    CMDBuildUI.util.Utilities.removeLoadMask(mask);
                }, Ext.emptyFn, this);
                view.getViewModel().set("settingsMap.geometryDelete", false);
            }

            if (!Ext.isEmpty(shapepromises)) {
                /**
                 *
                 */
                Ext.Deferred.all(shapepromises).then(function (results) {
                    results = Ext.Array.merge.apply(this, results);
                    if (view) {

                        view.geolayerVisibility(results);
                        view.getlayerLayerVisibility(results);

                        //adds the record in view memory store
                        view.getShapeFeatureStore().loadRecords(results, {
                            addRecords: !replace
                        });

                        //adds
                        this.onShapeFeaturesLoad.call(this, replace);
                    }
                }, Ext.emptyFn, Ext.emptyFn, this);
            }
        }
    },

    /**
     * Callback after loading the features
     */
    onFeaturesLoad: function (replace) {
        var view = this.getView();
        var olMap = view.getOlMap();

        if (view.getFeatureStore()) {
            if (!replace) {
                view.getFeatureStore().getGroups().getRange().forEach(function (group, index, array) {
                    var ollayername = group.getGroupKey();
                    view.addOlFeatures(olMap, ollayername, group.getRange(), replace);
                });
            } else {
                Ext.Array.forEach(view.getLayerStore().getRange(), function (item, index, array) {
                    if (item.get('type') == CMDBuildUI.model.gis.GeoAttribute.type.geometry) {
                        var ollayername = item.get('ollayername');
                        var groups = view.getFeatureStore().getGroups();
                        if (groups) {
                            var group = groups.findBy(function (item, index, array) {
                                return item.getGroupKey() == ollayername;
                            }, this, 0);

                            var groupRange;
                            if (group) {
                                groupRange = group.getRange()
                            } else {
                                groupRange = [];
                            }
                            view.addOlFeatures(olMap, ollayername, groupRange, replace);
                        } else {
                            view.addOlFeatures(olMap, ollayername, [], replace);
                        }
                    }
                }, this);
            }
        }
    },

    onShapeFeaturesLoad: function (replace) {
        var view = this.getView();
        var shapefeatures = view.getShapeFeatureStore();

        if (shapefeatures) {
            view.addOlGeoFeatures(view.getOlMap(), '_', shapefeatures.getRange(), replace);
        }
    },

    /**
     * This function is responsable for adding and removing olLayers
     * @param {*} geoattributes
     * @param {*} filter
     * @param {*} eOpts
     */
    onFilterChange: function (layerStore, filters, eOpts) {
        var view = this.getView();

        //not filtered records
        var dataSource = layerStore.getDataSource();

        //filtered records
        var data = layerStore.getData();
        var olMap = view.getOlMap();
        var hasRemoved = false;
        var hasAdded = false;

        dataSource.getRange().forEach(function (item, index, array) {

            if (data.isItemFiltered(item)) {
                //must remove olLayer. If already removed doesn't break
                view.removeOlLayer(olMap, item.get('ollayername'));
                hasRemoved = true;
            } else {
                if (item.get('type') == CMDBuildUI.model.gis.GeoAttribute.type.geometry) {
                    //add olLayer, doesn't add if already exist
                    if (!view.getOlLayer(olMap, item.get('ollayername'))) {
                        view.addOlLayer(olMap, item);
                        hasAdded = true;
                    }
                }
            }
        }, this);

        if (hasAdded) {
            this.delayloadfeatures.call(this, hasRemoved);
        }
    },

    /**
     *
     * @param {Ext.data.ChainedStore} store
     * @param {Ext.data.Model} record
     * @param {String} operation
     * @param {String[]} modifiedFieldNames
     * @param {object} details
     * @param {object} eOpts
     */
    onLayerStoreUpdate: function (store, record, operation, modifiedFieldNames, details, eOpts) {
        /**
         * handles the visibility of the
         */
        if (Ext.Array.contains(modifiedFieldNames, 'checked')) {
            var view = this.getView();
            var olMap = view.getOlMap();

            view.setVisibleOlLayer(olMap, record.get('ollayername'), record.get('checked'));
        }
    },

    onResize: function (extCmp, width, height) {
        var view = this.getView();
        var map = view.getOlMap();
        map.setSize([width, height]);
        this.delayloadfeatures();
    },
    _delayvalue: 100,
    getDelayedTask: function () {
        if (!this._delayedTask) {
            this._delayedTask = new Ext.util.DelayedTask();
        }

        return this._delayedTask;
    },

    /**
     * @param view
     * @param eOpts
     */
    createOlMap: function () {
        var view = this.getView();
        // sets and generates the html div id
        view.setHtml(
            Ext.String.format(
                '<div id="{0}" style="height: 100%;"></div>',
                view.getDivMapId()
            )
        );
        var zoom = view.getZoom() || this.getViewModel().get('actualZoom');
        var center = view.getMapCenter();

        var viewConfig = {
            projection: 'EPSG:3857',
            zoom: zoom,
            center: center,
            maxZoom: 25 > CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.maxZoom) ? CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.maxZoom) : 25,
            minZoom: 2 < CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.minZoom) ? CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.minZoom) : 2
        };

        // Configure map components
        var olView = new ol.View(viewConfig);
        olView.on('change:resolution', function (olEvent) {
            var newZoom = this.getZoom();
            view.setZoom(newZoom);
        });

        olView.on('change:center', function (olEvent) {
            var newCenter = this.getCenter();
            view.setMapCenter(newCenter);
        });

        var baseLayer = new ol.layer.Tile({
            source: new ol.source.OSM()
        });
        baseLayer.set('name', CMDBuildUI.model.gis.GeoAttribute.BASETILE);
        var controls = this.createMapControls();
        // add map to container
        // TODO: Resolve the probleme on the opening of map, if i resize the window (of chrome) all works otherwise is wrong
        var olMap = new ol.Map({
            controls: controls,
            target: view.getDivMapId(),
            view: olView,
            layers: [
                baseLayer
            ],
            overlays: [
                this.createOlPopup()
            ]
        });
        olMap.set('objecttype', view.getObjectType());
        olMap.set('objecttypename', view.getObjectTypeName());

        view.setOl_Interaction_select(olMap);

        view.setOlMap(olMap);
        this.addBimInteraction();
        this.addLongPressEvent();
    },

    /**
     * @returns {[ol.control.Control]}
     */
    createMapControls: function () {
        var me = this,
            view = this.getView();
        return [
            new ol.control.Zoom(),
            new ol.control.ScaleLine(),
            new ol.control.MousePosition({
                projection: 'EPSG:4326',
                coordinateFormat: function (coord) { //Template for the mousePostiton controller
                    var view = me.getView();
                    if (view) {

                        var olMap = view.getOlMap();
                        var olView = olMap.getView();
                        olView.set('coord', coord);

                        var template = Ext.String.format('{0}: {1} {2}: {3}',
                            CMDBuildUI.locales.Locales.gis.zoom,
                            view.getZoom(),
                            CMDBuildUI.locales.Locales.gis.position,
                            '{x} {y}'
                        );
                        return ol.coordinate.format(coord, template, 2);
                    }
                }
            }),
            new searchControl(),
            view.getMapLabelsControl(),
            view.modifySizeLabels(),
            view.getInfoControl()
        ];
    },

    createOlPopup: function () {
        var view = this.getView();

        var infowindow = document.createElement("div");
        infowindow.id = "cmdbuildui-olmap-infowindow";
        infowindow.className = "cmdbuildui-olmap-infowindow";

        var closer = document.createElement("a");
        closer.id = "cmdbuildui-olmap-infowindow-closer";
        closer.className = "cmdbuildui-olmap-infowindow-closer";
        closer.href = "#";

        var content = view.infoWindowContent = document.createElement("div");
        content.id = "cmdbuildui-olmap-infowindow-content";
        content.className = "cmdbuildui-olmap-infowindow-content";

        infowindow.append(closer, content);

        view.getEl().dom.append(infowindow);

        view.infoWindow = new ol.Overlay({
            element: infowindow,
            positioning: ol.OverlayPositioning.CENTER_CENTER,
            autoPanAnimation: {
                duration: 250
            }
        });


        closer.onclick = function () {
            view.infoWindow.setPosition(undefined);
            closer.blur();
            return false;
        };

        return view.infoWindow;
    },

    /**
     * This function sets hover and styling for the features with bim
     * Add's an event handler for the map pointer move
     */
    addBimInteraction: function () {
        // var bimEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.bim.enabled);
        // if (!bimEnabled) {
        //     return;
        // }

        this.addBimPopup();
        this.pointermoveMapAssign();

    },

    /**
     * Binds a function to pointermove event
     */
    pointermoveMapAssign: function () {
        var map = this.getView().getOlMap();
        map.on('pointermove', this.pointerMoveFunction, this);
    },

    // /**
    //  *
    //  */
    // pointermoveMapUnassign: function () {
    //     var map = this.getView().getOlMap();
    //     map.un('pointermove', this.pointerMoveFunction, this);
    // },
    /**
     * This is the function fired on 'pointermove' ol event
     * Is used to kwno when open the popup for the bim elements
     * @param event
     */
    pointerMoveFunction: function (event) {
        var me = this;
        var map = this.getView().getOlMap();
        var features = map.getFeaturesAtPixel(event.pixel);

        if (features) {
            var feature = features[0];
            var featureId = feature.getId();

            if (feature.get('hasBim') == true) { //HACK: enable popup only for point type

                if (featureId != this._popupBim.lastFeatureId && !me._popupBimEvents.popupHover) {
                    this._popupBim.lastFeatureId = featureId;
                    this._popupBim.lastProjectId = feature.get('projectId');
                    this._popupBimEvents.featureHover = true;

                    var position = feature.getGeometry().getCoordinates();
                    this._popupBim.overlay.setPosition(position);
                }
            }
        }
        else {
            this._popupBimEvents.featureHover = false;
        }

        if (this._popupBimEvents.featureHover == false && this._popupBimEvents.popupHover == false) {
            this._popupBim.overlay.setPosition(undefined);
            this._popupBim.lastFeatureId = null;
        }
    },

    /**
     * creates the DOM element,the ol.Overlay element and adds it to the map
     */
    addBimPopup: function () {
        var me = this;
        /**
         * Creates the DOM element
         */

        var extEl = new Ext.button.Button({
            text: CMDBuildUI.locales.Locales.bim.showBimCard,
            localized: {
                text: 'CMDBuildUI.locales.Locales.bim.showBimCard'
            },
            renderTo: Ext.getBody(),
            handler: function () {
                CMDBuildUI.util.bim.Util.openBimPopup(
                    CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.bim.viewer),
                    me._popupBim.lastProjectId,
                    null
                );
            }
        });

        var element = extEl.getEl().dom;

        /**
         * Add controls to the dom elemnt
         */

        element.onmouseenter = function (mouseEvt) {
            me._popupBimEvents.popupHover = true;
        };

        element.onmouseleave = function (mouseEvt) {
            me._popupBimEvents.popupHover = false;

            if (me._popupBimEvents.featureHover == false && me._popupBimEvents.popupHover == false) {
                me._popupBim.overlay.setPosition(undefined);
                me._popupBim.lastFeatureId = null;
            }
        };


        /**
         * creates ol.Overlay
         */
        this._popupBim = {
            overlay: new ol.Overlay({
                element: element,
                id: 'bimMapPopup',
                offset: [0, -25],
                stopEvent: false
            }),
            element: element
        };
        this._popupBim.overlay.setPosition(undefined);

        var map = this.getView().getOlMap(); // this can be passed as argument of the function avoiding calling the view
        map.addOverlay(this._popupBim.overlay);
    },


    /**
     * @param  {ol.Feature} feature the interested feature
     * @param  {Ext.data.Store} bimProjectStore the bim.Projects store. NOTE: is passed as argument to avoid calling Ext.getStore('bim.Projects') for each function call
     */
    setBimProperty: function (feature, bimProjectStore) {
        var me = this;
        var bimEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.bim.enabled);

        if (!bimEnabled) {
            return;
        }

        /**
         * this function sets some values in the feature if ther is a match in the bim.Projects store
         * @param _feature the feature being analized
         */
        setFeatureBim = function (_feature) {
            var _owner_id = _feature.get('data')._owner_id;
            var _owner_class = _feature.get('data')._owner_type;
            //vedere nei range dello store se trovo una corrispondenza tra owner id e owner type. se si assegno has bim alla feature
            var range = bimProjectStore.getRange();
            var bool = false;
            var i;

            for (i = 0; i < range.length && !bool; i++) {
                if (range[i].get('ownerClass') == _owner_class && range[i].get('ownerCard') == _owner_id) {
                    bool = true;
                }
            }


            if (bool) { //HACK: chose here the extra bim values to add to the feature
                i--; //must do, set the i to the correct value. Because the for loop increases import {  } from "module";

                var data = _feature.get('data');
                data.hasBim = true;
                data.bimActive = range[i].get('active');
                data.projectId = range[i].get('projectId');

                //me.setBimStyle(_feature);
            }

        };

        if (bimProjectStore.isLoaded()) { //handles the case in wich the store is already loaded
            setFeatureBim(feature);
        } else {    //handle the case in wich the store is not yet loaded and used the callback function
            bimProjectStore.load({
                callback: setFeatureBim(feature),
                scope: this
            });
        }
    },

    addLongPressEvent: function () {
        var view = this.getView(),
            olMap = view.getOlMap(),
            targetElement = Ext.get(olMap.getTargetElement());

        this.mon(targetElement, 'longpress', function (event, node, options, eOpts) {
            if (!view.getMapContainerView().getMapTabPanel().getDrawmode()) {
                var idElements = [];

                //https://openlayers.org/en/v4.6.5/apidoc/ol.Map.html#forEachFeatureAtPixel
                olMap.forEachFeatureAtPixel([event.parentEvent.event.layerX, event.parentEvent.event.layerY],
                    function (feature, layer) {
                        idElements.push(feature.get('_owner_id'));
                    });

                CMDBuildUI.util.Utilities.openPopup(
                    CMDBuildUI.view.map.longpress.Grid.longpressPopupId,
                    CMDBuildUI.locales.Locales.gis.longpresstitle,
                    {
                        xtype: 'map-longpress-grid',
                        viewModel: {
                            data: {
                                idElements: idElements
                            }
                        }
                    },
                    null,
                    {
                        width: '45%',
                        height: '45%'
                    });
            }
        });
    },

    privates: {

        /**
         * {object} object containing the ol.Overlay and the htmlElement used fo it
         */
        _popupBim: {
            /**
             * saves the information about the last feature wich opened the popup
             */
            lastFeatureId: null,
            /**
             * Contains the html element used as popup
             */
            element: null,
            /**
             * contains the ol.Overlay object
             */
            overlay: null,
            /**
             * The id of the last project clicked
             */
            lastProjectId: null
        },

        _popupBimEvents: {
            popupHover: null,
            featureHover: null
        },

        loadFeaturesStore: function (config, advancedfilter) {
            var deferred = new Ext.Deferred();

            // create temp store
            var childrenstore = Ext.create("Ext.data.Store", {
                proxy: {
                    type: 'baseproxy',
                    model: 'CMDBuildUI.model.gis.GeoValue'
                    // url: config.url,
                    // extraParams: config.params,
                },
                pageSize: 0
            });

            if (advancedfilter) {
                childrenstore.setAdvancedFilter(advancedfilter);
            }

            var view = this.getView();
            var attach_nav_tree_collection = view.getAttach_nav_tree_collection();
            var navigationTree = view.getNavigationTree();

            childrenstore.load(Ext.applyIf(config, {
                callback: function (records, operation, success) {
                    if (success) {
                        if (operation.getParams()[CMDBuildUI.model.gis.GeoValueTree.attach_nav_tree]) {
                            var attach_nav_items = operation.getResultSet().metadata[CMDBuildUI.model.gis.GeoValueTree.nav_tree_items];
                            var newItems = [];
                            if (attach_nav_items.length) {
                                Ext.Array.forEach(attach_nav_items, function (attach_nav_item, index, array) {
                                    attach_nav_item.description = Ext.String.htmlEncode(attach_nav_item.description);
                                    var composedId = Ext.String.format('{0}-{1}', attach_nav_item._id, attach_nav_item.navTreeNodeId);

                                    if (!attach_nav_tree_collection.getByKey(composedId)) {
                                        attach_nav_item._id_composed = composedId;
                                        var newItem = Ext.create('CMDBuildUI.model.gis.GeoValueTree',
                                            Ext.applyIf(attach_nav_item, {
                                                text: attach_nav_item.description,
                                                _objectid: attach_nav_item._id,
                                                _objecttypename: attach_nav_item.type,
                                                // _navtreedef: navtreedef,
                                                leaf: true,
                                                checked: true
                                            })
                                        );

                                        newItem.setNavTreeNode(navigationTree.getNodeRecursive(attach_nav_item.navTreeNodeId));
                                        newItems.push(newItem);
                                    }
                                }, this);

                                attach_nav_tree_collection.add(newItems);
                            }
                        }
                        deferred.resolve(records);
                    } else {
                        deferred.reject();
                    }

                    Ext.asap(function () {
                        childrenstore.destroy();
                    });
                }
            }));
            return deferred.promise;
        },

        loadShapeFeatureStore: function (config, advancedfilter) {
            var deferred = new Ext.Deferred();

            // create temp store
            var childrenstore = Ext.create("Ext.data.Store", {
                proxy: {
                    type: 'baseproxy',
                    model: 'CMDBuildUI.model.gis.GeoLayer'
                    // url: config.url,
                    // extraParams: config.params,
                },
                pageSize: 0
            });

            if (advancedfilter) {
                childrenstore.setAdvancedFilter(advancedfilter);
            }

            childrenstore.load(Ext.applyIf(config, {
                callback: function (records, operation, success) {
                    if (success) {
                        deferred.resolve(records);
                    }
                }
            }));
            return deferred.promise;
        }
    },

    /**
     * https://github.com/openlayers/openlayers/blob/v4.6.5/src/ol/source/vector.js#L742
     * @param {*} extent
     * @param {*} resolution
     * @param {*} projection
     * @param {*} replace
     */
    strategy: function (extent, resolution, projection, replace) {
        var loadedExtentsRtree = this.getView().getLoadedExtentsRtree();

        if (replace) {
            loadedExtentsRtree.clear();
        }

        var extentsToLoad = [extent];
        var extentEnlarged = this.enlargeExtent(extent);

        var i, ii;
        var toload;

        for (i = 0, ii = extentsToLoad.length; i < ii; ++i) {
            var extentToLoad = extentsToLoad[i];
            var alreadyLoaded = loadedExtentsRtree.forEachInExtent(extentToLoad,
                /**
                 * @param {{extent: ol.Extent}} object Object.
                 * @return {boolean} Contains.
                 */
                function (object) {
                    return ol.extent.containsExtent(object.extent, extentToLoad);
                });
            if (!alreadyLoaded) {
                loadedExtentsRtree.insert(extentEnlarged, { extent: extentEnlarged.slice() });
                toload = true;
            } else {
                toload = false;
            }
        }

        return toload
    },

    enlargeExtent: function (extent) {
        //[minx, miny, maxx, maxy]
        var minx = extent[0];
        var miny = extent[1];
        var maxx = extent[2];
        var maxy = extent[3];

        var xlength = maxx - minx;
        var ylength = maxy - miny;
        return [
            extent[0] - (xlength * 0.5),
            extent[1] - (ylength * 0.5),
            extent[2] + (xlength * 0.5),
            extent[3] + (ylength * 0.5)
        ];
    }
});
