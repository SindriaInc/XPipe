package org.cmdbuild.service.rest.v2.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.cmdbuild.cardfilter.StoredFilterImpl;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Path("processes/{processId}/filters/")
@Produces(APPLICATION_JSON)
public class ProcessFiltersWsV2 {

    @POST
    @Path(EMPTY)
    public Object create(@PathParam("processId") String processId, WsFilterData element) {
        return null;
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@PathParam("processId") String processId, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset) {
        return null;
    }

    @GET
    @Path("{filterId}/")
    public Object readOne(@PathParam("processId") String processId, @PathParam("filterId") Long filterId) {
        return null;
    }

    @PUT
    @Path("{filterId}/")
    public Object update(@PathParam("processId") String processId, @PathParam("filterId") Long filterId, WsFilterData element) {
        return null;
    }

    @DELETE
    @Path("{filterId}/")
    public Object delete(@PathParam("processId") String processId, @PathParam("filterId") Long filterId) {
        return null;
    }

    public static class WsFilterData {

        private final String name;
        private final String description;
        private final String target;
        private final String configuration;
        private final boolean shared;

        public WsFilterData(@JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("target") String target,
                @JsonProperty("configuration") String configuration,
                @JsonProperty("shared") Boolean shared) {
            this.name = checkNotBlank(name, "missing required param 'name'");
            this.description = description;
            this.target = checkNotBlank(target, "missing required param 'target'");
            this.configuration = configuration;
            this.shared = shared;
        }

        public StoredFilterImpl.StoredFilterImplBuilder toCardFilter() {
            return StoredFilterImpl.builder()
                    .withOwnerName(target)
                    .withConfiguration(configuration)
                    .withDescription(description)
                    .withName(name)
                    .withShared(shared);
        }
    }

}
