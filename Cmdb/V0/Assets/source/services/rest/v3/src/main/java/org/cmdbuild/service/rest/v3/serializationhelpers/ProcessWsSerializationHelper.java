/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.serializationhelpers;

import com.fasterxml.jackson.databind.JsonNode;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Maps.transformValues;
import static com.google.common.collect.Maps.uniqueIndex;
import java.util.Collection;
import static java.util.Collections.singletonList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.formstructure.FormStructure;
import org.cmdbuild.formstructure.FormStructureService;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.ProcessSerializer;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.ltEqZeroToNull;
import org.cmdbuild.widget.WidgetService;
import org.cmdbuild.workflow.WorkflowConfiguration;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.dao.ExtendedRiverPlanRepository;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.model.Task;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.workflow.type.LookupType;
import org.cmdbuild.workflow.type.ReferenceType;
import static org.cmdbuild.workflow.utils.ClosedFlowUtils.buildTaskForClosedFlowAddWidgets;
import org.springframework.stereotype.Component;

/**
 * As in {@link ProcessSerializer} plus:
 * <ol>
 * <li>task list (tasks are the step of {@link Process} and depends on <i>XPDL
 * definition</i>;
 * <li>related card widget.
 * </ol>
 *
 * @author afelice
 */
@Component
public class ProcessWsSerializationHelper extends ProcessSerializer {

    private final AttributeTypeConversionService attributeDetailService;
    private final WidgetService widgetService;
    private final ExtendedRiverPlanRepository planRepository;
    private final WorkflowService workflowService;
    private final FormStructureService formStructureService;
    private final RoleRepository roleRepository;

    public ProcessWsSerializationHelper(CardWsSerializationHelperv3 cardSerializationHelper, AttributeTypeConversionService attributeDetailService, WidgetService widgetService, ExtendedRiverPlanRepository planRepository, WorkflowService workflowService, FormStructureService formStructureService, UserClassService classeService, ClassSerializationHelper classSerializationHelper, ObjectTranslationService translationService, RoleRepository roleRepository, WorkflowConfiguration workflowConfiguration) {
        super(checkNotNull(cardSerializationHelper), checkNotNull(classeService), checkNotNull(classSerializationHelper), checkNotNull(translationService), checkNotNull(workflowConfiguration));
        this.attributeDetailService = checkNotNull(attributeDetailService);
        this.widgetService = checkNotNull(widgetService);
        this.planRepository = checkNotNull(planRepository);
        this.workflowService = checkNotNull(workflowService);
        this.formStructureService = checkNotNull(formStructureService);
        this.roleRepository = checkNotNull(roleRepository);
    }

    @Override
    public FluentMap<String, Object> serializeFlow(Flow card) {
        return serializeFlow(card, false, false, false, DaoQueryOptionsImpl.emptyOptions());
    }

    public FluentMap<String, Object> serializeFlowIncludeTasklist(Flow card) {
        return serializeFlow(card, true, false, false, DaoQueryOptionsImpl.emptyOptions());
    }

    public FluentMap<String, Object> serializeFlow(Flow card, boolean includeTasklist, boolean taskListFull, boolean includeModel, DaoQueryOptions queryOptions) {
        FluentMap<String, Object> serialization = super.serializeFlow(card, includeModel, queryOptions);

        return serialization.accept(m -> {
            Map<String, Object> widgetData = card.getWidgetData();
            if (includeTasklist) {
                Collection<Task> taskList = taskListFull ? workflowService.getTaskList(card) : workflowService.getTaskListLean(card);
                if (taskList.isEmpty() && card.isCompleted()) {
                    taskList = singletonList(buildTaskForClosedFlowAddWidgets(card, widgetService));
                }
                m.put("_tasklist", taskList.stream().map(t -> serializeTask(card, t, false)).collect(toList()));
                if (widgetData.isEmpty()) {
                    widgetData = workflowService.getWidgetData(taskList, card);
                }
            }
            widgetData.forEach((k, v) -> {
                m.put(k, serializeWidgetValue(v));
            });
        });
    }

    @Nullable
    private Object serializeWidgetValue(@Nullable Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof ReferenceType[] referenceTypes) {
            return list(referenceTypes).map(r -> map("_id", r.getId()));
        } else if (value instanceof LookupType[] lookupTypes) {
            return list(lookupTypes).map(r -> map("_id", r.getId()));
        } else if (value instanceof ReferenceType referenceType) {
            return ltEqZeroToNull(referenceType.getId());
        } else if (value instanceof LookupType lookupType) {
            return ltEqZeroToNull(lookupType.getId());
        } else {
            return value;
        }
    }

    public FluentMap<String, Object> serializeDetailedHistory(Flow flow) {//TODO improve this, remove duplicate code
        return serializeFlow(flow).with(serializeBasicHistory(flow));
    }

    public Object serializeBasicTask(Task task) {
        return serializeTask(task.getProcessInstance(), task, false);
    }

    public Object serializeDetailedTask(Task task) {
        return serializeTask(task.getProcessInstance(), task, true);
    }

    public FluentMap<String, ?> serializeDetailedTaskDefinition(Process process, TaskDefinition taskDefinition) {
        return serializeBasicTaskDefinition(process, taskDefinition)
                .with("widgets", taskDefinition.getWidgets().stream()
                        .map(w -> widgetService.widgetDataToWidget(process.getName(), taskDefinition.getId(), w, planRepository.getPlanByClasseId(process.getName()).getDefaultValues()))//TODO move this somewhere else, not in ws layer
                        .map(w -> classSerializationHelper.serializeWorkflowWidget(process, taskDefinition, w))
                        .collect(toList())).with("_id", taskDefinition.getId(), "writable", true);
    }

    public FluentMap<String, ?> serializeEssentialTaskDefinition(Process process, TaskDefinition definition) {
        return map(
                "_definition", definition.getId(),
                "description", definition.getDescription(),
                "_description_translation", translationService.translateTaskDescription(process.getName(), definition.getId(), definition.getDescription()));
    }

    private Object serializeTask(Flow card, Task task, boolean detailed) {
        FluentMap<String, ?> res;
        if (detailed) {
            res = serializeBasicTaskDefinition(card.getType(), task.getDefinition()).with("widgets", list(transform(task.getWidgets(), w -> classSerializationHelper.serializeWorkflowWidget(card.getType(), task.getDefinition(), w))));
        } else {
            res = serializeEssentialTaskDefinition(card.getType(), task.getDefinition());
        }
        String performerDescription = Optional.ofNullable(roleRepository.getGroupWithNameOrNull(task.getPerformerName())).map(Role::getDescription).orElse(task.getPerformerName());
        return res.with(
                "_id", task.getId(),
                "writable", task.isWritable(),
                "performer", task.getPerformerName(),
                "_performer_description", performerDescription,
                "_performer_description_translation", translationService.translateRoleDescription(task.getPerformerName(), performerDescription),
                "description_addition", serializeTaskDescriptionValue(task.getDescriptionValue()),
                "_activity_subset_id", task.getActivitySubsetId()
        );
    }

    private FluentMap<String, ?> serializeBasicTaskDefinition(Process process, TaskDefinition taskDefinition) {
        Map<String, Object> attributesByName = transformValues(uniqueIndex(process.getCoreAttributes(), Attribute::getName), attributeDetailService::serializeAttributeType);
        return serializeEssentialTaskDefinition(process, taskDefinition).with(
                "instructions", taskDefinition.getInstructions(),
                "_instructions_translation", translationService.translateTaskInstructions(process.getName(), taskDefinition.getId(), taskDefinition.getInstructions()),
                "attributes", serializeTaskAttributes(attributesByName, taskDefinition)).accept(m -> {
            FormStructure form = formStructureService.getFormForTaskOrNull(process, taskDefinition.getId());
            if (form != null) {
                m.put("formStructure", fromJson(form.getData(), JsonNode.class));
            }
        });
    }

    private Object serializeTaskAttributes(Map<String, Object> attributesByName, TaskDefinition definition) {
        AtomicInteger index = new AtomicInteger(0);
        return definition.getVariables().stream().map((attr) -> {
            return map(
                    "_id", attr.getName(),
                    "mandatory", attr.isMandatory(),
                    "writable", attr.isWritable(),
                    "action", attr.isAction(),
                    "index", index.getAndIncrement()
            )
                    .skipNullValues()
                    .with("detail", attributesByName.get(attr.getName()));
        }).collect(toList());
    }

}
