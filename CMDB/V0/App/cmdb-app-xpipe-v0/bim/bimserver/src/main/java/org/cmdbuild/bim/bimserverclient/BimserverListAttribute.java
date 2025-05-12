package org.cmdbuild.bim.bimserverclient;

import java.util.ArrayList;
import java.util.List;

import org.bimserver.emf.IdEObject;
import org.eclipse.emf.common.util.EList;
import org.cmdbuild.bim.legacy.model.BimAttribute;

public class BimserverListAttribute extends BimserverAttribute implements ListAttribute {

	protected BimserverListAttribute(final String name, final EList value) {
		super(name, value);
	}

	@Override
	public List<BimAttribute> getValues() {
		final EList<Object> datavalues = ((EList<Object>) getDatavalue());
		final List<BimAttribute> values = new ArrayList<BimAttribute>();
		for (final Object datavalue : datavalues) {
			if (datavalue instanceof EList) {
				final BimAttribute attribute = new BimserverListAttribute(name, (EList) datavalue);
				values.add(attribute);
			} else if (datavalue instanceof IdEObject) {
				final BimAttribute attribute = new BimserverReferenceAttribute(name, (IdEObject) datavalue);
				values.add(attribute);
			} else {
				final BimAttribute attribute = new BimserverSimpleAttribute(name, datavalue);
				values.add(attribute);
			}
		}
		return values;
	}

}
