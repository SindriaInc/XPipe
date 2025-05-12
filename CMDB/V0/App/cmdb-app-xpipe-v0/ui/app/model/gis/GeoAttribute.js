Ext.define('CMDBuildUI.model.gis.GeoAttribute', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        type: {
            geometry: 'geometry', // postgis,
            shape: 'shape', // geoserver
            geotiff: 'geotiff' // geoserver
        },
        // SUBTYPES ARE USED ONLY IF type === geometry
        subtype: {
            linestring: 'linestring',
            point: 'point',
            polygon: 'polygon',
            shape: 'shape', // geoserver
            geotiff: 'geotiff' // geoserver
        },
        strokeStyle: {
            dash: 'dash',
            dashdot: 'dashdot',
            dot: 'dot',
            longdash: 'longdash',
            longdashdot: 'longdashdot',
            solid: 'solid'
        },
        GEOATTRIBUTE: 'GEOATTRIBUTE',
        BASETILE: 'BASETILE',

        getOlLayerVectorStyleRemoved: function () {
            var selectionStyle = new ol.style.Style({
                image: new ol.style.Circle({
                    fill: new ol.style.Fill({
                        color: 'rgba(255,165,0,1)'//'orange',//[0, 255, 0, 0.5],
                    }),
                    stroke: new ol.style.Stroke({
                        width: 1,
                        color: 'rgba(255,165,0,1)'
                    }),
                    radius: 6
                }),
                fill: new ol.style.Fill({
                    color: 'rgba(255,165,0,0.5)'//[255, 165, 0, 0.7], //'orange'
                }),
                stroke: new ol.style.Stroke({
                    width: 1,
                    color: 'rgba(255,165,0,1)'
                })
            });

            return selectionStyle;
        }
    },

    fields: [{
        name: 'name',
        type: 'string'
    }, {
        name: 'owner_type',
        type: 'string'
    }, {
        name: 'active',
        type: 'boolean'
    }, {
        name: 'type', //geometry, geotiff, shape
        type: 'string'
    }, {
        name: 'subtype', //point, linestring, poligon, geotiff, shape
        type: 'string'
    }, {
        name: 'description',
        type: 'string'
    }, {
        name: '_description_translation',
        type: 'string'
    }, {
        name: 'index',
        type: 'number'
    }, {
        name: 'visibility',
        type: 'auto'    //@type {String[]}
    }, {
        name: 'zoomMin',
        type: 'number'
    }, {
        name: 'zoomMax',
        type: 'number'
    }, {
        name: 'zoomDef',
        type: 'number'
    }, {
        name: '_beginDate',
        type: 'string'
    }, {
        name: 'style',
        type: 'auto',
        reference: 'CMDBuildUI.model.gis.GeoStyle'
    }, {
        name: '_icon',
        type: 'auto',
        convert: function (value, record) {
            if (value) {
                var _img = Ext.dom.Element.create({
                    tag: 'img',
                    src: Ext.String.format('{0}/uploads/{1}/download', CMDBuildUI.util.Config.baseUrl, value)
                }, 'img');

                _img.onload = function (event) {
                    record.set('_img', _img);
                }.bind(_img);
            }
            return value;
        }
    }, {
        name: 'checked',
        type: 'boolean',
        defaultValue: true,
        persist: false
    }, {
        name: 'text',
        type: 'string',
        persist: false,
        calculate: function (data) {
            return Ext.String.format('{0} ({1})', data._description_translation, CMDBuildUI.util.helper.ModelHelper.getObjectDescription(data.owner_type));
        }
    }, {
        name: 'ollayername',
        type: 'string',
        calculate: function (data) {
            return Ext.String.format('{0}_{1}_{2}',
                CMDBuildUI.model.gis.GeoAttribute.GEOATTRIBUTE,
                data.name,
                data.owner_type);
        },
        persist: false
    }, {
        name: 'composed_name',
        type: 'string',
        calculate: function (data) {
            return Ext.String.format('{0}.{1}',
                data.owner_type,
                data.name
            );
        },
        persist: false
    }, {
        name: 'thematism',
        type: 'auto',
        persist: false
    }, {
        name: '_img',
        type: 'auto',
        persist: false
    }],

    proxy: {
        type: 'baseproxy',
        url: CMDBuildUI.util.api.Classes.getGeoAttributes('Building') //FIXME: work with parent proxy without setting it
    },

    /**
     * @returns {ol.layer.Vector || ol.layer.Tile} //complete with the returned types
     */
    createOlLayer: function () { //This can be put on in the CMDBuildUI.model.gis.geoAttribute model
        var type = this.get('type');

        switch (type) {
            case CMDBuildUI.model.gis.GeoAttribute.type.geometry:
                return this.createOlLayerVector();
                break;
                // case CMDBuildUI.model.gis.GeoAttribute.type.shape:
                //     return this.createOlWMSLayer();
                break;
            case CMDBuildUI.model.gis.GeoAttribute.type.geotiff:
                break;
        }

        CMDBuildUI.util.Logger.log(
            Ext.String.format("olMap layer still not implemented for type {0}", type),
            CMDBuildUI.util.Logger.levels.warn
        );
    },

    /**
     * 
     * @returns {ol.layer.Vector} the layer vector created
     */
    createOlLayerVector: function () {

        var olLayerSource = new ol.source.Vector({
            strategy: ol.loadingstrategy.bbox
        });

        var olLayer = new ol.layer.Vector({
            source: olLayerSource,
            style: this.getOlLayerVectorStyle.bind(this),
            zIndex: 1000 - this.get('index')
        });

        olLayer.setCheckedLayer = function (checked) {
            this.setVisible(checked);
        }.bind(olLayer);

        olLayer.setCheckedLayer(this.get('checked'));
        olLayer.set('name', this.get('ollayername'));
        olLayer.set('type', this.get('type'));
        olLayer.set('owner_type', this.get('owner_type'));
        return olLayer;
    },

    // /**
    //  * @returns {ol.layer.Tile}
    //  */
    // createOlWMSLayer: function () {
    //     var olLayer = new ol.layer.Tile({
    //         // extent: el.get('extent')/* (el.get('extent')) ? (ol.proj.transformExtent(el.get('extent'),'EPSG:4326','EPSG:3857')) : undefined */,
    //         source: new ol.source.TileWMS({
    //             url: Ext.String.format('{0}/wms/', CMDBuildUI.util.Config.geoserverBaseUrl),
    //             params: {
    //                 'LAYERS': this.get('name'),
    //                 'TILED': true
    //             },
    //             serverType: 'geoserver'
    //         }),
    //         visible: this.get('checked')
    //     });

    //     olLayer.set('name', this.get('ollayername'));
    //     olLayer.set('type', this.get('type'));
    //     return olLayer;
    // },

    /**
     * 
     * @param {*} olFeature 
     * @param {*} number 
     * @returns {[ol.style.Style]} the complete calculated style for the geoattribute
     */
    getOlLayerVectorStyle: function (olFeature, number) {
        var style = [];
        var subtype = this.get('subtype');
        var thematism = this.get('thematism');

        if (!(thematism instanceof CMDBuildUI.model.thematisms.Thematism)) {
            switch (subtype) {
                case CMDBuildUI.model.gis.GeoAttribute.subtype.point:

                    if (olFeature.get('hasBim')) {
                        //adds the bim style

                        style.push(this.getOlLayerVectorBimStyle());

                        if (this.get('_img')) {
                            //if has bim and icon
                            style.push(this.getOlLayerVectorBaseStyle(olFeature.get("label"), olFeature.get("labelSize")));
                        }
                    } else {
                        //adds the Base style, defined in geoAttribute
                        style.push(this.getOlLayerVectorBaseStyle(olFeature.get("label"), olFeature.get("labelSize")));
                    }

                    break;
                case CMDBuildUI.model.gis.GeoAttribute.subtype.polygon:
                case CMDBuildUI.model.gis.GeoAttribute.subtype.linestring:
                case CMDBuildUI.model.gis.GeoAttribute.subtype.shape:
                    style.push(this.getOlLayerVectorBaseStyle(olFeature.get("label"), olFeature.get("labelSize")));
                    break;
            }

        } else {
            //if there is an applied thematism

            var defaultStyle = this.getStyle();

            if (olFeature) {
                var ownerId = olFeature.get('_owner_id');
                var result = Ext.Array.findBy(thematism.get('result') || [], function (result) {
                    return result.owner_id == ownerId;
                }, this);

                if (result) {
                    var resultstyle = result.geostyle,
                        text = this.getOlLayerText(olFeature.get("label"), olFeature.get("labelSize"));

                    switch (subtype) {
                        case CMDBuildUI.model.gis.GeoAttribute.subtype.point:
                            //point thematism

                            style.push(
                                new ol.style.Style({
                                    image: new ol.style.Circle({
                                        fill: resultstyle.getOlFill(),
                                        stroke: defaultStyle.getOlStroke(),
                                        radius: this.get('_img') ? defaultStyle.get('pointRadius') / 2 : defaultStyle.get('pointRadius')
                                    }),
                                    text: text
                                })
                            );

                            break;
                        case CMDBuildUI.model.gis.GeoAttribute.subtype.polygon:
                            //polygon thematism

                            style.push(
                                new ol.style.Style({
                                    fill: resultstyle.getOlFill(),
                                    stroke: defaultStyle.getOlStroke(),
                                    text: text
                                })
                            );
                            break;
                        case CMDBuildUI.model.gis.GeoAttribute.subtype.linestring:
                            //linestring thematism

                            style.push(
                                new ol.style.Style({
                                    stroke: defaultStyle.getOlStroke({
                                        color: resultstyle.getFillColor()
                                    }),
                                    text: text
                                })
                            );
                            break;
                    }
                }
            }
        }

        return style;
    },

    getOlLayerVectorBaseStyle: function (label, labelSize) {
        var style = this.getStyle();

        var fill = style.getOlFill();
        var stroke = style.getOlStroke();

        //should define subtype cases
        var _img = this.get('_img');
        var image;

        if (_img) {
            var pointradius = style.get('pointRadius');
            var coef = _img.width / _img.height;
            var scale;

            if (coef > 1) {
                scale = (pointradius * 2) / _img.width;
            } else {
                scale = (pointradius * 2) / _img.height;
            }

            image = new ol.style.Icon({
                src: _img.src,
                scale: scale
            });
        } else {
            image = new ol.style.Circle({
                fill: fill,
                stroke: stroke,
                radius: style.get('pointRadius')
            });
        }

        var text = this.getOlLayerText(label, labelSize, _img, scale);

        return new ol.style.Style({
            stroke: stroke,
            fill: fill,
            image: image,
            text: text
        });
    },

    /**
     * Bim style, red square added for icons images, rectangular point otherwise
     */
    getOlLayerVectorBimStyle: function () {
        var style = this.getStyle();

        var fill = style.getOlFill();
        var stroke = style.getOlStroke();

        return new ol.style.Style({
            image: new ol.style.RegularShape({
                stroke: this.get('_img') ?
                    new ol.style.Stroke({
                        width: 2,
                        color: 'red'
                    })
                    :
                    stroke,
                fill: this.get('_icon') ?
                    null
                    :
                    fill,
                radius: style.get('pointRadius'),
                points: 4,
                angle: Math.PI / 4
            })
        });
    },

    /**
     * 
     */
    getOlLayerVectorStyleSelected: function () {
        var selectionStyle;

        switch (this.get('subtype')) {
            case this.statics().subtype.linestring:
                selectionStyle = new ol.style.Style({
                    stroke: this.getStyle().getOlStroke({
                        color: 'rgba(255,165,0,1)'
                    })
                });
                break;
            case this.statics().subtype.point:
            case this.statics().subtype.polygon:
                selectionStyle = new ol.style.Style({
                    image: new ol.style.Circle({
                        fill: new ol.style.Fill({
                            color: 'rgba(255,165,0,1)'//'orange',//[0, 255, 0, 0.5],
                        }),
                        stroke: new ol.style.Stroke({
                            width: 1,
                            color: 'white'
                        }),
                        radius: 10

                    }),
                    fill: new ol.style.Fill({
                        color: 'rgba(255,165,0,0.5)'//[255, 165, 0, 0.7], //'orange'
                    }),
                    stroke: new ol.style.Stroke({
                        color: 'rgba(255,165,0,1)',
                        width: 2
                    })

                });
                break;
        }
        return selectionStyle;
    },

    createEmptyGeoValue: function (ownerId) {
        return Ext.create('CMDBuildUI.model.gis.GeoValue', {
            _id: null,
            _type: this.get('subtype'),
            _attr: this.get('name'),
            _attr_description: this.get('_description_translation'),
            _owner_type: this.get('owner_type'),
            _owner_id: ownerId,
            _can_write: this.get('writable')
        });
    },

    /**
     * 
     * @param {String} label 
     * @param {String} labelSize 
     * @param {Object} img 
     * @param {Number} scale 
     * @returns {[ol.style.Text]} the style text to show in map elements
     */
    getOlLayerText: function (label, labelSize, img, scale) {
        var labelSize = labelSize ? labelSize : '12px';
        if (label) {
            return new ol.style.Text({
                text: label,
                font: "600 " + labelSize + " 'Open Sans', helvetica, arial, verdana, sans-serif",
                textBaseline: "top",
                fill: new ol.style.Fill({ color: '#005ca9' }),
                stroke: new ol.style.Stroke({
                    color: '#fff', width: 3
                }),
                offsetY: img ? img.height * scale / 2 : 0,
                overflow: true
            });
        } else {
            return null;
        }
    }

});