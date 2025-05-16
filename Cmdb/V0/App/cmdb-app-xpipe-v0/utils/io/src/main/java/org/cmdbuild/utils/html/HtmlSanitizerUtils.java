/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.html;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Maps.transformValues;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.owasp.html.AttributePolicy;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlSanitizerUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /* taken from org.owasp.html.Sanitizers */
    private static final AttributePolicy INTEGER = (String elementName, String attributeName, String value) -> {
        int n = value.length();
        if (n == 0) {
            return null;
        }
        for (int i = 0; i < n; ++i) {
            char ch = value.charAt(i);
            if (ch == '.') {
                if (i == 0) {
                    return null;
                }
                return value.substring(0, i);  // truncate to integer.
            } else if (!('0' <= ch && ch <= '9')) {
                return null;
            }
        }
        return value;
    };

    private final static PolicyFactory DEFAULT_POLICY_FACTORY = Sanitizers.FORMATTING.and(Sanitizers.STYLES).and(Sanitizers.BLOCKS).and(Sanitizers.LINKS).and(Sanitizers.TABLES),
            EMAIL_POLICY_FACTORY = Sanitizers.FORMATTING.and(Sanitizers.STYLES).and(Sanitizers.BLOCKS).and(
                    /* taken and modified from org.owasp.html.Sanitizers */
                    new HtmlPolicyBuilder()
                            .allowUrlProtocols("http", "https", "data", "cid").allowElements("img")
                            .allowAttributes("alt", "src").onElements("img")
                            .allowAttributes("border", "height", "width").matching(INTEGER)
                            .onElements("img")
                            .toFactory()
            ).and(Sanitizers.LINKS).and(Sanitizers.TABLES),
            LINK_POLICY_FACTORY = new HtmlPolicyBuilder().allowStandardUrlProtocols().allowUrlProtocols("ftp", "ftps", "ws", "wss").allowElements("a").allowAttributes("href", "target").onElements("a").toFactory();

    @Nullable
    public static String sanitizeHtmlForEmail(@Nullable String str) {
        return sanitizeHtml(str, EMAIL_POLICY_FACTORY);
    }

    @Nullable
    public static String sanitizeHtml(@Nullable String str) {
        return sanitizeHtml(str, DEFAULT_POLICY_FACTORY);
    }

    @Nullable
    public static String sanitizeHtmlForLink(@Nullable String str) {
        return sanitizeHtml(str, LINK_POLICY_FACTORY);
    }

    public static boolean isSafeHtml(@Nullable String html) {
        return isBlank(html) || equal(html, LINK_POLICY_FACTORY.sanitize(html));
    }

    @Nullable
    private static String sanitizeHtml(@Nullable String str, PolicyFactory policy) {
        if (isBlank(str)) {
            return str;
        } else {
            String sanitized = policy.sanitize(str);
            if (!equal(sanitized, str)) {
                LOGGER.trace("sanitized html\n=== source html BEGIN ===\n{}\n== source html END; sanitized html BEGIN ===\n{}\n===sanitized html END ===", str, sanitized);
            }
            return sanitized;
        }
    }

    public static <K, V> Map<K, V> sanitizeHtmlInMapValues(Map<K, V> map) {
        return newLinkedHashMap(transformValues(map, (V o) -> o instanceof String ? (V) sanitizeHtml((String) o) : o));
    }

}
