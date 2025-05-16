Ext.define('CMDBuildUI.util.bim.Viewer', {
    singleton: true,

    /**
     * @param {Object} object
     */
    select: function (object) {
        var minimalInstance = CMDBuildUI.util.bimsurfer.util.getMinimalInstance();
        var viewer = minimalInstance.viewer;

        var clickSelect = viewer.getControl("BIMSURFER.Control.ClickSelect");
        var orbit = viewer.getControl("BIMSURFER.Control.PickFlyOrbit");

        var gid = object.gid
        if (!gid) {
            return;
        }

        clickSelect.pick({
            nodeId: gid
        });

        // var sceneObject = CMDBuildUI.util.bim.SceneTree.findNode(gid);
        var sceneJsNode = viewer.scene.findNode(object.gid);

        var matrix = sceneJsNode.nodes[0].nodes[0];
        var worldMatrix = matrix.getWorldMatrix();
        var geometryNode = matrix.nodes[0];
        var color = geometryNode._core.arrays.colors;
        var boundary = geometryNode.getBoundary();
        /*
         * Object {xmin: -150, ymin: -150, zmin: 0, xmax: 3950, ymax: 150,
         * zmax: 2800}
         * 
         */
        var center = {
            x: (boundary.xmax - boundary.xmin) / 2,
            y: (boundary.ymax - boundary.ymin) / 2,
            z: (boundary.zmax - boundary.zmin) / 2
        }
        var centerTransformed = SceneJS_math_transformVector4(worldMatrix, [center.x, center.y, center.z, 1]);
        centerTransformed = {
            x: centerTransformed[0],
            y: centerTransformed[1],
            z: centerTransformed[2]
        }
        this.zoomLookAtPoint(centerTransformed, 10000);
        orbit.pick({
            nodeId: gid,
            worldPos: [centerTransformed.x, centerTransformed.y, centerTransformed.z]
        });
    },

    /**
     * 
     */
    zoomLookAtPoint: function (point, distance, limits) {
        var minimalInstance = CMDBuildUI.util.bimsurfer.util.getMinimalInstance();
        var viewer = minimalInstance.viewer;

        var lookUpObject = viewer.scene.findNode("main-lookAt");
        lookUpObject.setLook(point);
        lookUpObject.setEye({
            x: 0,
            y: -distance,
            z: 0
        });
    },

    updateVisibility: function (objects) {
        var minimalInstance = CMDBuildUI.util.bimsurfer.util.getMinimalInstance();
        Ext.Array.forEach(objects, function (item, index, array) {
            minimalInstance.updateVisibility(item);
        }, this);
    },

    setVisibility: function (oids, value) {
        var minimalInstance = CMDBuildUI.util.bimsurfer.util.getMinimalInstance();
        //         var viewer = minimalInstance.bimServerViewer.viewer;

        viewer.setVisibility(oids, value);
    },

    /**
     *  updates the value for the layer with full opacity
     * @param {String} layername the ifc layer name 
    */
    showLayer: function (layerName) {
        delete this.transparentLayers[layerName];
    },

    /**
     *  updates the value for the layer with half transparence
     * @param {String} layername the ifc layer name 
    */
    semiHideLayer: function (layerName) {
        this.transparentLayers[layerName] = 1;
    },

    /**
     *  updates the value for the layer not visible
     * @param {String} layername the ifc layer name 
    */
    hideLayer: function (layerName) {
        this.transparentLayers[layerName] = 2;
    },

    /**
     * 
     */
    defaultView: function () {
        var minimalInstance = CMDBuildUI.util.bimsurfer.util.getMinimalInstance();
        var orbit = minimalInstance.viewer.getControl("BIMSURFER.Control.PickFlyOrbit");
        this.setOrbitEye(orbit.originEye);
    },

    /**
     * 
     */
    sideView: function () {
        var minimalInstance = CMDBuildUI.util.bimsurfer.util.getMinimalInstance();
        var bounds = minimalInstance.geometryLoader.modelBounds;
        this.setOrbitEye({
            x: 0,
            y: bounds.max.y * 3,
            z: 0
        });
    },

    /**
     * 
     */
    topView: function () {
        var minimalInstance = CMDBuildUI.util.bimsurfer.util.getMinimalInstance();
        var bounds = minimalInstance.geometryLoader.modelBounds;
        this.setOrbitEye({
            x: 2,
            y: 0,
            z: bounds.max.z * 3
        });
    },

    /**
     * 
     */
    frontView: function () {
        var minimalInstance = CMDBuildUI.util.bimsurfer.util.getMinimalInstance();
        var bounds = minimalInstance.geometryLoader.modelBounds;
        this.setOrbitEye({
            x: bounds.max.x * 3,
            y: 0,
            z: 0
        });
    },

    /**
     * 
     */
    setOrbitEye: function (eye, look) {
        var minimalInstance = CMDBuildUI.util.bimsurfer.util.getMinimalInstance();
        var orbit = minimalInstance.viewer.getControl("BIMSURFER.Control.PickFlyOrbit");
        if (!look) {
            look = {
                x: 0,
                y: 0,
                z: 0
            };
        }
        orbit.lookAt.setLook(look);
        orbit.lookAt.setUp({
            x: 0,
            y: 0,
            z: 1
        })

        orbit.lookAt.setEye(eye);
        var view = orbit.obtainView();
        orbit.restoreView(view);
    },

    show: function (poid, success, failure, scope) {
        var me = this;
        this._init();

        var email = 'admin@bimserver.com'//CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.bim.user);
        var password = 'admin'//CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.bim.password);

        //The value is instanciated when downloading the modules
        var minimal = window.minimal//CMDBuildUI.util.bimsurfer.util.getMinimal();

        Ext.asap(function () {
            var minimalInstance = new minimal({
                bimServerAddress: window.cmdbuildConfig.bimserverBaseUrl,
                bimServerLogin: {
                    username: email,
                    password: password
                },
                viewerSettings: {
                    canvas: 'divBim3DView',
                    onSelect: function (poid, sceneJsNode) {
                        var object = sceneJsNode.data.object;
                        if (minimalInstance.viewer.selected == object) {
                            return
                        } else {
                            minimalInstance.viewer.selected = object;
                            Ext.GlobalEvents.fireEventArgs('highlitedifcobject', [object]);
                        }
                    }.bind(this),
                    onUnselect: Ext.emptyFn
                },
                ifcProperties: {
                    onSelect: function (groupId, object, data) {
                        Ext.GlobalEvents.fireEventArgs("ifcpropertychange", [groupId, object, data]);
                    },
                    onUnselect: function (groupId, object) {
                        Ext.GlobalEvents.fireEventArgs("ifcpropertyremove", []);
                    }
                }
            });

            CMDBuildUI.util.bimsurfer.util.setMinimalInstance(minimalInstance);
            minimalInstance.start(poid).then(function () {
                var minimalInstance = CMDBuildUI.util.bimsurfer.util.getMinimalInstance();
                var orbit = minimalInstance.viewer.getControl("BIMSURFER.Control.PickFlyOrbit");
                try {
                    orbit.originEye = orbit.obtainView().eye;
                } catch (error) {
                    orbit.originEye = orbit.eye;
                }

                if (!Ext.isEmpty(success)) {
                    success.call(scope);
                }

                var bimStore = Ext.getStore('bim.Projects');
                bimStore.load({
                    callback: function (records, operation, success) {

                        var restoreRoot = true;
                        Ext.Array.forEach(minimalInstance.projects, function (item, index, array) {
                            var roid = item.lastRevisionId;
                            var model = minimalInstance.models[roid];

                            if (model) {
                                model.getAllOfType('IfcProject', true, function (project) {
                                    CMDBuildUI.util.bim.IfcTree._init(project, restoreRoot);
                                    restoreRoot = false;

                                    var bimProject = Ext.Array.findBy(records, function (item, index, array) {
                                        return item.get('projectId') == model.poid;
                                    }, this);

                                    if (bimProject) {
                                        Ext.GlobalEvents.fireEventArgs("ifctreeready", [bimProject]);
                                    }
                                }.bind(this));
                            } else {
                                failure.call(scope);
                            }
                        }, this)
                    },
                    scope: this
                })

            }.bind(this), function () {
                if (!Ext.isEmpty(failure)) {
                    failure.call(scope);
                }
            }.bind(this));
        }, this);
    },

    setRotateMode: function () {
        var minimalInstance = CMDBuildUI.util.bimsurfer.util.getMinimalInstance();
        var orbit = minimalInstance.viewer.getControl("BIMSURFER.Control.PickFlyOrbit");
        orbit.mode = 'orbit';
    },

    setPanMode: function () {
        var minimalInstance = CMDBuildUI.util.bimsurfer.util.getMinimalInstance();
        var orbit = minimalInstance.viewer.getControl("BIMSURFER.Control.PickFlyOrbit");
        orbit.mode = 'pan';
    },

    /**
     * This function makes extra operation on the instance
     * -Add another listener on clickUp on the canvas
     * @param {Minimal} minimalInstance
     */
    _serviceOperation: function (minimalInstance) {
        try {
            //add mouseUp event
            var canvas = minimalInstance.bimServerViewer.canvas;
            var camera = minimalInstance.bimServerViewer.viewer.cameraControl;

            //This function is very similat to the one in the bimview cameracontrol module
            canvas.addEventListener("mouseup", function (e) {

                var dt = e.timeStamp - camera.mouseDownTime;
                switch (e.which) {
                    case 1:
                        if (dt < 500. && camera.closeEnoughCanvas(camera.mouseDownPos, camera.mousePos)) {
                            var viewObject = camera.viewer.pick({
                                canvasPos: camera.mousePos,
                                shiftKey: e.shiftKey
                            });

                            if (viewObject && viewObject.object) {
                                CMDBuildUI.util.bim.Viewer.select(viewObject.object.objectId);
                            }

                        }
                        break;
                }
            });
        } catch (e) { }
    },

    /**
     * @param {String} cameraType: orthographic or perspective
     */
    setCamera: function (cameraType) {
        CMDBuildUI.util.Logger.log('Function not available for BIMSURVER V1', CMDBuildUI.util.Logger.levels.debug);
        // var minimalInstance = CMDBuildUI.util.bimsurfer.util.getMinimalInstance();
        // var viewerCamera = minimalInstance.bimServerViewer.viewer.camera;

        // switch (cameraType) {
        //     case 'orthographic':
        //         viewerCamera._projection = viewerCamera.orthographic
        //         break;
        //     case 'perspective':
        //         viewerCamera._projection = viewerCamera.perspective;
        //         break;
        // }

        // minimalInstance.bimServerViewer.viewer.updateViewport()
    },

    /**
     * This function restores the transparancy 
     */
    _init: function () {
        this.transparentLayers = {
            'IfcSpace': 2
        }

        /**
         * FIXME: view casese for more than one project
         */
        CMDBuildUI.util.bimsurfer.util.init();
    },

    privates: {
        /**
         * Set the default values sono layers. see this.reset() function
         * 
        */
        transparentLayers: {}
    }
});