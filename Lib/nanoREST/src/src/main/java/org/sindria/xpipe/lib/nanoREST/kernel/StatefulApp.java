package org.sindria.xpipe.lib.nanoREST.kernel;

import java.io.IOException;
import java.util.Map;

public abstract class StatefulApp extends RestKernel {

    /**
     * BaseApp constructor v1 hardcoded
     */
    public StatefulApp(Class typeController, String apiVersion, String serviceName) throws IOException {
        super(typeController, apiVersion, serviceName);
    }

    /**
     * BaseApp constructor v2 config
     */
    public StatefulApp(Class typeController) throws IOException {
        super(typeController);
    }

    /**
     * Provides a mapping of command names to their respective command implementations.
     * @return A map where the key is the command name and the value is the command instance.
     */
    protected abstract Map<String, CommandKernel> getCommands();

    /**
     * Handles the execution of commands dynamically.
     * @param args The command-line arguments.
     */
    public final void handle(String[] args) {

        System.out.println("Stateful app ready");
        // TODO: implement console like minecraft server

    }
}
