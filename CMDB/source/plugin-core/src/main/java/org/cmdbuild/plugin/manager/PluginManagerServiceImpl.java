/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.plugin.manager;

import java.io.File;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import org.cmdbuild.common.localization.LanguageService;
import org.cmdbuild.dao.config.inner.Patch;
import org.cmdbuild.dms.inner.DmsProviderService;
import org.cmdbuild.etl.job.EtlLoadHandler;
import org.cmdbuild.minions.PostStartup;
import org.cmdbuild.plugin.PluginService;
import org.cmdbuild.plugin.PremiumPluginService;
import org.cmdbuild.plugin.checker.PluginChecker;
import org.cmdbuild.plugin.patchmanager.SystemPluginPatchManager;
import org.cmdbuild.systemplugin.SystemPlugin;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.cmdbuild.uicomponents.admincustompage.AdminCustomPageService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElementOrNull;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrDefault;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
public class PluginManagerServiceImpl implements PluginManagerService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, PluginService> pluginService;
    private final AdminCustomPageService adminCustomPageService;
    private final LanguageService languageService;
    private final SystemPluginPatchManager pluginPatchManager;
    private final PluginChecker pluginChecker;

    private final static String DMS_MODULE = "dms", WY_MODULE = "waterway", GENERIC = "generic";

    public PluginManagerServiceImpl(List<PluginService> pluginService, AdminCustomPageService adminCustomPageService, LanguageService languageService, SystemPluginPatchManager pluginPatchManager, List<PluginChecker> pluginChecker) {
        this.pluginService = map(pluginService, PluginService::getName).immutable();
        this.adminCustomPageService = checkNotNull(adminCustomPageService);
        this.languageService = checkNotNull(languageService);
        this.pluginPatchManager = checkNotNull(pluginPatchManager);
        this.pluginChecker = pluginChecker.stream().collect(onlyElementOrNull());
    }

    @PostStartup
    public void init() {
        checkPluginSignature();
    }

    @Override
    public String getModuleType(SystemPlugin plugin) {
        return applyOrDefault(getServiceOrNull(plugin), p -> {
            if (p instanceof DmsProviderService) {
                return DMS_MODULE;
            } else if (p instanceof EtlLoadHandler) {
                return WY_MODULE;
            } else {
                return GENERIC;
            }
        }, GENERIC);
    }

    @Override
    public UiComponentInfo getAdminCustomPageOrNull(SystemPlugin plugin) {
        return adminCustomPageService.getByPluginOrNull(plugin);
    }

    @Override
    public Map<String, Object> getConfigs(SystemPlugin plugin) {
        try {
            return applyOrNull(getServiceOrNull(plugin), p -> p.getConfigs(languageService.getContextLanguage()));
        } catch (Exception ex) {
            logger.warn("error retrieving configs, returning null", ex);
            return emptyMap();
        }
    }

    @Override
    public List<Patch> getPatches(SystemPlugin plugin) {
        return list(pluginPatchManager.getPatchesDb(plugin)).with(pluginPatchManager.getPatchesOnFile(plugin));
    }

    @Override
    public boolean hasPatches(SystemPlugin plugin) {
        return pluginPatchManager.hasPatches(plugin);
    }

    @Override
    public void applyPatches(SystemPlugin plugin) {
        pluginPatchManager.applyPatches(plugin);
    }

    private PluginService getServiceOrNull(SystemPlugin plugin) {
        return applyOrNull(plugin.getService(), pluginService::get);
    }

    private void checkPluginSignature() {
        list(pluginService.values()).filter(PremiumPluginService.class).forEach(plugin -> {
            logger.info("premium plugin detected =< {} >", plugin.getName());
            String pathJar = plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            if (pluginChecker != null) {
                if (!pluginChecker.checkPlugin(plugin.getName(), pathJar)) {
                    removePluginNotSigned(plugin.getName(), pathJar);
                }
            } else {
                removePluginNotSigned(plugin.getName(), pathJar);
            }
        });
    }

    private void removePluginNotSigned(String pluginName, String path) throws RuntimeException {
        new File(path).delete();
        throw runtime("premium plugin detected, your license cannot execute %s", pluginName);
    }
}
