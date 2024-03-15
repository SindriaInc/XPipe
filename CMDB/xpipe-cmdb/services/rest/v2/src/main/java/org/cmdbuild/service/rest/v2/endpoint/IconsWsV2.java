package org.cmdbuild.service.rest.v2.endpoint;

import javax.swing.Icon;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("icons/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class IconsWsV2 {

    @POST
    @Path(EMPTY)
    public Object create(Icon icon) {
        return null;
    }

    @GET
    @Path(EMPTY)
    public Object readMany() {
        return null;
    }

    @GET
    @Path("{iconId}/")
    public Object readOne(@PathParam("iconId") Long id) {
        return null;
    }

    @PUT
    @Path("{iconId}/")
    public Object update(@PathParam("iconId") Long id, Icon icon) {
        return null;
    }

    @DELETE
    @Path("{iconId}/")
    public Object delete(@PathParam("iconId") Long id) {
        return null;
    }

}
