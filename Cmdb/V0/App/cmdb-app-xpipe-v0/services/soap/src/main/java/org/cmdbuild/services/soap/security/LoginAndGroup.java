package org.cmdbuild.services.soap.security;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.cmdbuild.auth.login.LoginUserIdentity;

public class LoginAndGroup {

	/**
	 * @deprecated Use {@code login(String)} instead.
	 */
	@Deprecated
	public static LoginAndGroup newInstance(final LoginUserIdentity login) {
		return loginAndGroup(login);
	}

	/**
	 * @deprecated Use {@code login(String)} instead.
	 */
	@Deprecated
	public static LoginAndGroup newInstance(final LoginUserIdentity login, final String group) {
		return loginAndGroup(login, group);
	}

	public static LoginAndGroup loginAndGroup(final LoginUserIdentity login) {
		return new LoginAndGroup(login, null);
	}

	public static LoginAndGroup loginAndGroup(final LoginUserIdentity login, final String group) {
		return new LoginAndGroup(login, group);
	}

	private final LoginUserIdentity login;
	private final String group;

	private LoginAndGroup(final LoginUserIdentity login, final String group) {
		this.login = login;
		this.group = group;
	}

	public LoginUserIdentity getLogin() {
		return login;
	}

	public String getGroup() {
		return group;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof LoginAndGroup)) {
			return false;
		}
		final LoginAndGroup other = LoginAndGroup.class.cast(obj);
		return new EqualsBuilder() //
				.append(this.login, other.login) //
				.append(this.group, other.group) //
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder() //
				.append(login) //
				.append(group) //
				.toHashCode();
	}

	@Override
	public String toString() {
		return reflectionToString(this, SHORT_PREFIX_STYLE);
	}

}
