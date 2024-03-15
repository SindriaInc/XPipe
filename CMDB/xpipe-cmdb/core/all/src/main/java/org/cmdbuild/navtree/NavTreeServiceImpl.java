package org.cmdbuild.navtree;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.MoreCollectors.toOptional;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.cache.Holder;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.data.filter.SorterElementDirection.ASC;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Component
public class NavTreeServiceImpl implements NavTreeService {

    private final DaoService dao;
    private final CmCache<Optional<NavTree>> treeByType;
    private final Holder<List<NavTree>> allTrees;

    public NavTreeServiceImpl(DaoService dao, CacheService cacheService) {
        this.dao = checkNotNull(dao);
        this.treeByType = cacheService.newCache("nav_tree_by_type", CacheConfig.SYSTEM_OBJECTS);
        this.allTrees = cacheService.newHolder("nav_tree_all", CacheConfig.SYSTEM_OBJECTS);
    }

    private void invalidateCache() {
        treeByType.invalidateAll();
        allTrees.invalidate();
    }

    @Override
    public List<NavTree> getAll() {
        return allTrees.get(() -> dao.selectAll().from(NavTree.class).orderBy(ATTR_CODE, ASC).asList());
    }

    @Override
    @Nullable
    public NavTree getTreeOrNull(String type) {
        return treeByType.get(type, () -> Optional.ofNullable(doGetDomainTree(type))).orElse(null);
    }

    @Override
    public NavTree create(NavTree tree) {
        dao.createOnly(tree);
        invalidateCache();
        return getTree(tree.getName());
    }

    @Override
    public NavTree update(NavTree tree) {
        dao.updateOnly(NavTreeImpl.copyOf(tree).withId(getTree(tree.getName()).getId()).build());
        invalidateCache();
        return getTree(tree.getName());
    }

    @Override
    public void fixDirections(String treeId) {
        NavTree tree = getTreeOrNull(treeId);
        checkNotNull(tree, "cannot find the specified tree");
        dao.callFunction("_cm3_navtree_element_directions_invert", map("navcode", treeId));
        invalidateCache();
    }

    @Override
    public void removeTree(String treeType) {
        dao.delete(getTree(treeType));
        invalidateCache();
    }

    @Nullable
    private NavTree doGetDomainTree(String type) {
        return getAll().stream().filter(a -> equal(a.getName(), type)).collect(toOptional()).orElse(null);
    }

}
