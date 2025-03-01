package org.cmdbuild.service.rest.v3.endpoint;

import java.time.ZoneId;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("timezones")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class TimezonesWs {

    @GET
    @Path("")
    public Object readAvailableTimezones() {
        return response(ZoneId.getAvailableZoneIds().stream().sorted().map(z -> map("_id", z, "description", z)));
    }

}
