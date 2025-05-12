package org.cmdbuild.services.soap.utils;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;
import static org.cmdbuild.logic.mapping.json.Constants.DIRECTION_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.PROPERTY_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.AND_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.ATTRIBUTE_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.CQL_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.FULL_TEXT_QUERY_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.OPERATOR_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.OR_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.SIMPLE_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.VALUE_KEY;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.logic.mapping.json.Constants;
import org.cmdbuild.services.soap.types.Attribute;
import org.cmdbuild.services.soap.types.CQLQuery;
import org.cmdbuild.services.soap.types.Filter;
import org.cmdbuild.services.soap.types.FilterOperator;
import org.cmdbuild.services.soap.types.Order;
import org.cmdbuild.services.soap.types.Query;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.google.common.collect.Lists;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import org.cmdbuild.lookup.LookupRepository;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.lookup.LookupValue;

/**
 * Represents a mapper used to convert from {@link Attribute} to
 * {@link JSONArray} and vice versa
 */
@Component
public class SoapToJsonUtilsService {

    private static final Logger logger = LoggerFactory.getLogger(SoapToJsonUtilsService.class);

    /**
     *
     * @param attributes an array of Attribute objects (SOAP)
     * @return a JSONArray containing the names of the attributes
     */
    public static JSONArray toJsonArray(Attribute[] attributes) {
        JSONArray jsonArray = new JSONArray();
        if (attributes != null) {
            for (Attribute soapAttribute : attributes) {
                jsonArray.put(soapAttribute.getName());
            }
        }
        return jsonArray;

    }

    public static JSONArray toJsonArray(Order[] sorters, Attribute[] attributesSubsetForSelect) {
        JSONArray jsonArray = new JSONArray();
        if (sorters != null) {
            for (Order order : sorters) {
                JSONObject object = new JSONObject();
                try {
                    object.put(PROPERTY_KEY, order.getColumnName());
                    object.put(DIRECTION_KEY, order.getType());
                } catch (JSONException e) {
                    // empty
                }
                jsonArray.put(object);
            }
        } else {
            if (attributesSubsetForSelect != null && attributesSubsetForSelect.length > 0) {
                Attribute attributeUsedForOrder = attributesSubsetForSelect[0];
                JSONObject object = new JSONObject();
                try {
                    object.put(PROPERTY_KEY, attributeUsedForOrder.getName());
                    object.put(DIRECTION_KEY, "ASC");
                } catch (JSONException e) {
                    // empty
                }
                jsonArray.put(object);
            }
        }
        return jsonArray;
    }

    public JSONObject createJsonFilterFrom(Query queryType, String fullTextQuery, CQLQuery cqlQuery, Classe targetClass, LookupRepository lookupStore) {
        JSONObject filterObject = new JSONObject();
        try {
            if (queryType != null) {
                JSONObject attributeFilterObject = jsonQuery(queryType, targetClass, lookupStore);
                if (attributeFilterObject != null) {
                    filterObject.put(ATTRIBUTE_KEY, attributeFilterObject);
                }
            }
            if (StringUtils.isNotBlank(fullTextQuery)) {
                filterObject.put(FULL_TEXT_QUERY_KEY, fullTextQuery);
            }
            if (cqlQuery != null) {
                filterObject.put(CQL_KEY, cqlQuery.getCqlQuery());
            }
        } catch (JSONException ex) {

        }
        return filterObject;
    }

    private JSONObject jsonQuery(Query query, Classe targetClass, LookupRepository lookupStore) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        Filter filter = query.getFilter();
        FilterOperator filterOperator = query.getFilterOperator();
        if (filter != null) {
            jsonObject.put(SIMPLE_KEY, jsonForFilter(targetClass, lookupStore, filter));
        } else if (filterOperator != null) {
            String operator = LogicalOperatorMapper.of(filterOperator.getOperator()).getJson();
            JSONArray jsonSubQueries = new JSONArray();
            for (Query subQuery : filterOperator.getSubquery()) {
                jsonSubQueries.put(jsonQuery(subQuery, targetClass, lookupStore));
            }
            jsonObject.put(operator, jsonSubQueries);
        }
        return jsonObject;
    }

    private JSONObject jsonForFilter(Classe targetClass, LookupRepository lookupStore, Filter filter) throws JSONException {
        String attributeToFilter = filter.getName();
        JSONObject simple = new JSONObject();
        List<Object> values = Lists.newArrayList();
        for (String value : filter.getValue()) {
            org.cmdbuild.dao.entrytype.Attribute attribute = targetClass.getAttributeOrNull(attributeToFilter);
            CardAttributeType<?> attributeType = (attribute == null) ? null : attribute.getType();
            if (attributeType instanceof LookupAttributeType) {
                LookupAttributeType lookupAttributeType = LookupAttributeType.class.cast(attributeType);
                String lookupTypeName = lookupAttributeType.getLookupTypeName();
                Long lookupId;
                if (isNotBlank(value) && isNumeric(value)) {
                    lookupId = rawToSystem(lookupAttributeType, value).getId();
                } else {
                    // so it should be the description
                    lookupId = findLookupIdByTypeAndValue(lookupTypeName, value, lookupStore);
                }
                values.add((lookupId == null) ? null : lookupId.toString());
            } else if (attributeType instanceof ReferenceAttributeType) {
                if (isNotBlank(value)) {
                    values.add(value);
                }
            } else {
                if (rawToSystem(attributeType, value) != null) {
                    values.add(value);
                }
            }
        }
        simple.put(ATTRIBUTE_KEY, attributeToFilter);
        if (values.isEmpty()) {
            simple.put(OPERATOR_KEY, Constants.FilterOperator.NULL.toString());
        } else if (values.size() == 1) {
            simple.put(OPERATOR_KEY, SimpleOperatorMapper.of(filter.getOperator()).getJson());
        } else {
            simple.put(OPERATOR_KEY, Constants.FilterOperator.IN.toString());
        }
        JSONArray jsonValues = new JSONArray();
        for (Object value : values) {
            jsonValues.put(value);
        }
        simple.put(VALUE_KEY, jsonValues);
        return simple;
    }

    private static Long findLookupIdByTypeAndValue(String lookupTypeName, String description, LookupRepository lookupStore) {
//		LookupType lookupType = LookupType.newInstance().withName(lookupTypeName).build();
        Iterable<LookupValue> lookupList = lookupStore.getAllByType(lookupTypeName);
        for (LookupValue lookup : lookupList) {
            if (lookup.getDescription().equals(description)) {
                return lookup.getId();
            }
        }
        logger.warn("lookup with description '{}' not found for within type '{}'", description, lookupTypeName);
        return 0L;
    }

    private static enum LogicalOperatorMapper {
        AND(AND_KEY),
        OR(OR_KEY);

        private final String json;

        private LogicalOperatorMapper(String json) {
            this.json = json;
        }

        public String getJson() {
            return json;
        }

        public static LogicalOperatorMapper of(String s) {
            for (LogicalOperatorMapper value : values()) {
                if (value.json.equalsIgnoreCase(s)) {
                    return value;
                }
            }
            throw new IllegalArgumentException();
        }

    }

    private static enum SimpleOperatorMapper {

        EQUALS(Constants.FilterOperator.EQUAL),
        LIKE(Constants.FilterOperator.LIKE),
        UNDEFINED(null);

        private final Constants.FilterOperator filterOperator;

        private SimpleOperatorMapper(Constants.FilterOperator filterOperator) {
            this.filterOperator = filterOperator;
        }

        public String getJson() {
            return (filterOperator == null) ? name() : filterOperator.toString();
        }

        public static SimpleOperatorMapper of(String s) {
            for (SimpleOperatorMapper value : values()) {
                if (value == null) {
                    continue;
                }
                if (value.name().equalsIgnoreCase(s)) {
                    return value;
                }
            }
            logger.warn("operator mapper not found for '{}'", s);
            return UNDEFINED;
        }

    }
}
