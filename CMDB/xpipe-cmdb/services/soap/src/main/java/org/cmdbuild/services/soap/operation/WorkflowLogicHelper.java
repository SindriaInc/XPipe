package org.cmdbuild.services.soap.operation;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cmdbuild.services.soap.structure.ActivitySchema;
import org.cmdbuild.services.soap.structure.AttributeSchema;
import org.cmdbuild.services.soap.structure.WorkflowWidgetDefinition;
import org.cmdbuild.services.soap.structure.WorkflowWidgetSubmission;
import org.cmdbuild.services.soap.types.Attribute;
import org.cmdbuild.services.soap.types.Card;
import org.cmdbuild.services.soap.types.Workflow;
import org.cmdbuild.workflow.model.WorkflowException;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.workflow.model.Task;
import static org.cmdbuild.workflow.utils.WorkflowUtils.getEntryTaskForCurrentUser;
import org.springframework.stereotype.Component;
import org.cmdbuild.widget.model.WidgetData; 
import org.cmdbuild.widget.model.Widget;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.model.TaskAttribute;

@Component
public class WorkflowLogicHelper {

	private static final WorkflowWidgetSubmission[] EMPTY_WORKFLOW_WIDGETS_SUBMISSION = new WorkflowWidgetSubmission[]{};

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final WorkflowService workflowService;
	private final SerializationStuff serializationUtils;
	private final CardAdapter cardAdapter;
	private final OperationUserSupplier userSupplier;

	public WorkflowLogicHelper(OperationUserSupplier userSupplier, final WorkflowService workflowLogic, final DaoService view, final CardAdapter cardAdapter) {
		this.workflowService = workflowLogic;
		this.serializationUtils = new SerializationStuff(view);
		this.cardAdapter = cardAdapter;
		this.userSupplier = userSupplier;
	}

	public String getInstructions(String className, Long cardId) {
		try {
			return safeInstructionsFor(activityFor(className, cardId));
		} catch (final WorkflowException e) {
			final String message = format("cannot get instructions for className '{}' and cardId '{}'", className,
					cardId);
			logger.warn(message);
			return EMPTY;
		}
	}

	private String safeInstructionsFor(final TaskDefinition activity) {
		return (activity == null) ? EMPTY : activity.getInstructions();
	}

	public ActivitySchema getActivitySchema(final String className, Long cardId) {
		try {
			final ActivitySchema activitySchema = new ActivitySchema();
			activitySchema.setAttributes(activityAttributesFor(className, cardId));
			activitySchema.setWidgets(workflowWidgetDefinitionsFor(className, cardId));
			return activitySchema;
		} catch (final WorkflowException e) {
			final String message = format("cannot get activity schema for className '%s' and cardId '%d'", className,
					cardId);
			logger.error(message, e);
			forwardException(message, e);
			return null; // unreachable code
		}
	}

	public List<AttributeSchema> getAttributeSchemaList(final String className, final Long cardId) {
		try {
			return activityAttributesFor(className, cardId);
		} catch (final WorkflowException e) {
			final String message = format("cannot get attribute schema object for className '%s' and cardId '%d'",
					className, cardId);
			logger.error(message, e);
			forwardException(message, e);
			return null; // unreachable code
		}
	}

	private TaskDefinition startActivityFor(final String className) throws WorkflowException {
		return activityFor(className, null);
	}

	private TaskDefinition activityFor(final String className, final Long cardId) throws WorkflowException {
		final TaskDefinition activity;
		if (isStartActivity(cardId)) {
			activity = getEntryTaskForCurrentUser(workflowService.getProcess(className), userSupplier.getUser());
		} else {
			final Flow processInstance = workflowService.getFlowCard(className, cardId.longValue());
			activity = selectActivityFor(processInstance);
		}
		return activity;
	}

	private TaskDefinition selectActivityFor(final Flow processInstance) throws WorkflowException {
		return selectActivityInstanceFor(processInstance).getDefinition();
	}

	public Task selectActivityInstanceFor(final Flow processInstance) {
		Task selectedActivityInstance = null;
		for (final Task activityInstance : workflowService.getTaskList(processInstance)) {
			if (selectedActivityInstance == null) {
				selectedActivityInstance = activityInstance;
			} else if (isLower(selectedActivityInstance, activityInstance)) {
				selectedActivityInstance = activityInstance;
			}
		}
		return selectedActivityInstance;
	}

	private boolean isLower(final Task activityInstance1, final Task activityInstance2) {
		return activityInstance1.getId().compareTo(activityInstance2.getId()) < 0;
	}

	private List<AttributeSchema> activityAttributesFor(final String className, final Long cardId)
			throws WorkflowException {
		final TaskDefinition activity = activityFor(className, cardId);
		final List<AttributeSchema> attributeSchemas = new ArrayList<AttributeSchema>();
		int index = 0;
		final Process userProcessClass = workflowService.getProcess(className);
		for (final TaskAttribute variable : activity.getVariables()) {
			final org.cmdbuild.dao.entrytype.Attribute attribute = userProcessClass.getAttributeOrNull(variable.getName());
			final AttributeSchema attributeSchema = serializationUtils.serialize(attribute, index++);
			attributeSchema.setVisibility(visibilityFor(variable));
			attributeSchemas.add(attributeSchema);
		}
		return attributeSchemas;
	}

	private String visibilityFor(final TaskAttribute variable) {
		final String output;
		if (variable.isWritable()) {
			if (variable.isMandatory()) {
				output = "REQUIRED";
			} else {
				output = "UPDATE";
			}
		} else {
			output = "VIEW";
		}
		return output;
	}

	private List<WorkflowWidgetDefinition> workflowWidgetDefinitionsFor(final String className, final Long cardId) throws WorkflowException {
//		final List<WorkflowWidgetDefinition> widgetList = new ArrayList<>();
//		final TaskDefinition taskDefinition = activityFor(className, cardId);
//		List<WidgetData> widgets = workflowService.getWidgetsForUserTask(className, (long) toIntExact( cardId, taskDefinition.getId());
//		for (final WidgetData widget : widgets) {
//			final WidgetData concreteWidget = WidgetData.class.cast(widget);
//			final SoapWidgetSerializer serializer = new SoapWidgetSerializer(concreteWidget);
//			final WorkflowWidgetDefinition wwd = serializer.serialize();
//			widgetList.add(wwd);
//		}
		return emptyList(); //TODO
	}

	public Workflow updateProcess(final Card card, final boolean advance) {
		return updateProcess(card, EMPTY_WORKFLOW_WIDGETS_SUBMISSION, advance);
	}

	public Workflow updateProcess(final Card card, final WorkflowWidgetSubmission[] widgets, final boolean advance) {
		try {
			cardAdapter.resolveAttributes(card);
			final Flow processInstance;
			if (isNewProcess(card)) {
				TaskDefinition taskDefinition = startActivityFor(card.getClassName());
				//List<WidgetData> activityWidgets = workflowService.getWidgetsForUserTask(card.getClassName(), (long) card.getId(), taskDefinition.getId());
				processInstance = workflowService.startProcess(
						card.getClassName(),
						variablesFor(card),
						//						widgetSubmission(activityWidgets, widgets),
						advance).getFlowCard();
			} else {
				processInstance = workflowService.getFlowCard(
						card.getClassName(),
						card.getId());
				final Task selectedActivity = selectActivityInstanceFor(processInstance);
				List<Widget> activityWidgets = selectedActivity.getWidgets();
				workflowService.updateProcess(
						card.getClassName(),
						card.getId(),
						selectedActivity.getId(),
						variablesFor(card),
						//						widgetSubmission(activityWidgets, widgets),
						advance).getFlowCard();
			}
			return workflowFor(processInstance);
		} catch (final WorkflowException e) {
			final String message = format("cannot update process for className '%s' and cardId '%d'", card.getClassName(), card.getId());
			logger.error(message, e);
			forwardException(message, e);
			throw new UnsupportedOperationException("this code should be unreacheable");
		}
	}

	private boolean isNewProcess(Card card) {
		return isStartActivity(card.getId());
	}

	private boolean isStartActivity(Long cardId) {
		return (cardId == null) ? true : (cardId <= 0);
	}

//	private long longIdFor( Card card) {
//		return new Integer(card.getId()).longValue();
//	}
	private Map<String, Object> variablesFor(final Card card) {
		final Map<String, Object> variables = new HashMap<String, Object>();
		for (final Attribute attribute : card.getAttributeList()) {
			variables.put(attribute.getName(), attribute.getValue());
		}
		return variables;
	}

	private Map<String, Object> widgetSubmission(final List<WidgetData> activityWidgets,
			final WorkflowWidgetSubmission[] widgets) {
//		final Map<String, Object> widgetSubmissions = new HashMap<String, Object>();
//		for (final WorkflowWidgetSubmission submission : safeWidgetListOf(widgets)) {
//			final String widgetId = submission.getIdentifier();
//			final Collection<WidgetData> filteredActivityWidgets = filter(activityWidgets,
//					widgetIdEqualsTo(widgetId));
//			if (!filteredActivityWidgets.isEmpty()) {
//				final WidgetData activityWidget = filteredActivityWidgets.iterator().next();
//				final WidgetData widget = WidgetData.class.cast(activityWidget);
//				final WidgetSubmissionConverter converter = new WidgetSubmissionConverter(widget);
//				final Object widgetSubmission = converter.convertFrom(submission);
//				if (widgetSubmission != null) {
//					widgetSubmissions.put(widgetId, widgetSubmission);
//				}
//			}
//		}
//		return widgetSubmissions;
		return emptyMap(); //TODO
	}

//	private Predicate<WidgetData> widgetIdEqualsTo(final String widgetId) {
//		return new Predicate<WidgetData>() {
//			@Override
//			public boolean apply(final WidgetData input) {
//				return input.getStringId().equals(widgetId);
//			}
//		};
//	}
	private List<WorkflowWidgetSubmission> safeWidgetListOf(final WorkflowWidgetSubmission[] widgets) {
		final List<WorkflowWidgetSubmission> list;
		if (widgets == null) {
			list = Collections.emptyList();
		} else {
			list = Arrays.asList(widgets);
		}
		return list;
	}

	private Workflow workflowFor(final Flow processInstance) {
		final Workflow workflow = new Workflow();
		workflow.setProcessid(processInstance.getCardId().intValue());
		workflow.setProcessinstanceid(processInstance.getFlowId());
		return workflow;
	}

	private void forwardException(final String message, final WorkflowException e) {
		throw new RuntimeException(message, e);
	}

	public void suspendProcess(final Card card) {
		try {
			workflowService.suspendProcess(card.getClassName(), card.getId());
		} catch (final WorkflowException e) {
			final String message = format("cannot suspend process for className '%s' and cardId '%d'",
					card.getClassName(), card.getId());
			logger.error(message, e);
			forwardException(message, e);
		}
	}

	public void resumeProcess(final Card card) {
		try {
			workflowService.resumeProcess(card.getClassName(), card.getId());
		} catch (final WorkflowException e) {
			final String message = format("cannot resume process for className '%s' and cardId '%d'",
					card.getClassName(), card.getId());
			logger.error(message, e);
			forwardException(message, e);
		}
	}

	public void abortProcess(final Card card) {
		try {
			workflowService.abortProcess(card.getClassName(), card.getId());
		} catch (final WorkflowException e) {
			final String message = format("cannot abort process for className '%s' and cardId '%d'",
					card.getClassName(), card.getId());
			logger.error(message, e);
			forwardException(message, e);
		}
	}

}
