/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.endpoint;

import org.cmdbuild.service.rest.v3.model.WsTriggerData;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.function.Consumer;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.cmdbuild.calendar.CalendarService;
import org.cmdbuild.calendar.beans.CalendarSequence;
import org.cmdbuild.calendar.beans.CalendarTrigger;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.CalendarWsSerializationHelper;
import org.cmdbuild.service.rest.v3.utils.InMemoryQueryProcessor;
import static org.cmdbuild.utils.date.CmDateUtils.toDate;
import jakarta.annotation.security.RolesAllowed;
import org.apache.commons.lang3.math.NumberUtils;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_CLASSES_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_CLASSES_VIEW_AUTHORITY;
import org.cmdbuild.classe.access.UserCardService;
import org.cmdbuild.dao.beans.Card;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("calendar/triggers/")
@Produces(APPLICATION_JSON)
public class CalendarTriggerWs {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CalendarService service;
    private final CalendarWsSerializationHelper helper;
    private final UserCardService cardService;

    public CalendarTriggerWs(CalendarService service, CalendarWsSerializationHelper helper, UserCardService cardService) {
        this.service = checkNotNull(service);
        this.helper = checkNotNull(helper);
        this.cardService = checkNotNull(cardService);
    }

    @GET
    @Path("{triggerId}/")
    public Object readOne(@PathParam("triggerId") Long triggerId) {
        return response(helper.serializeDetailedTrigger(service.getTriggerById(triggerId)));
    }

    @GET
    @Path("{triggerId}/generate-sequence")
    public Object getSequencePreview(@PathParam("triggerId") Long triggerId, @QueryParam("date") String dateValue) {
        CalendarSequence sequence = service.buildSequenceFromTrigger(triggerId, toDate(dateValue));
        return response(helper.serializeDetailedSequence(sequence));
    }

    @POST
    @Path("{triggerId}/create-events")
    public Object createEvents(@PathParam("triggerId") String triggerIdOrCode, WsQueryOptions query) {
        CalendarTrigger trigger;
        if (NumberUtils.isCreatable(triggerIdOrCode)) {
            trigger = service.getTriggerById(toLong(triggerIdOrCode));
        } else {
            trigger = service.getTriggerByCode(triggerIdOrCode);
        }
        List<Card> cards = cardService.getUserCards(trigger.getOwnerClass(), query.getQuery()).elements();
        logger.info("create events for {} cards", cards.size());
        cards.forEach(c -> service.createSequenceFromTrigger(trigger.getId(), c));
        return success();
    }

    @GET
    @Path(EMPTY)
    @RolesAllowed(ADMIN_CLASSES_VIEW_AUTHORITY)
    public Object readMany(WsQueryOptions query) {
        List<CalendarTrigger> triggers = service.getAllTriggers();
        return InMemoryQueryProcessor.toResponse(triggers, query.getQuery(), query.isDetailed(), helper::serializeBasicTrigger, helper::serializeDetailedTrigger);
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object create(WsTriggerData data) {
        CalendarTrigger trigger = data.toTrigger().accept((Consumer) service.fixTimeZone()).build();
        trigger = service.createTrigger(trigger);
        return response(helper.serializeDetailedTrigger(trigger));
    }

    @PUT
    @Path("{triggerId}/")
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object update(@PathParam("triggerId") Long triggerId, WsTriggerData data) {
        CalendarTrigger trigger = service.getTriggerById(triggerId);
        trigger = service.updateTrigger(data.toTrigger().withId(trigger.getId()).build());
        return response(helper.serializeDetailedTrigger(trigger));
    }

    @DELETE
    @Path("{triggerId}/")
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object delete(@PathParam("triggerId") Long triggerId) {
        service.deleteTrigger(triggerId);
        return success();
    }

}
