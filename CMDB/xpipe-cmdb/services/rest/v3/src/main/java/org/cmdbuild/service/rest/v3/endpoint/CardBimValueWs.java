/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import org.cmdbuild.bim.BimObject;
import org.cmdbuild.bim.BimService;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CARD_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("{a:processes|classes}/{" + CLASS_ID + "}/{b:cards|instances}/{" + CARD_ID + "}/bimvalue")
@Produces(APPLICATION_JSON)
public class CardBimValueWs {

    private final DaoService dao;
    private final BimService bimService;

    public CardBimValueWs(DaoService dao, BimService bimService) {
        this.dao = checkNotNull(dao);
        this.bimService = checkNotNull(bimService);
    }

    @GET
    @Path("")
    public Object getAllForCard(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @QueryParam("if_exists") @DefaultValue(FALSE) Boolean checkIfExists, @QueryParam("include_related") @DefaultValue(FALSE) Boolean includeRelated) {
        Card card = dao.getCard(classId, cardId);
        BimObject bimObject = includeRelated ? bimService.getBimObjectForCardOrViaNavTreeOrNull(card) : bimService.getBimObjectForCardOrNull(card);
        checkArgument(checkIfExists || bimObject != null, "bim object not found for car = %s", card);
        return response(map().accept(m -> {
            if (bimObject != null) {
                m.put("_id", bimObject.getId(),
                        "projectId", bimObject.getProjectId(),
                        "globalId", bimObject.getGlobalId(),
                        "_owner_type", bimObject.getOwnerClassId(),
                        "_owner_id", bimObject.getOwnerCardId());
            }
            if (checkIfExists) {
                m.put("exists", bimObject != null);
            }
        }));
    }

}
