/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.security;

import org.cmdbuild.auth.session.dao.beans.AuthenticationToken;
import static com.google.common.base.Preconditions.checkNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.cmdbuild.auth.user.OperationUserStack;
import org.cmdbuild.auth.session.SessionService;

/**
 *
 */
@Component
public class AuthenticationProviderImpl implements AuthenticationProvider {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final SessionService sessionService;

	public AuthenticationProviderImpl(SessionService sessionService) {
		this.sessionService = checkNotNull(sessionService);
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		AuthenticationToken myAuthenticationToken = (AuthenticationToken) authentication;
		logger.trace("authenticate user = {}", myAuthenticationToken);

		sessionService.setCurrent((String) myAuthenticationToken.getPrincipal()); //this actually set current user for legacy framework; note that session token has already been validated by auth filter

		OperationUserStack operationUser = sessionService.getCurrentSession().getOperationUser();
		logger.trace("operationUser from session = {}", operationUser);
		AuthenticatedUser user = new AuthenticatedUser(operationUser); // this will be stored in session, but will not actually be used by legacy code

		return user;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return AuthenticationToken.class.isAssignableFrom(authentication);
	}

}
