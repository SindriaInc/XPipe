package org.cmdbuild.bim.bimserverclient;

import org.cmdbuild.bim.BimException;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bimserver.emf.IdEObject;
import org.cmdbuild.bim.legacy.model.Entity;
import org.eclipse.emf.common.util.EList;
import org.cmdbuild.bim.legacy.model.BimAttribute;

public class BimserverEntity implements Entity {

	private static final String IFC_LABEL = "IfcLabel";
	private static final String IFC_VALUE = "IfcValue";
	private static final String ORG_BIMSERVER_MODELS_IFC4 = "org.bimserver.models.ifc4";
	private static final String ORG_BIMSERVER_MODELS_IFC2X3TC1 = "org.bimserver.models.ifc2x3tc1";
	private static final String WRAPPED_VALUE = "getWrappedValue";
	private final IdEObject bimserverDataObject;

	public BimserverEntity(final IdEObject object) {
		this.bimserverDataObject = object;
	}

	@Override
	public boolean isValid() {
		return (bimserverDataObject != null);
	}

	@Override
	public Map<String, BimAttribute> getAttributes() {
		throw new UnsupportedOperationException();
	}

	private String packageName() {
		if (bimserverDataObject.getClass().getPackage().getName().equals("org.bimserver.models.ifc2x3tc1.impl")) {
			return ORG_BIMSERVER_MODELS_IFC2X3TC1;
		} else if (bimserverDataObject.getClass().getPackage().getName().equals("org.bimserver.models.ifc4.impl")) {
			return ORG_BIMSERVER_MODELS_IFC4;
		} else {
			throw new UnsupportedOperationException();
		}
	}

	private boolean ifc2x3() {
		return packageName().equals(ORG_BIMSERVER_MODELS_IFC2X3TC1);
	}

	private boolean ifc4() {
		return packageName().equals(ORG_BIMSERVER_MODELS_IFC4);
	}

	@Override
	public BimAttribute getAttributeByName(final String attributeName) {

		BimAttribute attribute = BimAttribute.NULL_ATTRIBUTE;

		try {
			final String[] split = StringUtils.split(attributeName, "_");
			final String callerClass = String.format("%s.%s", packageName(), split[0]);
			final String methodName = String.format("get%s", split[1]);
			final Class<?> ifcEntityClass = Class.forName(callerClass);
			final Method method = ifcEntityClass.getDeclaredMethod(methodName);

			if (!ifcEntityClass.isInstance(bimserverDataObject)) {
				return attribute;
			}

			Object response = method.invoke(bimserverDataObject);

			if (Class.forName(packageName() + "." + IFC_VALUE).isInstance(response)
					|| Class.forName(packageName() + "." + IFC_LABEL).isInstance(response)) {
				final Method declaredMethod = response.getClass().getDeclaredMethod(WRAPPED_VALUE);
				response = declaredMethod.invoke(response);
			}

			if (response != null) {
				if (response instanceof IdEObject) {
					attribute = new BimserverReferenceAttribute(attributeName, IdEObject.class.cast(response));
				} else if (response instanceof EList) {
					attribute = new BimserverListAttribute(attributeName, EList.class.cast(response));
				} else {
					attribute = new BimserverSimpleAttribute(attributeName, response);
				}
			}
		} catch (final Throwable t) {
			throw new BimException("error getting attribute " + attributeName + " of " + getTypeName(), t);
		}
		return attribute;
	}

	@Override
	public String getKey() {
		String key = String.valueOf(bimserverDataObject.getOid());
		if (ifc2x3() && bimserverDataObject instanceof org.bimserver.models.ifc2x3tc1.IfcRoot) {
			key = org.bimserver.models.ifc2x3tc1.IfcRoot.class.cast(bimserverDataObject).getGlobalId();
		} else if (ifc4() && bimserverDataObject instanceof org.bimserver.models.ifc4.IfcRoot) {
			key = org.bimserver.models.ifc4.IfcRoot.class.cast(bimserverDataObject).getGlobalId();
		}
		return key;
	}

	public Long getOid() {
		return bimserverDataObject.getOid();
	}

	@Override
	public String getTypeName() {
		return bimserverDataObject.getClass().getSimpleName();
	}

	@Override
	public String toString() {
		return bimserverDataObject.getClass().getSimpleName() + " " + getKey();
	}

	@Override
	public String getGlobalId() {
		String key = EMPTY;
		if (ifc2x3() && bimserverDataObject instanceof org.bimserver.models.ifc2x3tc1.IfcRoot) {
			key = org.bimserver.models.ifc2x3tc1.IfcRoot.class.cast(bimserverDataObject).getGlobalId();
		} else if (ifc4() && bimserverDataObject instanceof org.bimserver.models.ifc4.IfcRoot) {
			key = org.bimserver.models.ifc4.IfcRoot.class.cast(bimserverDataObject).getGlobalId();
		}
		return key;
	}

}
