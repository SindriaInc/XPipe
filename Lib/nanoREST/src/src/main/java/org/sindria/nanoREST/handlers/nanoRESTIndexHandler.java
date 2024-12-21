package org.sindria.nanoREST.handlers;

import fi.iki.elonen.router.RouterNanoHTTPD;

public class nanoRESTIndexHandler extends RouterNanoHTTPD.IndexHandler {

    @Override
    public String getText() {
        return "<html><body><center><h1>nanoREST is up and running</h1></center><br /><br /><center>Copyright Sindria Inc.</center></body></html>";
    }

}