package org.cmdbuild.workflow.inner;

import java.util.List;
import org.cmdbuild.dao.beans.ForwardingCard;

import org.cmdbuild.workflow.model.FlowStatus;
import org.cmdbuild.workflow.model.PlanInfo;
import org.cmdbuild.workflow.model.Task;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Process;

public abstract class ForwardingFlow extends ForwardingCard implements Flow {

	/**
	 * Usable by subclasses only.
	 */
	protected ForwardingFlow() {
	}

	@Override
	protected abstract Flow delegate();

//	@Override
//	public List<Task> getTaskList() {
//		return delegate().getTaskList();
//	}
//
//	@Override
//	public Task getTaskOrNull(String activityInstanceId) {
//		return delegate().getTaskOrNull(activityInstanceId);
//	}

	@Override
	public Process getType() {
		return delegate().getType();
	}

	@Override
	public Long getCardId() {
		return delegate().getCardId();
	}

	@Override
	public String getFlowId() {
		return delegate().getFlowId();
	}

	@Override
	public FlowStatus getStatus() {
		return delegate().getStatus();
	}

	@Override
	public PlanInfo getPlanInfoOrNull() {
		return delegate().getPlanInfoOrNull();
	}
}
