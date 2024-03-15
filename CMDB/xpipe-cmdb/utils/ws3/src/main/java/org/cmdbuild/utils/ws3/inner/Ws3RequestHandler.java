/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.inner;

import org.cmdbuild.utils.ws3.api.Ws3WarningSource;

public interface Ws3RequestHandler {

    Ws3ResponseHandler handleRequest(Ws3RpcRequest request) throws Exception;

    Ws3ResponseHandler handleRequest(Ws3RestRequest request) throws Exception;

    Ws3WarningSource getWarningSource();

    default String handleRequestToString(Ws3RpcRequest request) throws Exception {
        return handleRequest(request).prepareResponse().getResponseAsString();
    }

    default String handleRequestToString(Ws3RestRequest request) throws Exception {
        return handleRequest(request).prepareResponse().getResponseAsString();
    }

}
