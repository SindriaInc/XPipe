package org.cmdbuild.auth.login;

import javax.annotation.Nullable;

public interface ClientRequestAuthenticator {

    boolean isEnabled();

    @Nullable
    RequestAuthenticatorResponse<LoginUserIdentity> authenticate(AuthRequestInfo request);

    @Nullable
    default RequestAuthenticatorResponse<Void> logout(@Nullable Object request) {
        return null;
    }
}
