/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.service.rest.v3.serializationhelpers;

import static java.util.Arrays.asList;
import java.util.Map;
import org.cmdbuild.dao.beans.DomainMetadataImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.DomainImpl;
import org.cmdbuild.service.rest.common.serializationhelpers.JsonEcqlFilterHelper;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.hamcrest.CoreMatchers.instanceOf;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.Mockito.mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author afelice
 */
public class DomainSerializationHelperTest {
 
    public static final String REFERENCE_FILTERS_JSON_ATTR = "_referenceFilters";
    public static final String CLASS_REFERENCE_FILTERS_JSON_ATTR = "classReferenceFilters";
    public static final String SOURCE_FILTER_JSON_ATTR = JsonEcqlFilterHelper.SOURCE_CLASS_LABEL;
    public static final String DESTINATION_FILTER_JSON_ATTR = JsonEcqlFilterHelper.DESTINATION_CLASS_LABEL;
    
    private final static String CLASS_DOMAIN_FLOOR_FILTER = "from Floor where Id in (/(select \"Id\" from \"Floor\" where \"Building\" = 0{client:Building.Id})/)"; 
    private final static String CLASS_DOMAIN_FLOOR_FILTER_ECQL_ID = "7sz8g6pdj4ch3drjij5ggxsq8jh659oiiufopfshppx6245ej5r3gvy3"; // EcqlId{source=DOMAIN, id=[FloorRoom, sourceFilter::Floor]}
    
    private final static String CLASS_ATTRIBUTE_ROOM_FILTER_ECQL_ID = "b7ajnokc26tji9iiljhe5yret7ej";
    
    private final static String CLASS_DOMAIN_ROOM_FILTER = "from Room where Id in (/(select \"Id\" from \"Room\" where \"Building\" = 0{client:Building.Id})/)"; 
    private final static String CLASS_DOMAIN_ROOM_FILTER_ECQL_ID = "13igdixxyr1r5jdfojkgdmr3p1osyrszf84fzfw0lb2o9ww4g1gg8u3"; // EcqlId{source=DOMAIN, id=[FloorRoom, targetFilter::Room]}
    
    private final ObjectTranslationService translationService = mock(ObjectTranslationService.class);
    
    private static DomainSerializationHelper instance;
    
    @Before
    public void init() {
        UniqueTestIdUtils.prepareTuid(); 
        
        instance = new DomainSerializationHelper(translationService);
    } 
    
    /**
     * Test of serializeDetailedDomain method, for source class domain filter, of class DomainSerializationHelper.
     */
    @Test
    public void testSerializeDetailedDomain_SourceClassDomainFilter() {
        System.out.println("serializeDetailedDomain_SourceClassDomainFilter");
        
        //arrange:
        Map<String, String> aClassReferenceFilters = map(
                Domain.DOMAIN_SOURCE_CLASS_TOKEN, CLASS_DOMAIN_FLOOR_FILTER // contains raw ecql
        );
        Classe floor = mockBuildClass("Floor");
        Classe room = mockBuildClass("Room");
        Domain domain = new DomainImpl.DomainImplBuilder()
                                        .withName("FloorRoom")
                                        .withId(1L)
                                        .withClass1(floor) // source
                                        .withClass2(room) // target
                                        .withMetadata(DomainMetadataImpl.builder()
                                                                .withClassReferenceFilters(aClassReferenceFilters)
                                                                .build())
                                        .build();  

        //act:
        Map<String, Object> result = instance.serializeDetailedDomain(domain);
        
        //assert:
        assertTrue(result.containsKey(CLASS_REFERENCE_FILTERS_JSON_ATTR));
        Map<String, Object> resultClassReferenceFilters = (Map<String, Object>) result.get(CLASS_REFERENCE_FILTERS_JSON_ATTR);
        assertTrue(resultClassReferenceFilters.containsKey(SOURCE_FILTER_JSON_ATTR));
        assertEquals(CLASS_DOMAIN_FLOOR_FILTER, resultClassReferenceFilters.get(SOURCE_FILTER_JSON_ATTR));
        assertTrue(resultClassReferenceFilters.containsKey(SOURCE_FILTER_JSON_ATTR+"_ecqlFilter"));
        assertThat(resultClassReferenceFilters.get(SOURCE_FILTER_JSON_ATTR+"_ecqlFilter"), instanceOf(Map.class));
        
        Map<String, Object> resultEcqlFilter = (Map<String, Object>) resultClassReferenceFilters.get(SOURCE_FILTER_JSON_ATTR+"_ecqlFilter");
        assertEquals(CLASS_DOMAIN_FLOOR_FILTER_ECQL_ID, resultEcqlFilter.get("id"));        
        Map<String, Object> resultEcqlFilterBindings = (Map<String, Object>) resultEcqlFilter.get("bindings");
        assertEquals(asList("Building.Id"), resultEcqlFilterBindings.get("client"));       
    }
    
    /**
     * Test of serializeDetailedDomain method, for destination class domain filter, of class DomainSerializationHelper.
     */
    @Test
    public void testSerializeDetailedDomain_DestinationClassDomainFilter() {
        System.out.println("serializeDetailedDomain_DestinationClassDomainFilter");
        
        //arrange:
        Map<String, String> aClassReferenceFilters = map(
                Domain.DOMAIN_TARGET_CLASS_TOKEN, CLASS_DOMAIN_ROOM_FILTER // contains raw ecql
        );
        Classe floor = mockBuildClass("Floor");
        Classe room = mockBuildClass("Room");
        Domain domain = new DomainImpl.DomainImplBuilder()
                                        .withName("FloorRoom")
                                        .withId(1L)
                                        .withClass1(floor) // source
                                        .withClass2(room) // target
                                        .withMetadata(DomainMetadataImpl.builder()
                                                                .withClassReferenceFilters(aClassReferenceFilters)
                                                                .build())
                                        .build();  

        //act:
        Map<String, Object> result = instance.serializeDetailedDomain(domain);
        
        //assert:
        assertTrue(result.containsKey(CLASS_REFERENCE_FILTERS_JSON_ATTR));
        Map<String, Object> resultClassReferenceFilters = (Map<String, Object>) result.get(CLASS_REFERENCE_FILTERS_JSON_ATTR);        
        assertTrue(resultClassReferenceFilters.containsKey(DESTINATION_FILTER_JSON_ATTR));
        assertEquals(CLASS_DOMAIN_ROOM_FILTER, resultClassReferenceFilters.get(DESTINATION_FILTER_JSON_ATTR));
        assertTrue(resultClassReferenceFilters.containsKey(DESTINATION_FILTER_JSON_ATTR+"_ecqlFilter"));
        assertThat(resultClassReferenceFilters.get(DESTINATION_FILTER_JSON_ATTR+"_ecqlFilter"), instanceOf(Map.class));
        
        Map<String, Object> resultEcqlFilter = (Map<String, Object>) resultClassReferenceFilters.get(DESTINATION_FILTER_JSON_ATTR+"_ecqlFilter");
        assertEquals(CLASS_DOMAIN_ROOM_FILTER_ECQL_ID, resultEcqlFilter.get("id"));        
        Map<String, Object> resultEcqlFilterBindings = (Map<String, Object>) resultEcqlFilter.get("bindings");
        assertEquals(asList("Building.Id"), resultEcqlFilterBindings.get("client"));       
    }    
    
    /**
     * Test of serializeDetailedDomain method, for both (source and target) class domain filter, of class DomainSerializationHelper.
     */
    @Test
    public void testSerializeDetailedDomain_BothClassDomainFilter() {
        System.out.println("serializeDetailedDomain_BothClassDomainFilter");
        
        //arrange:
        Map<String, String> aClassReferenceFilters = map(
                Domain.DOMAIN_SOURCE_CLASS_TOKEN, CLASS_DOMAIN_FLOOR_FILTER, // contains raw ecql,
                Domain.DOMAIN_TARGET_CLASS_TOKEN, CLASS_DOMAIN_FLOOR_FILTER // contains raw ecql
        );
        Classe floor = mockBuildClass("Floor");
        Classe room = mockBuildClass("Room");
        Domain domain = new DomainImpl.DomainImplBuilder()
                                        .withName("FloorRoom")
                                        .withId(1L)
                                        .withClass1(floor) // source
                                        .withClass2(room) // target
                                        .withMetadata(DomainMetadataImpl.builder()
                                                                .withClassReferenceFilters(aClassReferenceFilters)
                                                                .build())
                                        .build();  

        //act:
        Map<String, Object> result = instance.serializeDetailedDomain(domain);
        
        //assert:
        Map<String, Object> resultClassReferenceFilters = (Map<String, Object>) result.get(CLASS_REFERENCE_FILTERS_JSON_ATTR);
        assertTrue(resultClassReferenceFilters.containsKey(SOURCE_FILTER_JSON_ATTR));
        assertTrue(resultClassReferenceFilters.containsKey(DESTINATION_FILTER_JSON_ATTR));
    }    
    
    private static ClasseImpl mockBuildClass(String classeName) {
        return ClasseImpl.builder()
                .withName(classeName)
                .build();
    }    
    
} // end DomainSerializationHelperTest class

/**
 * Duplicated here from module cmdbuild-test-framework to not import all that module only to use this class.
 * 
 * @author afelice
 */
class UniqueTestIdUtils {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static int i = 0;

    public static void prepareTuid() {
        i++;
    }

    /** 
     * @return test unique id (to prefix names and stuff)
     */
    public static String tuid() {
        return Integer.toString(i, 32);
    }

    /**  
     * @param id
     * @return param + test unique id
     */
    public static String tuid(String id) {
        return id + tuid();//StringUtils.capitalizeFirstLetter(tuid());
    }

} // end UniqueTestIdUtils class