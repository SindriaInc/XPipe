package org.cmdbuild.dao.beans;

import org.cmdbuild.dao.entrytype.Classe;

public abstract class ForwardingCard extends ForwardingEntry implements Card {

	/**
	 * Usable by subclasses only.
	 */
	protected ForwardingCard() {
	}

	@Override
	protected abstract Card delegate();

	@Override
	public Classe getType() {
		return delegate().getType();
	}

	@Override
	public String getCode() {
		return delegate().getCode();
	}

	@Override
	public String getDescription() {
		return delegate().getDescription();
	}

	@Override
	public Long getCurrentId() {
		return delegate().getCurrentId();
	}

}
