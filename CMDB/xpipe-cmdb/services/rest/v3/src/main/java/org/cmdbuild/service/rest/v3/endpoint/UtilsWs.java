package org.cmdbuild.service.rest.v3.endpoint;

import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.SYSTEM_ACCESS_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils.encryptValueIfNotEncrypted;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.springframework.web.bind.annotation.RequestBody;

@Path("utils/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
public class UtilsWs {

    private static final String CRYPTO_VALUE = "value";

    @POST
    @Path("crypto/encrypt")
    @Consumes(APPLICATION_JSON)
    public Object encryptValue(@RequestBody Map<String, String> payload) {
        return response(map("encrypted", encryptValueIfNotEncrypted(payload.get(CRYPTO_VALUE))));
    }

}
