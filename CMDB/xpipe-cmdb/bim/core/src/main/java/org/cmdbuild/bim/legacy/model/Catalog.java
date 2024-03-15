package org.cmdbuild.bim.legacy.model;

public interface Catalog {

	Iterable<EntityDefinition> getEntitiesDefinitions();

	@Override
	String toString();

	int getSize();
}
