package org.cmdbuild.services.soap;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Collection;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.phase.PhaseInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.cmdbuild.auth.login.AuthenticationStore;
import org.cmdbuild.auth.login.LoginDataImpl;
import org.cmdbuild.auth.user.UserType;
import org.cmdbuild.services.soap.security.LoginAndGroup;
import org.cmdbuild.services.soap.security.PasswordHandler.AuthenticationStringHelper;
import org.cmdbuild.services.soap.utils.WebserviceUtils;
import org.slf4j.Logger;
import org.w3c.dom.Element;

import javax.inject.Named;
import org.cmdbuild.auth.session.SessionService;
import static org.cmdbuild.auth.user.SessionType.ST_BATCH;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("operationUserInterceptor")
public class OperationUserInterceptor extends AbstractSoapInterceptor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AuthenticationStore authenticationStore;
    private final SessionService sessionService;
    private final WSS4JInInterceptor delegate;

    public OperationUserInterceptor(@Named("cmdbuildPasswordCallbackInterceptor") WSS4JInInterceptor delegate, AuthenticationStore authenticationStore, SessionService sessionService) {
        super(Phase.PRE_INVOKE);
        this.delegate = checkNotNull(delegate);
        this.authenticationStore = checkNotNull(authenticationStore);
        this.sessionService = checkNotNull(sessionService);
    }

    @Override
    public Collection<PhaseInterceptor<? extends Message>> getAdditionalInterceptors() {
        return list(new PhaseInterceptor<SoapMessage>() {
            @Override
            public Set<String> getAfter() {
                return delegate.getAfter();
            }

            @Override
            public Set<String> getBefore() {
                return delegate.getBefore();
            }

            @Override
            public String getId() {
                return delegate.getId();
            }

            @Override
            public String getPhase() {
                return delegate.getPhase();
            }

            @Override
            public Collection<PhaseInterceptor<? extends Message>> getAdditionalInterceptors() {
                return delegate.getAdditionalInterceptors();
            }

            @Override
            public void handleMessage(SoapMessage message) throws Fault {
                if (message.hasHeader(QName.valueOf("CMDBuild-Authorization"))) {
                    //do nothing
                } else {
                    delegate.handleMessage(message);
                }
            }

            @Override
            public void handleFault(SoapMessage message) {
                delegate.handleFault(message);
            }

        });
    }

    @Override
    public Set<QName> getUnderstoodHeaders() {
        return delegate.getUnderstoodHeaders();
    }

    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        Header header = message.getHeader(QName.valueOf("CMDBuild-Authorization"));
        String token = (header == null) ? null : Element.class.cast(header.getObject()).getFirstChild().getTextContent();
        if (isNotBlank(token)) {
            logger.debug("found token =< {} >", token);
            if (sessionService.exists(token)) {
                sessionService.setCurrent(token);
            } else {
                throw runtime("invalid auth token =< %s >", token);
            }
        } else {
            String authData = new WebserviceUtils().getAuthData(message);
            AuthenticationStringHelper authenticationString = new AuthenticationStringHelper(authData);
            storeOperationUser(authenticationString);
        }
    }

    private void storeOperationUser(AuthenticationStringHelper authenticationString) {
        logger.debug("storing operation user for authentication string = {}", authenticationString);
        LoginAndGroup loginAndGroup = loginAndGroupFor(authenticationString);
        try {
            tryLogin(loginAndGroup);
            authenticationStore.setType(UserType.APPLICATION);
            authenticationStore.setLogin(loginAndGroup.getLogin());
        } catch (RuntimeException e) {
            logger.warn("error logging in", e);
            if (authenticationString.shouldImpersonate()) {
                /*
				 * fallback to the authentication login, should always work
                 */
                LoginAndGroup fallbackLogin = authenticationString.getAuthenticationLogin();
                String current = sessionService.getCurrentSessionIdOrNull();
                if (isNotBlank(current) && sessionService.exists(current)) {
                    sessionService.deleteSession(current);
                }
                tryLogin(fallbackLogin);
                authenticationStore.setLogin(loginAndGroup.getLogin());
                authenticationStore.setType(UserType.GUEST);
            } else {
                logger.error("cannot recover this error", e);
                throw e;
            }
        }
        wrapExistingOperationUser(authenticationString);
        logger.debug("operation user successfully stored");
    }

    private LoginAndGroup loginAndGroupFor(AuthenticationStringHelper authenticationString) {
        logger.debug("getting login and group for authentication string '{}'", authenticationString);
        LoginAndGroup authenticationLogin = authenticationString.getAuthenticationLogin();
        LoginAndGroup impersonationLogin = authenticationString.getImpersonationLogin();
        LoginAndGroup loginAndGroup;
        if (authenticationString.shouldImpersonate()) {
            logger.debug("should authenticate");
            /*
			 * should impersonate but authentication user can be a privileged
			 * service user
             */
//			if (isPrivilegedServiceUser(authenticationLogin)) {//TODO fix this
//				/*
//				 * we trust that the privileged service user has one group only
//				 */
//				loginAndGroup = LoginAndGroup.newInstance(authenticationLogin.getLogin());
//			} else {
            loginAndGroup = impersonationLogin;
//			}
        } else {
            loginAndGroup = authenticationLogin;
        }
        logger.debug("login and group are '{}'", loginAndGroup);
        return loginAndGroup;
    }

    private void wrapExistingOperationUser(AuthenticationStringHelper authenticationString) {
//		String current = sessionService.getCurrentSessionIdOrNull();
//		OperationUser operationUser = sessionService.getUser(current);
//		OperationUser wrapperOperationUser;
        if (authenticationString.shouldImpersonate()) {
            throw new UnsupportedOperationException("TODO");
//			logger.warn
            //TODO
//			LoginUser authenticatedUser = operationUser.getAuthenticatedUser();
//			if (isPrivilegedServiceUser(authenticationString.getAuthenticationLogin())) { //TODO fix this (workflow user name)
//				logger.debug("wrapping operation user with extended username");
//				final String username = authenticationString.getImpersonationLogin().getLogin().getValue();
//				final CMGroup group;
//				if (authenticationString.impersonateForcibly()) {
//					final CMGroup _group = sessionService
//							.getGroupWithName(authenticationString.getImpersonationLogin().getGroup());
//					if (_group == null) {
//						group = operationUser.getDefaultGroupOrNull();
//					} else {
//						group = _group;
//					}
//				} else {
//					group = operationUser.getDefaultGroupOrNull();
//				}
//				wrapperOperationUser = copyOf(operationUser).withAuthenticatedUser(AuthenticatedUserWithExtendedUsername.from(authenticatedUser, username)).withDefaultGroup(group).build();
//			} 
//			else
//			if (authenticationStore.getType() == UserType.DOMAIN) {
//				/*
//				 * we don't want that a User is represented by a Card of a user
//				 * class, so we login again with the authentication user
//				 *
//				 * at the end we keep the authenticated user with the privileges
//				 * of the impersonated... it's a total mess
//				 */
//				tryLogin(authenticationString.getAuthenticationLogin());
//				final OperationUser _operationUser = sessionService.getUser(current);
//				wrapperOperationUser = copyOf(operationUser).withAuthenticatedUser(AuthenticatedUserWithOtherGroups.from(_operationUser.getAuthenticatedUser(), authenticatedUser)).build();
//				authenticationStore.setType(UserType.DOMAIN);
//			} else {
//				wrapperOperationUser = operationUser;
//			}
//		} else {
//			wrapperOperationUser = operationUser;
        }
//		sessionService.setUser(current, wrapperOperationUser);
    }

//	private boolean isPrivilegedServiceUser(LoginAndGroup loginAndGroup) {
//		LoginUser user = userRepository.getUserOrNull(loginAndGroup.getLogin());
//		return user != null && user.isPrivileged();
//	}
    private void tryLogin(LoginAndGroup loginAndGroup) {
        logger.debug("trying login with '{}'", loginAndGroup);
        String id = sessionService.create(LoginDataImpl.builder()
                .withLoginString(loginAndGroup.getLogin().getValue())
                .withGroupName(loginAndGroup.getGroup())
                .withSessionType(ST_BATCH)
                .withNoPasswordRequired()
                .allowServiceUser()
                .build());
        sessionService.setCurrent(id);
    }

//	private LoginDataImpl loginFor(final LoginAndGroup loginAndGroup) {
//		return LoginDataImpl.builder() //
//				.withLoginString(loginAndGroup.getLogin().getValue()) //
//				.withGroupName(loginAndGroup.getGroup()) //
//				.withNoPasswordRequired() //
//				.build();
//	}
//	private static class AuthenticatedUserWithExtendedUsername extends ForwardingAuthenticatedUser {
//
//		public static AuthenticatedUserWithExtendedUsername from(final LoginUser authenticatedUser,
//				final String username) {
//			return new AuthenticatedUserWithExtendedUsername(authenticatedUser, username);
//		}
//
//		private static final String SYSTEM = "system";
//		private static final String FORMAT = "%s / %s";
//
//		private final LoginUser authenticatedUser;
//		private final String username;
//
//		private AuthenticatedUserWithExtendedUsername(final LoginUser authenticatedUser,
//				final String username) {
//			this.authenticatedUser = authenticatedUser;
//			this.username = username;
//		}
//
//		@Override
//		protected LoginUser delegate() {
//			return authenticatedUser;
//		}
//
//		@Override
//		public String getUsername() {
//			return String.format(FORMAT, SYSTEM, username);
//		}
//
//		@Override
//		public boolean isPasswordExpired() {
//			return authenticatedUser.isPasswordExpired();
//		}
//
//		@Override
//		public ZonedDateTime getPasswordExpirationTimestamp() {
//			return authenticatedUser.getPasswordExpirationTimestamp();
//		}
//
//		@Override
//		public ZonedDateTime getLastPasswordChangeTimestamp() {
//			return authenticatedUser.getLastPasswordChangeTimestamp();
//		}
//
//		@Override
//		public ZonedDateTime getLastExpiringNotificationTimestamp() {
//			return authenticatedUser.getLastExpiringNotificationTimestamp();
//		}
//
//	}
//
//	private static final class AuthenticatedUserWithOtherGroups extends ForwardingAuthenticatedUser {
//
//		public static AuthenticatedUserWithOtherGroups from(final LoginUser authenticatedUser,
//				final LoginUser userForGroups) {
//			return new AuthenticatedUserWithOtherGroups(authenticatedUser, userForGroups);
//		}
//
//		private final LoginUser authenticatedUser;
//		private final LoginUser userForGroups;
//
//		private AuthenticatedUserWithOtherGroups(final LoginUser authenticatedUser,
//				final LoginUser userForGroups) {
//			this.authenticatedUser = authenticatedUser;
//			this.userForGroups = userForGroups;
//		}
//
//		@Override
//		protected LoginUser delegate() {
//			return authenticatedUser;
//		}
//
//		@Override
//		public List<RoleInfo> getRoleInfos() {
//			return userForGroups.getRoleInfos();
//		}
//
//		@Override
//		public boolean isPasswordExpired() {
//			return authenticatedUser.isPasswordExpired();
//		}
//
//		@Override
//		public ZonedDateTime getPasswordExpirationTimestamp() {
//			return authenticatedUser.getPasswordExpirationTimestamp();
//		}
//
//		@Override
//		public ZonedDateTime getLastPasswordChangeTimestamp() {
//			return authenticatedUser.getLastPasswordChangeTimestamp();
//		}
//
//		@Override
//		public ZonedDateTime getLastExpiringNotificationTimestamp() {
//			return authenticatedUser.getLastExpiringNotificationTimestamp();
//		}
//
//	}
}
