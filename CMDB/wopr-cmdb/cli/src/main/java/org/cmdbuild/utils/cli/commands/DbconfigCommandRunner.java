/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.getFirst;
import com.google.common.collect.Ordering;
import static com.google.common.io.Files.copy;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import static java.lang.String.format;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.dao.config.inner.ConfigImportStrategy.CIS_DATA_ONLY;
import static org.cmdbuild.dao.config.inner.ConfigImportStrategy.CIS_RESTORE_BACKUP;
import org.cmdbuild.dao.config.inner.DatabaseCreator;
import static org.cmdbuild.dao.config.inner.DatabaseCreator.EMBEDDED_DATABASES;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfig;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfigImpl;
import org.cmdbuild.dao.config.inner.PatchService;
import static org.cmdbuild.dao.config.utils.PatchManagerUtils.getFunctionCodeRepository;
import org.cmdbuild.dao.sql.utils.SqlFunction;
import static org.cmdbuild.utils.cli.utils.CliUtils.getDbdumpFileOrNull;
import static org.cmdbuild.utils.cli.utils.CliUtils.hasInteractiveConsole;
import org.cmdbuild.utils.cli.utils.DatabaseUtils;
import static org.cmdbuild.utils.cli.utils.DatabaseUtils.buildDatabaseCreator;
import static org.cmdbuild.utils.cli.utils.DatabaseUtils.dropDatabase;
import static org.cmdbuild.utils.cli.utils.DatabaseUtils.getSqlDir;
import org.cmdbuild.utils.crypto.CmDataCryptoUtils;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.io.CmIoUtils.javaTmpDir;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.tempFile;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;
import org.cmdbuild.utils.postgres.PostgresUtils;
import org.slf4j.LoggerFactory;

public class DbconfigCommandRunner extends AbstractCommandRunner {

    public DbconfigCommandRunner() {
        super(list("dbconfig", "db", "d"), "configure cmdbuild database");
    }

    @Override
    protected Options buildOptions() {
        Options options = super.buildOptions();
        options.addOption("configfile", true, "cmdbuild database config file (es: database.conf); default to conf/<webapp>/database.conf");
        options.addOption("skippatches", false, "skip patches (do not apply patches)");
        options.addOption("backuprestore", false, "backup-restore import mode (restore all configs from dump)");
        options.addOption("dataonly", false, "data-only import mode (restore no configs from dump - except those strongly coupled with data, such as multitenant mode)");
        options.addOption("keepconfigs", false, "keep local configs for those categories that are excluded from config import");
        options.addOption("xzip", false, "compress dump with xzip (best compression, slow)");
        options.addOption("singlecore", false, "use one core to restore db (useful when concurrency is a problem)");
        options.addOption("excludelogs", false, "avoid dumping table _Request, _JobRun and _EventLog");
        options.addOption("freezesessions", false, "freeze all existing sessions in db (so that they won't expire); this is useful when importing a bug report");
        options.addOption("interactive", false, "force interactive cmd");
        return options;
    }

    @Override
    protected void printAdditionalHelp() {
        System.err.println("\nactions:"
                + "\n\tdrop: drop database"
                + "\n\tcreate <database type|dump to import>: create database"
                + "\n\trecreate|importdb <database type|dump to import>: drop database, then create database"
                + "\n\tcheck <database type|dump to import>: check dump file"
                + "\n\tpatch: apply patches to existing database"
                + "\n\tpatch <lastpatch>: apply patches to existing database, up to <lastpatch>"
                + "\n\tlistpatches: list available patches"
                + "\n\tdump [target_file]: dump database to file");
        System.err.println("\nconfig file example:\n");
        System.err.println(readToString(getClass().getResourceAsStream("/database.conf_cli_example")));
    }

    @Override
    protected void exec(CommandLine cmd) throws Exception {
        String action = getFirst(cmd.getArgList(), "");
        if (isBlank(action)) {
            System.err.println("no action supplied!");
        } else {
            switch (action.trim().toLowerCase()) {
                case "drop" ->
                    dropDatabase(getConfigFile(cmd));
                case "create" ->
                    createDatabase(cmd, getConfigFile(cmd));
                case "patch" ->
                    patchDatabase(cmd, getConfigFile(cmd));
                case "listpatches" ->
                    listPatches(cmd, getConfigFile(cmd));
                case "listfunctions" ->
                    listFunctions();
                case "check" ->
                    checkDump(cmd);
                case "recreate", "importdb" ->
                    dropAndCreateDatabase(cmd, getConfigFile(cmd));
                case "compactdb" ->
                    compactDb(getConfigFile(cmd));
                case "rebuildpatcheshash" ->
                    rebuildPatchesHash(getConfigFile(cmd));
                case "dump" -> {
                    DatabaseCreatorConfig config = DatabaseCreatorConfigImpl.builder().withConfig(new FileInputStream(getConfigFile(cmd))).build();
                    boolean xzCompression = cmd.hasOption("xzip");
                    boolean lightMode = cmd.hasOption("excludelogs");
                    String filename = cmd.getArgList().stream().skip(1).findFirst().orElse(null);
                    File file;
                    if (isNotBlank(filename)) {
                        file = new File(filename);
                        if (file.getName().endsWith(".xz")) {
                            xzCompression = true;
                        }
                    } else {
                        file = new File(javaTmpDir(), format("cmdbuild_%s_%s.%s", config.getDatabaseName(), CmDateUtils.dateTimeFileSuffix(), xzCompression ? "dump.xz" : "dump"));
                    }
                    if (hasInteractiveConsole()) {
                        System.out.printf("dump database = %s to file = %s\n", config.getDatabaseUrl(), file.getAbsolutePath());
                    }
                    PostgresUtils.newHelper(
                            config.getHost(),
                            config.getPort(),
                            config.getAdminUser(),
                            config.getAdminPassword())
                            .withDatabase(config.getDatabaseName())
                            .withXzCompression(xzCompression)
                            .withLightMode(lightMode)
                            .buildHelper()
                            .dumpDatabaseToFile(file);
                    if (hasInteractiveConsole()) {
                        System.out.printf("dump OK to %s %s\n", file.getAbsolutePath(), FileUtils.byteCountToDisplaySize(file.length()));
                    } else if (cmd.hasOption("interactive")) {
                        // do nothing, skip redirect to sysout and delete file
                    } else {
                        copy(file, System.out);
                        deleteQuietly(file);
                    }
                }
                default ->
                    throw new IllegalArgumentException("unknown action: " + action);
            }
        }
    }

    public static File prepareDumpFile(File sourceFile) {
        if (sourceFile.getName().endsWith(".zip")) {
            sourceFile = extractDatabaseFromZipFile(sourceFile);
        }
        return sourceFile;
    }

    private void dropAndCreateDatabase(CommandLine cmd, File configFile) throws Exception {
        dropDatabase(configFile);
        createDatabase(cmd, configFile);
    }

    private void patchDatabase(CommandLine cmd, File configFile) throws Exception {
        DatabaseCreator databaseCreator = buildDatabaseCreator(DatabaseCreatorConfigImpl.builder().withConfig(configFile).build());
        if (cmd.getArgList().size() <= 1) {
            System.err.println("apply patches");
            databaseCreator.applyPatches();
        } else {
            String lastPatch = cmd.getArgList().get(1);
            System.err.printf("apply patches up to =< %s >\n", lastPatch);
            databaseCreator.applyPatchesUpTo(lastPatch);
        }
        System.err.println("done");
    }

    private void listPatches(CommandLine cmd, File configFile) throws Exception {
        PatchService patchManager = buildDatabaseCreator(DatabaseCreatorConfigImpl.builder().withConfig(configFile).build()).getPatchManager();
        System.out.printf("\nlast patch on db is =< %s >\n\n", patchManager.getLastPatchOnDbKeyOrNull());
        System.out.printf("found %s available patches:\n", patchManager.getAvailableCorePatches().size());
        patchManager.getAvailableCorePatches().forEach(p -> System.out.printf("\t%s %-30s %s\n", p.getCategory(), p.getVersion(), p.getDescription()));
    }

    private void listFunctions() throws Exception {
        List<SqlFunction> functions = getFunctionCodeRepository(getSqlDir()).getAvailableFunctions();
        System.out.printf("found %s available functions:\n", functions.size());
        functions.stream().sorted(Ordering.natural().onResultOf(SqlFunction::getSignature)).forEach(f -> System.out.printf("\t%-80s %s\n", f.getSignature(), f.getRequiredPatchVersion()));
    }

    private void compactDb(File configFile) {
        new DatabaseCreator(DatabaseCreatorConfigImpl.build(readToString(configFile))).compactDb();
    }

    private void rebuildPatchesHash(File configFile) {
        PatchService patchManager = buildDatabaseCreator(DatabaseCreatorConfigImpl.builder().withConfig(configFile).build()).getPatchManager();
        patchManager.rebuildPatchesHash();
    }

    private void checkDump(CommandLine cmd) throws Exception {
        doWithDbFile(getDatabaseType(cmd), (databaseType) -> {
            File file = new File(databaseType);
            try {
                PostgresUtils.checkDumpFile(file);
                System.err.printf("dump OK = %s (%s)\n", file.getAbsolutePath(), byteCountToDisplaySize(file.length()));
            } catch (Exception ex) {
                System.err.printf("\ndump ERROR = %s (%s) : %s\n\n", file.getAbsolutePath(), byteCountToDisplaySize(file.length()), ex.toString());
                throw ex;
            }
        });
    }

    private static String getDatabaseType(CommandLine cmd) {
        return trimAndCheckNotBlank(cmd.getArgList().stream().skip(1).findFirst().orElse(null), "must set non-null 'dbtype' (es: 'empty','demo',...)");
    }

    private void createDatabase(CommandLine cmd, File configFile) throws Exception {
        doWithDbFile(getDatabaseType(cmd), (databaseType) -> {

            DatabaseCreatorConfig config = DatabaseCreatorConfigImpl.builder().accept(b -> {
                if (cmd.hasOption("backuprestore")) {
                    b.withConfigImportStrategy(CIS_RESTORE_BACKUP);
                } else if (cmd.hasOption("dataonly")) {
                    b.withConfigImportStrategy(CIS_DATA_ONLY);
                }
                if (cmd.hasOption("keepconfigs")) {
                    b.withKeepLocalConfig(true);
                }
                if (cmd.hasOption("singlecore")) {
                    b.withSingleCore(true);
                }
            }).withSource(databaseType).withConfig(configFile).build();

            DatabaseUtils.createDatabase(config, !cmd.hasOption("skippatches"));

            if (cmd.hasOption("freezesessions")) {
                System.out.println("freezing sessions");
                new DatabaseCreator(config).freezeSessions();
            }

        });

    }

    private void doWithDbFile(String databaseType, Consumer<String> consumer) throws IOException {
        List<File> toDelete = list();

        if (!EMBEDDED_DATABASES.contains(databaseType)) { //TODO improve this
            File file = getDbdumpFileOrNull(databaseType);
            if (file != null) {
                databaseType = file.getAbsolutePath();
            }
        }

        if (databaseType.endsWith(".secure")) {
            File secure = new File(databaseType), file = new File(databaseType.replaceFirst(".secure$", ""));
            try (FileInputStream in = new FileInputStream(secure); FileOutputStream out = new FileOutputStream(file)) {
                CmDataCryptoUtils.withPassword(new String(System.console().readPassword("password required for file < %s >: ", secure))).decrypt(in, out);
            }
            databaseType = file.getAbsolutePath();
            toDelete.add(file);
        }

        if (databaseType.endsWith(".zip")) {
            File file = new File(databaseType);
            checkArgument(file.isFile(), "invalid zip file = %s", databaseType);
            databaseType = extractDatabaseFromZipFile(file).getAbsolutePath();
            toDelete.add(new File(databaseType));
        }

        try {
            consumer.accept(databaseType);
        } finally {
            toDelete.forEach(FileUtils::deleteQuietly);
        }
    }

    public static File extractDatabaseFromZipFile(File file) {
        try (ZipFile zipFile = new ZipFile(file)) {
            Optional<? extends ZipEntry> entry = zipFile.stream().filter((e) -> e.getName().endsWith(".backup")).findAny();
            checkArgument(entry.isPresent(), "database backup not found in zip file = %s", file.getAbsolutePath());
            LoggerFactory.getLogger(DbconfigCommandRunner.class).info("selected database backup file = {}", entry.get().getName());
            File dump = tempFile(null, "dump");
            FileUtils.copyInputStreamToFile(zipFile.getInputStream(entry.get()), dump);
            return dump;
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

}
