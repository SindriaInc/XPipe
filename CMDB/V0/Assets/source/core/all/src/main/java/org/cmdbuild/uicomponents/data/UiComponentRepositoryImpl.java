package org.cmdbuild.uicomponents.data;

import static com.google.common.collect.MoreCollectors.toOptional;
import jakarta.annotation.Nullable;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.systemplugin.SystemPlugin;
import org.cmdbuild.systemplugin.SystemPluginService;
import static org.cmdbuild.uicomponents.data.UiComponentType.UCT_ADMINCUSTOMPAGE;
import static org.cmdbuild.uicomponents.utils.UiComponentUtils.getAdminCustomPageCode;
import static org.cmdbuild.uicomponents.utils.UiComponentUtils.getCodeFromExtComponentData;
import static org.cmdbuild.uicomponents.utils.UiComponentUtils.normalizeComponentData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import org.springframework.stereotype.Component;

@Component
public class UiComponentRepositoryImpl implements UiComponentRepository {

    private final DaoService dao;
    private final SystemPluginService systemPluginService;
    private final Holder<List<UiComponentData>> all;
    private final CmCache<Optional<UiComponentData>> byCode;
    private final CmCache<UiComponentData> byId;

    public UiComponentRepositoryImpl(DaoService dao, SystemPluginService systemPluginService, CacheService cacheService) {
        this.dao = checkNotNull(dao);
        this.systemPluginService = checkNotNull(systemPluginService);
        all = cacheService.newHolder("ui_components_all", CacheConfig.SYSTEM_OBJECTS);
        byCode = cacheService.newCache("ui_components_by_code", CacheConfig.SYSTEM_OBJECTS);
        byId = cacheService.newCache("ui_components_by_id", CacheConfig.SYSTEM_OBJECTS);
    }

    private void invalidateCache() {
        all.invalidate();
        byCode.invalidateAll();
        byId.invalidateAll();
    }

    @Override
    public List<UiComponentData> getAll() {
        return list(all.get(this::doReadAll)).with(getAllSystemPluginUiComponent());
    }

    @Override
    @Nullable
    public UiComponentData getByTypeAndNameOrNull(UiComponentType type, String name) {
        checkNotNull(type);
        checkNotBlank(name);
        return byCode.get(key(type, name), () -> getAll().stream().filter((c) -> Objects.equals(type, c.getType()) && Objects.equals(c.getName(), name)).collect(toOptional())).orElse(null);
    }

    @Override
    public UiComponentData getById(long id) {
        return byId.get(id, () -> getAll().stream().filter((c) -> Objects.equals(c.getId(), id)).collect(onlyElement("ui component not found for id = %s", id)));
    }

    @Override
    public UiComponentData create(UiComponentData component) {
        component = dao.create(component);
        invalidateCache();
        return component;
    }

    @Override
    public UiComponentData update(UiComponentData component) {
        component = dao.update(component);
        invalidateCache();
        return component;
    }

    @Override
    public void delete(long id) {
        dao.delete(UiComponentData.class, id);
        invalidateCache();
    }

    private List<UiComponentData> doReadAll() {
        return dao.selectAll().from(UiComponentData.class).asList();
    }

    private List<UiComponentData> getAllSystemPluginUiComponent() {
        return systemPluginService.getSystemPlugins().stream().flatMap(p -> loadAdminCustompage(p).stream()).collect(toList());
    }

    private List<UiComponentData> loadAdminCustompage(SystemPlugin plugin) {
        return plugin.getResources("customcomponents.admincustompage", "zip").values().stream().map(data -> {
            return UiComponentDataImpl.builder().withId(-1L).withActive(Boolean.TRUE).withData(normalizeComponentData(singletonList(data))).withName(getCodeFromExtComponentData(singletonList(data))).withDescription(getAdminCustomPageCode(plugin.getName())).withType(UCT_ADMINCUSTOMPAGE).build(); // id to -1, it is mandatory
        }).collect(toList());
    }
}
