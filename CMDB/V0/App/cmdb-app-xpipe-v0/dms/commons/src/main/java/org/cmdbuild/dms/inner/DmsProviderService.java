package org.cmdbuild.dms.inner;

import java.util.List;
import java.util.Optional;

import javax.activation.DataHandler;
import javax.annotation.Nullable;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dms.DocumentData;
import org.cmdbuild.minions.MinionComponent;
import org.cmdbuild.utils.io.BigByteArray;

public interface DmsProviderService extends MinionComponent {

    static final String DMS_PROVIDER_CMIS = "cmis", DMS_PROVIDER_POSTGRES = "postgres", DMS_PROVIDER_SHAREPOINT_ONLINE = "sharepoint_online", DMS_PROVIDER_ORACLEUCM = "oracleucm";

    boolean isServiceOk();

    String getDmsProviderServiceName();

    DocumentInfoAndDetail getDocument(String documentId);

    List<DocumentInfoAndDetail> getDocuments(String classId, long cardId);

    List<DocumentInfoAndDetail> getDocumentVersions(String documentId);

    DocumentInfoAndDetail create(String classId, long cardId, DocumentData document);

    DocumentInfoAndDetail update(String documentId, DocumentData document);

    DataHandler download(String documentId, @Nullable String version);

    List<String> queryDocuments(String fulltextQuery, String classId, @Nullable Long cardId);

    void delete(String documentId);

    default List<String> queryDocumentsForClass(String fulltextQuery, String classId) {
        return queryDocuments(fulltextQuery, classId, null);
    }

    default List<String> queryDocumentsForCard(String fulltextQuery, String classId, Long cardId) {
        return queryDocuments(fulltextQuery, classId, cardId);
    }

    default DocumentInfoAndDetail createLink(String sourceDocumentId, List<String> targetAbsolutePath) {
        return createLink(getDocument(sourceDocumentId), targetAbsolutePath);
    }

    default DocumentInfoAndDetail createLink(DocumentInfoAndDetail document, List<String> targetAbsolutePath) {
        throw new UnsupportedOperationException("operation not supported by this dms provider");
    }

    default void deleteLink(List<String> targetAbsolutePath) {
        throw new UnsupportedOperationException("operation not supported by this dms provider");
    }

    default BigByteArray exportAllDocumentsAsZipFile() {
        throw new UnsupportedOperationException("operation not supported by this dms provider");
    }

    default DataHandler download(String documentId) {
        return download(documentId, null);
    }

    default Optional<DataHandler> preview(String documentId) {
        return Optional.empty();
    }

    default void checkService() {
    }

    default void updateFolder(Card card) {

    }

}
