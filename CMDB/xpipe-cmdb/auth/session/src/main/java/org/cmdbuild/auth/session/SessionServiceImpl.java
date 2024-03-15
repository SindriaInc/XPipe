/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.session;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import org.cmdbuild.auth.session.inner.CurrentSessionHolder;
import org.cmdbuild.auth.session.inner.SessionDataService;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import static java.lang.String.format;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.audit.RequestCompleteEvent;
import org.cmdbuild.auth.user.OperationUserStore;
import org.cmdbuild.auth.user.OperationUser;
import static org.cmdbuild.auth.user.OperationUserImpl.anonymousOperationUser;
import org.cmdbuild.auth.login.LoginDataImpl;
import org.springframework.stereotype.Component;
import static org.cmdbuild.auth.session.model.SessionImpl.copyOf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.auth.session.model.Session;
import org.cmdbuild.auth.session.model.SessionData;
import org.cmdbuild.auth.login.LoginData;
import static org.cmdbuild.utils.random.CmRandomUtils.DEFAULT_RANDOM_ID_SIZE;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.cmdbuild.auth.session.dao.SessionRepository;
import org.cmdbuild.auth.login.AuthenticationService;
import org.cmdbuild.auth.login.RequestAuthenticatorResponse;
import static org.cmdbuild.auth.login.RequestAuthenticatorResponseImpl.emptyResponse;
import static org.cmdbuild.auth.session.SessionExpirationStrategy.ES_DEFAULT;
import org.cmdbuild.auth.session.dao.beans.AuthenticationToken;
import org.cmdbuild.auth.session.model.SessionImpl;
import org.cmdbuild.auth.user.LoginUserImpl;
import static org.cmdbuild.auth.user.OperationUser.USER_ATTR_SESSION_ID;
import org.cmdbuild.auth.user.OperationUserImpl;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.lock.LockService;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.encode.CmEncodeUtils.xdecodeString;
import static org.cmdbuild.utils.encode.CmEncodeUtils.xencodeString;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Component
public class SessionServiceImpl implements SessionService {

    public static final int SESSION_TOKEN_SIZE = DEFAULT_RANDOM_ID_SIZE;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CurrentSessionHolder sessionHolder;
    private final LockService lockService;
    private final SessionRepository sessionRepository;
    private final OperationUserStore userStore;
    private final AuthenticationService authenticationService;
    private final SessionDataService sessionDataService;
    private final EventBus eventBus;

    public SessionServiceImpl(LockService lockService, CurrentSessionHolder sessionHolder, SessionRepository sessionStore, OperationUserStore userStore, AuthenticationService authenticationLogic, EventBusService eventBusService, SessionDataService sessionDataService) {
        this.sessionRepository = checkNotNull(sessionStore);
        this.userStore = checkNotNull(userStore);
        this.authenticationService = checkNotNull(authenticationLogic);
        this.sessionDataService = checkNotNull(sessionDataService);
        this.sessionHolder = checkNotNull(sessionHolder);
        this.lockService = checkNotNull(lockService);
        eventBus = eventBusService.getContextEventBus();
        eventBusService.getRequestEventBus().register(new Object() {
            @Subscribe
            public void handleRequestCompleteEvent(RequestCompleteEvent event) {
                Session session = getCurrentSessionOrNull();
                if (session != null) {
                    updateSession(session);
                }
            }
        });

    }

    @Override
    public void validateSessionId(String sessionId) {
        getSessionById(sessionId);//TODO replace with more efficent query
    }

    @Override
    public Session getSessionById(String sessionId) {
        return checkNotNull(getSessionByIdOrNull(sessionId), "cannot find session for id = %s", sessionId);
    }

    @Override
    public List<Session> getAllSessions() {
        return sessionRepository.getAllSessions();
    }

    @Override
    @Nullable
    public Session getSessionByIdOrNull(String sessionId) {
        if (isTransientSessionToken(sessionId)) {
            return buildTransientSessionFromToken(sessionId);
        } else {
            return sessionRepository.getSessionByIdOrNull(sessionId);
        }
    }

    @Override
    public int getActiveSessionCount() {
        return sessionRepository.getActiveSessionCount();
    }

    @Override
    public String create(LoginData login) {
        OperationUser user = authenticationService.validateCredentialsAndCreateOperationUser(login);
        String sessionId = login.noPersist() ? createTransientSessionToken(user.getUsername()) : createSessionToken();
        user = OperationUserImpl.copyOf(user).withParam(USER_ATTR_SESSION_ID, sessionId).build();
        updateSession(sessionId, user);
        return sessionId;
    }

    @Override
    public void update(String sessionId, LoginData login) {
        Session session = getSessionById(sessionId);
        OperationUser user = authenticationService.updateOperationUser(login, session.getOperationUser());
        user = OperationUserImpl.copyOf(user).withParam(USER_ATTR_SESSION_ID, sessionId).build();
        updateSession(sessionId, user);
        if (equal(sessionId, getCurrentSessionIdOrNull())) {
            userStore.setUser(session.getOperationUser());
            eventBus.post(CurrentSessionSetOrUpdateEvent.INSTANCE);
        }
    }

    @Override
    public boolean exists(String id) {
        try {
            getSessionById(id); //TODO more efficient query; do not rely on exception for control flow
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public RequestAuthenticatorResponse<Void> deleteSession(String sessionId, @Nullable Object request) {
        Session session = getSessionById(sessionId);
        RequestAuthenticatorResponse<Void> response;
        if (session.getOperationUser().isInteractive()) {//TODO  check this
            response = authenticationService.invalidateCredentialsAndCreateLogoutResponse(request);
        } else {
            response = emptyResponse();
        }
        if (!isTransientSessionToken(sessionId)) {
            sessionRepository.deleteSession(session.getSessionId());
        }
        //TODO event?? // CurrentSessionSetOrUpdateEvent
        return response;
    }

    @Override
    public void deleteAll() {
        sessionRepository.deleteAll();
    }

    @Override
    public void impersonate(ImpersonateRequest request) {
        doWithLockOnSession(() -> {
            OperationUser user = authenticationService.validateCredentialsAndCreateOperationUser(LoginDataImpl.builder().withNoPasswordRequired().withServiceUsersAllowed(true).withForceUserGroup(true)
                    .withLoginString(request.hasUsername() ? request.getUsername() : getCurrentSession().getOperationUser().getUsername()).withGroupName(request.getGroup()).build());
            if (request.hasSponsor()) {
                user = OperationUserImpl.copyOf(user).withSponsor(LoginUserImpl.build(request.getSponsor())).build();
            }
            doImpersonate(user);
        });
    }

    @Override
    public void impersonate(OperationUser imp) {
        doWithLockOnSession(() -> {
            doImpersonate(imp);
        });
    }

    @Override
    public void deimpersonate() {
        doWithLockOnSession(() -> {
            Session session = sessionRepository.createOrUpdateSession(copyOf(getCurrentSession()).deImpersonate().build());
            userStore.setUser(session.getOperationUser());
            eventBus.post(CurrentSessionSetOrUpdateEvent.INSTANCE);
        });
    }

    @Override
    @Nullable
    public String getCurrentSessionIdOrNull() {
        return sessionHolder.getOrNull();
    }

    @Override
    public void setCurrent(@Nullable String sessionId) {
        sessionHolder.set(sessionId);
        if (isBlank(sessionId)) {
            logger.trace("remove session from current request context");
            userStore.setUser(anonymousOperationUser());
            SecurityContextHolder.getContext().setAuthentication(null);
        } else {
            userStore.setUser(getUserOrAnonymousWhenMissing(sessionId));
            Authentication auth = new AuthenticationToken(sessionId);
            logger.trace("set spring security auth = {}", auth);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        eventBus.post(CurrentSessionSetOrUpdateEvent.INSTANCE);
    }

    @Override
    public Session getCurrentSession() {
        return getSessionById(checkNotBlank(getCurrentSessionIdOrNull(), "current session not set"));
    }

    @Override
    @Nullable
    public Session getCurrentSessionOrNull() {
        String sessionId = getCurrentSessionIdOrNull();
        return sessionId == null ? null : getSessionByIdOrNull(sessionId);
    }

    @Override
    public void updateCurrentSession(Function<Session, Session> fun) {
        if (!hasSession()) {
            logger.warn("cannot update current session: current session is not available");
        } else {
            doWithLockOnSession(() -> {
                Session session = getCurrentSession();
                session = fun.apply(session);
                updateSession(session);
            });
        }
    }

    @Override
    public boolean sessionExistsAndHasDefaultGroup(String sessionId) {
        return (sessionId == null) ? false : getUserOrAnonymousWhenMissing(sessionId).hasDefaultGroup();
    }

    @Override
    public OperationUser getUser(String sessionId) {
        return getSessionById(sessionId).getOperationUser();
    }

    @Override
    public void setUser(String sessionId, OperationUser user) {
        sessionRepository.createOrUpdateSession(copyOf(getSessionById(sessionId)).impersonate(user).build());
    }

    @Override
    public SessionData getCurrentSessionDataSafe() {
        return sessionDataService.getCurrentSessionDataSafe();
    }

    @Override
    public TransientSessionHelper createAndSetTransient(String username) {
        String sessionId = getCurrentSessionIdOrNull();
        logger.debug("create transient session for username =< {} >", username);
        createAndSet(buildTransientSessionLoginData(username));
        return () -> {
            logger.debug("deimpersonate transient session, restore prev session =< {} >", sessionId);
            setCurrent(sessionId);
        };
    }

    private void doImpersonate(OperationUser imp) {
        Session session = getCurrentSession();
        session = sessionRepository.createOrUpdateSession(copyOf(session).impersonate(imp).build());
        userStore.setUser(session.getOperationUser());
        eventBus.post(CurrentSessionSetOrUpdateEvent.INSTANCE);
    }

    private void updateSession(Session session) {
        logger.debug("update session = {}", session);
        sessionRepository.createOrUpdateSession(SessionImpl.copyOf(session).withLastActiveDate(now()).build());
        if (equal(session.getSessionId(), getCurrentSessionIdOrNull())) {
            userStore.setUser(session.getOperationUser());
            eventBus.post(CurrentSessionSetOrUpdateEvent.INSTANCE);
        }
    }

    private Session updateSession(String sessionId, OperationUser user) {
//        Map<String, String> userConfig = userConfigService.getByUsername(user.getUsername());//TODO check this
//        return sessionRepository.updateSession(builder().withSessionId(sessionId).withOperationUser(user).withSessionData((Map) userConfig).build()); 
        return isTransientSessionToken(sessionId) ? buildTransientSessionFromToken(sessionId) : sessionRepository.createOrUpdateSession(SessionImpl.builder().withSessionId(sessionId).withOperationUser(user).withExpirationStrategy(ES_DEFAULT).build());
    }

    private void doWithLockOnSession(Runnable task) {
        lockService.doWithRequestLock(format("session.%s", checkNotBlank(sessionHolder.getCurrentSessionIdNotNull())), task);
    }

    private String createSessionToken() {
        return randomId(SESSION_TOKEN_SIZE);
    }

    private Session buildTransientSessionFromToken(String sessionId) {//TODO add security, transient session expiration!
        return SessionImpl.builder().withSessionId(sessionId).withOperationUser(authenticationService.validateCredentialsAndCreateOperationUser(buildTransientSessionLoginData(getUsernameFromTransientSessionToken(sessionId)))).withExpirationStrategy(ES_DEFAULT).build();
    }

    private LoginData buildTransientSessionLoginData(String username) {
        return LoginDataImpl.builder().withPasswordRequired(false).withLoginString(username).allowServiceUser().withNoPersist(true).build();
    }

    public static String createTransientSessionToken(String username) {
        String token = xencodeString(checkNotBlank(username));
        checkArgument(token.length() + 6 <= SESSION_TOKEN_SIZE, "unable to build transient session token, username too long");
        token = "tr1_" + token + "_" + randomId(SESSION_TOKEN_SIZE - token.length() - 5);
        checkArgument(token.length() == SESSION_TOKEN_SIZE);
        return token;
    }

    public static boolean isTransientSessionToken(String token) {
        if (isBlank(token) || token.length() != SESSION_TOKEN_SIZE && !token.matches("tr1_[^_]+_[^_]+")) {
            return false;
        } else {
            try {
                getUsernameFromTransientSessionToken(token);
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }

    public static String getUsernameFromTransientSessionToken(String token) {
        Matcher matcher = Pattern.compile("tr1_([^_]+)_[^_]+").matcher(checkNotBlank(token));
        checkArgument(matcher.matches());
        return checkNotBlank(xdecodeString(matcher.group(1)));
    }

    private OperationUser getUserOrAnonymousWhenMissing(String sessionId) {
        Session session = getSessionByIdOrNull(sessionId);
        return session == null ? anonymousOperationUser() : session.getOperationUser();
    }

}
