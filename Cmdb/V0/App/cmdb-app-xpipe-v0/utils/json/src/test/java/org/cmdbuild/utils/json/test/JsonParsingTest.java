/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.json.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import org.cmdbuild.utils.json.CmJsonUtils;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class JsonParsingTest {

    @Test
    public void testJsonNullToString() {
        JsonElement value = null;

        String string = CmJsonUtils.toString(value);
        assertEquals(null, string);
    }

    @Test
    public void testJsonNullValueToString() {
        JsonElement value = JsonNull.INSTANCE;

        String string = CmJsonUtils.toString(value);
        assertEquals(null, string);
    }

    @Test
    public void testJsonEmptyStringValueToString() {
        JsonElement value = new JsonPrimitive("");

        String string = CmJsonUtils.toString(value);
        assertEquals("", string);
    }

    @Test
    public void testJsonStringToString() {
        JsonElement value = new JsonPrimitive("my string");

        String string = CmJsonUtils.toString(value);
        assertEquals("my string", string);
    }

    @Test
    public void testJsonNumberToString() {
        JsonElement value = new JsonPrimitive(123);

        String string = CmJsonUtils.toString(value);
        assertEquals("123", string);
    }

    @Test
    public void testJsonEmptyArrayToString() {
        JsonArray value = new JsonArray();

        String string = CmJsonUtils.toString(value);
        assertEquals("[]", string);
    }

    @Test
    public void testJsonSingletonArrayOfNullToString() {
        JsonArray value = new JsonArray();
        value.add(JsonNull.INSTANCE);

        String string = CmJsonUtils.toString(value);
        assertEquals("[null]", string);
    }

    @Test
    public void testJsonSingletonArrayOfStringToString() {
        JsonArray value = new JsonArray();
        value.add("my string");

        String string = CmJsonUtils.toString(value);
        assertEquals("[\"my string\"]", string);
    }

    @Test
    public void testJsonArrayOfStringsToString() {
        JsonArray value = new JsonArray();
        value.add("my string");
        value.add("other string");

        String string = CmJsonUtils.toString(value);
        assertEquals("[\"my string\",\"other string\"]", string);
    }

}
