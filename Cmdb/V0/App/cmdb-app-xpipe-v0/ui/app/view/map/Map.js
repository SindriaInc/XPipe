Ext.define('CMDBuildUI.view.map.Map', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.map.MapController',
        'CMDBuildUI.view.map.MapModel'
    ],

    statics: {
        filterZoomMaxId: 'zoomMax',
        filterZoomMinId: 'zoomMin',
        filterVisibility: 'visibility'
    },

    mixins: ['CMDBuildUI.view.map.Mixin'],
    alias: 'widget.map-map',
    controller: 'map-map',
    viewModel: {
        type: 'map-map'
    },

    config: {
        olMap: {
            $value: undefined,
            evented: true
        },
        divMapId: null,

        objectType: undefined,
        objectTypeName: undefined,
        objectId: undefined,

        theObject: undefined,

        mapCenter: {
            $value: undefined,
            evented: true,
            lazy: true
        },
        zoom: {
            $value: undefined,
            evented: true,
            lazy: true
        },

        /**
         * This param is not used in standard configuration. Can be set for when the map is stand alone, witout the container set
         */
        thematismId: {
            $value: undefined,
            evented: true
        },

        //the instance thematism. Calculated starting from thematismId
        theThematism: undefined,
        highlightselected: undefined,

        /**
         * calculated starting from objectType and objectTypeName
         */
        layerStore: null,

        //used the one in the binding. Is created for
        featureStore: Ext.create('Ext.data.Store', {
            model: 'CMDBuildUI.model.gis.GeoValue',
            proxy: {
                type: 'memory'
            },
            grouper: {
                property: 'ollayername'
            }
        }),

        shapeFeatureStore: undefined,

        bimStore: undefined,

        hashMap: undefined,

        drawmode: {
            $value: undefined,
            evented: true
        },
        advancedfilter: {
            $value: null,
            evented: true
        },

        navigationTree: {
            $value: null,
            evented: true
        },

        attach_nav_tree_collection: {
            value: undefined,
            evented: true
        },

        loadedExtentsRtree: null,

        initialized: {
            $value: false
        },

        start: false
    },

    reference: 'map',
    publishes: [
        'olMap',
        'objectType',
        'objectTypeName',
        'objectId',
        'theObject',

        'zoom',

        'thematismId',
        'theThematism',
        'featureStore',
        'shapeFeatureStore',
        'drawmode',
        'advancedfilter',

        'initialized',
        'start'
    ],

    bind: {
        layerStore: '{layerStore}'
    },

    twoWayBindable: [
        'zoom',
        'mapCenter',
        'objectId',
        'drawmode',
        'layerStore'
    ],

    labelsVisibility: "hidden",

    infoWindow: null,
    infoWindowContent: null,

    /**
     * Add the listener once olMap is set   TODO: Could allow more listeners
     */
    updateOlMap: function (newValue, oldValue) {

    },

    applyZoom: function (zoom) {
        var minZoom = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.minZoom);
        var maxZoom = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.maxZoom);

        if (Ext.isNumber(zoom)) {
            zoom = Math.max(minZoom, zoom);
            zoom = Math.min(maxZoom, zoom);

            return zoom;
        } else {
            return minZoom;
        }
    },

    updateZoom: function (value, oldValue) {
        if (this.getOlMap() && this.getOlMap().getView().getZoom() != value) {
            this.getOlMap().getView().setZoom(value);
        }
    },

    updateHighlightselected: function (value, oldValue) {
        if (this.getOlMap()) {
            this.ol_interaction_select_refresh(this.getOlMap());
        }
    },

    applyMapCenter: function (value, oldValue) {
        if (Ext.isArray(value) && Ext.isArray(oldValue) && !Ext.Array.equals(value, oldValue)) {
            return value;
        } else if (Ext.isArray(value) && !Ext.isArray(oldValue)) {
            return value;
        }
    },

    updateMapCenter: function (value, oldValue) {
        if (this.getOlMap()) {
            var olMapCenter = this.getOlMap().getView().getCenter();
            if (!Ext.isArray(olMapCenter) || olMapCenter[0] != value[0] || olMapCenter[1] != value[1]) {
                this.getOlMap().getView().setCenter(value);
            }
        }
    },

    initComponent: function () {
        this.callParent(arguments);
        if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.navigationTreeEnabled) == false) {
            this.geovaluesVisibility = Ext.emptyFn;
        }

        if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.bim.enabled) == false) {
            this.geovaluesBim = Ext.emptyFn;
        }
        this.setDivMapId("map-" + Ext.id());
    },

    updateHashMap: function (value, oldValue) {
        value.event.addListener('endupdate', this.onHashMapUpdate, this);
    },

    privates: {
        geovaluesVisibility: function (records) {
            var hashMap = this.getHashMap();
            if (hashMap) {
                Ext.Array.each(records, function (record, index, array) {
                    var ownerid = record.get('_owner_id');

                    var c = hashMap.get(ownerid);
                    var checked = (c == undefined || c == true) ? true : false;
                    record.set('checked', checked);
                }, this);
            }
        },

        /**
         *
         * @param {*} records
         */
        geolayerVisibility: function (records) {
            var hashMap = this.getHashMap();
            if (hashMap) {
                Ext.Array.forEach(records, function (item, index, array) {
                    var ownerid = item.get('_owner_id');

                    var c = hashMap.get(ownerid);
                    var checked = (c == undefined || c == true) ? true : false;
                    item.set('checked', checked);
                }, this);
            }
        },

        getlayerLayerVisibility: function (records) {
            var layerStore = this.getLayerStore();

            Ext.Array.forEach(records, function (item, index, array) {
                var ollayername = item.get('ollayername');

                var geoattributeindex = layerStore.findBy(function (item) {
                    return item.get('ollayername') == ollayername;
                }, this);

                if (geoattributeindex != -1) {
                    var geoattribute = layerStore.getAt(geoattributeindex);
                    item.set(CMDBuildUI.model.gis.GeoLayer.checkedLayer, geoattribute.get('checked'));
                } else {
                    item.set(CMDBuildUI.model.gis.GeoLayer.checkedLayer, false);
                }
            }, this);
        },

        /**
         * This function manipulates the geovalues and sets the correct `hasBim` and `projectId` property if necessary
         * @param {CMDBuildUI.model.gis.GeoValue} records
         */
        geovaluesBim: function (geovalues) {
            var store = Ext.getStore("bim.Projects");

            Ext.Array.each(geovalues, function (geovalue, index, array) {
                if (geovalue.get('_type') == CMDBuildUI.model.gis.GeoAttribute.subtype.point) {
                    var ownerid = geovalue.get('_owner_id');
                    var projectIndex = store.find('ownerCard', ownerid);
                    if (projectIndex != -1) {
                        geovalue.set('hasBim', true);
                        var project = store.getAt(projectIndex);
                        geovalue.set('projectId', project.get('projectId'));
                    }
                }
            }, this);
        },

        refreshBtnClick: function () {
            this.fireEventArgs('refreshbtnclick', [this])
        }
    },

    onHashMapUpdate: function (hash) {
        var featureStore = this.getFeatureStore();
        var shapefeatureStore = this.getShapeFeatureStore();

        var keyValues = hash.keyValue;
        if (featureStore && shapefeatureStore) {
            var olMap = this.getOlMap();

            for (var key in keyValues) {
                var checked = hash.get(key);

                var geovalueindex = -1;
                do {
                    geovalueindex = featureStore.find('_owner_id', key, ++geovalueindex);

                    if (geovalueindex != -1) {
                        var geovalue = featureStore.getAt(geovalueindex);
                        geovalue.set('checked', checked);

                        if (olMap) {
                            this.setVisibleOlFeature(olMap, geovalue.get('ollayername'), geovalue, 'checked', checked);
                        }
                    }
                } while (geovalueindex != -1);

                var shapefeatureindex = -1;
                do {
                    shapefeatureindex = shapefeatureStore.find('_owner_id', key, ++shapefeatureindex);

                    if (shapefeatureindex != -1) {
                        var shapefeature = shapefeatureStore.getAt(shapefeatureindex);
                        shapefeature.set('checked', checked);

                        if (olMap) {
                            this.setVisibleOlGeoFeature(olMap, '_', shapefeature, CMDBuildUI.model.gis.GeoLayer.checked, checked);
                        }
                    }
                } while (shapefeatureindex != -1);
            }
        }
    }
});
