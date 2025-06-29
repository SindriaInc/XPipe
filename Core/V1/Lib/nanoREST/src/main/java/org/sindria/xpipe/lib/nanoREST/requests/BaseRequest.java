package org.sindria.xpipe.lib.nanoREST.requests;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;

import java.util.*;

/**
 * BaseRequest represents an HTTP request.
 *
 * Inspired by Laravel/Symfony request APIs.
 */
public abstract class BaseRequest {

    private static final String HEADER_FORWARDED = "0b00001";
    private static final String HEADER_X_FORWARDED_FOR = "0b00010";
    private static final String HEADER_X_FORWARDED_HOST = "0b00100";
    private static final String HEADER_X_FORWARDED_PROTO = "0b01000";
    private static final String HEADER_X_FORWARDED_PORT = "0b10000";

    private static String[] trustedProxies = new String[0];
    private static Integer trustedHeaderSet = -1;

    private final RouterNanoHTTPD.UriResource uriResourceHTTPD;
    private final Map<String, String> urlParamsHTTPD;
    private final NanoHTTPD.IHTTPSession sessionHTTPD;

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

    public BaseRequest(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
        this.uriResourceHTTPD = uriResource;
        this.urlParamsHTTPD = urlParams;
        this.sessionHTTPD = session;
        this.inizialize();
    }

    public void inizialize() {
        this.query = this.urlParamsHTTPD.values();
        this.cookies = this.sessionHTTPD.getCookies();
        this.headers = this.sessionHTTPD.getHeaders();
        this.requestUri = this.uriResourceHTTPD.getUri();
        this.method = this.sessionHTTPD.getMethod();
    }

    protected static void initializeFormats() {
        BaseRequest.formats = new HashMap<>();
        formats.put("html", new String[]{"text/html", "application/xhtml+xml"});
        formats.put("txt", new String[]{"text/plain"});
        formats.put("js", new String[]{"application/javascript", "application/x-javascript", "text/javascript"});
        formats.put("css", new String[]{"text/css"});
        formats.put("json", new String[]{"application/json", "application/x-json"});
        formats.put("jsonld", new String[]{"application/ld+json"});
        formats.put("xml", new String[]{"text/xml", "application/xml", "application/x-xml"});
        formats.put("rdf", new String[]{"application/rdf+xml"});
        formats.put("atom", new String[]{"application/atom+xml"});
        formats.put("rss", new String[]{"application/rss+xml"});
        formats.put("form", new String[]{"application/x-www-form-urlencoded"});
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

    public static void setTrustedProxies(String[] proxies, Integer trustedHeaderSet) {
        BaseRequest.trustedProxies = proxies;
        BaseRequest.trustedHeaderSet = trustedHeaderSet;
    }

    public Boolean isFromTrustedProxy() {
        if (trustedProxies == null || trustedProxies.length == 0) {
            return false;
        }

        String remoteAddr = headers.get("remote-addr");
        if (remoteAddr == null) {
            return false;
        }

        for (String proxy : trustedProxies) {
            if (remoteAddr.equals(proxy)) {
                return true;
            }
        }

        return false;
    }


    public boolean isSecure() {
        if (isFromTrustedProxy()) {
            String proto = headers.getOrDefault("x-forwarded-proto", "http");
            return proto.equalsIgnoreCase("https");
        }
        return false;
    }

    public String getScheme() {
        return this.isSecure() ? "https" : "http";
    }

    public static String[] getTrustedProxies() {
        return BaseRequest.trustedProxies;
    }

    public static Integer getTrustedHeaderSet() {
        return BaseRequest.trustedHeaderSet;
    }

    public String getClientIp() {
        if (isFromTrustedProxy()) {
            String forwardedFor = headers.get("x-forwarded-for");
            if (forwardedFor != null) {
                return forwardedFor.split(",")[0].trim();
            }
        }

        // Fallback: da header "remote-addr" se disponibile
        return headers.getOrDefault("remote-addr", "127.0.0.1");
    }


    public Integer getPort() {
        if (isFromTrustedProxy()) {
            String portStr = headers.get("x-forwarded-port");
            if (portStr != null) {
                try {
                    return Integer.parseInt(portStr);
                } catch (NumberFormatException ignored) {}
            }
        }
        return 80;
    }

    public String getHost() {
        return headers.get("host");
    }

    public String getHttpHost() {
        String scheme = this.getScheme();
        Integer port = this.getPort();

        if ((scheme.equals("http") && 80 == port) || (scheme.equals("https") && 443 == port)) {
            return this.getHost();
        }

        return this.getHost() + ":" + port;
    }

    public static String normalizeQueryString(Map<String, List<String>> queryParams) {
        List<String> pairs = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
            String key = entry.getKey();
            for (String value : entry.getValue()) {
                pairs.add(key + "=" + value);
            }
        }

        Collections.sort(pairs);
        return String.join("&", pairs);
    }

    public Boolean isXmlHttpRequest() {
        String requestedWith = headers.get("x-requested-with");
        return requestedWith != null && requestedWith.equalsIgnoreCase("XMLHttpRequest");
    }

    public String getMimeType(String format) {
        if (BaseRequest.formats == null) {
            BaseRequest.initializeFormats();
        }
        return formats.containsKey(format) ? formats.get(format)[0] : null;
    }

    public String[] getMimeTypes(String format) {
        if (BaseRequest.formats == null) {
            BaseRequest.initializeFormats();
        }
        return formats.getOrDefault(format, null);
    }

    public String getContentType() {
        return headers.get("content-type");
    }

    public String getUserAgent() {
        return headers.get("user-agent");
    }

    public Boolean isMethod(String method) {
        return this.method.name().equalsIgnoreCase(method);
    }

    public Boolean isMethodCacheable() {
        return this.method == NanoHTTPD.Method.GET || this.method == NanoHTTPD.Method.HEAD;
    }

    public String getProtocolVersion() {
        return headers.getOrDefault("protocol", "HTTP/1.1");
    }

    public String getContent() {
        if (this.content != null) {
            return this.content;
        }

        Map<String, String> files = new HashMap<>();
        try {
            sessionHTTPD.parseBody(files);
            this.content = files.get("postData");
        } catch (Exception e) {
            e.printStackTrace();
            this.content = "";
        }

        return this.content;
    }

    public String getLanguages() {
        if (this.languages != null) {
            return this.languages;
        }
        return headers.get("accept-language");
    }

    public String getLocale() {
        if (this.locale != null) {
            return this.locale;
        }

        String acceptLanguage = getLanguages();
        if (acceptLanguage != null && !acceptLanguage.isEmpty()) {
            String[] langs = acceptLanguage.split(",");
            if (langs.length > 0) {
                this.locale = langs[0].split(";")[0];
                return this.locale;
            }
        }

        return this.defaultLocale;
    }

    public String getDefaultLocale() {
        return this.defaultLocale;
    }

    public String getCharsets() {
        if (this.charsets != null) return this.charsets;
        return headers.getOrDefault("accept-charset", "UTF-8");
    }

    public String getEncodings() {
        if (this.encodings != null) return this.encodings;
        return headers.getOrDefault("accept-encoding", "");
    }
}
