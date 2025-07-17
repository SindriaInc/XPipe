package org.sindria.xpipe.core.lib.nanorest.registry;

import org.sindria.xpipe.core.lib.nanorest.action.Action;

import java.util.HashMap;
import java.util.Map;

public class ActionRegistry {
    private static final Map<String, Action> actions = new HashMap<>();

    public static void register(String path, Action action) {
        actions.put(path, action);
    }

    public static Action get(String path) {
        return actions.get(path);
    }

    public static boolean exists(String path) {
        return actions.containsKey(path);
    }

    public static Map<String, Action> all() {
        return actions;
    }
}
