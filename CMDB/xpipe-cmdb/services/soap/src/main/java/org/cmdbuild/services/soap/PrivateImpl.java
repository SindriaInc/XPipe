package org.cmdbuild.services.soap;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.transform;
import static java.lang.Math.toIntExact;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.Collection;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.toList;

import javax.activation.DataHandler;
import javax.jws.WebService;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.cmdbuild.classe.access.UserCardService;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.services.soap.serializer.AttributeSchemaSerializer;
import org.cmdbuild.services.soap.structure.ActivitySchema;
import org.cmdbuild.services.soap.structure.AttributeSchema;
import org.cmdbuild.services.soap.structure.ClassSchema;
import org.cmdbuild.services.soap.structure.FunctionSchema;
import org.cmdbuild.services.soap.structure.MenuSchema;
import org.cmdbuild.services.soap.structure.WorkflowWidgetSubmission;
import org.cmdbuild.services.soap.types.Attachment;
import org.cmdbuild.services.soap.types.Attribute;
import org.cmdbuild.services.soap.types.CQLQuery;
import org.cmdbuild.services.soap.types.Card;
import org.cmdbuild.services.soap.types.CardExt;
import org.cmdbuild.services.soap.types.CardList;
import org.cmdbuild.services.soap.types.CardListExt;
import org.cmdbuild.services.soap.types.Lookup;
import org.cmdbuild.services.soap.types.Order;
import org.cmdbuild.services.soap.types.Query;
import org.cmdbuild.services.soap.types.Reference;
import org.cmdbuild.services.soap.types.Relation;
import org.cmdbuild.services.soap.types.RelationExt;
import org.cmdbuild.services.soap.types.ReportParams;
import org.cmdbuild.services.soap.types.WSEvent;
import org.cmdbuild.services.soap.types.Workflow;
import org.cmdbuild.dao.utils.WsAttributeValueConvertingVisitor;
import org.springframework.stereotype.Component;
import org.cmdbuild.services.soap.types.UserGroup;
import org.cmdbuild.services.soap.types.UserInfo;
import org.cmdbuild.services.soap.types.UserType;
import org.cmdbuild.utils.crypto.CmLegacyPasswordUtils;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.data.filter.AttributeFilterConditionOperator;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.SorterElementDirection;
import org.cmdbuild.data.filter.beans.AttributeFilterConditionImpl;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl;
import org.cmdbuild.data.filter.beans.CmdbSorterImpl;
import org.cmdbuild.data.filter.beans.SorterElementImpl;
import static org.cmdbuild.services.soap.types.Card.HACK_VALUE_SERIALIZER;
import static org.cmdbuild.services.soap.types.Card.LEGACY_VALUE_SERIALIZER;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.dao.function.StoredFunctionService;

@Component("privateImpl")
@WebService(endpointInterface = "org.cmdbuild.services.soap.Private", targetNamespace = "http://soap.services.cmdbuild.org")
public class PrivateImpl extends AbstractWebservice implements Private {

    @Autowired
    private StoredFunctionService functionCallService;
    @Autowired
    private DaoService dao;
    @Autowired
    private UserCardService cardService;

    @Override
    public Card getCard(String className, Long cardId, Attribute[] attributeList) {
        return getCard(className, cardId, attributeList, false);
    }

    private CardExt getCard(String className, Long cardId, Attribute[] attributeList, boolean enableLongDateFormat) {
        return dataAccessLogicHelper().getCardExt(className, cardId, attributeList, enableLongDateFormat);
    }

    @Override
    public CardExt getCardWithLongDateFormat(String className, Long cardId, Attribute[] attributeList) {
        return getCard(className, cardId, attributeList, true);
    }

    @Override
    public CardList getCardList(String className, Attribute[] attributeList, Query queryType, Order[] orderType, Long limit, Long offset, String fullTextQuery, CQLQuery cqlQuery) {
        logger.info("get card list for class = {} cql query = {}", className, Optional.ofNullable(cqlQuery).map(CQLQuery::getCqlQuery).orElse(null));
        return getCards(className, attributeList, queryType, orderType, limit, offset, fullTextQuery, cqlQuery, false);
    }

    private CardList getCards(String className, Attribute[] attributeList, Query queryType, Order[] orderType, Long limit, Long offset, String fullTextQuery, CQLQuery cqlQuery, boolean enableLongDateFormat) {

        CmdbFilter filter = CmdbFilterImpl.builder().accept(f -> {
            if (cqlQuery != null && isNotBlank(cqlQuery.getCqlQuery())) {
                f.withCqlFilter(cqlQuery.getCqlQuery());
            }
            if (isNotBlank(fullTextQuery)) {
                f.withFulltextFilter(fullTextQuery);
            }
            if (queryType != null) {
                //TODO check queryType.getFilterOperator (???)
                f.withAttributeFilter(AttributeFilterConditionImpl.builder()
                        .withKey(queryType.getFilter().getName())
                        .withOperator(soapFilterOperatorToCmdbOperator(queryType.getFilter().getOperator()))
                        .withValues(queryType.getFilter().getValue())
                        .build().toAttributeFilter());
            }
        }).build();

        CmdbSorter order = orderType == null || orderType.length == 0
                ? CmdbSorterImpl.noopSorter()
                : new CmdbSorterImpl(transform(list(orderType), (o) -> new SorterElementImpl(o.getColumnName(), parseEnum(o.getType(), SorterElementDirection.class))));

        PagedElements<org.cmdbuild.dao.beans.Card> cards = cardService.getUserCards(className, DaoQueryOptionsImpl.builder().withFilter(filter).withSorter(order).withPaging(offset, limit).build());
        return new CardList(cards.map((c) -> new Card(c, enableLongDateFormat ? HACK_VALUE_SERIALIZER : LEGACY_VALUE_SERIALIZER)).elements(), toIntExact(cards.totalSize()));
    }

    private static AttributeFilterConditionOperator soapFilterOperatorToCmdbOperator(String operator) {
        return Optional.ofNullable((AttributeFilterConditionOperator) map("EQUALS", AttributeFilterConditionOperator.EQUAL).get(operator))//TODO check other values to translate
                .orElseGet(() -> parseEnum(operator, AttributeFilterConditionOperator.class));
    }

    @Override
    public CardList getCardListWithLongDateFormat(String className, Attribute[] attributeList, Query queryType, Order[] orderType, Long limit, Long offset, String fullTextQuery, CQLQuery cqlQuery) {
        return getCards(className, attributeList, queryType, orderType, limit, offset, fullTextQuery, cqlQuery, true);
    }

    @Override
    public CardListExt getCardListExt(String className, Attribute[] attributeList, Query queryType, Order[] orderType, Long limit, Long offset, String fullTextQuery, CQLQuery cqlQuery) {
        return dataAccessLogicHelper().getCardListExt(className, attributeList, queryType, orderType, limit, offset, fullTextQuery, cqlQuery);
    }

    @Override
    public CardList getCardHistory(String className, long cardId, Long limit, Long offset) {
        return dataAccessLogicHelper().getCardHistory(className, cardId, limit, offset);
    }

    @Override
    public long createCard(Card card) {
        return dataAccessLogicHelper().createCard(card);
    }

    @Override
    public boolean updateCard(Card card) {
        return dataAccessLogicHelper().updateCard(card);
    }

    @Override
    public boolean deleteCard(String className, long cardId) {
        dao.delete(className, cardId);//TODO check permissions
        return true;
    }

    @Override
    public long createLookup(Lookup lookup) {
        return lookupLogicHelper().createLookup(lookup);
    }

    @Override
    public boolean deleteLookup(long lookupId) {
        return lookupLogicHelper().disableLookup(lookupId);
    }

    @Override
    public boolean updateLookup(Lookup lookup) {
        return lookupLogicHelper().updateLookup(lookup);
    }

    @Override
    public Lookup getLookupById(long id) {
        return lookupLogicHelper().getLookupById(id);
    }

    @Override
    public Lookup[] getLookupList(String type, String value, boolean parentList) {
        return lookupLogicHelper().getLookupListByDescription(type, value, parentList);
    }

    @Override
    public Lookup[] getLookupListByCode(String type, String code, boolean parentList) {
        return lookupLogicHelper().getLookupListByCode(type, code, parentList);
    }

    @Override
    public boolean createRelation(Relation relation) {
        return dataAccessLogicHelper().createRelation(relation);
    }

    @Override
    public boolean createRelationWithAttributes(Relation relation, List<Attribute> attributes) {
        return dataAccessLogicHelper().createRelationWithAttributes(relation, attributes);
    }

    @Override
    public boolean deleteRelation(Relation relation) {
        dao.deleteRelation(relation.getDomainName(), relation.getCard1Id(), relation.getCard2Id()); //TODO apply permissions
        return true;
    }

    @Override
    public List<Relation> getRelationList(String domain, String className, long cardId) {
        return dataAccessLogicHelper().getRelations(className, domain, cardId);
    }

    @Override
    public List<RelationExt> getRelationListExt(String domain, String className, long cardId) {
        return dataAccessLogicHelper().getRelationsExt(className, domain, cardId);
    }

    @Override
    public List<Attribute> getRelationAttributes(Relation relation) {
        return dataAccessLogicHelper().getRelationAttributes(relation);
    }

    @Override
    public void updateRelationAttributes(Relation relation, Collection<Attribute> attributes) {
        dataAccessLogicHelper().setRelationAttributes(relation, attributes);
    }

    @Override
    public Relation[] getRelationHistory(Relation relation) {
        return dataAccessLogicHelper().getRelationHistory(relation);
    }

    @Override
    public Attachment[] getAttachmentList(String className, long cardId) {
        return dmsLogicHelper().getAttachmentList(className, cardId);
    }

    @Override
    public boolean uploadAttachment(String className, long objectId, DataHandler file, String filename, String category, String description) {
        return dmsLogicHelper().uploadAttachment(className, objectId, file, filename, category,
                description);
    }

    @Override
    public DataHandler downloadAttachment(String className, long objectId, String filename) {
        return dmsLogicHelper().download(className, objectId, filename);
    }

    @Override
    public boolean deleteAttachment(String className, long cardId, String filename) {
        return dmsLogicHelper().delete(className, cardId, filename);
    }

    @Override
    public boolean updateAttachmentDescription(String className, long cardId, String filename, String description) {
        return dmsLogicHelper().updateDescription(className, cardId, filename, description);
    }

    @Override
    public Workflow updateWorkflow(Card card, boolean completeTask, WorkflowWidgetSubmission[] widgets) {
        return workflowLogicHelper().updateProcess(card, widgets, completeTask);
    }

    @Override
    public String getProcessHelp(String classname, Long cardid) {
        return workflowLogicHelper().getInstructions(classname, cardid);
    }

    @Override
    public AttributeSchema[] getAttributeList(String className) {
        return dataAccessLogicHelper().getAttributeList(className);
    }

    @Override
    public ActivitySchema getActivityObjects(String className, Long cardid) {
        return workflowLogicHelper().getActivitySchema(className, cardid);
    }

    @Override
    public Reference[] getReference(String className, Query query, Order[] orderType, Long limit, Long offset, String fullTextQuery, CQLQuery cqlQuery) {
        return dataAccessLogicHelper().getReference(className, query, orderType, limit, offset, fullTextQuery,
                cqlQuery);
    }

    @Override
    public MenuSchema getCardMenuSchema() {
        return dataAccessLogicHelper().getVisibleClassesTree();
    }

    @Override
    public MenuSchema getActivityMenuSchema() {
        return dataAccessLogicHelper().getVisibleProcessesTree();
    }

    @Override
    public MenuSchema getMenuSchema() {
        return dataAccessLogicHelper().getMenuSchemaForPreferredGroup();
    }

    @Override
    public org.cmdbuild.services.soap.types.Report[] getReportList(String type, long limit, long offset) {
        return dataAccessLogicHelper().getReportsByType(type, limit, offset);
    }

    @Override
    public AttributeSchema[] getReportParameters(long id, String extension) {
        return dataAccessLogicHelper().getReportParameters(id, extension);
    }

    @Override
    public DataHandler getReport(long id, String extension, ReportParams[] params) {
        return dataAccessLogicHelper().getReport(id, extension, params);
    }

    @Override
    public DataHandler getBuiltInReport(String reportId, String extension, ReportParams[] params) {
        return dataAccessLogicHelper().getReport(reportId, extension, params);
    }

    @Override
    @Deprecated
    public String sync(String xml) {
        throw unsupported("this method is not supported any more");
    }

    @Override
    public UserInfo getUserInfo() {
        org.cmdbuild.auth.user.UserInfo userInfo = authenticationLogicHelper().getUserInfo();
        UserInfo bean = new UserInfo();
        bean.setUserType(userInfo.getUserType() == null ? null : UserType.valueOf(userInfo.getUserType().name()));
        bean.setUsername(userInfo.getUsername());
        bean.setGroups(userInfo.getGroups() == null ? emptySet() : new LinkedHashSet<>(userInfo.getGroups().stream()
                .map((group) -> {
                    UserGroup newGroup = new UserGroup();
                    newGroup.setDescription(group.getDescription());
                    newGroup.setName(group.getName());
                    return newGroup;
                }).collect(toList())));
        return bean;
    }

    @Override
    public ClassSchema getClassSchema(String className) {
        return dataAccessLogicHelper().getClassSchema(className, true);
    }

    @Override
    public ClassSchema getClassSchemaByName(String name, boolean includeAttributes) {
        return dataAccessLogicHelper().getClassSchema(name, includeAttributes);
    }

    @Override
    public ClassSchema getClassSchemaById(long id, boolean includeAttributes) {
        return dataAccessLogicHelper().getClassSchema(id, includeAttributes);
    }

    @Override
    public Attribute[] callFunction(String functionName, Attribute[] params) {
        logger.info("calling function = {}", functionName);

        Map<String, Object> in = params == null ? emptyMap() : asList(params).stream().collect(CmMapUtils.toMap(Attribute::getName, Attribute::getValue));

        logger.debug("calling function = {} with params = {}:", functionName, in);

        Map<String, Object> out = functionCallService.callFunction(functionName, in);

        Attribute[] output;
        if (out.isEmpty()) {
            output = new Attribute[0];
        } else {
            Attribute[] outParams = convertFunctionOutput(dao.getFunctionByName(functionName), out);//TODO function access control
            logger.info(format("output parameters for function '%s':", functionName));
            if (outParams != null) {
                for (Attribute attribute : outParams) {
                    logger.info(format("- %s",
                            ToStringBuilder.reflectionToString(attribute, ToStringStyle.SHORT_PREFIX_STYLE)));
                }
            }
            output = outParams;
        }
        return output;
    }

    private Attribute[] convertFunctionOutput(StoredFunction function, Map<String, Object> out) {
        List<org.cmdbuild.dao.entrytype.Attribute> outputParams = function.getOutputParameters();
        Attribute[] output = new Attribute[outputParams.size()];
        int i = 0;
        for (org.cmdbuild.dao.entrytype.Attribute p : outputParams) {
            Attribute a = new Attribute();
            a.setName(p.getName());
            a.setValue(nativeValueToWsString(p.getType(), out.get(p.getName())));
            output[i] = a;
            ++i;
        }
        return output;
    }

    private String nativeValueToWsString(CardAttributeType<?> type, Object value) {
//		return (value == null) ? EMPTY : new AbstractAttributeValueVisitor(type, value, translationFacade, lookupSerializer()) {
        return (value == null) ? EMPTY : new WsAttributeValueConvertingVisitor(type, value).convertValue().toString();
    }

    @Override
    public void notify(WSEvent wsEvent) {
        throw new UnsupportedOperationException("legacy shark helper, not supported any more");
//        logger.info("event received");
//        wsEvent.accept(new WSEvent.Visitor() {
//
//            @Override
//            public void visit(WSProcessStartEvent wsEvent) {
//                logger.info(format("event for process start: %d / %s / %s", wsEvent.getSessionId(), wsEvent.getProcessDefinitionId(), wsEvent.getProcessInstanceId()));
//                SharkEvent event = SharkEvent.newProcessStartEvent(wsEvent.getProcessDefinitionId(), wsEvent.getProcessInstanceId());
//                workflowEventManager().pushEvent(wsEvent.getSessionId(), event);
//            }
//
//            @Override
//            public void visit(WSProcessUpdateEvent wsEvent) {
//                logger.info(format("event for process update: %d / %s / %s", wsEvent.getSessionId(), wsEvent.getProcessDefinitionId(), wsEvent.getProcessInstanceId()));
//                SharkEvent event = SharkEvent.newProcessUpdateEvent(wsEvent.getProcessDefinitionId(), wsEvent.getProcessInstanceId());
//                workflowEventManager().pushEvent(wsEvent.getSessionId(), event);
//            }
//
//        });
    }

    @Override
    public List<FunctionSchema> getFunctionList() {
        List<FunctionSchema> functionSchemas = new ArrayList<>();
//        for (StoredFunction function : userDataView().findAllFunctions()) {
        for (StoredFunction function : dao.getAllFunctions()) {//TODO function access control
            functionSchemas.add(functionSchemaFor(function));
        }
        return functionSchemas;
    }

    private FunctionSchema functionSchemaFor(StoredFunction function) {
        FunctionSchema functionSchema = new FunctionSchema();
        functionSchema.setName(function.getName());
        functionSchema.setInput(attributeSchemasFrom(function.getInputParameters()));
        functionSchema.setOutput(attributeSchemasFromOutputParameters(function.getOutputParameters()));
        return functionSchema;
    }

    private List<AttributeSchema> attributeSchemasFromOutputParameters(List<org.cmdbuild.dao.entrytype.Attribute> outputParameters) {
        List<AttributeSchema> attributeSchemas = new ArrayList<>();
        for (org.cmdbuild.dao.entrytype.Attribute parameter : outputParameters) {
            attributeSchemas.add(AttributeSchemaSerializer.serialize(parameter));
        }
        return attributeSchemas;
    }

    private List<AttributeSchema> attributeSchemasFrom(List<org.cmdbuild.dao.entrytype.Attribute> parameters) {
        List<AttributeSchema> attributeSchemas = new ArrayList<>();
        for (org.cmdbuild.dao.entrytype.Attribute parameter : parameters) {
            attributeSchemas.add(AttributeSchemaSerializer.serialize(parameter));
        }
        return attributeSchemas;
    }

    @Override
    public String generateDigest(String plainText, String digestAlgorithm) throws NoSuchAlgorithmException {
        if (digestAlgorithm == null) {
            logger.error("The digest algorithm is null");
            throw new IllegalArgumentException("Both the argument must not be null. Specify the text to be encrypted and a valid digest algorithm");
        }
        if (plainText == null) {
            return null;
        }
        checkArgument(digestAlgorithm.equalsIgnoreCase("BASE64"), "unsupported digest ango = %s", digestAlgorithm);
//		Digester digester = DigesterFactory.createDigester(digestAlgorithm);
//		logger.info("Generating digest with algorithm " + digester + " ("
//				+ (digester.isReversible() ? "reversible" : "irreversible") + ")");
        return CmLegacyPasswordUtils.encrypt(plainText);
    }

    @Override
    public void suspendWorkflow(Card card) {
        workflowLogicHelper().suspendProcess(card);
    }

    @Override
    public void resumeWorkflow(Card card) {
        workflowLogicHelper().resumeProcess(card);
    }

    @Override
    public void copyAttachment(String sourceClassName, long sourceId, String filename, String destinationClassName, long destinationId) {
        dmsLogicHelper().copy(sourceClassName, sourceId, filename, destinationClassName, destinationId);
    }

    @Override
    public void moveAttachment(String sourceClassName, long sourceId, String filename, String destinationClassName, long destinationId) {
        dmsLogicHelper().move(sourceClassName, sourceId, filename, destinationClassName, destinationId);
    }

    @Override
    public void abortWorkflow(Card card) {
        workflowLogicHelper().abortProcess(card);
    }

    @Override
    public String createSession() {
        return sessionLogic().getCurrentSessionIdOrNull();
    }

}
