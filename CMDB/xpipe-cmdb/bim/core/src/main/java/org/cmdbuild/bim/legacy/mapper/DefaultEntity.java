package org.cmdbuild.bim.legacy.mapper;

import java.util.Map;

import org.cmdbuild.bim.legacy.model.Entity;

import com.google.common.collect.Maps;
import org.cmdbuild.bim.legacy.model.BimAttribute;

public class DefaultEntity implements Entity {

	private final Map<String, BimAttribute> attributesMap;
	private final String typeName;
	private final String key;

	private DefaultEntity(final String typeName, final String key) {
		this.key = key;
		this.typeName = typeName;
		this.attributesMap = Maps.newHashMap();
	}

	public static DefaultEntity withTypeAndKey(final String typeName, final String key) {
		return new DefaultEntity(typeName, key);
	}

	@Override
	public boolean isValid() {
		return key != null && !key.isEmpty();
	}

	@Override
	public Map<String, BimAttribute> getAttributes() {
		return attributesMap;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getTypeName() {
		return typeName;
	}

	@Override
	public BimAttribute getAttributeByName(final String attributeName) {
		return BimAttribute.class.cast(attributesMap.containsKey(attributeName) ? attributesMap.get(attributeName) : BimAttribute.NULL_ATTRIBUTE);
	}

	public void addAttribute(final BimAttribute attribute) {
		attributesMap.put(attribute.getName(), attribute);
	}

	@Override
	public String toString() {
		return typeName + ": " + getKey();
	}

	@Override
	public String getGlobalId() {
		throw new UnsupportedOperationException();
	}

}
