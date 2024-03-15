/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.ecql.utils;

import org.cmdbuild.ecql.EcqlId;
import org.cmdbuild.ecql.EcqlSource;
import static com.google.common.collect.Iterables.getOnlyElement;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.DomainImpl;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EcqlUtilsTest {

    private static final String A_DOMAIN_NAME = "FloorRoom";
    private static final String A_DOMAIN_CLASS = "Room";
    private static final String A_SOURCE_DOMAIN_CLASS = "Floor";    
    
    // EcqlId{source=DOMAIN, id=[FloorRoom, sourceFilter::Room]}
    private final static String EXP_GENERATED_ROOM_DOMAIN_FILTER_ECQL_ID = "13igdixxyr1r5jdfojkgdmr3otqlod69crwkwts6zogh5xgvf9kedob";    
    
    @Test
    public void testResolveEcqlXa1() {
        String ecql1 = "from Hardware where Id in (/(select \"IdObj2\" from \"Map_AssetMgtCI\" where \"IdObj1\"={xa:process} and \"Status\"='A')/";
        String ecql2 = EcqlUtils.resolveEcqlXa(ecql1, map("process", 12345678l));
        assertEquals("from Hardware where Id in (/(select \"IdObj2\" from \"Map_AssetMgtCI\" where \"IdObj1\"=12345678 and \"Status\"='A')/", ecql2);
    }

    @Test
    public void testResolveEcqlXa2() {
        String ecql1 = "from Hardware where Id in (/(select \"IdObj2\" from \"Map_AssetMgtCI\" where \"IdObj1\"='{xa:process}' and \"Status\"='A')/";
        String ecql2 = EcqlUtils.resolveEcqlXa(ecql1, map("process", "test"));
        assertEquals("from Hardware where Id in (/(select \"IdObj2\" from \"Map_AssetMgtCI\" where \"IdObj1\"='test' and \"Status\"='A')/", ecql2);
    }

    @Test
    public void testBuildEcqlId() {
        String encodedId = EcqlUtils.buildEcqlId(EcqlSource.CLASS_ATTRIBUTE, 1234);
        assertEquals("7d5fwl7t", encodedId);
        EcqlId ecqlId = EcqlUtils.parseEcqlId(encodedId);
        assertEquals(EcqlSource.CLASS_ATTRIBUTE, ecqlId.getSource());
        assertEquals(String.valueOf(1234), getOnlyElement(ecqlId.getId()));
    }

    @Test
    public void testParseEcqlId() {
        String encodedId = "ekfufsvssidcxn";
        EcqlId ecqlId = EcqlUtils.parseEcqlId(encodedId);
        assertEquals(EcqlSource.CLASS_ATTRIBUTE, ecqlId.getSource());
        assertEquals(String.valueOf(1234), getOnlyElement(ecqlId.getId()));
    }
    
    @Test
    public void testBuildDomainEcqlId() {
        System.out.println("buildDomainEcqlId");
        
        //arrange:        
        Classe room = createClasse(A_DOMAIN_CLASS);
        Classe floor = createClasse(A_SOURCE_DOMAIN_CLASS);        
        Domain floorRoomDomain = DomainImpl.builder()
                .withName(A_DOMAIN_NAME)
                .withClass1(floor) // source
                .withClass2(room) // target
                .build();            
        
        //act: 
        String encodedId = EcqlUtils.buildDomainEcqlId(floorRoomDomain, EcqlUtils.buildUniqueClassToken(Domain.DOMAIN_SOURCE_CLASS_TOKEN, room));
        
        //assert:
        assertEquals(EXP_GENERATED_ROOM_DOMAIN_FILTER_ECQL_ID, encodedId);
    }  
    
    @Test
    public void testParseDomainEcqlId() {
        System.out.println("parseDomainEcqlId");
        
        //act:
        EcqlId ecqlId = EcqlUtils.parseEcqlId(EXP_GENERATED_ROOM_DOMAIN_FILTER_ECQL_ID);
        
        //assert:
        assertEquals(EcqlSource.DOMAIN, ecqlId.getSource());
        assertEquals(asList(A_DOMAIN_NAME, format("%s%sRoom", Domain.DOMAIN_SOURCE_CLASS_TOKEN, EcqlUtils.UNIQUE_CLASS_TOKEN_DELIMITER)), ecqlId.getId());
    }     
    
    @Test
    public void testFetchClassToken() {
        System.out.println("fetchClassToken");
        
        //act:
        String result = EcqlUtils.fetchClassToken("sourceFilter::Room1");
        
        //assert:
        assertEquals("sourceFilter", result);
    }         

    @Test
    public void testBuildEcqlId2() {
        String encodedId = EcqlUtils.buildEcqlId(EcqlSource.CLASS_ATTRIBUTE, 1234, "Something");
        assertEquals("33wdpdl29k27lu2bo2wvk073u7jy5bt17", encodedId);
        EcqlId ecqlId = EcqlUtils.parseEcqlId(encodedId);
        assertEquals(EcqlSource.CLASS_ATTRIBUTE, ecqlId.getSource());
        assertEquals(String.valueOf(1234), ecqlId.getId().get(0));
        assertEquals("Something", ecqlId.getId().get(1));
    }

    private Classe createClasse(String classeName) {
        return ClasseImpl.builder()
                .withName(classeName)
                .withId(Long.valueOf(UniqueTestIdUtils.tuid())) // Without this the attribute related filter is categorized as EMBEDDED instead of CLASS_ATTRIBUTE
                .build();
    }       
    
} // end EcqlUtilsTest class

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

}
