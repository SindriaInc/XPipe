package org.cmdbuild.easytemplate.store;

import javax.annotation.Nullable;

public interface Easytemplate {

	@Nullable
	Long getId();

	String getKey();

	String getValue();
}
