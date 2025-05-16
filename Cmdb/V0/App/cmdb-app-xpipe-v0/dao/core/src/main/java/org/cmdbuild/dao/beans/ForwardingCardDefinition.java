package org.cmdbuild.dao.beans;

import java.util.Map.Entry;

import org.cmdbuild.dao.beans.CardDefinition;
import org.cmdbuild.dao.beans.DatabaseEntryDefinition;

public abstract class ForwardingCardDefinition extends ForwardingEntryDefinition implements CardDefinition {

	final CardDefinition delegate;

	protected ForwardingCardDefinition(final CardDefinition delegate) {
		this.delegate = delegate;
	}

	@Override
	protected DatabaseEntryDefinition delegate() {
		return delegate;
	}

	@Override
	public CardDefinition set(final String key, final Object value) {
		delegate.set(key, value);
		return this;
	}

	@Override
	public CardDefinition set(final Iterable<? extends Entry<String, ? extends Object>> keysAndValues) {
		delegate.set(keysAndValues);
		return this;
	}

	@Override
	public CardDefinition setUser(final String user) {
		delegate.setUser(user);
		return this;
	}

	@Override
	public CardDefinition setCode(final Object value) {
		delegate.setCode(value);
		return this;
	}

	@Override
	public CardDefinition setDescription(final Object value) {
		delegate.setDescription(value);
		return this;
	}

	@Override
	public CardDefinition setCurrentId(final Long currentId) {
		delegate.setCurrentId(currentId);
		return this;
	}

	@Override
	public Card save() {
		return delegate.save();
	}

}
