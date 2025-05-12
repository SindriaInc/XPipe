/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.eventbus;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import java.util.Map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface EventBusService {

    Map<String, EventBus> getAllByCode();

    /** 
     * events: {@link DaoEvent} 
     */
    EventBus getDaoEventBus();

    /**
     * events: {@link GrantDataUpdatedEvent}
     */
    EventBus getGrantEventBus();

    /**
     * events: {@link CardEvent}
     */
    EventBus getCardEventBus();

    /**
     * events: {@link FlowUpdatedEvent}
     */
    EventBus getWorkflowEventBus();

    /**
     * events: {@link FilterUpdateEvent}
     */
    EventBus getFilterEventBus();

    EventBus getWaterwayEventBus();

    EventBus getContextEventBus();

    EventBus getRequestEventBus();

    EventBus getSystemEventBus();

    EventBus getSysCommandEventBus();

    EventBus getClusterMessagesEventBus();

    EventBus getJobRunEventBus();

    EventBus getConfigEventBus();

    default EventBus getByCode(String code) {
        return checkNotNull(getAllByCode().get(checkNotBlank(code)), "eventbus not found for code =< %s >", code);
    }
}
