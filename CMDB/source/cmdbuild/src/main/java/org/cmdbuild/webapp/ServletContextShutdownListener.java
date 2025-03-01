/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.cmdbuild.minions.MinionService;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@WebListener
public class ServletContextShutdownListener implements ServletContextListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void contextInitialized(ServletContextEvent event) {
        //do nothing
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        logger.info("received context destroyed event");
        try {
            WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(event.getServletContext());
            RequestContextService requestContextService = applicationContext.getBean(RequestContextService.class);
            requestContextService.initCurrentRequestContext("servlet context destroyed event");
            try {
                MinionService systemService = applicationContext.getBean(MinionService.class);
                systemService.stopSystem();
            } finally {
                requestContextService.destroyCurrentRequestContext();
            }
        } catch (Exception ex) {
            logger.error("error processing context destroyed event", ex);
            throw runtime(ex);
        }
    }

}
