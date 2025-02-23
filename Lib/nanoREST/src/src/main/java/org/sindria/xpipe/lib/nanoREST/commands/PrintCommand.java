package org.sindria.xpipe.lib.nanoREST.commands;

import org.sindria.xpipe.lib.nanoREST.kernel.CommandKernel;

import java.util.Map;

public class PrintCommand extends CommandKernel {
    @Override
    public void execute(Map<String, String> args) {
        String message = args.getOrDefault("message", "Hello, World!");
        System.out.println(message);
    }
}
