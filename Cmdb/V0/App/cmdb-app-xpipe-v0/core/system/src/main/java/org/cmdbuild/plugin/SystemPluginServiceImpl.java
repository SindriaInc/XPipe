/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.plugin;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.Files.copy;
import java.io.File;
import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import java.util.List;
import static java.util.stream.Collectors.joining;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.debuginfo.BuildInfoService;
import org.cmdbuild.fault.FaultEvent;
import org.cmdbuild.fault.FaultEventImpl;
import org.cmdbuild.platform.PlatformService;
import static org.cmdbuild.plugin.SystemPluginUtils.scanFilesForPlugins;
import static org.cmdbuild.plugin.SystemPluginUtils.scanFolderForPlugins;
import static org.cmdbuild.utils.encode.CmPackUtils.pack;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.unsafeConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.minions.PostStartup;

@Component
public class SystemPluginServiceImpl implements SystemPluginService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DirectoryService directoryService;
    private final BuildInfoService buildInfoService;
    private final PlatformService platformService;
    private final GlobalConfigService configService;

    private final List<SystemPlugin> list = list();

    public SystemPluginServiceImpl(DirectoryService directoryService, BuildInfoService buildInfoService, PlatformService platformService, GlobalConfigService configService) {
        this.directoryService = checkNotNull(directoryService);
        this.buildInfoService = checkNotNull(buildInfoService);
        this.platformService = checkNotNull(platformService);
        this.configService = checkNotNull(configService);
    }

    @PostStartup
    public void init() {
        if (directoryService.hasWebappLibDirectory()) {
            logger.info("scan libs for system plugins");
            scanFolderForPlugins(directoryService.getWebappLibDirectory()).forEach(this::loadPlugin);
            logger.info("found {} system plugins: {}", list.size(), list.stream().map(SystemPlugin::getName).sorted().collect(joining(", ")));
            configService.putString("org.cmdbuild.plugin.info", pack(toJson(list(list).map(p -> map("name", p.getName(), "version", p.getVersion())))));
            configService.putString("org.cmdbuild.plugin.list", list(list).map(SystemPlugin::getNameVersion).collect(joining(",")));
        } else {
            logger.warn("webapp lib directory not available: skip system plugin processing");
        }
    }

    @Override
    public List<SystemPlugin> getSystemPlugins() {
        return unmodifiableList(list);
    }

    @Override
    public void deploySystemPlugins(Collection<File> files) {
        checkArgument(directoryService.hasWebappLibDirectory(), "unable to deploy system plugins: webapp lib directory not available");
        List<SystemPlugin> plugins = list(scanFilesForPlugins(files)).map(p -> addHealthCheck(p, files));
        checkArgument(!plugins.isEmpty(), "no plugin found for deploy");
        //TODO filter non-plugin jars ???
        files.forEach(unsafeConsumer(f -> {
            logger.info(marker(), "deploy plugin file =< {} >", f.getName());
            copy(f, new File(directoryService.getWebappLibDirectory(), f.getName()));
        }));
        platformService.restartContainer();
    }

    private void loadPlugin(SystemPlugin plugin) {
        checkArgument(!list.stream().anyMatch(p -> equal(p.getName(), plugin.getName())), "duplicate plugin found for name =< %s >", plugin);
        list.add(addHealthCheck(plugin));
    }

    private SystemPlugin addHealthCheck(SystemPlugin plugin) {
        return addHealthCheck(plugin, emptyList());
    }

    private SystemPlugin addHealthCheck(SystemPlugin plugin, Collection<File> extraLibs) {
        List<FaultEvent> healthCheck = list();
        if (!equal(buildInfoService.getBuildInfo().getVersionNumber(), plugin.getRequiredCoreVersion())) {//TODO improve version check
            healthCheck.add(FaultEventImpl.warning("plugin version mismatch: required core version =< %s > actual core version =< %s >", plugin.getRequiredCoreVersion(), buildInfoService.getBuildInfo().getVersionNumber()));
        }
        if (directoryService.hasWebappLibDirectory()) {
            plugin.getRequiredLibFiles().forEach(l -> {
                if (directoryService.getWebappLibDirectory().listFiles(f -> equal(f.getName(), l)).length == 0 && !extraLibs.stream().anyMatch(f -> equal(f.getName(), l))) {
                    healthCheck.add(FaultEventImpl.error("missing required lib =< %s >", l));
                }
            });
        } else {
            logger.warn("unable to check plugin dependencies, webapp lib directory not available");
        }
        healthCheck.forEach(f -> logger.warn(marker(), "found issue for plugin = {} : {}", plugin.getNameVersion(), f.getMessageAndLevel()));
        return SystemPluginImpl.copyOf(plugin).withHealthCheck(healthCheck).build();
    }

}
