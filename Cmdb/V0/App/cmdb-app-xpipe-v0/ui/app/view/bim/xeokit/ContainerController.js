Ext.define('CMDBuildUI.view.bim.xeokit.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.bim-xeokit-container',

    control: {
        '#': {
            afterrender: 'onAfterRender',
            beforedestroy: 'onBeforeDestroy'
        }
    },

    /**
     * @param {CMDBuildUI.view.bim.xeokit.Container} view
     * @param {Object} eOpts
     */
    onAfterRender: function (view, eOpts) {
        var me = this,
            vm = this.getViewModel(),
            bimPopup = view.up("#bimPopup");

        Ext.Loader.loadScript({
            url: 'resources/js/xeokit/xeokit-sdk.es.js',
            onLoad: function () {
                var viewer = new xeokitSdkEs.Viewer({
                    canvasId: "bimCanvas",
                    transparent: true,
                    saoEnabled: true,
                    localeService: new xeokitSdkEs.LocaleService({
                        messages: {
                            "language": {
                                "NavCube": {
                                    "front": CMDBuildUI.locales.Locales.bim.navCube.front,
                                    "back": CMDBuildUI.locales.Locales.bim.navCube.back,
                                    "top": CMDBuildUI.locales.Locales.bim.navCube.top,
                                    "bottom": CMDBuildUI.locales.Locales.bim.navCube.bottom,
                                    "left": CMDBuildUI.locales.Locales.bim.navCube.left,
                                    "right": CMDBuildUI.locales.Locales.bim.navCube.right
                                }
                            }
                        },
                        locale: "language"
                    })
                });

                new xeokitSdkEs.SectionPlanesPlugin(viewer, {
                    overviewCanvasID: "sectionPlanesPlugin",
                    overviewVisible: true
                });

                new xeokitSdkEs.ViewCullPlugin(viewer);

                viewer.cameraControl.followPointer = true;
                viewer.scene.highlightMaterial.fillAlpha = 0.3;
                viewer.scene.highlightMaterial.edgeColor = [1, 1, 0];

                new xeokitSdkEs.StoreyViewsPlugin(viewer);

                Ext.getStore('bim.Projects').load({
                    callback: function (records, operation, success) {
                        var project = Ext.Array.findBy(records, function (item, index) {
                            return item.get("projectId") === view.projectId
                        });
                        viewer.projectId = project.id;
                        viewer.selectedId = view.selectedId;

                        me.setWithCredentials(true);
                        var xktLoader = new xeokitSdkEs.XKTLoaderPlugin(viewer),
                            modelNode = xktLoader.load({
                                id: "myModel",
                                src: Ext.String.format('{0}/bim/projects/{1}/file?bimFormat=xkt', CMDBuildUI.util.Config.baseUrl, project.id),
                                edges: true,
                                backfaces: true
                            });

                        modelNode.on("loaded", function () {
                            me.setWithCredentials(false);
                            me.onModelLoaded(this);
                            var paramsCam = me.calculateParamsCamera(viewer);
                            vm.set("eye", paramsCam[0]);
                            vm.set("look", paramsCam[1]);

                            viewer.cameraFlight.jumpTo({
                                eye: paramsCam[0],
                                look: paramsCam[1],
                                up: vm.get("up"),
                                projection: "perspective",
                                fit: true
                            });

                            new xeokitSdkEs.NavCubePlugin(viewer, {
                                canvasId: "bimNavCubeCanvas",
                                visible: true,
                                cameraFlyDuration: vm.get("duration"),
                                size: 250,
                                alignment: "bottomRight",
                                bottomMargin: 100,
                                rightMargin: 10,
                                color: 'white'
                            });

                            view.getObjectTreeTab().fireEvent('objectselected', null);
                            if (viewer.selectedId) {
                                var pickResult;
                                Ext.Object.each(viewer.scene.objects, function (key, value, allobjects) {
                                    if (key == viewer.selectedId) {
                                        pickResult = value;
                                        return false;
                                    }
                                })
                                me.selectElement(viewer, pickResult);
                            }

                            // Resize popup for render model after xeokit version 2.3.0-beta-29 that doesn't render the model twice in a row 
                            setTimeout(function () {
                                bimPopup.setWidth(bimPopup.getWidth() + 1);
                                bimPopup.setHeight(bimPopup.getHeight() + 1);
                            }, 250);
                        });
                    }
                });

                viewer.cameraControl.on("picked", function (pickResult) {
                    me.selectElement(viewer, pickResult);
                });

                viewer.cameraControl.on("pickedNothing", function () {
                    var entitySelected = Ext.Object.getValues(viewer.scene.selectedObjects)[0];
                    if (entitySelected) {
                        entitySelected.selected = false;
                    }
                    vm.set("enabledTabs.properties", false);
                    vm.set("enabledTabs.card", false);
                    if (view.getTabPanel().getActiveTab().xtype !== "bim-xeokit-tab-layers") {
                        view.getTabPanel().setActiveTab(0);
                    }
                });

                view._viewer = viewer;
            }
        });
    },

    /**
     * 
     * @param {CMDBuildUI.view.bim.xeokit.Container} view 
     * @param {Object} eOpts 
     */
    onBeforeDestroy: function (view, eOpts) {
        view.getViewer().destroy();
    },

    /**
     * @param {xeokitsdk.viewer.scene.PerformanceModel.PerformanceModel} model
     */
    onModelLoaded: function (model) {
        var view = this.getView(),
            vm = view.lookupViewModel();

        var store = vm.get('objectsTreeStore'),
            types = vm.get('layersStore'),
            rootMetaObject = view._viewer.metaScene.metaModels.myModel.rootMetaObject;

        this.createObjectTree(rootMetaObject, store.getRootNode());
        this.createTypeList(rootMetaObject, types);
    },

    privates: {

        /**
         * Populate the objectsTreeStore with entities
         * @param {Object} metadata represent entity
         * @param {Object} parent the element represents the root
         */
        createObjectTree: function (metadata, parent) {
            /**
             * View @xeokit/xeokit-sdk/src/plugins/TreeViewPlugin/ModelTreeView.js _createContainmentNodes
             */
            var me = this,
                objType = metadata.type,
                objName = metadata.name || objType,
                objId = metadata.id,
                children = metadata.children || [];

            var node = parent.appendChild({
                text: objName,
                entityType: objType,
                entityId: objId,
                leaf: !children.lenght,
                checked: true
            });

            children.forEach(function (child) {
                me.createObjectTree(child, node);
            });
        },

        /**
         * Populate the layersStore with the types of a entities
         * @param {Object} metadata represent entity
         * @param {Object} store the store to push the data
         */
        createTypeList: function (metadata, store) {
            /**
             * View @xeokit/xeokit-sdk/src/plugins/TreeViewPlugin/ModelTreeView.js  _createTypesNodes
             */
            var me = this,
                objType = metadata.type,
                objName = metadata.name || objType,
                objId = metadata.id,
                children = metadata.children || [],
                typeNode = store.findRecord("entityType", objType);

            if (!typeNode) {
                var text = objType;
                if (Ext.String.startsWith(text, "ifc", true)) {
                    text = text.slice(3);
                }
                text = text.replace(/([a-z])([A-Z])/g, "$1 $2")
                typeNode = store.add({
                    text: text,
                    entityType: objType,
                    children: [],
                    length: 0,
                    visible: true
                })[0];
            }

            typeNode.data.children.push({
                text: objName,
                entityType: objType,
                entityId: objId,
                leaf: !children.lenght
            });

            typeNode.data.length += 1;

            children.forEach(function (child) {
                me.createTypeList(child, store);
            });
        },

        /**
         * Do specific actions on the selection of entity 
         * @param {Object} viewer 
         * @param {Object} pickResult the entity selected 
         * @returns null if not entity selected
         */
        selectElement: function (viewer, pickResult) {
            var scene = viewer.scene,
                entitySelected = Ext.Object.getValues(scene.selectedObjects)[0],
                view = this.getView(),
                viewCard = view.getCardTab(),
                viewProperties = view.getPropertiesTab(),
                entity = pickResult ? (pickResult.entity || pickResult) : null;

            if (!entity) {
                return;
            }

            if (!entitySelected || entity.id !== entitySelected.id) {

                if (entitySelected) {
                    entitySelected.selected = false;
                }

                scene.setObjectsSelected(entity.id, true);
                viewProperties.setEntity(entity);
                view.getObjectTreeTab().fireEvent('objectselected', entity);
                view.getLayerTab().fireEvent('objectselected', entity);
                if (!entity.mappingInfo) {
                    CMDBuildUI.util.bim.Util.getRelatedCard(viewer.projectId, entity.id, function (data) {
                        entity.mappingInfo = data;
                        viewCard.setEntity(entity);
                    });
                } else {
                    viewCard.setEntity(entity);
                }
                viewer.cameraFlight.flyTo(entity);

            }

        },

        /**
         * Calculate eye and look for the initial camera view
         * @param {Object} viewer 
         * @returns [eye, look]
         */
        calculateParamsCamera: function (viewer) {
            var vm = this.getViewModel(),
                math = xeokitSdkEs.math,
                center = math.vec3(),
                aabb = viewer.scene.aabb,
                dist = Math.abs(math.getAABB3Diag(aabb) / Math.tan(viewer.cameraFlight._fitFOV * math.DEGTORAD));

            /* la formula da cui si è partiti è [center[0] - (dist * dir[0]), center[1] - (dist * dir[1]), center[2] - (dist * dir[2])];
            eyeRight = [center[0] + dist, center[1], center[2]];
            eyeFront = [center[0], center[1], center[2] + dist];
            eyeTop = [center[0], center[1] + dist, center[2]];
            eyeMiddle = [center[0] - (-0.7 * dist), center[1], center[2] - (-0.7 * dist)];*/

            vm.set("eyeTop", [center[0], center[1] + dist, center[2]]);
            vm.set("lookTop", center);

            math.getAABB3Center(aabb, center);
            return [[center[0] + dist / 2, center[1] + dist / 4, center[2] + 0.85 * dist], center];
        },

        /**
         * Enable or disable the parameter withCredentials of the XMLHttpRequest
         * @param {Boolean} active 
         */
        setWithCredentials: function (active) {
            var xp = XMLHttpRequest.prototype;
            XMLHttpRequest = function (args) {
                var obj = new xp.constructor(args);
                obj.withCredentials = active;
                return obj;
            };
            XMLHttpRequest.prototype = xp;
        }

    }
});
