package org.cmdbuild.bim.legacy.model;

import java.util.List;

public interface EntityDefinition {

	String getIfcType();

	List<AttributeDefinition> getAttributes();

	boolean isValid();

	String getCmClass();

	void setCmClass(String className);

	final EntityDefinition NULL_ENTITYDEFINITION = new EntityDefinition() {

		@Override
		public String getIfcType() {
			return "";
		}

		@Override
		public String getCmClass() {
			return "";
		}

		@Override
		public List<AttributeDefinition> getAttributes() {
			return null;
		}

		@Override
		public boolean isValid() {
			return false;
		}

		@Override
		public void setCmClass(final String label) {
		}

	};

}
