package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Ordering;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.WILDCARD;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.SYSTEM_ACCESS_AUTHORITY;
import org.cmdbuild.minions.Minion;
import org.cmdbuild.minions.MinionService;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBoolean;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("system_services/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
public class MinionsWs {

    private final MinionService minionService;

    public MinionsWs(MinionService servicesStatusService) {
        this.minionService = checkNotNull(servicesStatusService);
    }

    @GET
    @Path("")
    public Object getAll(@QueryParam("hidden") @DefaultValue("default") String hidden) {
        return response(minionService.getMinions().stream().filter(m -> {
            return switch (hidden) {
                case "default" ->
                    true;
                case "false", "true" ->
                    equal(m.isHidden(), toBoolean(hidden));
                default ->
                    throw unsupported("unsupported hidden parameter =< %s >", hidden);
            };
        }).sorted(Ordering.natural().onResultOf(Minion::getDescription)).map(this::serializeServiceStatus));
    }

    @GET
    @Path("{serviceId}")
    public Object getOne(@PathParam("serviceId") String serviceId) {
        return response(serializeServiceStatus(minionService.getMinion(serviceId)));
    }

    @POST
    @Path("{serviceId}/start")
    @Consumes(WILDCARD)
    public Object start(@PathParam("serviceId") String serviceId) {
        minionService.startMinion(serviceId);
        return response(serializeServiceStatus(minionService.getMinion(serviceId)));
    }

    @POST
    @Path("{serviceId}/stop")
    @Consumes(WILDCARD)
    public Object stop(@PathParam("serviceId") String serviceId) {
        minionService.stopMinion(serviceId);
        return response(serializeServiceStatus(minionService.getMinion(serviceId)));
    }

    private Object serializeServiceStatus(Minion minion) {
        return map("_id", minion.getId(),
                "name", minion.getName(),
                "description", minion.getDescription(),
                "status", serializeEnum(minion.getStatus()),
                "_is_enabled", minion.isEnabled(),
                "_can_start", minion.canStart(),
                "_can_stop", minion.canStop()
        );
    }

}
