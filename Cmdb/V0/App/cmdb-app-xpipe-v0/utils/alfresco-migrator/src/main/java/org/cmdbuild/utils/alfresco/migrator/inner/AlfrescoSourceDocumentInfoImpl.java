/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.alfresco.migrator.inner;

import java.util.Map;
import org.cmdbuild.utils.alfresco.migrator.AlfrescoSourceDocumentInfo;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.codehaus.commons.nullanalysis.Nullable;

public class AlfrescoSourceDocumentInfoImpl implements AlfrescoSourceDocumentInfo {

    private final String category;
    private final String description;
    private final String name;
    private final String folder;
    private final String author;
    private final Map<String, String> properties;

    public AlfrescoSourceDocumentInfoImpl(String name, String folder, @Nullable String category, @Nullable String description, @Nullable String author, Map<String, String> properties) {
        this.category = category;
        this.description = description;
        this.author = author;
        this.name = checkNotBlank(name);
        this.folder = checkNotBlank(folder);
        this.properties = map(properties).immutable();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFolder() {
        return folder;
    }

    @Nullable
    @Override
    public String getCategory() {
        return category;
    }

    @Nullable
    @Override
    public String getAuthor() {
        return author;
    }

    @Nullable
    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

}
