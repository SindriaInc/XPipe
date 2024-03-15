/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.utils;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.nullToEmpty;
import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import org.cmdbuild.dao.config.inner.DatabaseCreator;
import static org.cmdbuild.dao.config.inner.DatabaseCreator.EMBEDDED_DATABASES;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfigImpl;
import static org.cmdbuild.utils.cli.utils.DatabaseUtils.buildDatabaseCreator;
import org.cmdbuild.utils.io.StreamProgressListener;

public class CliUtils {

    public static boolean hasInteractiveConsole() {
        return System.console() != null;
    }

    public static File getDbdumpFile(String name) {
        return checkNotNull(getDbdumpFileOrNull(name), "file not found for name =< %s >", name);
    }

    @Nullable
    public static File getDbdumpFileOrNull(String name) {
        File file = new File(name);
        if (file.exists()) {
            return file;
        } else {
            List<File> dirs = Splitter.on(":").trimResults().omitEmptyStrings().splitToList(nullToEmpty(System.getenv("CMDBUILD_DBDUMP_LOCATIONS"))).stream().map(d -> new File(d)).filter(File::isDirectory).collect(toList());
            for (File dir : dirs) {
                file = new File(dir, name);
                if (file.exists()) {
                    return file;
                }
            }
        }
        if (EMBEDDED_DATABASES.contains(name)) {
            DatabaseCreator databaseCreator = buildDatabaseCreator(DatabaseCreatorConfigImpl.builder().build());
            return databaseCreator.getDumpFile(name);
        }
        return null;
    }

    public static StreamProgressListener buildProgressListener(String name) {
        AtomicBoolean isFirst = new AtomicBoolean(true);
        return (e) -> {
            if (!isFirst.getAndSet(false)) {
                System.out.print("\033[1A\033[2K");
            }
            System.out.printf("  %s progress: %s\n", name, e.getProgressDescriptionDetailed());
        };
    }

}
