/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.alfresco.migrator.inner;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.cmdbuild.utils.alfresco.migrator.AlfrescoSourceDocument;
import org.cmdbuild.utils.alfresco.migrator.AlfrescoSourceDocumentInfo;

public interface AlfrescoSource extends AutoCloseable {

    void readSourceDocumentInfos(Object eventListener);

    void readSourceDocuments(Object eventListener, @Nullable Predicate<AlfrescoSourceDocumentInfo> filter);

    void checkConnectionOk();

    interface DocumentInfoFoundEvent {

        AlfrescoSourceDocumentInfo getDocumentInfo();
    }

    interface DocumentFoundEvent {

        AlfrescoSourceDocument getDocument();
    }

    interface FolderProcessedEvent {

        String getPath();
    }

}
