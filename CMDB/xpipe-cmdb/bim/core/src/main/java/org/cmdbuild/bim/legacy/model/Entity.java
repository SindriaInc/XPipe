package org.cmdbuild.bim.legacy.model;

import java.util.Map;

import org.cmdbuild.bim.utils.BimConstants;

public interface Entity {

	public static final String KEYATTRIBUTE = BimConstants.IFC_GLOBALID;

	final Entity NULL_ENTITY = new Entity() {

		@Override
		public boolean isValid() {
			return false;
		}

		@Override
		public Map<String, BimAttribute> getAttributes() {
			return null;
		}

		@Override
		public BimAttribute getAttributeByName(final String attributeName) {
			return null;
		}

		@Override
		public String getKey() {
			return "";
		}

		@Override
		public String getTypeName() {
			return "";
		}

		@Override
		public String toString() {
			return "NULL_ENTITY";
		}

		@Override
		public String getGlobalId() {
			return "";
		}

	};

	boolean isValid();

	Map<String, BimAttribute> getAttributes();

	BimAttribute getAttributeByName(String attributeName);

	String getKey();

	String getTypeName();
	
	String getGlobalId();

}
