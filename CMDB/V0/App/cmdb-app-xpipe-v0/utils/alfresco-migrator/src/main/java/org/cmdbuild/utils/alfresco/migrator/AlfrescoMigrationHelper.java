/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.alfresco.migrator;

import javax.annotation.Nullable;

public interface AlfrescoMigrationHelper extends AutoCloseable {

    void listSourceDocuments();

    void migrateDocuments();

    AlfrescoMigrationHelper withListener(AlfrescoMigrationCallback listener);

    @Nullable
    String getMappedCategory(@Nullable String sourceCategory);

}
