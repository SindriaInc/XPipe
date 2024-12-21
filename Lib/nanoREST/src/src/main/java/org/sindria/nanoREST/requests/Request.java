package org.sindria.nanoREST.requests;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;
import org.sindria.nanoREST.requests.validators.Validator;

import java.util.Map;

public class Request extends BaseRequest {

    /**
     * Validator singleton instance
     */
    public Validator validator;

    /**
     * Request constructor
     */
    public Request(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
        super(uriResource, urlParams, session);
        this.validator = Validator.getInstance();
    }

    /**
     * Get the request method.
     */
    public NanoHTTPD.Method method() {
        return this.getMethod();
    }

    /**
     * Determine if the request is over HTTPS.
     */
    public Boolean secure() {
        return this.isSecure();
    }

    /**
     * Get the client user agent.
     */
    public String userAgent() {
        return this.getUserAgent();
    }

    /**
     * Get the client IP address.
     */
    public String ip() {
        return this.getClientIp();
    }

    /**
     * Determine if the request is the result of an AJAX call.
     */
    public Boolean ajax() {
        return this.isXmlHttpRequest();
    }
}
