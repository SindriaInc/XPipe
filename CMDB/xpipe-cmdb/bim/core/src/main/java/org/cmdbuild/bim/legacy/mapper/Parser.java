package org.cmdbuild.bim.legacy.mapper;

public interface Parser {

	int getNumberOfNestedEntities(String entityPath);

	int getNumberOfAttributes(String entityPath);

	String getIfcType(String entityPath);

	String getIfcAttributeType(String entityPath, int i);

	String getIfcAttributeValue(String entityPath, int i);

	String getIfcAttributeName(String path, int i);

	String getCmClassName(String entityPath);

	String getCmAttributeName(String entityPath, int i);

}
