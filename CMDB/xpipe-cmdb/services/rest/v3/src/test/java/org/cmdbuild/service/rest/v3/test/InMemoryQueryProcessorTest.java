/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.test;

import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.service.rest.v3.utils.InMemoryQueryProcessor;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class InMemoryQueryProcessorTest {

    @Test
    public void testDetailedSerialization() {
        Map<String, Object> basic = getOnlyElement(getData(InMemoryQueryProcessor.toResponse(list(new Object()), DaoQueryOptionsImpl.emptyOptions(), false, (x) -> map("s", "basic"), (x) -> map("s", "detailed"))));
        assertEquals("basic", basic.get("s"));
        Map<String, Object> detailed = getOnlyElement(getData(InMemoryQueryProcessor.toResponse(list(new Object()), DaoQueryOptionsImpl.emptyOptions(), true, (x) -> map("s", "basic"), (x) -> map("s", "detailed"))));
        assertEquals("detailed", detailed.get("s"));
    }

    private static List<Map<String, Object>> getData(Object response) {
        return (List) firstNotNull(((Map) response).get("data"), emptyList());
    }

}
