package org.cmdbuild.services.soap.test;

import static org.cmdbuild.services.soap.security.LoginAndGroup.loginAndGroup;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.login.LoginType;
import org.cmdbuild.services.soap.security.PasswordHandler.AuthenticationStringHelper;
import org.junit.Test;

import static org.cmdbuild.auth.login.LoginType.LT_AUTO;

public class AuthenticationStringTest {

    private static LoginUserIdentity login(final String value) {
        return LoginUserIdentity.builder() //
                .withValue(value) //
                .build();
    }

    private LoginUserIdentity login(final String value, final LoginType type) {
        return LoginUserIdentity.builder() //
                .withValue(value) //
                .withType(type) //
                .build();
    }

    @Test
    public void usernameWithNoImpersonate() {
        // when
        final AuthenticationStringHelper output = new AuthenticationStringHelper("foo");

        // then
        assertThat(output.getAuthenticationLogin().toString(), equalTo(loginAndGroup(login("foo")).toString()));
        assertThat(output.getImpersonationLogin(), equalTo(null));
        assertThat(output.shouldImpersonate(), equalTo(false));
        assertThat(output.impersonateForcibly(), equalTo(false));
    }

    @Test
    public void usernameAndGroupWithNoImpersonate() {
        // when
        final AuthenticationStringHelper output = new AuthenticationStringHelper("foo@bar");

        // then
        assertThat(output.getAuthenticationLogin().toString(), equalTo(loginAndGroup(login("foo"), "bar").toString()));
        assertThat(output.getImpersonationLogin(), equalTo(null));
        assertThat(output.shouldImpersonate(), equalTo(false));
        assertThat(output.impersonateForcibly(), equalTo(false));
    }

    @Test
    public void usernameWithUsernameImpersonate() {
        // when
        final AuthenticationStringHelper output = new AuthenticationStringHelper("foo#bar");

        // then
        assertThat(output.getAuthenticationLogin().toString(), equalTo(loginAndGroup(login("foo")).toString()));
        assertThat(output.getImpersonationLogin().toString(), equalTo(loginAndGroup(login("bar")).toString()));
        assertThat(output.shouldImpersonate(), equalTo(true));
        assertThat(output.impersonateForcibly(), equalTo(false));
    }

    @Test
    public void usernameWithUsernameImpersonateAndGroup() {
        // when
        final AuthenticationStringHelper output = new AuthenticationStringHelper("foo#bar@baz");

        // then
        assertThat(output.getAuthenticationLogin().toString(), equalTo(loginAndGroup(login("foo"), "baz").toString()));
        assertThat(output.getImpersonationLogin().toString(), equalTo(loginAndGroup(login("bar"), "baz").toString()));
        assertThat(output.shouldImpersonate(), equalTo(true));
        assertThat(output.impersonateForcibly(), equalTo(false));
    }

    @Test
    public void usernameWithEmailImpersonate() {
        // when
        final AuthenticationStringHelper output = new AuthenticationStringHelper("foo#bar@example.com");

        // then
        assertThat(output.getAuthenticationLogin().toString(), equalTo(loginAndGroup(login("foo")).toString()));
        assertThat(output.getImpersonationLogin().toString(), equalTo(loginAndGroup(login("bar@example.com", LT_AUTO)).toString()));
        assertThat(output.shouldImpersonate(), equalTo(true));
        assertThat(output.impersonateForcibly(), equalTo(false));
    }

    @Test
    public void usernameWithEmailImpersonateAndGroup() {
        // when
        final AuthenticationStringHelper output = new AuthenticationStringHelper("foo#bar@example.com@baz");

        // then
        assertThat(output.getAuthenticationLogin().toString(), equalTo(loginAndGroup(login("foo"), "baz").toString()));
        assertThat(output.getImpersonationLogin().toString(), equalTo(loginAndGroup(login("bar@example.com", LT_AUTO), "baz").toString()));
        assertThat(output.shouldImpersonate(), equalTo(true));
        assertThat(output.impersonateForcibly(), equalTo(false));
    }

    @Test
    public void usernameWithUsernameImpersonateForcibly() {
        // when
        final AuthenticationStringHelper output = new AuthenticationStringHelper("foo!bar");

        // then
        assertThat(output.getAuthenticationLogin().toString(), equalTo(loginAndGroup(login("foo")).toString()));
        assertThat(output.getImpersonationLogin().toString(), equalTo(loginAndGroup(login("bar")).toString()));
        assertThat(output.shouldImpersonate(), equalTo(true));
        assertThat(output.impersonateForcibly(), equalTo(true));
    }

    @Test
    public void usernameWithUsernameImpersonateAndGroupForcibly() {
        // when
        final AuthenticationStringHelper output = new AuthenticationStringHelper("foo!bar@baz");

        // then
        assertThat(output.getAuthenticationLogin().toString(), equalTo(loginAndGroup(login("foo"), "baz").toString()));
        assertThat(output.getImpersonationLogin().toString(), equalTo(loginAndGroup(login("bar"), "baz").toString()));
        assertThat(output.shouldImpersonate(), equalTo(true));
        assertThat(output.impersonateForcibly(), equalTo(true));
    }

    @Test
    public void usernameWithEmailImpersonateForcibly() {
        // when
        final AuthenticationStringHelper output = new AuthenticationStringHelper("foo!bar@example.com");

        // then
        assertThat(output.getAuthenticationLogin().toString(), equalTo(loginAndGroup(login("foo")).toString()));
        assertThat(output.getImpersonationLogin().toString(), equalTo(loginAndGroup(login("bar@example.com", LT_AUTO)).toString()));
        assertThat(output.shouldImpersonate(), equalTo(true));
        assertThat(output.impersonateForcibly(), equalTo(true));
    }

    @Test
    public void usernameWithEmailImpersonateAndGroupForcibly() {
        // when
        final AuthenticationStringHelper output = new AuthenticationStringHelper("foo!bar@example.com@baz");

        // then
        assertThat(output.getAuthenticationLogin().toString(), equalTo(loginAndGroup(login("foo"), "baz").toString()));
        assertThat(output.getImpersonationLogin().toString(), equalTo(loginAndGroup(login("bar@example.com", LT_AUTO), "baz").toString()));
        assertThat(output.shouldImpersonate(), equalTo(true));
        assertThat(output.impersonateForcibly(), equalTo(true));
    }

}
