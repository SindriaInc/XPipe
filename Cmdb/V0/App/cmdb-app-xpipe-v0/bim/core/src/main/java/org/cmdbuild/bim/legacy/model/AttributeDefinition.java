package org.cmdbuild.bim.legacy.model;

public interface AttributeDefinition {

	String getIfcName();

	String getCmName();

	void setCmName(String label);

	EntityDefinition getReference();

}
