package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.lang.String.format;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.cmdbuild.classe.access.UserClassService;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.repository.FkDomainRepository;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_CREATE;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.FkDomain;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.dao.utils.CmFilterUtils;
import static org.cmdbuild.dao.utils.DomainUtils.serializeDomainCardinality;
import org.cmdbuild.data.filter.AttributeFilterCondition;
import org.cmdbuild.data.filter.CmdbFilter;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DESTINATION;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SOURCE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmExceptionUtils.illegalArgument;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.utils.lang.CmStringUtils;

@Path("fkdomains/")
@Produces(APPLICATION_JSON)
public class FkDomainWs {

    private final FkDomainRepository domainRepository;
    private final DaoService dao;
    private final UserClassService classService;
    private final ObjectTranslationService translationService;

    public FkDomainWs(FkDomainRepository domainRepository, DaoService dao, UserClassService userClassService, ObjectTranslationService translationService) {
        this.domainRepository = checkNotNull(domainRepository);
        this.dao = checkNotNull(dao);
        this.classService = checkNotNull(userClassService);
        this.translationService = checkNotNull(translationService);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @QueryParam(FILTER) String filterStr, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset) {
        List<FkDomain> domains = domainRepository.getAllFkDomains();
//                isAdminViewMode(viewMode) ? domainService.getUserDomains() : domainService.getActiveUserDomains(); TODO user access (?)
        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
        if (filter.hasAttributeFilter()) {
            domains = AttributeFilterProcessor.<FkDomain>builder()
                    .withKeyToValueFunction((key, domain) -> {
                        return switch (key) {
                            case SOURCE ->
                                domain.getSourceClass();
                            case DESTINATION ->
                                domain.getTargetClass();
                            case "cardinality" ->
                                serializeDomainCardinality(domain.getCardinality());
                            case "isMasterDetail" ->
                                domain.isMasterDetail();
                            default ->
                                throw illegalArgument("unsupported filter key = %s", key);
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
                                    throw illegalArgument("unsupported operator = %s", condition.getOperator());
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
        List list = paged(domains, offset, limit).stream().map(this::serializeFkDomain).collect(toList());
        return response(list, domains.size());
    }

    private FluentMap<String, Object> serializeFkDomain(FkDomain input) {
        return map(
                "_id", format("%s_%s", input.getSourceClass().getName(), input.getSourceAttr().getName()),
                "source", input.getSourceClass().getName(),
                "sourceProcess", input.getSourceClass().isProcess(),
                "destination", input.getTargetClass().getName(),
                "destinationProcess", input.getTargetClass().isProcess(),
                "cardinality", serializeDomainCardinality(input.getCardinality()),
                "cascadeAction", serializeEnum(input.getCascadeAction()),
                //                "descriptionDirect", input.getDirectDescription(),
                //                "_descriptionDirect_translation", translationService.translateDomainDirectDescription(input.getName(), input.getDirectDescription()),
                //                "descriptionInverse", input.getInverseDescription(),
                //                "_descriptionInverse_translation", translationService.translateDomainInverseDescription(input.getName(), input.getInverseDescription()),
                //                "indexDirect", input.getIndexForSource(),
                //                "indexInverse", input.getIndexForTarget(),
                "descriptionMasterDetail", input.getMasterDetailDescription(),
                "_descriptionMasterDetail_translation", translationService.translateAttributeFkMasterDetailDescription(input.getSourceAttr(), input.getMasterDetailDescription()),
                //                "_descriptionMasterDetail_translation", translationService.translateDomainMasterDetailDescription(input.getName(), input.getMasterDetailDescription()),
                //                "filterMasterDetail", input.getMasterDetailFilter(),
                "isMasterDetail", input.isMasterDetail(),
                "fk_attribute_name", input.getSourceAttr().getName(),
                "fk_attribute_direction", serializeEnum(input.getDirection()),
                //                "inline", input.getMetadata().isInline(),
                //                "defaultClosed", input.getMetadata().isDefaultClosed(),
                //                "active", input.isActive(),
                //                "disabledSourceDescendants", CmCollectionUtils.toList(input.getDisabledSourceDescendants()),
                //                "disabledDestinationDescendants", CmCollectionUtils.toList(input.getDisabledTargetDescendants())
                "_can_create", Optional.ofNullable(classService.getUserClassOrNull(input.getSourceClass().getName())).map(c -> c.hasUiPermission(CP_CREATE)).orElse(false)
        );
    }

}
