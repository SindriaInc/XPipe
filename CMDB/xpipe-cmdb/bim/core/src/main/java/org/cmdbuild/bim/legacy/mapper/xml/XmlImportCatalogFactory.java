package org.cmdbuild.bim.legacy.mapper.xml;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.bim.legacy.mapper.Parser;
import org.cmdbuild.bim.legacy.model.AttributeDefinition;
import org.cmdbuild.bim.legacy.model.Catalog;
import org.cmdbuild.bim.legacy.model.CatalogFactory;
import org.cmdbuild.bim.legacy.model.EntityDefinition;
import org.cmdbuild.bim.legacy.model.implementation.AttributeDefinitionFactory;
import org.cmdbuild.bim.legacy.model.implementation.ImportEntityDefinition;
import org.cmdbuild.bim.legacy.model.implementation.ListAttributeDefinition;
import org.cmdbuild.bim.legacy.model.implementation.ReferenceAttributeDefinition;
import org.cmdbuild.bim.legacy.model.implementation.SimpleAttributeDefinition;
import org.cmdbuild.bim.BimException;
import org.slf4j.Logger;

import com.google.common.collect.Lists;
import org.slf4j.LoggerFactory;

public class XmlImportCatalogFactory implements CatalogFactory {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static class XmlCatalog implements Catalog {

		private final List<EntityDefinition> entities;

		public XmlCatalog(final List<EntityDefinition> entities, final List<String> names) {
			this.entities = entities;
		}

		@Override
		public Iterable<EntityDefinition> getEntitiesDefinitions() {
			return entities;
		}

		@Override
		public String toString() {
			String summary = StringUtils.EMPTY;
			for (final EntityDefinition entity : entities) {
				summary = summary + "ENTITY " + entity.getIfcType().toUpperCase() + "\n";
				final Iterable<AttributeDefinition> attributes = entity.getAttributes();
				for (final AttributeDefinition attribute : attributes) {
					summary = summary + attribute.toString() + "\n";
				}
			}
			return summary;
		}

		@Override
		public int getSize() {
			return entities.size();
		}

	}

	private final Parser parser;
	private final List<EntityDefinition> entities;
	private final List<String> names = Lists.newArrayList();

	public XmlImportCatalogFactory(final File xmlFile) {
		parser = new XmlParser(xmlFile);
		entities = Lists.newArrayList();
	}

	private XmlImportCatalogFactory(final String xmlString) {
		parser = new XmlParser(xmlString);
		entities = Lists.newArrayList();
	}

	public static XmlImportCatalogFactory withXmlStringMapper(final String xmlString) {
		return new XmlImportCatalogFactory(xmlString);
	}

	@Override
	public Catalog create() {
		parseEntities();
		return new XmlCatalog(entities, names);
	}

	private void parseEntities() {
		String path = XmlParser.ROOT;
		try {
			final int numberOfTypesToRead = parser.getNumberOfNestedEntities(path);
			logger.info(numberOfTypesToRead + " entries");
			for (int i = 1; i <= numberOfTypesToRead; i++) {
				path = XmlParser.ROOT + "/entity[" + i + "]";
				final String name = parser.getIfcType(XmlParser.ROOT + "/entity[" + i + "]");
				final EntityDefinition entityDefinition = new ImportEntityDefinition(name);
				final String label = parser.getCmClassName(path);
				logger.info("{} - {}", name, label);
				entityDefinition.setCmClass(label);
				readEntity(entityDefinition, path);
				entities.add(entityDefinition);
				names.add(name);
			}
		} catch (final BimException e) {
			logger.error(e.getMessage());
		}
	}

	private void readEntity(final EntityDefinition entityDefinition, String path) {
		for (int i = 1; i <= parser.getNumberOfAttributes(path); i++) {
			final String ifcAttributeType = parser.getIfcAttributeType(path, i);
			final String cmAttributeName = parser.getCmAttributeName(path, i);
			final String attributeValue = parser.getIfcAttributeValue(path, i);
			final String ifcAttributeName = parser.getIfcAttributeName(path, i);
			final AttributeDefinitionFactory factory = new AttributeDefinitionFactory(ifcAttributeType);
			final AttributeDefinition attributeDefinition = factory.createAttribute(ifcAttributeName);
			attributeDefinition.setCmName(cmAttributeName);
			if (!attributeValue.equals("")) {
				((SimpleAttributeDefinition) attributeDefinition).setValue(attributeValue);
			}
			entityDefinition.getAttributes().add(attributeDefinition);
			if (attributeDefinition instanceof ReferenceAttributeDefinition) {
				final String path_tmp = path;
				path = path + "/attributes/attribute[" + i + "]";
				final int numberOfNestedEntities = parser.getNumberOfNestedEntities(path);
				if (numberOfNestedEntities != 1) {
					throw new BimException("Expected 1 nested entity, found " + numberOfNestedEntities);
				}
				final EntityDefinition referencedEntityDefinition = new ImportEntityDefinition("");
				((ReferenceAttributeDefinition) attributeDefinition).setReference(referencedEntityDefinition);
				path = path + "/entity";
				readEntity(referencedEntityDefinition, path);
				path = path_tmp;
			} else if (attributeDefinition instanceof ListAttributeDefinition) {
				final String path_tmp = path;
				path = path + "/attributes/attribute[" + i + "]";
				final int numberOfNestedEntities = parser.getNumberOfNestedEntities(path);
				if (numberOfNestedEntities == 0) {
				} else if (numberOfNestedEntities > 0) {
					for (int j = 1; j <= numberOfNestedEntities; j++) {
						final String path0 = path;
						path = path + "/entity[" + j + "]";
						final EntityDefinition referencedEntityDefinition = new ImportEntityDefinition("");
						((ListAttributeDefinition) attributeDefinition).setReference(referencedEntityDefinition);
						((ListAttributeDefinition) attributeDefinition).getAllReferences()
								.add(referencedEntityDefinition);
						readEntity(referencedEntityDefinition, path);
						path = path0;
					}
				} else {
					throw new BimException("error reading reference list " + ifcAttributeName);
				}
				path = path_tmp;
			}
		}
	}

}
