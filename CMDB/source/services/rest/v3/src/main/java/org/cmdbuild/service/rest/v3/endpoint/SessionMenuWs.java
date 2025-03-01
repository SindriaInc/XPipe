package org.cmdbuild.service.rest.v3.endpoint;

import org.cmdbuild.service.rest.v3.helpers.SessionWsCommons;
import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import org.cmdbuild.auth.session.SessionService;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.menu.Menu;

import org.cmdbuild.menu.MenuService;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import org.cmdbuild.service.rest.v3.serializationhelpers.MenuSerializationHelper;

@Path("sessions/{sessionId}/menu")
@Produces(APPLICATION_JSON)
public class SessionMenuWs extends SessionWsCommons {

    private final MenuService menuService;
    private final MenuSerializationHelper helper;

    public SessionMenuWs(MenuService menuService, MenuSerializationHelper helper, SessionService sessionService) {
        super(sessionService);
        this.menuService = checkNotNull(menuService);
        this.helper = checkNotNull(helper);
    }

    @GET
    @Path("")
    public Object read(@PathParam("sessionId") String sessionId, @QueryParam("flat") @DefaultValue(FALSE) Boolean flatMenu) {
        checkIsCurrent(sessionId);
        Menu menu = menuService.getMenuForCurrentUser();
        return response(flatMenu ? helper.serializeFlatUserMenu(menu) : helper.serializeUserMenu(menu));
    }

}
