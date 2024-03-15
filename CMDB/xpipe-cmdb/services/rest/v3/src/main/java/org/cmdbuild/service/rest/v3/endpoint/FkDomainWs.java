package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Objects.equal;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.cmdbuild.classe.access.UserClassService;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.data.filter.AttributeFilterCondition;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.dao.utils.CmFilterUtils;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DESTINATION;

import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SOURCE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.utils.lang.CmStringUtils;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.repository.FkDomainRepository;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_CREATE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_READ;
import static org.cmdbuild.dao.utils.DomainUtils.serializeDomainCardinality;
import org.cmdbuild.dao.entrytype.FkDomain;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;

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
                        switch (key) {
                            case SOURCE -> {
                                return domain.getSourceClass();
                            }
                            case DESTINATION -> {
                                return domain.getTargetClass();
                            }
                            case "cardinality" -> {
                                return serializeDomainCardinality(domain.getCardinality());
                            }
                            case "isMasterDetail" -> {
                                return domain.isMasterDetail();
                            }
                            default ->
                                throw new IllegalArgumentException("unsupported filter key = " + key);
                        }
                    })
                    .withConditionEvaluatorFunction(new AttributeFilterProcessor.ConditionEvaluatorFunction() {

                        @Override
                        public boolean evaluate(AttributeFilterCondition condition, Object value) {
                            switch (condition.getOperator()) {
                                case EQUAL -> {
                                    return equal(valueToString(value), condition.getSingleValue());
                                }
                                case IN -> {
                                    return condition.getValues().contains(valueToString(value));
                                }
                                case CONTAIN -> {
                                    return ((Classe) value).equalToOrAncestorOf(dao.getClasse(condition.getSingleValue())); //TODO filter also 
                                }
                                default ->
                                    throw new IllegalArgumentException("unsupported operator = " + condition.getOperator());
                            }
                        }

                        private String valueToString(Object value) {
                            if (value instanceof Classe) {
                                return ((Classe) value).getName();
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
                //                "sourceProcess", input.getSourceClass().isProcess(),
                "destination", input.getTargetClass().getName(),
                //                "destinationProcess", input.getTargetClass().isProcess(),
                "cardinality", serializeDomainCardinality(input.getCardinality()),
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
