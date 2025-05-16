package org.cmdbuild.chat;

import java.util.List;
import javax.annotation.Nullable;

public interface PeerService {

    List<ChatPeer> getPeersForCurrentSession();

    @Nullable
    String getIconUrl(ChatPeer peer);

}
