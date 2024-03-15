Ext.define('CMDBuildUI.view.map.Mixin', {
    mixinId: 'map-mixing',

    /**
     * @param {} olMap
     * @param {String} ollayername
     * @returns {ol.layer.Base} the layer with the passed name. undefined if not foud
     */
    getOlLayer: function (olMap, ollayername) {
        var olLayersCollection = olMap.getLayers();

        return Ext.Array.findBy(olLayersCollection.getArray(), function (item, index, array) {
            return item.get('name') == ollayername;
        });
    },

    /**
     *
     * @param {*} olMap
     * @param {*} ollayername
     */
    getOlLayers: function (olMap, ollayername) {
        var olLayersCollection = olMap.getLayers();

        var results = [];
        Ext.Array.forEach(olLayersCollection.getArray(), function (item, index, array) {
            if (item.get('name') == ollayername) {
                results.push(item);
            }
        }, this);

        return results;
    },

    /**
     *
     * @param {*} olMap
     * @param {*} ollayername
     * @param {*} Id
     */
    getOlGeoFeatureById: function (olMap, ollayername, Id) {
        var olLayersCollection = olMap.getLayers();

        return Ext.Array.findBy(olLayersCollection.getArray(), function (item, index, array) {
            return item.get('id') == Id;
        }, this);
    },

    /**
     *
     * @param {Function} callback
     * @param {Object} scope
     */
    forEachGeometryLayer: function (callback, scope) {
        var map = this.getOlMap();
        map.getLayers().forEach(function (item, index, array) {
            if (item.get('type') == CMDBuildUI.model.gis.GeoAttribute.type.geometry) {
                callback.call(scope, item, index, array);
            }
        });
    },

    /**
     *
     * @param {*} callback
     * @param {*} scope
     */
    forEachShapeLayer: function (callback, scope) {
        var map = this.getOlMap();
        map.getLayers().forEach(function (item, index, array) {
            if (item.get('type') == CMDBuildUI.model.gis.GeoAttribute.type.shape) {
                callback.call(scope, item, index, array);
            }
        });
    },

    /**
     *
     * @param {*} callback
     * @param {*} scope
     */
    forEachGeoTiffLayer: function (callback, scope) {
        var map = this.getOlMap();
        map.getLayers().forEach(function (item, index, array) {
            if (item.get('type') == CMDBuildUI.model.gis.GeoAttribute.type.geotiff) {
                callback.call(scope, item, index, array);
            }
        });
    },

    /**
     *
     * @param {CMDBuildUI.model.gis.GeoAttribute} geoattribute
     */
    addOlLayer: function (olMap, geoattribute) {
        var olLayer = geoattribute.createOlLayer();

        if (olLayer.get('owner_type') == olMap.get('objecttypename')) {
            olLayer.on('change:visible', function (Event) {
                this.ol_interaction_select_refresh(olMap);
            }, this);
        }

        //add the layer
        this.getOlMap().addLayer(olLayer);
        this.clearExtent();

        //adds the thematism if selected
        if (this.getTheThematism() && geoattribute.get('ollayername') == this.getTheThematism().get('ollayername')) {
            this.applyThematism(olMap, geoattribute, this.getTheThematism());
        }
    },

    clearExtent: function () {
        var loadedExtentsRtree = this.getLoadedExtentsRtree();
        loadedExtentsRtree.clear();
    },

    /**
     *
     * @param {ol.layer.Vector || ol.layer.Tile} olLayer
     */
    removeOlLayer: function (olMap, ollayername) {
        var olLayers = this.getOlLayers(olMap, ollayername);

        Ext.Array.forEach(olLayers, function (item, index, array) {
            olMap.removeLayer(item);
        }, this);

    },

    /**
     *
     */
    setVisibleOlLayer: function (olMap, ollayername, visible) {
        var olLayers = this.getOlLayers(olMap, ollayername);

        Ext.Array.forEach(olLayers, function (item, index, array) {
            item.setCheckedLayer(visible);
        }, this);
    },

    /**
     *
     * @param {*} olMap
     * @param {String} ollayername
     * @param {[CMDBuildUI.model.gis.GeoValue]} geovalues
     * @param {Boolean} replace If true clears the layer before adding the features
     */
    addOlFeatures: function (olMap, ollayername, geovalues, replace) {
        var olFeatures = [],
            me = this,
            labelvisibility = me.labelsVisibility;

        geovalues.forEach(function (geovalue, index, array) {
            var showLabel = false;
            if (labelvisibility === 'all' || (labelvisibility === 'allclass' && geovalue.get("_owner_type") === me.objectTypeName)) {
                showLabel = true
            }
            olFeatures.push(geovalue.getOlFeature({
                showLabel: showLabel,
                labelSize: me.labelSize
            }));
        });

        var olLayer = this.getOlLayer(olMap, ollayername);
        if (olLayer) {

            if (replace) {
                olLayer.getSource().clear();
            }

            olLayer.getSource().addFeatures(olFeatures);

            if (replace || (olLayer.get('owner_type') == olMap.get('objecttypename'))) {
                this.ol_interaction_select_refresh(olMap);
            }
        }
    },

    /**
     *
     * @param {*} olMap
     * @param {*} ollayername
     * @param {*} geolayers
     * @param {*} replace
     */
    addOlGeoFeatures: function (olMap, ollayername, geolayers, replace) {
        Ext.Array.forEach(geolayers, function (geolayer, array, index) {

            var foundOlGeoFeature = Ext.Array.findBy(olMap.getLayers().getArray(), function (olLayers, array, index) {
                return olLayers.get('id') == geolayer.getId();
            }, this);

            if (replace) {
                olMap.removeLayer(foundOlGeoFeature);
                foundOlGeoFeature = null;
            }

            if (!foundOlGeoFeature) {
                var geoFeature = geolayer.getOlGeoFeature();
                olMap.addLayer(geoFeature);
            }
        }, this);
    },

    /**
     *
     * @param {*} olMap
     * @param {String} ollayername
     * @param {String} geovalue
     * @param {String} type drawmode || checked
     * @param {Boolean} value
     */
    setVisibleOlFeature: function (olMap, ollayername, geovalue, type, value) {
        var olLayer = this.getOlLayer(olMap, ollayername);

        if (olLayer) {
            var source = olLayer.getSource(),
                olFeature = source.getFeatureById(geovalue.getId());

            if (!olFeature) {
                olFeature = Ext.Array.findBy(source.getFeatures(), function (item, index) {
                    return item.get("_owner_id") == geovalue.get("_owner_id");
                });
            }

            if (olFeature) {
                switch (type) {
                    case 'checked':
                        olFeature.setChecked(value);
                        break;
                    case 'drawmode':
                        olFeature.setDrawmode(value);
                        break;
                }
                this.ol_interaction_select_refresh(olMap);
            }
        }
    },

    /**
     *
     * @param {*} olMap
     * @param {*} ollayername
     * @param {CMDBuildUI.model.gis.GeoLayer} geovalue
     * @param {*} type
     * @param {*} value
     */
    setVisibleOlGeoFeature: function (olMap, ollayername, geovalue, type, value) {
        var olGeoFeature = this.getOlGeoFeatureById(olMap, ollayername, geovalue.getId());

        if (olGeoFeature) {

            switch (type) {
                case CMDBuildUI.model.gis.GeoLayer.checked:
                    olGeoFeature.setChecked(value);
                    break;
            }
        }
    },

    /**
     *
     * @param {*} olMap
     * @param {CMDBuildUI.model.thematisms.Thematism} thematism
     */
    applyThematism: function (olMap, geoattribute, thematism) { //move in model;
        if (thematism) {
            geoattribute.set('thematism', thematism);
        } else {
            geoattribute.set('thematism', undefined);
        }

        var ollayername = geoattribute.get('ollayername');
        var olLayer = this.getOlLayer(olMap, ollayername);
        if (olLayer) {
            this.ol_interaction_select_refresh(olMap);
            olLayer.changed();
        }
    },

    /**
     *
     * @param {CMDBuildUI.model.gis.GeoValue} config.record
     */
    modify: function (config) {
        config = Ext.merge({
            record: null,
            listeners: {
                modifystart: {
                    fn: Ext.emptyFn,
                    scope: this
                },
                modifyend: {
                    fn: Ext.emptyFn,
                    scope: this
                }
            }
        }, config);

        //compose the layer name
        var drawModifyLayerName = Ext.String.format('{0}_{1}_{2}', CMDBuildUI.map.util.Util.DRAWMODIFYLAYERNAME, config.record.get('_attr'), config.record.get('_owner_type'));

        //gets the draw modify layer
        var drawModifyLayer = this.getDrawModifyLayer(drawModifyLayerName);
        if (!drawModifyLayer) {
            drawModifyLayer = this.createDrawModifyLayer(drawModifyLayerName, config.record);
        }

        //hide the feature in the layer
        var olMap = this.getOlMap();
        var ollayername = config.record.get('ollayername');
        this.setVisibleOlFeature(olMap, ollayername, config.record, 'drawmode', true);

        //compose the interaction name
        var modifyInteractionName = Ext.String.format('{0}_{1}_{2}', CMDBuildUI.map.util.Util.MODIFYINTERACTIONNAME, config.record.get('_attr'), config.record.get('_owner_type'));

        var modifyInteraction = this.getModifyInteraction(modifyInteractionName);
        if (!modifyInteraction) {
            modifyInteraction = this.createModifyInteraction(modifyInteractionName, {
                source: drawModifyLayer.getSource()
            });
        }

        //modify start listener
        modifyInteraction.once('modifystart', function () {
            config.listeners.modifystart.fn.call(config.listeners.modifystart.scope);
        });

        //modifyend listener
        modifyInteraction.on('modifyend', function (event) {
            // event.feature, event.mapBrowserEvent, event.target, event.type

            var points = CMDBuildUI.map.util.Util.olCoordinatesToObject(event.features.item(0).getGeometry().getType(), event.features.item(0).getGeometry().getCoordinates());

            config.listeners.modifyend.fn.call(config.listeners.modifyend.scope, points);
        });
    },

    /**
     *
     * @param {*} config
     */
    draw: function (config) {
        config = Ext.merge({
            record: null,
            listeners: {
                drawend: {
                    fn: Ext.emptyFn,
                    scope: this
                },
                drawstart: {
                    fn: Ext.emptyFn,
                    scope: this
                }
            }
        }, config);

        //compose the layer name
        var drawModifyLayerName = Ext.String.format('{0}_{1}_{2}', CMDBuildUI.map.util.Util.DRAWMODIFYLAYERNAME, config.record.get('_attr'), config.record.get('_owner_type'));

        //gets the draw modify layer
        var drawModifyLayer = this.getDrawModifyLayer(drawModifyLayerName);
        if (!drawModifyLayer) {
            drawModifyLayer = this.createDrawModifyLayer(drawModifyLayerName, config.record);
        }

        //draw interactoin name
        var drawInteractionName = Ext.String.format('{0}_{1}_{2}', CMDBuildUI.map.util.Util.DRAWINTERACTIONNAME, config.record.get('_attr'), config.record.get('_owner_type'));

        var drawInteraction = this.getDrawInteraction(drawInteractionName);
        if (!drawInteraction) {
            drawInteraction = this.createDrawInteraction(drawInteractionName, {
                source: drawModifyLayer.getSource(),
                type: this.format(config.record.get('_type'))
            });
        }

        drawInteraction.setActive(true);
        //drawstart listener
        drawInteraction.once('drawstar', function (event) {
            config.listeners.drawstart.fn.call(config.listeners.drawstart.scope);
        });

        //drawend listener
        drawInteraction.once('drawend', function (event) {
            drawInteraction.setActive(false);
            var points = CMDBuildUI.map.util.Util.olCoordinatesToObject(event.feature.getGeometry().getType(), event.feature.getGeometry().getCoordinates());

            config.listeners.drawend.fn.call(config.listeners.drawstart.scope, points);
        });
    },

    /**
     *
     * @param {Object} config
     */
    clear: function (config) {
        Ext.merge({
            record: null,
            listeners: {
                clear: {
                    fn: Ext.emptyFn,
                    scope: this
                }
            }
        }, config);

        //compose the layer name
        var drawModifyLayerName = Ext.String.format('{0}_{1}_{2}', CMDBuildUI.map.util.Util.DRAWMODIFYLAYERNAME, config.record.get('_attr'), config.record.get('_owner_type'));

        //gets the draw modify layer
        var drawModifyLayer = this.getDrawModifyLayer(drawModifyLayerName);
        if (!drawModifyLayer) {
            drawModifyLayer = this.createDrawModifyLayer(drawModifyLayerName, config.record);
        }

        //hide the feature in the layer
        var olMap = this.getOlMap();
        var ollayername = config.record.get('ollayername');
        this.setVisibleOlFeature(olMap, ollayername, config.record, 'drawmode', true);

        drawModifyLayer.getSource().once('clear', function () {
            config.listeners.clear.fn.call(config.listeners.clear.scope);
        });

        drawModifyLayer.getSource().clear();
    },

    /**
     *
     */
    clean: function (record) {

        //compose the interaction name
        var modifyInteractionName = Ext.String.format('{0}_{1}_{2}', CMDBuildUI.map.util.Util.MODIFYINTERACTIONNAME, record.get('_attr'), record.get('_owner_type'));

        this.removeModifyinteraction(modifyInteractionName);

        //compose the draw interactoin name
        var drawInteractionName = Ext.String.format('{0}_{1}_{2}', CMDBuildUI.map.util.Util.DRAWINTERACTIONNAME, record.get('_attr'), record.get('_owner_type'));

        this.removeDrawInteraction(drawInteractionName);

        //compose the layer name
        var drawModifyLayerName = Ext.String.format('{0}_{1}_{2}', CMDBuildUI.map.util.Util.DRAWMODIFYLAYERNAME, record.get('_attr'), record.get('_owner_type'));

        this.removeDrawModifyLayer(drawModifyLayerName);

        //hide the feature in the layer
        var olMap = this.getOlMap();
        var ollayername = record.get('ollayername');
        this.setVisibleOlFeature(olMap, ollayername, record, 'drawmode', false);

    },

    hideshow: function (config) {
        Ext.merge({
            record: null,
            show: null,
            listeners: {
                clear: {
                    fn: Ext.emptyFn,
                    scope: this
                },
                set: {
                    fn: Ext.emptyFn,
                    scope: this
                }
            }
        }, config);

        //hide the feature in the layer
        var olMap = this.getOlMap(),
            ollayername = config.record.get('ollayername'),
            olGeoFeature = this.getOlGeoFeatureById(olMap, ollayername, config.record.getId());

        if (!olGeoFeature) {
            olGeoFeature = Ext.Array.findBy(olMap.getLayers().getArray(), function (item, index, array) {
                if (item.type === "TILE") {
                    return item.getSource().key_.indexOf(config.record.get("_owner_id").toString()) !== -1;
                }
            }, this);
        }

        olGeoFeature.setChecked(config.show);
        !config.show ? config.listeners.clear.fn.call(config.listeners.clear.scope) : config.listeners.set.fn.call(config.listeners.set.scope);
    },

    /**
     *
     * @param {ol.Map} olMap
     * @param {Array} center
     * @param {Number} zoom
     */
    animatemap: function (olMap, center, zoom) {
        center = Ext.Array.equals(center, olMap.getCenter()) ? [center[0] + (olMap.getMaxZoom() / olMap.getZoom()), center[1]] : center;
        olMap.animate({
            center: center,
            zoom: zoom,
            duration: 500
        });
    },

    /**
     *
     * @param {String} name the name for the layer
     * @returns {ol.layer.Vector}
     */
    getDrawModifyLayer: function (name) {
        //get the draw layer
        var drawModifyLayer = Ext.Array.findBy(this.getOlMap().getLayers().getArray(), function (layer, index, array) {
            if (layer.get('name') == name) {
                return true;
            }
        });

        return drawModifyLayer;
    },

    /**
     *
     * @param {String} name the name for the layer
     * @param {CMDBuildUI.model.gis.GeoValue} geoValue
     * @returns {ol.layer.Vector}
     */
    createDrawModifyLayer: function (name, geoValue) {
        var drawSource = new ol.source.Vector();

        if (geoValue.hasValues()) {
            drawSource.addFeature(geoValue.getOlFeature());
        }

        drawModifyLayer = new ol.layer.Vector({
            source: drawSource,
            style: new ol.style.Style({
                image: new ol.style.Circle({
                    fill: new ol.style.Fill({
                        color: 'rgba(255,165,0,1)' //'orange',//[0, 255, 0, 0.5],
                    }),
                    stroke: new ol.style.Stroke({
                        width: 1,
                        color: 'white'
                    }),
                    radius: 10

                }),
                fill: new ol.style.Fill({
                    color: 'rgba(255,165,0,0.5)' //[255, 165, 0, 0.7], //'orange'
                }),
                stroke: new ol.style.Stroke({
                    color: 'white',
                    width: 2
                })
            }),
            zIndex: 5555
        });
        drawModifyLayer.set('name', name);
        this.getOlMap().addLayer(drawModifyLayer);

        return drawModifyLayer;
    },

    /**
     *
     * @param {String} name
     */
    removeDrawModifyLayer: function (name) {
        var drawModifyLayer = this.getDrawModifyLayer(name);
        if (drawModifyLayer) {
            this.getOlMap().removeLayer(drawModifyLayer);
        }
    },

    /**
     *
     * @param {String} name  The name of the draw interaction
     * @returns {ol.interaction.Draw}
     */
    getDrawInteraction: function (name) {
        //get the draw interaction
        var drawInteraction = Ext.Array.findBy(this.getOlMap().getInteractions().getArray(), function (interaction, index, array) {
            if (interaction.get("name") == name) {
                return true;
            }
        });

        return drawInteraction;
    },

    /**
     * @param {String} name
     * @param {Object} config
     * @returns {ol.interaction.Draw}
     */
    createDrawInteraction: function (name, config) {

        //creates the interaction object
        drawInteraction = new ol.interaction.Draw({
            source: config.source,
            type: config.type // style: style //HACK: change drawInteraction style
        });
        drawInteraction.set('name', name);

        //adds the interaction to the map
        this.getOlMap().addInteraction(drawInteraction);

        return drawInteraction;
    },

    /**
     *
     * @param {String} name
     */
    removeDrawInteraction: function (name) {
        var drawInteraction = this.getDrawInteraction(name);
        if (drawInteraction) {
            this.getOlMap().removeInteraction(drawInteraction);
        }
    },

    /**
     *
     * @param {String} name  The name of the modify interaction
     * @returns {ol.interaction.Modify}
     */
    getModifyInteraction: function (name) {

        var modifyInteraction = Ext.Array.findBy(this.getOlMap().getInteractions().getArray(), function (interaction, index, array) {
            if (interaction.get("name") == name) {
                return true;
            }
        });

        return modifyInteraction;
    },

    /**
     *
     * @param {*} name
     * @param {*} config
     * @returns {ol.interaction.Modify}
     */
    createModifyInteraction: function (name, config) {
        //creates the interaction object
        modifyInteraction = new ol.interaction.Modify({
            source: config.source // style: olTmpStyle
        });
        modifyInteraction.set('name', name);

        //adds the interaction to the map
        this.getOlMap().addInteraction(modifyInteraction);

        return modifyInteraction;
    },

    /**
     *
     * @param {String} name
     */
    removeModifyinteraction: function (name) {
        var modifyInteraction = this.getModifyInteraction(name);
        if (modifyInteraction) {
            this.getOlMap().removeInteraction(modifyInteraction);
        }
    },

    /*
     * this function modifyes a string mantaining the same words but setting uppercase the first latter and lowercase the remaining
     * @param {String} inp the input string
     * @return {String} the modified string
     */
    format: function (type) {
        type = type.toLowerCase();

        switch (type) {
            case 'point':
                return 'Point';
            case 'linestring':
                return 'LineString';
            case 'polygon':
                return 'Polygon';
        }
    },

    getOl_interaction_select: function (olMap) {
        var collection = olMap.getInteractions();
        for (var i = 0; i < collection.getLength(); i++) {
            var item = collection.item(i);
            if (item instanceof ol.interaction.Select) {
                return item;
            }
        }
    },

    setOl_Interaction_select: function (olMap) {
        var me = this,
            vm = this.getViewModel(),
            olMapView = olMap.getView(),
            mapContainerView = me.getMapContainerView(),
            oldInteration = me.getOl_interaction_select(olMap),
            objecttypename = olMap.get('objecttypename'),
            layerStore = me.getLayerStore(),

            listTab = mapContainerView.getListTab(),
            listStore = listTab.getStore(),
            selectionModelList = listTab.getSelectionModel(),

            navTreeTab = mapContainerView.getNavigationTreeTab(),
            navTreeStore = navTreeTab.getStore(),
            selectionModelNavTree = navTreeTab.getSelectionModel(),

            cardGrid = me.getCardGridView(),
            cardGridStore = cardGrid.getStore(),
            selectionModelCardGrid = cardGrid.getSelectionModel();

        if (oldInteration) {
            olMap.removeInteraction(oldInteration);
        }

        var interaction = new ol.interaction.Select({
            multi: false,
            hitTolerance: 2,
            condition: function (Event) {
                if (Event.type == ol.MapBrowserEventType.SINGLECLICK) {
                    return me.getDrawmode() == false;
                }
            },
            toggleCondition: function (Event) {
                if (Event.type == ol.MapBrowserEventType.SINGLECLICK && vm.get("contextmenu.multiselection.enabled")) {
                    return me.getDrawmode() == false;
                }
            },
            layers: function (layer) {
                var owner_type = layer.get('owner_type');
                if (owner_type) {
                    var layerklass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(owner_type),
                        layerhierarchy = layerklass.getHierarchy();

                    if (Ext.Array.contains(layerhierarchy, objecttypename)) {
                        //the layer is defined in the same class or in a subClass of objecttypename
                        return true;
                    }
                }
                return false;
            },
            style: function (olFeature, number) {
                var visible = this.isVisibleOlFeature(olFeature),
                    labelsVisibility = me.labelsVisibility,
                    deselected = olFeature.get("_deselected");

                olFeature = olFeature.get('_geovalue').getOlFeature({
                    showLabel: labelsVisibility === 'selected' || labelsVisibility === 'allclass' || labelsVisibility === 'all',
                    labelSize: me.labelSize
                });

                if (!visible && !vm.get("contextmenu.multiselection.enabled")) {
                    if (me.infoWindow._current == olFeature.getId() || me.infoWindow._ownerId != olFeature.get("_owner_id")) {
                        me.removeInfoWindow();
                    }
                    var featurenotfound = CMDBuildUI.model.gis.GeoAttribute.getOlLayerVectorStyleRemoved(),
                        removeFeature = Ext.Array.findBy(interaction.getFeatures().getArray(), function (item, index) {
                            return olFeature.getId() == item.getId()
                        });

                    interaction.getFeatures().remove(removeFeature);
                    return [featurenotfound];

                } else if (visible && layerStore) {

                    var ollayername = olFeature.get('ollayername'),
                        index = layerStore.find('ollayername', ollayername),
                        item = layerStore.getAt(index),
                        highlightselected = item.get('thematism') ? this.getHighlightselected() : true,
                        basestyle = item.getOlLayerVectorStyle(olFeature);

                    if (highlightselected && !deselected) {
                        //if thematism is applied and highlightselected
                        var selectionstyle = item.getOlLayerVectorStyleSelected();
                        //return basestyle.push(selectionstyle);
                        return Ext.Array.merge(basestyle, selectionstyle);
                    } else {
                        var dark = deselected ? 1 : 0.5;
                        if (deselected) {
                            olFeature.setProperties({ "_deselected": true });
                        }
                        //if thematism is not applied or the selected item must not be highlited
                        basestyle[0].fill_ ? basestyle[0].fill_.color_[3] = dark : null;
                        return basestyle;
                    }

                }
            }.bind(this)
        });

        interaction.on('select', function (event) {

            var mapEvent = event.mapBrowserEvent,
                selected = event.selected,
                deselected = event.deselected,
                multiselection = vm.get("contextmenu.multiselection.enabled"),
                selectAll = vm.get("settingsMap.selectAll") && mapEvent && mapEvent.coordinate,
                features = interaction.getFeatures(),
                featuresArray = features.getArray();

            if (multiselection && !(mapEvent && mapEvent.selectAll)) {

                // Used to clear selections on map when activate multiselection
                if ((Ext.isEmpty(selected) && Ext.isEmpty(deselected) && (!mapEvent || mapEvent.deselectAll))) {
                    features.clear();
                    me.setObjectId(null);
                    //Used when deselect all items to do a little moviment on the map to view the changes
                    if (mapEvent && mapEvent.deselectAll) {
                        olMapView.animate({
                            rotation: -0.017,
                            duration: 100
                        });
                    }
                    return;
                }

                // Used to remove feature just selected because it is treated later
                if ((mapEvent && !mapEvent.featuresMap && !mapEvent.select_refresh) || selectAll) {
                    try {
                        features.remove(selected[0]);
                    } catch (e) {
                        features.remove(selected[0]);
                    }
                    if (selectAll) {
                        return;
                    }
                }

            } else {
                features.clear();
                if (selected.length == 0 && deselected.length > 0) {
                    selectionModelList.deselectAll();
                    selectionModelNavTree.deselectAll();
                    selectionModelCardGrid.deselectAll();
                }
            }

            if (selected.length != 0 && !mapEvent.select_refresh) {
                var exit = false;
                Ext.Array.each(selected, function (olFeature, index, allitems) {
                    var owner_id = olFeature.get("_owner_id");
                    if (multiselection) {
                        var sameObjectId = me.getObjectId() == owner_id ? true : false;

                        // Used when select all items on grid
                        if (mapEvent.selectAll) {
                            olFeature.setProperties({ "_deselected": false });
                            features.push(olFeature);

                        } else if ((mapEvent && mapEvent.featuresMap) || sameObjectId) {
                            var recordList = listStore.findRecord("_id", owner_id),
                                recordNavTree = navTreeStore.findRecord("_id", owner_id),
                                recordGrid = cardGridStore.findRecord("_id", owner_id),
                                indexFeature,
                                olFeaturePresent = Ext.Array.findBy(featuresArray, function (item, index, allitems) {
                                    indexFeature = index;
                                    return item.getId() == olFeature.getId();
                                });

                            // Add the feature if not present
                            if (!olFeaturePresent) {
                                olFeature.setProperties({ "_deselected": false });
                                features.push(olFeature);
                                selectionModelList.select(recordList, true, true);
                                selectionModelNavTree.select(recordNavTree, true, true);
                                selectionModelCardGrid.select(recordGrid, true, true);

                                // Modify the selection for the feature on map if it present
                            } else {
                                exit = true;
                                if (featuresArray[indexFeature].get("_deselected")) {
                                    Ext.Array.forEach(featuresArray, function (item, index, allitems) {
                                        if (item.get("_owner_id") == owner_id) {
                                            item.setProperties({ "_deselected": false });
                                        };
                                    });
                                    selectionModelList.select(recordList, true, true);
                                    selectionModelNavTree.select(recordNavTree, true, true);
                                    selectionModelCardGrid.select(recordGrid, true, true);

                                } else {
                                    Ext.Array.forEach(featuresArray, function (item, index, allitems) {
                                        if (item.get("_owner_id") == owner_id) {
                                            item.setProperties({ "_deselected": true });
                                        };
                                    });
                                    selectionModelList.deselect(recordList, true);
                                    selectionModelNavTree.deselect(recordNavTree, true);
                                    selectionModelCardGrid.deselect(recordGrid, true);
                                }
                            }

                        } else {
                            exit = true;
                        }

                    } else {
                        features.push(olFeature);
                    }

                    if (mapEvent && !mapEvent.silent && !mapEvent.selectAll) {
                        if (me.getObjectId() != owner_id) {
                            me.setObjectId(olFeature ? owner_id : null);
                        }
                    }

                    if ((olFeature && mapEvent.animate && !mapEvent.selectAll) || sameObjectId) {
                        var geoAttr = layerStore.getDataSource().find("ollayername", olFeature.get("ollayername")),
                            actualZoom = olMapView.getZoom(),
                            zoom;
                        if (geoAttr && (actualZoom < geoAttr.get("zoomMin") || actualZoom > geoAttr.get("zoomMax"))) {
                            zoom = geoAttr.get("zoomDef");
                        }

                        me.animatemap(
                            olMapView,
                            ol.extent.getCenter(olFeature.getGeometry().getExtent()),
                            zoom);
                    }

                    if (exit) {
                        return false;
                    }

                    // enable info window
                    if (olFeature && !multiselection) {
                        var geovalue = olFeature.get("_geovalue"),
                            olFeatureSelected = event.selected.length;
                        if (olFeatureSelected === 1) {
                            var visible = me.isVisibleOlFeature(olFeature);
                            if (visible) {
                                var coordinates = me.calculateGeometryCoordinates(olFeature.getGeometry());
                                if (me.infoWindow._current == geovalue.getId()) {
                                    if (me.infoWindow.getPosition() !== coordinates) {
                                        me.infoWindow.setPosition(coordinates);
                                    }
                                } else {
                                    me.infoWindow._current = geovalue.getId();
                                    me.infoWindow._ownerId = geovalue.get("_owner_id");
                                    geovalue.getInfoWindowContent().then(function (text) {
                                        if (text) {
                                            me.infoWindowContent.innerHTML = text;
                                            me.infoWindow.setPosition(coordinates);
                                        } else {
                                            me.removeInfoWindow();
                                        }
                                    });
                                }
                            }
                        } else if (olFeatureSelected > 1 && me.infoWindow._ownerId != geovalue.get("_owner_id")) {
                            me.removeInfoWindow();
                        }
                    } else {
                        me.removeInfoWindow();
                    }
                });

                //Used when reselect all items to do a little moviment on the map to view the changes
                if (mapEvent && mapEvent.selectAll && Ext.isEmpty(mapEvent.select_refresh)) {
                    olMapView.animate({
                        rotation: 0.017,
                        duration: 100
                    });
                }
            } else {
                me.removeInfoWindow();
                var refreshMap = !mapEvent ? false : mapEvent.select_refresh;

                if (multiselection && !refreshMap) {
                    var featuresMap = !mapEvent ? null : mapEvent.featuresMap,
                        id = !Ext.isEmpty(deselected) ? deselected.get("_id") : featuresMap,
                        recordList = listStore.findRecord("_id", id),
                        recordNavTree = navTreeStore.findRecord("_id", id),
                        recordGrid = cardGridStore.findRecord("_id", id);

                    if (!Ext.isEmpty(deselected)) {
                        var olFeature, firsttime;

                        Ext.Array.forEach(featuresArray, function (item, index, allitems) {
                            if (item.get("_owner_id") == id) {
                                firsttime = Ext.isEmpty(item.get("_deselected"));
                                olFeature = item;
                                item.setProperties({ "_deselected": deselected.get("_deselected") });
                            };
                        });

                        if (olFeature && !firsttime) {
                            var geoAttr = layerStore.getDataSource().find("ollayername", olFeature.get("ollayername")),
                                actualZoom = olMapView.getZoom(),
                                zoom;
                            if (geoAttr && (actualZoom < geoAttr.get("zoomMin") || actualZoom > geoAttr.get("zoomMax"))) {
                                zoom = geoAttr.get("zoomDef");
                            }

                            me.animatemap(
                                olMapView,
                                ol.extent.getCenter(olFeature.getGeometry().getExtent()),
                                zoom);
                        }

                        if (deselected.get("_deselected")) {
                            selectionModelList.deselect(recordList, true);
                            selectionModelNavTree.deselect(recordNavTree, true);
                            selectionModelCardGrid.deselect(recordGrid, true);
                        } else {
                            selectionModelList.select(recordList, true, true);
                            selectionModelNavTree.select(recordNavTree, true, true);
                            selectionModelCardGrid.select(recordGrid, true, true);
                        }

                        // When a card not have features on the map and is selected
                    } else if (featuresMap) {
                        selectionModelList.select(recordList, true, true);
                        selectionModelNavTree.select(recordNavTree, true, true);
                        selectionModelCardGrid.select(recordGrid, true, true);
                    }
                }

            }

        }, this);

        olMap.addInteraction(interaction);
    },

    isVisibleOlFeature: function (olFeature) {
        var layerStore = this.getLayerStore();
        var ollayername = olFeature.get('ollayername');
        var index = layerStore.find('ollayername', ollayername);

        if (index == -1) {
            //if the layer is filtered
            return false;
        } else {
            var featureStore = this.getFeatureStore();
            var featureIndex;
            var feature;
            var featurechek;
            var item = layerStore.getAt(index);

            if (item.get('checked') == true) {
                // if the layer is visible

                if (this.getAdvancedfilter() && !this.getAdvancedfilter().isEmpty()) {
                    //if there is a filter applied
                    featureIndex = featureStore.find('_id', olFeature.getId());

                    if (featureIndex == -1) {
                        //if the feature is filtered
                        return false;

                    } else {
                        //if the feature is not filtered
                        feature = featureStore.getAt(featureIndex)
                        featurechek = feature.get('checked');

                        if (featurechek == true || Ext.isEmpty(featurechek)) {
                            //if the feature is visible; set by the navigation tree;
                            return true;

                        } else {
                            //if the feature is not visible
                            return false;
                        }
                    }
                } else {
                    //if there is not a filter applied
                    featureIndex = featureStore.find('_id', olFeature.getId());

                    if (featureIndex != -1) {
                        //if the feature is in the store
                        feature = featureStore.getAt(featureIndex);
                        featurechek = feature.get('checked');

                        if (featurechek == true || Ext.isEmpty(featurechek)) {
                            //if the feature is visible; set by the navigation tree
                            return true;

                        } else {
                            //if the feature is not visible
                            return false;
                        }

                    } else {
                        //if the feature is not in the store
                        return false;
                    }
                }
            } else {
                //if the layer is not visible
                return false;
            }
        }
    },

    /**
     *
     * @param {*} olMap
     * @param {CMDBuildUI.model.gis.GeoValue} geovalues
     */
    ol_interaction_select_select: function (olMap, geovalues, silent, animate, featuresMap) {
        silent = Ext.isEmpty(silent) ? false : silent;

        var interaction = this.getOl_interaction_select(olMap);
        if (interaction) {

            var selected = [],
                deselected = [];

            Ext.Array.forEach(geovalues, function (item, index, array) {
                if (item.get('x') || item.get('points')) {
                    var featureStore = this.getFeatureStore();
                    selected.push(item.getOlFeature());
                    if (!featureStore.findRecord('_id', item.getId())) {
                        featureStore.add(item);
                    }
                }
            }, this);

            interaction.dispatchEvent(new ol.interaction.Select.Event('select', selected, deselected, {
                silent: silent,
                animate: animate,
                featuresMap: featuresMap
            }));
        }
    },

    /**
     * This function is created to trigger the style calculation.
     * Used for when changing the 'checked' in layerStore records
     * @param {*} olMap
     * @param {Boolean} [reselect=false]
     */
    ol_interaction_select_refresh: function (olMap, reselect) {
        var vm = this.getViewModel(),
            interaction = this.getOl_interaction_select(olMap),
            objectTypeName = this.getObjectTypeName(),
            multiselection = vm.get("contextmenu.multiselection.enabled"),
            selectAll = vm.get("settingsMap.selectAll");

        if (interaction) {
            var selected = [],
                deselected = [],
                id = this.getTheObject() ? this.getTheObject().getId() : null;

            Ext.Array.forEach(this.getFeatureStore().getRange(), function (item, index, allitems) {
                if (item.get("_owner_id") == id) {
                    selected.push(item.getOlFeature());
                } else if (selectAll && item.get("_owner_type") == objectTypeName) {
                    selected.push(item.getOlFeature());
                    multiselection = false;
                }
            });
            if (reselect) {
                interaction.dispatchEvent(new ol.interaction.Select.Event('select', [], selected, {
                    silent: false
                }));
            }
            Ext.asap(function () {
                interaction.dispatchEvent(new ol.interaction.Select.Event('select', selected, deselected, {
                    silent: false,
                    animate: false,
                    select_refresh: multiselection,
                    selectAll: selectAll
                }));
            });
        }
    },

    privates: {
        getMapLabelsControl: function () {
            var MapLabelsControl = (function (Control, context) {
                function MapLabelsControl(opt_options) {
                    var options = opt_options || {};

                    var span = document.createElement("span");
                    span.innerHTML = CMDBuildUI.locales.Locales.gis.labels.label + ": ";

                    function clickEvent() {
                        this.parentElement.querySelector("button.active").className = "";
                        this.className = "active";
                        context.labelsVisibility = this.getAttribute("data-labelvisibility");
                        context.fireEvent("lablelvisibilitychange");
                    }

                    var b_hidden = document.createElement('button');
                    b_hidden.innerHTML = CMDBuildUI.locales.Locales.gis.labels.hidden;
                    b_hidden.setAttribute("aria-label", CMDBuildUI.locales.Locales.gis.labels.hiddentitle);
                    b_hidden.setAttribute("title", CMDBuildUI.locales.Locales.gis.labels.hiddentitle);
                    b_hidden.setAttribute("data-labelvisibility", "hidden");
                    b_hidden.addEventListener("click", clickEvent);

                    var b_current = document.createElement('button');
                    b_current.innerHTML = CMDBuildUI.locales.Locales.gis.labels.selected;
                    b_current.setAttribute("aria-label", CMDBuildUI.locales.Locales.gis.labels.selectedtitle)
                    b_current.setAttribute("title", CMDBuildUI.locales.Locales.gis.labels.selectedtitle);
                    b_current.setAttribute("data-labelvisibility", "selected");
                    b_current.addEventListener("click", clickEvent);

                    var b_all_class = document.createElement('button'),
                        objectTypeName = " " + (context.ownerCt.theObjectType.get("_description_translation") || context.objectTypeName);
                    b_all_class.textContent = CMDBuildUI.locales.Locales.gis.labels.all + objectTypeName;
                    b_all_class.setAttribute("aria-label", CMDBuildUI.locales.Locales.gis.labels.alltitleclass + objectTypeName)
                    b_all_class.setAttribute("title", CMDBuildUI.locales.Locales.gis.labels.alltitleclass + objectTypeName);
                    b_all_class.setAttribute("data-labelvisibility", "allclass");
                    b_all_class.addEventListener("click", clickEvent);

                    var b_all = document.createElement('button');
                    b_all.innerHTML = CMDBuildUI.locales.Locales.gis.labels.all;
                    b_all.setAttribute("aria-label", CMDBuildUI.locales.Locales.gis.labels.alltitle);
                    b_all.setAttribute("title", CMDBuildUI.locales.Locales.gis.labels.alltitle);
                    b_all.setAttribute("data-labelvisibility", "all");
                    b_all.addEventListener("click", clickEvent);

                    switch (context.labelsVisibility) {
                        case 'hidden':
                            b_hidden.className = 'active';
                            b_hidden.setAttribute("aria-selected", true);
                            break;
                        case 'selected':
                            b_current.className = 'active';
                            b_current.setAttribute("aria-selected", true);
                            break;
                        case 'allclass':
                            b_all_class.className = 'active';
                            b_all_class.setAttribute("aria-selected", true);
                            break;
                        case 'all':
                            b_all.className = 'active';
                            b_all.setAttribute("aria-selected", true);
                            break;
                    }

                    var element = document.createElement('div');
                    element.className = 'ol-labels-control ol-control';
                    element.append(span, b_hidden, b_current, b_all_class, b_all);

                    Control.call(this, {
                        element: element,
                        target: options.target
                    });
                }

                if (Control) MapLabelsControl.__proto__ = Control;
                MapLabelsControl.prototype = Object.create(Control && Control.prototype);
                MapLabelsControl.prototype.constructor = MapLabelsControl;

                return MapLabelsControl;
            }(ol.control.Control, this));

            return new MapLabelsControl();
        },

        getInfoControl: function () {
            var InfoControl = (function (Control, context) {
                function InfoControl(opt_options) {
                    var options = opt_options || {};

                    function clickEvent() {
                        var hidden = context.infoWindow.element.hidden;
                        context.infoWindow.element.hidden = !hidden;
                        if (hidden) {
                            this.className = 'cmdbuildicon-gis-hideinfo';
                            this.setAttribute("data-qtip", CMDBuildUI.locales.Locales.gis.labels.hideinfowindow);
                        } else {
                            this.className = 'cmdbuildicon-gis-showinfo';
                            this.setAttribute("data-qtip", CMDBuildUI.locales.Locales.gis.labels.showinfowindow);
                        }
                    }

                    var div = document.createElement('div');
                    div.className = 'ol-infowindow ol-control';

                    var info_icon = document.createElement('button');
                    info_icon.className = 'cmdbuildicon-gis-hideinfo';
                    info_icon.setAttribute("data-qtip", CMDBuildUI.locales.Locales.gis.labels.hideinfowindow);
                    info_icon.addEventListener("click", clickEvent);

                    div.append(info_icon);

                    Control.call(this, {
                        element: div,
                        target: options.target
                    });
                }

                if (Control) InfoControl.__proto__ = Control;
                InfoControl.prototype = Object.create(Control && Control.prototype);
                InfoControl.prototype.constructor = InfoControl;

                return InfoControl;
            }(ol.control.Control, this));

            return new InfoControl();
        }
    },

    modifySizeLabels: function () {
        var modifySizeLabels = (function (Control, context) {
            function modifySizeLabels(opt_options) {
                var options = opt_options || {},
                    slider = Ext.create('Ext.slider.Single', {
                        labelAlign: 'top',
                        floating: true,
                        fieldLabel: CMDBuildUI.locales.Locales.gis.menu.font,
                        width: 200,
                        increment: 1,
                        minValue: 8,
                        maxValue: 22,
                        cls: Ext.baseCSSPrefix + 'map-label-sizeslider',
                        value: 13,
                        listeners: {
                            change: function (slider, newValue, thumb, eOpts) {
                                context.labelSize = newValue;
                                context.fireEvent("lablelvisibilitychange");
                            },
                            blur: function (slider, event, eOpts) {
                                if (!(event.delegatedTarget.className === "x-fa fa-cog")) {
                                    slider.setVisible(false);
                                }
                            }
                        }
                    });

                function clickEvent() {
                    if (!slider.isVisible()) {
                        slider.showBy(infooptions, 'br-tr');
                        slider.focus();
                    } else {
                        slider.setVisible(false);
                    }
                }

                var el = document.createElement('div');
                el.className = 'ol-infowindow-options ol-control';

                var infooptions = document.createElement('button');
                infooptions.className = 'x-fa fa-cog';
                infooptions.setAttribute("data-qtip", CMDBuildUI.locales.Locales.gis.menu.options);
                infooptions.addEventListener("click", clickEvent);

                el.append(infooptions);

                Control.call(this, {
                    element: el,
                    target: options.target
                });
            }

            if (Control) modifySizeLabels.__proto__ = Control;
            modifySizeLabels.prototype = Object.create(Control && Control.prototype);
            modifySizeLabels.prototype.constructor = modifySizeLabels;

            return modifySizeLabels;
        }(ol.control.Control, this));

        return new modifySizeLabels();
    },

    /**
     * 
     * @returns {Boolean}
     */
    isMultiSelectionEnabled: function () {
        return this.getListTab().getSelectionModel().getSelectionMode() === 'SIMPLE';
    },

    /**
     * 
     * @returns {Ext.panel.Panel}
     */
    getMapContainerView: function () {
        return this.up("map-container");
    },

    /**
     * 
     * @returns {Ext.grid.Panel}
     */
    getListTab: function () {
        return this.down("map-tab-cards-list");
    },

    /**
     * 
     * @returns {Ext.tree.Panel}
     */
    getNavigationTreeTab: function () {
        return this.down("map-tab-cards-navigationtree");
    },

    /**
     * 
     * @returns {Ext.panel.Panel}
     */
    getViewMap: function () {
        return this.down("map-map");
    },

    /**
     * 
     * @returns {Ext.tab.Panel}
     */
    getMapTabPanel: function () {
        return this.down("map-tab-tabpanel");
    },

    /**
     * 
     * @returns {Ext.panel.Panel}
     */
    getCardView: function () {
        return this.up('classes-cards-grid-container');
    },

    /**
     * 
     * @returns {Ext.grid.Panel}
     */
    getCardGridView: function () {
        return this.getCardView().down("classes-cards-grid-grid");
    },

    /**
     * Modify the selection of the record on the map
     * @param {Ext.data.Model} record 
     * @param {Boolean} deselected 
     */
    modifySelection: function (record, deselected) {
        var viewMap = this.getMapContainerView().getViewMap(),
            interaction = viewMap.getOl_interaction_select(viewMap.getOlMap());

        record.set("_deselected", deselected);
        interaction.dispatchEvent(new ol.interaction.Select.Event('select', [], record));
    },

    /**
     * Manage the selection of the record
     * @param {Ext.data.Model} record 
     */
    onSelectItem: function (record) {
        if (record.get("_id") == this.getObjectId()) {
            this.modifySelection(record, false);
        } else {
            this.setObjectId(Ext.num(record.get('_id')));
        }
    },

    /**
     * Remove values of infoWindow parameters
     */
    removeInfoWindow: function () {
        this.infoWindow._current = null;
        this.infoWindow._ownerId = null;
        this.infoWindow.setPosition(undefined);
    },

    /**
     * Calculate coordinates for geometry
     * @param {ol.geom.GeometryType} geometry 
     * @returns {Array}
     */
    calculateGeometryCoordinates: function (geometry) {
        var coordinates;
        switch (geometry.getType()) {
            case 'LineString':
                coordinates = geometry.getCoordinates()[geometry.getCoordinates().length / 2];
                break;
            case 'Polygon':
                coordinates = geometry.getInteriorPoint().getCoordinates();
                break;
            case 'Point':
                coordinates = geometry.getCoordinates();
        }
        return coordinates;
    }
});