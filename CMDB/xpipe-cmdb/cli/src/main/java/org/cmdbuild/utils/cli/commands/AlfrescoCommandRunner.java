/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import static com.google.common.base.Strings.nullToEmpty;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import static org.apache.commons.lang3.StringUtils.abbreviate;
import org.cmdbuild.utils.alfresco.migrator.AlfrescoMigrationHelper;
import org.cmdbuild.utils.alfresco.migrator.AlfrescoMigrationUtils;
import org.cmdbuild.utils.alfresco.migrator.AlfrescoSourceDocumentInfo;
import org.cmdbuild.utils.cli.utils.CliAction;
import org.cmdbuild.utils.cli.utils.CliCommand;
import org.cmdbuild.utils.cli.utils.CliCommandParser;
import static org.cmdbuild.utils.cli.utils.CliCommandUtils.executeAction;
import static org.cmdbuild.utils.io.CmPropertyUtils.loadProperties;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringInline;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class AlfrescoCommandRunner extends AbstractCommandRunner {

    private final Map<String, CliAction> actions;

    public AlfrescoCommandRunner() {
        super("alfresco", "alfresco utils");
        actions = new CliCommandParser().parseActions(this);
    }

    @Override
    protected Options buildOptions() {
        Options options = super.buildOptions();
        return options;
    }

    @Override
    protected void printAdditionalHelp() {
        super.printAdditionalHelp();
        System.out.println("\navailable alfresco utils:");
        actions.values().stream().distinct().forEach((action -> {
            System.out.printf("\t%-32s\t%s\n", action.getHelpAliases(), action.getHelpParameters());
        }));
    }

    @Override
    protected void exec(CommandLine cmd) throws Exception {
        Iterator<String> iterator = cmd.getArgList().iterator();
        if (!iterator.hasNext()) {
            System.out.println("no method selected, doing nothing...");
        } else {
            executeAction(actions, iterator);
        }
    }

    @CliCommand
    protected void print(String config) {
        print(config, false);
    }

    @CliCommand
    protected void printDetailed(String config) {
        print(config, true);
    }

    @CliCommand
    protected void printCsv(String config) throws IOException {
        AlfrescoMigrationHelper helper = AlfrescoMigrationUtils.newHelper(loadProperties(new File(config)));
        try (CsvListWriter writer = new CsvListWriter(new PrintWriter(System.out), CsvPreference.EXCEL_PREFERENCE)) {
            writer.write("PATH", "SOURCE_CATEGORY", "MAPPED_CATEGORY", "AUTHOR", "DESCRIPTION", "DETAILS");
            helper.withListener(rethrowConsumer((AlfrescoSourceDocumentInfo d) -> {
                writer.write(d.getPath(), d.getCategory(), helper.getMappedCategory(d.getCategory()), d.getAuthor(), d.getDescription(), mapToLoggableStringInline(d.getProperties()));
            })::accept).listSourceDocuments();
        }
    }

    @CliCommand
    protected void test(String config) {
        AlfrescoMigrationHelper helper = AlfrescoMigrationUtils.newHelper(loadProperties(new File(config)));
        System.out.println("OK");
    }

    @CliCommand
    protected void migrate(String config) {
        AlfrescoMigrationUtils.newHelper(loadProperties(new File(config))).withListener(d -> {
            System.out.printf("migrated document: %s\n", d.getPath());
        }).migrateDocuments();
        System.out.println("done");
    }

    @CliCommand
    protected void example() {
        System.out.println(AlfrescoMigrationUtils.getConfigFileExample());
    }

    private void print(String config, boolean detailed) {
        AlfrescoMigrationHelper helper = AlfrescoMigrationUtils.newHelper(loadProperties(new File(config)));
        System.out.println("     PATH                                                                                SOURCE_CATEGORY                    MAPPED_CATEGORY                     AUTHOR    DESCRIPTION");
        helper.withListener(d -> {
            System.out.printf("%-70s  %32s   %32s   %24s    <%s>\n",
                    abbreviate(d.getPath(), 70),
                    d.getCategory(), 32,
                    helper.getMappedCategory(d.getCategory()), 32,
                    "<" + nullToEmpty(d.getAuthor()) + ">",
                    nullToEmpty(d.getDescription()));
            if (detailed) {
                System.out.println(mapToLoggableString(d.getProperties()));
            }
        }).listSourceDocuments();
    }

}
