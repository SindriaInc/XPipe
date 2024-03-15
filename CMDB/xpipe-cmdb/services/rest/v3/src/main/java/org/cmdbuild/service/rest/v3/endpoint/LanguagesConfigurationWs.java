package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import static java.util.stream.Collectors.toList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import org.cmdbuild.common.localization.LanguageInfo;
import org.cmdbuild.common.localization.LanguageService;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("configuration/languages/")
@Produces(APPLICATION_JSON)
public class LanguagesConfigurationWs {

    private final LanguageService languageService;

    public LanguagesConfigurationWs(LanguageService languageService) {
        this.languageService = checkNotNull(languageService);
    }

    /**
     * return a list of available languages. Does not require
     * authentication.<br>
     * return format is like this:
     * <pre><code>
     * {
     *  "data": [{
     *    "code": "en",
     *    "description": "English"
     *   },{
     *    "code": "sr",
     *    "description": "Srpski"
     *   },{
     *    "code": "ru",
     *    "description": "Русский"
     *   }]
     * }
     * </code></pre>
     *
     * @return language list
     */
    @GET
    @Path("")
    public Object getLoginLanguages() {
        Collection<LanguageInfo> list = languageService.getLoginLanguagesInfo();
        return response(list.stream().map(LanguagesConfigurationWs::languageInfoToResponse).collect(toList()));
    }

    public static Object languageInfoToResponse(LanguageInfo l) {
        return map("code", l.getCode(), "description", l.getDescription());
    }

}
