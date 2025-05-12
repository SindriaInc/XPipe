/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.alfresco.migrator.inner;

import com.google.common.base.Preconditions;
import org.cmdbuild.utils.alfresco.migrator.AlfrescoSourceDocument;

public class DocumentFoundEventImpl implements AlfrescoSource.DocumentFoundEvent {

    private final AlfrescoSourceDocument document;

    public DocumentFoundEventImpl(AlfrescoSourceDocument document) {
        this.document = Preconditions.checkNotNull(document);
    }

    @Override
    public AlfrescoSourceDocument getDocument() {
        return document;
    }

}
