/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.log;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.status.StatusUtil;
import ch.qos.logback.core.util.StatusPrinter;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.eventbus.EventBus;
import java.io.File;
import java.io.StringReader;
import java.net.URI;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.EventBusUtils.logExceptions;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowFunction;
import static org.cmdbuild.utils.xml.CmXmlUtils.isXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;
import org.xml.sax.InputSource;

/**
 * this is a service used to configure logback loggin system. It's implemented
 * as a java static singleton and not as a spring singleton, so it can be used
 * to set logging config before spring starts.
 */
public class LogbackHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static LogbackHelper INSTANCE = new LogbackHelper();

    private final EventBus eventBus = new EventBus(logExceptions(logger));

    private LogbackHelper() {
    }

    public static LogbackHelper getInstance() {
        return INSTANCE;
    }

    /**
     * @return event bus to handle {@link LogbackConfigurationReloadedEvent}
     * events
     */
    public EventBus getEventBus() {
        return eventBus;
    }

    public synchronized void configureLogback(String configSource) {
        try {
            if (configSource.startsWith("file:")) {
                configSource = readToString(new File(configSource.replaceFirst("^file:", "")));
            } else if (configSource.startsWith("classpath:")) {
                configSource = readToString(getClass().getClassLoader().getResourceAsStream(configSource.replaceFirst("^classpath:", "")));
            } else if (!isXml(configSource)) {
                configSource = readToString(URI.create(configSource));
            }

            LoggerContext context = getLoggerContext();
            context.reset();
            context.getStatusManager().clear();

            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            configurator.doConfigure(new InputSource(new StringReader(configSource)));

            StatusPrinter.printInCaseOfErrorsOrWarnings(context);

            checkArgument(new StatusUtil(context).isErrorFree(0), "logback configuration failed");

            logger.info("logback configured");

            eventBus.post(LogbackConfigurationReloadedEvent.INSTANCE);
        } catch (Exception ex) {
            System.err.printf("\nlogback config error: %s\n=== LOGBACK CONFIG BEGIN ===\n%s\n=== LOGBACK CONFIG END ==\n\n", ex.toString(), configSource);
            throw runtime(ex, "error configuring logback");
        }
    }

    public List<File> getActiveLogFiles() {
        try {
            LoggerContext loggerContext = getLoggerContext();
            return loggerContext.getLoggerList().stream().flatMap(rethrowFunction(l -> list(l.iteratorForAppenders()).stream().filter(FileAppender.class::isInstance)
                    .map(FileAppender.class::cast).map(f -> new File(f.getFile())).filter(File::exists).map(rethrowFunction(File::getCanonicalPath))))
                    .sorted().distinct().map(File::new).collect(toList());
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    public List<File> getAllLogFiles() {
        try {
            LoggerContext loggerContext = getLoggerContext();
//            String logDirParam = firstNotBlankOrNull(loggerContext.getProperty("CM_LOG_DIR"), System.getProperty("org.cmdbuild.log.dir"), System.getenv("CM_LOG_DIR"));
            String logDirParam = loggerContext.getProperty("CM_LOG_DIR");
            if (isNotBlank(logDirParam) && new File(logDirParam).isDirectory()) {
                File logDir = new File(logDirParam).getCanonicalFile();
                return set(FileUtils.listFiles(logDir, null, true)).with(getActiveLogFiles()).stream().sorted().collect(toImmutableList());
            } else {
                logger.info(marker(), "log dir config param not available, returning active log files");
                return getActiveLogFiles();
            }
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    private LoggerContext getLoggerContext() {
        return (LoggerContext) StaticLoggerBinder.getSingleton().getLoggerFactory();
    }

    /**
     * this event is generated whenever the logback configuration is reloaded.
     * It may be used for services that need to register custom
     * loggers/appenders <i>after</i> the static configuration has been loaded
     * from file.
     */
    public static enum LogbackConfigurationReloadedEvent {
        INSTANCE
    }

}
