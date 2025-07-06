package org.sindria.xpipe.core.lib.nanorest.debugger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class Debugger {

    // Gson instance with pretty print
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private Debugger() {
        // Utility class, non instanziabile
    }

    /**
     * Dump any number of objects to console in pretty JSON format.
     */
    public static void dump(Object... vars) {
        System.out.println("==========[ DUMP ]==========");

        for (int i = 0; i < vars.length; i++) {
            Object var = vars[i];
            System.out.println("--> Argument #" + (i + 1));
            try {
                String json = gson.toJson(var);
                System.out.println(json);
            } catch (Exception e) {
                System.out.println("Error dumping variable: " + e.getMessage());
            }
            System.out.println("----------------------------");
        }

        System.out.println("============================");
    }

    /**
     * Dump and die: dump objects then terminate program.
     */
    public static void dd(Object... vars) {
        dump(vars);
        System.exit(1); // You can use RuntimeException for test environments
    }
}

