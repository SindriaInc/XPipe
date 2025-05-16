/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.dao.beans;

import java.util.Map;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.DomainImpl.DomainImplBuilder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author afelice
 */
public class DomainImplTest {
    
    private final static String FLOOR_FILTER = "from Floor where Id in (/(select \"Id\" from \"Floor\" where \"Building\" = 0{client:Building.Id})/)";
    
    /**
     * Test of withMetadata method, for domain filter by class attribute, of class DomainImpl.
     */
    @Test
    public void testWithMetadata_classAttributeFitler() {
        System.out.println("withMetadata_classAttributeFilter");
        
        //arrange:
        Map<String, String> aReferenceFilters = map(
                // EcqlId{source=CLASS_ATTRIBUTE, id=[Room, Floor]} -> Class attribute in Room with type Reference and pointing to Floor
                "Floor", "1kpo50n3y230ezume2pywttnt8b"  // contains Ecql encrypted Id
        );
        Classe floor = mockBuildClass("Floor");
        Classe room = mockBuildClass("Room");

        
        //act:
        Domain instance = new DomainImplBuilder()
                                        .withName("FloorRoom")
                                        .withId(1L)
                                        .withClass1(floor) // source
                                        .withClass2(room) // target
                                        .withMetadata(DomainMetadataImpl.builder()
                                                                .withReferenceFilters(aReferenceFilters)
                                                                .build())
                                        .build();
        
        //assert:
        assertEquals(aReferenceFilters, 
                    instance.getMetadata().getReferenceFilters());
    }
    
    /**
     * Test of withMetadata method, for domain filter on class, of class DomainImpl.
     */
    @Test
    public void testWithMetadata_domainClassReferenceFilters() {
        System.out.println("withMetadata_domainClassReferenceFilters");
        
        //arrange:
        Map<String, String> aReferenceFilters = map(
                Domain.DOMAIN_SOURCE_CLASS_TOKEN, FLOOR_FILTER // contains raw ecql
        );
        Classe floor = mockBuildClass("Floor");
        Classe room = mockBuildClass("Room");

        
        //act:
        Domain instance = new DomainImplBuilder()
                                        .withName("FloorRoom")
                                        .withId(1L)
                                        .withClass1(floor) // source
                                        .withClass2(room) // target
                                        .withMetadata(DomainMetadataImpl.builder()
                                                                .withClassReferenceFilters(aReferenceFilters)
                                                                .build())
                                        .build();
        
        //assert:
        assertEquals(aReferenceFilters, 
                    instance.getMetadata().getClassReferenceFilters());
    }    

    private static ClasseImpl mockBuildClass(String classeName) {
        return ClasseImpl.builder()
                .withName(classeName)
                .build();
    }
    
}
