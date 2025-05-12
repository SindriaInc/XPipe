/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.database.job;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.slf4j.LoggerFactory;

public class SqlDriverUtils {

    static {
        try {
            new Reflections(new TypeAnnotationsScanner(), new SubTypesScanner()).getSubTypesOf(Driver.class).forEach(rethrowConsumer(c -> {
                Class.forName(c.getName());
            }));
        } catch (Exception ex) {
            LoggerFactory.getLogger("org.cmdbuild.etl.database.job.SqlDriverUtils").warn("error loading sql driver classes", ex);
        }
    }

    public static List<Class<? extends Driver>> getInstalledJdbcDrivers() {
        return DriverManager.drivers().map(d -> d.getClass())
                .filter(c -> !c.getSimpleName().matches("DriverWrapper|DriverWrapperLW|ContainerDatabaseDriver|DriverWrapperAutoprobe|XmlaOlap4jDriver|PoolingDriver|P6SpyDriver"))
                .collect(toList());
    }

}
