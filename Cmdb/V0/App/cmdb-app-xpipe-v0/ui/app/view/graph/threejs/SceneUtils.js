Ext.define('CMDBuildUI.graph.threejs.SceneUtils', {
    singleton: true,
    me: this,

    /**
     * @param {Object} data
     * @type {String} data.containerView
     * @type {cy.nodes} data.cy
     */
    init: function (data) {
        this.reset();
        this.cy = data.cy;
        //saves the container view
        this.containterView = data.containterView;

        this.createScene();
        //this.addAxes();
        this.fillScene(data.cy);
        this.tooltip = new this.tooltipContext(this.tooltipConf, 'hola', 'hola');
        this.renderFunction();
    },

    /**
     * resets the privates variables
     */
    reset: function () {
        this.tooltipConf = {
            //Gets the pressed state of the button
            enable: CMDBuildUI.graph.util.canvasMenu.getComponent(
                CMDBuildUI.graph.util.canvasMenu.getIdComponent('tooltip'))
                .pressed //HACK: Disable tooltip 
        };
        this.selectedNode = {};
        this.renderSceneVariable = true;
        this.clickCTRL = false;
        this.nodeRegister = {};
        this.edgeRegister = {};
        this.containterView = null;
    },

    /**
     * The render function required by threejs
     */
    renderFunction: function () {
        // console.log("renderFunction Start");
        var me = this;
        function render() {
            me.raycaster.setFromCamera(me.mouse, me.camera);
            me.controls.update();
            me.mouseMoveInteraction();

            // me.drawraycastRay();
            me.renderer.render(me.scene, me.camera);
            if (me.renderSceneVariable) {
                requestAnimationFrame(render);
            } else {
                /**
                 * Threejs saved variables reset
                 */
                this.render = null;
                this.camera = null;
                this.raycaster = null;
                this.controls = null;
                this.scene = null;
                this.mouse = null;
            }
        }
        render();
        this.camera.position.z = 20;
        this.renderer.setClearColor(0xffffff);
    },

    /**
     *Creates the basic scene adding necessary controls
     */
    createScene: function () {
        this.div = Ext.getElementById('cy');
        this.scene = new THREE.Scene();
        this.camera = new THREE.PerspectiveCamera(
            60,
            this.div.clientWidth / this.div.clientHeight,
            1,
            3000);

        // this.camera = new THREE.OrthographicCamera(
        //     this.div.width() / - 2,
        //     this.div.width() / 2,
        //     this.div.height() / 2,
        //     this.div.height() / - 2,
        //     1,
        //     1000);

        this.renderer = new THREE.WebGLRenderer();

        this.renderer.setSize(this.div.clientWidth, this.div.clientHeight);
        this.div.appendChild(this.renderer.domElement);

        // /**
        //  * add Orbit controls camera 
        //  */
        //  this.addOrbitControl();

        /**
         * Adds the raycast function
         */
        this.addInteractionControls();
        this.manageEventListeners('add');
        /**
         * add trackballControls
         * NOTE: this methos must stay after 'this.div.append(this.renderer.domElement);' 
         */
        this.addTrackballControl();
    },

    /**
     * 
     */
    destroyScene: function () {
        this.renderSceneVariable = false;
        this.tooltip.destroy();
    },

    /**
     * Adds the x y z axes in the scene
     */
    addAxes: function () {
        var axesHelper = new THREE.AxesHelper(100);
        this.scene.add(axesHelper);
    },

    /**
     * This function is helpful for debugging errors to know where the raycast ray actually points to;
     */
    drawraycastRay: function () {
        if (this.ray) {
            this.scene.remove(this.ray);
        }

        this.ray = new THREE.ArrowHelper(this.raycaster.ray.direction, this.raycaster.ray.origin, 300, 0xff0000);
        this.scene.add(this.ray);
    },

    /**
     * adds the orbit Control in the scene
     */
    addOrbitControl: function () {
        this.controls = new THREE.OrbitControls(this.camera, this.renderer.domElement);
        this.controls.autoRotate = false;
    },

    /**
     * adds the trackballControls
     */
    addTrackballControl: function () {
        this.controls = new THREE.TrackballControls(this.camera, this.renderer.domElement);
        this.controls.rotateSpeed = 1.5;
        this.controls.zoomSpeed = 1.2;
        this.controls.panSpeed = 0.8;
        this.controls.noZoom = false;
        this.controls.noPan = false;
        this.controls.staticMoving = false;
        this.controls.dynamicDampingFactor = 0.3;
    },

    /**
     * This function handles the mouse coordinates change and the creation of the raycast vector
     */
    addInteractionControls: function () {
        var me = this;
        this.raycaster = new THREE.Raycaster();
        this.mouse = new THREE.Vector2();

        /**
         * adds the click control
         */
        this.addClickControl();

        this.div.onmousemove = function (event) {
            // event.preventDefault();
            me.mouse.x = (event.offsetX / me.renderer.domElement.width) * 2 - 1;
            me.mouse.y = - (event.offsetY / me.renderer.domElement.height) * 2 + 1;

            /**
             * Save tooltip informations
             */
            me.tooltipConf.mouseX = event.clientX;
            me.tooltipConf.mouseY = event.clientY;
        };
    },

    /**
     * Here is handled the intersection between the mouse and the objects in the scene
     */
    mouseMoveInteraction: function () {
        var intersects = this.raycaster.intersectObjects(this.scene.children),
            vmGraph = this.containterView.getViewModel();
        /**
         * Handles the tooltip
         */
        if (intersects.length == 0) {
            if (this.tooltip.isOpen()) {
                this.tooltip.close();
                vmGraph.set("pointerExternalCanvas", true);
            }

        } else {
            if (this.tooltip.getEnable()) {
                //Need to be allways opened to set the position
                this.tooltip.open();
                var j = 0;
                for (var i = 0; i < intersects.length; i++) {
                    if (intersects[i].object.type === 'Sprite' || intersects[i].object.type === 'Mesh') {
                        j = i;
                        i = intersects.length;
                    }
                }

                if (vmGraph && vmGraph.get("pointerExternalCanvas")) {
                    if (intersects[j].object.type == 'Line') {
                        // this.tooltip.setHeader(CMDBuildUI.locales.Locales.relationGraph.relation);

                        var idObjects = [];
                        Ext.Array.forEach(intersects, function (item, index, allitems) {
                            idObjects.push(item.object.name);
                        });
                        this.getEdgeInformation(idObjects, function (obj) {
                            this.tooltip.setText('relation', obj);
                        });
                    } else if (intersects[j].object.type == 'Sprite') {
                        // this.tooltip.setHeader(CMDBuildUI.locales.Locales.relationGraph.card);
                        this.getNodeInformation(intersects[j].object.name, function (obj) {
                            this.tooltip.setText('node', obj);
                        });
                    }
                }

            } else {
                if (this.tooltip.isOpen()) {
                    this.tooltip.close();
                }

            }

        }
    },

    /**
     * This function handles the add an the remove of keyDown and KeyUp event listeners
     * @param {String} method the config string, can be add || remove;
     */
    manageEventListeners: function (method) {
        var me = this;

        /**
         * Handler event for the keydown event 
         * @param {Object} eOpts 
         */
        function keyDownFunction(eOpts) {
            eOpts = eOpts.browserEvent;
            // console.log(eOpts, "keyDown");
            if (eOpts.key == "Control") {
                me.clickCTRL = true;
            }
        }

        /**
         * Handler function for the keyup event
         * @param {Object} eOpts 
         */
        function keyUpFunction(eOpts) {
            eOpts = eOpts.browserEvent;
            // console.log(eOpts, "keyUp");
            if (eOpts.key == "Control") {
                me.clickCTRL = false;
            }
        }

        switch (method) {
            case 'add':
                this.containterView.mon(Ext.getWin(), 'keydown', keyDownFunction, this);
                this.containterView.mon(Ext.getWin(), 'keyup', keyUpFunction, this);
                break;
            //the remotion of the listenr is handled by the .mon method
        }
    },

    /**
     * This function adds in the scene the sphere according to the cy nodes
     * @param {cytoscape} cy graph rapresented by cytoscape. Have information about edges and nodes
     */
    fillScene: function (cy) {
        var nodes = cy.nodes().toArray();
        var objects = [];

        nodes.forEach(function (node) {
            if (!this.nodeRegister[node.id()]) {
                this.nodeRegister[node.id()] = true;    //save the nodeRegister new data
                this.fillNode(node, objects);
            }

        }, this);

        var edges = cy.edges().toArray();
        edges.forEach(function (edge) {
            if (!this.edgeRegister[edge.id()]) {
                this.edgeRegister[edge.id()] = true;    //save the edgeRegister new data
                this.fillEdge(edge);
            }
        }, this);

        /**
         * add drag control
         */
        if (objects.length > 0) {
            this.addDragControl(objects);
        }
        CMDBuildUI.graph.threejs.GuiSphere.run(this.scene, cy, this.camera);
    },

    enableLabels: function (value) {
        this.cy.nodes().forEach(function (node) {
            if (node.glLabel) {
                node.glLabel.visible = value;
            }
        });

        this.cy.edges().forEach(function (edge) {
            if (edge.glLabel) {
                edge.glLabel.visible = value;
            }
        });

        this.allLabelsEnabled = value;
    },


    /**
     * 
     */
    fillNode: function (node, objects) {
        var sprite = this.createSprite(node.data().type, node.id());
        objects.push(sprite);   //insert the three.js object in the array
        this.scene.add(sprite); //add three.js object to the scene

        //create the related label
        var message = Ext.String.format('{0}: {1}',
            Ext.String.startsWith(node.data().type, 'compound_') ?
                CMDBuildUI.locales.Locales.relationGraph.compoundnode
                :
                CMDBuildUI.util.helper.ModelHelper.getObjectDescription(node.data().type), node.data().description);
        var label = this.createLabel(message, 'node');
        sprite.add(label);

        node.glSprite = sprite; //save the three.js object in cytoscape as a reference
        node.glLabel = label;
    },

    /**
     * @param edge
     */
    fillEdge: function (edge) {
        var line = this.createLine(edge);
        this.scene.add(line);

        //creates the related label
        var message = Ext.String.format('{0}', edge.data().descriptionDirect);
        var label = this.createLabel(message, 'edge');
        line.add(label);

        edge.glLine = line;
        edge.glLabel = label;
    },
    /**
     * This function creates the mesh sphere.
     * @param {cy.Object} node the cytoscape node whit some informations
     * @returns the THREE.Mesh object
     */
    createSphere: function (node) {
        var sphere_geometry = new THREE.SphereGeometry(6, 32, 32);
        var sphere_material = new THREE.MeshBasicMaterial({ color: 0x0000ff });
        var sphere = new THREE.Mesh(sphere_geometry, sphere_material);
        sphere.position.x = (Math.random() * 300) - 150;
        sphere.position.y = (Math.random() * 300) - 150;
        sphere.position.z = (Math.random() * 300) - 150;

        sphere.name = node.id();
        return sphere;
    },

    /**
     * This function creates the mesh sphere.
     * @param {String} type
     * @param {String} id
     * @param {String} imgType Type Parameters for a specific image
     * @returns the THREE.Mesh object
     */
    createSprite: function (type, id, imgType) {
        var spriteMap;
        var image = this.getImage(type, function (imgType, evt) {

            if (imgType == 'selected') {
                sprite.scale.set(2, 2, 1);

            } else {
                var step_radius = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.relgraph.node.stepRadius);

                // var img_base_width = 128;
                // var img_base_height = 128;

                // var img_x = evt.target.width;
                // var img_y = evt.target.height;

                // var scale_x = img_base_width / img_x;
                // var scale_y = img_base_height / img_y;
                var scale_x = 1;
                var scale_y = 1;

                scale_x = scale_x * (step_radius / 60);
                scale_y = scale_y * (step_radius / 60);

                sprite.scale.set(scale_x, scale_y, 1);
            }
            spriteMap.needsUpdate = true;
        }, imgType);

        spriteMap = new THREE.Texture(image);
        var spriteMaterial = new THREE.SpriteMaterial({ map: spriteMap }/* { color: 0xffffff } */);
        var sprite = new THREE.Sprite(spriteMaterial);

        sprite.name = id;
        return sprite;
    },

    /**
     * 
     * @param {String} text 
     */
    createLabel: function (text, type) {
        var fontsize = 64;
        var fontface = 'Arial';
        text = text || 'XXXXXXXXXX';

        var canvas = document.createElement('canvas');
        canvas.width = 1;
        canvas.height = 2.75 * fontsize; //2 * fontsize;
        context = canvas.getContext('2d');

        /**
         * Sets the context text parameters;
         * @param {context} context 
         * @param {number} fontsize 
         * @param {SVGFESpecularLightingElement} fontface 
         */
        function setContextText(context, fontsize, fontface) {
            context.font = Ext.String.format("{0}px {1}", fontsize, fontface);
            context.textBaseline = "alphabetic";
            context.textAlign = "left";
            context.fillStyle = '#FFFFFF'; //text color
        }

        //calculates the text metrics
        setContextText(context, fontsize, fontface);
        var metrics = context.measureText(text);

        //set the canvas width 5% longher than the text. Uses ceil to have integetr numbers
        canvas.width = metrics.width + metrics.width * (20 / 100);

        //calculates text position
        var textWidth = metrics.width;
        var cx = canvas.width / 2;
        var cy = canvas.height / 2;
        var tx = textWidth / 2;
        var ty = fontsize / 2;

        //calculates backgroud color of the label
        if (type == 'edge') {
            context.fillStyle = 'rgba(116, 116, 116, 1)';
        } else if (type == 'node') {
            context.fillStyle = 'rgba(0, 92, 168, 1)'; //$basecolor color
        }

        //draws the label backgroud
        context.fillRect(0, 0, canvas.width, canvas.height);

        //Writes the text in the context
        setContextText(context, fontsize, fontface);
        context.fillText(text, cx - tx, cy + ty);

        //creates the sprite label
        var spriteMap = new THREE.CanvasTexture(canvas);
        spriteMap.needsUpdate = true;
        // spriteMap.generateMipmaps = false;

        var spriteMaterial = new THREE.SpriteMaterial({ map: spriteMap, color: 0xffffff, fog: false });
        spriteMaterial.sizeAttenuation = false;

        var sprite = new THREE.Sprite(spriteMaterial);

        //sets the scale. This maintains the correct porportion
        var factor = 16;
        sprite.scale.set((canvas.width / canvas.height) / factor, 1 / factor, 1);

        //sets the position of the label 1 y higher than his parent
        sprite.position.set(0, 1.5, 0);

        //sets the visibility of the label
        sprite.visible = this.allLabelsEnabled;

        sprite.name = Ext.String.format('{0}_{1}', 'label', type);
        return sprite;
    },

    /**
     * This function creates a line rapresenting the edge between two nodes in the graph
     * @param {} edge the cytoscape edge description
     * @returns the mesh.Line
     */
    createLine: function (edge) {
        var source = edge.source();
        var target = edge.target();

        var scene_source = this.scene.getObjectByName(source.id());
        var scene_target = this.scene.getObjectByName(target.id());

        var p1 = scene_source.position;
        var p2 = scene_target.position;

        var line_material = new THREE.LineBasicMaterial({
            color: CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.relgraph.edge.color),
            linewidth: 1
        });
        var line_geometry = new THREE.Geometry();
        // line_geometry.vertices.push(p1, p2);
        line_geometry.vertices.push(scene_source.getWorldPosition(p1), scene_target.getWorldPosition(p2));

        var line = new THREE.Line(line_geometry, line_material);

        line.name = edge.id();
        return line;
    },

    /**
     * This function modifies the scene line associated to the object
     * @param {Mesh.Sprite} scene_object_1 sprite being moved 
     */
    modifyLine: function (scene_object_1) {
        var id1 = scene_object_1.name, id2;
        var cy_node = this.cy.nodes('#' + id1);
        var edges = cy_node.connectedEdges().toArray();

        edges.forEach(function (edge) {
            //get id2 id
            id2 = edge.source().id();
            if (id1 == id2) {
                id2 = edge.target().id();
            }

            var scene_object_2 = this.scene.getObjectByName(id2);
            var scene_line = edge.glLine;

            scene_line.geometry.vertices = [];
            scene_line.geometry.vertices.push(scene_object_1.position, scene_object_2.position);
            scene_line.geometry.verticesNeedUpdate = true;

            label = edge.glLabel;
            label.position.set(
                (scene_object_1.position.x + scene_object_2.position.x) / 2,
                (scene_object_1.position.y + scene_object_2.position.y) / 2,
                (scene_object_1.position.z + scene_object_2.position.z) / 2
            );
        }, this);
    },

    /**
     * @param {String[]} spriteIds
     * @param {[lineIds]} lineIds
     */
    removeObjects: function (spriteIds, lineIds) {
        spriteIds.forEach(function (spriteId) {
            var spriteObj = this.findObjectInScene('name', spriteId);
            if (spriteObj) {
                this.scene.remove(spriteObj);
                delete this.nodeRegister[spriteId];
            } else {
                throw new Error('Is Not Possible, must be here this element with this id');
            }
        }, this);

        lineIds.forEach(function (lineId) {
            var lineObj = this.findObjectInScene('name', lineId);
            if (lineObj) {
                this.scene.remove(lineObj);
                delete this.edgeRegister[lineId];
            } else {
                throw new Error('Is Not possible');
            }
        }, this);
    },
    /**
     * This function adds the drag controls to objects
     * @param {[objects]} objects an array of objects that are draggable
     */
    addDragControl: function (objects) {
        var me = this;
        var dragControls = new THREE.DragControls(objects, this.camera, this.renderer.domElement);

        dragControls.addEventListener('dragstart', function (objectF) {
            me.controls.enabled = false;
            me.modifyLineBool = true;
            // me.tooltipConf.enabled = false;
            me.tooltip.close();
            me.tooltip.pauseStart();
            /**
             * Modifies the edges
             */
            function callTymeOut(object) {
                if (me.modifyLineBool) {
                    setTimeout(function () {
                        me.modifyLine(object);
                        callTymeOut(object);
                    }, 20);
                }
            }

            callTymeOut(objectF.object);
        });
        dragControls.addEventListener('dragend', function () {
            me.controls.enabled = true;
            // me.tooltipConf.enabled = true;
            me.tooltip.pauseEnd();
            delete me.modifyLineBool;
            me.setCameraTarget();
        });
    },

    /**
     * this function handles the click on a node
     */
    addClickControl: function () {
        var me = this;
        this.div.onclick = function () {
            me.raycaster.setFromCamera(me.mouse, me.camera);
            var intersects = me.raycaster.intersectObjects(me.scene.children);

            var j;
            for (var i = 0; i < intersects.length; i++) {
                if (intersects[i].object.type === 'Sprite' || intersects[i].object.type === 'Mesh') {
                    j = i;
                    i = intersects.length;
                }
            }
            if (j !== undefined) {
                me.setSelectedNode([intersects[j].object.name], me.clickCTRL);
            }
        };

        this.div.ondblclick = function (eOpts) {
            // console.log('doubleClick');
            var intersects = me.raycaster.intersectObjects(me.scene.children);
            var doubleclickedNodes = [];

            for (var i = 0; i < intersects.length; i++) {
                if (intersects[i].object.type == 'Mesh' || intersects[i].object.type == 'Sprite') {
                    doubleclickedNodes.push(intersects[i].object.name);
                    i = intersects.lengthl;
                }
            }

            if (doubleclickedNodes.length > 0) {
                Ext.GlobalEvents.fireEventArgs('doubleclicknode', [doubleclickedNodes]);
            }
        };

        this.div.oncontextmenu = function (eOpts) {
            eOpts.preventDefault();
        };

    },

    /**
     * @param {[Number]} ids the Number containing the id of the nodes interessed
     * @param {Boolean} add if true none element will be deleted, if false the old element will be deleted. DEFAULT false;
     */
    setSelectedNode: function (ids, add) {
        add = add || false;
        ids.forEach(function (id) {
            if (this.selectedNode[id]) {
                this.selectedNode[id].deletable = 'persistent';
            } else {
                var object = this.findObjectInScene('name', id);
                this.selectedNode[id] = {
                    object: object,
                    oldColor: this.colorManage('copy', object.material.color),
                    deletable: false
                };
                this.spriteManage('add', id);
                // object.material.color.set(0xFFFF99); //yellow
            }
        }, this);

        var el;
        var selectionIds = [];

        //IMPROVE: this function can be write in a better way
        for (var id in this.selectedNode) {
            if (this.selectedNode.hasOwnProperty(id)) {
                el = this.selectedNode[id];
                if (add === true) {
                    if (el.deletable === 'persistent') {
                        //delete the sprite
                        // this.colorManage('set', el.object, el.oldColor);
                        this.spriteManage('remove', id);
                        delete this.selectedNode[id];
                    } else {
                        el.deletable = true;
                        selectionIds.push(id);
                    }
                } else if (el.deletable === true && !add) {
                    // this.colorManage('set', el.object, el.oldColor);
                    this.spriteManage('remove', id);
                    delete this.selectedNode[id];
                } else {
                    el.deletable = true;
                    selectionIds.push(id);
                }
            }
        }
        Ext.GlobalEvents.fireEventArgs('sceneselectednode', [selectionIds]);
    },

    /**
     * @param {String} action The action to take: add or remove
     * @param {String} id The id of the sprite selected
     */
    spriteManage: function (action, id) {
        var parentSprite;
        var selectedSprite;
        switch (action) {
            case 'add':
                parentSprite = this.findObjectInScene('name', id);

                //finds out if the "selected sprite" is already in his children
                if (parentSprite && parentSprite.children) {
                    selectedSprite = Ext.Array.findBy(parentSprite.children, function (item, index, array) {
                        return item.name == 'selected';
                    });
                }

                //if not adds
                if (!selectedSprite) {
                    var newSprite = this.createSprite(null, null, 'selected');
                    newSprite.name = 'selected';

                    newSprite.position.x = 0;
                    newSprite.position.y = 0;
                    newSprite.position.z = 0;

                    parentSprite.add(newSprite);
                }
                break;
            case 'remove':
                try {
                    parentSprite = this.findObjectInScene('name', id);

                    //finds out if the "selected sprite" is already in his children
                    if (parentSprite && parentSprite.children) {
                        selectedSprite = Ext.Array.findBy(parentSprite.children, function (item, index, array) {
                            return item.name == 'selected';
                        });
                    }

                    //if found remove it
                    if (selectedSprite) {
                        parentSprite.remove(selectedSprite);
                    }

                } catch (err) {
                    // console.log('should Happend only after removing a compound');
                }
                break;
        }
    },

    /**
     * a self made function for finding objects in the scene
     * @param {String} name the name of the param to look for
     * @param {} value the value of the name
     */
    findObjectInScene: function (name, value) {
        var els = this.scene.children;
        var el;
        for (var i = 0; i < els.length; i++) {
            el = els[i];
            if (el[name] == value) {
                return el;
            }
        }

        return undefined;
    },

    /**
     * This function finds the data saved in the cytoscape libraries relate to a specific node
     * @param {String} nodeId the id of the node saved in the cytoscape
     * @param {function} callback the callbackFunction to call
     * @param {Object} scope
     */
    getNodeInformation: function (nodeId, callback, scope) {
        scope = scope || this;
        var data = this.cy.nodes('#' + nodeId).data();
        callback.call(scope, data);

    },

    /**
     * This function finds the data saved in the cytoscape libraries relate to specifics nodes
     * @param {String} edgesId the ids of the nodes saved in the cytoscape
     * @param {function} callback the callbackFunction to call
     * @param {Object} scope
     */
    getEdgeInformation: function (edgesId, callback, scope) {
        var data = [],
            me = this;
        scope = scope || this;
        Ext.Array.each(edgesId, function (item, index, allitems) {
            var element = me.cy.edges('#' + item).data();
            if (index > 0 && (data[0].source != element.source || data[0].target != element.target)) {
                data = Ext.Array.from(data[0]);
                return false;
            } else {
                data.push(element);
            }
        })
        callback.call(scope, data);
    },
    /**
     * This function manages different funtions for the specific color vector
     */
    colorManage: function (op, value1, value2) {
        switch (op) {
            case 'copy':
                return {
                    r: value1.r,
                    b: value1.b,
                    g: value1.g
                };
            case 'set':
                value1.material.color.r = value2.r;
                value1.material.color.b = value2.b;
                value1.material.color.g = value2.g;
                break;
        }
    },
    /**
     * This functions sets the camera targhet in the baricenter of the graph
     */
    setCameraTarget: function () {
        var nodes = this.cy.nodes().toArray();
        var baricenter = this.calculateBaricenter(nodes);
        this.controls.target = baricenter;
    },

    /**
     * This function gets some cyNodes and calculates the baricenter of the structure by its scene position
     * @param {cy.nodes} nodes nodes.glSprite contains the related threejs element
     * @returns the point rapresenting the baricenter of that 
     */
    calculateBaricenter: function (nodes) {
        var sum_x = 0, sum_y = 0, sum_z = 0, length = nodes.length;
        nodes.forEach(function (node) {
            if (node.glSprite) {
                var pos = node.glSprite.position;
                sum_x += pos.x;
                sum_y += pos.y;
                sum_z += pos.z;
            }
        }, this);

        return new THREE.Vector3(sum_x / length, sum_y / length, sum_z / length);
    },



    /**
     * This function returns ann img object
     * @param {String} type
     * @param {Function} onLoad
     */
    getImage: function (type, onLoad, imgType) {
        var image = Ext.dom.Element.create({
            tag: 'img'
        }, 'img');

        image.crossOrigin = "use-credentials";
        image.onload = function (imgType, evt) {
            onLoad.call(this, imgType, evt);
        }.bind(image, imgType);

        imgType = imgType || null;
        switch (imgType) {
            //return the class image if Set. Return Default image;
            case null:
                //Assertion if null imgType --> type != null should allways bew set
                var tmpType = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(type);
                var icon;

                if (tmpType) { // tipo trovato
                    icon = tmpType.get('_icon');
                } else { //tipo non trovato --> compound Node
                    image.src = Ext.getResourcePath('images/compound.png', 'shared');
                    return image;
                }

                if (icon) {
                    image.src = Ext.String.format('{0}/uploads/{1}/download', CMDBuildUI.util.Config.baseUrl, icon);
                    image.onerror = function (e) { //Handles the erro on loading image from server
                        var objectType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(type);
                        switch (objectType) {
                            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                                image.src = Ext.getResourcePath('images/default.png', 'shared');
                                break;
                            case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                                image.src = Ext.getResourcePath('images/process.png', 'shared');
                                break;
                            default:
                                console.error('Should be a type');
                        }
                    }
                } else {

                    var objectType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(type);
                    switch (objectType) {
                        case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                            image.src = Ext.getResourcePath('images/default.png', 'shared');
                            break;
                        case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                            image.src = Ext.getResourcePath('images/process.png', 'shared');
                            break;
                        default:
                            console.error('Should be a type');
                    }
                }
                break;
            //Returnsw allways the default image
            case 'default':
                image.src = Ext.getResourcePath('images/default.png', 'shared')
                break;
            // Return the selected Image
            case 'selected':
                image.src = Ext.getResourcePath('images/selected.png', 'shared');
                break;
            default:
                console.error('Should Never happend');
        }
        return image;
    },
    privates: {
        /**
         * If true the request animationFrame can continue
         */
        renderSceneVariable: true,

        /**
         * Saves tooltip informations
         */
        tooltipConf: {
            /**
             * Coordinates of the mouse
             */
            mouseX: null,
            mouseY: null,
            enable: true
        },

        allLabelsEnabled: false,

        /**
         * Saves the information about the selected Node in sceneJS
         */
        selectedNode: {},

        /**
         * 
         */
        clickCTRL: false,

        /**
         * This object saves the cytoscape id of the node that have a related three.js sphere
         * {1234: true,
         * 1233: true}
         * 
         * Is't easy to tell if a node has been created or not
         * TODO: replace the findInSceneFunction
         */
        nodeRegister: {},

        /**
         * This object saves the cytoscape id of the edge that have a related three.js line
         * {1234: true,
         * 1233: true}
         * 
         * Is't easy to tell if a node has been created or not
         */
        edgeRegister: {},

        /**
         * a structure containing type names and icons urls
         */
        mapTypeUrl: {},

        /**
         * this is a class wich handless the tooltip
         */
        tooltipContext: function (tooltipConf, text) {
            this.text = text || '';
            this.panel = null;
            this.enable = tooltipConf.enable;
            this.pause = tooltipConf.pause || false;
            this.tooltipConf = tooltipConf;

            this.init = function () {
                this.panel = Ext.create('Ext.panel.Panel', {
                    width: 200,
                    renderTo: Ext.getBody(),
                    hidden: true
                });
                this.panel.ariaEl.dom.style.zIndex = 190000;
            };

            // this.setHeader = function (header) {
            //     if (this.panel && this.enable === true && !(this.pause === true)) {
            //         this.header = header;
            //         this.panel.setTitle(this.header);
            //     }
            // };

            /**
             * 
             * @param {String} mode node or relation
             * @param {Object} config 
             */
            this.setText = function (mode, config) {
                this.panel.setHtml();
                switch (mode) {
                    case 'node':
                        this.panel.setHtml(
                            Ext.String.format('<div class="relgraph-tip relgraph-tip-node">{0}: {1}',
                                config.id.includes('compound') ? CMDBuildUI.locales.Locales.relationGraph.compoundnode : CMDBuildUI.util.helper.ModelHelper.getObjectDescription(config.type),
                                config.description
                            )
                        );
                        break;
                    case 'relation':

                        function createLabel(relation) {
                            var attributes;
                            if (relation.relationAttributes) {
                                attributes = '<div class="relgraph-tip relgraph-tip-attributes">';
                                var keys = Ext.Object.getKeys(relation.relationAttributes);
                                Ext.Array.forEach(keys, function (item, index, allitems) {
                                    var value = relation.relationAttributes[item];
                                    if (value) {
                                        attributes = attributes.concat(Ext.String.format('<b>{0}</b>:<br>{1}<br>', item, value));
                                    }
                                });
                                attributes = attributes.concat("</div>");
                            }

                            return Ext.String.format('<div class="relgraph-tip relgraph-tip-relation">{0}: {1}</br><b>{2}</b></br>{3}: {4}{5}</div>',
                                CMDBuildUI.util.helper.ModelHelper.getObjectDescription(relation.sourceType),
                                relation.sourceDescription,
                                relation.isDirect ? relation.descriptionDirect : relation.descriptionInverse,
                                CMDBuildUI.util.helper.ModelHelper.getObjectDescription(relation.destinationType),
                                relation.destinationDescription,
                                attributes
                            );
                        }

                        var label = '';
                        Ext.Array.forEach(config, function (item, index, allitems) {
                            if (index > 0) {
                                label = label.concat('<hr>');
                            }
                            label = label.concat(createLabel(item));
                        });

                        this.panel.setHtml(label);
                        break;
                }
            };

            this.setEnable = function (enable) {
                this.enable = enable;

                if (enable === false) {
                    this.destroy();
                }
            };

            this.getEnable = function () {
                return this.enable;
            };

            this.pauseStart = function () {
                this.pause = true;
            };

            this.pauseEnd = function () {
                this.pause = false;
            };

            this.open = function () {
                if (this.panel && this.enable === true && !(this.pause === true)) {
                    this.panel.setVisible(true);
                    this.position();
                }
            };

            this.close = function () {
                if (this.panel && this.enable === true && !(this.pause === true)) {
                    this.panel.setVisible(false);
                }
            };

            this.destroy = function () {
                if (this.panel && this.enable === true) {
                    this.panel.destroy();
                }
            };

            this.position = function () {
                var left = this.tooltipConf.mouseX;
                var top = this.tooltipConf.mouseY;

                if (this.panel && this.panel.ariaEl.dom && this.enable === true && !(this.pause === true)) {
                    this.panel.ariaEl.dom.style.top = (top - this.panel.getHeight() - 20) + "px";
                    this.panel.ariaEl.dom.style.left = (left - (this.panel.getWidth() / 2)) + "px";
                }
            };

            this.isOpen = function () {
                if (this.panel) {
                    return this.panel.isVisible();
                }
            };

            this.init();
            this.setText('Hola');
            this.position();
        }
    }
});