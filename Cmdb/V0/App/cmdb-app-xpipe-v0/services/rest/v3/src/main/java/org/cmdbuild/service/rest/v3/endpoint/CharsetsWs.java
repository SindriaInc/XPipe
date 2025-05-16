package org.cmdbuild.service.rest.v3.endpoint;

import static java.lang.String.format;
import java.nio.charset.Charset;
import static java.util.stream.Collectors.joining;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
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
