/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.auth.user;

import org.cmdbuild.auth.login.LoginModuleConfiguration;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
public interface UserScriptService {

    public LoginUserIdentity getLoginFromScript(LoginModuleConfiguration authConf, Logger customLogger, Object loginData);

}
