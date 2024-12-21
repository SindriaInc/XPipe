package org.sindria.nanoREST.requests;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;

import java.util.*;

/**
 * BaseRequest represents an HTTP request.
 *
 * @author Luca Pitzoi <luca.pitzoi@sindria.org>
 */
public abstract class BaseRequest {

    private static final String HEADER_FORWARDED = "0b00001"; // When using RFC 7239
    private static final String HEADER_X_FORWARDED_FOR = "0b00010";
    private static final String HEADER_X_FORWARDED_HOST = "0b00100";
    private static final String HEADER_X_FORWARDED_PROTO = "0b01000";
    private static final String HEADER_X_FORWARDED_PORT = "0b10000";
    private static final String HEADER_X_FORWARDED_ALL = "0b11110"; // All "X-Forwarded-*" headers
    private static final String HEADER_X_FORWARDED_AWS_ELB = "0b11010"; // AWS ELB doesn't send X-Forwarded-Host

    private static String[] trustedProxies;


    private static final String METHOD_HEAD = "HEAD";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_PATCH = "PATCH";
    private static final String METHOD_DELETE = "DELETE";
    private static final String METHOD_PURGE = "PURGE";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String METHOD_TRACE = "TRACE";
    private static final String METHOD_CONNECT = "CONNECT";


    private final RouterNanoHTTPD.UriResource uriResourceHTTPD;
    private final Map<String, String> urlParamsHTTPD;
    private final NanoHTTPD.IHTTPSession sessionHTTPD;

    public String request;

    public Collection<String> query;

    public String files;

    public NanoHTTPD.CookieHandler cookies;

    public Map<String, String> headers;

    protected String content;

    protected String languages;

    protected String charsets;

    protected String encodings;

    public String requestUri;

    protected NanoHTTPD.Method method;

    protected String session;

    protected String locale;

    protected String defaultLocale = "en";

    protected static Map<String, String[]> formats;


    private final Boolean isHostValid = true;
    private final Boolean isForwardedValid = true;

    private static Integer trustedHeaderSet = -1;


    private static final Map<String, String> forwardedParams = new HashMap<>();
    static {
        forwardedParams.put(BaseRequest.HEADER_X_FORWARDED_FOR, "for");
        forwardedParams.put(BaseRequest.HEADER_X_FORWARDED_HOST, "host");
        forwardedParams.put(BaseRequest.HEADER_X_FORWARDED_PROTO, "proto");
        forwardedParams.put(BaseRequest.HEADER_X_FORWARDED_PORT, "host");
    };

    /**
     * Names for headers that can be trusted when
     * using trusted proxies.
     *
     * The FORWARDED header is the standard as of rfc7239.
     *
     * The other headers are non-standard, but widely used
     * by popular reverse proxies (like Apache mod_proxy or Amazon EC2).
     */
     private static final Map<String, String> trustedHeaders = new HashMap<>();
     static {
         trustedHeaders.put(BaseRequest.HEADER_FORWARDED, "FORWARDED");
         trustedHeaders.put(BaseRequest.HEADER_X_FORWARDED_FOR, "X_FORWARDED_FOR");
         trustedHeaders.put(BaseRequest.HEADER_X_FORWARDED_HOST, "X_FORWARDED_HOST");
         trustedHeaders.put(BaseRequest.HEADER_X_FORWARDED_PROTO, "X_FORWARDED_PROTO");
         trustedHeaders.put(BaseRequest.HEADER_X_FORWARDED_PORT, "X_FORWARDED_PORT");
     };

    /**
     * BaseRequest constructor
     */
    public BaseRequest(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
        this.uriResourceHTTPD = uriResource;
        this.urlParamsHTTPD = urlParams;
        this.sessionHTTPD = session;
        this.inizialize();
    }

    /**
     * Sets the parameters for this request.
     *
     * This method also re-initializes all properties.
     */
    public void inizialize() {
        this.query = this.urlParamsHTTPD.values();
        this.cookies = this.sessionHTTPD.getCookies();
        this.headers = this.sessionHTTPD.getHeaders();
        this.requestUri = this.uriResourceHTTPD.getUri();
        this.method = this.sessionHTTPD.getMethod();
    }


    /**
     * Initializes HTTP request formats.
     */
    protected static void initializeFormats()
    {
        BaseRequest.formats = new HashMap<>();
        BaseRequest.formats.put("html", new String[] {"text/html","application/xhtml+xml"});
        BaseRequest.formats.put("txt", new String[] {"text/plain"});
        BaseRequest.formats.put("js", new String[] {"application/javascript", "application/x-javascript", "text/javascript"});
        BaseRequest.formats.put("css", new String[] {"text/css"});
        BaseRequest.formats.put("json", new String[] {"application/json", "application/x-json"});
        BaseRequest.formats.put("jsonld", new String[] {"application/ld+json"});
        BaseRequest.formats.put("xml", new String[] {"text/xml", "application/xml", "application/x-xml"});
        BaseRequest.formats.put("rdf", new String[] {"application/rdf+xml"});
        BaseRequest.formats.put("atom", new String[] {"application/atom+xml"});
        BaseRequest.formats.put("rss", new String[] {"application/rss+xml"});
        BaseRequest.formats.put("form", new String[] {"application/x-www-form-urlencoded"});
    }


    public NanoHTTPD.Method getMethod() {
        return this.method;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public NanoHTTPD.CookieHandler getCookies() {
        return this.cookies;
    }

    /**
     * Sets a list of trusted proxies.
     *
     * You should only list the reverse proxies that you manage directly.
     */
    public static void setTrustedProxies(String[] proxies, Integer trustedHeaderSet) {
        int i = 0;
        for (String proxy: proxies) {
            BaseRequest.trustedProxies[i] = proxy;
            i++;
        }

        BaseRequest.trustedHeaderSet = trustedHeaderSet;
    }

    /**
     * Indicates whether this request originated from a trusted proxy.
     * true if the request came from a trusted proxy, false otherwise
     */
    public Boolean isFromTrustedProxy() {
        // TODO: implement
        return false;
    }

    /**
     * Checks whether the request is secure or not.
     *
     * This method can read the client protocol from the "X-Forwarded-Proto" header
     * when trusted proxies were set via "setTrustedProxies()".
     *
     * The "X-Forwarded-Proto" header must contain the protocol: "https" or "http".
     */
    public boolean isSecure() {
        // TODO: implement
        return false;
    }


    /**
     * Gets the request's scheme.
     */
    public String getScheme() {
        return this.isSecure() ? "https" : "http";
    }

    /**
     * Gets the list of trusted proxies.
     */
    public static String[] getTrustedProxies() {
        return BaseRequest.trustedProxies;
    }

    /**
     * Gets the set of trusted headers from trusted proxies.
     */
    public static Integer getTrustedHeaderSet() {
        return BaseRequest.trustedHeaderSet;
    }


    /**
     * Returns the client IP address.
     *
     * @see "https://wikipedia.org/wiki/X-Forwarded-For"
     */
    public String getClientIp() {
        return this.headers.get("http-client-ip");
    }

    /**
     * Returns the port on which the request is made.
     */
    public Integer getPort() {
        // TODO: implement
        return 80;
    }

    /**
     * Returns the host name.
     */
    public String getHost() {
        return this.headers.get("host");
    }

    /**
     * Returns the HTTP host being requested.
     *
     * The port name will be appended to the host if it's non-standard.
     */
    public String getHttpHost() {
        String scheme = this.getScheme();
        Integer port = this.getPort();

        if ((scheme.equals("http") && 80 == port) || (scheme.equals("https") && 443 == port)) {
            return this.getHost();
        }

        return this.getHost()+":"+port;
    }


    /**
     * Normalizes a query string.
     *
     * It builds a normalized query string, where keys/value pairs are alphabetized,
     * have consistent escaping and unneeded delimiters are removed.
     */
    public static String normalizeQueryString() {
        // TODO: implement
        return "";
    }

    /**
     * Returns true if the request is a XMLHttpRequest.
     *
     * It works if your JavaScript library sets an X-Requested-With HTTP header.
     * It is known to work with common JavaScript frameworks:
     *
     * @see "https://wikipedia.org/wiki/List_of_Ajax_frameworks#JavaScript"
     */
    public Boolean isXmlHttpRequest() {
        // TODO: implement
        return false;
    }

    /**
     * Gets the mime type associated with the format.
     *
     * The associated mime type (null if not found)
     */
    public String getMimeType(String format) {

        String mime = null;

        if (BaseRequest.formats == null) {
            BaseRequest.initializeFormats();
        }

        for (String f : BaseRequest.formats.keySet()) {
            if (format.equals(f)) {
                mime = BaseRequest.formats.get(f)[0];
            }
        }

        return mime;
    }

    /**
     * Gets the mime types associated with the format.
     *
     * The associated mime types
     */
    public String[] getMimeTypes(String format) {
        String[] mime = null;

        if (BaseRequest.formats == null) {
            BaseRequest.initializeFormats();
        }

        for (String f : BaseRequest.formats.keySet()) {
            if (format.equals(f)) {
                mime = BaseRequest.formats.get(f);
            }
        }

        return mime;
    }

    /**
     * Gets the format associated with the request.
     */
    public String getContentType() {
        return this.headers.get("content-type");
    }

    /**
     * Gets the user agent associated with the request.
     */
    public String getUserAgent() {
        return this.headers.get("user-agent");
    }

    /**
     * Checks if the request method is of specified type.
     */
    public Boolean isMethod(String method) {
        // TODO: implement
        return true;
    }

    /**
     * Checks whether the method is cacheable or not.
     * True for GET and HEAD, false otherwise
     *
     * @see "https://tools.ietf.org/html/rfc7231#section-4.2.3"
     */
    public Boolean isMethodCacheable() {
        // TODO: implement
        return false;
    }

    /**
     * Returns the protocol version.
     */
    public String getProtocolVersion() {
        // TODO: implement
        return "";
    }


    /**
     * Returns the request body content.
     */
    public String getContent() {
        // TODO: implement
        return "";
    }

    /**
     * Gets a list of languages acceptable by the client browser.
     */
    public String getLanguages() {
        if (this.languages != null) {
            return this.languages;
        }
        return this.headers.get("accept-language");
    }

    /**
     * Get the locale.
     */
    public String getLocale() {
        if (this.locale != null) {
            return this.locale;
        }
        // TODO: implement
        return null;
    }

    /**
     * Get the default locale.
     */
    public String getDefaultLocale() {
        return this.defaultLocale;
    }

    /**
     * Gets a list of charsets acceptable by the client browser.
     */
    public String getCharsets() {
        if (this.charsets != null) {
            return this.charsets;
        }
        // TODO: implement
        return null;
    }

    /**
     * Gets a list of encodings acceptable by the client browser.
     */
    public String getEncodings() {
        if (this.encodings != null) {
            return this.encodings;
        }
        // TODO: implement
        return null;
    }
}
