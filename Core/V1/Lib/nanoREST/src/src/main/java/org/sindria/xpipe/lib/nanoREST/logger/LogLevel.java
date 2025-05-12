package org.sindria.xpipe.lib.nanoREST.logger;

public enum LogLevel {

    TRACE(1),
    DEBUG(2),
    INFO(3),
    WARN(4),
    ERROR(5);

    final int level;

    LogLevel(int level) {
        this.level = level;
    }
}
