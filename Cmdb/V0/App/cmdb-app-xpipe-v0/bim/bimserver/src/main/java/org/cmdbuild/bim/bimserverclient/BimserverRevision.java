package org.cmdbuild.bim.bimserverclient;

import java.util.Date;

import org.bimserver.interfaces.objects.SRevision;

public class BimserverRevision implements BimRevision {

	private final SRevision revision;

	protected BimserverRevision(final SRevision revision) {
		this.revision = revision;
	}

	@Override
	public String getProjectId() {
		return Long.toString(revision.getProjectId());
	}

	@Override
	public Date getDate() {
		return revision.getDate();
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
