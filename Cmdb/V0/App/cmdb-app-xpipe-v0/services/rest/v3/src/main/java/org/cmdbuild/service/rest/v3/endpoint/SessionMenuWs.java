package org.cmdbuild.service.rest.v3.endpoint;

import org.cmdbuild.service.rest.v3.helpers.SessionWsCommons;
import static com.google.common.base.Preconditions.checkNotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
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
