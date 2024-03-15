/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.servlet;

import static com.google.common.base.Preconditions.checkArgument;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import org.cmdbuild.utils.ws3.inner.Ws3BatchRequestHelper;
import org.cmdbuild.utils.ws3.inner.Ws3ResponseHandler;
import org.cmdbuild.utils.ws3.inner.Ws3RestRequest;
import org.cmdbuild.utils.ws3.inner.Ws3RpcRequest;
import org.cmdbuild.utils.ws3.utils.Ws3Exception;
import static org.cmdbuild.utils.ws3.utils.Ws3Utils.buildWs3RpcResourceUri;
import static org.cmdbuild.utils.ws3.utils.Ws3RpcUtils.buildRpcRequest;

public class Ws3RpcRequestHandlerServlet extends Ws3AbstractHandlerServlet {

    @Override
    protected Ws3ResponseHandler handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getPathInfo();
        Ws3RestRequest restRequest = buildWs3RestRequest(request, "dummy");
        String resourceUri;
        String payload;
        if (isNotBlank(restRequest.getParam("request"))) {
            payload = restRequest.getParam("request");
        } else if (restRequest.hasPart("request")) {
            payload = readToString(restRequest.getPart("request").getDataSource());
        } else if (restRequest.hasPayload()) {
            payload = restRequest.getPayload();
        } else {
            payload = null;
        }

        if (isNotBlank(path)) {
            Matcher matcher = Pattern.compile("^/?([^/]+)/([^/]+)/?$").matcher(path);
            checkArgument(matcher.matches(), "invalid rpc request path =< %s >", path);
            resourceUri = buildWs3RpcResourceUri(matcher.group(1), matcher.group(2));
        } else if (isNotBlank(restRequest.getParam("service")) && isNotBlank(restRequest.getParam("method"))) {
            resourceUri = buildWs3RpcResourceUri(restRequest.getParam("service"), restRequest.getParam("method"));
        } else {
            resourceUri = null;
        }
        Ws3RpcRequest rpcRequest = buildRpcRequest(resourceUri, payload, restRequest.getParams(), restRequest.getHeaders(), restRequest.getInner());
        try {
            logger.debug("processing ws3 rpc request =< {} >", resourceUri);
            if (rpcRequest.isBatch()) {
                return new Ws3BatchRequestHelper(getHandler()).handleBatchRequest(rpcRequest);
            } else {
                return getHandler().handleRequest(rpcRequest);
            }
        } catch (Exception ex) {
            throw new Ws3Exception(ex, "error processing request =< %s >", resourceUri);
        }
    }

}
