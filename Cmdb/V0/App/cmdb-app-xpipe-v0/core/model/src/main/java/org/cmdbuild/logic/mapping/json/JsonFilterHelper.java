package org.cmdbuild.logic.mapping.json;

import static org.apache.commons.lang3.ObjectUtils.*;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.AND_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.ATTRIBUTE_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.NOT_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.OR_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.SIMPLE_KEY;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonFilterHelper {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public static interface FilterElementGetter {

		boolean hasElement();

		JSONObject getElement() throws JSONException;

	}

	private final JSONObject filter;

	/**
	 * @param filter JSON object representing a filter, can be {@code null}.
	 */
	public JsonFilterHelper(final JSONObject filter) {
		this.filter = defaultIfNull(filter, new JSONObject());
	}

	public JSONObject merge(final FilterElementGetter filterElementGetter) throws JSONException {
		if (!filterElementGetter.hasElement()) {
			logger.debug("missing element");
			return filter;
		}

		final JSONObject additionalElement = filterElementGetter.getElement();
		logger.info("adding condition '{}' to actual filter '{}'", additionalElement, filter);

		final JSONObject alwaysValidJsonFilter = filter;

		final JSONObject attribute;
		if (alwaysValidJsonFilter.has(ATTRIBUTE_KEY)) {
			attribute = filter.getJSONObject(ATTRIBUTE_KEY);
		} else {
			logger.debug("filter has no element '{}' adding an empty one", ATTRIBUTE_KEY);
			attribute = new JSONObject();
			alwaysValidJsonFilter.put(ATTRIBUTE_KEY, attribute);
		}

		if (attribute.has(AND_KEY) || attribute.has(OR_KEY)) {
			logger.debug("attribute element has 'and' or 'or' sub-elements");
			final String key = attribute.has(AND_KEY) ? AND_KEY : OR_KEY;
			final JSONArray actual = attribute.getJSONArray(key);
			attribute.remove(key);
			final JSONArray arrayWithFlowStatus = new JSONArray();
			arrayWithFlowStatus.put(object(key, actual));
			arrayWithFlowStatus.put(simple(additionalElement));
			attribute.put(AND_KEY, arrayWithFlowStatus);
		} else if (attribute.has(SIMPLE_KEY) || attribute.has(NOT_KEY)) {
			logger.debug("attribute element has 'simple' or 'not' sub-elements");
			final String key = attribute.has(SIMPLE_KEY) ? SIMPLE_KEY : NOT_KEY;
			final JSONObject actual = attribute.getJSONObject(key);
			final JSONArray arrayWithFlowStatus = new JSONArray();
			arrayWithFlowStatus.put(object(key, actual));
			arrayWithFlowStatus.put(simple(additionalElement));
			attribute.put(AND_KEY, arrayWithFlowStatus);
			attribute.remove(key);
		} else {
			logger.debug("attribute element is empty");
			attribute.put(SIMPLE_KEY, additionalElement);
		}

		logger.debug("resulting filter is '{}'", alwaysValidJsonFilter);

		return alwaysValidJsonFilter;
	}

	private JSONObject simple(final JSONObject jsonObject) throws JSONException {
		return object(SIMPLE_KEY, jsonObject);
	}

	private JSONObject object(final String key, final JSONObject jsonObject) throws JSONException {
		return new JSONObject() {
			{
				put(key, jsonObject);
			}
		};
	}

	private JSONObject object(final String key, final JSONArray jsonArray) throws JSONException {
		return new JSONObject() {
			{
				put(key, jsonArray);
			}
		};
	}

}
