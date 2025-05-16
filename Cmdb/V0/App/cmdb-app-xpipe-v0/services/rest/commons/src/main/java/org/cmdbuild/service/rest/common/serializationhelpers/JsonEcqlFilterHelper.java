/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import java.util.Map;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Plain attributes to be used in WS to set/get filters for source/destination classes. 
 * 
 * <br><br><b>Note</b>:
 * Used only in rest request/response json; in model the {@link Domain#DOMAIN_SOURCE_CLASS_TOKEN}/{@link Domain#DOMAIN_TARGET_CLASS_TOKEN}.
 * 
 * @author afelice
 */
public class JsonEcqlFilterHelper {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    public static final String SOURCE_CLASS_LABEL = "sourceFilter";
    public static final String DESTINATION_CLASS_LABEL = "destinationFilter";    
    private static final Map<String, String> mapToModel = map(SOURCE_CLASS_LABEL, Domain.DOMAIN_SOURCE_CLASS_TOKEN,
                                                              DESTINATION_CLASS_LABEL, Domain.DOMAIN_TARGET_CLASS_TOKEN
                                                          );
    private static final Map<String, String> mapToLabel= map(Domain.DOMAIN_SOURCE_CLASS_TOKEN,SOURCE_CLASS_LABEL, 
                                                        Domain.DOMAIN_TARGET_CLASS_TOKEN, DESTINATION_CLASS_LABEL
                                                    );
    
    public static Map<String, String> toModel(Map<String, String> referenceFilters) {
        return map(referenceFilters).mapKeys(l -> mapToModel.get(l));
    }    
    
    public static Map<String, String> fromModel(Map<String, String> referenceFilters) {
        return map(referenceFilters).mapKeys(c -> mapToLabel.get(c));
    }
    
    public static String fromModel(String referenceClassToken) {
        return mapToLabel.get(referenceClassToken);
    }
}
