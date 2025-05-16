package org.cmdbuild.bim.legacy.mapper.xml;

import java.util.List;

import org.cmdbuild.bim.legacy.mapper.DefaultAttribute;
import org.cmdbuild.bim.legacy.mapper.DefaultEntity;
import org.cmdbuild.bim.legacy.mapper.Reader;
import org.cmdbuild.bim.legacy.model.AttributeDefinition;
import org.cmdbuild.bim.legacy.model.Entity;
import org.cmdbuild.bim.legacy.model.EntityDefinition;
import org.cmdbuild.bim.legacy.model.implementation.ListAttributeDefinition;
import org.cmdbuild.bim.legacy.model.implementation.ReferenceAttributeDefinition;
import org.cmdbuild.bim.legacy.model.implementation.SimpleAttributeDefinition;
import org.cmdbuild.bim.BimException;
import org.cmdbuild.bim.bimserverclient.ListAttribute;
import org.cmdbuild.bim.bimserverclient.ReferenceAttribute;
import org.cmdbuild.bim.bimserverclient.SimpleAttribute;
import org.slf4j.Logger;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.cmdbuild.bim.legacy.model.BimAttribute;
import org.slf4j.LoggerFactory;
import org.cmdbuild.bim.bimserverclient.BimserverClientService;

public class BimReader implements Reader {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final BimserverClientService service;
	private String revisionId;

	public BimReader(final BimserverClientService service) {
		this.service = service;
	}

	@Override
	public List<Entity> readEntities(final String revisionId, final EntityDefinition entityDefinition) {
		this.revisionId = revisionId;
		final List<Entity> entities = Lists.newArrayList();
		read(new ReaderListener() {
			@Override
			public void retrieved(final Entity entity) {
				entities.add(entity);
			}

		}, entityDefinition);
		return entities;
	}

	private void read(final ReaderListener listener, final EntityDefinition entityDefinition) {

		logger.debug("reading data for revision " + revisionId + " for class " + entityDefinition.getIfcType()
				+ " corresponding to " + entityDefinition.getCmClass());
		if (entityDefinition.isValid()) {
			final Iterable<Entity> entities = service.getEntitiesByType(entityDefinition.getIfcType(), revisionId);
			if (Iterables.size(entities) == 0) {
				throw new BimException(
						"No entities of type " + entityDefinition.getIfcType() + " found in revision " + revisionId);
			}
			logger.debug(Iterables.size(entities) + " entities found");
			for (final Entity entity : entities) {
				final Entity entityToFill = DefaultEntity.withTypeAndKey(entityDefinition.getCmClass(),
						entity.getKey());
				if (!entityToFill.isValid()) {
					continue;
				}
				final boolean toInsert = readEntityAttributes(entity, entityDefinition, revisionId, entityToFill);
				if (toInsert) {
					listener.retrieved(entityToFill);
				}
			}
		}
	}

	private boolean readEntityAttributes(final Entity entity, final EntityDefinition entityDefinition,
			final String revisionId, final Entity retrievedEntity) {
		final Iterable<AttributeDefinition> attributesToRead = entityDefinition.getAttributes();
		boolean exit = false;
		for (final AttributeDefinition attributeDefinition : attributesToRead) {
			logger.debug("attribute " + attributeDefinition.getIfcName() + " of entity " + entity.getTypeName());
			if (!exit) {
				final String attributeName = attributeDefinition.getIfcName();
				final BimAttribute attribute = entity.getAttributeByName(attributeName);
				if (attribute.isValid()) {
					if (attributeDefinition instanceof SimpleAttributeDefinition) {
						final SimpleAttributeDefinition simpleAttributeDefinition = (SimpleAttributeDefinition) attributeDefinition;
						if (simpleAttributeDefinition.getValue() != "") {
							logger.debug(attributeName + " must have value " + simpleAttributeDefinition.getValue());
							logger.debug("It has value " + attribute.getValue());
							if (!simpleAttributeDefinition.getValue().equals(attribute.getValue())) {
								logger.debug("skip this entity");
								exit = true;
								return false;
							}
						}
						if (!exit) {
							logger.debug(attributeDefinition.getCmName() + ": " + attribute.getValue());
							final BimAttribute retrievedAttribute = DefaultAttribute
									.withNameAndValue(attributeDefinition.getCmName(), attribute.getValue());
							((DefaultEntity) retrievedEntity).addAttribute(retrievedAttribute);
						}
					} else if (attributeDefinition instanceof ReferenceAttributeDefinition) {
						final ReferenceAttribute referenceAttribute = (ReferenceAttribute) attribute;
						final Entity referencedEntity = service.getReferencedEntity(referenceAttribute, revisionId);
						final EntityDefinition referencedEntityDefinition = attributeDefinition.getReference();

						if (referencedEntity.isValid() && referencedEntityDefinition.isValid()) {
							readEntityAttributes(referencedEntity, referencedEntityDefinition, revisionId,
									retrievedEntity);
						} else {
							logger.debug("referenced entity valid " + referencedEntity.isValid());
						}
					} else if (attributeDefinition instanceof ListAttributeDefinition) {
						final ListAttribute list = (ListAttribute) attribute;
						int count = 1;
						for (final BimAttribute value : list.getValues()) {
							if (value instanceof ReferenceAttribute) {

								final ReferenceAttribute referenceAttribute = (ReferenceAttribute) value;
								final Entity referencedEntity = service.getReferencedEntity(referenceAttribute,
										revisionId);

								for (final EntityDefinition nestedEntityDefinition : ((ListAttributeDefinition) attributeDefinition)
										.getAllReferences()) {
									if (referencedEntity.isValid() && nestedEntityDefinition.isValid()) {
										readEntityAttributes(referencedEntity, nestedEntityDefinition, revisionId,
												retrievedEntity);
									} else {

									}
								}
							} else {
								final SimpleAttribute simpleAttribute = (SimpleAttribute) value;
								if (list.getValues().size() > 1) {
									final BimAttribute retrievedAttribute = DefaultAttribute.withNameAndValue(
											attributeDefinition.getCmName() + "" + count,
											simpleAttribute.getStringValue());
									((DefaultEntity) retrievedEntity).addAttribute(retrievedAttribute);
								} else {

									final BimAttribute retrievedAttribute = DefaultAttribute.withNameAndValue(
											attributeDefinition.getCmName(), simpleAttribute.getStringValue());
									((DefaultEntity) retrievedEntity).addAttribute(retrievedAttribute);
								}
								count++;
							}
						}
					}
				}
			}
		}
		return true;
	}

}
