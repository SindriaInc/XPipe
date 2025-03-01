package org.cmdbuild.auth.grant;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Maps.filterKeys;
import static com.google.common.collect.Maps.uniqueIndex;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.auth.grant.GrantConstants.GRANT_ATTR_ROLE_ID;
import static org.cmdbuild.auth.grant.GrantMode.GM_NONE;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.eventbus.EventBusService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class GrantDataRepositoryImpl implements GrantDataRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EventBusService grantEventBusService;

    private final DaoService dao;
    private final CmCache<List<GrantData>> grantDataByRoleId;

    public GrantDataRepositoryImpl(DaoService dao, CacheService cacheService, EventBusService grantEventBusService1) {
        this.dao = checkNotNull(dao);
        this.grantEventBusService = checkNotNull(grantEventBusService1);
        this.grantDataByRoleId = cacheService.newCache("grant_data_by_role_id");
    }

    private void invalidateCache() {
        grantDataByRoleId.invalidateAll();
        grantEventBusService.getGrantEventBus().post(GrantDataUpdatedEvent.INSTANCE);
    }

    @Override
    public List<GrantData> getGrantsForRole(long roleId) {
        return grantDataByRoleId.get(Long.toString(roleId), () -> doGetGrantsForRole(roleId));
    }

    private List<GrantData> doGetGrantsForRole(long roleId) {
        return Ordering.natural().onResultOf(GrantDataRepositoryImpl::keyForGrant).sortedCopy(dao.selectAll().from(GrantData.class)
                .where(GRANT_ATTR_ROLE_ID, EQ, roleId)
                .asList());
    }

    @Override
    public List<GrantData> getGrantsForTypeAndRole(PrivilegedObjectType type, long groupId) {
        checkNotNull(type);
        return getGrantsForRole(groupId).stream().filter((g) -> equal(g.getType(), type)).collect(toList());
    }

    @Override
    public List<GrantData> setGrantsForRole(long roleId, Collection<GrantData> grants) {
        return doUpdateGrantsForRole(roleId, grants, true);
    }

    @Override
    public List<GrantData> updateGrantsForRole(long roleId, Collection<GrantData> grants) {
        return doUpdateGrantsForRole(roleId, grants, false);
    }

    private List<GrantData> doUpdateGrantsForRole(long roleId, Collection<GrantData> grants, boolean deleteMissing) {
        grants = list(grants).map(g -> GrantDataImpl.copyOf(g).withRoleId(roleId).build());

        Map<String, GrantData> currentGrants = uniqueIndex(getGrantsForRole(roleId), GrantDataRepositoryImpl::keyForGrant);
        Map<String, GrantData> newGrants = uniqueIndex(grants, GrantDataRepositoryImpl::keyForGrant);

        Collection<GrantData> toDelete = list();
        if (deleteMissing) {
            filterKeys(currentGrants, not(in(newGrants.keySet()))).values().forEach(toDelete::add);
        }
        Collection<GrantData> toCreate = list(filterKeys(newGrants, not(in(currentGrants.keySet()))).values());

        List<GrantData> toUpdate = Sets.intersection(currentGrants.keySet(), newGrants.keySet()).stream().map((key) -> {
            GrantData currentGrant = checkNotNull(currentGrants.get(key));
            GrantData newGrant = checkNotNull(newGrants.get(key));

            return GrantDataImpl.copyOf(newGrant).withId(currentGrant.getId()).build();
        }).collect(toList());

        toUpdate.stream().filter(g -> g.isMode(GM_NONE)).forEach(toDelete::add);
        toUpdate.removeIf(g -> g.isMode(GM_NONE));
        toCreate.removeIf(g -> g.isMode(GM_NONE));

        toDelete.forEach(dao::delete);
        List<GrantData> res = list(toCreate.stream().map(dao::create).collect(toList()))
                .with(toUpdate.stream().map(dao::update).collect(toList()));

        invalidateCache();

        return Ordering.natural().onResultOf(GrantDataRepositoryImpl::keyForGrant).sortedCopy(res);
    }

    private static String keyForGrant(GrantData grantData) {
        return key(grantData.getType(), grantData.getObjectIdOrClassNameOrCode());
    }

}
