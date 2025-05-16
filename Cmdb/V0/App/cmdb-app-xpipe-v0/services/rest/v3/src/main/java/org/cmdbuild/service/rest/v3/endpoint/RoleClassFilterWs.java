package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.cardfilter.CardFilterAsDefaultForClass;
import org.cmdbuild.cardfilter.CardFilterAsDefaultForClassImpl;
import org.cmdbuild.cardfilter.CardFilterService;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import javax.annotation.security.RolesAllowed;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_SEARCHFILTERS_VIEW_AUTHORITY;

@Path("roles/{roleId}/filters")
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_SEARCHFILTERS_VIEW_AUTHORITY)
public class RoleClassFilterWs {

    private final RoleRepository roleRepository;
    private final CardFilterService filterService;

    public RoleClassFilterWs(RoleRepository roleRepository, CardFilterService filterService) {
        this.roleRepository = checkNotNull(roleRepository);
        this.filterService = checkNotNull(filterService);
    }

    @GET
    @Path("")
    public Object read(@PathParam("roleId") String roleId) {
        Role role = roleRepository.getByNameOrId(roleId);
        List<CardFilterAsDefaultForClass> filters = filterService.getDefaultFiltersForRole(role.getId());
        return response(filters.stream().map(f -> map(
                "_id", f.getFilter().getId(),
                "_defaultFor", f.getDefaultForClass()
        )));
    }

    @POST
    @Path("")
    public Object updateWithPost(@PathParam("roleId") String roleId, List<WsDefaultStoredFilterForClass> filters) {
        Role role = roleRepository.getByNameOrId(roleId);
        List<CardFilterAsDefaultForClass> filtersUpdate = filters.stream().map(f -> new CardFilterAsDefaultForClassImpl(filterService.getById(f.getId()), f.getForClass(), role.getId())).collect(toList());
        filterService.setDefaultFiltersForRole(role.getId(), filtersUpdate);
        return read(roleId);
    }

    public static class WsDefaultStoredFilterForClass {

        private final long id;
        private final String forClass;

        public WsDefaultStoredFilterForClass(@JsonProperty("_id") Long id, @JsonProperty("_defaultFor") String forClass) {
            this.id = id;
            this.forClass = checkNotNull(forClass);
        }

        public long getId() {
            return id;
        }

        public String getForClass() {
            return forClass;
        }

    }
}
