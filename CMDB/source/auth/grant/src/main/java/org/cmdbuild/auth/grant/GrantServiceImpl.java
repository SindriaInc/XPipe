/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.grant;

import com.google.common.base.Function;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Supplier;
import com.google.common.collect.ComparisonChain;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Maps.transformValues;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import org.cmdbuild.auth.grant.GrantDataImpl.GrantDataImplBuilder;
import static org.cmdbuild.auth.grant.GrantMode.GM_NONE;
import static org.cmdbuild.auth.grant.GrantUtils.modeToPrivileges;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_CLASS;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_CUSTOMPAGE;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_DASHBOARD;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_ETLGATE;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_ETLTEMPLATE;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_FILTER;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_OFFLINE;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_PROCESS;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_REPORT;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_VIEW;
import org.cmdbuild.authorization.CardFilterAsPrivilegeSubject;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.cardfilter.CardFilterService;
import static org.cmdbuild.common.Constants.BASE_CLASS_NAME;
import static org.cmdbuild.common.Constants.BASE_PROCESS_CLASS_NAME;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dashboard.DashboardRepository;
import org.cmdbuild.etl.gate.inner.EtlGate;
import org.cmdbuild.etl.gate.inner.EtlGateRepository;
import org.cmdbuild.etl.loader.EtlTemplateRepository;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.offline.OfflineService;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.uicomponents.custompage.CustomPageService;
import org.cmdbuild.utils.lang.CmCollectionUtils.FluentList;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmExceptionUtils.illegalArgument;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmNullableUtils.isBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import org.cmdbuild.view.ViewDefinitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GrantServiceImpl implements GrantService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final GrantDataRepository repository;
    private final DaoService dao;
    private final ViewDefinitionService viewService;
    private final CardFilterService filterStore;
    private final CustomPageService customPagesService;
    private final ReportService reportService;
    private final CmCache<Collection<Grant>> privilegePairListByTypeAndGroupId;
    private final EtlTemplateRepository etlTemplateRepository;
    private final DashboardRepository dashboardRepository;
    private final EtlGateRepository etlGateRepository;
    private final OfflineService offlineService;

    public GrantServiceImpl(DashboardRepository dashboardRepository, OfflineService offlineService, EtlGateRepository etlGateRepository, EtlTemplateRepository importExportTemplateService, EventBusService grantEventBus, ReportService reportService, GrantDataRepository repository, DaoService dao, ViewDefinitionService viewService, CardFilterService filterStore, CustomPageService customPagesService, CacheService cacheService) {
        this.repository = checkNotNull(repository);
        this.dao = checkNotNull(dao);
        this.viewService = checkNotNull(viewService);
        this.filterStore = checkNotNull(filterStore);
        this.customPagesService = checkNotNull(customPagesService);
        this.reportService = checkNotNull(reportService);
        this.dashboardRepository = checkNotNull(dashboardRepository);
        this.etlTemplateRepository = checkNotNull(importExportTemplateService);
        this.etlGateRepository = checkNotNull(etlGateRepository);
        this.offlineService = checkNotNull(offlineService);
        privilegePairListByTypeAndGroupId = cacheService.newCache("cache_privilege_pair_list_by_type_and_group_id");
        grantEventBus.getGrantEventBus().register(new Object() {

            @Subscribe
            public void handleGrantDataUpdatedEvent(GrantDataUpdatedEvent event) {
                privilegePairListByTypeAndGroupId.invalidateAll();
            }
        });
    }

    @Override
    public List<GrantData> getGrantsForRoleIncludeRecordsWithoutGrant(long roleId) {
        List<GrantData> grantsForRole = repository.getGrantsForRole(roleId);
        Set<String> grantsForRoleKeys = grantsForRole.stream().map((g) -> key(g.getType(), g.getObjectIdOrClassNameOrCode())).collect(toSet());
        Supplier<GrantDataImplBuilder> builder = () -> GrantDataImpl.builder().withMode(GM_NONE).withRoleId(roleId);
        List<GrantData> list = list(grantsForRole);

        List<Classe> classes = dao.getAllClasses().stream()
                .filter(c -> c.hasServiceListPermission())
                .filter(c -> !c.isDmsModel())
                .filter(c -> !set(BASE_CLASS_NAME, BASE_PROCESS_CLASS_NAME).contains(c.getName())) //TODO improve this
                .collect(toList());

        classes.stream().filter(c -> !c.isProcess()).filter((c) -> !grantsForRoleKeys.contains(key(POT_CLASS, c.getName()))).map((c) -> builder.get().withType(POT_CLASS).withClassName(c.getName()).build()).forEach(list::add);
        classes.stream().filter(c -> c.isProcess()).filter((c) -> !grantsForRoleKeys.contains(key(POT_PROCESS, c.getName()))).map((c) -> builder.get().withType(POT_PROCESS).withClassName(c.getName()).build()).forEach(list::add);

        customPagesService.getAll().stream().filter((c) -> !grantsForRoleKeys.contains(key(POT_CUSTOMPAGE, c.getId()))).map((c) -> builder.get().withType(POT_CUSTOMPAGE).withObjectId(c.getId()).build()).forEach(list::add);

        dashboardRepository.getAll().stream().filter((c) -> !grantsForRoleKeys.contains(key(POT_DASHBOARD, c.getId()))).map((c) -> builder.get().withType(POT_DASHBOARD).withObjectId(c.getId()).build()).forEach(list::add);

        filterStore.readAllSharedFilters().stream().filter((f) -> !grantsForRoleKeys.contains(key(POT_FILTER, f.getId()))).map((f) -> builder.get().withType(POT_FILTER).withObjectId(f.getId()).build()).forEach(list::add);

        reportService.getAll().stream().filter((r) -> !grantsForRoleKeys.contains(key(POT_REPORT, r.getId()))).map((r) -> builder.get().withType(POT_REPORT).withObjectId(r.getId()).build()).forEach(list::add);

        viewService.getAllSharedViews().stream().filter((v) -> !grantsForRoleKeys.contains(key(POT_VIEW, v.getId()))).map((v) -> builder.get().withType(POT_VIEW).withObjectId(v.getId()).build()).forEach(list::add);

        etlTemplateRepository.getTemplates().stream().filter((v) -> !grantsForRoleKeys.contains(key(POT_ETLTEMPLATE, v.getCode()))).map((v) -> builder.get().withType(POT_ETLTEMPLATE).withObjectCode(v.getCode()).build()).forEach(list::add);

        etlGateRepository.getAll().stream().filter(not(EtlGate::getAllowPublicAccess)).filter(g -> isBlank(g.getConfig("tag"))).filter((v) -> !grantsForRoleKeys.contains(key(POT_ETLGATE, v.getCode()))).map((v) -> builder.get().withType(POT_ETLGATE).withObjectCode(v.getCode()).build()).forEach(list::add);

        offlineService.getAll().stream().filter((v) -> !grantsForRoleKeys.contains(key(POT_OFFLINE, v.getCode()))).map((v) -> builder.get().withType(POT_OFFLINE).withObjectCode(v.getCode()).build()).forEach(list::add);

        Collections.sort(list, (a, b) -> ComparisonChain.start().compare(a.getType(), b.getType()).compare(a.getObjectIdOrClassNameOrCode().toString(), b.getObjectIdOrClassNameOrCode().toString()).result());
        return list;
    }

    @Override
    @Nullable
    public String getGrantObjectDescription(GrantData grant) {
        try {
            return switch (grant.getType()) {
                case POT_CLASS, POT_PROCESS ->
                    dao.getClasse(grant.getClassName()).getDescription();
                case POT_CUSTOMPAGE ->
                    customPagesService.getForUser(grant.getObjectId()).getDescription();
                case POT_FILTER ->
                    filterStore.getById(grant.getObjectId()).getDescription();
                case POT_REPORT ->
                    reportService.getById(grant.getObjectId()).getDescription();
                case POT_VIEW ->
                    viewService.getById(grant.getObjectId()).getDescription();
                case POT_ETLTEMPLATE ->
                    etlTemplateRepository.getTemplateByName(grant.getObjectCode()).getDescription();
                case POT_ETLGATE ->
                    etlGateRepository.getByCode(grant.getObjectCode()).getDescription();
                case POT_DASHBOARD ->
                    dashboardRepository.getDashboardById(grant.getObjectId()).getDescription();
                case POT_OFFLINE ->
                    offlineService.getById(grant.getObjectId()).getDescription();
                default ->
                    throw illegalArgument("unsupported grant type = %s", grant.getType());
            };
        } catch (Exception ex) {
            logger.warn(marker(), "error retrieving description for grant record = {}", grant, ex);
            return null;
        }
    }

    @Override
    public List<GrantData> getGrantsForTypeAndRole(PrivilegedObjectType type, long groupId) {
        return repository.getGrantsForTypeAndRole(type, groupId);
    }

    @Override
    public List<GrantData> getGrantsForRole(long roleId) {
        return repository.getGrantsForRole(roleId);
    }

    @Override
    public List<GrantData> setGrantsForRole(long roleId, Collection<GrantData> grants) {
        return repository.setGrantsForRole(roleId, grants);
    }

    @Override
    public List<GrantData> updateGrantsForRole(long roleId, Collection<GrantData> grants) {
        return repository.updateGrantsForRole(roleId, grants);
    }

    @Override
    public Collection<Grant> getAllPrivilegesByGroupId(long groupId) {
        return list(getClassPrivilegesByGroupId(groupId))
                .with(getViewPrivilegesByGroupId(groupId))
                .with(getFilterPrivilegesByGroupId(groupId))
                .with(getCustomPagesPrivilegesByGroupId(groupId))
                .with(getReportPrivilegesByGroupId(groupId))
                .with(getEtlTemplatePrivilegesByGroupId(groupId))
                .with(getEtlGatePrivilegesByGroupId(groupId))
                .with(getOfflinePrivilegesByGroupId(groupId))
                .with(getDashboardPrivilegesByGroupId(groupId));
    }

    private Collection<Grant> getClassPrivilegesByGroupId(long groupId) {
        return list(getPrivilegesByGroupId(PrivilegedObjectType.POT_CLASS, groupId, (p) -> dao.getClasse(p.getClassName())))
                .with(getPrivilegesByGroupId(PrivilegedObjectType.POT_PROCESS, groupId, (p) -> dao.getClasse(p.getClassName())));
    }

    private Collection<Grant> getViewPrivilegesByGroupId(long groupId) {
        return getPrivilegesByGroupId(PrivilegedObjectType.POT_VIEW, groupId, (p) -> viewService.getById(p.getObjectId())); //TODO refactor like report service below
    }

    private Collection<Grant> getFilterPrivilegesByGroupId(long groupId) {
        return getPrivilegesByGroupId(PrivilegedObjectType.POT_FILTER, groupId, (p) -> new CardFilterAsPrivilegeSubject(filterStore.getSharedFilterById(p.getObjectId()))); //TODO refactor like report service below
    }

    private Collection<Grant> getCustomPagesPrivilegesByGroupId(long groupId) {
        return getPrivilegesByGroupId(PrivilegedObjectType.POT_CUSTOMPAGE, groupId, (p) -> customPagesService.getCustomPageAsPrivilegeSubjectById(p.getObjectId()));
    }

    private Collection<Grant> getReportPrivilegesByGroupId(long groupId) {
        return getPrivilegesByGroupId(PrivilegedObjectType.POT_REPORT, groupId, (p) -> reportService.getReportAsPrivilegeSubjectById(p.getObjectId()));
    }

    private Collection<Grant> getDashboardPrivilegesByGroupId(long groupId) {
        return getPrivilegesByGroupId(PrivilegedObjectType.POT_DASHBOARD, groupId, (p) -> dashboardRepository.getDashboardById(p.getObjectId()).getInfo());
    }

    private Collection<Grant> getEtlTemplatePrivilegesByGroupId(long groupId) {
        return getPrivilegesByGroupId(PrivilegedObjectType.POT_ETLTEMPLATE, groupId, (p) -> etlTemplateRepository.getTemplateByName(p.getObjectCode()));
    }

    private Collection<Grant> getEtlGatePrivilegesByGroupId(long groupId) {
        return getPrivilegesByGroupId(PrivilegedObjectType.POT_ETLGATE, groupId, (p) -> etlGateRepository.getByCode(p.getObjectCode()));
    }

    private Collection<Grant> getOfflinePrivilegesByGroupId(long groupId) {
        return getPrivilegesByGroupId(PrivilegedObjectType.POT_OFFLINE, groupId, (p) -> offlineService.getByCode(p.getObjectCode()));
    }

    private Collection<Grant> getPrivilegesByGroupId(PrivilegedObjectType type, long groupId, Function<GrantData, PrivilegeSubjectWithInfo> fun) {
        return privilegePairListByTypeAndGroupId.get(key(type.name(), Long.toString(groupId)), () -> doGetPrivilegesByGroupId(type, groupId, fun));
    }

    private Collection<Grant> doGetPrivilegesByGroupId(PrivilegedObjectType type, long groupId, Function<GrantData, PrivilegeSubjectWithInfo> fun) {
        FluentList<Grant> res = list();
        repository.getGrantsForTypeAndRole(type, groupId).forEach((p) -> {
            try {
                PrivilegeSubjectWithInfo object = checkNotNull(fun.apply(p));
                res.add(toGrant(p, object));
            } catch (Exception ex) {
                logger.warn(marker(), "error processing grant record = {}", p, ex);
//                dao.delete(GrantData.class, p.getId()); // TODO handle with config
//                logger.warn("removed grant record = {}", p);
            }
        });
        return res.immutable();
    }

    private Grant toGrant(GrantData grant, PrivilegeSubjectWithInfo object) {
        return GrantImpl.builder()
                .withObject(object)
                .withObjectType(grant.getType())
                .withPrivilegeFilter(grant.getPrivilegeFilter())
                .withCustomPrivileges(grant.getCustomPrivileges())
                .accept((b) -> {
                    b.withPrivileges(modeToPrivileges(grant.getMode()));
                    switch (grant.getType()) {
                        case POT_CLASS, POT_PROCESS -> {
                            dao.getClasse(object.getName()); //check class exists
                            b.withAttributePrivileges(map(transformValues(nullToEmpty(grant.getAttributePrivileges()), p -> parseEnum(p, GrantAttributePrivilege.class))));
                            b.withdmsPrivileges(map(transformValues(nullToEmpty(grant.getDmsPrivileges()), p -> parseEnum(p, GrantAttributePrivilege.class))));
                            b.withGisPrivileges(map(transformValues(nullToEmpty(grant.getGisPrivileges()), p -> parseEnum(p, GrantAttributePrivilege.class))));
                        }
                    }
                })
                .build();
    }

    @Override
    public GrantData getGrantDataByRoleAndTypeAndName(Long id, PrivilegedObjectType objectType, String objectTypeName) {
        List<GrantData> grantData = getGrantsForRoleIncludeRecordsWithoutGrant(id);
        return getOnlyElement(grantData.stream().filter(p -> p.getType().equals(objectType) && toStringOrNull(p.getObjectIdOrClassNameOrCode()).equals(objectTypeName)).collect(toList()),
                getOnlyElement(grantData.stream().filter(p -> {
                    if (POT_VIEW.equals(objectType) && p.getType().equals(objectType)) {
                        return viewService.getAllSharedViews().stream().anyMatch(v -> v.getName().equals(objectTypeName) && Objects.equals(v.getId(), p.getObjectId()));
                    }
                    if (POT_FILTER.equals(objectType) && p.getType().equals(objectType)) {
                        return filterStore.readAllSharedFilters().stream().anyMatch(f -> f.getName().equals(objectTypeName) && Objects.equals(f.getId(), p.getObjectId()));
                    }
                    return false;
                }).collect(toList()), null));
    }

}
