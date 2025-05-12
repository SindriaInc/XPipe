package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.cmdbuild.calendar.CalendarService;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import static org.cmdbuild.dao.utils.CmFilterUtils.merge;
import static org.cmdbuild.dao.utils.CmFilterUtils.parseFilter;
import org.cmdbuild.service.rest.common.serializationhelpers.CalendarWsSerializationHelper;
import static org.cmdbuild.service.rest.common.serializationhelpers.CalendarWsSerializationHelper.CAL_ATTR_MAPPING;
import org.cmdbuild.view.View;
import org.cmdbuild.view.ViewService;
import static org.cmdbuild.cleanup.ViewType.VT_CALENDAR;

@Path("calendar/views/{viewId}/events/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CalendarViewEventWs {

    private final CalendarService service;
    private final CalendarWsSerializationHelper helper;
    private final ViewService viewService;

    public CalendarViewEventWs(CalendarService service, CalendarWsSerializationHelper helper, ViewService viewService) {
        this.service = checkNotNull(service);
        this.helper = checkNotNull(helper);
        this.viewService = checkNotNull(viewService);
    }

    @GET
    @Path("{eventId}")
    public Object readOne(@PathParam("eventId") Long eventId) {
        //TODO access control
        return response(helper.serializeDetailedEvent(service.getEventById(eventId)));
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@PathParam("viewId") String viewId, @QueryParam(LIMIT) Long limit, @QueryParam(START) Long offset, @QueryParam("detailed") @DefaultValue(FALSE) boolean detailed, @QueryParam(FILTER) String filterStr) {
        View view = viewService.getSharedForCurrentUserByNameOrId(viewId);
        checkArgument(view.isOfType(VT_CALENDAR));
        return response(service.getUserEvents(DaoQueryOptionsImpl.builder().withFilter(merge(parseFilter(filterStr), parseFilter(view.getFilter()))).withPaging(offset, limit).build().mapAttrNames(CAL_ATTR_MAPPING))
                .map(detailed ? helper::serializeDetailedEvent : helper::serializeEvent));
    }

}
