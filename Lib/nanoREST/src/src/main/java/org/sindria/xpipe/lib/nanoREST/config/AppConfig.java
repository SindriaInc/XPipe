package org.sindria.xpipe.lib.nanoREST.config;

import org.sindria.xpipe.lib.nanoREST.config.helpers.ConfigHelper;
import org.sindria.xpipe.lib.nanoREST.config.models.Config;

import java.io.IOException;

public class AppConfig {

    /**
     * app
     */
    public static Config config = null;

    /**
     * AppConfig sigleton
     */
    private static AppConfig INSTANCE;

    /**
     * AppConfig instance
     */
    public static AppConfig getInstance() throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new AppConfig();
        }
        return INSTANCE;
    }

    /**
     * AppConfig constructor
     */
    public AppConfig() throws IOException {
        ConfigHelper configHelper = new ConfigHelper();
        AppConfig.config = configHelper.loadConfig("application.yaml");
    }

    public Integer getPort() throws IOException {
        AppConfig.getInstance();
        return Integer.parseInt(AppConfig.config.getNanorest().getNanohttpd().getPort());
    }


}
