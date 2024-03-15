/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v2.providers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import javax.ws.rs.ext.Provider;

@Provider
public class MyJacksonJaxbJsonProvider extends JacksonJaxbJsonProvider {

    public MyJacksonJaxbJsonProvider() {
        super(getObjectMapperForCxfWs(), JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS);
    }

    public static ObjectMapper getObjectMapperForCxfWs() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
}
