package org.cmdbuild.service.rest.v3.helpers;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.auth.session.SessionService;
import static org.cmdbuild.service.rest.v3.helpers.SessionWsCommons.CURRENT;

public abstract class SessionWsCommons {

    public static final String CURRENT = "current";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final SessionService sessionService;

    protected SessionWsCommons(SessionService sessionService) {
        this.sessionService = checkNotNull(sessionService);
    }

    @Nullable
    protected String sessionIdOrCurrent(String sessionId) {
        if (CURRENT.equalsIgnoreCase(sessionId)) {
            return sessionService.getCurrentSessionIdOrNull();
        } else {
            return sessionId;
        }
    }

    protected void checkIsCurrent(String sessionId) {
        checkArgument(equal(sessionId, CURRENT) || equal(sessionId, sessionService.getCurrentSessionIdOrNull()), "session id param must be equal to current session id");
    }
}
