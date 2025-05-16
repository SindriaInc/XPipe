/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cluster;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.fault.FaultEventCollectorService;
import org.cmdbuild.temp.TempService;
import static org.cmdbuild.utils.io.CmIoUtils.countBytes;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import org.cmdbuild.utils.ws3.api.Ws3ResourceRepository;
import org.cmdbuild.utils.ws3.api.Ws3WarningSource;
import org.cmdbuild.utils.ws3.inner.Ws3RequestHandler;
import org.cmdbuild.utils.ws3.inner.Ws3RequestHandlerImpl;
import org.cmdbuild.utils.ws3.inner.Ws3ResponsePrinter;
import static org.cmdbuild.utils.ws3.utils.Ws3RpcUtils.parseRpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RpcHelperImpl implements RpcHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Ws3RequestHandler requestHandler;
    private final SessionService sessionService;
    private final FaultEventCollectorService faultService;
    private final TempService tempService;

    public RpcHelperImpl(TempService tempService, SessionService sessionService, Ws3ResourceRepository repository, Ws3WarningSource warningSource, FaultEventCollectorService faultService) {
        this.requestHandler = new Ws3RequestHandlerImpl(repository, warningSource);
        this.sessionService = checkNotNull(sessionService);
        this.faultService = checkNotNull(faultService);
        this.tempService = checkNotNull(tempService);
    }

    @Override
    public String invokeRpcMethod(@Nullable String sessionId, String payload) {
        if (isNotBlank(sessionId)) {
            sessionService.setCurrent(sessionId);
        }
        try {
            logger.debug("invoke rpc method =< {} >", abbreviate(payload));
            Ws3ResponsePrinter printer = requestHandler.handleRequest(parseRpcRequest(payload)).prepareResponse();
            if (printer.isJson()) {//TODO check also for `attachment` file download headers
                return printer.getResponseAsString();
            } else {
                DataSource responseData = printer.getResponseAsDataSource();
//                Map<String, String> responseHeaders = response.getResponseHeaders();
                String tempId = tempService.putTempData(responseData);//TODO set temp ttl, pin temp data to current session id; TODO store other headers
//                Content-Disposition: inline; filename="cmdbuild_sys.log"//TODO
                return toJson(map("success", true, "_response", map(
                        "type", "download",
                        "downloadId", tempId,
                        "contentType", responseData.getContentType(),
                        "fileName", responseData.getName(),
                        "size", countBytes(responseData)
                )));
            }
        } catch (Exception ex) {
            logger.error("error processing request", ex);
            return toJson(map("success", false, "messages", faultService.buildMessagesForJsonResponse(ex)));
        } finally {
            if (isNotBlank(sessionId)) {
                sessionService.setCurrent(null);
            }
        }
    }

}
