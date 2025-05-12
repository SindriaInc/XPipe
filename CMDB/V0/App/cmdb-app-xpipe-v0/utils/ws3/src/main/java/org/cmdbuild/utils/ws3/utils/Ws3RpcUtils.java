/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Streams.stream;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.ws3.inner.Ws3RpcRequest;
import org.cmdbuild.utils.ws3.inner.Ws3RpcRequestImpl;
import static org.cmdbuild.utils.ws3.utils.Ws3Utils.WS3RPC_BATCH_REQUEST;
import static org.cmdbuild.utils.ws3.utils.Ws3Utils.buildWs3RpcResourceUri;

public class Ws3RpcUtils {

    private final static WsRpcRequestData EMPTY_DATA = new WsRpcRequestData(null, null, null, null, null, null, null, null);

    public static Ws3RpcRequest parseRpcRequest(String payload) {
        return buildRpcRequest(null, checkNotBlank(payload), emptyMap(), emptyMap(), null);
    }

    public static Ws3RpcRequest buildRpcRequest(@Nullable String resourceUri, @Nullable String payload, Map<String, String> requestParams, Map<String, String> requestHeaders, @Nullable Object request) {
        WsRpcRequestData data = isBlank(payload) ? EMPTY_DATA : readPayloadToData(payload);
        if (isBlank(resourceUri)) {
            resourceUri = data.buildResourceUri();
        }
        return new Ws3RpcRequestImpl(
                data.id,
                resourceUri,
                map(requestParams).with(data.params),
                map(requestHeaders).with(data.headers),
                data.data,
                request,
                data.extract,
                data.batch.stream().map(r -> new Ws3RpcRequestImpl(r.id, buildWs3RpcResourceUri(r.service, r.method), r.params, requestHeaders, r.data, request, r.extract, emptyList())).collect(toImmutableList()));
    }

    public static WsRpcRequestData readPayloadToData(String payload) {
        checkNotBlank(payload);
        JsonNode json = fromJson(payload, JsonNode.class);
        if (json.isArray()) {
            List<WsRpcRequestData> elements = stream(json.elements()).map(e -> fromJson(e, WsRpcRequestData.class)).collect(toList());
            return new WsRpcRequestData(null, null, null, null, null, null, elements, null);
        } else {
            return fromJson(json, WsRpcRequestData.class);
        }
    }

    public static String serializeWs3RpcRequest(Ws3RpcRequest request) {
        return toJson(map(
                "service", request.getService(),
                "method", request.getMethod(),
                "id", request.getId(),
                "params", request.getParams(),
                "headers", request.getHeaders(),
                "extract", request.getBindingsToExtractForNextBatchRequests(),
                "data", fromJson(request.getPayload(), JsonNode.class),
                "batch", request.getBatchRequests().stream().map(Ws3RpcUtils::serializeWs3RpcRequest).collect(toImmutableList())
        ));
    }

    public static class WsRpcRequestData {

        private final String service, method, id;
        private final Map<String, String> params, headers, extract;
        private final List<WsRpcRequestData> batch;
        private final JsonNode data;

        public WsRpcRequestData(
                @JsonProperty("service") String service,
                @JsonProperty("method") String method,
                @JsonProperty("id") String id,
                @JsonProperty("params") Map<String, String> params,
                @JsonProperty("headers") Map<String, String> headers,
                @JsonProperty("extract") Map<String, String> extract,
                @JsonProperty("batch") List<WsRpcRequestData> batch,
                @JsonProperty("data") JsonNode data) {
            this.id = id;
            this.params = firstNotNull(params, emptyMap());
            this.headers = firstNotNull(headers, emptyMap());
            this.extract = firstNotNull(extract, emptyMap());
            this.batch = firstNotNull(batch, emptyList());
            this.data = data;
            this.service = service;
            this.method = method;
        }

        public boolean isBatch() {
            return !batch.isEmpty();
        }

        private String buildResourceUri() {
            if (isBatch()) {
                return WS3RPC_BATCH_REQUEST;
            } else {
                return buildWs3RpcResourceUri(service, method);
            }
        }

    }
}
