/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp;

import java.io.IOException;
import javax.servlet.GenericServlet;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import static org.cmdbuild.common.http.HttpConst.CMDBUILD_REQUEST_ID_HEADER;
import org.cmdbuild.webapp.filters.RequestTrackingFilterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorServlet extends GenericServlet {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        logger.debug("processing error response");
        Exception ex = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        String requestId = (String) request.getAttribute(CMDBUILD_REQUEST_ID_HEADER);
        if (!(ex instanceof RequestTrackingFilterException)) {
            logger.error("error processing request = {}", requestId, ex);
        }
        ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("text/plain");
        response.getWriter().printf("error processing request %s\n", requestId);//TODO html error message, json error message
        logger.debug("error response sent");
    }

}
