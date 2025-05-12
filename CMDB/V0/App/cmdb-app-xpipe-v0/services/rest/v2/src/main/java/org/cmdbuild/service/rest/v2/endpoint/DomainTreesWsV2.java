package org.cmdbuild.service.rest.v2.endpoint;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;

@Path("domainTrees")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class DomainTreesWsV2 {

    @GET
    @Path(EMPTY)
    public Object readMany(@QueryParam(FILTER) String filter, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset) {
        return null;
    }

    @GET
    @Path("{id}/")
    public Object readOne(@PathParam("id") String id) {
        return null;
    }

}
