package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import static com.google.common.collect.MoreCollectors.toOptional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.auth.login.PasswordService;
import org.cmdbuild.auth.multitenant.api.MultitenantService;
import org.cmdbuild.auth.multitenant.api.UserAvailableTenantContext;
import org.cmdbuild.auth.multitenant.api.UserAvailableTenantContext.TenantActivationPrivileges;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_USERS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_USERS_VIEW_AUTHORITY;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.session.model.Session;
import org.cmdbuild.auth.user.LoginUser;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserDataImpl;
import org.cmdbuild.auth.user.UserFilteredRepository;
import static org.cmdbuild.auth.user.UserRepository.BLANK_PASSWORD;
import org.cmdbuild.auth.userrole.UserRole;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.v3.utils.PositionOfUtils.handlePositionOfAndGetMeta;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_LANGUAGE;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_MULTIGROUP;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_MULTITENANT_ACTIVATION_PRIVILEGES;
import org.cmdbuild.userconfig.UserConfigService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

@Path("users/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class UserWs {

    private final static Map<String, String> USER_TABLE_ATTR_NAME_MAPPING = ImmutableMap.of(
            "username", "Username",
            "description", ATTR_DESCRIPTION,
            "email", "Email",
            "active", "Active"
    );

    private final UserFilteredRepository repository;
    private final MultitenantService multitenantService;
    private final RoleRepository groupRepository;
    private final UserConfigService userPreferencesStore;
    private final SessionService sessionService;
    private final PasswordService passwordService;

    public UserWs(UserFilteredRepository repository, MultitenantService multitenantService, RoleRepository groupRepository, UserConfigService userPreferencesStore, SessionService sessionService, PasswordService passwordService) {
        this.repository = checkNotNull(repository);
        this.multitenantService = checkNotNull(multitenantService);
        this.groupRepository = checkNotNull(groupRepository);
        this.userPreferencesStore = checkNotNull(userPreferencesStore);
        this.sessionService = checkNotNull(sessionService);
        this.passwordService = checkNotNull(passwordService);
    }

    @GET
    @Path(EMPTY)
    @RolesAllowed(ADMIN_USERS_VIEW_AUTHORITY)
    public Object readMany(WsQueryOptions query) {
        DaoQueryOptions queryOptions = query.getQuery().mapAttrNames(USER_TABLE_ATTR_NAME_MAPPING);
        PagedElements<UserData> users = repository.getMany(queryOptions);
        return response(users.map(defaultIfNull(query.isDetailed(), false) ? this::serializeFastDetailedUser : this::serializeUser), handlePositionOfAndGetMeta(queryOptions, users));
    }

    @GET
    @Path("{userId}/")
    @RolesAllowed(ADMIN_USERS_VIEW_AUTHORITY)
    public Object readOne(@PathParam("userId") Long id) {
        UserData user = repository.getUserDataById(id);
        return response(serializeDetailedUser(user));
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_USERS_MODIFY_AUTHORITY)
    public Object create(WsUserData data) {
        checkCanModify(data);
        UserData user = data.toUserData().accept(u -> {
            if (data.changePasswordRequired) {
                u.withRecoveryToken(passwordService.encryptPassword(data.password));
            } else {
                u.withPassword(passwordService.encryptPassword(data.password));
            }
        }).build();
        user = repository.create(user);
        updatePrefs(user.getUsername(), data);
        updateRoles(user, data);
        updateTenants(user, data);
        user = repository.getUserDataById(user.getId());
        return response(serializeDetailedUser(user));
    }

    @PUT
    @Path("{userId}/")
    @RolesAllowed(ADMIN_USERS_MODIFY_AUTHORITY)
    public Object update(@PathParam("userId") Long id, WsUserData data) {
        checkArgument(repository.currentUserCanModify(repository.getUserDataById(id)), "CM: current user is not allowed to create/modify user = %s", id);
        checkCanModify(data);
        UserData user = data.toUserData().withId(id).accept(u -> {
            if (data.changePasswordRequired) {
                u.withPassword(BLANK_PASSWORD).withRecoveryToken(isNotBlank(data.password) ? passwordService.encryptPassword(data.password) : repository.getUserDataById(id).getRecoveryTokenOrPassword());
            } else {
                u.withRecoveryToken(null).withPassword(isNotBlank(data.password) ? passwordService.encryptPassword(data.password) : repository.getUserDataById(id).getPasswordOrRecoveryToken());
            }
        }).build();
        user = repository.update(user);
        updatePrefs(user.getUsername(), data);
        updateRoles(user, data);
        updateTenants(user, data);
        user = repository.getUserDataById(id);
        return response(serializeDetailedUser(user));
    }

    @PUT
    @Path("current/password")
    public Object changePasswordForCurrentUser(WsUserPswData data) {
        Session session = sessionService.getCurrentSession();
        passwordService.verifyAndUpdatePasswordForUser(session.getOperationUser().getUsername(), data.getOldpassword(), data.getPassword());
        return success();
    }

    @PUT
    @Path("{username}/password")
    public Object changePassword(@PathParam("username") String username, WsUserPswData data) {
        passwordService.verifyAndUpdatePasswordForUser(username, data.getOldpassword(), data.getPassword());
        return success();
    }

    @POST
    @Path("{username}/password/recovery")
    public Object requirePasswordRecovery(@PathParam("username") String username, WsPasswordRecoveryData data) {
        LoginUser user = repository.getActiveUserByUsernameOrNull(username);
        checkArgument(user != null && equal(user.getEmail().toLowerCase(), data.getEmail().toLowerCase()), "CM: user not found for username =< %s > and email =< %s >", username, data.getEmail());//TODO check error reporting/security
        passwordService.requirePasswordRecovery(username);
        return success();
    }

    private void checkCanModify(WsUserData data) {
        checkArgument(repository.currentUserCanModify(data.toUserData().build(), list(data.userGroups).map(WsRoleOrTenantData::getId).map(groupRepository::getById), list(data.userTenants).map(WsRoleOrTenantData::getId)), "CM: current user is not allowed to create/modify user with this access privileges");
    }

    private void updatePrefs(String username, WsUserData data) {
        userPreferencesStore.setByUsernameDeleteIfNull(username, "cm_user_initialPage", trimToNull(data.initialPage));
        userPreferencesStore.setByUsernameDeleteIfNull(username, USER_CONFIG_LANGUAGE, data.lang);
        userPreferencesStore.setByUsernameDeleteIfNull(username, USER_CONFIG_MULTIGROUP, toStringOrNull(data.multiGroup));
        userPreferencesStore.setByUsernameDeleteIfNull(username, USER_CONFIG_MULTITENANT_ACTIVATION_PRIVILEGES, serializeEnum(data.multiTenantActivationPrivileges));
    }

    private void updateRoles(UserData user, WsUserData data) {
        groupRepository.setUserGroups(user.getId(), data.userGroups.stream().map(WsRoleOrTenantData::getId).collect(toList()), data.defaultRole);
    }

    private void updateTenants(UserData user, WsUserData data) {
        if (multitenantService.isEnabled() && multitenantService.isUserTenantUpdateEnabled()) {
            multitenantService.setUserTenants(user.getId(), list(data.userTenants).map(WsRoleOrTenantData::getId));
        }
    }

    public static FluentMap<String, Object> serializeMinimalUser(UserData user) {
        return map(
                "_id", user.getId(),
                "username", user.getUsername(),
                "description", user.getDescription()
        );
    }

    private FluentMap<String, Object> serializeUser(UserData user) {
        return serializeMinimalUser(user).with(
                "email", user.getEmail(),
                "active", user.isActive(),
                "service", user.isService(),
                "_can_write", repository.currentUserCanModify(user)
        );
    }

    private FluentMap<String, Object> serializeFastDetailedUser(UserData user) {
        List<UserRole> userGroups = groupRepository.getUserGroups(user.getId());
        List roles = userGroups.stream().map(UserRole::getRole).map(r -> map(
                "_id", r.getId(),
                "name", r.getName(),
                "description", r.getDescription(),
                "_description_translation", r.getDescription()//TODO
        )).collect(toList());
        UserRole defaultGroup = userGroups.stream().filter(UserRole::isDefault).collect(toOptional()).orElse(null);
        Map<String, String> prefs = userPreferencesStore.getByUsername(user.getUsername());
        return serializeUser(user).with(
                "userGroups", roles,
                "defaultUserGroup", Optional.ofNullable(defaultGroup).map(UserRole::getId).orElse(null),
                "_defaultUserGroup_description", Optional.ofNullable(defaultGroup).map(UserRole::getDescription).orElse(null),
                "language", prefs.get(USER_CONFIG_LANGUAGE),
                "initialPage", prefs.get("cm_user_initialPage"),
                "multiGroup", toBooleanOrDefault(prefs.get(USER_CONFIG_MULTIGROUP), false),
                "multiTenantActivationPrivileges", prefs.get(USER_CONFIG_MULTITENANT_ACTIVATION_PRIVILEGES)
        );
    }

    private Object serializeDetailedUser(UserData user) {
        UserAvailableTenantContext tenantContext = multitenantService.getAvailableTenantContextForUser(user.getId());
        List<UserRole> userGroups = groupRepository.getUserGroups(user.getId());
        this.sessionService.getCurrentSession().getOperationUser();
        // TODO commented because of performance, this is not used by ui; return available groups to limited user
        //        List<Map<String, Object>> currentUserAvailableGroups = groupRepository.getAllGroups().stream().filter(repository::currentUserCanAddUsersToRole).map(r -> mapOf(String.class, Object.class).with(
        //                "_id", r.getId(),
        //                "name", r.getName(),
        //                "description", r.getDescription(),
        //                "_description_translation", r.getDescription()//TODO
        //        )).collect(toList());
        List<Map<String, Object>> roles = userGroups.stream().map(UserRole::getRole).map(r -> mapOf(String.class, Object.class).with(
                "_id", r.getId(),
                "name", r.getName(),
                "description", r.getDescription(),
                "_description_translation", r.getDescription()//TODO
        )).collect(toList());
        UserRole defaultGroup = userGroups.stream().filter(UserRole::isDefault).collect(toOptional()).orElse(null);
        List<Map<String, Object>> tenants = multitenantService.getAvailableUserTenants(tenantContext).stream().map(t -> mapOf(String.class, Object.class).with(
                "_id", t.getId(),
                "name", t.getDescription(),
                "description", t.getDescription(),
                "_description_translation", t.getDescription()//TODO
        )).collect(toList());
        Map<String, String> prefs = userPreferencesStore.getByUsername(user.getUsername());
        return serializeUser(user).with("userTenants", tenants,
                "defaultUserTenant", tenantContext.getDefaultTenantId(),
                "userGroups", roles,
                //                "_availableUserGroups", currentUserAvailableGroups,
                "defaultUserGroup", Optional.ofNullable(defaultGroup).map(UserRole::getId).orElse(null),
                "_defaultUserGroup_description", Optional.ofNullable(defaultGroup).map(UserRole::getDescription).orElse(null),
                "language", prefs.get(USER_CONFIG_LANGUAGE),
                "initialPage", prefs.get("cm_user_initialPage"),
                "multiGroup", toBooleanOrDefault(prefs.get(USER_CONFIG_MULTIGROUP), false),
                "multiTenantActivationPrivileges", prefs.get(USER_CONFIG_MULTITENANT_ACTIVATION_PRIVILEGES),
                "changePasswordRequired", user.hasRecoveryToken() && !user.hasPassword());
    }

    public static class WsRoleOrTenantData {

        private final long id;

        public WsRoleOrTenantData(@JsonProperty("_id") Long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }

    }

    public static class WsPasswordRecoveryData {

        private final String email;

        public WsPasswordRecoveryData(@JsonProperty("email") String email) {
            this.email = checkNotBlank(email, "missing 'email' param");
        }

        public String getEmail() {
            return email;
        }

    }

    public static class WsUserPswData {

        private final String password, oldpassword;

        public WsUserPswData(@JsonProperty("password") String password, @JsonProperty("oldpassword") String oldpassword) {
            this.password = checkNotBlank(password, "missing 'password' param");
            this.oldpassword = checkNotBlank(oldpassword, "missing 'oldpassword' param");
        }

        public String getPassword() {
            return password;
        }

        public String getOldpassword() {
            return oldpassword;
        }

    }

    public static class WsUserData {

        private final Long id, defaultRole;
        private final String username, description, email, password, initialPage, lang;
//        private final ZonedDateTime passwordExpiration, lastPasswordChange, lastExpiringNotification;
        private final Boolean isActive, isService, multiTenant, multiGroup;
        private final boolean changePasswordRequired;
        private final List<WsRoleOrTenantData> userTenants, userGroups;
        private final TenantActivationPrivileges multiTenantActivationPrivileges;

        public WsUserData(@JsonProperty("_id") Long id,
                @JsonProperty("username") String username,
                @JsonProperty("description") String description,
                @JsonProperty("email") String email,
                @JsonProperty("password") String password,
                @JsonProperty("initialPage") String initialPage,
                @JsonProperty("changePasswordRequired") Boolean changePasswordRequired,
                //                @JsonProperty("passwordExpiration") ZonedDateTime passwordExpiration,
                //                @JsonProperty("lastPasswordChange") ZonedDateTime lastPasswordChange,
                //                @JsonProperty("lastExpiringNotification") ZonedDateTime lastExpiringNotification,
                @JsonProperty("active") Boolean isActive,
                @JsonProperty("service") Boolean isService,
                @JsonProperty("language") String lang,
                @JsonProperty("multiGroup") Boolean multiGroup,
                @JsonProperty("multiTenant") Boolean multiTenant,
                @JsonProperty("multiTenantActivationPrivileges") String multiTenantActivationPrivileges,
                @JsonProperty("defaultUserGroup") Long defaultRole,
                @JsonProperty("userTenants") List<WsRoleOrTenantData> userTenants,
                @JsonProperty("userGroups") List<WsRoleOrTenantData> userGroups) {
            this.id = id;
            this.username = checkNotBlank(username, "'username' is null");
            this.password = password;
            this.description = description;
            this.email = email;
            this.changePasswordRequired = firstNotNull(changePasswordRequired, false);
//            this.passwordExpiration = passwordExpiration;
//            this.lastPasswordChange = lastPasswordChange;
//            this.lastExpiringNotification = lastExpiringNotification;
            this.isActive = isActive;
            this.isService = isService;
            this.lang = lang;
            this.initialPage = initialPage;
            this.multiGroup = multiGroup;
            this.multiTenant = multiTenant;
            this.userTenants = nullToEmpty(userTenants);
            this.userGroups = checkNotNull(userGroups, "'userGroups' is null");
            this.defaultRole = defaultRole;
            this.multiTenantActivationPrivileges = parseEnumOrNull(multiTenantActivationPrivileges, TenantActivationPrivileges.class);
        }

        private UserDataImpl.UserDataImplBuilder toUserData() {
            return UserDataImpl.builder()
                    .withId(id)
                    .withUsername(username)
                    .withDescription(description)
                    .withEmail(email)
                    //                    .withPasswordExpiration(passwordExpiration)
                    //                    .withLastPasswordChange(lastPasswordChange)
                    //                    .withLastExpiringNotification(lastExpiringNotification)
                    .withActive(isActive)
                    .withService(isService);
        }

    }

}
