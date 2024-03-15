/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.migration;

import com.google.common.base.Joiner;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import com.google.common.base.Stopwatch;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import static java.lang.String.format;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import javax.inject.Provider;
import static org.apache.commons.dbcp2.Utils.closeQuietly;
import org.apache.commons.lang.StringUtils;
import static org.apache.commons.lang.StringUtils.isBlank;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.cmdbuild.common.beans.LookupValue;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import static org.cmdbuild.dao.beans.CardImpl.buildCard;
import org.cmdbuild.dao.beans.ClassMetadataImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_FLOW_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_NEXT_EXECUTOR;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_PLAN_INFO;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_TASK_DEFINITION_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_TASK_INSTANCE_ID;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.RiverVariableInfo;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlUtils.buildStepIdFromParentActivityIdAndActivityId;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.cmdbuild.workflow.inner.FlowMigrationService;
import static org.cmdbuild.workflow.WorkflowCommonConst.RIVER;
import org.cmdbuild.workflow.WorkflowTypeConverter;
import org.cmdbuild.workflow.model.WorkflowException;
import org.cmdbuild.workflow.dao.ExtendedRiverPlanRepository;
import static org.cmdbuild.workflow.utils.FlowDataSerializerUtils.serializeRiverFlowData;
import static org.cmdbuild.workflow.utils.WfRiverXpdlUtils.riverPlanIdToLegacyUniqueProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.workflow.WorkflowCommonConst.SHARK;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.migration.SharkDbUtils.SharkHelper;
import org.cmdbuild.workflow.migration.SharkDbUtils.XpdlData;
import org.cmdbuild.workflow.model.PlanInfo;
import org.cmdbuild.workflow.model.PlanInfoImpl;
import org.cmdbuild.workflow.river.engine.RiverFlowStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_FLOW_DATA;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_FLOW_STATUS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.core.q3.WhereOperator.IN;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.utils.date.CmDateUtils.toUserDuration;
import static org.cmdbuild.utils.io.CmStreamProgressUtils.buildProgressListener;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toIntegerOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;
import static org.cmdbuild.utils.lang.CmExecutorUtils.waitUntil;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import org.cmdbuild.workflow.inner.FlowMigrationConfig;
import org.cmdbuild.workflow.model.FlowStatus;
import org.cmdbuild.workflow.model.XpdlInfo;
import static org.cmdbuild.workflow.river.engine.RiverFlowStatus.ABORTED;
import static org.cmdbuild.workflow.river.engine.RiverFlowStatus.COMPLETE;
import static org.cmdbuild.workflow.river.engine.RiverFlowStatus.RUNNING;
import static org.cmdbuild.workflow.river.engine.RiverFlowStatus.SUSPENDED;
import static org.cmdbuild.workflow.utils.FlowStatusUtils.FLOW_STATUS_LOOKUP;
import static org.cmdbuild.workflow.utils.FlowStatusUtils.STATE_OPEN_NOT_RUNNING_SUSPENDED;
import static org.cmdbuild.workflow.utils.FlowStatusUtils.STATE_OPEN_RUNNING;
import static org.cmdbuild.workflow.utils.FlowStatusUtils.toFlowStatus;
import org.slf4j.MDC;
import org.springframework.jdbc.core.StatementCallback;

@Component
public class FlowMigrationServiceImpl implements FlowMigrationService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final GlobalConfigService configService;
    private final ExtendedRiverPlanRepository riverPlanRepository;
    private final DaoService dao;
    private final WorkflowTypeConverter typeConverter;
    private final LookupService lookupService;
    private final Provider<WorkflowService> workflowService;
    private final RequestContextService requestContextService;

    public FlowMigrationServiceImpl(RequestContextService requestContextService, LookupService lookupService, Provider<WorkflowService> workflowService, GlobalConfigService configService, ExtendedRiverPlanRepository riverPlanRepository, DaoService dao, WorkflowTypeConverter typeConverter) {
        this.configService = checkNotNull(configService);
        this.riverPlanRepository = checkNotNull(riverPlanRepository);
        this.dao = checkNotNull(dao);
        this.typeConverter = checkNotNull(typeConverter);
        this.workflowService = checkNotNull(workflowService);
        this.lookupService = checkNotNull(lookupService);
        this.requestContextService = checkNotNull(requestContextService);
    }

    @Override
    public void migrateFlowInstancesToNewProvider(String classId, FlowMigrationConfig config) {
        Classe classe = dao.getClasse(classId);
        if (equal(classe.getMetadata().getFlowProviderOrNull(), RIVER)) {
            logger.warn(marker(), "process already migrated to river, nothing to do");
        } else {
            checkArgument(equal(classe.getMetadata().getFlowProviderOrNull(), SHARK), "cannot migrate process = %s with provider =< %s >: unsupported source provider", classe, classe.getMetadata().getFlowProviderOrNull());
            new SharkToRiverMigrationProcess(classe, config).migrateFlowInstancesFromSharkToRiver();
        }
    }

    private class SharkToRiverMigrationProcess {

        private final JdbcTemplate jdbc = dao.getJdbcTemplate();
        private final Classe process;
        private final FlowMigrationConfig config;
        private RiverPlan defaultPlan;
        private final SharkHelper sharkHelper;
        private final boolean skipHistory, setNullOnConvertError;

        private final Set<Long> activeFlowStatusLookupCodes;
        private final Set<Card> flowsWithError = set();
        private final Map<String, RiverPlan> planMapping = map();
        private final long activeCardsCount, inactiveCardsCount, cardsCount;
        private final int workThreadCount;

        private final Stopwatch stopwatch;

        private final AtomicLong processedCards = new AtomicLong();

        private Connection connection;

        public SharkToRiverMigrationProcess(Classe process, FlowMigrationConfig config) {
            logger.info("preparing shark to river migration process");
            this.process = checkNotNull(process);
            this.config = checkNotNull(config);
            this.skipHistory = toBooleanOrDefault(configService.getString("org.cmdbuild.workflow.migration.skipHistoryRecord"), false);
            this.setNullOnConvertError = toBooleanOrDefault(configService.getString("org.cmdbuild.workflow.migration.setNullOnConvertError"), false);
            sharkHelper = SharkDbUtils.sharkHelper(configService::getString);

            stopwatch = Stopwatch.createStarted();

            activeFlowStatusLookupCodes = ImmutableSet.of(lookupService.getLookupByTypeAndCode(FLOW_STATUS_LOOKUP, STATE_OPEN_RUNNING).getId(), lookupService.getLookupByTypeAndCode(FLOW_STATUS_LOOKUP, STATE_OPEN_NOT_RUNNING_SUSPENDED).getId());

            activeCardsCount = dao.selectCount().from(process).where(ATTR_FLOW_STATUS, IN, activeFlowStatusLookupCodes).getCount();
            cardsCount = dao.selectCount().from(process).getCount();
            inactiveCardsCount = cardsCount - activeCardsCount;

            logger.info("preparing migration for {} cards ( {} active cards )", activeCardsCount + inactiveCardsCount, activeCardsCount);

            workThreadCount = toIntegerOrDefault(configService.getString("org.cmdbuild.workflow.migration.threadCount"), Runtime.getRuntime().availableProcessors() + 1);
        }

        public void migrateFlowInstancesFromSharkToRiver() {
            logger.info("running shark to river migration process, for process = {}", process);
            loadPlans();
            if (cardsCount > 0) {
                logger.info("execute migration of {} active instances", activeCardsCount);
                processedCards.set(0);
                Consumer<Long> progressListener = buildProgressListener(activeCardsCount, (e) -> logger.info("migration progress: {}", e.getProgressDescriptionDetailed()));
                ExecutorService processorThreads = Executors.newFixedThreadPool(workThreadCount);
                ArrayBlockingQueue<Card> queue = new ArrayBlockingQueue(1000);//buffer size
                for (int i = 0; i < workThreadCount; i++) {
                    int threadNo = i;
                    String mdcCmId = MDC.get("cm_id");//TODO improve mdc copy
                    Runnable job = () -> {
                        MDC.put("cm_type", "req");
                        MDC.put("cm_id", format("%s:%s", mdcCmId, threadNo));//TODO improve mdc copy
                        requestContextService.initCurrentRequestContext("migrate progess thread " + threadNo);
                        Card card;
                        while (!Thread.currentThread().isInterrupted()) {
                            try {
                                card = queue.take();
                                prepareFlowInstance(card);
                                progressListener.accept(processedCards.get());
                            } catch (InterruptedException ex) {
                                logger.debug("worker thread {} interrupted", threadNo);
                                return;
                            } catch (Exception ex) {
                                logger.error("error", ex);
                                //TODO break everything
                                return;
                            }
                        }
                    };
                    processorThreads.submit(job);
                }
                try {
                    connection = jdbc.getDataSource().getConnection();
                    connection.setAutoCommit(false);

                    try (Statement statement = connection.createStatement()) {
                        statement.execute("CREATE TEMP TABLE _wf_migration_aux (id bigint, process_code varchar, unique_process_definition varchar, activity_instance_id varchar[], activity_definition_id varchar[], next_executor varchar[], flow_data jsonb)");
                    }

                    jdbc.execute((StatementCallback<Void>) (Statement s) -> {
                        try {
                            ResultSet resultSet = s.executeQuery(format("SELECT \"Id\",\"FlowStatus\",\"ProcessCode\",\"UniqueProcessDefinition\",\"ActivityInstanceId\",\"ActivityDefinitionId\",\"NextExecutor\" FROM %s WHERE \"Status\" = 'A' AND \"FlowStatus\" IN (%s)", entryTypeToSqlExpr(process), Joiner.on(",").join(activeFlowStatusLookupCodes))); //TODO filter only active, etc
                            while (resultSet.next()) {
                                queue.put(buildCard(process,
                                        ATTR_ID, resultSet.getLong(ATTR_ID),
                                        ATTR_FLOW_STATUS, lookupService.getLookup(resultSet.getLong(ATTR_FLOW_STATUS)),
                                        ATTR_FLOW_ID, resultSet.getString(ATTR_FLOW_ID),
                                        ATTR_PLAN_INFO, resultSet.getString(ATTR_PLAN_INFO),
                                        ATTR_TASK_INSTANCE_ID, resultSet.getObject(ATTR_TASK_INSTANCE_ID),
                                        ATTR_TASK_DEFINITION_ID, resultSet.getObject(ATTR_TASK_DEFINITION_ID),
                                        ATTR_NEXT_EXECUTOR, resultSet.getObject(ATTR_NEXT_EXECUTOR)));
                            }
                            return null;
                        } catch (InterruptedException ex) {
                            throw runtime(ex);
                        }
                    });

                    waitUntil(() -> processedCards.get() == activeCardsCount, -1);

                    logger.info("processed {} active instances", activeCardsCount);
                    checkArgument(flowsWithError.isEmpty(), "migration failed for %s / %s instances (note: db was not modified)", flowsWithError.size(), activeCardsCount);
                    logger.info("no errors, finalizing migration");
                    completeMigration();

                    try (Statement statement = connection.createStatement()) {
                        statement.execute("DROP TABLE _wf_migration_aux");
                    }

                    logger.info("commit changes to db");
                    connection.commit();
                    connection.setAutoCommit(true);//TODO check this

                } catch (Exception ex) {
                    throw runtime(ex);
                } finally {
                    shutdownQuietly(processorThreads);
                    if (connection != null) {
                        closeQuietly(connection);
                        connection = null;
                    }
                }
            }
            updateProcessProvider(process);
        }

        private void updateProcessProvider(Classe process) {
            logger.info("update process = {}, set default provider = river", process);
            dao.updateClass(ClassDefinitionImpl.copyOf(process).withMetadata(ClassMetadataImpl.copyOf(process.getMetadata()).withFlowProvider(RIVER).build()).build());

            logger.info("migration completed for process = {} total time = {}", process, toUserDuration(stopwatch.elapsed()));
        }

        private void prepareFlowInstance(Card flow) {
            try {
                Card card = doPrepareFlowInstance(flow);
                addPreparedCard(card);
            } catch (Exception ex) {
                logger.error("check error", ex);
                flowsWithError.add(flow);
            } finally {
                processedCards.incrementAndGet();
            }
        }

        private synchronized void addPreparedCard(Card card) throws SQLException {
            try (Statement statement = connection.createStatement()) {
                statement.execute(format("INSERT INTO _wf_migration_aux VALUES (%s,%s,%s,%s,%s,%s,%s::jsonb)",
                        card.getId(),
                        systemToSqlExpr(card.getString(ATTR_FLOW_ID)),
                        systemToSqlExpr(card.getString(ATTR_PLAN_INFO)),
                        systemToSqlExpr(card.get(ATTR_TASK_INSTANCE_ID, List.class)),
                        systemToSqlExpr(card.get(ATTR_TASK_DEFINITION_ID, List.class)),
                        systemToSqlExpr(card.get(ATTR_NEXT_EXECUTOR, List.class)),
                        systemToSqlExpr(card.getString(ATTR_FLOW_DATA))));

            }
        }

        private void completeMigration() throws SQLException {
            logger.info("loaded on db {} instances (temp table)", processedCards.get() - flowsWithError.size());
            try (Statement statement = connection.createStatement()) {
                if (skipHistory) {
                    logger.info("skip history mode active, disable target class triggers");
                    statement.execute(format("SELECT _cm3_class_triggers_disable(%s)", systemToSqlExpr(process)));
                }
                try {
                    logger.info("execute upgrade from temp to main table");
                    String plainIdExpr;
                    if (planMapping.isEmpty()) {
                        plainIdExpr = systemToSqlExpr(defaultPlan.getId());
                    } else {
                        plainIdExpr = "CASE \"UniqueProcessDefinition\"";
                        plainIdExpr += planMapping.entrySet().stream().map(e -> format(" WHEN %s THEN %s", systemToSqlExpr(e.getKey()), systemToSqlExpr(e.getValue().getId()))).collect(joining());
                        if (defaultPlan != null) {
                            plainIdExpr += format(" ELSE %s", systemToSqlExpr(defaultPlan.getId()));
                        }
                        plainIdExpr += " END";
                    }
                    logger.info("plan id mapping for inactive flows =< {} >", plainIdExpr);
                    logger.info("update {} inactive records", inactiveCardsCount);
                    statement.execute(format("UPDATE %s "
                            + " SET \"ProcessCode\" = _cm3_utils_random_id(),"
                            + " \"UniqueProcessDefinition\" = format('river#0#%%s',%s),"
                            + " \"ActivityInstanceId\" = ARRAY[]::varchar[],"
                            + " \"ActivityDefinitionId\" = ARRAY[]::varchar[],"
                            + " \"NextExecutor\" = ARRAY[]::varchar[],"
                            + " \"FlowData\" = jsonb_build_object('RiverFlowStatus', CASE _cm3_lookup_code(\"FlowStatus\") WHEN 'open.running' THEN 'RUNNING' WHEN 'open.not_running.suspended' THEN 'SUSPENDED' WHEN 'closed.aborted' THEN 'ABORTED' WHEN 'closed.completed' THEN 'COMPLETE' END)"
                            + " WHERE \"Status\" = 'A' AND \"FlowStatus\" NOT IN (_cm3_lookup('FlowStatus','open.running'),_cm3_lookup('FlowStatus','open.not_running.suspended'))", entryTypeToSqlExpr(process), plainIdExpr));
                    logger.info("update {} active records", activeCardsCount);
                    statement.execute(format("UPDATE %s "
                            + " SET \"ProcessCode\" = t.process_code,"
                            + " \"UniqueProcessDefinition\" = t.unique_process_definition,"
                            + " \"ActivityInstanceId\" = t.activity_instance_id,"
                            + " \"ActivityDefinitionId\" = t.activity_definition_id,"
                            + " \"NextExecutor\" = t.next_executor,"
                            + " \"FlowData\" = t.flow_data"
                            + " FROM _wf_migration_aux t WHERE \"Id\" = t.id", entryTypeToSqlExpr(process)));
                    statement.execute(format("UPDATE %s p "
                            + "SET \"ProcessCode\" = (SELECT \"ProcessCode\" FROM %s WHERE \"Status\"='A' AND \"Id\" = p.\"CurrentId\"),\n"
                            + "	\"UniqueProcessDefinition\" = (SELECT \"UniqueProcessDefinition\" FROM %s WHERE \"Status\"='A' AND \"Id\" = p.\"CurrentId\");", entryTypeToSqlExpr(process), entryTypeToSqlExpr(process), entryTypeToSqlExpr(process)));
                } finally {
                    if (skipHistory) {
                        logger.info("skip history mode active, re-enable target class triggers");
                        statement.execute(format("SELECT _cm3_class_triggers_enable(%s)", systemToSqlExpr(process)));
                    }
                }
            }
            logger.info("upgrade completed for {} instances", cardsCount);
        }

        private Card doPrepareFlowInstance(Card flow) {
            logger.debug("prepare migration for flow = {} (load data from shark, migrate activities), card {} of {}", flow, processedCards.get() + 1, activeCardsCount);
            RiverPlan riverPlan = getPlan(flow);
            try {
                checkArgument(!toFlowStatus(flow.get(ATTR_FLOW_STATUS, LookupValue.class)).isCompleted());
                logger.debug("get flow variables from shark for flow = {}", flow);
                Map<String, Object> data;
                List<String> riverTaskIds;
                List<String> activityPerformers;

                data = sharkHelper.getFlowDataForProcess(flow.getString(ATTR_FLOW_ID));
                logger.trace("shark flow variables = \n\n{}\n", mapToLoggableStringLazy(data));

                data = data.entrySet().stream().map(e -> Pair.of(e.getKey(), rawToRiverFlow(e.getKey(), e.getValue(), riverPlan))).collect(toMap(Pair::getKey, Pair::getValue));

                riverTaskIds = (List) flow.getNotNull(ATTR_TASK_INSTANCE_ID, List.class).stream().map((sharkActivityId) -> {
                    logger.debug("process shark activity = {}", sharkActivityId);
                    Triple<String, String, String> sharkActivityDefinitionIds = sharkHelper.getSharkActivityDefinitionIdWithParent((String) sharkActivityId);
                    logger.debug("shark activity id = {} has activity definition id = {} and parent activity definition id = {}", sharkActivityId, sharkActivityDefinitionIds.getLeft(), sharkActivityDefinitionIds.getMiddle());
                    String riverStepId;
                    if (isBlank(sharkActivityDefinitionIds.getRight())) {
                        riverStepId = sharkActivityDefinitionIds.getLeft();
                        logger.debug("No more parents, returning riverStepId: {}", riverStepId);
                    } else {
                        riverStepId = getAllParents(sharkActivityDefinitionIds, sharkActivityDefinitionIds.getLeft());
                        logger.debug("Returned riverstepId: {}", riverStepId);
                    }
                    checkNotNull(riverPlan.getStepById(riverStepId));
                    logger.debug("converting shark activity id = {} to river step/task id = {}", sharkActivityId, riverStepId);
                    checkNotNull(riverPlan.getTask(riverStepId));
                    return riverStepId;
                }).collect(toImmutableList());
                activityPerformers = flow.getNotNull(ATTR_NEXT_EXECUTOR, List.class);

                String serializedData = serializeRiverFlowData(data, process, riverPlan, toRiverFlowStatus(toFlowStatus(flow.get(ATTR_FLOW_STATUS, LookupValue.class))));

                return CardImpl.copyOf(flow)
                        .withAttribute(ATTR_TASK_INSTANCE_ID, riverTaskIds.stream().map((x) -> randomId()).collect(toList())) //TODO replace randomId with specific newTaskId function
                        .withAttribute(ATTR_TASK_DEFINITION_ID, riverTaskIds)
                        .withAttribute(ATTR_NEXT_EXECUTOR, activityPerformers)
                        .withAttribute(ATTR_PLAN_INFO, riverPlanIdToLegacyUniqueProcessDefinition(riverPlan.getId()))
                        .withAttribute(ATTR_FLOW_ID, randomId())//TODO use newRiverFlowId() function
                        .withAttribute(ATTR_FLOW_DATA, serializedData)
                        .build();
            } catch (Exception ex) {
                throw new WorkflowException(ex, "error preparing flow instance migration for flow = %s with plan = %s and source plan =< %s >", flow, riverPlan, flow.getString(ATTR_PLAN_INFO));
            }
        }

        private String getAllParents(Triple<String, String, String> sharkActivityIds, String riverId) {
            riverId = buildStepIdFromParentActivityIdAndActivityId(sharkActivityIds.getMiddle(), riverId);
            logger.debug("Getting parents for {} id, with {} riverStepId", sharkActivityIds.getLeft(), riverId);
            Triple<String, String, String> sharkActivityDefinitionIds = sharkHelper.getSharkActivityDefinitionIdWithParent((String) sharkActivityIds.getRight());
            if (isBlank(sharkActivityDefinitionIds.getRight())) {
                logger.debug("No more parents, returning riverStepId: {}", riverId);
                return riverId;
            } else {
                logger.debug("Another parent found");
                return getAllParents(sharkActivityDefinitionIds, riverId);
            }
        }

        @Nullable
        private Object rawToRiverFlow(String key, @Nullable Object value, RiverPlan plan) {
            RiverVariableInfo<?> info = plan.getGlobalVariables().get(key);
            if (info == null) {
                return value;
            } else {
                try {
                    Object converted = typeConverter.rawValueToFlowValue(value, info.getJavaType());
                    logger.trace("converted value =< {} > ({}) to value =< {} > ({})", value, getClassOfNullable(value).getName(), converted, getClassOfNullable(converted).getName());
                    return converted;
                } catch (Exception ex) {
                    if (setNullOnConvertError) {
                        logger.warn("error converting flow value {} = {} ({}) to type = {}: {}: will set value to null", key, value, getClassOfNullable(value).getName(), info.getJavaType().getName(), ex.toString());
                        logger.debug("error", ex);
                        return typeConverter.rawValueToFlowValue(null, info.getJavaType());
                    } else {
                        throw new WorkflowException(ex, "error converting flow value %s = %s (%s) to type = %s", key, value, getClassOfNullable(value).getName(), info.getJavaType().getName());
                    }
                }
            }
        }

        private void loadPlans() {
            planMapping.putAll(config.getFlowMigrationMapping().entrySet().stream().sorted(Ordering.natural().onResultOf(e -> PlanInfoImpl.deserialize(e.getKey()).getVersionInt())).collect(toMap(Entry::getKey, e -> {
                if (e.getValue().isNew()) {
                    logger.info("load xpdl for mapping {}", e.getKey());
                    return riverPlanRepository.getPlanById(workflowService.get().addXpdl(process.getName(), e.getValue().getContent()).getPlanId());
                } else if (e.getValue().isLegacy()) {
                    return copyExistingXpdlToRiver(PlanInfoImpl.deserialize(e.getKey()));
                } else {
                    throw new IllegalArgumentException("unsupported xpdl target = " + e.getValue());
                }
            })));

            if (config.getDefaultXpdl() != null) {
                if (config.getDefaultXpdl().isNew()) {
                    workflowService.get().addXpdl(process.getName(), config.getDefaultXpdl().getContent());
                } else if (config.getDefaultXpdl().isLegacy()) {
                    dao.getJdbcTemplate().queryForList(format("SELECT DISTINCT \"UniqueProcessDefinition\" FROM %s WHERE \"Status\" = 'A'", entryTypeToSqlExpr(process)), String.class).stream().filter(StringUtils::isNotBlank)
                            .filter(not(planMapping::containsKey)).map(PlanInfoImpl::deserialize).sorted(Ordering.natural().onResultOf(PlanInfo::getVersionInt)).forEach(p -> {
                        logger.info("load legacy xpdl for mapping {}", p);
                        planMapping.put(p.serialize(), copyExistingXpdlToRiver(p));
                    });
                }

                defaultPlan = riverPlanRepository.getPlanByClassIdOrNull(process.getName());
                logger.info("default target plan is = {}", defaultPlan);
            }

            logger.info("plan mapping = \n\n{}\n", mapToLoggableString(planMapping));
        }

        private RiverPlan getPlan(Card flow) {
            return checkNotNull(planMapping.getOrDefault(flow.getString(ATTR_PLAN_INFO), defaultPlan), "plan not found for flow = %s", flow);
        }

        private RiverPlan copyExistingXpdlToRiver(PlanInfo planInfo) {
            logger.info("copying existing xpdl from shark for id =< {} >", planInfo.serialize());
            XpdlData xpdlData = sharkHelper.getXpdlDataByPackageAndVersion(planInfo.getPackageId(), planInfo.getVersion());
            XpdlInfo xpdlInfo = workflowService.get().addXpdl(process.getName(), newDataSource(xpdlData.getXpdlData()));
            return riverPlanRepository.getPlanById(xpdlInfo.getPlanId());
        }
    }

    private static RiverFlowStatus toRiverFlowStatus(FlowStatus flowStatus) {
        switch (flowStatus) {
            case COMPLETED:
                return COMPLETE;
            case OPEN:
                return RUNNING;
            case SUSPENDED:
                return SUSPENDED;
            case ABORTED:
                return ABORTED;
            default:
                throw unsupported("unsupported flow status = %s", flowStatus);
        }
    }
}
