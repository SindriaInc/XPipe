/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.alfresco.migrator.test;

import org.cmdbuild.utils.alfresco.migrator.AlfrescoMigrationUtils;
import static org.cmdbuild.utils.alfresco.migrator.AlfrescoMigrationUtils.getConfigFileExample;
import static org.cmdbuild.utils.io.CmPropertyUtils.loadProperties;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlfrescoMigratorTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    @Ignore
    public void testListSource() {
        AlfrescoMigrationUtils.newHelper(loadProperties(getClass().getResourceAsStream("/org/cmdbuild/utils/alfresco/migrator/example_config.properties"))).withListener(d -> {
            logger.info("found document =< {} > with category =< {} >", d.getPath(), d.getCategory());
        }).listSourceDocuments();
    }

    @Test
    @Ignore
    public void testMigrate() {
        AlfrescoMigrationUtils.newHelper(loadProperties(getClass().getResourceAsStream("/org/cmdbuild/utils/alfresco/migrator/example_config.properties"))).withListener(d -> {
            logger.info("processing document =< {} > with category =< {} >", d.getPath(), d.getCategory());
        }).migrateDocuments();
    }

    @Test
    public void testConfigExample() {
        checkNotBlank(getConfigFileExample());
    }

}
