package org.sindria.xpipe.fnd.v1.notifications.command;

import org.sindria.xpipe.core.lib.nanorest.kernel.CommandKernel;

import java.util.Map;

// Example implementation: SumCommand
public class SumCommand extends CommandKernel {
    @Override
    public void execute(Map<String, String> args) {
        try {
            int a = Integer.parseInt(args.getOrDefault("a", "0"));
            int b = Integer.parseInt(args.getOrDefault("b", "0"));
            System.out.println("Sum: " + (a + b));
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        }
    }
}
