package org.cmdbuild.bim.legacy.model.implementation;

import org.cmdbuild.bim.legacy.model.EntityDefinition;

public class SimpleAttributeDefinition extends DefaultAttributeDefinition {

	public SimpleAttributeDefinition(final String attributeName) {
		super(attributeName);
	}

	private String value = "";

	@Override
	public EntityDefinition getReference() {
		return EntityDefinition.NULL_ENTITYDEFINITION;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return this.getIfcName();
	}

}
