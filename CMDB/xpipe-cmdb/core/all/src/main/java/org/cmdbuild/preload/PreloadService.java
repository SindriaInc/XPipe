/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.preload;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cmdbuild.config.CoreConfiguration; 
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.url.CmUrlUtils.decodeUrlPathAndParams;
import org.cmdbuild.utils.url.UrlPathAndParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.utils.ws3.api.Ws3ResourceRepository;
import org.cmdbuild.utils.ws3.inner.Ws3RequestHandler;
import org.cmdbuild.utils.ws3.inner.Ws3RequestHandlerImpl;
import org.cmdbuild.utils.ws3.inner.Ws3RestRequestImpl;
import org.cmdbuild.minions.PostStartup;

@Component
public class PreloadService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CoreConfiguration config;
    private final Ws3ResourceRepository ws3ResourceRepository;

    public PreloadService(CoreConfiguration config, Ws3ResourceRepository ws3ResourceRepository) {
        this.config = checkNotNull(config);
        this.ws3ResourceRepository = checkNotNull(ws3ResourceRepository);
    }

    public void runPreload() {
        runPreloadAtStartup();
    }

    @PostStartup(delay = "PT5s" )
    public void runPreloadAtStartup() {
        if (config.isPreloadEnabled() && !config.getPreloadRestUrls().isEmpty()) {
            logger.info("preload configured rest resources");
            Ws3RequestHandler requestHandler = new Ws3RequestHandlerImpl(ws3ResourceRepository);
            config.getPreloadRestUrls().forEach(r -> {
                try {
                    logger.info("preload resource =< {} >", r);
                    UrlPathAndParams pathAndParams = decodeUrlPathAndParams(r);
                    String path = pathAndParams.getPath();
                    //TODO check this (localization ?? )
                    String requestType;
                    Matcher matcher = Pattern.compile("(.*):(.*)").matcher(r);
                    if (matcher.matches()) {
                        requestType = matcher.group(1);
                        path = path.replace(requestType + ":", "");
                    } else {
                        requestType = "get";
                    }
                    requestHandler.handleRequest(new Ws3RestRequestImpl(null, format("ws3rest:%s:%s", requestType, path), pathAndParams.getParams(), map(), map("CMDBuild-Localization", config.getDefaultLanguage()), null));
                } catch (Exception ex) {
                    logger.warn("error preloading resource =< {} >", r, ex);
                }
            });
            logger.info("preload completed");
        }
    }

}
