package org.cmdbuild.bim.legacy.model.implementation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.bim.legacy.model.AttributeDefinition;
import org.cmdbuild.bim.legacy.model.EntityDefinition;

public class ImportEntityDefinition implements EntityDefinition {

	private final String ifcType;
	private String cmClass;
	List<AttributeDefinition> attributes;

	public ImportEntityDefinition(final String name) {
		attributes = new ArrayList<>();
		cmClass = StringUtils.EMPTY;
		this.ifcType = name;
	}

	@Override
	public String getIfcType() {
		return ifcType;
	}

	@Override
	public void setCmClass(final String label) {
		this.cmClass = label;
	}

	@Override
	public String getCmClass() {
		return cmClass;
	}

	@Override
	public List<AttributeDefinition> getAttributes() {
		return attributes;
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
