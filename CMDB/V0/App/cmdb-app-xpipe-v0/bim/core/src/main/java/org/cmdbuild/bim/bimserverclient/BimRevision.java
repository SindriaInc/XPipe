package org.cmdbuild.bim.bimserverclient;

import java.util.Date;

public interface BimRevision {

	String getProjectId();

	Date getDate();

	boolean isValid();

	public static BimRevision NULL_REVISION = new BimRevision() {

		@Override
		public String getProjectId() {
			return null;
		}

		@Override
		public Date getDate() {
			return null;
		}

		@Override
		public boolean isValid() {
			return false;
		}
	};

}
