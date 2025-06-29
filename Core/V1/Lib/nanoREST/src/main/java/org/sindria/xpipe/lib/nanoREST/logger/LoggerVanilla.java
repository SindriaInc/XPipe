package org.sindria.xpipe.lib.nanoREST.logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LoggerVanilla {

    private static LoggerVanilla _instance;
    private LogLevel logLevel;
    private List<String> logs;
    private PrintWriter fileWriter;

    private LoggerVanilla() {
        logLevel = LogLevel.INFO;
        logs = new ArrayList<>();
        try {
            fileWriter = new PrintWriter(new FileWriter("server.log"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Singleton instance retrieval
    public static LoggerVanilla getInstance() {
        if (_instance == null) {
            synchronized (LoggerVanilla.class) {
                if (_instance == null) {
                    _instance = new LoggerVanilla();
                }
            }
        }
        return _instance;
    }

    // Set the log level
    public void setLogLevel(LogLevel level) {
        logLevel = level;
    }

    // Log a message with the specified level
    public void log(LogLevel level, String message) {
        if (level.ordinal() >= logLevel.ordinal()) {
            String log = String.format("[%s] [%s] %s", level, LocalDateTime.now(), message);
            System.out.println(log);
            fileWriter.println(log);
            fileWriter.flush();
        }
        logs.add(String.format("[%s] [%s] %s", level, LocalDateTime.now(), message));
    }

    // Display all logged messages
    public void displayLogs() {
        for (String log : logs) {
            System.out.println(log);
        }
    }

}
