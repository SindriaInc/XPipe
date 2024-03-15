/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.inner;

import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Maps.transformValues;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmCollectionUtils.toList;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public class Ws3RestRequestImpl implements Ws3RestRequest {

    private final Object inner;
    private final Map<String, List<String>> params;
    private final Map<String, String> headers;
    private final Map<String, Ws3Part> parts;
    private final String resourceUri;
    private final String payload;

    public Ws3RestRequestImpl(String resourceUri) {
        this(resourceUri, emptyMap());
    }

    public Ws3RestRequestImpl(String resourceUri, Map<String, ?> params) {
        this(resourceUri, params, null);
    }

    public Ws3RestRequestImpl(String resourceUri, Map<String, ?> params, @Nullable String payload) {
        this(resourceUri, params, emptyMap(), payload);
    }

    public Ws3RestRequestImpl(String resourceUri, Map<String, ?> params, Map<String, Ws3Part> parts, @Nullable String payload) {
        this(null, resourceUri, params, parts, emptyMap(), payload);
    }

    public Ws3RestRequestImpl(String resourceUri, Map<String, ?> params, List<Ws3Part> parts, @Nullable String payload) {
        this(null, resourceUri, params, map(parts, Ws3Part::getPartName), emptyMap(), payload);
    }

    public Ws3RestRequestImpl(@Nullable Object inner, String resourceUri, Map<String, ?> params, Map<String, Ws3Part> parts, Map<String, String> headers, @Nullable String payload) {
        this.resourceUri = checkNotBlank(resourceUri);
        this.params = map(params).mapValues((v) -> {
            if (v instanceof Iterable) {
                return toList((Iterable<String>) v);//TODO convert to string (?)
            } else {
                return singletonList(toStringOrNull(v));
            }
        }).immutable();
        this.headers = map(headers).mapKeys(v -> nullToEmpty(v).toLowerCase()).immutable();
        this.parts = map(parts).immutable();
        this.payload = payload;
        this.inner = inner;
    }

    @Override
    public String getResourceUri() {
        return resourceUri;
    }

    @Override
    public Map<String, String> getParams() {
        return transformValues(params, (v) -> getFirst(v, null));
    }

    @Override
    @Nullable
    public List<String> getParams(String key) {
        return params.get(key);
    }

    @Override
    @Nullable
    public String getPayload() {
        return payload;
    }

    @Override
    public Ws3RestRequest withParams(Map<String, String> otherParams) {
        return new Ws3RestRequestImpl(inner, resourceUri, map((Map) params).with(otherParams), parts, headers, payload);
    }

    @Nullable
    @Override
    public Object getInner() {
        return inner;
    }

    @Override
    public Map<String, Ws3Part> getParts() {
        return parts;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return "Ws3RestRequest{" + "uri=" + resourceUri + '}';
    }

}
