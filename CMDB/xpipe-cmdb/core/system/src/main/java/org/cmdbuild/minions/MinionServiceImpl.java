/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.minions;

import com.github.lalyos.jfiglet.FigletFont;
import com.google.common.base.Joiner;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.Maps.uniqueIndex;
import com.google.common.collect.Ordering;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import static java.lang.String.format;
import java.lang.management.ManagementFactory;
import java.time.ZonedDateTime;
import java.util.Collection;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import javax.annotation.PreDestroy;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.config.api.ConfigReloadEvent;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.dao.ConfigurableDataSource;
import org.cmdbuild.dao.DatasourceConfiguredEvent;
import org.cmdbuild.dao.config.DatabaseConfiguration;
import org.cmdbuild.dao.config.inner.AllPatchesAppliedEvent;
import org.cmdbuild.dao.config.inner.PatchService;
import org.cmdbuild.dao.postgres.listener.PostgresNotificationEvent;
import org.cmdbuild.dao.postgres.listener.PostgresNotificationEventService;
import org.cmdbuild.debuginfo.BuildInfoService;
import org.cmdbuild.eventbus.EventBusService;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_ERROR;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_NOTRUNNING;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_READY;
import static org.cmdbuild.minions.MinionStatus.MS_DISABLED;
import static org.cmdbuild.minions.MinionStatus.MS_NOTRUNNING;
import static org.cmdbuild.minions.MinionStatus.MS_READY;
import static org.cmdbuild.minions.SystemStatus.SYST_BEFORE_DATABASE_CHECK;
import static org.cmdbuild.minions.SystemStatus.SYST_BEFORE_LOADING_CONFIG;
import static org.cmdbuild.minions.SystemStatus.SYST_BEFORE_PATCH_CHECK;
import static org.cmdbuild.minions.SystemStatus.SYST_BEFORE_READY;
import static org.cmdbuild.minions.SystemStatus.SYST_BEFORE_STARTING_SERVICES;
import static org.cmdbuild.minions.SystemStatus.SYST_BEFORE_STOPPING_SERVICES;
import static org.cmdbuild.minions.SystemStatus.SYST_CHECKING_DATABASE;
import static org.cmdbuild.minions.SystemStatus.SYST_CHECKING_PATCH;
import static org.cmdbuild.minions.SystemStatus.SYST_CLEANUP;
import static org.cmdbuild.minions.SystemStatus.SYST_ERROR;
import static org.cmdbuild.minions.SystemStatus.SYST_LOADING_CONFIG;
import static org.cmdbuild.minions.SystemStatus.SYST_LOADING_CONFIG_FILES;
import static org.cmdbuild.minions.SystemStatus.SYST_NOT_RUNNING;
import static org.cmdbuild.minions.SystemStatus.SYST_PREPARING_SERVICES;
import static org.cmdbuild.minions.SystemStatus.SYST_READY;
import static org.cmdbuild.minions.SystemStatus.SYST_STARTING_CLUSTER;
import static org.cmdbuild.minions.SystemStatus.SYST_STARTING_SERVICES;
import static org.cmdbuild.minions.SystemStatus.SYST_STOPPING_SERVICES;
import static org.cmdbuild.minions.SystemStatus.SYST_WAITING_FOR_APP_CONTEXT;
import static org.cmdbuild.minions.SystemStatus.SYST_WAITING_FOR_DATABASE_CONFIGURATION;
import static org.cmdbuild.minions.SystemStatus.SYST_WAITING_FOR_PATCH_MANAGER;
import static org.cmdbuild.minions.SystemStatusUtils.serializeSystemStatus;
import static org.cmdbuild.platform.PlatformUtils.checkOsUser;
import org.cmdbuild.plugin.SystemPluginService;
import org.cmdbuild.plugin.SystemPluginUtils;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.date.CmDateUtils.toUserDuration;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.queue;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExecutorUtils.namedThreadFactory;
import static org.cmdbuild.utils.lang.CmExecutorUtils.safe;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.multilineWithOffset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MinionServiceImpl implements MinionService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ExecutorService executor = Executors.newSingleThreadExecutor(namedThreadFactory(getClass()));

    private final DatabaseConfiguration databaseConfiguration;
    private final PatchService patchService;
    private final RequestContextService requestContextService;
    private final EventBus systemEventBus, datasourceEventBus;
    private final BuildInfoService buildInfoService;
    private final SystemPluginService pluginService;
    private final PostgresNotificationEventService pgEventService;
    private final MinionBeanRepository repository;
    private final GlobalConfigService configService;

    private final Map<String, Minion> minionsById = map();
    private MinionEngine minionEngine;

    private ZonedDateTime startupDateTime;
    private SystemStatus systemStatus = SYST_WAITING_FOR_APP_CONTEXT;

    public MinionServiceImpl(GlobalConfigService configService, SystemPluginService pluginService, ConfigurableDataSource dataSource, PostgresNotificationEventService pgEventService, MinionBeanRepository repository, BuildInfoService buildInfoService, DatabaseConfiguration databaseConfiguration, PatchService patchManager, RequestContextService requestContextService, EventBusService eventBusService) {
        this.patchService = checkNotNull(patchManager);
        this.requestContextService = checkNotNull(requestContextService);
        this.databaseConfiguration = checkNotNull(databaseConfiguration);
        this.buildInfoService = checkNotNull(buildInfoService);
        this.repository = checkNotNull(repository);
        this.pgEventService = checkNotNull(pgEventService);
        this.pluginService = checkNotNull(pluginService);
        this.configService = checkNotNull(configService);
        datasourceEventBus = dataSource.getEventBus();
        systemEventBus = eventBusService.getSystemEventBus();
        eventBusService.getConfigEventBus().register(new Object() {
            @Subscribe
            public void handleConfigReloadEvent(ConfigReloadEvent event) {
                if (equal(systemStatus, SYST_READY)) {
                    minionEngine.reloadServicesForConfigUpdateEvent(event);
                }
            }
        });
    }

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        logger.trace("spring context refresh event");
        if (hasStatus(SYST_WAITING_FOR_APP_CONTEXT)) {
            logger.info("spring context ready");

            checkOsUser();
            //TODO check system mem/resources, etc 

            logger.info("loading {} system services", repository.getMinionBeans().size());
            repository.getMinionBeans().forEach((innerBean) -> {
                try {
                    MinionImpl minion = new MinionImpl(innerBean.getBean());
                    minionsById.put(minion.getId(), minion);
                } catch (Exception ex) {
                    throw runtime(ex, "error processing system service bean =< %s > ( %s )", innerBean.getName(), innerBean.getBean());
                }
            });

            minionEngine = new MinionEngine(list(repository.getMinionBeans()).map(InnerBean::getBean).with((List) SYSTEM_MINIONS));

            executor.submit(safe(() -> {
                MDC.put("cm_type", "sys");
                MDC.put("cm_id", "sys:boot");
                requestContextService.initCurrentRequestContext("system startup");
                //TODO set user
                logger.info("start system");

                logger.debug("post app context ready event");
                setSystemStatus(SYST_PREPARING_SERVICES);
                systemEventBus.post(AppContextReadyEvent.INSTANCE);
                minionEngine.start(serializeEnum(SYST_LOADING_CONFIG_FILES));
            }));
        }
    }

    @Override
    public SystemStatus getSystemStatus() {
        return systemStatus;
    }

    @Override
    public List<InnerBean> getMinionBeans() {
        return repository.getMinionBeans();
    }

    @Override
    public Collection<Minion> getMinions() {
        return minionsById.values();
    }

    @Override
    public Minion getMinion(String id) {
        return checkNotNull(minionsById.get(checkNotBlank(id)), "service not found for id =< %s >", id);
    }

    @Override
    public void startSystem() {
        checkArgument(hasStatus(SYST_NOT_RUNNING), "cannot start services, invalid system status = %s (system must be in status %s)", serializeSystemStatus(systemStatus), serializeSystemStatus(SYST_NOT_RUNNING));
//        runNextStep(SYST_BEFORE_DATABASE_CHECK);
        clearSystemMinions();
        minionEngine.start(serializeEnum(SYST_BEFORE_DATABASE_CHECK));
    }

    @Override
    public void stopSystem() {
        if (!equal(SYST_READY, systemStatus)) {
            logger.warn(marker(), "cannot stop services, current system status = {} (system must be in status READY)", serializeSystemStatus(systemStatus));
        } else {
//            runNextStep(SYST_BEFORE_STOPPING_SERVICES);
            clearSystemMinions();
            minionEngine.start(serializeEnum(SYST_BEFORE_STOPPING_SERVICES));
        }
    }

    @PreDestroy
    public void cleanup() {
        shutdownQuietly(executor);
    }

    private void clearSystemMinions() {
        SYSTEM_MINIONS.forEach(SystemMinion::setNotRunning);//TODO improve this
    }

    private final List<SystemMinion> SYSTEM_MINIONS = ImmutableList.of(
            new SystemMinion((String) null, SYST_LOADING_CONFIG_FILES, postEvent(SystemLoadingConfigFilesEvent.INSTANCE)),
            new SystemMinion(SYST_LOADING_CONFIG_FILES, SYST_BEFORE_DATABASE_CHECK, null),
            new SystemMinion(SYST_BEFORE_DATABASE_CHECK, SYST_CHECKING_DATABASE, this::checkDatabase),
            new SystemMinion((String) null, SYST_BEFORE_PATCH_CHECK, null),
            new SystemMinion(SYST_BEFORE_PATCH_CHECK, SYST_CHECKING_PATCH, this::checkPatches),
            new SystemMinion((String) null, SYST_BEFORE_LOADING_CONFIG, null),
            new SystemMinion(SYST_BEFORE_LOADING_CONFIG, SYST_LOADING_CONFIG, postEvent(SystemLoadingConfigEvent.INSTANCE)),
            new SystemMinion(SYST_LOADING_CONFIG, SYST_BEFORE_STARTING_SERVICES, null),
            new SystemMinion(SYST_BEFORE_STARTING_SERVICES, SYST_STARTING_CLUSTER, null),
            new SystemMinion(SYST_STARTING_CLUSTER, SYST_STARTING_SERVICES, null),
            new SystemMinion(SYST_STARTING_SERVICES, SYST_BEFORE_READY, postEvent(SystemTriggerPostStartupEvent.INSTANCE)),
            new SystemMinion(SYST_BEFORE_READY, SYST_READY, () -> {
                setSystemStatus(SYST_READY);
                postEvent(SystemReadyEvent.INSTANCE).run();
            }),
            new SystemMinion((String) null, SYST_BEFORE_STOPPING_SERVICES, postEvent(SystemShutdownInitiatedEvent.INSTANCE)),
            new SystemMinion(SYST_BEFORE_STOPPING_SERVICES, SYST_STOPPING_SERVICES, () -> minionEngine.stopAllServices()),
            new SystemMinion(SYST_STOPPING_SERVICES, SYST_CLEANUP, postEvent(SystemTriggerPreShutdownEvent.INSTANCE)),
            new SystemMinion(SYST_CLEANUP, SYST_NOT_RUNNING, null));

    private class MinionEngine {

        private final Map<String, MinionComponent> minions;

        private final Queue<String> startQueue = queue();

        public MinionEngine(List<MinionComponent> minions) {
            this.minions = uniqueIndex(minions, MinionComponent::getName);
        }

        public synchronized void start(String name) {
            startQueue.add(checkNotBlank(name));
            Set<String> queued = set(startQueue);
            while (!startQueue.isEmpty()) {
                boolean changed = doStart(startQueue.poll());
                if (changed && startQueue.isEmpty()) {
                    logger.debug("check next service to auto start");
                    String next = getNextMinionToStartOrNull();
                    if (isNotBlank(next) && queued.add(next)) {
                        logger.debug("next service to start =< {} >", next);
                        startQueue.add(next);
                    } else {
                        logger.debug("no other service to start found");
                    }
                }
            }
        }

        public synchronized void queueForStart(String name) {
            startQueue.add(checkNotBlank(name));

        }

        @Nullable
        private String getNextMinionToStartOrNull() {
            return minions.values().stream()
                    .filter(m -> m.getMinionHandler().isAutostart() && m.getMinionHandler().hasStatus(MS_NOTRUNNING) && getRequires(m).stream().allMatch(r -> getMinion(r).isReady()))
                    .sorted(Ordering.natural().<MinionComponent>onResultOf(m -> m.getMinionHandler().getOrder()).thenComparing(MinionComponent::getName))
                    .filter(m -> m.getMinionHandler().checkRuntimeStatus(MRS_NOTRUNNING))
                    .findFirst().map(MinionComponent::getName).orElse(null);
        }

        private synchronized boolean doStart(String name) {
            MinionComponent minion = getMinion(name);
            return switch (minion.getMinionHandler().checkStatus()) {
                case MS_DISABLED -> {
                    logger.debug("skip startup of service =< {} >, service is disabled", name);
                    yield false;
                }
                case MS_READY -> {
                    logger.debug("skip startup of service =< {} >, service is already running", name);
                    yield false;
                }
                default -> {
                    logger.debug("start service =< {} > ( {} ) cur status = {}", name, minion, serializeEnum(minion.getStatus()));
                    try {
                        minion.start();
                        checkArgument(minion.isReady(), "service =< %s > failed to start", name);
                        logger.debug("started service =< {} >", name);
                    } catch (Throwable ex) {
                        logger.error(marker(), "error starting service =< {} >", name, ex);
                        if (minion instanceof SystemMinion) {//TODO
                            setSystemStatus(SYST_ERROR);
                        }
                    }
                    yield true;
                }
            };
        }

        public synchronized void stop(String name) {
            List<String> minionsToStop = list(name);
            logger.debug("preparing to stop service =< {} >", name);
            while (true) {
                List<String> newMinionsToStop = list(minionsToStop).map(this::getMinion).flatMap(this::getMinionsToStopBefore).map(MinionComponent::getName).distinct().filter((k) -> !minionsToStop.contains(k));
                if (newMinionsToStop.isEmpty()) {
                    break;
                } else {
                    minionsToStop.addAll(0, newMinionsToStop);
                }
            }
            if (minionsToStop.size() > 1) {
                logger.debug("stop services =< {} >", Joiner.on(", ").join(minionsToStop));
            }
            minionsToStop.forEach(this::doStop);
        }

        private synchronized void doStop(String name) {
            MinionComponent minion = getMinion(name);
            switch (minion.getMinionHandler().checkStatus()) {
                case MS_DISABLED ->
                    logger.debug("skip shutdown of service =< {} >, service is disabled", name);
                case MS_NOTRUNNING ->
                    logger.debug("skip shutdown of service =< {} >, service is already not running", name);
                default -> {
                    logger.debug("stop service =< {} > ( {} ) cur status = {}", name, minion, serializeEnum(minion.getStatus()));
                    try {
                        minion.stop();
//                        checkArgument(!minion.isReady(), "service =< %s > failed to stop", name);
                        logger.debug("stopped service =< {} >", name);
                    } catch (Exception ex) {
                        logger.error(marker(), "error stopping service =< {} >", name, ex);
                    }
                }
            }
        }

        public synchronized void stopAllServices() {
            logger.debug("stop all services");
            minions.values().stream()
                    .filter(m -> m.isReady() && !(m instanceof SystemMinion))
                    .sorted(Ordering.natural().<MinionComponent>onResultOf(m -> m.getMinionHandler().getOrder()).thenComparing(MinionComponent::getName).reversed())
                    .map(MinionComponent::getName).forEach(this::stop);
        }

        public synchronized void reloadServicesForConfigUpdateEvent(ConfigReloadEvent event) {
            logger.debug("reload services for config reload event = {}", event);
            reloadServices(list(minions.values()).filter(m -> getConfigNamespaces(m.getMinionHandler().getReloadOnConfigs()).stream().anyMatch(n -> event.impactNamespace(n)))
                    .sorted(Ordering.natural().<MinionComponent>onResultOf(m -> m.getMinionHandler().getOrder()).thenComparing(MinionComponent::getName))
                    .map(MinionComponent::getName).sorted().toSet());
        }

        public synchronized void reloadServices(Set<String> minionsToReload) {
            logger.debug("reload services = {}", minionsToReload);

            List<String> allMinionsToReload = list();

            Stack<String> stack = new Stack<>();
            list(minionsToReload).reverse().forEach(stack::push);
            while (!stack.isEmpty()) {
                String item = stack.pop();
                if (!allMinionsToReload.contains(item)) {
                    List<String> next = minions.values().stream().filter(m -> getRequires(m).contains(item)).map(MinionComponent::getName).filter(not(allMinionsToReload::contains)).collect(toList());
                    if (next.isEmpty()) {
                        allMinionsToReload.add(item);
                    } else {
                        stack.push(item);
                        list(next).sorted().reverse().forEach(stack::push);
                    }
                }
            }
            logger.debug("reload actual services = {}", allMinionsToReload);

            allMinionsToReload.forEach(this::stop);
            list(allMinionsToReload).reverse().forEach(this::start);
        }

        private Set<String> getConfigNamespaces(Collection<Object> items) {
            return list(items).map(i -> {
                if (i instanceof String ns) {
                    return ns;
                } else {
                    return configService.getConfigNamespaceFromConfigBeanClass((Class) i);
                }
            }).toSet();
        }

        private List<MinionComponent> getMinionsToStopBefore(MinionComponent minion) {
            return list(minions.values())
                    .filter(m -> m.getMinionHandler().hasStatus(MS_READY) && getRequires(m).contains(minion.getName()))
                    .sorted(Ordering.natural().<MinionComponent>onResultOf(m -> m.getMinionHandler().getOrder()).thenComparing(MinionComponent::getName).reversed());
        }

        private MinionComponent getMinion(String name) {
            return checkNotNull(minions.get(checkNotBlank(name)), "minion not found for name =< %s >", name);
        }

        private Set<String> getRequires(MinionComponent minion) {
            if (minion.getMinionHandler().getRequires().isEmpty() && !(minion instanceof SystemMinion)) {
                return singleton(serializeEnum(SYST_STARTING_SERVICES));
            } else {
                return minion.getMinionHandler().getRequires();
            }
        }

    }

    private class SystemMinion implements MinionComponent, MinionHandler {

        private final String name;
        private final Set<String> requires;
        private MinionRuntimeStatus status = MRS_NOTRUNNING;
        private final Runnable action;
        private final boolean autostart;
        private SystemStatus systemStatus;

        public SystemMinion(@Nullable SystemStatus after, SystemStatus systemStatus, @Nullable Runnable action) {
            this(serializeEnum(after), serializeEnum(systemStatus), action);
            this.systemStatus = systemStatus;
        }

        public SystemMinion(@Nullable String after, SystemStatus systemStatus, @Nullable Runnable action) {
            this(after, serializeEnum(systemStatus), action);
            this.systemStatus = systemStatus;
        }

        public SystemMinion(@Nullable String after, String name, @Nullable Runnable action) {
            this.name = checkNotBlank(name);
            this.action = firstNotNull(action, () -> {
            });
            if (isBlank(after)) {
                requires = emptySet();
                autostart = false;
            } else {
                requires = singleton(after);
                autostart = true;
            }
        }

        @Override
        public int getOrder() {
            return 100;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public boolean isAutostart() {
            return autostart;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public MinionRuntimeStatus getRuntimeStatus() {
            return status;
        }

        @Override
        public Set<String> getRequires() {
            return requires;
        }

        @Override
        public void start() {
            try {
                status = MRS_READY;
                if (this.systemStatus != null) {
                    setSystemStatus(systemStatus);
                }
                action.run();
            } catch (Exception ex) {
                status = MRS_ERROR;
                throw ex;
            }
        }

        @Override
        public MinionHandler getMinionHandler() {
            return this;
        }

        public void setNotRunning() {
            status = MRS_NOTRUNNING;
        }

        @Override
        public String toString() {
            return "SystemMinion{" + "name=" + name + '}';
        }

    }

//    @PostConstruct
//    public void test() {
//        logger.info("received application context PostConstruct event");
//    }
    @Override
    public ZonedDateTime getStartupDateTime() {
        return checkNotNull(startupDateTime, "startup date not available yet");
    }

    private void setSystemStatus(SystemStatus systemStatus) {
        if (!equal(systemStatus, this.systemStatus)) {
            this.systemStatus = checkNotNull(systemStatus);
            logger.debug("system status set to = {}", this.systemStatus);
            switch (this.systemStatus) {
                case SYST_READY -> {
                    startupDateTime = now();
                    String asciiArtBanner = multilineWithOffset(format("%s                    v%s READY", safe(() -> FigletFont.convertOneLine(firstNotBlank(buildInfoService.getVertName(), "CMDBuild"))), buildInfoService.getCompleteVersionNumber()), 8),
                            //                    String asciiArtBanner = multilineWithOffset(format("%s                    v%s READY", safe(() -> FigletFont.convertOneLine("classpath:/figlet/fonts/standard.flf", firstNotBlank(buildInfoService.getVertName(), "CMDBuild"))), buildInfoService.getCompleteVersionNumber()), 8),
                            servicesStatus = multilineWithOffset(MinionUtils.buildServicesStatusInfoMessage(list(getMinions()).without(Minion::isHidden)), 8),
                            pluginStatus = multilineWithOffset(SystemPluginUtils.buildPluginStatusInfoMessage(pluginService.getSystemPlugins()), 8),
                            sourceCodeVersionInfo = buildInfoService.getCommitInfo();
                    logger.info("\n\n\n\n{}\n\n\n\n{}\n\n{}\n\trunning source code rev {}\n\tstartup (uptime) {}\n\n", asciiArtBanner, servicesStatus, pluginStatus, sourceCodeVersionInfo, toUserDuration(ManagementFactory.getRuntimeMXBean().getUptime()));
                }
                case SYST_CHECKING_DATABASE, SYST_LOADING_CONFIG, SYST_WAITING_FOR_PATCH_MANAGER, SYST_WAITING_FOR_DATABASE_CONFIGURATION, SYST_STARTING_SERVICES, SYST_STOPPING_SERVICES, SYST_NOT_RUNNING, SYST_ERROR ->
                    logger.info("\n\n\n\n\tsystem is {}\n\n\n", serializeSystemStatus(this.systemStatus));

            }
        }
    }

    private Runnable postEvent(Object event) {
        checkNotNull(event);
        return () -> systemEventBus.post(event);
    }

    private void checkDatabase() {
        logger.info("check database");
        if (databaseConfiguration.hasConfig()) {
            minionEngine.queueForStart(serializeEnum(SYST_BEFORE_PATCH_CHECK));
        } else {
            datasourceEventBus.register(new Object() {

                @Subscribe
                public void handleDatasourceConfiguredEvent(DatasourceConfiguredEvent event) {
                    datasourceEventBus.unregister(this);
                    executor.submit(safe(() -> minionEngine.start(serializeEnum(SYST_BEFORE_PATCH_CHECK))));
                }
            });
            setSystemStatus(SYST_WAITING_FOR_DATABASE_CONFIGURATION);
        }
    }

    private void checkPatches() {
        logger.info("check patch");
        if (databaseConfiguration.enableAutoPatch() && patchService.hasPendingPatchesOrFunctions()) {
            try {
                patchService.applyPendingPatchesAndFunctions();
            } catch (Exception ex) {
                logger.error("error during auto-patching", ex);
            }
        }

        if (patchService.hasPendingPatchesOrFunctions()) {
            setSystemStatus(SYST_WAITING_FOR_PATCH_MANAGER);
            logger.info("patch manager is not ready, waiting for user input");
            Object listener = new Object() {

                @Subscribe
                public void handleAllPatchAppliedAndDatabaseReadyEvent(AllPatchesAppliedEvent event) {
                    handleEvent();
                }

                @Subscribe
                public void handlePostgresNotificationEvent(PostgresNotificationEvent event) {
                    if (event.isEvent("org.cmdbuild.dao.config.AllPatchesApplied")) {
                        handleEvent();
                    }
                }

                private void handleEvent() {
                    patchService.getEventBus().unregister(this);
                    pgEventService.getEventBus().unregister(this);
                    executor.submit(safe(() -> minionEngine.start(serializeEnum(SYST_BEFORE_LOADING_CONFIG))));
                }

            };
            patchService.getEventBus().register(listener);
            pgEventService.getEventBus().register(listener);
        } else {
            minionEngine.queueForStart(serializeEnum(SYST_BEFORE_LOADING_CONFIG));
        }
    }

    private class MinionImpl implements Minion {

        private final MinionHandler handler;

        public MinionImpl(MinionComponent component) {
            this.handler = checkNotNull(component.getMinionHandler());
        }

        @Override
        public String toString() {
            return "MinionImpl{" + "name=" + getName() + '}';
        }

        @Override
        public String getName() {
            return handler.getName();
        }

        @Override
        public String getDescription() {
            return handler.getDescription();
        }

        @Override
        public MinionStatus getStatus() {
            return handler.getStatus();
        }

        @Override
        public void startService() {
            throw new UnsupportedOperationException();
//            logger.info("start {}", getName());
//            checkArgument(canStop, "manual start/stop not supported for this service");
//            if (!equal(getStatus(), MS_DISABLED)) {
//                config.set(SERVICE_ENABLED, FALSE);
//                waitSafeUntil(() -> equal(getStatus(), MS_DISABLED), 5);
//            }
//            config.set(SERVICE_ENABLED, TRUE);
        }

        @Override
        public void stopService() {
            throw new UnsupportedOperationException();
//            logger.info("stop {}", getName());
//            checkArgument(canStop, "manual start/stop not supported for this service");
//            config.set(SERVICE_ENABLED, FALSE);
        }

        @Override
        public boolean isEnabled() {
            return handler.isEnabled();
        }

        @Override
        public boolean canStart() {
            return false;//canStop && set(MS_ERROR, MS_NOTRUNNING, MS_DISABLED).contains(getStatus());
        }

        @Override
        public boolean canStop() {
            return false;//canStop && set(MS_READY, MS_ERROR).contains(getStatus());
        }

        @Override
        public boolean isHidden() {
            return handler.isHidden();
        }

    }
}
