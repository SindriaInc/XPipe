package org.sindria.xpipe.lib.nanoREST.response;

import fi.iki.elonen.NanoHTTPD;

import java.io.InputStream;

public class ResponseOverride extends NanoHTTPD.Response {

    protected ResponseOverride(IStatus status, String mimeType, InputStream data, long totalBytes) {
        super(status, mimeType, data, totalBytes);
    }

}
