/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Triple;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.classe.access.UserDomainService;
import org.cmdbuild.dao.beans.DomainMetadataImpl;
import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import static org.cmdbuild.dao.entrytype.AttributeMetadata.FILTER;
import static org.cmdbuild.dao.entrytype.AttributeMetadata.USE_DOMAIN_FILTER;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.DomainImpl;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.ecql.EcqlRepository;
import org.cmdbuild.ecql.inner.EcqlExpressionImpl;
import org.cmdbuild.ecql.utils.EcqlUtils;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.isNullOrEmpty;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author afelice
 */
public class AttributeTypeConversionServiceTest {

    public static final String REFERENCE_FILTERS_JSON_ATTR = "_referenceFilters";

    private static final String FLOOR_FILTER = "from Floor where Id in (/(select \"Id\" from \"Floor\" where \"Building\" = 0{client:Building.Id})/)";
    private static final String CLASS_ATTRIBUTE_FLOOR_FILTER_ECQL_ID = "b7ajnokc26tji9iiljhe5yret7ej";

    public static final String SOURCE_FILTER_JSON_ATTR = JsonEcqlFilterHelper.SOURCE_CLASS_LABEL;
    public static final String DESTINATION_FILTER_JSON_ATTR = JsonEcqlFilterHelper.DESTINATION_CLASS_LABEL;

    private final DaoService dao = mock(DaoService.class);
    private final UserDomainService userDomainService = mock(UserDomainService.class);
    private final EcqlRepository ecqlRepository = mock(EcqlRepository.class);

    private static AttributeTypeConversionService instance;

    @Before
    public void init() {
        UniqueTestIdUtils.prepareTuid();

        instance = new AttributeTypeConversionService(
                dao,
                mock(ObjectTranslationService.class),
                mock(UserClassService.class),
                userDomainService,
                mock(CalendarWsSerializationHelper.class),
                mock(LookupService.class),
                mock(DmsService.class),
                ecqlRepository
        );
    }

    /**
     * Test of serializeAttributeType method, class attribute filter, of class
     * AttributeTypeConversionService.
     */
    @Test
    public void testSerializeAttributeType_ClassAttributeFilter() {
        System.out.println("serializeAttributeType_ClassAttributeFilter");

        //arrange:
        final int mockFloorRoomUniqueId = 1;
        // EcqlId{source=CLASS_ATTRIBUTE, id=[Room1, Floor]}
        final String expFloor_filterEcqlId = "b7ajnokc26tji9iiljhe5yret7ej";

        // Floor1, Room1, Domain:FloorRoom1
        Triple<Classe, Classe, Domain> inverseDomainData = mockBuildDomainInverse("Floor", "Room",
                "FloorRoom", map(Domain.DOMAIN_SOURCE_CLASS_TOKEN, expFloor_filterEcqlId),
                mockFloorRoomUniqueId);
        Classe floor = inverseDomainData.getLeft();
        Classe room = inverseDomainData.getMiddle();
        Domain floorRoomDomain = inverseDomainData.getRight();
        // Emulate ecql filter calculus
        Domain withFilterEcqlIdDomain = DomainImpl.copyOf(floorRoomDomain)
                .withMetadata(
                        DomainMetadataImpl.builder()
                                .withReferenceFilters(map(EcqlUtils.buildUniqueClassToken(Domain.DOMAIN_SOURCE_CLASS_TOKEN, room), expFloor_filterEcqlId))
                                .build()
                )
                .build();
        when(userDomainService.getDomain(floorRoomDomain.getName())).thenReturn(withFilterEcqlIdDomain);
        when(ecqlRepository.getById(expFloor_filterEcqlId))
                .thenReturn(new EcqlExpressionImpl(FLOOR_FILTER));
        Attribute floorAttribute = AttributeImpl.builder()
                .withOwner(room)
                .withName("Floor")
                .withType(new ReferenceAttributeType(floorRoomDomain, RelationDirection.RD_INVERSE))
                .withMeta(FILTER, FLOOR_FILTER)
                .build();

        //act:
        Map<String, Object> result = instance.serializeAttributeType(floorAttribute);

        //assert:
        assertTrue(result.containsKey("filter"));
        assertEquals(FLOOR_FILTER, result.get("filter"));
        assertTrue(result.containsKey("ecqlFilter"));
        assertThat(result.get("ecqlFilter"), instanceOf(Map.class));
        Map<String, Object> resultEcqlFilter = (Map<String, Object>) result.get("ecqlFilter");
        assertEquals(CLASS_ATTRIBUTE_FLOOR_FILTER_ECQL_ID, resultEcqlFilter.get("id"));
        Map<String, Object> resultEcqlFilterBindings = (Map<String, Object>) resultEcqlFilter.get("bindings");
        assertEquals(asList("Building.Id"), resultEcqlFilterBindings.get("client"));

        assertTrue(result.containsKey(REFERENCE_FILTERS_JSON_ATTR));
        final Map<String, Object> resultCalculatedReferenceFilters = (Map<String, Object>) result.get(REFERENCE_FILTERS_JSON_ATTR);
        assertEquals(2, resultCalculatedReferenceFilters.size());

        assertEquals(expFloor_filterEcqlId, resultCalculatedReferenceFilters.get(SOURCE_FILTER_JSON_ATTR));
        Map<String, Object> resultCalculatedEcqlFilter = (Map<String, Object>) resultCalculatedReferenceFilters.get(SOURCE_FILTER_JSON_ATTR + "_ecqlFilter");
        assertEquals(CLASS_ATTRIBUTE_FLOOR_FILTER_ECQL_ID, resultCalculatedEcqlFilter.get("id"));
        Map<String, Object> resultCalculatedEcqlFilterBindings = (Map<String, Object>) resultCalculatedEcqlFilter.get("bindings");
        assertEquals(asList("Building.Id"), resultCalculatedEcqlFilterBindings.get("client"));
    }

    /**
     * Test of serializeAttributeType method, class attribute filter, with use
     * domain filter flag, of class AttributeTypeConversionService.
     */
    @Test
    public void testSerializeAttributeType_ClassAttributeFilter_UseDomainFilter() {
        System.out.println("serializeAttributeType_ClassAttributeFilter_UseDomainFilter");

        //arrange:
        final int mockFloorRoomUniqueId = 1;
        // EcqlId{source=CLASS_ATTRIBUTE, id=[Room1, Floor]}
        final String expFloor_filterEcqlId = "b7ajnokc26tji9iiljhe5yret7ej";

        // Floor1, Room1, Domain:FloorRoom1
        Triple<Classe, Classe, Domain> inverseDomainData = mockBuildDomainInverse("Floor", "Room",
                "FloorRoom", map(Domain.DOMAIN_SOURCE_CLASS_TOKEN, expFloor_filterEcqlId),
                mockFloorRoomUniqueId);
        Classe floor = inverseDomainData.getLeft();
        Classe room = inverseDomainData.getMiddle();
        Domain floorRoomDomain = inverseDomainData.getRight();
        // Emulate ecql filter calculus
        Domain withFilterEcqlIdDomain = DomainImpl.copyOf(floorRoomDomain)
                .withMetadata(
                        DomainMetadataImpl.builder()
                                .withReferenceFilters(map(EcqlUtils.buildUniqueClassToken(Domain.DOMAIN_SOURCE_CLASS_TOKEN, room), expFloor_filterEcqlId))
                                .build()
                )
                .build();
        when(userDomainService.getDomain(floorRoomDomain.getName())).thenReturn(withFilterEcqlIdDomain);
        when(ecqlRepository.getById(expFloor_filterEcqlId))
                .thenReturn(new EcqlExpressionImpl(FLOOR_FILTER));
        Attribute floorAttribute = AttributeImpl.builder()
                .withOwner(room)
                .withName("Floor")
                .withType(new ReferenceAttributeType(floorRoomDomain, RelationDirection.RD_INVERSE))
                .withMeta(FILTER, FLOOR_FILTER,
                        USE_DOMAIN_FILTER, "true")
                .build();

        //act:
        Map<String, Object> result = instance.serializeAttributeType(floorAttribute);

        //assert:
        assertTrue(result.containsKey("filter"));
        assertTrue((boolean) result.get("useDomainFilter"));
        assertEquals(FLOOR_FILTER, result.get("filter"));
        assertTrue(result.containsKey("ecqlFilter"));
        assertThat(result.get("ecqlFilter"), instanceOf(Map.class));
        Map<String, Object> resultEcqlFilter = (Map<String, Object>) result.get("ecqlFilter");
        assertEquals(CLASS_ATTRIBUTE_FLOOR_FILTER_ECQL_ID, resultEcqlFilter.get("id"));
        Map<String, Object> resultEcqlFilterBindings = (Map<String, Object>) resultEcqlFilter.get("bindings");
        assertEquals(asList("Building.Id"), resultEcqlFilterBindings.get("client"));

        assertTrue(result.containsKey(REFERENCE_FILTERS_JSON_ATTR));
        final Map<String, Object> resultCalculatedReferenceFilters = (Map<String, Object>) result.get(REFERENCE_FILTERS_JSON_ATTR);
        assertEquals(2, resultCalculatedReferenceFilters.size());

        assertEquals(expFloor_filterEcqlId, resultCalculatedReferenceFilters.get(SOURCE_FILTER_JSON_ATTR));
        Map<String, Object> resultCalculatedEcqlFilter = (Map<String, Object>) resultCalculatedReferenceFilters.get(SOURCE_FILTER_JSON_ATTR + "_ecqlFilter");
        assertEquals(CLASS_ATTRIBUTE_FLOOR_FILTER_ECQL_ID, resultCalculatedEcqlFilter.get("id"));
        Map<String, Object> resultCalculatedEcqlFilterBindings = (Map<String, Object>) resultCalculatedEcqlFilter.get("bindings");
        assertEquals(asList("Building.Id"), resultCalculatedEcqlFilterBindings.get("client"));
    }

    /**
     * Test of serializeAttributeType method, class attribute filter, with use
     * domain filter flag but not yet inserted that filter, of class
     * AttributeTypeConversionService.
     */
    @Test
    public void testSerializeAttributeType_ClassAttributeFilter_UseDomainFilter_MissingDomainFilter() {
        System.out.println("serializeAttributeType_ClassAttributeFilter_UseDomainFilter_MissingDomainFilter");

        //arrange:
        final int mockFloorRoomUniqueId = 1;
        // EcqlId{source=CLASS_ATTRIBUTE, id=[Room1, Floor]}
        final String expFloor_filterEcqlId = "b7ajnokc26tji9iiljhe5yret7ej";

        // Floor1, Room1, Domain:FloorRoom1
        Triple<Classe, Classe, Domain> inverseDomainData = mockBuildDomainInverse("Floor", "Room",
                "FloorRoom",
                mockFloorRoomUniqueId);
        Classe floor = inverseDomainData.getLeft();
        Classe room = inverseDomainData.getMiddle();
        Domain floorRoomDomain = inverseDomainData.getRight();
        // Emulate (empty) ecql filter calculus
        Domain withFilterEcqlIdDomain = DomainImpl.copyOf(floorRoomDomain).build();
        when(userDomainService.getDomain(floorRoomDomain.getName())).thenReturn(withFilterEcqlIdDomain);
        when(ecqlRepository.getById(expFloor_filterEcqlId))
                .thenReturn(new EcqlExpressionImpl(FLOOR_FILTER));
        Attribute floorAttribute = AttributeImpl.builder()
                .withOwner(room)
                .withName("Floor")
                .withType(new ReferenceAttributeType(floorRoomDomain, RelationDirection.RD_INVERSE))
                .withMeta(FILTER, FLOOR_FILTER,
                        USE_DOMAIN_FILTER, "true")
                .build();

        //act:
        Map<String, Object> result = instance.serializeAttributeType(floorAttribute);

        //assert:
        assertTrue(result.containsKey("filter"));
        assertTrue((boolean) result.get("useDomainFilter"));
        assertEquals(FLOOR_FILTER, result.get("filter"));
        assertTrue(result.containsKey("ecqlFilter"));
        assertThat(result.get("ecqlFilter"), instanceOf(Map.class));
        Map<String, Object> resultEcqlFilter = (Map<String, Object>) result.get("ecqlFilter");
        assertEquals(CLASS_ATTRIBUTE_FLOOR_FILTER_ECQL_ID, resultEcqlFilter.get("id"));
        Map<String, Object> resultEcqlFilterBindings = (Map<String, Object>) resultEcqlFilter.get("bindings");
        assertEquals(asList("Building.Id"), resultEcqlFilterBindings.get("client"));

        assertFalse(result.containsKey(REFERENCE_FILTERS_JSON_ATTR));
    }

    /**
     * Test of serializeAttributeType method, several class attribute filters
     * with use domain filter flag on one of leafs, of class
     * AttributeTypeConversionService.
     */
    @Test
    public void testSerializeAttributeType_Inheritance_UseDomainFilterOnLeaf() {
        System.out.println("serializeAttributeType_Inheritance_UseDomainFilterOnLeaf");

        //arrange:
        final int mockUniqueId = 5;
        final String domainFilter = "from Employee where Abc = 0{client:Building.Id})/)";
        final String hardwareFilter = "from Employee where Def = 0{client:Building.Id})/)";
        final String virtualFilter = "from Employee where Ghi = 0{client:Building.Id})/)";

        // EcqlId{source=DOMAIN, id=[CIAssignee5, sourceFilter]}
        final String expDomain_filterEcqlId = "2tdpno0vmngrgpv716a0fe0k318whs3x3e4gx5nzaj9l5mj";
        // EcqlId{source=CLASS_ATTRIBUTE, id=[Hardware5, Assignee]}
        final String expHardware_filterEcqlId = "b7ajnokc26tji9iiljhe5yret7ej";
        // EcqlId{source=CLASS_ATTRIBUTE, id=[Virtual5, Assignee]}
        final String expVirtual_filterEcqlId = "2ky7b96sanpnz84uctwnlwrrve89gqal9zwhdqme5gloaizryjk0yd18t9seypq3";

        // EmployeeI5, CI5, Domain:CIAssignee5
        Triple<Classe, Classe, Domain> inverseDomainData = mockBuildDomainInverse("Employee", "CI",
                "CIAssignee", map(Domain.DOMAIN_SOURCE_CLASS_TOKEN, expDomain_filterEcqlId),
                mockUniqueId);
        Classe employee = inverseDomainData.getMiddle();
        Classe ci = inverseDomainData.getLeft();
        Classe hardware = createClasse("Hardware", list("CI"));
        Classe desktop = createClasse("Desktop", list("CI", "Hardware"));
        Classe virtual = createClasse("Virtual", list("CI", "Hardware"));
        Domain ciAssegneeDomain = inverseDomainData.getRight();
        // Emulate ecql filter calculus
        Domain withFilterEcqlIdDomain = DomainImpl.copyOf(ciAssegneeDomain)
                .withMetadata(
                        DomainMetadataImpl.builder()
                                .withReferenceFilters(map(EcqlUtils.buildUniqueClassToken(Domain.DOMAIN_SOURCE_CLASS_TOKEN, desktop), expDomain_filterEcqlId))
                                .build()
                )
                .build();
        when(userDomainService.getDomain(ciAssegneeDomain.getName())).thenReturn(withFilterEcqlIdDomain);
        when(ecqlRepository.getById(expDomain_filterEcqlId))
                .thenReturn(new EcqlExpressionImpl(domainFilter));
        when(ecqlRepository.getById(expHardware_filterEcqlId))
                .thenReturn(new EcqlExpressionImpl(hardwareFilter));
        when(ecqlRepository.getById(expVirtual_filterEcqlId))
                .thenReturn(new EcqlExpressionImpl(virtualFilter));
        Attribute hardwareAttribute = AttributeImpl.builder()
                .withOwner(hardware)
                .withName("Assighee")
                .withType(new ReferenceAttributeType(ciAssegneeDomain, RelationDirection.RD_INVERSE))
                .withMeta(FILTER, hardwareFilter)
                .build();
        Attribute desktopAttribute = AttributeImpl.builder()
                .withOwner(desktop)
                .withName("Assighee")
                .withType(new ReferenceAttributeType(ciAssegneeDomain, RelationDirection.RD_INVERSE))
                .withMeta(USE_DOMAIN_FILTER, "true")
                .build();
        Attribute virtualAttribute = AttributeImpl.builder()
                .withOwner(virtual)
                .withName("Assighee")
                .withType(new ReferenceAttributeType(ciAssegneeDomain, RelationDirection.RD_INVERSE))
                .withMeta(FILTER, virtualFilter)
                .build();

        //act:
        Map<String, Object> result = instance.serializeAttributeType(desktopAttribute);

        //assert:
        assertTrue((boolean) result.get("useDomainFilter"));

        assertTrue(result.containsKey(REFERENCE_FILTERS_JSON_ATTR));
        final Map<String, Object> resultCalculatedReferenceFilters = (Map<String, Object>) result.get(REFERENCE_FILTERS_JSON_ATTR);
        assertEquals(2, resultCalculatedReferenceFilters.size());

        assertEquals(expDomain_filterEcqlId, resultCalculatedReferenceFilters.get(SOURCE_FILTER_JSON_ATTR));
        Map<String, Object> resultCalculatedEcqlFilter = (Map<String, Object>) resultCalculatedReferenceFilters.get(SOURCE_FILTER_JSON_ATTR + "_ecqlFilter");
        assertEquals(expDomain_filterEcqlId, resultCalculatedEcqlFilter.get("id"));
        Map<String, Object> resultCalculatedEcqlFilterBindings = (Map<String, Object>) resultCalculatedEcqlFilter.get("bindings");
        assertEquals(asList("Building.Id"), resultCalculatedEcqlFilterBindings.get("client"));
    }

    /**
     * Test of serializeAttributeType method, several class attribute filters
     * with use domain filter flag on one of leafs, of class
     * AttributeTypeConversionService.
     */
    @Test
    public void testSerializeAttributeType_Inheritance_ClassAttributeOnLeaf() {
        System.out.println("serializeAttributeType_Inheritance_ClassAttributeOnLeaf");

        //arrange:
        final int mockUniqueId = 5;
        final String domainFilter = "from Employee where Abc = 0{client:Building.Id})/)";
        final String hardwareFilter = "from Employee where Def = 0{client:Building.Id})/)";
        final String virtualFilter = "from Employee where Ghi = 0{client:Building.Id})/)";

        // EcqlId{source=DOMAIN, id=[CIAssignee5, sourceFilter]}
        final String expDomain_filterEcqlId = "2tdpno0vmngrgpv716a0fe0k318whs3x3e4gx5nzaj9l5mj";
        // EcqlId{source=CLASS_ATTRIBUTE, id=[Hardware5, Assignee]}
        final String expHardware_filterEcqlId = "b7ajnokc26tji9iiljhe5yret7ej";
        // EcqlId{source=CLASS_ATTRIBUTE, id=[Virtual5, Assignee]}
        final String expVirtual_filterEcqlId = "2ky7b96sanpnz84uctwnlwrrve89gqal9zwhdqme5gloaizryjk0yd18t9seypq3";

        // EmployeeI5, CI5, Domain:CIAssignee5
        Triple<Classe, Classe, Domain> inverseDomainData = mockBuildDomainInverse("Employee", "CI",
                "CIAssignee", map(Domain.DOMAIN_SOURCE_CLASS_TOKEN, expDomain_filterEcqlId),
                mockUniqueId);
        Classe employee = inverseDomainData.getMiddle();
        Classe ci = inverseDomainData.getLeft();
        Classe hardware = createClasse("Hardware", list("CI"));
        Classe desktop = createClasse("Desktop", list("CI", "Hardware"));
        Classe virtual = createClasse("Virtual", list("CI", "Hardware"));
        Domain ciAssegneeDomain = inverseDomainData.getRight();
        // Emulate ecql filter calculus
        Domain withFilterEcqlIdDomain = DomainImpl.copyOf(ciAssegneeDomain)
                .withMetadata(
                        DomainMetadataImpl.builder()
                                .withReferenceFilters(map(EcqlUtils.buildUniqueClassToken(Domain.DOMAIN_SOURCE_CLASS_TOKEN, virtual), expVirtual_filterEcqlId))
                                .build()
                )
                .build();
        when(userDomainService.getDomain(ciAssegneeDomain.getName())).thenReturn(withFilterEcqlIdDomain);
        when(ecqlRepository.getById(expDomain_filterEcqlId))
                .thenReturn(new EcqlExpressionImpl(domainFilter));
        when(ecqlRepository.getById(expHardware_filterEcqlId))
                .thenReturn(new EcqlExpressionImpl(hardwareFilter));
        when(ecqlRepository.getById(expVirtual_filterEcqlId))
                .thenReturn(new EcqlExpressionImpl(virtualFilter));
        Attribute hardwareAttribute = AttributeImpl.builder()
                .withOwner(hardware)
                .withName("Assighee")
                .withType(new ReferenceAttributeType(ciAssegneeDomain, RelationDirection.RD_INVERSE))
                .withMeta(FILTER, hardwareFilter)
                .build();
        Attribute desktopAttribute = AttributeImpl.builder()
                .withOwner(desktop)
                .withName("Assighee")
                .withType(new ReferenceAttributeType(ciAssegneeDomain, RelationDirection.RD_INVERSE))
                .withMeta(USE_DOMAIN_FILTER, "true")
                .build();
        Attribute virtualAttribute = AttributeImpl.builder()
                .withOwner(virtual)
                .withName("Assighee")
                .withType(new ReferenceAttributeType(ciAssegneeDomain, RelationDirection.RD_INVERSE))
                .withMeta(FILTER, virtualFilter)
                .build();

        //act:
        Map<String, Object> result = instance.serializeAttributeType(virtualAttribute);

        //assert:
        assertFalse((boolean) result.get("useDomainFilter"));

        assertTrue(result.containsKey(REFERENCE_FILTERS_JSON_ATTR));
        final Map<String, Object> resultCalculatedReferenceFilters = (Map<String, Object>) result.get(REFERENCE_FILTERS_JSON_ATTR);
        assertEquals(2, resultCalculatedReferenceFilters.size());

        assertEquals(expVirtual_filterEcqlId, resultCalculatedReferenceFilters.get(SOURCE_FILTER_JSON_ATTR));
        Map<String, Object> resultCalculatedEcqlFilter = (Map<String, Object>) resultCalculatedReferenceFilters.get(SOURCE_FILTER_JSON_ATTR + "_ecqlFilter");
        assertEquals(expVirtual_filterEcqlId, resultCalculatedEcqlFilter.get("id"));
        Map<String, Object> resultCalculatedEcqlFilterBindings = (Map<String, Object>) resultCalculatedEcqlFilter.get("bindings");
        assertEquals(asList("Building.Id"), resultCalculatedEcqlFilterBindings.get("client"));
    }

    /**
     * Test of serializeAttributeType method, no reference filters, of class
     * AttributeTypeConversionService.
     */
    @Test
    public void testSerializeAttributeType_NoReferenceFilters() {
        System.out.println("serializeAttributeType_NoReferenceFilters");

        //arrange:
        final int mockFloorRoomUniqueId = 1;
        // EcqlId{source=CLASS_ATTRIBUTE, id=[Room1, Floor]}
        final String expFloor_filterEcqlId = "b7ajnokc26tji9iiljhe5yret7ej";

        // Floor1, Room1, Domain:FloorRoom1
        Triple<Classe, Classe, Domain> inverseDomainData = mockBuildDomainInverse("Floor", "Room",
                "FloorRoom", map(Domain.DOMAIN_SOURCE_CLASS_TOKEN, expFloor_filterEcqlId),
                mockFloorRoomUniqueId);
        Classe floor = inverseDomainData.getLeft();
        Classe room = inverseDomainData.getMiddle();
        Domain floorRoomDomain = inverseDomainData.getRight();
        when(userDomainService.getDomain(floorRoomDomain.getName())).thenReturn(floorRoomDomain);
        Attribute floorAttribute = AttributeImpl.builder()
                .withOwner(room)
                .withName("Floor")
                .withType(new ReferenceAttributeType(floorRoomDomain, RelationDirection.RD_INVERSE))
                .build();

        //act:
        Map<String, Object> result = instance.serializeAttributeType(floorAttribute);

        //assert:
        assertFalse(result.containsKey("filter"));
        assertFalse(result.containsKey(REFERENCE_FILTERS_JSON_ATTR));
    }

    /**
     * Generates a domain source - target.
     *
     * Handles uniqueness of instances of {@link Class} and {@link Domain} to be
     * used with real {@link DaoService} using the given
     * <code>mockUniqueId</code>.
     *
     * @param sourceClassName
     * @param targetClassName
     * @param domainName
     * @param mockUniqueId
     * @return sourceClasse, targetClasse, Domain
     */
    private Triple<Classe, Classe, Domain> mockBuildDomainInverse(String sourceClassName, String targetClassName,
            String domainName,
            int mockUniqueId) {
        return mockBuildDomainInverse(sourceClassName, targetClassName, domainName, emptyMap(), mockUniqueId);
    }

    /**
     * Generates a domain source - target.
     *
     * Handles uniqueness of instances of {@link Class} and {@link Domain} to be
     * used with real {@link DaoService} using the given
     * <code>mockUniqueId</code>.
     *
     * @param sourceClassName
     * @param targetClassName
     * @param domainName
     * @param classReferenceFilters
     * @param mockUniqueId
     * @return sourceClasse, targetClasse, Domain
     */
    private Triple<Classe, Classe, Domain> mockBuildDomainInverse(String sourceClassName, String targetClassName,
            String domainName, Map<String, String> classReferenceFilters,
            int mockUniqueId) {
        // See Relation1IT.testRelationAttributes1()
        Classe sourceClass = createClasse(sourceClassName + mockUniqueId),
                targetClass = createClasse(targetClassName + mockUniqueId);
        // Create domain FloorRoom1
        // "_id": "FloorRoom1",
        // "name": "FloorRoom1",
        // "description": "Floor room",
        // "source": "Floor1",
        // "sources": [
        //     "Floor1"
        // ],
        // "destination": "Room1",
        // "destinations": [
        //     "Room1"
        // ],
        DomainImpl.DomainImplBuilder domainBuilder = DomainImpl.builder()
                .withName(domainName + mockUniqueId)
                .withClass1(sourceClass) // Floor
                .withClass2(targetClass) // Room
                ;

        if (!isNullOrEmpty(classReferenceFilters)) {
            domainBuilder.withMetadata(b -> b.withReferenceFilters(classReferenceFilters));
        }

        Domain domain = domainBuilder.build();

        return Triple.of(sourceClass, targetClass, domain);
    }

    private Classe createClasse(String classeName) {
        return createClasse(classeName, emptyList());
    }

    private Classe createClasse(String classeName, List<String> ancestors) {
        ClasseImpl.ClasseBuilder builder = ClasseImpl.builder()
                .withName(classeName)
                .withId(Long.valueOf(UniqueTestIdUtils.tuid())); // Without this the attribute related filter is categorized as EMBEDDED instead of CLASS_ATTRIBUTE

        if (!ancestors.isEmpty()) {
            builder.withAncestors(ancestors);
        }

        return builder.build();
    }

} // end AttributeTypeConversionServiceTest class

/**
 * Duplicated here from module cmdbuild-test-framework to not import all that
 * module only to use this class.
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
