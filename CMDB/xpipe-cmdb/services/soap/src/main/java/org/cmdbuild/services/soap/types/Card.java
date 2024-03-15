package org.cmdbuild.services.soap.types;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.common.Constants;
import org.cmdbuild.common.beans.IdAndDescriptionImpl;
import org.cmdbuild.dao.entrytype.attributetype.CMAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.DateAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateTimeAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForwardingAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.NullAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TimeAttributeType;

import com.google.common.collect.Lists;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FILE;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FORMULA;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.LINK;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.LOOKUPARRAY;
import static org.cmdbuild.utils.date.CmDateUtils.toJavaDate;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Card {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public interface ValueSerializer {

        String serializeValueForAttribute(CardAttributeType<?> attributeType, String attributeName, Object attributeValue);

    }

    public static final ValueSerializer LEGACY_VALUE_SERIALIZER = new LegacyValueSerializer();
    public static final ValueSerializer HACK_VALUE_SERIALIZER = new HackValueSerializer();
    private final List<Attribute> NO_ATTRIBUTES = Lists.newArrayList();

    private static abstract class AbstractValueSerializer implements ValueSerializer {

        @Override
        public final String serializeValueForAttribute(final CardAttributeType<?> attributeType,
                final String attributeName, final Object attributeValue) {
            if (attributeValue == null) {
                return StringUtils.EMPTY;
            } else {
                final Object convertedValue;
                if (isLookUpReferenceOrForeignKey(attributeType)) {
                    convertedValue = convertLookUpReferenceOrForeignKey(attributeValue);
                } else if (isDateTimeOrTimeStamp(attributeType)) {
                    convertedValue = convertDateTimeOrTimeStamp(attributeType, attributeValue);
                } else {
                    convertedValue = rawToSystem(attributeType, attributeValue);
                }
                return convertedValue != null ? convertedValue.toString() : StringUtils.EMPTY;
            }
        }

        protected Object convertLookUpReferenceOrForeignKey(final Object attributeValue) {
            final Object convertedValue;
            final IdAndDescriptionImpl foreignReference = (IdAndDescriptionImpl) attributeValue;
            convertedValue = foreignReference != null ? foreignReference.getDescription() : StringUtils.EMPTY;
            return convertedValue;
        }

        protected Object convertDateTimeOrTimeStamp(final CardAttributeType<?> attributeType, final Object attributeValue) {
            return new ForwardingAttributeTypeVisitor() {

                private final CMAttributeTypeVisitor DELEGATE = NullAttributeTypeVisitor.getInstance();

                private Object attributeValue;
                private Object convertedValue;

                @Override
                protected CMAttributeTypeVisitor delegate() {
                    return DELEGATE;
                }

                @Override
                public void visit(DateAttributeType attributeType) {
                    convertedValue = dateAsStringUtc(toJavaDate(rawToSystem(attributeType, attributeValue)), dateFormat());
                }

                @Override
                public void visit(DateTimeAttributeType attributeType) {
                    convertedValue = dateAsString(toJavaDate(rawToSystem(attributeType, attributeValue)), dateTimeFormat());
                }

                @Override
                public void visit(TimeAttributeType attributeType) {
                    convertedValue = dateAsStringUtc(toJavaDate(rawToSystem(attributeType, attributeValue)), timeFormat());
                }

                private String dateAsString(Date date, String dateFormat) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
                    simpleDateFormat.setTimeZone(TimeZone.getDefault());
                    return simpleDateFormat.format(date);
                }

                private String dateAsStringUtc(Date date, String dateFormat) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
                    return simpleDateFormat.format(date);
                }

                public Object convert(Object attributeValue) {
                    this.attributeValue = attributeValue;
                    attributeType.accept(this);
                    return convertedValue;
                }

            }.convert(attributeValue);
        }

        protected String dateFormat() {
            return Constants.DATE_TWO_DIGIT_YEAR_FORMAT;
        }

        protected String dateTimeFormat() {
            return Constants.DATETIME_TWO_DIGIT_YEAR_FORMAT;
        }

        protected String timeFormat() {
            return Constants.TIME_FORMAT;
        }

    }

    private static class LegacyValueSerializer extends AbstractValueSerializer {

    }

    private static class HackValueSerializer extends AbstractValueSerializer {

        @Override
        protected String dateFormat() {
            return Constants.SOAP_ALL_DATES_PRINTING_PATTERN;
        }

        @Override
        protected String dateTimeFormat() {
            return Constants.SOAP_ALL_DATES_PRINTING_PATTERN;
        }

        @Override
        protected String timeFormat() {
            return Constants.SOAP_ALL_DATES_PRINTING_PATTERN;
        }

    }

    private static boolean isLookUpReferenceOrForeignKey(final CardAttributeType<?> attributeType) {
        return attributeType instanceof ReferenceAttributeType //
                || attributeType instanceof LookupAttributeType //
                || attributeType instanceof ForeignKeyAttributeType;
    }

    private static boolean isDateTimeOrTimeStamp(final CardAttributeType<?> attributeType) {
        return attributeType instanceof DateAttributeType //
                || attributeType instanceof TimeAttributeType //
                || attributeType instanceof DateTimeAttributeType;
    }

    private String className;
    private long id;
    private Calendar beginDate;
    private Calendar endDate;
    private String user;
    private List<Attribute> attributeList;
    private List<Metadata> metadata;

    public Card() {
    }

    public Card(org.cmdbuild.dao.beans.Card cardModel, ValueSerializer valueSerializer) {
        setup(cardModel);
        List<Attribute> attrs = new ArrayList<>();
        for (Entry<String, Object> entry : cardModel.getAllValuesAsMap().entrySet()) {
            Attribute tmpAttribute = new Attribute();
            String attributeName = entry.getKey();
            if (cardModel.getType().hasAttribute(attributeName) && !cardModel.getType().getAttribute(attributeName).isOfType(LOOKUPARRAY, FILE, FORMULA, LINK) && !cardModel.getType().getAttribute(attributeName).getMetadata().isPassword()) { //TODO excluded, implement them on soap?
                CardAttributeType<?> attributeType = cardModel.getType().getAttribute(attributeName).getType();
                String value = valueSerializer.serializeValueForAttribute(attributeType, attributeName,
                        cardModel.get(attributeName));
                tmpAttribute.setName(attributeName);
                tmpAttribute.setValue(value);
                if (isLookUpReferenceOrForeignKey(attributeType)) {
                    IdAndDescriptionImpl foreignReference = (IdAndDescriptionImpl) cardModel.get(attributeName);
                    if (foreignReference != null && foreignReference.getId() != null) {
                        tmpAttribute.setCode(foreignReference.getId().toString());
                    }
                }
                attrs.add(tmpAttribute);
            }
        }
        this.setAttributeList(attrs);
    }

    public Card(org.cmdbuild.dao.beans.Card cardModel, Attribute[] attrs, ValueSerializer valueSerializer) {
        Attribute attribute;
        List<Attribute> list = new ArrayList<Attribute>();
        logger.debug("Filtering card with following attributes");
        for (Attribute a : attrs) {
            String name = a.getName();
            if (name != null && !name.equals(StringUtils.EMPTY)) {
                Object attributeValue = cardModel.get(name);
                attribute = new Attribute();
                attribute.setName(name);
                CardAttributeType<?> attributeType = cardModel.getType().getAttributeOrNull(name).getType();
                if (attributeValue != null) {
                    attribute.setValue(valueSerializer.serializeValueForAttribute(attributeType, name, attributeValue));
                }
                if (isLookUpReferenceOrForeignKey(attributeType)) {
                    IdAndDescriptionImpl foreignReference = (IdAndDescriptionImpl) cardModel.get(name);
                    if (foreignReference != null && foreignReference.getId() != null) {
                        attribute.setCode(foreignReference.getId().toString());
                    }
                }
                logger.debug("Attribute name=" + name + ", value="
                        + valueSerializer.serializeValueForAttribute(attributeType, name, attributeValue));
                String attributeName = attribute.getName();
                if (!attributeName.equals("Id") && !attributeName.equals("ClassName")
                        && !attributeName.equals("BeginDate") && !attributeName.equals("User")
                        && !attributeName.equals("EndDate")) {
                    list.add(attribute);
                }
            }

            this.setAttributeList(list);
        }
        setup(cardModel);
    }

    public Card(org.cmdbuild.dao.beans.Card cardModel, Attribute[] attrs) {
        this(cardModel, attrs, new LegacyValueSerializer());
    }

    protected void setup(org.cmdbuild.dao.beans.Card cardModel) {
        this.setId(cardModel.getId());
        this.setClassName(cardModel.getClassName());
        this.setUser(cardModel.getUser());
        this.setBeginDate(GregorianCalendar.from(cardModel.getBeginDate()));
        this.setEndDate(cardModel.getEndDate() != null ? GregorianCalendar.from(cardModel.getEndDate()) : null);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String classname) {
        this.className = classname;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Calendar getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(final Calendar beginDate) {
        this.beginDate = beginDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(final Calendar endDate) {
        this.endDate = endDate;
    }

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public List<Attribute> getAttributeList() {
        return (attributeList != null) ? attributeList : NO_ATTRIBUTES;
    }

    public void setAttributeList(final List<Attribute> attributeList) {
        this.attributeList = attributeList;
    }

    public List<Metadata> getMetadata() {
        return metadata;
    }

    public void setMetadata(final List<Metadata> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        String attributes = "";
        final Iterator<Attribute> itr = attributeList.iterator();
        while (itr.hasNext()) {
            attributes += itr.next().toString();
        }
        return "[className: " + className + " id:" + id + " attributes: " + attributes + "]";
    }
}
