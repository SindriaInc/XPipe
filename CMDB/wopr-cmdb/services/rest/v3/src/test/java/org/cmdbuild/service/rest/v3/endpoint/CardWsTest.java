/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.service.rest.v3.endpoint;

import static java.util.Arrays.asList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.classe.access.UserCardService;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.QueryBuilder;
import org.cmdbuild.dao.core.q3.ResultRow;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl;
import org.cmdbuild.data.filter.beans.CmdbSorterImpl;
import org.cmdbuild.dms.DmsService;
import static org.cmdbuild.email.Email.EMAIL_CLASS_NAME;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.helpers.CardsForDomainFetcher;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import static org.cmdbuild.service.rest.v3.endpoint.TestHelper_Model.mockBuildClasse;
import static org.cmdbuild.service.rest.v3.endpoint.TestHelper_Model.mockBuildCard;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 *
 * @author afelice
 */
public class CardWsTest {
    
    private static final EnumSet<CardWsSerializationHelperv3.ExtendedCardOptions> NONE_EXTENDED_CARD_FLAG = EnumSet.noneOf(CardWsSerializationHelperv3.ExtendedCardOptions.class);    
    private static final String A_KNOWN_CLASS_NAME = "aClass";
    private static final String A_KNOWN_ATTRIBUTE_NAME = "aAttribute";
    
    private final CardWs instance;
    
    private final UserClassService classService = mock(UserClassService.class);
    private final UserCardService cardService = mock(UserCardService.class);
    private final DaoService dao = mock(DaoService.class);
    private final CardWsSerializationHelperv3 helper = mock(CardWsSerializationHelperv3.class);
    private final DmsService dmsService = mock(DmsService.class);
    private final CardsForDomainFetcher cardsForDomainFetcher = mock(CardsForDomainFetcher.class);   
    
    private final AttributeWithoutOwner attribWOCode;
    private final AttributeWithoutOwner attribWODescription;
    private final Classe aSuperClasse;
    private final Card aSuperCard_1;
    private final Card aSuperCard_3;
    
    private final AttributeWithoutOwner attribWO_FirstLevel;
    private final Classe aDerivedClasse;
    private final Card aDerivedCard_2;
    private final Card aDerivedCard_4;
            
    public CardWsTest() {
        instance = new CardWs(classService, cardService, dao, helper, dmsService, cardsForDomainFetcher);
        
        attribWOCode = TestHelper_Model.mockBuildAttributeWithoutOwner("Code");
        attribWODescription = TestHelper_Model.mockBuildAttributeWithoutOwner("Description");
        aSuperClasse = mockBuildClasse("TestCards", 
                list(attribWOCode, attribWODescription));
        aSuperCard_1 = mockBuildCard(1L, aSuperClasse, 
                map(
                        attribWOCode.getName(), "superCard_1", 
                        attribWODescription.getName(), "super card 1"
                ));
        aSuperCard_3 = mockBuildCard(3L, aSuperClasse, 
                map(
                        attribWOCode.getName(), "superCard_3", 
                        attribWODescription.getName(), "super card 3"
                ));        
        
        attribWO_FirstLevel = TestHelper_Model.mockBuildAttributeWithoutOwner("User");        
        aDerivedClasse = mockBuildClasse("TestDerivedCards", list(aSuperClasse.getName()),
                asList(attribWO_FirstLevel));
        aDerivedCard_2 = mockBuildCard(2L, aDerivedClasse, 
                map(
                        attribWOCode.getName(), "derivedCard_2", 
                        attribWODescription.getName(), "derived card 2"
                ));
        aDerivedCard_4 = mockBuildCard(4L, aDerivedClasse, 
                map(
                        attribWOCode.getName(), "derivedCard_4", 
                        attribWODescription.getName(), "derived card 4"
                ));             
    }
    
    /**
     * Test of readOne method invoked services, with info only flag, of class
     * CardWs.
     */
    @Test
    public void testReadOne_infoOnly() {
        System.out.println("readOne_infoOnly");
        
        //arrange:
        FluentMap<String, Object> expCardSerialization = map("Code", "superCard_1");
        String classeId = aSuperClasse.getId().toString();
        final Card aCard = aSuperCard_1;
        long cardId = aCard.getId();
        when(cardService.getUserCardInfo(classeId, cardId)).thenReturn(aCard);
        when(helper.serializeCard(aCard)).thenReturn(expCardSerialization);
        
        //act:
        Object result = instance.readOne(aSuperClasse.getId().toString(), aCard.getId(), 
                false, false, false, true); // infoOnly is true
        
        //assert:
        checkResponse(result, expCardSerialization);        
        verify(cardService, times(1)).getUserCardInfo(eq(classeId), eq(cardId));
        verifyNoMoreInteractions(cardService);
        verify(helper, times(1)).serializeCard(aCard);
        verifyNoMoreInteractions(helper);
    }
     
    /**
     * Test of readOne method invoked services, default, of class
     * CardWs.
     */
    @Test
    public void testReadOne() {
        System.out.println("readOne");
        
        //arrange:
        FluentMap<String, Object> expCardSerialization = map("Code", "superCard_1");
        String classeId = aSuperClasse.getId().toString();
        final Card aCard = aSuperCard_1;
        long cardId = aCard.getId();
        when(cardService.getUserCard(classeId, cardId)).thenReturn(aCard);
        //when(helper.serializeCard(eq(aCard), eq(NONE_EXTENDED_CARD_FLAG))).thenReturn(expCardSerialization); 
        when(helper.serializeCard(eq(aCard))).thenReturn(expCardSerialization); // Ora passa per serializeCard(card, ExtendedCardOptions...) invece che per serializeCard(card, Set<ExtendedCardOptions>) 
        
        //act:
        Object result = instance.readOne(aSuperClasse.getId().toString(), aCard.getId(), 
                false, false, false, false); // no flags set
        
        //assert:
        checkResponse(result, expCardSerialization); 
        verify(cardService, times(1)).getUserCard(eq(classeId), eq(cardId));
        verifyNoMoreInteractions(cardService);
        //verify(helper, times(1)).serializeCard(eq(aCard), eq(NONE_EXTENDED_CARD_FLAG));
        verify(helper, times(1)).serializeCard(eq(aCard)); // Ora passa per serializeCard(card, ExtendedCardOptions...) invece che per serializeCard(card, Set<ExtendedCardOptions>)
        verifyNoMoreInteractions(helper);
    }     
     
    /**
     * Test of readOne method invoked services, with include model flag, of class
     * CardWs.
     */
    @Test
    public void testReadOne_includeModel() {
        System.out.println("readOne_includeModel");
        
        //arrange:
        FluentMap<String, Object> expCardSerialization = map("Code", "superCard_1");
        String classeId = aSuperClasse.getId().toString();
        final Card aCard = aSuperCard_1;
        long cardId = aCard.getId();
        when(cardService.getUserCard(classeId, cardId)).thenReturn(aCard);
        when(helper.serializeCard(eq(aCard), eq(EnumSet.of(CardWsSerializationHelperv3.ExtendedCardOptions.INCLUDE_MODEL)))).thenReturn(expCardSerialization);
        
        //act:
        Object result = instance.readOne(aSuperClasse.getId().toString(), aCard.getId(), 
                true, false, false, false); // include model flag set
        
        //assert:
        checkResponse(result, expCardSerialization);
        verify(cardService, times(1)).getUserCard(eq(classeId), eq(cardId));
        verifyNoMoreInteractions(cardService);
        verify(helper, times(1)).serializeCard(eq(aCard), eq(EnumSet.of(CardWsSerializationHelperv3.ExtendedCardOptions.INCLUDE_MODEL)));
        verifyNoMoreInteractions(helper);
    }  

    /**
     * Test of readOne method invoked services, with include widgets flag, of class
     * CardWs.
     */
    @Test
    public void testReadOne_includeWidgets() {
        System.out.println("readOne_includeWidgets");
        
        //arrange:
        FluentMap<String, Object> expCardSerialization = map("Code", "superCard_1");
        FluentMap<String, Object> expWidgetsSerialization = map("Widgets", "super widgets");
        String classeId = aSuperClasse.getId().toString();
        final Card aCard = aSuperCard_1;
        long cardId = aCard.getId();
        when(cardService.getUserCard(classeId, cardId)).thenReturn(aCard);
        //when(helper.serializeCard(eq(aCard), eq(NONE_EXTENDED_CARD_FLAG))).thenReturn(expCardSerialization);
        when(helper.serializeCard(eq(aCard))).thenReturn(expCardSerialization); // Ora passa per serializeCard(card, ExtendedCardOptions...) invece che per serializeCard(card, Set<ExtendedCardOptions>)
        when(helper.serializeWidgets(aCard)).thenReturn((FluentMap<String, Object> m) -> m.putAll(expWidgetsSerialization));
        
        //act:
        Object result = instance.readOne(aSuperClasse.getId().toString(), aCard.getId(), 
                false, true, false, false); // include widgets flags set
        
        //assert:
        checkResponse(result, expCardSerialization.with(expWidgetsSerialization)); 
        verify(cardService, times(1)).getUserCard(eq(classeId), eq(cardId));
        verifyNoMoreInteractions(cardService);
        //verify(helper, times(1)).serializeCard(eq(aCard), eq(NONE_EXTENDED_CARD_FLAG));
        verify(helper, times(1)).serializeCard(eq(aCard)); // Ora passa per serializeCard(card, ExtendedCardOptions...) invece che per serializeCard(card, Set<ExtendedCardOptions>)
        verify(helper, times(1)).serializeWidgets(aCard); 
        verifyNoMoreInteractions(helper);
    }         
    
    /**
     * Test of readOne method invoked services, with include stats flag, of class
     * CardWs.
     */
    @Test
    public void testReadOne_includeStats() {
        System.out.println("readOne_includeStats");
        
        //arrange:
        int expStats_AttachmentCount = 2;
        int expStats_EmailCount = 3;
        FluentMap<String, Object> expCardStatsSerialization = map(
                "_attachment_count", expStats_AttachmentCount,
                "_email_count", expStats_EmailCount);        
        FluentMap<String, Object> expCardSerialization = map("Code", "superCard_1");
        expCardSerialization.putAll(expCardStatsSerialization);

        String classeId = aSuperClasse.getId().toString();
        final Card aCard = aSuperCard_1;
        long cardId = aCard.getId();
        when(cardService.getUserCard(classeId, cardId)).thenReturn(aCard);       
        //when(helper.serializeCard(eq(aCard), eq(NONE_EXTENDED_CARD_FLAG))).thenReturn(expCardSerialization);
        when(helper.serializeCard(eq(aCard))).thenReturn(expCardSerialization); // Ora passa per serializeCard(card, ExtendedCardOptions...) invece che per serializeCard(card, Set<ExtendedCardOptions>)
        
        when(dmsService.getCardAttachmentCountSafe(aCard)).thenReturn(expStats_AttachmentCount);
        mockSelectCount(dao, EMAIL_CLASS_NAME, expStats_EmailCount);
        
        //act:
        Object result = instance.readOne(aSuperClasse.getId().toString(), aCard.getId(), 
                false, false, true, false); // infoStats is true
        
        //assert:
        checkResponse(result, expCardSerialization);        
        verify(cardService, times(1)).getUserCard(eq(classeId), eq(cardId));
        verifyNoMoreInteractions(cardService);
        //verify(helper, times(1)).serializeCard(eq(aCard), eq(NONE_EXTENDED_CARD_FLAG));
        verify(helper, times(1)).serializeCard(eq(aCard)); // Ora passa per serializeCard(card, ExtendedCardOptions...) invece che per serializeCard(card, Set<ExtendedCardOptions>)
        verifyNoMoreInteractions(helper);
    }    
    
    /**
     * Test of readMany method invoked services, with distinct attribute flag, of class
     * CardWs.
     * 
     * <p>Returns the distinct values for an attribute/couples of attributes. Used
     * to create dynamic multipart keys.
     */
    @Test
    public void testReadMany_distinctAttribute() {
        System.out.println("readMany_distinctAttribute");
        
        //arrange:
        String classeId = A_KNOWN_CLASS_NAME;
        String distinctAttribute = A_KNOWN_ATTRIBUTE_NAME;
        final FluentMap<String, Object> firstAttribValue = map("key", 1);        
        final FluentMap<String, Object> secondAttribValue = map("key", 2);        
        List<Map<String,Object>> expMapList = list(firstAttribValue, secondAttribValue);        
        mockSelectDistinctAttribute(dao, classeId, distinctAttribute, expMapList);
        Classe classe = mock(Classe.class);
        when(classe.getName()).thenReturn(classeId);
        when(classService.getUserClass(classeId)).thenReturn(classe);
        when(dao.getClasse(classeId)).thenReturn(classe);  
        // Couldn't use all in one, some problem with type erasure in FluentMap<StringObject>
        //when(helper.serializeAttributeValue(any(),eq(distinctAttribute), any())).thenReturn(returnsArgAt(2));
        when(helper.serializeAttributeValue(any(),eq(distinctAttribute), eq(firstAttribValue))).thenAnswer(setupDummyAnswer(firstAttribValue));
        when(helper.serializeAttributeValue(any(),eq(distinctAttribute), eq(secondAttribValue))).thenAnswer(setupDummyAnswer(secondAttribValue));       
        
        //act:
        Object result = instance.readMany(classeId, mockWsQueryOptions(false), null,
                "", false, A_KNOWN_ATTRIBUTE_NAME, "");
        
        //assert:
        verify(helper, times(1)).serializeAttributeValue(any(), eq(distinctAttribute), eq(firstAttribValue));
        verify(helper, times(1)).serializeAttributeValue(any(), eq(distinctAttribute), eq(secondAttribValue));
        verifyNoMoreInteractions(helper);
    }
    
    private void checkResponse(Object result, Map<String, Object> expCardSerialization) {
        //assert:
        assertEquals(true, ((Map)result).get("success"));
        assertEquals(expCardSerialization, ((Map)result).get("data"));
    }     

    private void mockSelectCount(DaoService dao, String className, long expCount) {
        QueryBuilder selectCountBuilder = mock(QueryBuilder.class);
        when(dao.selectCount()).thenReturn(selectCountBuilder);
        QueryBuilder fromBuilder = mock(QueryBuilder.class);
        when(selectCountBuilder.from(className)).thenReturn(fromBuilder);
        QueryBuilder whereBuilder = mock(QueryBuilder.class);
        when(fromBuilder.where(anyString(), any(), any(Object[].class))).thenReturn(whereBuilder);
        when(whereBuilder.getCount()).thenReturn(expCount);
    }
    
    private void mockSelectDistinctAttribute(DaoService dao, String className, String attributeName, List<Map<String,Object>> expMapList) {
        QueryBuilder selectDistinctBuilder = mock(QueryBuilder.class);
        when(dao.selectDistinct(attributeName)).thenReturn(selectDistinctBuilder);
        when(selectDistinctBuilder.accept(any())).thenReturn(selectDistinctBuilder);
        QueryBuilder fromBuilder = mock(QueryBuilder.class);
        when(selectDistinctBuilder.from(className)).thenReturn(fromBuilder);
        QueryBuilder whereBuilder = mock(QueryBuilder.class);
        when(fromBuilder.where(any(CmdbFilter.class))).thenReturn(whereBuilder);
        List<ResultRow> resultRows = expMapList.stream().map(expMap -> {
                    ResultRow resRow = mock(ResultRow.class);
                    when(resRow.asMap()).thenReturn(expMap);
                    return resRow;
            })
            .collect(toList());
        when(whereBuilder.run()).thenReturn(resultRows);
    }

    private WsQueryOptions mockWsQueryOptions(boolean onlyGridAttrs) {
        DaoQueryOptions queryOptions = mock(DaoQueryOptions.class);        
        when(queryOptions.getOnlyGridAttrs()).thenReturn(onlyGridAttrs);
        when(queryOptions.mapAttrNames(any())).thenReturn(queryOptions); // tqueryOptions
        when(queryOptions.expandFulltextFilter(any())).thenReturn(queryOptions); 
        when(queryOptions.getFilter()).thenReturn(CmdbFilterImpl.noopFilter());
        when(queryOptions.getSorter()).thenReturn(new CmdbSorterImpl()); // NOOP sorter
                
        WsQueryOptions wsQueryOptions = mock(WsQueryOptions.class);
        when(wsQueryOptions.getQuery()).thenReturn(queryOptions);
        
        return wsQueryOptions;
    }
    
    /**
     * Builds an <code>Answer<FluentMap<String,Object>> that returns given value
     * 
     * @param value
     * @return 
     */
    private Answer<FluentMap<String,Object>> setupDummyAnswer(FluentMap<String,Object> value) {
        return (InvocationOnMock invocation) -> value;
    }
}
