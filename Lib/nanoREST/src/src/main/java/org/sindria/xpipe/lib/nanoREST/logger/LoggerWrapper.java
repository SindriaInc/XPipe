package org.sindria.xpipe.lib.nanoREST.logger;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerWrapper {

    private static LoggerWrapper instance;
    private final Logger logger;

    // Private constructor ensures Singleton pattern
    private LoggerWrapper() {
        logger = Logger.getLogger(LoggerWrapper.class.getName());
        configureLogger();
    }

    // Global access point to the instance
    public static synchronized LoggerWrapper getInstance() {
        if (instance == null) {
            instance = new LoggerWrapper();
        }
        return instance;
    }

    // Configures the logger (optional customization)
    private void configureLogger() {
        // Remove default handlers
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setUseParentHandlers(false);

        // Add a console handler with a simple formatter
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter());
        consoleHandler.setLevel(Level.ALL);

        logger.addHandler(consoleHandler);
        logger.setLevel(Level.ALL);
        // Fix: Without this line I get duplicate logs. I have checked at run time that in my case the number of handlers returned by logger.getHandlers() is just 1 after adding my console handler.
        logger.setUseParentHandlers(false);
    }

    // Wrapper methods for logging
    public void info(String message) {
        logger.info(message);
    }

    public void warning(String message) {
        logger.warning(message);
    }

    public void severe(String message) {
        logger.severe(message);
    }

    public void debug(String message) {
        logger.fine(message);
    }

    public void setLogLevel(Level level) {
        logger.setLevel(level);
    }

    // Additional utility method for logging exceptions
    public void logException(String message, Throwable throwable) {
        logger.log(Level.SEVERE, message, throwable);
    }

//    public static void main(String[] args) {
//        // Example usage
//        LoggerWrapper logger = LoggerWrapper.getInstance();
//
//        logger.info("This is an info message.");
//        logger.warning("This is a warning message.");
//        logger.severe("This is a severe error message.");
//        logger.debug("This is a debug message.");
//
//        try {
//            throw new Exception("Sample exception");
//        } catch (Exception e) {
//            logger.logException("Exception occurred", e);
//        }
//    }
}
