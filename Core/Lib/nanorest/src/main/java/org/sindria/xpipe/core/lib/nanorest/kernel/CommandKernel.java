package org.sindria.xpipe.core.lib.nanorest.kernel;

import java.util.*;

public abstract class CommandKernel {

    /**
     * Executes the command with the provided arguments.
     * @param args The command-line arguments
     */
    public abstract void execute(Map<String, String> args);

    /**
     * Parses command-line arguments into a key-value map.
     * Supports arguments in the form of --key=value and --flag.
     * @param arguments The command-line arguments
     * @return Parsed arguments as a map
     */
    public static Map<String, String> parseArguments(String[] arguments) {
        Map<String, String> parsedArgs = new HashMap<>();

        for (String arg : arguments) {
            if (arg.startsWith("--")) {
                String[] parts = arg.substring(2).split("=", 2);
                if (parts.length == 2) {
                    parsedArgs.put(parts[0], parts[1]);
                } else {
                    parsedArgs.put(parts[0], "true"); // Treat as flag (boolean true)
                }
            }
        }

        return parsedArgs;
    }

    /**
     * Runs the CLI command by parsing arguments and executing the logic.
     * @param args Command-line arguments
     */
    public final void run(String[] args) {
        Map<String, String> parsedArgs = parseArguments(args);
        execute(parsedArgs);
    }
}