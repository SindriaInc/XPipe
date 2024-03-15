package org.cmdbuild.service.rest.v2.endpoint;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Path("classes/{classId}/geocards/")
@Produces(APPLICATION_JSON)
public class GeoCardsWsV2 {

    @GET
    @Path(EMPTY)
    public Object readAllGeometries(@PathParam("classId") String classId) {
        return null;
    }

    @GET
    @Path("{cardId}/")
    public Object readGeometry(@PathParam("classId") String classId, @PathParam("cardId") Long cardId) {
        return null;
    }

}
