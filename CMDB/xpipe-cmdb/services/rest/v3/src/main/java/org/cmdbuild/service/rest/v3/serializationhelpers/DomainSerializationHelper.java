/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.serializationhelpers;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;
import static org.cmdbuild.dao.beans.RelationDirection.RD_INVERSE;
import org.cmdbuild.dao.entrytype.AbstractMetadata;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_UPDATE;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.utils.DomainUtils.getActualCascadeAction;
import static org.cmdbuild.dao.utils.DomainUtils.serializeDomainCardinality;
import org.cmdbuild.ecql.EcqlBindingInfo;
import org.cmdbuild.ecql.utils.EcqlUtils;
import static org.cmdbuild.ecql.utils.EcqlUtils.getEcqlExpression;
import org.cmdbuild.service.rest.common.serializationhelpers.EcqlFilterSerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.JsonEcqlFilterHelper;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.springframework.stereotype.Component;

@Component
public class DomainSerializationHelper {

    private final ObjectTranslationService translationService;

    public DomainSerializationHelper(ObjectTranslationService translationService) {
        this.translationService = checkNotNull(translationService);
    }

    public CmMapUtils.FluentMap<String, Object> serializeBasicDomain(Domain domain) {
        return map("_id", domain.getName(),
                "name", domain.getName(),
                "description", domain.getDescription());
    }

    public CmMapUtils.FluentMap<String, Object> serializeDetailedDomain(Domain domain) {
        return serializeBasicDomain(domain).with("source", domain.getSourceClass().getName(),
                "sources", domain.getSourceClasses().stream().map(Classe::getName).sorted().collect(toList()),
                "sourceProcess", domain.getSourceClass().isProcess(),
                "destination", domain.getTargetClass().getName(),
                "destinations", domain.getTargetClasses().stream().map(Classe::getName).sorted().collect(toList()),
                "destinationProcess", domain.getTargetClass().isProcess(),
                "cardinality", serializeDomainCardinality(domain.getCardinality()),
                "descriptionDirect", domain.getDirectDescription(),
                "_descriptionDirect_translation", translationService.translateDomainDirectDescription(domain.getName(), domain.getDirectDescription()),
                "descriptionInverse", domain.getInverseDescription(),
                "_descriptionInverse_translation", translationService.translateDomainInverseDescription(domain.getName(), domain.getInverseDescription()),
                "indexDirect", domain.getIndexForSource(),
                "indexInverse", domain.getIndexForTarget(),
                "descriptionMasterDetail", domain.getMasterDetailDescription(),
                "_descriptionMasterDetail_translation", translationService.translateDomainMasterDetailDescription(domain.getName(), domain.getMasterDetailDescription()),
                "filterMasterDetail", domain.getMasterDetailFilter(),
                "isMasterDetail", domain.isMasterDetail(),
                "sourceInline", domain.getMetadata().isSourceInline(),
                "sourceDefaultClosed", domain.getMetadata().isSourceDefaultClosed(),
                "destinationInline", domain.getMetadata().isTargetInline(),
                "destinationDefaultClosed", domain.getMetadata().isTargetDefaultClosed(),
                "active", domain.isActive(),
                "disabledSourceDescendants", CmCollectionUtils.toList(domain.getDisabledSourceDescendants()),
                "disabledDestinationDescendants", CmCollectionUtils.toList(domain.getDisabledTargetDescendants()),
                "masterDetailAggregateAttrs", CmCollectionUtils.toList(domain.getMasterDetailAggregateAttrs()),
                "masterDetailDisabledCreateAttrs", CmCollectionUtils.toList(domain.getMasterDetailDisabledCreateAttrs()),
                "cascadeActionDirect", serializeEnum(domain.getMetadata().getCascadeActionDirect()),
                "cascadeActionInverse", serializeEnum(domain.getMetadata().getCascadeActionInverse()),
                "_cascadeActionDirect_actual", serializeEnum(getActualCascadeAction(domain, domain.getMetadata().getCascadeActionDirect(), RD_DIRECT)),
                "_cascadeActionInverse_actual", serializeEnum(getActualCascadeAction(domain, domain.getMetadata().getCascadeActionInverse(), RD_INVERSE)),
                "cascadeActionDirect_askConfirm", domain.getMetadata().getCascadeActionDirectAskConfirm(),
                "cascadeActionInverse_askConfirm", domain.getMetadata().getCascadeActionInverseAskConfirm(),
                "_can_create", domain.hasServicePermission(CP_UPDATE))
                .accept(m -> {
                    ecqlClassReferenceFilterStuff(m, 
                                                  Domain.DOMAIN_SOURCE_CLASS_TOKEN, domain.getSourceClass(),
                                    domain.getSourceClassReferenceFilter(), 
                                    domain, domain.getMetadata());
                    })
                .accept(m -> {
                    ecqlClassReferenceFilterStuff(m, 
                                                  Domain.DOMAIN_TARGET_CLASS_TOKEN, domain.getTargetClass(),
                                    domain.getTargetClassReferenceFilter(), 
                                    domain, domain.getMetadata());
                    });
    }

    /**
     * Update <code>classReferenceFilters</code> attribute with given source/destination class filter.
     * 
     * @param domainData
     * @param classeToken <code>sourceFilter</code> or <code>targetFilter</code>
     * @param classe
     * @param filter
     * @param domain
     * @param metadata 
     */
    private void ecqlClassReferenceFilterStuff(CmMapUtils.FluentMap<String, Object> domainData, 
                                                String classeToken, Classe classe,
                                                String filter, 
                                                Domain domain, AbstractMetadata metadata) {
        if (isEmpty(filter)) {
            return;
        }
        
        EcqlBindingInfo ecqlBindingInfo = EcqlUtils.getEcqlBindingInfoForExpr(getEcqlExpression(metadata, filter));
        final String ecqlId = EcqlUtils.buildDomainEcqlId(domain, EcqlUtils.buildUniqueClassToken(classeToken, classe));
        String key = JsonEcqlFilterHelper.fromModel(classeToken);
        
        Map<String, Object> curClassReferenceFilters;
        if (domainData.get("classReferenceFilters") == null) {
            curClassReferenceFilters = map();
        } else {
            // Already existing class filter
            curClassReferenceFilters = (Map<String, Object>) domainData.get("classReferenceFilters");
        }
        curClassReferenceFilters.putAll(EcqlFilterSerializationHelper.buildEcqlFilterStuff(
                            key, filter, 
                            key+"_ecqlFilter", ecqlId, 
                            ecqlBindingInfo));
        domainData.with("classReferenceFilters", curClassReferenceFilters);
    }

}
