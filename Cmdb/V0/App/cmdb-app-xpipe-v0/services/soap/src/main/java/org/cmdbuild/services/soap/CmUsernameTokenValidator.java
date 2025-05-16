/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.services.soap;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.handler.RequestData;
import org.apache.wss4j.dom.message.token.UsernameToken;
import org.apache.wss4j.dom.validate.UsernameTokenValidator;
import org.cmdbuild.auth.utils.CmPasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("soap_usernameTokenValidator")
public class CmUsernameTokenValidator extends UsernameTokenValidator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void verifyPlaintextPassword(UsernameToken usernameToken, RequestData data) throws WSSecurityException {

        String user = usernameToken.getName();
        String pwType = usernameToken.getPasswordType();
        
        if (data.getCallbackHandler() == null) {
            throw new WSSecurityException(WSSecurityException.ErrorCode.FAILURE, "noCallback");
        }
        WSPasswordCallback pwCb = new WSPasswordCallback(user, null, pwType, WSPasswordCallback.USERNAME_TOKEN);
        try {
            data.getCallbackHandler().handle(new Callback[]{pwCb});
        } catch (IOException | UnsupportedCallbackException e) {
            logger.debug("psw callback error", e);
            throw new WSSecurityException(WSSecurityException.ErrorCode.FAILED_AUTHENTICATION, e);
        }
        String passwordDigestFromDb = pwCb.getPassword();
        if (passwordDigestFromDb == null) {
            logger.debug("Callback supplied no password for: {}", user);
            throw new WSSecurityException(WSSecurityException.ErrorCode.FAILED_AUTHENTICATION);
        }

        if (!CmPasswordUtils.verifyPassword(usernameToken.getPassword(), passwordDigestFromDb)) {
            throw new WSSecurityException(WSSecurityException.ErrorCode.FAILED_AUTHENTICATION);
        }

    }

}
