Ext.define('CMDBuildUI.util.bim.PreloadQuery', {
    singleton: true,

    preloadQuery_ifc2x3tc1: {
        defines: {
            Representation: {
                type: "IfcProduct",
                fields: ["Representation", "geometry"]
            },
            ContainsElementsDefine: {
                type: "IfcSpatialStructureElement",
                field: "ContainsElements",
                include: {
                    type: "IfcRelContainedInSpatialStructure",
                    field: "RelatedElements",
                    includes: [
                        "IsDecomposedByDefine",
                        "ContainsElementsDefine",
                        "Representation"
                    ]
                }
            },
            IsDecomposedByDefine: {
                type: "IfcObjectDefinition",
                field: "IsDecomposedBy",
                include: {
                    type: "IfcRelDecomposes",
                    field: "RelatedObjects",
                    includes: [
                        "IsDecomposedByDefine",
                        "ContainsElementsDefine",
                        "Representation"
                    ]
                }
            }
        },
        queries: [
            {
                type: "IfcProject",
                includes: [
                    "IsDecomposedByDefine",
                    "ContainsElementsDefine"
                ]
            },
            {
                type: {
                    name: "IfcRepresentation",
                    includeAllSubTypes: true
                }
            },
            {
                type: {
                    name: "IfcRepresentationItem",
                    includeAllSubtypes: true
                }
            },
            {
                type: {
                    name: "IfcProductRepresentation",
                    includeAllSubtypes: true
                }
            },
            {
                type: "IfcPresentationLayerWithStyle"
            },
            {
                type: {
                    name: "IfcProduct",
                    includeAllSubtypes: true
                }
            },
            {
                type: "IfcProductDefinitionShape"
            },
            {
                type: "IfcPresentationLayerAssignment"
            },
            {
                type: "IfcRelAssociatesClassification",
                includes: [
                    {
                        type: "IfcRelAssociatesClassification",
                        field: "RelatedObjects"
                    },
                    {
                        type: "IfcRelAssociatesClassification",
                        field: "RelatingClassification"
                    }
                ]
            },
            {
                type: "IfcSIUnit"
            },
            {
                type: "IfcPresentationLayerAssignment"
            }
        ]
    },
    preloadQuery_ifc4: {
        defines: {
            Representation: {
                type: "IfcProduct",
                field: "Representation"
            },
            ContainsElementsDefine: {
                type: "IfcSpatialStructureElement",
                field: "ContainsElements",
                include: {
                    type: "IfcRelContainedInSpatialStructure",
                    field: "RelatedElements",
                    includes: [
                        "IsDecomposedByDefine",
                        "ContainsElementsDefine",
                        "Representation"
                    ]
                }
            },
            IsDecomposedByDefine: {
                type: "IfcObjectDefinition",
                field: "IsDecomposedBy",
                include: {
                    type: "IfcRelAggregates",
                    field: "RelatedObjects",
                    includes: [
                        "IsDecomposedByDefine",
                        "ContainsElementsDefine",
                        "Representation"
                    ]
                }
            }
        },
        queries: [
            {
                type: "IfcProject",
                includes: [
                    "IsDecomposedByDefine",
                    "ContainsElementsDefine"
                ]
            },
            {
                type: "IfcRepresentation",
                includeAllSubtypes: true
            },
            {
                type: "IfcProductRepresentation"
            },
            {
                type: "IfcPresentationLayerWithStyle"
            },
            {
                type: "IfcProduct",
                includeAllSubtypes: true
            },
            {
                type: "IfcProductDefinitionShape"
            },
            {
                type: "IfcPresentationLayerAssignment"
            },
            {
                type: "IfcRelAssociatesClassification",
                includes: [
                    {
                        type: "IfcRelAssociatesClassification",
                        field: "RelatedObjects"
                    },
                    {
                        type: "IfcRelAssociatesClassification",
                        field: "RelatingClassification"
                    }
                ]
            },
            {
                type: "IfcSIUnit"
            },
            {
                type: "IfcPresentationLayerAssignment"
            }
        ]
    },
    get : function(schema) {
        return (schema === "ifc2x3tc1") ? this.preloadQuery_ifc2x3tc1 : this.preloadQuery_ifc4;
    }
});