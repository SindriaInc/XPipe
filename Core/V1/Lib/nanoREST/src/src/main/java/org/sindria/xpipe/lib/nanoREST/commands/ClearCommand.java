package org.sindria.xpipe.lib.nanoREST.commands;

import org.sindria.xpipe.lib.nanoREST.kernel.CommandKernel;

import java.io.IOException;
import java.util.Map;

public class ClearCommand extends CommandKernel {
    @Override
    public void execute(Map<String, String> args) {
        clearConsole();
    }

    private void clearConsole() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder processBuilder;

            if (os.contains("win")) {
                // Windows command
                processBuilder = new ProcessBuilder("cmd", "/c", "cls");
            } else {
                // Unix/Linux/macOS command
                processBuilder = new ProcessBuilder("clear");
            }

            processBuilder.inheritIO().start().waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error clearing console: " + e.getMessage());
        }
    }
}
