package org.cmdbuild.bim.bimserverclient;

public class BimserverSimpleAttribute extends BimserverAttribute implements SimpleAttribute {

	BimserverSimpleAttribute(final String name, final Object datavalue) {
		super(name, datavalue);
	}

	@Override
	public String getStringValue() {
		return getDatavalue().toString();
	}

}
