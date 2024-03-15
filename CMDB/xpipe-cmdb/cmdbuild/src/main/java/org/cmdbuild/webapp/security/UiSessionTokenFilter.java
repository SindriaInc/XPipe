/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.security;

import org.springframework.stereotype.Component;

@Component
public class UiSessionTokenFilter extends SessionTokenFilter {

    @Override
    protected boolean allowSessionsWithoutGroup() {
        return true;
    }

    @Override
    protected boolean enableRedirectToLoginForIncompleteSession() {
        return true;
    }

}
