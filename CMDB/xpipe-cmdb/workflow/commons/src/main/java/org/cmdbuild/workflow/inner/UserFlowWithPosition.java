package org.cmdbuild.workflow.inner;

import org.cmdbuild.workflow.model.Flow;

public class UserFlowWithPosition extends ForwardingFlow {

	private final Flow delegate;
	private final Long position;

	public UserFlowWithPosition(final Flow delegate, final Long position) {
		this.delegate = delegate;
		this.position = position;
	}

	@Override
	protected Flow delegate() {
		return delegate;
	}

	public Long getPosition() {
		return position;
	}

}
