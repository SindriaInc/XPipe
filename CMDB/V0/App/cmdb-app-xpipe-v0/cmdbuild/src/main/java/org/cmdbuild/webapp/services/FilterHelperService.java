/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.services;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static javax.ws.rs.core.HttpHeaders.SET_COOKIE;
import org.cmdbuild.auth.session.SessionService;
import static org.cmdbuild.common.http.HttpConst.CMDBUILD_AUTHORIZATION_COOKIE;
import org.cmdbuild.config.UiFilterConfiguration;
import org.cmdbuild.ui.UiBaseUrlService;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.ws3.utils.Ws3CookieUtils.buildSetCookieHeader;
import org.springframework.stereotype.Component;

@Component
public class FilterHelperService {

    private final UiFilterConfiguration config;
    private final UiBaseUrlService baseUrlService;
    private final SessionService sessionService;

    public FilterHelperService(UiFilterConfiguration config, UiBaseUrlService baseUrlService, SessionService sessionService) {
        this.config = checkNotNull(config);
        this.baseUrlService = checkNotNull(baseUrlService);
        this.sessionService = checkNotNull(sessionService);
    }

    public String getLoginRedirectUrl(HttpServletRequest request) {
        return format("%s/ui/#login", baseUrlService.getBaseUrl(request));
    }

    public void addSessionCookie(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader(SET_COOKIE, buildSetCookieHeader(CMDBUILD_AUTHORIZATION_COOKIE, sessionService.getCurrentSessionId(), config.getCookieMaxAgeSeconds(), firstNotBlank(request.getContextPath(), "/"), config.enableCookieSecure(request.isSecure()), true, config.getCookieSameSiteMode()));
    }

}
