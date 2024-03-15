/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.inner;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.ws3.utils.Ws3Utils.parseWs3RpcResourceUri;

public interface Ws3RpcRequest extends Ws3Request {

    String getRequestUri();

    @Nullable
    String getId();

    Map<String, String> getBindingsToExtractForNextBatchRequests();

    List<Ws3RpcRequest> getBatchRequests();

    default String getService() {
        return parseWs3RpcResourceUri(getRequestUri()).getService();
    }

    default String getMethod() {
        return parseWs3RpcResourceUri(getRequestUri()).getMethod();
    }

    default boolean isBatch() {
        return !getBatchRequests().isEmpty();
    }

    default boolean hasId() {
        return isNotBlank(getId());
    }

}
