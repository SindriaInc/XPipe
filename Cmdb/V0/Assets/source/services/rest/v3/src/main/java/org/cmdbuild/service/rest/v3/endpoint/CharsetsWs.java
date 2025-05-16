package org.cmdbuild.service.rest.v3.endpoint;

import static java.lang.String.format;
import java.nio.charset.Charset;
import static java.util.stream.Collectors.joining;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("system/charsets")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CharsetsWs {

    @GET
    @Path("")
    public Object readAvailableCharsets() {
        return response(Charset.availableCharsets().entrySet().stream().map(e -> map(
                "_id", e.getKey(),
                "description", format("%s (%s)", e.getValue().displayName(), e.getValue().aliases().stream().collect(joining(" ")))
        )));
    }

}
