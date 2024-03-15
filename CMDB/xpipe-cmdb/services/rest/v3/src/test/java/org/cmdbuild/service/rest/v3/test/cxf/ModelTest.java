/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.test.cxf;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import org.cmdbuild.utils.json.CmJsonUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Ignore;
import org.junit.Test;
//import static org.cmdbuild.service.rest.v3.providers.MyJacksonJaxbJsonProvider.getObjectMapperForCxfWs;

public class ModelTest {

//	private final ObjectMapper objectMapperForWs = getObjectMapperForCxfWs();
    private final ObjectMapper objectMapperForWs = CmJsonUtils.getObjectMapper();

    @Test
    public void testJsonModel1() throws IOException {
        WsModelData1 data = objectMapperForWs.readValue("{\"first\":\"one\",\"second\":2}", WsModelData1.class);
        assertNotNull(data);
        assertEquals("one", data.firstValue);
        assertEquals(Long.valueOf(2l), data.secondValue);
    }

    @Test
    public void testJsonModel2() throws IOException {
        WsModelData1 data = objectMapperForWs.readValue("{\"first\":\"one\",\"second\":\"2\"}", WsModelData1.class);
        assertNotNull(data);
        assertEquals("one", data.firstValue);
        assertEquals(Long.valueOf(2l), data.secondValue);
    }

    @Test
    public void testJsonModel3() throws IOException {
        WsModelData1 data = objectMapperForWs.readValue("{\"first\":\"one\",\"second\":\"2\",\"else\":\"wathever\"}", WsModelData1.class);
        assertNotNull(data);
        assertEquals("one", data.firstValue);
        assertEquals(Long.valueOf(2l), data.secondValue);
    }

    @Test
    public void testJsonModel4() throws IOException {
        WsModelData1 data = objectMapperForWs.readValue("{}", WsModelData1.class);
        assertNotNull(data);
        assertNull(data.firstValue);
        assertNull(data.secondValue);
    }

    @Test
    public void testJsonModel5() throws IOException {
        WsModelData2 data = objectMapperForWs.readValue("{\"first\":\"one\",\"second\":\"2\",\"else\":\"wathever\"}", WsModelData2.class);
        assertNotNull(data);
        assertEquals("one", data.firstValue);
        assertEquals(Long.valueOf(2l), data.secondValue);
        assertNotNull(data.mapValue);
        assertEquals(map("inner", "x", "else", "wathever"), data.mapValue);
    }

    @Test
    public void testJsonModel6() throws IOException {
        WsModelData3 data = objectMapperForWs.readValue("{\"first\":\"one\",\"second\":\"2\",\"else\":\"wathever\"}", WsModelData3.class);
        assertNotNull(data);
        assertNotNull(data.mapValue);
        assertEquals(map("first", "one", "second", "2", "else", "wathever"), data.mapValue);
    }

    @Test
    @Ignore("not supported yet by jackson; keep an eye on issue https://github.com/FasterXML/jackson-databind/issues/562 ")
    public void testJsonModel7() throws IOException {
        WsModelData4 data = objectMapperForWs.readValue("{\"first\":\"one\",\"second\":\"2\",\"else\":\"wathever\"}", WsModelData4.class);
        assertNotNull(data);
        assertEquals("one", data.firstValue);
        assertEquals(Long.valueOf(2l), data.secondValue);
        assertNotNull(data.mapValue);
        assertEquals(map("first", "one", "second", "2", "else", "wathever"), data.mapValue);
    }

    public static class WsModelData1 {

        private final String firstValue;
        private final Long secondValue;

        public WsModelData1(@JsonProperty("first") String firstValue, @JsonProperty("second") Long secondValue) {
            this.firstValue = firstValue;
            this.secondValue = secondValue;
        }

    }

    public static class WsModelData2 {

        private final String firstValue;
        private final Long secondValue;
        @JsonAnySetter
        private final Map<String, Object> mapValue = map("inner", "x");

        public WsModelData2(@JsonProperty("first") String firstValue, @JsonProperty("second") Long secondValue) {
            this.firstValue = firstValue;
            this.secondValue = secondValue;
        }

    }

    public static class WsModelData3 {

        private Map<String, Object> mapValue;

        @JsonCreator //note: jsonCreator annotation is required here
        public WsModelData3(Map<String, Object> mapValue) {
            this.mapValue = mapValue;
        }

    }

    public static class WsModelData4 {

        private final String firstValue;
        private final Long secondValue;
        private Map<String, Object> mapValue;

        public WsModelData4(@JsonProperty("first") String firstValue, @JsonProperty("second") Long secondValue,/* @JsonAnySetter (not supported yet) */ Map<String, Object> mapValue) {
            this.firstValue = firstValue;
            this.secondValue = secondValue;
            this.mapValue = mapValue;
        }

    }

}
