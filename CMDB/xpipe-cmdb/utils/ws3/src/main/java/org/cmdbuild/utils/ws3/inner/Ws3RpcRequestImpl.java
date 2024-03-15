/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.inner;

import com.fasterxml.jackson.databind.JsonNode;
import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.ws3.utils.Ws3Utils.WS3RPC_BATCH_REQUEST;

public class Ws3RpcRequestImpl implements Ws3RpcRequest {

    private final String rpcUri, id;
    private final Map<String, String> params, headers, bindings;
    private final JsonNode payload;
    private final Object inner;
    private final List<Ws3RpcRequest> batchRequests;

    public Ws3RpcRequestImpl(@Nullable String id, String rpcUri, Map<String, String> params, Map<String, String> headers, @Nullable JsonNode payload, @Nullable Object inner, Map<String, String> bindings, List<Ws3RpcRequest> batchRequests) {
        this.rpcUri = checkNotBlank(rpcUri);
        this.id = id;
        this.params = map(params).immutable();
        this.headers = map(headers).immutable();
        this.payload = payload;
        this.inner = inner;
        switch (rpcUri) {
            case WS3RPC_BATCH_REQUEST:
                this.batchRequests = ImmutableList.copyOf(batchRequests);
                checkArgument(!this.batchRequests.isEmpty(), "invalid batch request with empty batch list");
                this.bindings = emptyMap();
                break;
            default:
                this.batchRequests = emptyList();
                this.bindings = ImmutableMap.copyOf(bindings);
        }
    }

    @Override
    public String getRequestUri() {
        return rpcUri;
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    @Nullable
    @Override
    public String getPayload() {
        return payload == null || payload.isNull() ? null : toJson(payload);//TODO improve this
    }

    @Nullable
    @Override
    public Object getInner() {
        return inner;
    }

    @Override
    public Map<String, Ws3Part> getParts() {
        return emptyMap();//TODO
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    @Nullable
    public String getId() {
        return id;
    }

    @Override
    public Map<String, String> getBindingsToExtractForNextBatchRequests() {
        return bindings;
    }

    @Override
    public List<Ws3RpcRequest> getBatchRequests() {
        return batchRequests;
    }

    @Override
    public String toString() {
        return "Ws3RpcRequest{" + "uri=" + rpcUri + '}';
    }

}
