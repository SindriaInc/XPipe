package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import static java.util.stream.Collectors.toList;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import org.cmdbuild.common.localization.LanguageInfo;
import org.cmdbuild.common.localization.LanguageService;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import jakarta.annotation.security.RolesAllowed;

import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ACCESS_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;

@Path("languages")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class LanguagesWs {

    private final LanguageService languageService;

    public LanguagesWs(LanguageService languageService) {
        this.languageService = checkNotNull(languageService);
    }

    @GET
    @Path("")
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
    public Object readLanguages(@QueryParam("active") @DefaultValue(FALSE) Boolean activeOnly) {
        Collection<LanguageInfo> list = activeOnly ? languageService.getEnabledLanguagesInfo() : languageService.getAllLanguages();
        return response(list.stream().map(LanguagesConfigurationWs::languageInfoToResponse).collect(toList()));
    }

}
