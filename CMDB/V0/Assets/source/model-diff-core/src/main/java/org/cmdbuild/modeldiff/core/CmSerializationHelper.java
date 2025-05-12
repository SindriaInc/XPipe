/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.core;

/**
 * Names used in CMDBuild serialization.
 * 
 * @author afelice
 */
public interface CmSerializationHelper {

    // Data
    public static final String ATTR_ID_SERIALIZATION = "_id";
    public static final String ATTR_IDCLASS_SERIALIZATION = "_type";
    
    // Schema
    String ATTR_ACTIVE_SERIALIZATION = "active"; // to handle deactivation of Attribute/Classe/Process/Domain
    String ATTR_CODE_SERIALIZATION = "code";
    String ATTR_DESCRIPTION_SERIALIZATION = "description";
    String ATTR_INHERITED_SERIALIZATION = "inherited"; // to handle attributes inherited from a super Classe
    String ATTR_LOOKUP_TYPE_REFERENCE_SERIALIZATION = "lookupValues";
    String ATTR_LOOKUP_VALUES_SERIALIZATION = "values";
    String ATTR_METADATA_SERIALIZATION = "metadata"; // to handle metadata modified in attributes inherited from a super Classe
    String ATTR_METADATA_SUPERCLASS_SERIALIZATION = "";
    String ATTR_NAME_SERIALIZATION = "name";
    String ATTR_PARENT_SERIALIZATION = "parent"; // to handle topological sorting of Classe and LookupType
    String ATTR_REFERENCED_CLASSE_SERIALIZATION = "targetClass"; // to handle deactivation of reference/foreign key Attributes
    String ATTR_TYPE_SERIALIZATION = "type"; // to handle deactivation of reference/foreign key Attributes
    
}
