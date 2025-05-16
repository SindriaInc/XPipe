package org.cmdbuild.workflow.shark;

import org.apache.commons.lang3.Validate;

@Deprecated
public class SharkEvent {

	public enum Type {
		START, UPDATE
	}

	private final Type type;
	private final String processDefinitionId;
	private final String processInstanceId;

	public SharkEvent(final Type type, final String processDefinitionId, final String processInstanceId) {
		Validate.notNull(type);
		Validate.notEmpty(processDefinitionId);
		Validate.notEmpty(processInstanceId);
		this.type = type;
		this.processDefinitionId = processDefinitionId;
		this.processInstanceId = processInstanceId;
	}

	public Type getType() {
		return type;
	}

	public String getPlanId() {
		return processDefinitionId;
	}

	public String getWalkId() {
		return processInstanceId;
	}

	public static SharkEvent newProcessStartEvent(final String processDefinitionId, final String processInstanceId) {
		return new SharkEvent(Type.START, processDefinitionId, processInstanceId);
	}

	public static SharkEvent newProcessUpdateEvent(final String processDefinitionId, final String processInstanceId) {
		return new SharkEvent(Type.UPDATE, processDefinitionId, processInstanceId);
	}

	@Override
	public String toString() {
		return "WorkflowEvent{" + "type=" + type + ", processDefinitionId=" + processDefinitionId + ", processInstanceId=" + processInstanceId + '}';
	}

}
