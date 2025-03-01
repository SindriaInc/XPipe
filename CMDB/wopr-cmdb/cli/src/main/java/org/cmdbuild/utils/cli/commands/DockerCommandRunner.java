/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import jakarta.annotation.Nullable;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import org.cmdbuild.debuginfo.BuildInfo;
import org.cmdbuild.utils.cli.packager.CliPackagerUtils;
import org.cmdbuild.utils.cli.utils.CliAction;
import org.cmdbuild.utils.cli.utils.CliCommand;
import org.cmdbuild.utils.cli.utils.CliCommandParser;
import static org.cmdbuild.utils.cli.utils.CliCommandUtils.executeAction;
import static org.cmdbuild.utils.cli.utils.CliUtils.getBuildInfo;
import static org.cmdbuild.utils.exec.CmProcessUtils.executeProcess;
import static org.cmdbuild.utils.io.CmIoUtils.copy;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

public class DockerCommandRunner extends AbstractCommandRunner {

    private final Map<String, CliAction> actions;

    public DockerCommandRunner() {
        super("docker", "docker utils");
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
        System.out.println("\navailable docker utils:");
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

    @CliCommand("bim")
    protected void bimImage() throws Exception {
        bimImage(null);
    }

    @CliCommand("bim")
    protected void bimImage(@Nullable String ifcVersion) throws Exception {
        createDockerTempFolder("/org/cmdbuild/utils/xkt/Dockerfile", "/org/cmdbuild/utils/xkt/xkt_docker_create.sh", "#ifcversion#", firstNotNull(ifcVersion, "0.0.66"));
    }

    @CliCommand("gis")
    protected void gisImage(String odaUrl) throws Exception {
        createDockerTempFolder("/org/cmdbuild/utils/oda/Dockerfile", "/org/cmdbuild/utils/oda/oda_docker_create.sh", "#odaurl#", checkNotNull(odaUrl, "oda url not set"));
    }

    private void createDockerTempFolder(String dockerPath, String dockerShPath, String key, String value) throws Exception {
        BuildInfo buildInfo = getBuildInfo();
        String dockerImageVersion = buildInfo.getVersionNumber().replaceAll("([0-9]\\.[0-9]\\.[0-9])-.+", "$1");

        File tempDir = tempDir();
        File dockerFile = new File(tempDir, "Dockerfile");
        String dockerString = new String(CliPackagerUtils.class.getResourceAsStream(dockerPath).readAllBytes(), StandardCharsets.UTF_8).replace(key, checkNotNull(value));
        writeStringToFile(dockerFile, dockerString, StandardCharsets.UTF_8);
        File scriptFile = new File(tempDir, "oda_docker_create.sh");
        copy(CliPackagerUtils.class.getResourceAsStream(dockerShPath), scriptFile);
        String scriptString = new String(CliPackagerUtils.class.getResourceAsStream(dockerShPath).readAllBytes(), StandardCharsets.UTF_8).replace("#cmdbuildversion#", dockerImageVersion);
        writeStringToFile(scriptFile, scriptString, StandardCharsets.UTF_8);
        scriptFile.setExecutable(true);
        String output = executeProcess(list(scriptFile.getAbsolutePath(), dockerFile.getParent()), 1200l);
        logger.debug("docker output =<\n{}>", output);
    }
}
