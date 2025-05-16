package org.cmdbuild.uicomponents.data;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.MoreCollectors.toOptional;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.cache.Holder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.serializeListOfStrings;

@Component
public class UiComponentRepositoryImpl implements UiComponentRepository {

    private final DaoService dao;
    private final Holder<List<UiComponentData>> all;
    private final CmCache<Optional<UiComponentData>> byCode;
    private final CmCache<UiComponentData> byId;

    public UiComponentRepositoryImpl(DaoService dao, CacheService cacheService) {
        this.dao = checkNotNull(dao);
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
        return all.get(this::doReadAll);
    }

    @Override
    @Nullable
    public UiComponentData getByTypeAndNameOrNull(UiComponentType type, String name) {
        checkNotNull(type);
        checkNotBlank(name);
        return byCode.get(key(type, name), () -> getAll().stream().filter((c) -> equal(type, c.getType()) && equal(c.getName(), name)).collect(toOptional())).orElse(null);
    }

    @Override
    public UiComponentData getById(long id) {
        return byId.get(id, () -> getAll().stream().filter((c) -> equal(c.getId(), id)).collect(onlyElement("ui component not found for id = %s", id)));
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

}
