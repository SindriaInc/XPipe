package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.ImmutableSet;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import org.cmdbuild.auth.login.AuthenticationConfiguration;
import org.cmdbuild.auth.login.LoginModuleConfiguration;
import org.cmdbuild.auth.login.PasswordManagementConfiguration;
import org.cmdbuild.auth.multitenant.config.MultitenantConfiguration;
import static org.cmdbuild.auth.role.RolePrivilege.RP_ADMIN_ACCESS;
import static org.cmdbuild.auth.role.RolePrivilege.RP_CHAT_ACCESS;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.config.BimConfiguration;
import org.cmdbuild.config.CalendarServiceConfiguration;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.config.EmailConfiguration;
import org.cmdbuild.config.EtlConfiguration;
import org.cmdbuild.config.GisConfiguration;
import org.cmdbuild.config.GraphConfiguration;
import org.cmdbuild.config.MobileConfiguration;
import org.cmdbuild.config.SchedulerConfiguration;
import org.cmdbuild.config.UiConfiguration;
import org.cmdbuild.config.WebSocketConfiguration;
import org.cmdbuild.debuginfo.BuildInfoService;
import org.cmdbuild.dms.DmsConfiguration;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import org.cmdbuild.workflow.WorkflowConfiguration;

@Path("configuration/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ConfigurationWs {

    private final DmsConfiguration dmsConfiguration;
    private final ObjectTranslationService translationService;
    private final MultitenantConfiguration multitenantConfiguration;
    private final WebSocketConfiguration websocketConfiguration;
    private final WorkflowConfiguration workflowConfig;
    private final GisConfiguration gisConfiguration;
    private final BimConfiguration bimConfiguration;
    private final CoreConfiguration coreConfiguration;
    private final GraphConfiguration graphConfiguration;
    private final BuildInfoService buildInfoService;
    private final UiConfiguration uiConfiguration;
    private final MobileConfiguration mobileConfiguration;
    private final AuthenticationConfiguration authConfiguration;
    private final PasswordManagementConfiguration passwordManagementConfiguration;
    private final OperationUserSupplier operationUser;
    private final SchedulerConfiguration schedulerConfig;
    private final EtlConfiguration etlConfiguration;
    private final CalendarServiceConfiguration calendarServiceConfiguration;
    private final EmailConfiguration emailConfiguration;

    public ConfigurationWs(DmsConfiguration dmsConfiguration, ObjectTranslationService translationService, MultitenantConfiguration multitenantConfiguration, WebSocketConfiguration websocketConfiguration, WorkflowConfiguration workflowConfig, GisConfiguration gisConfiguration, BimConfiguration bimConfiguration, CoreConfiguration coreConfiguration, GraphConfiguration graphConfiguration, BuildInfoService buildInfoService, UiConfiguration uiConfiguration, MobileConfiguration mobileConfiguration, AuthenticationConfiguration authConfiguration, PasswordManagementConfiguration passwordManagementConfiguration, OperationUserSupplier operationUser, SchedulerConfiguration schedulerConfig, EtlConfiguration etlConfiguration, CalendarServiceConfiguration calendarServiceConfiguration, EmailConfiguration emailConfiguration) {
        this.dmsConfiguration = checkNotNull(dmsConfiguration);
        this.translationService = checkNotNull(translationService);
        this.multitenantConfiguration = checkNotNull(multitenantConfiguration);
        this.websocketConfiguration = checkNotNull(websocketConfiguration);
        this.workflowConfig = checkNotNull(workflowConfig);
        this.gisConfiguration = checkNotNull(gisConfiguration);
        this.bimConfiguration = checkNotNull(bimConfiguration);
        this.coreConfiguration = checkNotNull(coreConfiguration);
        this.graphConfiguration = checkNotNull(graphConfiguration);
        this.buildInfoService = checkNotNull(buildInfoService);
        this.uiConfiguration = checkNotNull(uiConfiguration);
        this.mobileConfiguration = checkNotNull(mobileConfiguration);
        this.authConfiguration = checkNotNull(authConfiguration);
        this.passwordManagementConfiguration = checkNotNull(passwordManagementConfiguration);
        this.operationUser = checkNotNull(operationUser);
        this.schedulerConfig = checkNotNull(schedulerConfig);
        this.etlConfiguration = checkNotNull(etlConfiguration);
        this.calendarServiceConfiguration = checkNotNull(calendarServiceConfiguration);
        this.emailConfiguration = checkNotNull(emailConfiguration);
    }

    @GET
    @Path("public")
    public Object getPublicConfig() {
        return response(getPublicConfigData());
    }

    @GET
    @Path("system")
    public Object getSystemConfig() {
        return response(getPublicConfigData()
                .with(
                        "cm_system_logout_redirect", authConfiguration.getLogoutRedirectUrl(),
                        "cm_system_keepalive_enabled", coreConfiguration.sessionKeepaliveEnabled(),
                        "cm_system_dms_enabled", dmsConfiguration.isEnabled(),
                        "cm_system_dms_category", dmsConfiguration.getDefaultDmsCategory(),
                        "cm_system_dms_fileExtensionCheckEnabled", dmsConfiguration.isRegularAttachmentsFileExtensionCheckEnabled(),
                        "cm_system_dms_allowedFileExtensions", dmsConfiguration.getRegularAttachmentsAllowedFileExtensions(),
                        "cm_system_dms_maxFileSize", dmsConfiguration.getMaxFileSize(),
                        "cm_system_email_maxAttachmentSizeForEmail", emailConfiguration.getMaxAttachmentSizeForEmailMegs(),
                        "cm_system_workflow_enabled", workflowConfig.isEnabled(),
                        "cm_system_workflow_hideSaveButton", workflowConfig.hideSaveButton(),
                        "cm_system_workflow_enableAddAttachmentOnClosedActivities", workflowConfig.enableAddAttachmentOnClosedActivities(),
                        "cm_system_gis_enabled", gisConfiguration.isEnabled(),
                        "cm_system_gis_geoserver_enabled", gisConfiguration.isGeoServerEnabled(),
                        "cm_system_gis_navigation_enabled", gisConfiguration.isNavigationEnabled(),
                        "cm_system_bim_enabled", bimConfiguration.isEnabled(),
                        "cm_system_bim_viewer", bimConfiguration.getViewer(),
                        "cm_system_mobile_enabled", mobileConfiguration.isMobileEnabled(),
                        "cm_system_ui_detailwindow_width", uiConfiguration.getDetailWindowWidth(),
                        "cm_system_ui_detailwindow_height", uiConfiguration.getDetailWindowHeight(),
                        "cm_system_ui_inlinecard_height", uiConfiguration.getInlineCardHeight(),
                        "cm_system_ui_popupwindow_width", uiConfiguration.getPopupWindowWidth(),
                        "cm_system_ui_popupwindow_height", uiConfiguration.getPopupWindowHeight(),
                        "cm_system_ui_startDay", uiConfiguration.getStartDay(),
                        "cm_system_ui_referencecombolimit", uiConfiguration.getReferenceComboLimit(),
                        "cm_system_ui_dms_maxpreviewlimit", uiConfiguration.getDmsMaxPreviewLimit(),
                        "cm_system_ui_email_groupByStatus", uiConfiguration.getEmailGroupByStatus(),
                        "cm_system_ui_email_defaultDelay", uiConfiguration.getEmailDefaultDelay(),
                        "cm_system_cardlock_enabled", coreConfiguration.getCardlockEnabled(),
                        "cm_system_cardlock_showuser", coreConfiguration.getCardlockShowUser(),
                        "cm_system_keep_filter_on_updated_card", uiConfiguration.getKeepFilterOnUpdatedCard(),
                        "cm_system_ui_decimalsSeparator", uiConfiguration.getDecimalsSeparator(),
                        "cm_system_ui_thousandsSeparator", uiConfiguration.getThousandsSeparator(),
                        "cm_system_ui_dateFormat", uiConfiguration.getDateFormat(),
                        "cm_system_ui_timeFormat", uiConfiguration.getTimeFormat(),
                        "cm_system_ui_relationlimit", coreConfiguration.getRelationLimit(),
                        "cm_system_admin_users_changePasswordRequiredForNewUser", coreConfiguration.isChangePasswordRequiredForNewUser(),
                        "cm_system_scheduler_enabled", schedulerConfig.isEnabled(),
                        "cm_system_scheduler_selectableclasses", schedulerConfig.getSelectableClasses(),
                        "cm_system_lookuparray_value_separator", etlConfiguration.getValueArraySeparator(),
                        "cm_system_chat_enabled", coreConfiguration.isChatEnabled() && operationUser.hasPrivileges(p -> p.hasPrivileges(RP_CHAT_ACCESS)),
                        "cm_system_calendar_service_enabled", calendarServiceConfiguration.isEnabled()
                ).accept((m) -> {
                    graphConfiguration.getConfig().forEach((k, v) -> {
                        m.put(format("cm_system_relgraph_%s", k), v);
                    });
                    if (gisConfiguration.isEnabled()) {
                        m.putAll(map(
                                "centerLat", gisConfiguration.getCenterLat(),
                                "centerLon", gisConfiguration.getCenterLon(),
                                "initialZoomLevel", gisConfiguration.getInitialZoomLevel(),
                                "osmMinZoom", gisConfiguration.getOsmMinZoom(),
                                "osmMaxZoom", gisConfiguration.getOsmMaxZoom()
                        ).mapKeys(k -> format("cm_system_gis_%s", k)));
                    }
                    if (mobileConfiguration.isMobileEnabled()) {
                        m.putAll(map(
                                "customer_code", mobileConfiguration.getMobileCustomerCode(),
                                "devicename_prefix", mobileConfiguration.getMobileDeviceNamePrefix()
                        ).mapKeys(k -> format("cm_system_mobile_%s", k)));
                    }
                    if (operationUser.hasPrivileges(p -> p.hasPrivileges(RP_ADMIN_ACCESS))) {
                        m.put("cm_system_multitenant_mode", serializeEnum(multitenantConfiguration.getMultitenantMode()).toUpperCase());
                    }
                })
        );
    }

    private FluentMap<String, Object> getPublicConfigData() {
        return mapOf(String.class, Object.class).put(
                "cm_system_instance_name", coreConfiguration.getInstanceName(),
                "cm_system_instance_name_translation", translationService.translateByCode("config.core.instance_name", coreConfiguration.getInstanceName()),
                "cm_system_version", buildInfoService.getVersionNumber(),
                "cm_system_version_patch", buildInfoService.getPatchVersionNumber(),
                "cm_system_version_full", buildInfoService.getCompleteVersionNumberWithVertName(),
                "cm_system_language_default", coreConfiguration.getDefaultLanguage(),
                "cm_system_use_language_prompt", coreConfiguration.useLanguagePrompt(),
                "cm_system_multitenant_enabled", multitenantConfiguration.isMultitenantEnabled(),
                "cm_system_password_change_enabled", passwordManagementConfiguration.isPasswordChangeEnabled(),
                "cm_system_password_diff_previous", passwordManagementConfiguration.getDifferentFromPrevious(),
                "cm_system_password_diff_username", passwordManagementConfiguration.getDifferentFromUsername(),
                "cm_system_password_min_length", passwordManagementConfiguration.getPasswordMinLength(),
                "cm_system_password_req_digit", passwordManagementConfiguration.requireDigit(),
                "cm_system_password_req_lowercase", passwordManagementConfiguration.requireLowercase(),
                "cm_system_password_req_uppercase", passwordManagementConfiguration.requireUppercase(),
                "cm_system_password_enable", passwordManagementConfiguration.isPasswordManagementEnabled(),
                "cm_system_login_default_enabled", authConfiguration.isDefaultLoginModuleEnabled(),
                "cm_system_services_websocket_enabled", websocketConfiguration.isWebSocketEnabled(),
                "cm_system_login_input_source", ImmutableSet.copyOf(authConfiguration.getLoginInputSource()),
                "cm_system_login_mobile_disable_username", authConfiguration.isLoginMobileDisableUsername(), // TODO change to cm_system_login_mobile_username_enabled
                "cm_system_login_default_hidden", authConfiguration.isDefaultLoginModuleEnabled() && !authConfiguration.isDefaultLoginModuleEnabledAndVisible(),
                "cm_system_login_modules", authConfiguration.getNonDefaultNonHiddenActiveLoginModules().stream().map(LoginModuleConfiguration::getCode).collect(joining(",")),
                "cm_system_login_header_enabled", authConfiguration.isHeaderEnabled(),
                "cm_system_login_help", nullToEmpty(authConfiguration.getLoginHelp()))
                .skipNullValues().with(
                        "cm_system_company_logo", coreConfiguration.getCompanyLogoUploadsId(),
                        "cm_system_timeout", uiConfiguration.getUiTimeout()
                ).accept(m -> {
                    if (multitenantConfiguration.isMultitenantEnabled()) {
                        m.put("cm_system_multitenant_name", multitenantConfiguration.getTenantName());
                    }
                    authConfiguration.getNonDefaultNonHiddenActiveLoginModules().forEach(c -> {
                        m.put(format("cm_system_login_module_%s_description", c.getCode()), c.getDescription(),
                                format("cm_system_login_module_%s_icon", c.getCode()), c.getIcon()//TODO base64 icon (???)
                        );
                    });
                }).then();
    }

}
