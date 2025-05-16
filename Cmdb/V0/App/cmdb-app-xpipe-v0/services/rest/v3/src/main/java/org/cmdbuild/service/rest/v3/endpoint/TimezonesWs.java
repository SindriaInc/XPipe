package org.cmdbuild.service.rest.v3.endpoint;

import java.time.ZoneId;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
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
