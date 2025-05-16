/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import java.util.Map;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author afelice
 */
public class JsonEcqlFilterHelperTest {
    
    private static final String DOMAIN_SOURCE_TOKEN = Domain.DOMAIN_SOURCE_CLASS_TOKEN;
    private static final String A_SOURCE_VALUE = "<SourceValue>";
    private static final String DOMAIN_DESTINATION_CLASS_NAME = Domain.DOMAIN_TARGET_CLASS_TOKEN;
    private static final String A_DESTINATION_VALUE = "<DestinationValue>";
    
    
    private final JsonEcqlFilterHelper instance = new JsonEcqlFilterHelper();

    /**
     * Test of toModel method, of class EcqlFilterHelper.
     */
    @Test
    public void testToModel() {
        System.out.println("toModel");
        
        //arrange:        
        Map<String, String> classReferenceFilters = map(JsonEcqlFilterHelper.SOURCE_CLASS_LABEL, A_SOURCE_VALUE,
                JsonEcqlFilterHelper.DESTINATION_CLASS_LABEL, A_DESTINATION_VALUE
        );
        Map<String, String> expResult = map(DOMAIN_SOURCE_TOKEN, A_SOURCE_VALUE,
                DOMAIN_DESTINATION_CLASS_NAME, A_DESTINATION_VALUE
        );
        
        //act: 
        Map<String, String> result = instance.toModel(classReferenceFilters);
        
        assertEquals(expResult, result);
    }

    /**
     * Test of fromModel method, of class EcqlFilterHelper.
     */
    @Test
    public void testFromModel() {
        System.out.println("fromModel");
        
        //arrange:        
        Map<String, String> classReferenceFilters = map(DOMAIN_SOURCE_TOKEN, A_SOURCE_VALUE,
                DOMAIN_DESTINATION_CLASS_NAME, A_DESTINATION_VALUE
        );
        Map<String, String> expResult = map(JsonEcqlFilterHelper.SOURCE_CLASS_LABEL, A_SOURCE_VALUE,
                JsonEcqlFilterHelper.DESTINATION_CLASS_LABEL, A_DESTINATION_VALUE
        );
        
        //act: 
        Map<String, String> result = instance.fromModel(classReferenceFilters);
        
        assertEquals(expResult, result);
    }
    
}
