package org.cmdbuild.workflow.inner;

import org.cmdbuild.workflow.model.FlowData;
import com.google.common.base.Supplier;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Classe;

import org.cmdbuild.workflow.model.FlowInfo;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Process;

public interface FlowCardRepository {

    Flow create(Flow flow);

    Flow update(Flow flow);

    @Deprecated
    Flow createFlowCard(Classe process, FlowData flow, Supplier<Map<String, Object>> processInstanceVariablesSupplier);

    @Deprecated
    Flow createFlowCard(Process processClass, FlowInfo processInstInfo, FlowData processData, Supplier<Map<String, Object>> processInstanceVariablesSupplier);

    @Deprecated
    Flow updateFlowCard(Flow processInstance, FlowData processData, Supplier<Map<String, Object>> processInstanceVariablesSupplier);

    @Deprecated
    Flow updateThisFlowCard(Flow card, FlowData flowData);

    Flow getFlowCardByPlanIdAndFlowId(String planId, String flowId);

    Flow getFlowCard(Flow processInstance);

    Flow getFlowCardByPlanAndCardId(Process processClass, Long cardId);

    Flow getFlowCardByClasseIdAndCardId(String classeName, Long cardId);

    Iterable<? extends Flow> queryOpenAndSuspended(Process processClass);

    PagedElements<Flow> getUserCardsByClassIdAndQueryOptions(String classId, DaoQueryOptions queryOptions);

//    @Deprecated //TODO drop this method once 30 is ready
//    PagedElements<UserFlowWithPosition> queryWithPosition(String className, DaoQueryOptions queryOptions, Iterable<Long> cardId);
    List<Flow> getCardsByFlowId(String classId, Collection<String> flowIds);

    Flow getUserFlowCard(String classId, long cardId);

    boolean userCanReadCard(String classId, long cardId);

}
