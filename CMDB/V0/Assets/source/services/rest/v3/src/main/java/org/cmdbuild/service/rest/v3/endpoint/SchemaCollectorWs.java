/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v3.endpoint;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;
import org.cmdbuild.api.SchemaCollectorApi;
import org.cmdbuild.api.SystemApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author afelice
 */
@Path("schema/collect")
@Consumes(TEXT_PLAIN)
@Produces(TEXT_PLAIN)
public class SchemaCollectorWs {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * A <i>dummy implementation</i> (that throws
     * <code>UnsupportedOperationException</code> on all methods) if
     * schemaCollector module not available.
     */
    private final SchemaCollectorApi schemaCollectorApi;

    public SchemaCollectorWs(SystemApi systemApi) {
        this.schemaCollectorApi = systemApi.schemaCollector(); // null if schemaCollector module not available
    }

    @GET
    @Path("test")
    public Object test(@QueryParam("msg") String msg) {
        logger.info("test({})", msg);

        String returnedMsg = "success:" + schemaCollectorApi.test(msg);

        logger.info("test({}) - returnedMsg =< {} >", msg, returnedMsg);

        return returnedMsg;
    }

    @PUT
    @Path("collectSchema")
    public Object collectSchema(@QueryParam("name") String curSystemMnemonicName, @QueryParam("id") String curSystemId) {
//        logger.info("collectSchema(\"{}\",\"{}\")", curSystemMnemonicName, curSystemId);

        // Throws UnsupportedOperationException if SchemaCollector is unavailable
        String returnedTmpFile = schemaCollectorApi.collectSchema(curSystemMnemonicName, curSystemId);

//        logger.info("collectSchema(\"{}\",\"{}\") - returnedTmpFile =< {} >", curSystemMnemonicName, curSystemId, returnedTmpFile);
        return returnedTmpFile;
    }

    @PUT
    @Path("compareSchema")
    public Object compareSchema(@QueryParam("other") String otherSchemaSerialization, @QueryParam("name") String curSystemMnemonicName) {
        // Throws UnsupportedOperationException if SchemaCollector is unavailable
        return schemaCollectorApi.compareSchema(otherSchemaSerialization, curSystemMnemonicName);
    }

    @PUT
    @Path("compareSchemaBetween")
    public Object compareSchemaBetween(@QueryParam("new") String newSchemaSerialization, @QueryParam("a") String aSchemaSerialization) {
        // Throws UnsupportedOperationException if SchemaCollector is unavailable
        return schemaCollectorApi.compareSchemaBetween(newSchemaSerialization, aSchemaSerialization);
    }

    @PUT
    @Path("applySchemaDiff")
    public Object applySchemaDiff(@QueryParam("diff") String diffSchemaSerialization) {
        // Throws UnsupportedOperationException if SchemaCollector is unavailable
        return schemaCollectorApi.applySchemaDiff(diffSchemaSerialization);
    }
}
