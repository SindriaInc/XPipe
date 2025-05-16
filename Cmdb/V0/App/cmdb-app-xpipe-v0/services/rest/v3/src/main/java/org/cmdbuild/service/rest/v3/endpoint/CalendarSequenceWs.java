package org.cmdbuild.service.rest.v3.endpoint;

import org.cmdbuild.service.rest.v3.model.WsSequenceData;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.function.Consumer;
import static java.util.stream.Collectors.toList;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.cmdbuild.calendar.CalendarService;
import org.cmdbuild.calendar.beans.CalendarEvent;
import org.cmdbuild.calendar.beans.CalendarSequence;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import org.cmdbuild.service.rest.common.serializationhelpers.CalendarWsSerializationHelper;

@Path("calendar/sequences/")
@Produces(APPLICATION_JSON)
public class CalendarSequenceWs {

    private final CalendarService service;
    private final CalendarWsSerializationHelper helper;

    public CalendarSequenceWs(CalendarService service, CalendarWsSerializationHelper helper) {
        this.service = checkNotNull(service);
        this.helper = checkNotNull(helper);
    }

    @GET
    @Path("{sequenceId}/")
    public Object readOne(@PathParam("sequenceId") Long sequenceId, @QueryParam("includeEvents") @DefaultValue(FALSE) boolean includeEvents) {
        return response(helper.serializeDetailedSequence(includeEvents ? service.getSequenceIncludeEvents(sequenceId) : service.getSequence(sequenceId))); //TODO card access control, sequence access control 
    }

    @GET
    @Path("by-card/{cardId}")
    public Object readManyByCard(@PathParam("cardId") Long cardId, @QueryParam("detailed") @DefaultValue(FALSE) boolean detailed, @QueryParam("includeEvents") @DefaultValue(FALSE) boolean includeEvents) {
        List<CalendarSequence> sequences = includeEvents ? service.getSequencesByCardIncludeEvents(cardId) : service.getSequencesByCard(cardId); //TODO card access control, sequence access control 
        return response(sequences.stream().map(detailed ? helper::serializeDetailedSequence : CalendarWsSerializationHelper::serializeBasicSequence));
    }

    @POST
    @Path(EMPTY)
    public Object create(WsSequenceData data) {
        CalendarSequence sequence = data.toSequence(service).accept((Consumer) service.fixTimeZone()).build();
        sequence = service.createSequence(sequence);
        return response(helper.serializeDetailedSequence(sequence));
    }

    @PUT
    @Path("{sequenceId}/")
    public Object update(@PathParam("sequenceId") Long sequenceId, WsSequenceData data) {
        CalendarSequence sequence = service.getSequence(sequenceId);
        sequence = service.updateSequence(data.toSequence(service).withId(sequence.getId()).build());
        return response(helper.serializeDetailedSequence(sequence));
    }

    @DELETE
    @Path("{sequenceId}/")
    public Object delete(@PathParam("sequenceId") Long sequenceId) {
        service.deleteSequence(sequenceId);
        return success();
    }

    @POST
    @Path("_ANY/generate-events")
    public Object getEventsPreview(WsSequenceData data) {
        List<CalendarEvent> events = service.buildEventsFromSequence(data.toSequence(service).build());
        return response(events.stream().map(helper::serializeDetailedEvent).collect(toList()));
    }

}
