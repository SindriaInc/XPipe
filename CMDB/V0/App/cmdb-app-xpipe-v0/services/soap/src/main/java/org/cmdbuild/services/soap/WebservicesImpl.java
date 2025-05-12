package org.cmdbuild.services.soap;

import java.util.List;

import javax.activation.DataHandler;
import javax.jws.WebService;
import org.cmdbuild.dao.core.q3.DaoService;

import org.cmdbuild.services.soap.structure.AttributeSchema;
import org.cmdbuild.services.soap.structure.MenuSchema;
import org.cmdbuild.services.soap.types.Attachment;
import org.cmdbuild.services.soap.types.Attribute;
import org.cmdbuild.services.soap.types.Card;
import org.cmdbuild.services.soap.types.CardList;
import org.cmdbuild.services.soap.types.Lookup;
import org.cmdbuild.services.soap.types.Order;
import org.cmdbuild.services.soap.types.Query;
import org.cmdbuild.services.soap.types.Reference;
import org.cmdbuild.services.soap.types.Relation;
import org.cmdbuild.services.soap.types.RelationExt;
import org.cmdbuild.services.soap.types.Workflow;
import org.springframework.beans.factory.annotation.Autowired;

@WebService(targetNamespace = "http://soap.services.cmdbuild.org", endpointInterface = "org.cmdbuild.services.soap.Webservices")
public class WebservicesImpl extends AbstractWebservice implements Webservices {

	@Autowired
	private DaoService dao;

	@Override
	public CardList getCardList(String className, Attribute[] attributeList, Query queryType, Order[] orderType, Long limit, Long offset, String fullTextQuery) {
		return dataAccessLogicHelper().getCardList(className, attributeList, queryType, orderType, limit, offset, fullTextQuery, null, false);
	}

	@Override
	public Card getCard(String className, Long cardId, Attribute[] attributeList) {
		return dataAccessLogicHelper().getCardExt(className, cardId, attributeList, false);
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
		return dataAccessLogicHelper().deleteRelation(relation);
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
	public Relation[] getRelationHistory(Relation relation) {
		return dataAccessLogicHelper().getRelationHistory(relation);
	}

	@Override
	public Attachment[] getAttachmentList(String className, long cardId) {
		return dmsLogicHelper().getAttachmentList(className, cardId);
	}

	@Override
	public boolean uploadAttachment(String className, long objectid, DataHandler file, String filename, String category, String description) {
		return dmsLogicHelper().uploadAttachment(className, Long.valueOf(objectid), file, filename, category, description);
	}

	@Override
	public DataHandler downloadAttachment(String className, long objectid, String filename) {
		return dmsLogicHelper().download(className, Long.valueOf(objectid), filename);
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
	public Workflow startWorkflow(Card card, boolean completeTask) {
		return workflowLogicHelper().updateProcess(card, completeTask);
	}

	@Override
	public boolean updateWorkflow(Card card, boolean completeTask) {
		workflowLogicHelper().updateProcess(card, completeTask);
		return true;
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
	public AttributeSchema[] getActivityObjects(String className, Long cardid) {
		List<AttributeSchema> attributeSchemaList = workflowLogicHelper().getAttributeSchemaList(className, cardid);
		return attributeSchemaList.toArray(new AttributeSchema[attributeSchemaList.size()]);
	}

	@Override
	public MenuSchema getActivityMenuSchema() {
		return dataAccessLogicHelper().getVisibleProcessesTree();
	}

	@Override
	public Reference[] getReference(String className, Query query, Order[] orderType, Long limit, Long offset, String fullTextQuery) {
		return dataAccessLogicHelper().getReference(className, query, orderType, limit, offset, fullTextQuery, null);
	}

	@Override
	public MenuSchema getCardMenuSchema() {
		return dataAccessLogicHelper().getVisibleClassesTree();
	}

	@Override
	public MenuSchema getMenuSchema() {
		return dataAccessLogicHelper().getMenuSchemaForPreferredGroup();
	}

	@Override
	public boolean resumeWorkflow(Card card, boolean completeTask) {
		if (completeTask) {
			logger.warn("ignoring completeTask parameter because it does not make any sense");
		}
		try {
			workflowLogicHelper().resumeProcess(card);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
