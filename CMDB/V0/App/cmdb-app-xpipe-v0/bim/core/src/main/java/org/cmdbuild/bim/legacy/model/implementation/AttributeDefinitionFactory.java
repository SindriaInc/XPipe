package org.cmdbuild.bim.legacy.model.implementation;

import org.cmdbuild.bim.legacy.model.AttributeDefinition;
import org.cmdbuild.bim.BimException;

public class AttributeDefinitionFactory {

	private final String type;

	public AttributeDefinitionFactory(final String type) {
		this.type = type;
	}

	public AttributeDefinition createAttribute(final String attributeName) {
		if (type.equals("simple")) {
			return new SimpleAttributeDefinition(attributeName);
		} else if (type.equals("reference")) {
			return new ReferenceAttributeDefinition(attributeName);
		} else if (type.equals("list")) {
			return new ListAttributeDefinition(attributeName);
		} else {
			throw new BimException("Unsupported attribute type " + this.type);
		}
	}

}
