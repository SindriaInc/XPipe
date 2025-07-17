package org.sindria.xpipe.core.lib.nanorest.action;

import org.sindria.xpipe.core.lib.nanorest.request.Request;
import org.sindria.xpipe.core.lib.nanorest.response.RestResponse;

public interface Action {
    RestResponse execute(Request request);
}
