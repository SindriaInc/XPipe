/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.alfresco.migrator.inner;

import com.google.common.base.Preconditions;
import java.util.Map;
import org.cmdbuild.utils.alfresco.migrator.AlfrescoSourceDocument;
import org.cmdbuild.utils.io.BigByteArray;
import org.codehaus.commons.nullanalysis.Nullable;

public class AlfrescoSourceDocumentImpl extends AlfrescoSourceDocumentInfoImpl implements AlfrescoSourceDocument {

    private final BigByteArray data;

    public AlfrescoSourceDocumentImpl(String name, String folder, @Nullable String category, @Nullable String description, @Nullable String author, Map<String, String> properties, BigByteArray data) {
        super(name, folder, category, description, author, properties);
        this.data = Preconditions.checkNotNull(data);
    }

    @Override
    public BigByteArray getData() {
        return data;
    }

    @Override
    public AlfrescoSourceDocument withFolder(String otherFolder) {
        return new AlfrescoSourceDocumentImpl(getName(), otherFolder, getCategory(), getDescription(), getAuthor(), getProperties(), data);
    }

}
