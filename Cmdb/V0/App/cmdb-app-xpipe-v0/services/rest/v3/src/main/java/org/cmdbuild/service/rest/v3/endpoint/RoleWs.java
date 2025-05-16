package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.auth.role.GroupConfig;
import org.cmdbuild.auth.role.GroupConfigImpl;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleImpl;
import org.cmdbuild.auth.role.RoleInfo;
import org.cmdbuild.auth.role.RolePrivilege;
import static org.cmdbuild.auth.role.RolePrivilege.RP_ADMIN_ROLES_MODIFY;
import static org.cmdbuild.auth.role.RolePrivilege.RP_ADMIN_ROLES_VIEW;
import static org.cmdbuild.auth.role.RolePrivilege.RP_ADMIN_USERS_VIEW;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ROLES_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ROLES_VIEW_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeUtils.getAdminPermissionsDependencies;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.role.RoleType;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.auth.userrole.UserRoleRepository;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.dao.utils.CmSorterUtils;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SORT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.utils.json.CmJsonUtils;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBoolean;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Path("roles/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class RoleWs {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleService;
    private final OperationUserSupplier operationUser;
    private final ObjectTranslationService translationService;

    public RoleWs(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleService,
            OperationUserSupplier operationUser,
            ObjectTranslationService translationService) {
        this.userRepository = checkNotNull(userRepository);
        this.roleRepository = checkNotNull(roleRepository);
        this.userRoleService = checkNotNull(userRoleService);
        this.operationUser = checkNotNull(operationUser);
        this.translationService = checkNotNull(translationService);
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @QueryParam(LIMIT) Long limit, @QueryParam(START) Long offset, @QueryParam("detailed") @DefaultValue(FALSE) boolean detailed) {
        if (operationUser.hasPrivileges(p -> p.hasPrivileges(RP_ADMIN_ROLES_VIEW))) {
            return response(paged(isAdminViewMode(viewMode) ? roleRepository.getAllGroups() : roleRepository.getActiveGroups(), offset, limit)
                    .stream().map((t) -> {
                        if (detailed) {
                            return serializeDetailedRole(t);
                        } else {
                            return serializeBasicRole(t);
                        }
                    }));
        } else {
            return response(paged(operationUser.getUser().getLoginUser().getRoleInfos(), offset, limit).stream().map(t -> serializeRoleInfo(t)));
        }
    }

    @GET
    @Path("{roleId}/")
    public Object readOne(@PathParam("roleId") String roleId) {
        if (operationUser.hasPrivileges(p -> p.hasPrivileges(RP_ADMIN_ROLES_VIEW))) {
            return response(serializeDetailedRole(roleRepository.getByNameOrId(roleId)));
        } else {
            return response(serializeRoleInfo(operationUser.getUser().getLoginUser().getRoleInfoByNameOrId(roleId)));
        }
    }

    @GET
    @Path("{roleId}/users")
    @RolesAllowed(ADMIN_ROLES_VIEW_AUTHORITY)
    public Object readRoleUsers(@PathParam("roleId") String roleId, @QueryParam(FILTER) String filterStr, @QueryParam(SORT) String sort, @QueryParam(LIMIT) Long limit, @QueryParam(START) Long offset, @QueryParam("assigned") Boolean assigned) {
        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
        CmdbSorter sorter = CmSorterUtils.parseSorter(sort);
        Role role = roleRepository.getByNameOrId(roleId);
        PagedElements<UserData> users;
        if (firstNotNull(assigned, true) == true) {
            users = userRepository.getAllWithRole(role.getId(), filter, sorter, offset, limit);
        } else {
            users = userRepository.getAllWithoutRole(role.getId(), filter, sorter, offset, limit);
        }
        return response(users.stream().map(UserWs::serializeMinimalUser), users.totalSize());
    }

    @GET
    @Path("admin/dependencies")
    @RolesAllowed(ADMIN_ROLES_VIEW_AUTHORITY)
    public Object getAdminDependencies() {
        return response(getAdminPermissionsDependencies());
    }

    @Deprecated//TODO move to POST
    @PUT
    @Path("{roleId}/users")
    @RolesAllowed(ADMIN_ROLES_MODIFY_AUTHORITY)
    public Object updateUsersPut(@PathParam("roleId") String roleId, WsRoleUsers users) {
        return updateUsersPost(roleId, users);
    }

    @POST
    @Path("{roleId}/users")
    @RolesAllowed(ADMIN_ROLES_MODIFY_AUTHORITY)
    public Object updateUsersPost(@PathParam("roleId") String roleId, WsRoleUsers users) {
        Role role = roleRepository.getByNameOrId(roleId);
        users.usersToAdd.forEach((userId) -> userRoleService.addRoleToUser(userId, role.getId()));
        users.usersToRemove.forEach((userId) -> userRoleService.removeRoleFromUser(userId, role.getId()));
        return success();
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_ROLES_MODIFY_AUTHORITY)
    public Object create(String jsonData) {
        Role role = toRole(jsonData).build();
        role = roleRepository.create(role);
        return response(serializeDetailedRole(role));
    }

    @PUT
    @Path("{roleId}/")
    @RolesAllowed(ADMIN_ROLES_MODIFY_AUTHORITY)
    public Object update(@PathParam("roleId") String roleId, String jsonData) {
        Role role = roleRepository.getByNameOrId(roleId);
        role = toRole(jsonData).withId(role.getId()).build();
        role = roleRepository.update(role);
        return response(serializeDetailedRole(role));
    }

    public FluentMap<String, Object> serializeRoleInfo(RoleInfo role) {
        return map(
                "_id", role.getId(),
                "name", role.getName(),
                "description", role.getDescription(),
                "_description_translation", translationService.translateRoleDescription(role.getName(), role.getDescription()));
    }

    public FluentMap<String, Object> serializeBasicRole(Role role) {
        return serializeRoleInfo(role).with(
                "type", role.getType().name().toLowerCase(),
                "email", role.getEmail(),
                "active", role.isActive());
    }

    private Object serializeDetailedRole(Role role) {
        GroupConfig config = role.getConfig();
        boolean canAddUsers = userRepository.currentUserCanAddUsersToRole(role);
        return serializeBasicRole(role).with(
                "processWidgetAlwaysEnabled", config.getProcessWidgetAlwaysEnabled(),
                "startingClass", config.getStartingClass(),
                "bulkUpdate", config.getBulkUpdate(),
                "bulkDelete", config.getBulkDelete(),
                "bulkAbort", config.getBulkAbort(),
                "fullTextSearch", config.getFullTextSearch(),
                "_can_users_read", operationUser.hasPrivileges(p -> p.hasPrivileges(RP_ADMIN_USERS_VIEW)),
                "_can_users_modify", canAddUsers
        ).accept((m) -> role.getRolePrivilegesAsMap().forEach((k, v) -> m.put(format("_%s", k.name().toLowerCase()), v)));
    }

    private RoleImpl.RoleImplBuilder toRole(String jsonData) {
        WsRoleData data = fromJson(jsonData, WsRoleData.class);
        Map<String, Boolean> customPermissions = CmJsonUtils.<Map<String, Object>>fromJson(jsonData, MAP_OF_OBJECTS).entrySet().stream()
                .filter((e) -> e.getKey().startsWith("_rp_") && !equal(parseEnum(e.getKey().replaceFirst("^_rp_", ""), RolePrivilege.class), RP_ADMIN_ROLES_MODIFY))
                .collect(toMap((e) -> e.getKey().replaceFirst("^_rp_", ""), (e) -> toBoolean(e.getValue())));
        return data.toRole().withCustomPrivileges(customPermissions);
    }

    public static class WsRoleUsers {

        private final List<Long> usersToAdd, usersToRemove;

        public WsRoleUsers(@JsonProperty("add") List<Long> usersToAdd, @JsonProperty("remove") List<Long> usersToRemove) {
            this.usersToAdd = checkNotNull(usersToAdd);
            this.usersToRemove = checkNotNull(usersToRemove);
        }

    }

    public static class WsRoleData {

        private final Long id;
        private final String name, description, email, startingClass;
        private final boolean isActive;
        private final Boolean bulkUpdate, bulkDelete, bulkAbort, fullTextSearch;
        private final RoleType type;
        private final Boolean processWidgetAlwaysEnabled;
        private final List<RolePrivilege> rolePrivileges;

        public WsRoleData(
                @JsonProperty("_id") Long id,
                @JsonProperty("type") String type,
                @JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("email") String email,
                @JsonProperty("startingClass") String startingClass,
                @JsonProperty("active") Boolean isActive,
                @JsonProperty("bulkUpdate") Boolean bulkUpdate,
                @JsonProperty("bulkDelete") Boolean bulkDelete,
                @JsonProperty("bulkAbort") Boolean bulkAbort,
                @JsonProperty("fullTextSearch") Boolean fullTextSearch,
                @JsonProperty("processWidgetAlwaysEnabled") Boolean processWidgetAlwaysEnabled,
                @JsonProperty("rolePrivileges") List<String> rolePrivileges) {
            this.id = id;
            this.name = checkNotBlank(name);
            this.description = description;
            this.email = email;
            this.startingClass = trimToNull(startingClass);
            this.isActive = firstNotNull(isActive, true);
            this.type = parseEnumOrDefault(type, RoleType.DEFAULT);
            this.processWidgetAlwaysEnabled = processWidgetAlwaysEnabled;
            this.bulkUpdate = bulkUpdate;
            this.bulkDelete = bulkDelete;
            this.bulkAbort = bulkAbort;
            this.fullTextSearch = fullTextSearch;
            this.rolePrivileges = list(nullToEmpty(rolePrivileges)).map(r -> parseEnum(r, RolePrivilege.class));
        }

        private RoleImpl.RoleImplBuilder toRole() {
            return RoleImpl.builder()
                    .withId(id)
                    .withActive(isActive)
                    .withType(type)
                    .withDescription(description)
                    .withEmail(email)
                    .withName(name)
                    .withCustomPrivileges(mapOf(String.class, Boolean.class).accept(m -> rolePrivileges.forEach(r -> m.put(serializeEnum(r), true))))
                    .withConfig(GroupConfigImpl.builder()
                            .withStartingClass(startingClass)
                            .withProcessWidgetAlwaysEnabled(processWidgetAlwaysEnabled)
                            .withBulkUpdate(bulkUpdate)
                            .withBulkDelete(bulkDelete)
                            .withBulkAbort(bulkAbort)
                            .withFullTextSearch(fullTextSearch)
                            .build());
        }

    }

}
