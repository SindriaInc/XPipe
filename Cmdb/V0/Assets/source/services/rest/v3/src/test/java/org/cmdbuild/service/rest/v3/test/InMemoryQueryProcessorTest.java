/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.test;

import static com.google.common.collect.Iterables.getOnlyElement;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.beans.AttributeFilterConditionImpl;
import org.cmdbuild.data.filter.beans.AttributeFilterImpl;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl;
import org.cmdbuild.service.rest.v3.utils.InMemoryQueryProcessor;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class InMemoryQueryProcessorTest {

    private final static Map<String, Object> BASIC_MAP = map("code", "basic");
    private final static Map<String, Object> DETAILED_MAP = map("code", "basic", "descr", "detailed");

    private final static String FULLSEARCHFILTER_WITHDATA = "{\"query\":\"detailed\"}";
    private final static String FULLSEARCHFILTER_NODATA = "{\"query\":\"nodata\"}";

    private final CmdbFilter filter = CmdbFilterImpl.build(AttributeFilterImpl.simple(AttributeFilterConditionImpl.eq("descr", "detailed")));

    private final DaoQueryOptions fullSearchNoData = DaoQueryOptionsImpl.builder().withFilter(FULLSEARCHFILTER_NODATA).build();
    private final DaoQueryOptions fullSearchWithData = DaoQueryOptionsImpl.builder().withFilter(FULLSEARCHFILTER_WITHDATA).build();
    private final DaoQueryOptions attributeFilter = DaoQueryOptionsImpl.builder().withFilter(filter).build();

    @Test
    public void testDetailedSerialization() {
        Map<String, Object> basic = getOnlyElement(getData(InMemoryQueryProcessor.toResponse(list(new Object()), DaoQueryOptionsImpl.emptyOptions(), false, (x) -> BASIC_MAP, (x) -> DETAILED_MAP)));
        checkEqualsMap(basic, BASIC_MAP);
        Map<String, Object> detailed = getOnlyElement(getData(InMemoryQueryProcessor.toResponse(list(new Object()), DaoQueryOptionsImpl.emptyOptions(), true, (x) -> BASIC_MAP, (x) -> DETAILED_MAP)));
        checkEqualsMap(detailed, DETAILED_MAP);
    }

    @Test
    public void testFullTextSearchBasic() { // search goes on detailed data, returns only basic data
        Map<String, Object> search = getOnlyElement(getData(InMemoryQueryProcessor.toResponse(list(new Object()), fullSearchWithData, false, (x) -> BASIC_MAP, (x) -> DETAILED_MAP)));
        checkEqualsMap(search, BASIC_MAP);
    }

    @Test
    public void testFullTextSearchDetailed() {
        Map<String, Object> search = getOnlyElement(getData(InMemoryQueryProcessor.toResponse(list(new Object()), fullSearchWithData, true, (x) -> BASIC_MAP, (x) -> DETAILED_MAP)));
        checkEqualsMap(search, DETAILED_MAP);
    }

    @Test
    public void testAttributeSearchBasic() { // search goes on detailed data, returns only basic data
        Map<String, Object> search = getOnlyElement(getData(InMemoryQueryProcessor.toResponse(list(new Object()), attributeFilter, false, (x) -> BASIC_MAP, (x) -> DETAILED_MAP)));
        checkEqualsMap(search, BASIC_MAP);
    }

    @Test
    public void testAttributeSearchDetailed() {
        Map<String, Object> search = getOnlyElement(getData(InMemoryQueryProcessor.toResponse(list(new Object()), attributeFilter, true, (x) -> BASIC_MAP, (x) -> DETAILED_MAP)));
        checkEqualsMap(search, DETAILED_MAP);
    }

    @Test
    public void testNoData() {
        boolean searchIsEmpty = getData(InMemoryQueryProcessor.toResponse(list(new Object()), fullSearchNoData, true, (x) -> BASIC_MAP, (x) -> DETAILED_MAP)).isEmpty();
        assertTrue(searchIsEmpty);
    }

    private static List<Map<String, Object>> getData(Object response) {
        return (List) firstNotNull(((Map) response).get("data"), emptyList());
    }

    private static void checkEqualsMap(Map<String, Object> expResult, Map<String, Object> result) {
        MapDifference<String, Object> mapDifference = Maps.difference(expResult, result);
        Map<String, Object> missing = mapDifference.entriesOnlyOnLeft();
        assertTrue(format("Expected but missing: %s", missing), missing.isEmpty());
        Map<String, Object> unexpected = mapDifference.entriesOnlyOnRight();
        assertTrue(format("Actual has unexpected: %s", unexpected), unexpected.isEmpty());
        Map<String, MapDifference.ValueDifference<Object>> differing = mapDifference.entriesDiffering();
        assertTrue(format("Expected and actual differs in: %s", differing), differing.isEmpty());
    }

}
