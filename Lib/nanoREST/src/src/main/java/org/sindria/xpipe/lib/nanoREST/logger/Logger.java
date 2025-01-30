package org.sindria.xpipe.lib.nanoREST.logger;

import org.sindria.xpipe.lib.nanoREST.config.AppConfig;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

public class Logger {

    private static Logger instance;
    private final java.util.logging.Logger logger;

    // Private constructor ensures Singleton pattern
    private Logger() {
        logger = java.util.logging.Logger.getLogger(Logger.class.getName());
        configureLogger();
    }

    // Global access point to the instance
    public static synchronized Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    // Configures the logger (optional customization)
    private void configureLogger() {
        // Remove default handlers
        java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
        rootLogger.setUseParentHandlers(false);

        // Add a console handler with a simple formatter
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter());
        consoleHandler.setLevel(Level.ALL);

        logger.addHandler(consoleHandler);

        // Setting log level from config
        String logLevel = AppConfig.config.getNanorest().getApplication().getLogger();

        switch (logLevel) {
            case "severe":
                logger.setLevel(Level.SEVERE);
                break;
            case "warning":
                logger.setLevel(Level.WARNING);
                break;
            case "info":
                logger.setLevel(Level.INFO);
                break;
            case "debug":
                logger.setLevel(Level.ALL);
                break;
            case "off":
                logger.setLevel(Level.OFF);
                break;
            default:
                logger.setLevel(Level.ALL);
        }

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
