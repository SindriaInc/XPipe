package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Predicates;
import static com.google.common.collect.ImmutableList.toImmutableList;
import jakarta.annotation.Nullable;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ACCESS_AUTHORITY;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.cardfilter.CardFilterService;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.classe.access.UserDomainService;
import static org.cmdbuild.common.Constants.BASE_CLASS_NAME;
import static org.cmdbuild.common.Constants.BASE_PROCESS_CLASS_NAME;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.dao.constants.SystemAttributes.ALL_RESERVED_ATTRIBUTES;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.dao.utils.AttributeFilterProcessor.MapKeyToValueFunction;
import static org.cmdbuild.dao.utils.FulltextMatcherImpl.fulltextMatcher;
import org.cmdbuild.dashboard.DashboardService;
import org.cmdbuild.data.filter.CmdbFilter;
import static org.cmdbuild.data.filter.FilterType.ATTRIBUTE;
import static org.cmdbuild.data.filter.FilterType.FULLTEXT;
import org.cmdbuild.etl.config.WaterwayDescriptorService;
import org.cmdbuild.etl.gate.EtlGateService;
import static org.cmdbuild.etl.gate.inner.EtlGateHandlerType.ETLHT_CAD;
import static org.cmdbuild.etl.gate.inner.EtlGateHandlerType.ETLHT_DATABASE;
import static org.cmdbuild.etl.gate.inner.EtlGateHandlerType.ETLHT_IFC;
import org.cmdbuild.etl.loader.EtlTemplateService;
import static org.cmdbuild.etl.loader.EtlTemplateTarget.ET_CLASS;
import org.cmdbuild.jobs.JobService;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import org.cmdbuild.uicomponents.data.UiComponentRepository;
import static org.cmdbuild.uicomponents.data.UiComponentType.UCT_CONTEXTMENU;
import static org.cmdbuild.uicomponents.data.UiComponentType.UCT_CUSTOMPAGE;
import static org.cmdbuild.uicomponents.data.UiComponentType.UCT_WIDGET;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;
import org.cmdbuild.view.ViewService;
import org.cmdbuild.workflow.WorkflowService;

@Path("search")
@Produces(APPLICATION_JSON)
public class SearchWs {

    private final UserClassService classService;
    private final DaoService dao;
    private final LookupService lookupService;
    private final DashboardService dashboardService;
    private final ReportService reportService;
    private final UiComponentRepository uiComponentRepository;
    private final RoleRepository roleRepository;
    private final JobService jobService;
    private final EtlGateService gateService;
    private final EtlTemplateService templateService;
    private final CardFilterService filterService;
    private final ViewService viewService;
    private final WorkflowService workflowService;
    private final UserDomainService domainService;
    private final WaterwayDescriptorService waterwayDescriptorService;

    public SearchWs(UserClassService classService, DaoService dao, LookupService lookupService, DashboardService dashboardService, ReportService reportService, UiComponentRepository uiComponentRepository, RoleRepository roleRepository, JobService jobService, EtlGateService gateService, EtlTemplateService templateService, CardFilterService filterService, ViewService viewService, WorkflowService workflowService, UserDomainService domainService, WaterwayDescriptorService waterwayDescriptorService) {
        this.classService = checkNotNull(classService);
        this.dao = checkNotNull(dao);
        this.lookupService = checkNotNull(lookupService);
        this.dashboardService = checkNotNull(dashboardService);
        this.reportService = checkNotNull(reportService);
        this.uiComponentRepository = checkNotNull(uiComponentRepository);
        this.roleRepository = checkNotNull(roleRepository);
        this.jobService = checkNotNull(jobService);
        this.gateService = checkNotNull(gateService);
        this.templateService = checkNotNull(templateService);
        this.filterService = checkNotNull(filterService);
        this.viewService = checkNotNull(viewService);
        this.workflowService = checkNotNull(workflowService);
        this.domainService = checkNotNull(domainService);
        this.waterwayDescriptorService = checkNotNull(waterwayDescriptorService);
    }

    @GET
//    @Path("/{[^/].+[^/]:itemType}")
//    @Path("/{itemType}")
//    @Path("/{itemType:.+}")
    @Path("/{itemType:.+}")
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
    public Object search(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam("itemType") String type, WsQueryOptions query) {
        checkArgument(isAdminViewMode(viewMode), "this ws is only available for admin view mode");
        return response(paged(new SearchHelper(query.getQuery().getFilter()).search(type), query.getOffset(), query.getLimit()));
    }

    @GET
    @Path("/{itemType1}/{itemType2}")//TODO fix ws framework, improve this
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
    public Object search2(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam("itemType1") String type1, @PathParam("itemType2") String type2, WsQueryOptions query) {
        return search(viewMode, type1 + "/" + type2, query);
    }

    private class SearchHelper {

        private final CmdbFilter filter;

        public SearchHelper(CmdbFilter filter) {
            this.filter = checkNotNull(filter);
            filter.checkHasOnlySupportedFilterTypes(FULLTEXT, ATTRIBUTE);
        }

        public List<Map<String, Object>> search(String type) {
            switch (trimAndCheckNotBlank(type).toLowerCase()) {
                case "classes" -> {
                    return doSearch(
                            classService.getAllUserClasses().stream().filter(c -> !equal(c.getName(), BASE_CLASS_NAME)),
                            c -> list(c.getName(), c.getDescription()),
                            c -> map("_id", c.getName(), "name", c.getName(), "description", c.getDescription(), "type", serializeEnum(c.getClassType())),
                            c -> list(c.getServiceAttributes()).without(a -> ALL_RESERVED_ATTRIBUTES.contains(a.getName())),
                            a -> list(a.getName(), a.getDescription()),
                            a -> map("_id", a.getName(), "name", a.getName(), "description", a.getDescription()));
                }
                case "processes" -> {
                    return doSearch(
                            workflowService.getAllProcessClasses().stream().filter(c -> !equal(c.getName(), BASE_PROCESS_CLASS_NAME)),
                            c -> list(c.getName(), c.getDescription()),
                            c -> map("_id", c.getName(), "name", c.getName(), "description", c.getDescription()),
                            c -> list(c.getServiceAttributes()).without(a -> ALL_RESERVED_ATTRIBUTES.contains(a.getName())),
                            a -> list(a.getName(), a.getDescription()),
                            a -> map("_id", a.getName(), "name", a.getName(), "description", a.getDescription()));
                }
                case "dms/models" -> {
                    return doSearch(
                            dao.getAllClasses().stream().filter(Classe::isDmsModel),
                            c -> list(c.getName(), c.getDescription()),
                            c -> map("_id", c.getName(), "name", c.getName(), "description", c.getDescription()),
                            c -> list(c.getServiceAttributes()).without(a -> ALL_RESERVED_ATTRIBUTES.contains(a.getName())),
                            a -> list(a.getName(), a.getDescription()),
                            a -> map("_id", a.getName(), "name", a.getName(), "description", a.getDescription()));
                }
                case "domains" -> {
                    return doSearch(
                            domainService.getUserDomains().stream(),
                            d -> list(d.getName(), d.getDescription()),
                            d -> map("_id", d.getName(), "name", d.getName(), "description", d.getDescription()),
                            d -> list(d.getServiceAttributes()).without(a -> ALL_RESERVED_ATTRIBUTES.contains(a.getName())),
                            a -> list(a.getName(), a.getDescription()),
                            a -> map("_id", a.getName(), "name", a.getName(), "description", a.getDescription()));
                }
                case "lookup_types", "lookup/types" -> {
                    return doSearch(
                            lookupService.getAllTypes().stream().filter(LookupType::isDefaultSpeciality).filter(LookupType::isAccessDefault),
                            t -> list(t.getName()),
                            t -> map("_id", t.getId(), "name", t.getName(), "description", t.getName()),
                            t -> lookupService.getAllLookup(t).elements(),
                            l -> list(l.getCode(), l.getDescription()),
                            l -> map("_id", l.getId(), "name", l.getCode(), "description", l.getDescription()));
                }
                case "dms/categories" -> {
                    return doSearch(
                            lookupService.getAllTypes().stream().filter(LookupType::isDmsCategorySpeciality),
                            t -> list(t.getName()),
                            t -> map("_id", t.getId(), "name", t.getName(), "description", t.getName()),
                            t -> lookupService.getAllLookup(t).elements(),
                            l -> list(l.getCode(), l.getDescription()),
                            l -> map("_id", l.getId(), "name", l.getCode(), "description", l.getDescription()));
                }
                case "dashboards" -> {
                    return doSearch(
                            dashboardService.getAll().stream(),
                            d -> list(d.getName(), d.getDescription()),
                            d -> map("_id", d.getId(), "name", d.getName(), "description", d.getDescription()));
//                            d -> d.getConfig(),///TODO dashboard charts
//                            l -> list(l.getCode(), l.getDescription()),
//                            l -> map("_id", l.getId(), "name", l.getCode(), "description", l.getDescription()));
                }
                case "reports" -> {
                    return doSearch(
                            reportService.getAll().stream(),
                            r -> list(r.getName(), r.getDescription()),
                            r -> map("_id", r.getId(), "name", r.getName(), "description", r.getDescription()));
                }
                case "custompages" -> {
                    return doSearch(
                            uiComponentRepository.getAllByType(UCT_CUSTOMPAGE).stream(),
                            r -> list(r.getName(), r.getDescription()),
                            r -> map("_id", r.getId(), "name", r.getName(), "description", r.getDescription(), "devices", list(r.getTargetDevices()).map(CmConvertUtils::serializeEnum)));
                }
                case "components/contextmenu" -> {
                    return doSearch(
                            uiComponentRepository.getAllByType(UCT_CONTEXTMENU).stream(),
                            r -> list(r.getName(), r.getDescription()),
                            r -> map("_id", r.getId(), "name", r.getName(), "description", r.getDescription(), "devices", list(r.getTargetDevices()).map(CmConvertUtils::serializeEnum)));
                }
                case "components/widget" -> {
                    return doSearch(
                            uiComponentRepository.getAllByType(UCT_WIDGET).stream(),
                            r -> list(r.getName(), r.getDescription()),
                            r -> map("_id", r.getId(), "name", r.getName(), "description", r.getDescription(), "devices", list(r.getTargetDevices()).map(CmConvertUtils::serializeEnum)));
                }
                case "roles" -> {
                    return doSearch(
                            roleRepository.getAllGroups().stream(),
                            r -> list(r.getName(), r.getDescription(), r.getEmail()),
                            r -> map("_id", r.getId(), "name", r.getName(), "description", r.getDescription(), "email", r.getEmail()));
                }
                case "jobs" -> {
                    return doSearch(
                            jobService.getAllJobs().stream(),
                            j -> list(j.getCode(), j.getDescription()),
                            j -> map("_id", j.getId(), "name", j.getCode(), "description", j.getDescription(), "type", j.getType()));
                }
                case "etl/templates" -> {
                    return doSearch(
                            templateService.getTemplates().stream(),
                            t -> list(t.getCode(), t.getDescription(), serializeEnum(t.getFileFormat()), t.getTargetName(), (t.isTargetClass() || t.isTargetProcess()) ? getClassDescriptionIfExists(t.getTargetName()) : null),
                            t -> map("_id", t.getCode(), "name", t.getCode(), "fileFormat", serializeEnum(t.getFileFormat()), "description", t.getDescription(), "type", serializeEnum(t.getType()), "target", t.getTargetName(), "target_description", (t.isTargetClass() || t.isTargetProcess()) ? getClassDescriptionIfExists(t.getTargetName()) : null));
                }
                case "etl/gates" -> {
                    return doSearch(
                            gateService.getAll().stream().filter(g -> g.hasSingleHandlerOfType(ETLHT_IFC, ETLHT_CAD, ETLHT_DATABASE)),
                            g -> list(g.getCode(), g.getDescription()).with(g.getShowOnClasses()),//TODO target class ???
                            g -> map("_id", g.getCode(), "name", g.getCode(), "description", g.getDescription(), "type", g.getSingleHandlerType()),//TODO target class ???
                            g -> list(g.getAllTemplates()).distinct().map(templateService::getTemplateByName),
                            t -> list(t.getCode(), t.getDescription(), t.getTargetName(), equal(ET_CLASS, t.getTargetType()) ? getClassDescriptionIfExists(t.getTargetName()) : null),
                            t -> map("_id", t.getCode(), "name", t.getCode(), "description", t.getDescription(), "type", serializeEnum(t.getType()), "target", t.getTargetName(), "target_description", equal(ET_CLASS, t.getTargetType()) ? getClassDescriptionIfExists(t.getTargetName()) : null));
                }
                case "filters" -> {
                    return doSearch(
                            filterService.readAllSharedFilters().stream(),
                            f -> list(f.getName(), f.getDescription(), f.getOwnerName(), getClassDescriptionIfExists(f.getOwnerName())),
                            f -> map("_id", f.getId(), "name", f.getName(), "description", f.getDescription(), "target", f.getOwnerName(), "target_description", getClassDescriptionIfExists(f.getOwnerName())));
                }
                case "views" -> {
                    return doSearch(
                            viewService.getAllSharedViews().stream(),
                            v -> list(v.getName(), v.getDescription(), v.getSourceClass(), v.getSourceFunction(), getClassDescriptionIfExists(v.getSourceClass())),
                            v -> mapOf(String.class, Object.class).with("_id", v.getId(), "name", v.getName(), "description", v.getDescription(), "type", serializeEnum(v.getType())).accept(m -> {
                                switch (v.getType()) {
                                    case VT_FILTER, VT_JOIN ->
                                        m.put("target", v.getSourceClass(), "target_description", getClassDescriptionIfExists(v.getSourceClass()));
                                    case VT_SQL ->
                                        m.put("target", v.getSourceFunction());
                                }
                            }));
                }
                case "busdescriptors" -> {
                    return doSearch(
                            waterwayDescriptorService.getAllDescriptors().stream(),
                            j -> list(j.getCode(), j.getDescription()),
                            j -> map("_id", j.getId(), "name", j.getCode(), "description", j.getDescription()));
                }
                default ->
                    throw runtime("unsupported search item type =< %s >", type);
            }
        }

        @Nullable
        private String getClassDescriptionIfExists(@Nullable String maybeClassName) {
            return isBlank(maybeClassName) ? null : Optional.ofNullable(dao.getClasseOrNull(maybeClassName)).map(Classe::getDescription).orElse(null);
        }

        private <T> List<Map<String, Object>> doSearch(Stream<T> source, Function<T, Collection<String>> entryFiltrables, Function<T, FluentMap<String, Object>> mapper) {
            return doSearch(source, entryFiltrables, mapper, null, null, null);
        }

        private <T, O> List<Map<String, Object>> doSearch(Stream<T> source, Function<T, Collection<String>> entryFiltrables, Function<T, FluentMap<String, Object>> mapper, @Nullable Function<T, Collection<O>> helper, @Nullable Function<O, Collection<String>> itemFiltrables, @Nullable Function<O, Map<String, Object>> itemMapper) {
            return new TypedSearchHelper<>(entryFiltrables, mapper, helper, itemFiltrables, itemMapper).search(source);
        }

        private class TypedSearchHelper<T, O> {

            private final Function<T, Collection<O>> itemsHelper;
            private final Function<T, FluentMap<String, Object>> mapper;
            private final Function<O, Map<String, Object>> itemMapper;
            private final Function<T, Collection<String>> entryFiltrables;
            private final Function<O, Collection<String>> itemFiltrables;
            private final Predicate<FluentMap<String, Object>> recordFilter;
            private final Predicate<Collection<String>> fulltextMatcher;

            public TypedSearchHelper(Function<T, Collection<String>> entryFiltrables, Function<T, FluentMap<String, Object>> mapper, @Nullable Function<T, Collection<O>> helper, @Nullable Function<O, Collection<String>> itemFiltrables, @Nullable Function<O, Map<String, Object>> itemMapper) {
                this.entryFiltrables = checkNotNull(entryFiltrables);
                this.mapper = checkNotNull(mapper);
                this.itemsHelper = helper;
                this.itemMapper = itemMapper;
                this.itemFiltrables = itemFiltrables;
                recordFilter = filter.hasAttributeFilter() ? AttributeFilterProcessor.builder().withKeyToValueFunction(MapKeyToValueFunction.INSTANCE).withFilter(filter.getAttributeFilter()).build()::match : Predicates.alwaysTrue();
                fulltextMatcher = filter.hasFulltextFilter() ? fulltextMatcher(filter.getFulltextFilter().getQuery())::matchesAny : Predicates.alwaysTrue();
            }

            public List<Map<String, Object>> search(Stream<T> source) {
                return source.map(FilterHelper::new).filter(FilterHelper::matches).map(FilterHelper::map).filter(recordFilter).collect(toImmutableList());
            }

            private class FilterHelper {

                private final T entry;
                private final List<O> items;
                private final boolean matches;

                public FilterHelper(T entry) {
                    this.entry = checkNotNull(entry);
                    items = itemsHelper == null ? emptyList() : itemsHelper.apply(entry).stream().filter(itemFiltrables.andThen(fulltextMatcher::test)::apply).collect(toImmutableList());
                    matches = !items.isEmpty() || entryFiltrables.andThen(fulltextMatcher::test).apply(entry);
                }

                public boolean matches() {
                    return matches;
                }

                public FluentMap<String, Object> map() {
                    return mapper.apply(entry).accept(m -> {
                        if (itemsHelper != null) {
                            m.put("items", items.stream().map(itemMapper).collect(toImmutableList()));
                        }
                    });
                }

            }

        }
    }

}
