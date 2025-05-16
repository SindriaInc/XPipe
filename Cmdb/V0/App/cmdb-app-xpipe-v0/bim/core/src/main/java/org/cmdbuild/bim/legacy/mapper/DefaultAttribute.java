package org.cmdbuild.bim.legacy.mapper;

import org.cmdbuild.bim.legacy.model.BimAttribute;

public class DefaultAttribute implements BimAttribute {

	private final String name;
	private String value;

	private DefaultAttribute(final String name, final String value) {
		this.name = name;
		this.value = value;
	}

	public static DefaultAttribute withNameAndValue(final String name, final String value) {
		return new DefaultAttribute(name, value);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return name + ": " + value;
	}

}
