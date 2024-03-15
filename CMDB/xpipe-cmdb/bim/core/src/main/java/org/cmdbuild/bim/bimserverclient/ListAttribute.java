package org.cmdbuild.bim.bimserverclient;

import java.util.List;

import org.cmdbuild.bim.legacy.model.BimAttribute;

public interface ListAttribute extends BimAttribute {

	List<BimAttribute> getValues();

}
