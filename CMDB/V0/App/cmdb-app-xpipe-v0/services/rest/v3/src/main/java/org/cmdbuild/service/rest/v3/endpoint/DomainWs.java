package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_DOMAINS_MODIFY_AUTHORITY;
import org.cmdbuild.classe.access.UserDomainService;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.dao.beans.DomainMetadataImpl;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.repository.DomainRepository;
import org.cmdbuild.dao.entrytype.CascadeAction;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.DomainDefinitionImpl;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.dao.utils.CmFilterUtils;
import static org.cmdbuild.dao.utils.DomainUtils.serializeDomainCardinality;
import org.cmdbuild.data.filter.AttributeFilterCondition;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.service.rest.common.serializationhelpers.JsonEcqlFilterHelper;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ACTIVE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DESTINATION;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DOMAIN_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EXT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SOURCE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import org.cmdbuild.service.rest.v3.serializationhelpers.DomainSerializationHelper;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.lang.CmStringUtils;

@Path("domains/")
@Produces(APPLICATION_JSON)
public class DomainWs {

    private final DomainRepository domainRepository;
    private final UserDomainService domainService;
    private final DaoService dao;
    private final DomainSerializationHelper domainSerializationHelper;

    public DomainWs(DomainRepository domainRepository, UserDomainService domainService, DaoService dao, DomainSerializationHelper domainSerializationHelper) {
        this.domainRepository = checkNotNull(domainRepository);
        this.domainService = checkNotNull(domainService);
        this.dao = checkNotNull(dao);
        this.domainSerializationHelper = checkNotNull(domainSerializationHelper);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @QueryParam(FILTER) String filterStr, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset, @QueryParam(EXT) Boolean includeFullDetails) {
        List<Domain> domains = isAdminViewMode(viewMode) ? domainService.getUserDomains(isAdminViewMode(viewMode)) : domainService.getActiveUserDomains();
        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
        filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
        if (filter.hasAttributeFilter()) {
            domains = AttributeFilterProcessor.<Domain>builder()
                    .withKeyToValueFunction((key, domain) -> {
                        return switch (key) {
                            case SOURCE ->
                                ((Domain) domain).getSourceClass();
                            case DESTINATION ->
                                ((Domain) domain).getTargetClass();
                            case ACTIVE ->
                                Boolean.toString(((Domain) domain).isActive());
                            case "cardinality" ->
                                serializeDomainCardinality(((Domain) domain).getCardinality());
                            default ->
                                throw new IllegalArgumentException("unsupported filter key = " + key);
                        };
                    })
                    .withConditionEvaluatorFunction(new AttributeFilterProcessor.ConditionEvaluatorFunction() {

                        @Override
                        public boolean evaluate(AttributeFilterCondition condition, Object value) {
                            return switch (condition.getOperator()) {
                                case EQUAL ->
                                    equal(valueToString(value), condition.getSingleValue());
                                case IN ->
                                    condition.getValues().contains(valueToString(value));
                                case CONTAIN ->
                                    ((Classe) value).equalToOrAncestorOf(dao.getClasse(condition.getSingleValue())); //TODO filter also 
                                default ->
                                    throw new IllegalArgumentException("unsupported operator = " + condition.getOperator());
                            };
                        }

                        private String valueToString(Object value) {
                            if (value instanceof Classe classe) {
                                return classe.getName();
                            } else {
                                return CmStringUtils.toStringOrNull(value);
                            }
                        }
                    })
                    .withFilter(filter.getAttributeFilter())
                    .filter(domains);
        }
        List list = paged(domains, offset, limit).stream().map(equal(includeFullDetails, Boolean.TRUE) ? domainSerializationHelper::serializeDetailedDomain : domainSerializationHelper::serializeBasicDomain).collect(toList());
        return response(list, domains.size());
    }

    @GET
    @Path("{" + DOMAIN_ID + "}/")
    public Object read(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam(DOMAIN_ID) String domainId) {
        Domain domain = domainService.getUserDomain(domainId, isAdminViewMode(viewMode));
        return response(domainSerializationHelper.serializeDetailedDomain(domain));
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_DOMAINS_MODIFY_AUTHORITY)
    public Object create(WsDomainData data) {
        Domain domain = domainRepository.createDomain(toDomainDefinition(data).build());
        return response(domainSerializationHelper.serializeDetailedDomain(domain));
    }

    @PUT
    @Path("{domainId}/")
    @RolesAllowed(ADMIN_DOMAINS_MODIFY_AUTHORITY)
    public Object update(@PathParam("domainId") String domainId, WsDomainData data) {
        Domain domain = domainRepository.getDomain(domainId);
        domain = domainRepository.updateDomain(toDomainDefinition(data).withOid(domain.getId()).build());
        return response(domainSerializationHelper.serializeDetailedDomain(domain));
    }

    @DELETE
    @Path("{domainId}/")
    @RolesAllowed(ADMIN_DOMAINS_MODIFY_AUTHORITY)
    public Object delete(@PathParam("domainId") String domainId) {
        domainRepository.deleteDomain(domainRepository.getDomain(domainId));
        return success();
    }

    private DomainDefinitionImpl.DomainDefinitionImplBuilder toDomainDefinition(WsDomainData domain) {
        return DomainDefinitionImpl.builder()
                .withName(domain.name)
                .withSourceClass(dao.getClasse(domain.source))
                .withTargetClass(dao.getClasse(domain.destination))
                .withMetadata(DomainMetadataImpl.builder()
                        .withCardinality(domain.cardinality)
                        .withDescription(domain.description)
                        .withDirectDescription(domain.descriptionDirect)
                        .withInverseDescription(domain.descriptionInverse)
                        .withIsActive(domain.isActive)
                        .withIsMasterDetail(domain.isMasterDetail)
                        .withDisabledSourceDescendants(domain.disabledSourceDescendants)
                        .withDisabledTargetDescendants(domain.disabledDestinationDescendants)
                        .withMasterDetailDescription(domain.descriptionMasterDetail)
                        .withMasterDetailFilter(domain.filterMasterDetail)
                        .withSourceIndex(domain.indexDirect)
                        .withTargetIndex(domain.indexInverse)
                        .withSourceInline(domain.inline1)
                        .withSourceDefaultClosed(domain.defaultClosed1)
                        .withTargetInline(domain.inline2)
                        .withTargetDefaultClosed(domain.defaultClosed2)
                        .withMasterDetailAggregateAttrs(domain.masterDetailAggregateAttrs)
                        .withMasterDetailDisabledCreateAttrs(domain.masterDetailDisabledCreateAttrs)
                        .withCascadeActionDirect(domain.cascadeActionDirect)
                        .withCascadeActionInverse(domain.cascadeActionInverse)
                        .withCascadeActionDirectAskConfirm(domain.cascadeActionDirectAskConfirm)
                        .withCascadeActionInverseAskConfirm(domain.cascadeActionInverseAskConfirm)
                        .withClassReferenceFilters(
                                JsonEcqlFilterHelper.toModel(domain.classReferenceFilters))
                        .build());
    }

    public static class WsDomainData {

        private final String name, description, source, destination, cardinality, descriptionDirect, descriptionInverse, descriptionMasterDetail, filterMasterDetail;
        private final Integer indexDirect, indexInverse;
        private final Boolean isActive, isMasterDetail, inline1, defaultClosed1, inline2, defaultClosed2, cascadeActionDirectAskConfirm, cascadeActionInverseAskConfirm;
        private final List<String> disabledSourceDescendants, disabledDestinationDescendants, masterDetailAggregateAttrs, masterDetailDisabledCreateAttrs;
        private final CascadeAction cascadeActionDirect, cascadeActionInverse;
        private final Map<String, String> classReferenceFilters;

        public WsDomainData(@JsonProperty("source") String source,
                @JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("destination") String destination,
                @JsonProperty("cardinality") String cardinality,
                @JsonProperty("descriptionDirect") String descriptionDirect,
                @JsonProperty("descriptionInverse") String descriptionInverse,
                @JsonProperty("indexDirect") Integer indexDirect,
                @JsonProperty("indexInverse") Integer indexInverse,
                @JsonProperty("descriptionMasterDetail") String descriptionMasterDetail,
                @JsonProperty("filterMasterDetail") String filterMasterDetail,
                @JsonProperty("disabledSourceDescendants") List<String> disabledSourceDescendants,
                @JsonProperty("disabledDestinationDescendants") List<String> disabledDestinationDescendants,
                @JsonProperty("masterDetailAggregateAttrs") List<String> masterDetailAggregateAttrs,
                @JsonProperty("masterDetailDisabledCreateAttrs") List<String> masterDetailDisabledCreateAttrs,
                @JsonProperty("active") Boolean isActive,
                @JsonProperty("isMasterDetail") Boolean isMasterDetail,
                @JsonProperty("sourceInline") Boolean inline1,
                @JsonProperty("sourceDefaultClosed") Boolean defaultClosed1,
                @JsonProperty("destinationInline") Boolean inline2,
                @JsonProperty("destinationDefaultClosed") Boolean defaultClosed2,
                @JsonProperty("cascadeActionDirect_askConfirm") Boolean cascadeActionDirectAskConfirm,
                @JsonProperty("cascadeActionInverse_askConfirm") Boolean cascadeActionInverseAskConfirm,
                @JsonProperty("cascadeActionDirect") String cascadeActionDirect,
                @JsonProperty("cascadeActionInverse") String cascadeActionInverse,
                @JsonProperty("classReferenceFilters") Map<String, Object> classReferenceFilters) {
            this.source = checkNotBlank(source);
            this.destination = checkNotBlank(destination);
            this.cardinality = checkNotBlank(cardinality);
            this.descriptionDirect = descriptionDirect;
            this.descriptionInverse = descriptionInverse;
            this.indexDirect = indexDirect;
            this.indexInverse = indexInverse;
            this.descriptionMasterDetail = descriptionMasterDetail;
            this.filterMasterDetail = filterMasterDetail;
            this.isActive = isActive;
            this.isMasterDetail = isMasterDetail;
            this.inline1 = inline1;
            this.defaultClosed1 = defaultClosed1;
            this.inline2 = inline2;
            this.defaultClosed2 = defaultClosed2;
            this.name = name;
            this.description = description;
            this.disabledSourceDescendants = disabledSourceDescendants;
            this.disabledDestinationDescendants = disabledDestinationDescendants;
            this.masterDetailAggregateAttrs = masterDetailAggregateAttrs;
            this.masterDetailDisabledCreateAttrs = masterDetailDisabledCreateAttrs;
            this.cascadeActionDirectAskConfirm = cascadeActionDirectAskConfirm;
            this.cascadeActionInverseAskConfirm = cascadeActionInverseAskConfirm;
            this.cascadeActionDirect = parseEnumOrNull(cascadeActionDirect, CascadeAction.class);
            this.cascadeActionInverse = parseEnumOrNull(cascadeActionInverse, CascadeAction.class);
            this.classReferenceFilters = map(nullToEmpty(classReferenceFilters))
                    .withoutKeys(k -> k.endsWith("_ecqlFilter"))
                    .entrySet().stream().collect(toMap(Map.Entry::getKey, e -> e.getValue().toString()));
        }

    }

}
