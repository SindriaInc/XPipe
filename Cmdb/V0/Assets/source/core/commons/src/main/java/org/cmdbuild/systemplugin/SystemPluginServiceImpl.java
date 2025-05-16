/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.systemplugin;

import com.google.common.eventbus.EventBus;
import jakarta.activation.DataSource;
import java.io.File;
import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static java.util.stream.Collectors.joining;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.debuginfo.BuildInfoService;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.fault.FaultEvent;
import org.cmdbuild.fault.FaultEventImpl;
import org.cmdbuild.minions.PostStartup;
import org.cmdbuild.minions.SystemReadyRestartRequiredEvent;
import static org.cmdbuild.systemplugin.SystemPluginUtils.removePluginFilesFromWarFileInplace;
import static org.cmdbuild.systemplugin.SystemPluginUtils.scanFolderForPlugins;
import static org.cmdbuild.utils.encode.CmPackUtils.pack;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElementOrNull;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExecutorUtils.safe;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.unsafeConsumer;
import static org.cmdbuild.utils.maven.MavenUtils.checkRangeVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import static org.springframework.util.FileCopyUtils.copy;

@Component
@Primary
public class SystemPluginServiceImpl implements SystemPluginService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DirectoryService directoryService;
    private final BuildInfoService buildInfoService;
    private final GlobalConfigService configService;
    private final SystemPluginConfiguration pluginConfiguration;
    private final CacheService cacheService;
    private final EventBus systemEventBus;

    private final List<SystemPlugin> list = list();

    public SystemPluginServiceImpl(DirectoryService directoryService, BuildInfoService buildInfoService, GlobalConfigService configService, SystemPluginConfiguration pluginConfiguration, CacheService cacheService, EventBusService systemEventService) {
        this.directoryService = checkNotNull(directoryService);
        this.buildInfoService = checkNotNull(buildInfoService);
        this.configService = checkNotNull(configService);
        this.pluginConfiguration = checkNotNull(pluginConfiguration);
        this.cacheService = checkNotNull(cacheService);
        this.systemEventBus = systemEventService.getSystemEventBus();
    }

    @PostStartup
    public void init() {
        if (directoryService.hasWebappLibDirectory()) {
            logger.info("scan libs for system plugins");
            scanFolderForPlugins(directoryService.getWebappLibDirectory()).forEach(this::loadPlugin);
            logger.info("found {} system plugins: {}", list.size(), list.stream().map(SystemPlugin::getName).sorted().collect(joining(", ")));
            configService.putString("org.cmdbuild.plugin.info", pack(toJson(list(list).map(p -> map("name", p.getName(), "version", p.getVersion())))));
            configService.putString("org.cmdbuild.plugin.list", list(list).map(SystemPlugin::getNameVersion).collect(joining(",")));
            postPluginUpdateEvent();
        } else {
            logger.warn("webapp lib directory not available: skip system plugin processing");
        }
    }

    public void postPluginUpdateEvent() {
        cacheService.invalidate("wy_items"); //TODO improve this, drop cache to reload all bus descriptors inside jars
        cacheService.invalidate("ui_components_all"); //TODO improve this, drop cache to reload all ui components inside jars
        cacheService.invalidate("ui_components_by_code"); //TODO improve this, drop cache to reload all ui components inside jars
        cacheService.invalidate("ui_components_by_id"); //TODO improve this, drop cache to reload all ui components inside jars
    }

    @Override
    public List<SystemPlugin> getSystemPlugins() {
        return unmodifiableList(list);
    }

    @Override
    public SystemPlugin getSystemPlugin(String pluginName) {
        return getSystemPlugins().stream().filter(p -> Objects.equals(p.getName(), pluginName)).collect(onlyElement("plugin with name =< %s > not found", pluginName));
    }

    @Override
    public void deploySystemPlugins(Collection<DataSource> dataFiles) {
        checkArgument(directoryService.hasWebappLibDirectory(), "unable to deploy system plugins: webapp lib directory not available");
        Collection<File> files = createTemporaryFiles(dataFiles);
        Map<String, SystemPlugin> plugins = map(files, File::getName, SystemPluginUtils::scanFileForPlugin).filterValues(Objects::nonNull).mapValues(p -> addHealthCheck(p, files));
        checkArgument(!plugins.isEmpty(), "no plugin found for deploy");
        files.forEach(unsafeConsumer(f -> {
            logger.info(marker(), "deploy plugin file =< {} >", f.getName());
            SystemPlugin oldPlugin = list(list).filter(p -> Objects.equals(p.getName(), plugins.get(f.getName()).getName())).collect(onlyElementOrNull());
            if (oldPlugin != null) {
                logger.info(marker(), "upgrading plugin =< {} > with =< {} >", oldPlugin.getFilename(), f.getName());
                removePluginFilesFromWarFileInplace(directoryService.getWebappDirectory(), list(oldPlugin.getFilename()));
            }
            copy(f, new File(directoryService.getWebappLibDirectory(), f.getName()));
        }));
        systemEventBus.post(SystemReadyRestartRequiredEvent.INSTANCE);
    }

    private void loadPlugin(SystemPlugin plugin) {
        checkArgument(!list.stream().anyMatch(p -> Objects.equals(p.getName(), plugin.getName())), "duplicate plugin found for name =< %s >", plugin);
        list.add(addHealthCheck(plugin));
    }

    private SystemPlugin addHealthCheck(SystemPlugin plugin) {
        return addHealthCheck(plugin, emptyList());
    }

    private SystemPlugin addHealthCheck(SystemPlugin plugin, Collection<File> extraLibs) {
        List<FaultEvent> healthCheck = list();
        if (pluginConfiguration.isVersionCheckEnabled()) {
            if (checkRangeVersion(plugin.getRequiredCoreVersion(), buildInfoService.getBuildInfo().getVersionNumber())) {
                healthCheck.add(FaultEventImpl.warning("plugin version mismatch: required core version =< %s > actual core version =< %s >", plugin.getRequiredCoreVersion(), buildInfoService.getBuildInfo().getVersionNumber()));
            }
        }
        if (directoryService.hasWebappLibDirectory()) {
            plugin.getRequiredLibFiles().forEach(l -> {
                if (directoryService.getWebappLibDirectory().listFiles(f -> Objects.equals(f.getName(), l)).length == 0 && !extraLibs.stream().anyMatch(f -> Objects.equals(f.getName(), l))) {
                    healthCheck.add(FaultEventImpl.error("missing required lib =< %s >", l));
                }
            });
        } else {
            logger.warn("unable to check plugin dependencies, webapp lib directory not available");
        }
        healthCheck.forEach(f -> logger.warn(marker(), "found issue for plugin = {} : {}", plugin.getNameVersion(), f.getMessageAndLevel()));
        return SystemPluginImpl.copyOf(plugin).withHealthCheck(healthCheck).build();
    }

    private Collection<File> createTemporaryFiles(Collection<DataSource> dataFiles) {
        File tempDir = tempDir();
        dataFiles.forEach(safe(a -> writeToFile(a, new File(tempDir, a.getName()))));
        return list(tempDir.listFiles());
    }
}
