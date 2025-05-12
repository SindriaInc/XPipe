package org.cmdbuild.workflow.xpdl;

import org.cmdbuild.workflow.model.SimplePlanExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttribute;

public class XpdlExtendedAttribute extends SimplePlanExtendedAttribute {

	public XpdlExtendedAttribute(String key, String value) {
		super(key, value);
	}

	public static XpdlExtendedAttribute newInstance(final ExtendedAttribute xa) {
		if (xa == null) {
			return null;
		} else {
			return new XpdlExtendedAttribute(xa.getName(), xa.getVValue());
		}
	}
}
