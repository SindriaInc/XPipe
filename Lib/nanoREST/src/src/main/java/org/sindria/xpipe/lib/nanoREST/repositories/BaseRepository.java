package org.sindria.xpipe.lib.nanoREST.repositories;

import org.sindria.xpipe.lib.nanoREST.config.AppConfig;
import org.sindria.xpipe.lib.nanoREST.logger.Logger;
import org.sindria.xpipe.lib.nanoREST.orm.AbstractMySQLConnector;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseRepository {

    /**
     * logger
     */
    protected final Logger logger;

    /**
     * connector
     */
    protected final AbstractMySQLConnector connector;


    public BaseRepository() {
        this.logger = Logger.getInstance();

        this.connector = new AbstractMySQLConnector(AppConfig.config.getNanorest().getDatasource().getHost(), Integer.parseInt(AppConfig.config.getNanorest().getDatasource().getPort()), AppConfig.config.getNanorest().getDatasource().getUsername(), AppConfig.config.getNanorest().getDatasource().getPassword()) {
            @Override
            protected void processServerResponse(byte[] data) {
                logger.info("Server Response: " + new String(data, StandardCharsets.UTF_8));
            }
        };

    }


    public void query(String query) {
        try {
            this.connector.connect();
            //this.connector.sendQuery(query);
            Object result = this.connector.executeQuery(query);
            logger.info("Query Result: " + result);
            this.connector.disconnect();
        } catch (IOException e) {
            logger.logException("Error during MySQL operation", e);
        }
    }
}
