package org.cmdbuild.services.soap;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;

import javax.activation.DataHandler;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlSeeAlso;

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
import org.cmdbuild.services.soap.types.Report;
import org.cmdbuild.services.soap.types.ReportParams;
import org.cmdbuild.services.soap.types.UserInfo;
import org.cmdbuild.services.soap.types.WSEvent;
import org.cmdbuild.services.soap.types.Workflow;

@WebService(targetNamespace = "http://soap.services.cmdbuild.org")
@XmlSeeAlso({org.cmdbuild.services.soap.types.WSProcessStartEvent.class,
	org.cmdbuild.services.soap.types.WSProcessUpdateEvent.class})
public interface Private {

	CardList getCardList(@WebParam(name = "className") String className,
			@WebParam(name = "attributeList") Attribute[] attributeList, @WebParam(name = "queryType") Query queryType,
			@WebParam(name = "orderType") Order[] orderType, @WebParam(name = "limit") Long limit,
			@WebParam(name = "offset") Long offset, @WebParam(name = "fullTextQuery") String fullTextQuery,
			@WebParam(name = "cqlQuery") CQLQuery cqlQuery);

	Card getCard(@WebParam(name = "className") String className, @WebParam(name = "cardId") Long cardId, @WebParam(name = "attributeList") Attribute[] attributeList);

	CardList getCardHistory(@WebParam(name = "className") String className, @WebParam(name = "cardId") long cardId, @WebParam(name = "limit") Long limit, @WebParam(name = "offset") Long offset);

	long createCard(@WebParam(name = "cardType") Card cardType);

	boolean updateCard(@WebParam(name = "card") Card card);

	boolean deleteCard(@WebParam(name = "className") String className, @WebParam(name = "cardId") long cardId);

	long createLookup(@WebParam(name = "lookup") Lookup lookup);

	boolean deleteLookup(@WebParam(name = "lookupId") long lookupId);

	boolean updateLookup(@WebParam(name = "lookup") Lookup lookup);

	Lookup getLookupById(@WebParam(name = "id") long id);

	Lookup[] getLookupList(@WebParam(name = "type") String type, @WebParam(name = "value") String value, @WebParam(name = "parentList") boolean parentList);

	Lookup[] getLookupListByCode(@WebParam(name = "type") String type, @WebParam(name = "code") String code, @WebParam(name = "parentList") boolean parentList);

	boolean createRelation(@WebParam(name = "relation") Relation relation);

	boolean createRelationWithAttributes(@WebParam(name = "relation") Relation relation, @WebParam(name = "attributes") List<Attribute> attributes);

	boolean deleteRelation(@WebParam(name = "relation") Relation relation);

	List<Relation> getRelationList(@WebParam(name = "domain") String domain, @WebParam(name = "className") String className, @WebParam(name = "cardId") long cardId);

	List<RelationExt> getRelationListExt(@WebParam(name = "domain") String domain, @WebParam(name = "className") String className, @WebParam(name = "cardId") long cardId);

	List<Attribute> getRelationAttributes(@WebParam(name = "relation") Relation relation);

	void updateRelationAttributes(@WebParam(name = "relation") Relation relation, @WebParam(name = "attributes") Collection<Attribute> attributes);

	Relation[] getRelationHistory(@WebParam(name = "relation") Relation relation);

	Attachment[] getAttachmentList(@WebParam(name = "className") String className, @WebParam(name = "cardId") long cardId);

	boolean uploadAttachment(@WebParam(name = "className") String className, @WebParam(name = "objectid") long objectid,
			@WebParam(name = "file") @XmlMimeType("application/octet-stream") DataHandler file,
			@WebParam(name = "filename") String filename, @WebParam(name = "category") String category,
			@WebParam(name = "description") String description);

	@XmlMimeType("application/octet-stream")
	DataHandler downloadAttachment(@WebParam(name = "className") String className, @WebParam(name = "objectid") long objectid, @WebParam(name = "filename") String filename);

	boolean deleteAttachment(@WebParam(name = "className") String className, @WebParam(name = "cardId") long cardId, @WebParam(name = "filename") String filename);

	boolean updateAttachmentDescription(@WebParam(name = "className") String className, @WebParam(name = "cardId") long cardId, @WebParam(name = "filename") String filename, @WebParam(name = "description") String description);

	Workflow updateWorkflow(@WebParam(name = "card") Card card, @WebParam(name = "completeTask") boolean completeTask, @WebParam(name = "widgets") WorkflowWidgetSubmission[] widgets);

	String getProcessHelp(@WebParam(name = "classname") String classname, @WebParam(name = "cardid") Long cardid);

	AttributeSchema[] getAttributeList(@WebParam(name = "className") String className);

	ActivitySchema getActivityObjects(@WebParam(name = "className") String className, @WebParam(name = "cardid") Long cardid);

	MenuSchema getActivityMenuSchema();

	Reference[] getReference(@WebParam(name = "className") String className, @WebParam(name = "query") Query query,
			@WebParam(name = "orderType") Order[] orderType, @WebParam(name = "limit") Long limit,
			@WebParam(name = "offset") Long offset, @WebParam(name = "fullTextQuery") String fullTextQuery,
			@WebParam(name = "cqlQuery") CQLQuery cqlQuery);

	MenuSchema getCardMenuSchema();

	MenuSchema getMenuSchema();

	Report[] getReportList(@WebParam(name = "type") String type, @WebParam(name = "limit") long limit, @WebParam(name = "offset") long offset);

	AttributeSchema[] getReportParameters(@WebParam(name = "id") long id, @WebParam(name = "extension") String extension);

	@XmlMimeType("application/octet-stream")
	DataHandler getReport(@WebParam(name = "id") long id, @WebParam(name = "extension") String extension, @WebParam(name = "params") ReportParams[] params);

    @Deprecated
	String sync(@WebParam(name = "xml") String xml);

	UserInfo getUserInfo();

	// HACK The Project Manager forced us to do this
	CardList getCardListWithLongDateFormat(@WebParam(name = "className") String className,
			@WebParam(name = "attributeList") Attribute[] attributeList, @WebParam(name = "queryType") Query queryType,
			@WebParam(name = "orderType") Order[] orderType, @WebParam(name = "limit") Long limit,
			@WebParam(name = "offset") Long offset, @WebParam(name = "fullTextQuery") String fullTextQuery,
			@WebParam(name = "cqlQuery") CQLQuery cqlQuery);

	ClassSchema getClassSchema(@WebParam(name = "className") String className);

	ClassSchema getClassSchemaByName(@WebParam(name = "className") String name, @WebParam(name = "includeAttributes") boolean includeAttributes);

	ClassSchema getClassSchemaById(@WebParam(name = "classId") long id, @WebParam(name = "includeAttributes") boolean includeAttributes);

	CardListExt getCardListExt(@WebParam(name = "className") String className,
			@WebParam(name = "attributeList") Attribute[] attributeList, @WebParam(name = "queryType") Query queryType,
			@WebParam(name = "orderType") Order[] orderType, @WebParam(name = "limit") Long limit,
			@WebParam(name = "offset") Long offset, @WebParam(name = "fullTextQuery") String fullTextQuery,
			@WebParam(name = "cqlQuery") CQLQuery cqlQuery);

	Attribute[] callFunction(@WebParam(name = "functionName") String functionName, @WebParam(name = "params") Attribute[] params);

	/**
	 * Notify CMDBuild of an external event.
	 *
	 * @param event
	 *            a generic event
	 */
	void notify(@WebParam(name = "event") WSEvent event);

	/**
	 * Returns available functions list.
	 */
	List<FunctionSchema> getFunctionList();

	/**
	 *
	 * @param plainText
	 * @param digestAlgorithm
	 *            for now three algorithms are allowed: "SHA1", "MD5", "BASE64"
	 * @return an encrypted text produced by the digest algorithm with the plain
	 *         text as input
	 */
	String generateDigest(@WebParam(name = "plainText") String plainText, @WebParam(name = "digestAlgorithm") String digestAlgorithm) throws NoSuchAlgorithmException;

	CardExt getCardWithLongDateFormat(@WebParam(name = "className") String className, @WebParam(name = "cardId") Long cardId, @WebParam(name = "attributeList") Attribute[] attributeList);

	@XmlMimeType("application/octet-stream")
	DataHandler getBuiltInReport(@WebParam(name = "id") String reportId, @WebParam(name = "extension") String extension, @WebParam(name = "params") ReportParams[] params);

	void suspendWorkflow(@WebParam(name = "card") Card card);

	void resumeWorkflow(@WebParam(name = "card") Card card);

	void copyAttachment(@WebParam(name = "sourceClassName") String sourceClassName, @WebParam(name = "sourceId") long sourceId, @WebParam(name = "filename") String filename, @WebParam(name = "destinationClassName") String destinationClassName, @WebParam(name = "destinationId") long destinationId);

	void moveAttachment(@WebParam(name = "sourceClassName") String sourceClassName, @WebParam(name = "sourceId") long sourceId, @WebParam(name = "filename") String filename, @WebParam(name = "destinationClassName") String destinationClassName, @WebParam(name = "destinationId") long destinationId);

	void abortWorkflow(@WebParam(name = "card") Card card);

	String createSession();

}
