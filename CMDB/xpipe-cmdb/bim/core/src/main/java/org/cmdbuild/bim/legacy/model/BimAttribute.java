package org.cmdbuild.bim.legacy.model;

public interface BimAttribute {

	final BimAttribute NULL_ATTRIBUTE = new BimAttribute() {

		@Override
		public String getName() {
			return null;
		}

		@Override
		public boolean isValid() {
			return false;
		}

		@Override
		public String getValue() {
			return "";
		}

		@Override
		public void setValue(final String value) {
		}

	};

	String getName();

	boolean isValid();

	String getValue();

	void setValue(String value);

}
