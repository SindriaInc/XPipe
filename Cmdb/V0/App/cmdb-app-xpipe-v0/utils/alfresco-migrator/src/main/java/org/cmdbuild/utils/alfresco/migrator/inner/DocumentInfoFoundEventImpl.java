/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.alfresco.migrator.inner;

import com.google.common.base.Preconditions;
import org.cmdbuild.utils.alfresco.migrator.AlfrescoSourceDocumentInfo;

public class DocumentInfoFoundEventImpl implements AlfrescoSource.DocumentInfoFoundEvent {

    private final AlfrescoSourceDocumentInfo documentInfo;

    public DocumentInfoFoundEventImpl(AlfrescoSourceDocumentInfo documentInfo) {
        this.documentInfo = Preconditions.checkNotNull(documentInfo);
    }

    @Override
    public AlfrescoSourceDocumentInfo getDocumentInfo() {
        return documentInfo;
    }

}
