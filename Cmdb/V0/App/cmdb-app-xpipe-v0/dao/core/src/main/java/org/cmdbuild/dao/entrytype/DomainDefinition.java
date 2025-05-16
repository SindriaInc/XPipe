package org.cmdbuild.dao.entrytype;

import javax.annotation.Nullable;

public interface DomainDefinition {

	@Nullable
	Long getOid();

	String getName();

	DomainMetadata getMetadata();

	String getSourceClassName();

	String getTargetClassName();
}
