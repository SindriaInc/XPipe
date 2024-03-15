Ext.define('CMDBuildUI.graph.threejs.GuiSphere', {
    singleton: true,
    /**
     * @param {Scene} scene the THREE scene
     * @param {cytoscape} cy the array containing the new cytoscape nodes  
     */
    run: function (scene, cy, camera) {
        this.visited = {};
        this.scene = scene;
        this.color = 0;

        var cyRoot = cy.nodes('[isRoot]').toArray()[0];
        if (!cyRoot) return;

        this._recursiveChildVisit(cyRoot, function (cyParent, cyNode) {
            // console.log(cyParent, cyNode);
        });

        CMDBuildUI.graph.threejs.SceneUtils.setCameraTarget();
        this.visited = {};
        delete this.color;
        // this.fitToScreen(camera);
    },

    /**
     * @param {Element} cyNode the cytoscape node being analized
     * @param {Function} apply the function to apply on each visited node (included the first one)
     */
    _recursiveChildVisit: function (cyNode, apply) {
        this.visited[cyNode.id()] = { visited: true };
        // var color = this.color;
        // this.color++;

        var outGoers = cyNode.outgoers('node').toArray();
        outGoers = this._removeVisited(outGoers);
        // apply(parent, cyNode);

        var nChilds = outGoers.length;
        // var nRows = this.calculateRows(nChilds);
        // var nCols = this.calculateCols(nChilds);
        // var stepRadius = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.relgraph.node.stepRadius);
        stepRadius = 7;
        // stepRadius = this.hasOutgoers(outGoers) ? stepRadius * 2 : stepRadius;

        // for (var i = 0; i < outGoers.length; i++) {
        //     var node = outGoers[i];
        // for (var r = 0; r < nRows; r++) {
        //     for (var c = 0; c < nCols; c++) {
        //         var node = outGoers[r * nCols + c];
        //         if (!node) return;

        //         /* // var phi = Math.acos(-1 + (2 * r) / nRows);
        //         var theta = ((-Math.PI / 2) + (Math.PI / nRows) * r) + (Math.PI / nRows) / 2;
        //         // var theta = (Math.PI / nRows) * r + ((Math.PI / nRows) / 2);
        //         var phi = ((2 * Math.PI) / nCols) * c; */

        //         var phi = Math.acos(-1 + (2 * (r * nCols + c)) / (nChilds));
        //         var theta = Math.sqrt((nChilds) * Math.PI) * phi;
        //         // console.log(r * nCols + c, phi, theta);

        //         threejsObj = node.glSprite; //TODO: modify in glSprite
        //         threejsObj.position.setFromSphericalCoords(stepRadius, phi, theta);
        //         threejsObj.position.addVectors(threejsObj.position, cyNode.glSprite.position);

        //         this._changeObjectColor(threejsObj.material.color, color);

        //         console.log(threejsObj.position, threejsObj);

        //         if (this.hasOutgoers([node])) {
        //             this._recursiveChildVisit(node, apply);
        //         }
        //     }
        // }
        var parentPhi = Math.atan(cyNode.glSprite.position.y / cyNode.glSprite.position.x) || 0;
        for (var i = 0; i < nChilds; i++) {
            var node = outGoers[i];
            this.visited[node.id()] = { visited: true };
            var stepRadiusN = this.hasOutgoers([node]) && this.singleOccurence(node.outgoers('node').toArray(), outGoers) ? stepRadius : (stepRadius / 2);

            if (!node) return;

            var phi = Math.acos(-1 + (2 * i) / (nChilds));//+ (Math.PI/nChilds)/2);
            var theta = Math.sqrt((nChilds) * Math.PI) * phi;

            threejsObj = node.glSprite; //TODO: modify in glSprite
            threejsObj.position.setFromSphericalCoords(stepRadiusN, phi, theta);
            threejsObj.position.addVectors(threejsObj.position, cyNode.glSprite.position);

            // HACK: adds some helper to visualize better the sprites
            // var box = new THREE.BoxHelper(threejsObj, 0x000000);
            // this.scene.add(box)

            // console.log(i, node.id(), threejsObj.position)
            // this._changeObjectColor(threejsObj.material.color, color);
        }
        CMDBuildUI.graph.threejs.SceneUtils.modifyLine(cyNode.glSprite);

        for (var j = 0; j < nChilds; j++) {
            var nodeJ = outGoers[j];
            if (this.hasOutgoers([nodeJ])) {
                this._recursiveChildVisit(nodeJ, apply);
            }
        }
    },

    fitToScreen: function (camera) {
        var boundingBox = new THREE.Box3();
        boundingBox.setFromObject(this.scene);

        // const center = boundingBox.getCenter(new THREE.Vector3());
        var size = boundingBox.getSize(new THREE.Vector3());
        var offset = 1;

        var max = Math.max(Math.max(size.x, size.y), size.z) + offset;
        var dist = max / 2 / Math.tan(Math.PI * camera.fov / 360);
        camera.position.z = dist;
    },

    _changeObjectColor: function (vector, color) {
        switch (color) {
            case 0:
                vector.r = 255;
                vector.b = 0;
                vector.g = 0;
                break;
            case 1:
                vector.r = 0;
                vector.b = 0;
                vector.g = 255;
                break;
            case 2:
                vector.r = 0;
                vector.b = 255;
                vector.g = 0;
                break;
            default:
                // console.log("COROUR END");
        }
    },

    /**
     * This function removes from the given array the elements that has yet been visited
     * This information is stored in this.visited[$nodeId].visited
     * @param {[Element]} array of cy nodes
     * @returns {[Element]} the elemets filtered
     */
    _removeVisited: function (array) {
        var dup = [];
        array.forEach(function (el) {
            if (!this.visited[el.id()] || !this.visited[el.id()].visited) {
                dup.push(el);
            } else {
                // console.log('not entered here');
            }
        }, this);
        return dup;
    },

    /**
     * This function tells if exixt elements in an array wich doesnt exist in the second
     * @param {[Element]} array1
     * @param {[Element]} array2
     * @returns {Boolean} true if not Exist i such as array[i] = a && for each l array[n] != a
     */
    singleOccurence: function (array1, array2) {
        var bool = false;
        for (var i = 0; i < array1.length; i++) {
            bool = true;
            for (var j = 0; j < array2.length; j++) {
                if (array1[i].id() === array2[j].id()) {
                    bool = false;
                    j = array2.length;
                }
            }
            if (bool === true) {
                return true;
            }
        }
        return false;
    },
    /**
     * @param {[Element]} nodes array of cy nodes
     * @returns {Boolean} True if any element in nodes have outgoers
     */
    hasOutgoers: function (nodes) {
        for (var i = 0; i < nodes.length; i++) {
            if (nodes[i].outgoers('node').length != 0) {
                return true;
            }
        }
        return false;
    },
    /**
     * @param {Number} n
     */
    calculateCols: function (n) {
        //pavimento di radice di n
        var col = Math.floor(Math.sqrt(n));
        var row = this.calculateRows(n);

        if (col * row < n)++col;
        return col;
    },

    /**
     * @param {Number}
     */
    calculateRows: function (n) {
        //soffito di radice di n
        n = Math.sqrt(n);
        return Math.ceil(n);
    }
});