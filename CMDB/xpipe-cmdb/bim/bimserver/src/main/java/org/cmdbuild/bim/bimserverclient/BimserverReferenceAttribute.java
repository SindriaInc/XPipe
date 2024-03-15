package org.cmdbuild.bim.bimserverclient;

import org.bimserver.emf.IdEObject;

public class BimserverReferenceAttribute extends BimserverAttribute implements ReferenceAttribute {

	private final BimserverEntity referencedEntity;

	BimserverReferenceAttribute(final String name, final IdEObject value) {
		super(name, value);
		referencedEntity = new BimserverEntity(value);
	}

	@Override
	public String getGlobalId() {
		return referencedEntity.getGlobalId();
	}

	@Override
	public long getOid() {
		return referencedEntity.getOid();
	}

	@Override
	public String getTypeName() {
		return getDatavalue().getClass().getSimpleName();
	}

	@Override
	public String getValue() {
		return getGlobalId();
	}

}
