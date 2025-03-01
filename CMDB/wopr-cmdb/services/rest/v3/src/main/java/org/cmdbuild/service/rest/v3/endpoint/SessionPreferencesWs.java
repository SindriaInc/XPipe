package org.cmdbuild.service.rest.v3.endpoint;

//import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.session.model.Session;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.config.CoreConfiguration;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ID;
import org.cmdbuild.service.rest.v3.helpers.SessionWsCommons;
import org.cmdbuild.userconfig.UserConfigService;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrNull;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;

@Path("sessions/{" + ID + "}/preferences")//it is actually user preferences
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class SessionPreferencesWs extends SessionWsCommons {

    private final UserConfigService userPreferencesStore;
    private final CoreConfiguration coreConfiguration;

    public SessionPreferencesWs(SessionService sessionService, UserConfigService userPreferencesStore, CoreConfiguration coreConfiguration) {
        super(sessionService);
        this.userPreferencesStore = checkNotNull(userPreferencesStore);
        this.coreConfiguration = checkNotNull(coreConfiguration);
    }

    @GET
    @Path(EMPTY)
    public Object read(@PathParam(ID) String sessionId) {
        logger.debug("read preferences for session = {}", sessionId);
        sessionId = sessionIdOrCurrent(sessionId);
        //TODO validate sessionId = current session id OR isAdmin()
        Session session = sessionService.getSessionById(sessionId);
        Map<String, Object> data = getUserConfig(session.getOperationUser());
        return response(data);
    }

    @GET
    @Path("{key}")
    @Produces(TEXT_PLAIN)
    public String getUserConfig(@PathParam(ID) String sessionId, @PathParam("key") String key) {
        sessionId = sessionIdOrCurrent(sessionId);
        //TODO validate sessionId = current session id OR isAdmin()
        Session session = sessionService.getSessionById(sessionId);
        return toStringOrEmpty(getUserConfig(session.getOperationUser()).get(key));
    }

    @PUT
    @Path("{key}")
    @Consumes(TEXT_PLAIN)
    public Object updateUserConfigValue(@PathParam(ID) String sessionId, @PathParam("key") String key, String value) {
        sessionId = sessionIdOrCurrent(sessionId);
        //TODO validate sessionId = current session id OR isAdmin()
        Session session = sessionService.getSessionById(sessionId);
        userPreferencesStore.setByUsername(session.getOperationUser().getLoginUser().getUsername(), key, value);
        return success();
    }

    @POST
    @Path("")
    public Object updateUserConfigValues(@PathParam(ID) String sessionId, Map<String, String> data) {
        logger.info("update user preferences with data = {}", data);
        sessionId = sessionIdOrCurrent(sessionId);
        //TODO validate sessionId = current session id OR isAdmin()
        Session session = sessionService.getSessionById(sessionId);
        userPreferencesStore.updateByUsername(session.getOperationUser().getUsername(), data);
        return response(getUserConfig(session.getOperationUser()));
    }

    @DELETE
    @Path("{key}")
    public Object deleteSystemConfigValue(@PathParam(ID) String sessionId, @PathParam("key") String key) {
        sessionId = sessionIdOrCurrent(sessionId);
        //TODO validate sessionId = current session id OR isAdmin()
        Session session = sessionService.getSessionById(sessionId);
        userPreferencesStore.deleteByUsername(session.getOperationUser().getLoginUser().getUsername(), key); 
        return success();
    }

    private Map<String, Object> getUserConfig(OperationUser operationUser) {
        Map<String, String> userConfig = userPreferencesStore.getByUsername(operationUser.getUsername());//TODO merge lang and starting class from here
        String initialPage = firstNotBlankOrNull(userConfig.get("cm_ui_startingClass"), userConfig.get("cm_user_initialPage"));
        if (isBlank(initialPage) && operationUser.hasDefaultGroup() && operationUser.getDefaultGroup().hasStartingClass()) {
            initialPage = operationUser.getDefaultGroup().getStartingClass();
        }
        if (isBlank(initialPage)) {
            initialPage = coreConfiguration.getStartingClassName();
        }
        return (Map) map()
                .with(userConfig)
                .accept((m) -> {
                    if (operationUser.hasDefaultGroup()) {
                        m.put(
                                //								"cm_ui_disabledModules", configuration.getDisabledModules(),
                                //								"cm_ui_disabledCardTabs", configuration.getDisabledCardTabs(),
                                //								"cm_ui_disabledProcessTabs", configuration.getDisabledProcessTabs(),
                                //								"cm_ui_hideSidePanel", configuration.isHideSidePanel(),
                                //								"cm_ui_fullScreenMode", configuration.isFullScreenMode(),
                                //								"cm_ui_simpleHistoryModeForCard", configuration.isSimpleHistoryModeForCard(),
                                //								"cm_ui_simpleHistoryModeForProcess", configuration.isSimpleHistoryModeForProcess(),
                                "cm_ui_processWidgetAlwaysEnabled", operationUser.getDefaultGroup().getConfig().getProcessWidgetAlwaysEnabled());
                    }
                })
                .with("_cm_ui_startingClass_actual", initialPage);
    }
}
