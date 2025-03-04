/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cmdbuild.utils.ws3.inner.Ws3ResponseHandler;
import org.cmdbuild.utils.ws3.inner.Ws3RestRequest;
import org.cmdbuild.utils.ws3.utils.Ws3Exception;
import static org.cmdbuild.utils.ws3.utils.Ws3Utils.buildWs3RestResourceUri;

public final class Ws3RestRequestHandlerServlet extends Ws3AbstractHandlerServlet {

    @Override
    protected Ws3ResponseHandler handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String method = request.getMethod(), path = request.getPathInfo();
        String resourceUri = buildWs3RestResourceUri(method, path);
        try {
            Ws3RestRequest ws3Request = buildWs3RestRequest(request, resourceUri);
            logger.debug("processing ws3 rest request =< {} >", resourceUri);
            return getHandler().handleRequest(ws3Request);
        } catch (Exception ex) {
            throw new Ws3Exception(ex, "error processing request =< %s >", resourceUri);
        }
    }
}
