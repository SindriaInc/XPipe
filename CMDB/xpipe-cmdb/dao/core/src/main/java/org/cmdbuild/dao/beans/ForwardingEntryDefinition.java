package org.cmdbuild.dao.beans;


import com.google.common.collect.ForwardingObject;
import org.cmdbuild.dao.beans.DatabaseEntryDefinition;

public abstract class ForwardingEntryDefinition extends ForwardingObject implements DatabaseEntryDefinition {

	/**
	 * Usable by subclasses only.
	 */
	protected ForwardingEntryDefinition() {
	}

	@Override
	protected abstract DatabaseEntryDefinition delegate();

	@Override
	public DatabaseEntryDefinition set(final String key, final Object value) {
		delegate().set(key, value);
		return this;
	}

	@Override
	public DatabaseEntryDefinition setUser(final String user) {
		delegate().setUser(user);
		return this;
	}

	@Override
	public DatabaseRecord save() {
		return delegate().save();
	}

}
