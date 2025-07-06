package org.sindria.xpipe.core.lib.nanorest.config;

import org.sindria.xpipe.core.lib.nanorest.config.helper.ConfigHelper;
import org.sindria.xpipe.core.lib.nanorest.config.model.Config;

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

//    public boolean getItalianToggle() throws IOException {
//        AppConfig.getInstance();
//        return Boolean.parseBoolean(AppConfig.config.getCore().getStoreView().getItalian());
//    }


}
