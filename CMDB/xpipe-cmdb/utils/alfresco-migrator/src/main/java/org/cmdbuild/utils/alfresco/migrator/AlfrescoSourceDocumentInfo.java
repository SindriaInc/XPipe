/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.alfresco.migrator;

import java.util.Map;
import static org.cmdbuild.utils.alfresco.migrator.AlfrescoMigrationUtils.buildPath;
import org.codehaus.commons.nullanalysis.Nullable;

public interface AlfrescoSourceDocumentInfo {

    String getName();

    String getFolder();

    @Nullable
    String getCategory();

    @Nullable
    String getAuthor();

    @Nullable
    String getDescription();

    Map<String, String> getProperties();

    default String getPath() {
        return buildPath(getFolder(), getName());
    }

}
