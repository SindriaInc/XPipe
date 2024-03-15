package org.cmdbuild.services.soap.operation;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.activation.DataHandler;

import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.services.soap.types.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.dms.DocumentDataImpl;

public class DmsLogicHelper {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final OperationUser operationUser;
	private final DmsService dmsLogic;

	public DmsLogicHelper(final OperationUser operationUser, final DmsService dmsLogic) {
		this.operationUser = operationUser;
		this.dmsLogic = dmsLogic;
	}

	public Attachment[] getAttachmentList(final String className, final Long cardId) {
		final List<DocumentInfoAndDetail> storedDocuments = dmsLogic.getCardAttachments(className, cardId);
		final List<Attachment> attachments = newArrayList();
		for (final DocumentInfoAndDetail storedDocument : storedDocuments) {
			final Attachment attachment = new Attachment(storedDocument);
			attachments.add(attachment);
		}
		return attachments.toArray(new Attachment[attachments.size()]);
	}

	public boolean uploadAttachment(final String className, final Long cardId, final DataHandler file,
			final String filename, final String category, final String description) {
		try {
			dmsLogic.create(className,
					cardId,
					DocumentDataImpl.builder()
							.withAuthor(operationUser.getLoginUser().getUsername())
							.withCategory(category)
							.withData(file)
							.withFilename(filename)
							.withDescription(description)
							.withMajorVersion(true)
							.build());
			return true;
		} catch (final Exception e) {
			final String message = String.format("error uploading file '%s' in '%s'", filename, className);
			logger.error(message, e);
		}
		return false;
	}

	public DataHandler download(final String className, final Long cardId, final String filename) {
		return dmsLogic.getDocumentData(className, cardId, filename, null);
	}

	public boolean delete(final String className, final Long cardId, final String filename) {
		dmsLogic.delete(className, cardId, filename);
		return true;
	}

	public boolean updateDescription(final String className, final Long cardId, final String filename, final String description) {
		try {
			dmsLogic.updateDocumentWithFilename(className, cardId, filename,
					DocumentDataImpl.builder()
							.withAuthor(operationUser.getLoginUser().getUsername())
							.withFilename(filename)
							.withDescription(description)
							.withMajorVersion(false)
							.build());
			return true;
		} catch (final Exception e) {
			return false;
		}
	}

	public void copy(final String sourceClassName, final Long sourceId, final String filename,
			final String destinationClassName, final Long destinationId) {
		throw new UnsupportedOperationException("UNSUPPORTED");
//		dmsLogic.copy( //
//				sourceClassName, //
//				sourceId, //
//				filename, //
//				destinationClassName, //
//				destinationId);
	}

	public void move(final String sourceClassName, final Long sourceId, final String filename,
			final String destinationClassName, final Long destinationId) {
		throw new UnsupportedOperationException("UNSUPPORTED");
//		dmsLogic.move( //
//				sourceClassName, //
//				sourceId, //
//				filename, //
//				destinationClassName, //
//				destinationId);
	}

}
