/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.ecql.inner;

import javax.inject.Provider;
import org.cmdbuild.dao.beans.DomainMetadataImpl;
import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.driver.repository.DomainRepository;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import static org.cmdbuild.dao.entrytype.AttributeMetadata.FILTER;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.DomainImpl;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dashboard.DashboardRepository;
import org.cmdbuild.easytemplate.store.EasytemplateRepository;
import org.cmdbuild.ecql.EcqlExpression;
import org.cmdbuild.ecql.EcqlRepository;
import org.cmdbuild.ecql.utils.EcqlUtils;
import org.cmdbuild.navtree.NavTreeService;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.sysparam.SysparamRepository;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.widget.WidgetService;
import org.cmdbuild.workflow.WorkflowService;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author afelice
 */
public class EcqlRepositoryImplTest {
    
    private final static String FLOOR_FILTER = "from Floor where Id in (/(select \"Id\" from \"Floor\" where \"Building\" = 0{client:Building.Id})/)";   
    private final static String DOMAIN_CLASS_FILTER_ECQL_ID = "13igdixxyr1r5jdfojkgdmr3otqlod69crwkwts6zogh5xgvf9kedob";
    private static final String DOMAIN_SOURCE_CLASS_NAME = "Floor";
    private static final String DOMAIN_NAME = "FloorRoom";
    
    private final DaoService dao = mock(DaoService.class);
    private final ClasseRepository classeRepository = mock(ClasseRepository.class);
    private final DomainRepository domainRepository = mock(DomainRepository.class);
    
    private EcqlRepository instance;
    
    @Before
    public void setUp() {
        //sessionService.createAndSet(LoginDataImpl.buildNoPasswordRequired(SYSTEM_USER));
        
        instance = new EcqlRepositoryImpl(classeRepository, domainRepository,
                mockProvider(ReportService.class), mockProvider(EasytemplateRepository.class), 
                mockProvider(SysparamRepository.class), mockProvider(NavTreeService.class), 
                mockProvider(DashboardRepository.class), mockProvider(WorkflowService.class), mockProvider(WidgetService.class));
    } 
      
    @Test
    public void testGetById_ClassAttributeFilter() {
        System.out.println("getById_ClassAttributeFilter");
        
        //arrange:
        Classe room = mockClasse("Room");
        Classe floor = mockBuildClasse("Floor");
        final String floorRoom_ClassAttributefilterEcqlId = "1kpo50n3y230ezume2pywttnt8b";

        // Create domain FloorRoom2
        Domain floorRoomDomain =  DomainImpl.builder()
                .withName(DOMAIN_NAME)
                .withId(1L)
                .withClass1(floor) // source
                .withClass2(room) // target
                .build();
                // Create Floor reference
        Attribute floorAttribute = AttributeImpl.builder()
                .withOwner(room)
                .withName("Floor")
                .withType(new ReferenceAttributeType(floorRoomDomain, RelationDirection.RD_INVERSE))
                .withMeta(FILTER, FLOOR_FILTER)
                .build();           
        //when(room.getAllAttributesAsMap()).thenReturn(map(floorAttribute.getName(), floorAttribute));
        when(room.getAttribute(eq(floorAttribute.getName()))).thenReturn( floorAttribute);
        
        when(classeRepository.getClasse(eq(room.getName()))).thenReturn(room); 
        when(domainRepository.getDomain(eq(floorRoomDomain.getName()))).thenReturn(floorRoomDomain); 
        
        //act:
        EcqlExpression ecql = instance.getById(floorRoom_ClassAttributefilterEcqlId);
        
        //assert:
        assertEquals(FLOOR_FILTER, ecql.getEcql());        
    }        
    
    /**
     * Test of getById method, of class EcqlRepositoryImpl.
     */
    @Test
    public void testGetById_DomainClassFilter() {
        System.out.println("getById_DomainClassFilter");
        
        //arrange:
        Classe room = mockBuildClasse("Room"),
                floor = mockBuildClasse(DOMAIN_SOURCE_CLASS_NAME);
        final String floorRoom_filterEcqlId = DOMAIN_CLASS_FILTER_ECQL_ID;

        // Create domain FloorRoom2
        Domain floorRoomDomain = DomainImpl.builder()
                .withName(DOMAIN_NAME)
                .withId(1L)
                .withClass1(floor) // source
                .withClass2(room) // target
                .withMetadata(DomainMetadataImpl.builder()
                        .withClassReferenceFilters(map(Domain.DOMAIN_SOURCE_CLASS_TOKEN, FLOOR_FILTER))
                        .build())
                .build();

        when(domainRepository.getDomain(eq(floorRoomDomain.getName()))).thenReturn(floorRoomDomain); 
        
        //act:
        EcqlExpression ecql = instance.getById(floorRoom_filterEcqlId);
        
        //assert:
        assertEquals(FLOOR_FILTER, ecql.getEcql());        
    }    

    private static Classe mockClasse(String classeName) {
        Classe mock = mock(Classe.class);
        when(mock.getName()).thenReturn(classeName);
        
        return mock;
    }
    
    private static ClasseImpl mockBuildClasse(String classeName) {
        return mockClasseBuilder(classeName)
                .build();
    }

    private static ClasseImpl.ClasseBuilder mockClasseBuilder(String classeName) {
        return ClasseImpl.builder()
                .withName(classeName);
    }
        
    private <T> Provider<T> mockProvider(Class<T> anyToStub) {
        T stub = (T) mock(anyToStub);
    
        return directProvider(stub);
    }
    
    private <T> Provider<T> directProvider(T injected) {
        return () -> injected;        
    }     
    
}
