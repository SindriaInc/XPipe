/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.platform;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.MoreCollectors.onlyElement;
import com.google.common.io.Files;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.dao.DaoException;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.PostgresDriverAutoconfigureHelperService;
import org.cmdbuild.dao.config.DatabaseConfiguration;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DEFAULT_DB_DRIVER_CLASS_NAME;
import static org.cmdbuild.utils.lang.CmReflectionUtils.existsOnClasspath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class PostgresDriverAutoconfigureHelperServiceImpl implements PostgresDriverAutoconfigureHelperService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final PlatformService platformService;
    private final DirectoryService directoryService;
    private final DatabaseConfiguration databaseConfiguration;

    public PostgresDriverAutoconfigureHelperServiceImpl(PlatformService platformService, DirectoryService directoryService, DatabaseConfiguration databaseConfiguration) {
        this.platformService = checkNotNull(platformService);
        this.directoryService = checkNotNull(directoryService);
        this.databaseConfiguration = checkNotNull(databaseConfiguration);
    }

    @Override
    public void autoconfigurePostgresDriver() {
        boolean restartRequired = false;
        try {
            if (directoryService.hasWebappDirectory() && equal(databaseConfiguration.getDriverClassName(), DEFAULT_DB_DRIVER_CLASS_NAME)) {
                File sourceDriverJarFile = FileUtils.listFiles(directoryService.getWebappDirectory(), new String[]{"jar"}, true).stream()
                        .filter(f -> f.getName().matches("postgresql-42.*[.]jar"))
                        .collect(onlyElement());
                logger.info("configuring postgres driver from file = {}", sourceDriverJarFile);
                File newDriverJarFile = new File(directoryService.getWebappLibDirectory(), sourceDriverJarFile.getName());
                logger.info("copy postgres driver to file = {}", newDriverJarFile);
                Files.copy(sourceDriverJarFile, newDriverJarFile);
                if (!existsOnClasspath(databaseConfiguration.getDriverClassName())) {
                    restartRequired = true;
                }
            }
        } catch (Exception ex) {
            logger.error("unable to auto configure postgres driver", ex);
        }
        if (restartRequired) {
            logger.warn("restarting container to load postgres driver from jar file");
            platformService.restartContainer();
            throw new DaoException("postgres driver has been auto configured, container reload is required to load postgres driver from jar file");
        }
    }

}
