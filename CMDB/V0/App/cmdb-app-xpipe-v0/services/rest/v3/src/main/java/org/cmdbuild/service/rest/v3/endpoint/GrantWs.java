package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toList;
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
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.auth.grant.GrantConstants.GDCP_ATTACHMENT;
import static org.cmdbuild.auth.grant.GrantConstants.GDCP_BULK_ABORT;
import static org.cmdbuild.auth.grant.GrantConstants.GDCP_BULK_DELETE;
import static org.cmdbuild.auth.grant.GrantConstants.GDCP_BULK_UPDATE;
import static org.cmdbuild.auth.grant.GrantConstants.GDCP_DETAIL;
import static org.cmdbuild.auth.grant.GrantConstants.GDCP_EMAIL;
import static org.cmdbuild.auth.grant.GrantConstants.GDCP_FLOW_CLOSED_MODIFY_ATTACHMENT;
import static org.cmdbuild.auth.grant.GrantConstants.GDCP_HISTORY;
import static org.cmdbuild.auth.grant.GrantConstants.GDCP_NOTE;
import static org.cmdbuild.auth.grant.GrantConstants.GDCP_ON_FILTER_MISMATCH;
import static org.cmdbuild.auth.grant.GrantConstants.GDCP_RELATION;
import static org.cmdbuild.auth.grant.GrantConstants.GDCP_RELGRAPH;
import static org.cmdbuild.auth.grant.GrantConstants.GDCP_SCHEDULE;
import org.cmdbuild.auth.grant.GrantData;
import static org.cmdbuild.auth.grant.GrantData.GDCP_CLONE;
import static org.cmdbuild.auth.grant.GrantData.GDCP_CREATE;
import static org.cmdbuild.auth.grant.GrantData.GDCP_DELETE;
import static org.cmdbuild.auth.grant.GrantData.GDCP_PRINT;
import static org.cmdbuild.auth.grant.GrantData.GDCP_SEARCH;
import static org.cmdbuild.auth.grant.GrantData.GDCP_UPDATE;
import org.cmdbuild.auth.grant.GrantDataImpl;
import org.cmdbuild.auth.grant.GrantDataRepository;
import org.cmdbuild.auth.grant.GrantMode;
import static org.cmdbuild.auth.grant.GrantMode.GM_NONE;
import org.cmdbuild.auth.grant.GrantService;
import org.cmdbuild.auth.grant.PrivilegedObjectType;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_CLASS;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_PROCESS;
import org.cmdbuild.auth.role.Role;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ROLES_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ROLES_VIEW_AUTHORITY;
import org.cmdbuild.auth.role.RoleRepository;
import static org.cmdbuild.auth.role.RoleType.ADMIN;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.FilterType;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrNull;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

@Path("roles/{roleId}/grants/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_ROLES_VIEW_AUTHORITY)
public class GrantWs {

    private final GrantDataRepository repository;
    private final GrantService grantService;
    private final RoleRepository roleRepository;

    public GrantWs(GrantDataRepository repository, GrantService grantService, RoleRepository roleRepository) {
        this.repository = checkNotNull(repository);
        this.grantService = checkNotNull(grantService);
        this.roleRepository = checkNotNull(roleRepository);
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@PathParam("roleId") String roleId, @QueryParam(FILTER) String filterStr, @QueryParam(LIMIT) Long limit, @QueryParam(START) Long offset, @QueryParam("includeObjectDescription") @DefaultValue(FALSE) Boolean includeObjectDescription, @QueryParam("includeRecordsWithoutGrant") @DefaultValue(FALSE) Boolean includeRecordsWithoutGrant) {
        Role role = roleRepository.getByNameOrId(roleId);
        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
        List<GrantData> grants;
        if (includeRecordsWithoutGrant) {
            grants = grantService.getGrantsForRoleIncludeRecordsWithoutGrant(role.getId());
        } else {
            grants = repository.getGrantsForRole(role.getId());
        }
        if (!filter.isNoop()) {
            filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
            grants = AttributeFilterProcessor.<GrantData>builder()
                    .withKeyToValueFunction((key, grant) -> {
                        return switch (key) {
                            case "objectType" ->
                                serializeEnum(grant.getType());
                            default ->
                                throw new IllegalArgumentException("unsupported filter key = " + key);
                        };
                    }).withFilter(filter.getAttributeFilter())
                    .filter(grants);
        }
        return response(paged(grants, offset, limit).map((g) -> serializeGrant(g, includeObjectDescription)));
    }

    @GET
    @Path("/by-target/{objectType}/{objectTypeName}")
    public Object readOneByObject(@PathParam("roleId") String roleId, @PathParam("objectType") String objectTypeStr, @PathParam("objectTypeName") String objectTypeName) {
        if (equal(roleId, "_ALL")) {
            return response(list(roleRepository.getAllGroups()).filter(g -> !equal(g.getType(), ADMIN)).map(g -> {
                Role role = roleRepository.getByNameOrId(g.getName());
                PrivilegedObjectType objectType = parseEnum(objectTypeStr, PrivilegedObjectType.class);
                GrantData grant = grantService.getGrantDataByRoleAndTypeAndName(role.getId(), objectType, objectTypeName);
                return serializeGrant(grant);
            }));
        } else {
            Role role = roleRepository.getByNameOrId(roleId);
            PrivilegedObjectType objectType = parseEnum(objectTypeStr, PrivilegedObjectType.class);
            GrantData grant = grantService.getGrantDataByRoleAndTypeAndName(role.getId(), objectType, objectTypeName);
            return response(serializeGrant(grant));
        }
    }

    @POST
    @Path("_ANY")
    @RolesAllowed(ADMIN_ROLES_MODIFY_AUTHORITY)
    public Object update(@PathParam("roleId") String roleId, List<WsGrantData> data) {
        if (equal(roleId, "_ALL")) {
            return response(list(updateMultiRoleGrants(data)).map(this::serializeGrant));
        } else {
            Role role = roleRepository.getByNameOrId(roleId);
            return response(list(updateRoleGrants(role, data)).map(this::serializeGrant));
        }
    }

    private Object serializeGrant(GrantData grant) {
        return serializeGrant(grant, false);
    }

    private Object serializeGrant(GrantData grant, boolean includeObjectDescription) {
        return map(
                "_id", grant.getId(),
                "role", grant.getRoleId(),
                "mode", serializeEnum(grant.getMode()),
                "objectType", serializeEnum(grant.getType()),
                "objectTypeName", grant.getObjectIdOrClassNameOrCode(),
                "filter", grant.getPrivilegeFilter(),
                "attributePrivileges", grant.getAttributePrivileges(),
                "dmsPrivileges", grant.getDmsPrivileges(),
                "gisPrivileges", grant.getGisPrivileges()
        ).accept((m) -> {
            switch (grant.getType()) {
                case POT_CLASS, POT_PROCESS -> {
                    m.put("_is_process", equal(grant.getType(), POT_PROCESS));
                    list(GDCP_CREATE, GDCP_UPDATE, GDCP_DELETE, GDCP_CLONE, GDCP_PRINT).forEach((p) -> {
                        m.put(format("_can_%s", p), toBooleanOrDefault(grant.getCustomPrivileges().get(p), true));
                    });
                    list(GDCP_FLOW_CLOSED_MODIFY_ATTACHMENT).forEach((p) -> {
                        m.skipNullValues().put(format("_can_%s", p), toBooleanOrNull(grant.getCustomPrivileges().get(p)));
                    });
                    m.put("_on_filter_mismatch", serializeEnum(parseEnumOrDefault(toStringOrNull(grant.getCustomPrivileges().get(GDCP_ON_FILTER_MISMATCH)), GM_NONE)));
                    list(GDCP_ATTACHMENT, GDCP_DETAIL, GDCP_EMAIL, GDCP_HISTORY, GDCP_NOTE, GDCP_RELATION, GDCP_SCHEDULE).forEach((p) -> {
                        m.put(format("_%s_access_read", p), toBooleanOrNull(grant.getCustomPrivileges().get(format("%s_read", p))));
                        m.put(format("_%s_access_write", p), toBooleanOrNull(grant.getCustomPrivileges().get(format("%s_write", p))));
                    });
                    list(GDCP_RELGRAPH).forEach(p -> {
                        m.put(format("_%s_access", p), toBooleanOrDefault(grant.getCustomPrivileges().get(p), true));//TODO: improve this and decide if it's an action or what (_can_# or _#_access)
                    });
                    grant.getCustomPrivileges().forEach((k, v) -> {
                        Matcher matcher = Pattern.compile("(widget|contextmenu)_(.+)").matcher(k);
                        if (matcher.matches()) {
                            m.put(format("_%s_%s_access", checkNotBlank(matcher.group(1)), checkNotBlank(matcher.group(2))), v);
                        }
                    });
                }
                case POT_VIEW ->
                    list(GDCP_PRINT, GDCP_SEARCH).forEach((p) -> {
                        m.put(format("_can_%s", p), toBooleanOrDefault(grant.getCustomPrivileges().get(p), true));
                    });
            }
            switch (grant.getType()) {
                case POT_CLASS ->
                    list(GDCP_BULK_UPDATE, GDCP_BULK_DELETE, GDCP_SEARCH).forEach((p) -> {
                        m.put(format("_can_%s", p), toBooleanOrNull(grant.getCustomPrivileges().get(p)));
                    });
                case POT_PROCESS ->
                    list(GDCP_BULK_ABORT, GDCP_SEARCH).forEach((p) -> {
                        m.put(format("_can_%s", p), toBooleanOrNull(grant.getCustomPrivileges().get(p)));
                    });
            }
            if (includeObjectDescription) {
                m.put("_object_description", grantService.getGrantObjectDescription(grant));
            }
        });
    }

    private List<GrantData> updateRoleGrants(Role role, List<WsGrantData> data) {
        return repository.updateGrantsForRole(role.getId(), data.stream().map(d -> GrantDataImpl.builder()
                .withAttributePrivileges(d.attributePrivileges)
                .withDmsPrivileges(d.dmsPrivileges)
                .withGisPrivileges(d.gisPrivileges)
                .withCustomPrivileges(d.customPrivileges)
                .withMode(d.mode)
                .withObjectIdOrClassName(d.classNameOrObjectId)
                .withPrivilegeFilter(d.filter)
                .withType(d.type)
                .withRoleId(role.getId())
                .build()).collect(toList()));
    }

    private List<GrantData> updateMultiRoleGrants(List<WsGrantData> data) {
        checkArgument(data.size() == data.stream().map(d -> d.role).distinct().toList().size(), "list of grants has duplicate of same group");
        List<GrantData> grants = list();
        list(data).forEach(d -> {
            grants.addAll(updateRoleGrants(roleRepository.getByNameOrId(checkNotBlank(d.role)), list(d)));
        });
        return grants;
    }

    public static class WsGrantData {

        private final String role;
        private final GrantMode mode;
        private final PrivilegedObjectType type;
        private final Object classNameOrObjectId;
        private final Map<String, String> attributePrivileges;
        private final Map<String, String> dmsPrivileges;
        private final Map<String, String> gisPrivileges;
        private final Map<String, Object> customPrivileges;
        private final String filter;

        @JsonCreator
        public WsGrantData(ObjectNode json) {
            Map<String, Object> map = fromJson(json, MAP_OF_OBJECTS);
            this.role = toStringOrNull(map.get("role"));
            this.mode = parseEnum((String) map.get("mode"), GrantMode.class);
            this.type = parseEnum((String) map.get("objectType"), PrivilegedObjectType.class);
            this.classNameOrObjectId = checkNotNull(map.get("objectTypeName"));
            this.filter = trimToNull((String) map.get("filter"));
            this.attributePrivileges = Optional.ofNullable(json.get("attributePrivileges")).map(p -> p.isObject() ? fromJson(p, MAP_OF_STRINGS) : null).orElse(null);
            this.dmsPrivileges = Optional.ofNullable(json.get("dmsPrivileges")).map(p -> p.isObject() ? fromJson(p, MAP_OF_STRINGS) : null).orElse(null);
            this.gisPrivileges = Optional.ofNullable(json.get("gisPrivileges")).map(p -> p.isObject() ? fromJson(p, MAP_OF_STRINGS) : null).orElse(null);
            this.customPrivileges = switch (this.type) {
                case POT_CLASS, POT_PROCESS -> {
                    yield (Map) map().skipNullValues().accept(m -> {
                        list(GDCP_CREATE, GDCP_UPDATE, GDCP_DELETE, GDCP_CLONE, GDCP_PRINT, GDCP_SEARCH, GDCP_FLOW_CLOSED_MODIFY_ATTACHMENT, GDCP_BULK_UPDATE, GDCP_BULK_DELETE, GDCP_BULK_ABORT).forEach(c -> {
                            m.put(c, toBooleanOrNull(map.get(format("_can_%s", c))));
                        });
                        list(GDCP_ATTACHMENT, GDCP_DETAIL, GDCP_EMAIL, GDCP_HISTORY, GDCP_NOTE, GDCP_RELATION, GDCP_SCHEDULE).forEach(c -> {
                            m.put(format("%s_read", c), toBooleanOrNull(map.get(format("_%s_access_read", c))));
                            m.put(format("%s_write", c), toBooleanOrNull(map.get(format("_%s_access_write", c))));
                        });
                        m.put(GDCP_RELGRAPH, toBooleanOrNull(map.get(format("_%s_access", GDCP_RELGRAPH))));
                        m.put(GDCP_ON_FILTER_MISMATCH, serializeEnum(parseEnumOrDefault((String) map.get("_on_filter_mismatch"), GM_NONE)));
                        map.forEach((k, v) -> {
                            Matcher matcher = Pattern.compile("_(widget|contextmenu)_(.+)_access").matcher(k);
                            if (matcher.matches()) {
                                m.put(format("%s_%s", checkNotBlank(matcher.group(1)), checkNotBlank(matcher.group(2))), toBooleanOrNull(v));
                            }
                        });
                    });
                }
                case POT_VIEW ->
                    (Map) map().skipNullValues().accept(m -> list(GDCP_PRINT, GDCP_SEARCH).forEach(c -> m.put(c, toBooleanOrNull(map.get(format("_can_%s", c))))));
                default ->
                    emptyMap();
            };
        }
    }

}
