package org.cmdbuild.services.soap.operation;

import static org.cmdbuild.common.Constants.Webservices.BOOLEAN_TYPE_NAME;
import static org.cmdbuild.common.Constants.Webservices.CHAR_TYPE_NAME;
import static org.cmdbuild.common.Constants.Webservices.DATE_TYPE_NAME;
import static org.cmdbuild.common.Constants.Webservices.DECIMAL_TYPE_NAME;
import static org.cmdbuild.common.Constants.Webservices.DOUBLE_TYPE_NAME;
import static org.cmdbuild.common.Constants.Webservices.FOREIGNKEY_TYPE_NAME;
import static org.cmdbuild.common.Constants.Webservices.INET_TYPE_NAME;
import static org.cmdbuild.common.Constants.Webservices.INTEGER_TYPE_NAME;
import static org.cmdbuild.common.Constants.Webservices.LONG_TYPE_NAME;
import static org.cmdbuild.common.Constants.Webservices.LOOKUP_TYPE_NAME;
import static org.cmdbuild.common.Constants.Webservices.REFERENCE_TYPE_NAME;
import static org.cmdbuild.common.Constants.Webservices.STRING_TYPE_NAME;
import static org.cmdbuild.common.Constants.Webservices.TEXT_TYPE_NAME;
import static org.cmdbuild.common.Constants.Webservices.TIMESTAMP_TYPE_NAME;
import static org.cmdbuild.common.Constants.Webservices.TIME_TYPE_NAME;
import static org.cmdbuild.common.Constants.Webservices.UNKNOWN_TYPE_NAME;
import org.cmdbuild.dao.core.q3.DaoService;

import org.cmdbuild.dao.entrytype.AttributePermissionMode;
import org.cmdbuild.dao.entrytype.attributetype.BooleanAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.CMAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.CharAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateTimeAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DecimalAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DoubleAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.RegclassAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IntegerAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IpAddressAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TextAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TimeAttributeType;
import org.cmdbuild.services.soap.structure.AttributeSchema;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.attributetype.LongAttributeType;

public class SerializationStuff {

    private final Logger logger = LoggerFactory.getLogger(getClass());

//	private static final Function<org.cmdbuild.data.store.metadata.Metadata, Metadata> TO_SOAP_METADATA = new Function<org.cmdbuild.data.store.metadata.Metadata, Metadata>() {
//
//		@Override
//		public Metadata apply(final org.cmdbuild.data.store.metadata.Metadata input) {
//			final Metadata element = new Metadata();
//			element.setKey(input.getName());
//			element.setValue(input.getValue());
//			return element;
//		}
//
//	};
    private final DaoService dao;

    public SerializationStuff(DaoService dao) {
        this.dao = dao;
    }

    public AttributeSchema serialize(final Attribute attribute) {
        logger.info("serializing attribute '{}'", attribute.getName());
        return serialize(attribute, attribute.getIndex());
    }

    public AttributeSchema serialize(final Attribute attribute, final int index) {
        final AttributeSchema schema = new AttributeSchema();
        attribute.getType().accept(new CMAttributeTypeVisitor() {

            @Override
            public void visit(final BooleanAttributeType attributeType) {
                schema.setType(BOOLEAN_TYPE_NAME);
            }

            @Override
            public void visit(final CharAttributeType attributeType) {
                schema.setType(CHAR_TYPE_NAME);
            }

            @Override
            public void visit(final DateAttributeType attributeType) {
                schema.setType(DATE_TYPE_NAME);
            }

            @Override
            public void visit(final DateTimeAttributeType attributeType) {
                schema.setType(TIMESTAMP_TYPE_NAME);
            }

            @Override
            public void visit(final DecimalAttributeType attributeType) {
                schema.setType(DECIMAL_TYPE_NAME);
                schema.setPrecision(attributeType.getPrecision());
                schema.setScale(attributeType.getScale());
            }

            @Override
            public void visit(final DoubleAttributeType attributeType) {
                schema.setType(DOUBLE_TYPE_NAME);
            }

            @Override
            public void visit(final RegclassAttributeType attributeType) {
                schema.setType(UNKNOWN_TYPE_NAME);
            }

            @Override
            public void visit(final LookupAttributeType attributeType) {
                schema.setType(LOOKUP_TYPE_NAME);
                schema.setLookupType(attributeType.getLookupTypeName());
            }

            @Override
            public void visit(final ForeignKeyAttributeType attributeType) {
                schema.setType(FOREIGNKEY_TYPE_NAME);
                final Classe targetClass = dao.getClasse(attributeType.getForeignKeyDestinationClassName());
                schema.setReferencedClassName(targetClass.getName());
                schema.setReferencedIdClass(targetClass.getId().intValue());
            }

            @Override
            public void visit(final IntegerAttributeType attributeType) {
                schema.setType(INTEGER_TYPE_NAME);
            }

            @Override
            public void visit(final LongAttributeType attributeType) {
                schema.setType(LONG_TYPE_NAME);
            }

            @Override
            public void visit(final IpAddressAttributeType attributeType) {
                schema.setType(INET_TYPE_NAME);
            }

            @Override
            public void visit(final ReferenceAttributeType attributeType) {
                schema.setType(REFERENCE_TYPE_NAME);
                final Domain domain = dao.getDomain(attributeType.getDomainName());
                if (domain == null) {
                    logger.error("cannot find domain '{}'", attributeType.getDomainName());
                } else {
                    schema.setDomainName(domain.getName());
                }
                if (domain.getSourceClass().getName().equals(attribute.getOwner().getName())) {
                    schema.setReferencedClassName(domain.getTargetClass().getName());
                    schema.setReferencedIdClass(domain.getTargetClass().getId().intValue());
                } else {
                    schema.setReferencedClassName(domain.getSourceClass().getName());
                    schema.setReferencedIdClass(domain.getSourceClass().getId().intValue());
                }
            }

            @Override
            public void visit(final StringArrayAttributeType attributeType) {
                schema.setType(UNKNOWN_TYPE_NAME);
            }

            @Override
            public void visit(final StringAttributeType attributeType) {
                schema.setType(STRING_TYPE_NAME);
                schema.setLength(attributeType.length);
            }

            @Override
            public void visit(final TextAttributeType attributeType) {
                schema.setType(TEXT_TYPE_NAME);
            }

            @Override
            public void visit(final TimeAttributeType attributeType) {
                schema.setType(TIME_TYPE_NAME);
            }

        });
        schema.setIdClass(attribute.getOwner().getId().intValue());
        schema.setName(attribute.getName());
        schema.setDescription(attribute.getDescription());
        schema.setBaseDSP(attribute.showInGrid());
        schema.setUnique(attribute.isUnique());
        schema.setNotnull(attribute.isMandatory());
        schema.setInherited(attribute.isInherited());
        schema.setIndex(index);
        schema.setFieldmode(serialize(attribute.getMode()));
        schema.setDefaultValue(attribute.getDefaultValue());
        schema.setClassorder(attribute.getClassOrder());
//		schema.setMetadata(from(concat(storedMetadata(attribute), filterMetadata(attribute))).toArray(Metadata.class) //TODO get metadata from attribute
//		);
        return schema;
    }

//	private FluentIterable<Metadata> storedMetadata(final Attribute attribute) {
////		final Store<org.cmdbuild.data.store.metadata.Metadata> store = metadataStoreFactory
////				.storeForAttribute(attribute);
////		final Iterable<org.cmdbuild.data.store.metadata.Metadata> elements = metadataStoreFactory.getAllMetadataForAttribute(attribute);
//		return from(elements) //
//				.transform(TO_SOAP_METADATA);
//	}
//	private Iterable<Metadata> filterMetadata(final Attribute attribute) {
//		final List<Metadata> elements = Lists.newArrayList();
//		final String filter = attribute.getFilter();
//		if (isNotBlank(filter)) {
//			final Metadata m = new Metadata();
//			m.setKey(SYSTEM_TEMPLATE_PREFIX);
//			m.setValue(filter);
//			elements.add(m);
//		}
//		return elements;
//	}
    public static String serialize(final AttributePermissionMode mode) {
//        switch (mode) {
//            case APM_WRITE:
//                return "write";
//            case APM_READ:
//                return "read";
//            case APM_HIDDEN:
//                return "hidden";
//            case APM_PROTECTED:
//                return "protected";
//            case APM_RESERVED:
//                return "reserved";
//                
//        }
        return mode.toString().replaceFirst("^([A-Z]+_)", "").toLowerCase();
//        throw new IllegalArgumentException(format("invalid mode '%s'", mode));
    }

}
