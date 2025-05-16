/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import static com.google.common.collect.Iterables.getFirst;
import java.io.File;
import static java.lang.String.format;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.io.CmIoUtils.javaTmpDir;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.io.CmPropertyUtils.loadProperties;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.workflow.migration.SharkDbUtils;

public class ShrkDbUtilsCommandRunner extends AbstractCommandRunner {

    public ShrkDbUtilsCommandRunner() {
        super("sharkdbutils", "shark db utils");
    }

    @Override
    protected Options buildOptions() {
        Options options = super.buildOptions();
        options.addOption("configfile", true, "shark db config file");
        options.addOption("dburl", true, "shark db jdbc url (es: 'jdbc:postgresql://localhost:5435/cmdbuild_30' )");
        return options;
    }

    @Override
    protected void printAdditionalHelp() {
        System.out.println("\nactions:\n\texportxpdls: export xpdls from shark db");
//        System.out.println("\nconfig file example:\n");
//        System.out.println(readToString(getClass().getResourceAsStream("/database.conf_cli_example")));
    }

    @Override
    protected void exec(CommandLine cmd) throws Exception {
        String action = getFirst(cmd.getArgList(), "");
        if (isBlank(action)) {
            System.err.println("no action supplied!");
        } else {
            Map<String, String> config = map();
            if (cmd.hasOption("configfile")) {
                File configFile = getFile(cmd, "configfile", true, "must set valid 'configfile'");
                config.putAll(loadProperties(configFile));
            }
            if (cmd.hasOption("dburl")) {
                config.put("org.cmdbuild.workflow.shark.db.url", cmd.getOptionValue("dburl"));
            }
            switch (action.trim().toLowerCase()) {
                case "exportxpdls":
                    File temp = new File(javaTmpDir(), format("xpdl_shark_export_%s", CmDateUtils.dateTimeFileSuffix()));
                    temp.mkdirs();
                    SharkDbUtils.sharkHelper(config).getAllXpdlData().forEach(x -> {
                        File file = new File(format("%s#%s.xpdl", x.getPackageId(), x.getVersion()));
                        System.out.printf("export xpdl id = %s version = %s to file = %s\n", x.getPackageId(), x.getVersion(), file.getAbsolutePath());
                        writeToFile(file, x.getXpdlData());
                    });
                    System.out.printf("export completed to dir = %s\n", temp.getAbsolutePath());
                    break;
                default:
                    throw new IllegalArgumentException("unknown action: " + action);
            }
        }
    }

}
