/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.webapp.security;

import java.util.function.Supplier;
import org.cmdbuild.minions.MinionService;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
public final class BootAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final MinionService bootService;

    public BootAuthorizationManager(MinionService bootService) {
        this.bootService = bootService;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        return new AuthorizationDecision(!bootService.isSystemReady());
    }
}
