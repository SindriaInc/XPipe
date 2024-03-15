package org.cmdbuild.bim.bimserverclient;

import org.cmdbuild.bim.legacy.model.BimAttribute;

public interface ReferenceAttribute extends BimAttribute {

	String getGlobalId();

	long getOid();

	String getTypeName();

}
