/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.plugin.patchmanager;

import static java.lang.String.format;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.cmdbuild.dao.config.inner.Patch;
import org.cmdbuild.dao.config.inner.PatchImpl;
import org.cmdbuild.dao.config.inner.PatchInfo;
import org.cmdbuild.dao.config.inner.PatchManager;
import org.cmdbuild.plugin.patchmanager.SystemPluginPatchManager;
import org.cmdbuild.modeldiff.core.SerializationHandle_String;
import org.cmdbuild.modeldiff.schema.SchemaCollector;
import org.cmdbuild.systemplugin.SystemPlugin;
import org.cmdbuild.systemplugin.SystemPluginService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElementOrNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
public class SystemPluginPatchManagerImpl implements SystemPluginPatchManager {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final PatchManager patchManager;
    private final SchemaCollector schemaCollector;

    public SystemPluginPatchManagerImpl(SystemPluginService systemPluginService, PatchManager patchManager, List<SchemaCollector> schemaCollector) {
        this.patchManager = checkNotNull(patchManager);
        this.schemaCollector = schemaCollector.stream().collect(onlyElementOrNull());
    }

    @Override
    public void applyPatches(SystemPlugin plugin) {
        if (hasPatches(plugin)) {
            getPatchesOnFile(plugin).forEach(patch -> {
                patchManager.applyPatchAndStore(patch);
                if (patch.isNotSql() && isSchemaFile(patch.getVersion())) {
                    if (schemaCollector != null) {
                        schemaCollector.applySchemaDiff(new SerializationHandle_String(patch.getContent().replace("-- PARAMS: NOT_SQL=true", "")));
                    } else {
                        logger.warn("skip patch =< {} > because model diff schema plugin is not available", patch.getVersion());
                    }
                }
            });
        }
    }

    @Override
    public List<Patch> getPatchesDb(SystemPlugin plugin) {
        return list(patchManager.getAllPatches()).filter(p -> p.hasPatchOnDb() && p.getCategory().equals(getCategoryPatch(plugin))).map(PatchInfo::getPatch);
    }

    @Override
    public List<Patch> getPatchesOnFile(SystemPlugin plugin) {
        return list(plugin.getResources("patches", "sql", "json").entrySet()).filter(e -> !list(getPatchesDb(plugin)).map(Patch::getVersion).contains(e.getKey())).map(entry -> {
            return PatchImpl.builder()
                    .withVersion(entry.getKey())
                    .withDescription(entry.getKey())
                    .withCategory(getCategoryPatch(plugin))
                    .accept(p -> {
                        if (isSchemaFile(entry.getKey())) {
                            p.withContent(format("-- PARAMS: NOT_SQL=true\n\n%s", new String(entry.getValue(), StandardCharsets.UTF_8)));
                        } else {
                            p.withContent(new String(entry.getValue(), StandardCharsets.UTF_8));
                        }
                    })
                    .build();
        }).sorted(Patch::getVersion);
    }

    @Override
    public boolean hasPatches(SystemPlugin plugin) {
        return !getPatchesOnFile(plugin).isEmpty();
    }

    private String getCategoryPatch(SystemPlugin plugin) {
        return format("plugin_%s", firstNotNull(plugin.getService(), plugin.getName()));
    }

    private boolean isSchemaFile(String fileName) {
        return fileName.endsWith(".json");
    }
}
