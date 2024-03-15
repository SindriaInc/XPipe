package org.cmdbuild.email.utils;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class MyAuthenticator extends Authenticator {

    private final PasswordAuthentication authentication;

    public MyAuthenticator(String username, String password) {
        authentication = new PasswordAuthentication(username, password);
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return authentication;
    }

}
