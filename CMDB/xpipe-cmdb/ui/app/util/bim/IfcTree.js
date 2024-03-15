Ext.define('CMDBuildUI.util.bim.IfcTree', {
    singleton: true,

    /**
     * 
     * @param restoreRoot when true the ifcTreeRoot is set as an ampty array
     */
    _init: function (project, restoreRoot) {
        if (restoreRoot) {
            this.ifcTreeRoot = [];
        }
        this.ifcObjectArray = [];
        this.getIfcObjectRecursive(this.ifcTreeRoot, project);
    },

    /**
     * @requires {[Object]} the ifc tree root
     */
    getIfcRoot: function () {
        return this.ifcTreeRoot;
    },

    /**
     * 
     */
    getIfcObjectRecursive: function (arParents, ifcObject) {
        var me = this;
        this.ifcObjectArray.push(ifcObject);
        var currentObject = {
            ifcObject: ifcObject,
            children: []
        };
        arParents.push(currentObject);
        if (ifcObject.getType() == "IfcBuildingStorey" || ifcObject.getType() == "IfcSpace") { //don't kwnow if is useful the second statment in the expression
            ifcObject.getContainsElements(
                function (relReferencedInSpatialStructure) {
                    relReferencedInSpatialStructure.getRelatedElements(
                        function (relatedElement) {
                            me.processRelatedElement(currentObject.children, relatedElement);
                        }
                    ).done(
                        function () {
                            ifcObject.getIsDecomposedBy(
                                function (isDecomposedBy) {
                                    if (isDecomposedBy != null) {
                                        isDecomposedBy.getRelatedObjects(
                                            function (relatedObject) {
                                                me.processRelatedElement(currentObject.children, relatedObject);
                                            }
                                        );
                                    }
                                }
                            );
                        }
                    );
                }
            );
        } else {
            if (ifcObject.getContainsElements != null) {
                ifcObject.getContainsElements(function (containedElement) {
                    containedElement.getRelatedElements(function (relatedElement) {
                        me.getIfcObjectRecursive(currentObject.children, relatedElement);
                    });
                });
            }
            ifcObject.getIsDecomposedBy(function (isDecomposedBy) {
                if (isDecomposedBy != null) {
                    isDecomposedBy.getRelatedObjects(function (relatedObject) {
                        me.getIfcObjectRecursive(currentObject.children, relatedObject);
                    });
                }
            });
        }
    },

    /**
     *@param {[Object]} arParents
     *@param {Object} relatedElement 
     */
    processRelatedElement: function (arParents, relatedElement) {
        this.getIfcObjectRecursive(arParents, relatedElement);
    },

    /**
     * @returns local variable containing ifcObjectArray 
     */
    getIfcObjectArray: function () {
        return this.ifcObjectArray;
    },

    /**
     * 
     */
    privates: {
        /**
         * the structure for the ifcTree
         */
        ifcTreeRoot: [],

        /**
         * {[Object] 
         */
        ifcObjectArray: undefined
    }
});