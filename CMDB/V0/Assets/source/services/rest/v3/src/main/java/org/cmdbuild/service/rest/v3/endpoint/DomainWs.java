package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_DOMAINS_MODIFY_AUTHORITY;
import org.cmdbuild.classe.access.UserDomainService;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.repository.DomainRepository;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.dao.utils.CmFilterUtils;
import static org.cmdbuild.dao.utils.DomainUtils.serializeDomainCardinality;
import org.cmdbuild.data.filter.AttributeFilterCondition;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.service.rest.common.serializationhelpers.DomainSerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.DomainSerializationHelper.WsDomainData;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ACTIVE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CARDINALITY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DESTINATION;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DOMAIN_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EXT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SOURCE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
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
                            case CARDINALITY ->
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
        Domain domain = domainRepository.createDomain(domainSerializationHelper.toDomainDefinition(data).build());
        return response(domainSerializationHelper.serializeDetailedDomain(domain));
    }

    @PUT
    @Path("{domainId}/")
    @RolesAllowed(ADMIN_DOMAINS_MODIFY_AUTHORITY)
    public Object update(@PathParam("domainId") String domainId, WsDomainData data) {
        Domain domain = domainRepository.getDomain(domainId);
        domain = domainRepository.updateDomain(domainSerializationHelper.toDomainDefinition(data).withOid(domain.getId()).build());
        return response(domainSerializationHelper.serializeDetailedDomain(domain));
    }

    @DELETE
    @Path("{domainId}/")
    @RolesAllowed(ADMIN_DOMAINS_MODIFY_AUTHORITY)
    public Object delete(@PathParam("domainId") String domainId) {
        domainRepository.deleteDomain(domainRepository.getDomain(domainId));
        return success();
    }
}
