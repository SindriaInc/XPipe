package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import org.cmdbuild.chat.PeerService;
import org.cmdbuild.chat.ChatPeer;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import static org.cmdbuild.dao.utils.SorterProcessor.sorted;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeLocal;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("sessions/current/peers")
@Produces(APPLICATION_JSON)
public class ChatPeersWs {

    private final PeerService service;

    public ChatPeersWs(PeerService service) {
        this.service = checkNotNull(service);
    }

    @GET
    @Path("")
    public Object getPeers(WsQueryOptions options) {
        List peers = list(service.getPeersForCurrentSession()).map(this::serializePeer);
        if (!options.getQuery().getFilter().isNoop()) {
            options.getQuery().getFilter().checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
            peers = AttributeFilterProcessor.builder().withFilter(options.getQuery().getFilter().getAttributeFilter()).filter(peers);
        }
        if (!options.getQuery().getSorter().isNoop()) {
            peers = sorted(peers, options.getQuery().getSorter());
        }
        return response(paged(peers, options.getOffset(), options.getLimit()));
    }

    private FluentMap<String, Object> serializePeer(ChatPeer peer) {
        return map(
                "_id", peer.getUsername(),
                "username", peer.getUsername(),
                "description", peer.getDescription(),
                "_hasMessages", peer.hasMessages(),
                "_hasNewMessages", peer.hasNewMessages(),
                "_newMessagesCount", peer.getNewMessageCount(),
                "_lastMessageTimestamp", toIsoDateTimeLocal(peer.getLastMessageTimestamp()),
                "icon", service.getIconUrl(peer)
        );
    }
}
