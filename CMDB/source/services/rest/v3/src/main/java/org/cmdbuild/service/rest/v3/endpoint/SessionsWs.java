package org.cmdbuild.service.rest.v3.endpoint;

import org.cmdbuild.service.rest.v3.helpers.SessionWsCommons;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.Maps.transformValues;
import com.google.common.collect.Ordering;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map.Entry;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import jakarta.ws.rs.core.Response;
import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.auth.grant.GroupOfPrivileges;

import org.cmdbuild.auth.multitenant.api.MultitenantService;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.auth.login.LoginDataImpl;
import org.cmdbuild.auth.session.SessionScope;

import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.session.model.Session;
import org.cmdbuild.config.CoreConfiguration;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EXT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ID;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.emptyToNull;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrNull;
import jakarta.annotation.security.RolesAllowed;
import static jakarta.ws.rs.core.HttpHeaders.SET_COOKIE;
import static org.cmdbuild.auth.config.AuthenticationServiceConfiguration.LoginServiceReturnId.RI_ALWAYS;
import org.cmdbuild.auth.login.AuthenticationConfiguration;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.SYSTEM_ACCESS_AUTHORITY;
import org.cmdbuild.auth.login.PasswordResetRequiredAuthenticationException;
import org.cmdbuild.auth.login.RequestAuthenticatorResponse;
import org.cmdbuild.auth.user.SessionType;
import static org.cmdbuild.auth.user.SessionType.ST_BATCH;
import static org.cmdbuild.auth.user.SessionType.ST_INTERACTIVE;
import org.cmdbuild.auth.login.UserVisibleAuthenticationException;
import static org.cmdbuild.fault.FaultUtils.buildMessageListForResponse;
import static org.cmdbuild.common.http.HttpConst.CMDBUILD_AUTHORIZATION_COOKIE;
import org.cmdbuild.config.UiFilterConfiguration;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.dao.utils.CmFilterUtils.serializeFilter;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.failure;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.ui.TargetDevice;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.ws3.utils.Ws3CookieUtils.buildDeleteCookieHeader;
import static org.cmdbuild.utils.ws3.utils.Ws3CookieUtils.buildSetCookieHeader;
import static org.cmdbuild.fault.FaultLevel.FL_ERROR;
import static org.cmdbuild.fault.FaultUtils.exceptionToUserMessage;

@Path("sessions/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class SessionsWs extends SessionWsCommons {

    private final MultitenantService multitenantService;
    private final CoreConfiguration coreConfig;
    private final AuthenticationConfiguration authConfig;
    private final UiFilterConfiguration uiConfig;
    private final ObjectTranslationService translationService;

    public SessionsWs(ObjectTranslationService translationService, UiFilterConfiguration config, SessionService sessionService, MultitenantService multitenantService, CoreConfiguration configuration, AuthenticationConfiguration authConfig) {
        super(sessionService);
        this.multitenantService = checkNotNull(multitenantService);
        this.coreConfig = checkNotNull(configuration);
        this.uiConfig = checkNotNull(config);
        this.authConfig = checkNotNull(authConfig);
        this.translationService = checkNotNull(translationService);
    }

    @POST
    @Path(EMPTY)
    public Object create(@Context HttpServletRequest request, WsSessionData sessionData, @QueryParam(EXT) @Nullable Boolean includeExtendedData, @QueryParam("scope") String scopeStr, @QueryParam("returnId") Boolean returnId) {
        checkNotBlank(sessionData.getUsername(), "'username' param cannot be null");
        checkNotNull(sessionData.getPassword(), "'password' param cannot be null");

        SessionScope scope = checkNotNull(convert(firstNotBlankOrNull(scopeStr, sessionData.scope), SessionScope.class), "must set 'scope' param (valid values = %s)", list(SessionScope.values()).stream().map(SessionScope::name).map(String::toLowerCase).collect(joining(",")));
        boolean serviceUsersAllowed, attachSessionCookie;
        SessionType sessionType;
        switch (scope) {
            case SERVICE -> {
                serviceUsersAllowed = true;
                attachSessionCookie = false;
                sessionType = ST_BATCH;
            }
            case UI -> {
                serviceUsersAllowed = false;
                attachSessionCookie = true;
                sessionType = ST_INTERACTIVE;
            }
            default ->
                throw unsupported("unsupported session scope = %s", scope);
        }
        try {
            String sessionId = sessionService.create(LoginDataImpl.builder()
                    .withLoginString(sessionData.getUsername())
                    .withPassword(sessionData.getPassword())
                    .withGroupName(sessionData.getRole())
                    .withServiceUsersAllowed(serviceUsersAllowed)
                    .withIgnoreTenantPolicies(sessionData.ignoreTenants)
                    .withTargetDevice(sessionData.device)
                    .withSessionType(sessionType)
                    .build());

            returnId = firstNotNull(returnId, equal(authConfig.getLoginServiceReturnIdMode(), RI_ALWAYS));

            Object responsePayload = response(serializeSession(sessionService.getSessionById(sessionId), includeExtendedData, returnId));
            if (attachSessionCookie) {
                return Response.ok(responsePayload).header(SET_COOKIE, buildSetCookieHeader(CMDBUILD_AUTHORIZATION_COOKIE, sessionId, uiConfig.getCookieMaxAgeSeconds(), firstNotBlank(request.getContextPath(), "/"), uiConfig.enableCookieSecure(request.isSecure()), true, uiConfig.getCookieSameSiteMode())).build();
            } else {
                return responsePayload;
            }
        } catch (UserVisibleAuthenticationException ex) {
            return Response.status(UNAUTHORIZED).entity(failure().with("messages", buildMessageListForResponse(FL_ERROR, true, exceptionToUserMessage(ex)))).build();
        } catch (PasswordResetRequiredAuthenticationException ex) {
            return Response.status(UNAUTHORIZED).entity(failure().with("passwordResetRequired", true)).build();
        }
    }

    @GET
    @Path("{" + ID + "}/")
    public Object readOne(@PathParam(ID) String sessionId, @QueryParam(EXT) Boolean includeExtendedData, @QueryParam("if_exists") @DefaultValue(FALSE) Boolean checkIfExists) {
        sessionId = sessionIdOrCurrent(sessionId);
        boolean exists = isNotBlank(sessionId) && sessionService.exists(sessionId);
        if (checkIfExists && !exists) {
            return response(map("exists", false));
        } else {
            checkArgument(exists, "session not found for id = %s", sessionId);
            return response(serializeSession(sessionService.getSessionById(sessionId), includeExtendedData, false).accept(m -> {
                if (checkIfExists) {
                    m.put("exists", true);
                }
            }));
        }
    }

    @GET
    @Path("{sessionId}/privileges")
    public Object readPrivileges(@PathParam("sessionId") String sessionId) {
        sessionId = sessionIdOrCurrent(sessionId);
        checkArgument(sessionService.exists(sessionId), "session not found for id = %s", sessionId);
        OperationUser user = sessionService.getUser(sessionId);
        return response(user.getPrivilegeContext().getAllPrivileges().entrySet().stream().sorted(Ordering.natural().onResultOf(Entry::getKey)).map((p) -> map(
                "subject", p.getKey())
                .with(serializeGroupOfPrivileges(p.getValue().getMinPrivilegesForAllRecords()))
                .accept((m) -> {
                    if (p.getValue().hasPrivilegesWithFilter()) {
                        m.put("hasPrivilegesWithFilter", true,
                                "privilegesWithFilter", p.getValue().getPrivilegeGroupsWithFilter().stream().map((pf) -> map("filter", serializeFilter(pf.getFilter())).with(serializeGroupOfPrivileges(pf))).collect(toList()));
                    }
                })).collect(toList()));
    }

    @GET
    @Path("")
    @RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
    public Object readAll() {
        List<Session> sessions = sessionService.getAllSessions();
        return response(sessions.stream().sorted(Ordering.natural().onResultOf(Session::getLastActiveDate).reversed()).map(this::serializeSession).collect(toList()));
    }

    @PUT
    @Path("{" + ID + "}/")
    public Object update(@PathParam(ID) String sessionId, WsSessionData sessionData, @QueryParam(EXT) Boolean includeExtendedData) {
        sessionId = sessionIdOrCurrent(sessionId);
        checkArgument(sessionService.exists(sessionId), "session not found for id = %s", sessionId);
        checkArgument(!isBlank(sessionData.getRole()), "'group' param cannot be null");
        OperationUser currentOperationUser = sessionService.getUser(sessionId);
        sessionService.update(sessionId, LoginDataImpl.builder()
                .withLoginString(currentOperationUser.getLoginUser().getUsername())
                .withGroupName(sessionData.getRole())
                .withDefaultTenant(sessionData.getDefaultTenant())
                .withActiveTenants(sessionData.getActiveTenants())
                .withIgnoreTenantPolicies(sessionData.ignoreTenants)
                .withTargetDevice(sessionData.device)
                .withServiceUsersAllowed(true)//TODO use scope
                .build());
        return response(serializeSession(sessionService.getSessionById(sessionId), includeExtendedData, false));
    }

    @DELETE
    @Path("{" + ID + "}/")
    public Object delete(@Context HttpServletRequest request, @PathParam(ID) String sessionId) {
        sessionId = sessionIdOrCurrent(sessionId);
        checkArgument(sessionService.exists(sessionId), "session not found for id = %s", sessionId);
        RequestAuthenticatorResponse<Void> response = sessionService.deleteSession(sessionId, request);
        return Response.ok(success().accept(m -> {
            if (response.hasRedirectUrl()) {
                m.put("redirect", response.getRedirectUrl());
            }
        })).header(SET_COOKIE, buildDeleteCookieHeader(CMDBUILD_AUTHORIZATION_COOKIE, firstNotBlank(request.getContextPath(), "/"), uiConfig.enableCookieSecure(request.isSecure()), true, uiConfig.getCookieSameSiteMode())).build();
    }

    @DELETE
    @Path("all")
    @RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
    public Object deleteAll() {
        logger.info("delete all sessions");
        sessionService.deleteAll();
        return success();
    }

    @POST
    @Path("current/keepalive")
    public Object keepalive() {
        return response(map("timeToLiveSeconds", coreConfig.getSessionTimeoutOrDefault(), "recommendedKeepaliveIntervalSeconds", coreConfig.getSessionTimeoutOrDefault() / 3));
    }

    private Object serializeSession(Session session) {
        return serializeSession(session, false, false);
    }

    private FluentMap<String, ?> serializeSession(Session session, @Nullable Boolean includeExtendedData, boolean includeId) {
        OperationUser user = session.getOperationUser();
        return (FluentMap) map(
                "_id", "current",
                "username", user.getLoginUser().getUsername(),
                "userId", user.getLoginUser().getId(),
                "userDescription", user.getLoginUser().getDescription(),
                "role", user.getDefaultGroupNameOrNull(),
                "availableRoles", user.getLoginUser().getGroupNames(),
                "multigroup", user.getLoginUser().hasMultigroupEnabled(),
                "rolePrivileges", user.getRolePrivilegesAsMap().entrySet().stream().filter((e) -> e.getValue() == true).collect(toMap((e) -> e.getKey().name().toLowerCase().replaceFirst("^rp_", ""), Entry::getValue)),
                "beginDate", toIsoDateTime(session.getBeginDate()),
                "lastActive", toIsoDateTime(session.getLastActiveDate()),
                "device", serializeEnum(session.getTargetDevice()),
                "sessionType", serializeEnum(user.getSessionType())
        ).accept((m) -> {
            if (includeId) {
                m.put("_id", session.getSessionId());
            }
            if (multitenantService.isEnabled()) {
                m.put("availableTenants", user.getLoginUser().getAvailableTenantContext().getAvailableTenantIds(),
                        "tenant", user.getUserTenantContext().getDefaultTenantId(),
                        "activeTenants", user.getUserTenantContext().getActiveTenantIds(),
                        "canIgnoreTenants", user.getLoginUser().getAvailableTenantContext().ignoreTenantPolicies(),
                        "ignoreTenants", user.getUserTenantContext().ignoreTenantPolicies(),
                        "multiTenantActivationPrivileges", serializeEnum(user.getLoginUser().getAvailableTenantContext().getTenantActivationPrivileges())
                );
            }
            if (firstNonNull(includeExtendedData, false)) {
                m.put(
                        "availableRolesExtendedData", user.getLoginUser().getRoleInfos().stream().map((g) -> map(
                        "_id", g.getId(),
                        "code", g.getName(),
                        "description", g.getDescription(),
                        "_description_translation", translationService.translateRoleDescription(g.getName(), g.getDescription())
                )).collect(toList()),
                        "availableTenantsExtendedData", multitenantService.getTenantDescriptions(user.getLoginUser().getAvailableTenantContext().getAvailableTenantIds()).entrySet().stream()
                                .sorted(Ordering.natural().onResultOf(Entry::getValue))
                                .map((e) -> map("code", e.getKey(), "description", firstNonNull(trimToNull(e.getValue()), format("tenant #%s", e.getKey())))).collect(toList()));
            }

        });
    }

    private static FluentMap<String, Object> serializeGroupOfPrivileges(GroupOfPrivileges privileges) {
        return (FluentMap) map(
                "source", privileges.getSource(),
                "privileges", privileges.getServicePrivileges().stream().sorted(Ordering.natural()).map((p) -> p.name().toLowerCase()).collect(toList())
        ).skipNullValues().with(
                "attributePrivileges", emptyToNull(map(transformValues(privileges.getAttributePrivileges(), (v) -> v.stream().sorted(Ordering.natural()).map((p) -> p.name().toLowerCase())))));
    }

    public static class WsSessionData {

        public final String username, password, role, scope;
        public final Long defaultTenant;
        public final boolean ignoreTenants;
        public final List<Long> activeTenants;
        public final TargetDevice device;

        public WsSessionData(@JsonProperty("username") String username,
                @JsonProperty("password") String password,
                @JsonProperty("role") String role,
                @JsonProperty("scope") String scope,
                @JsonProperty("device") String device,
                @JsonProperty("tenant") Long defaultTenant,
                @JsonProperty("ignoreTenants") Boolean ignoreTenants,
                @JsonProperty("activeTenants") List<Long> activeTenants) {
            this.username = username;
            this.password = password;
            this.role = role;
            this.scope = scope;
            this.device = parseEnumOrNull(device, TargetDevice.class);
            this.defaultTenant = defaultTenant;
            this.activeTenants = activeTenants == null ? emptyList() : ImmutableList.copyOf(activeTenants);
            this.ignoreTenants = firstNonNull(ignoreTenants, false);
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getRole() {
            return role;
        }

        public Long getDefaultTenant() {
            return defaultTenant;
        }

        public List<Long> getActiveTenants() {
            return activeTenants;
        }

        @Nullable
        public TargetDevice getDevice() {
            return device;
        }

    }
}
