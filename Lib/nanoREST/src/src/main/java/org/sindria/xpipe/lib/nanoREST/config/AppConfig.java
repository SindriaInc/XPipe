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

        System.out.println("Stampa cose");
        System.out.println(config.getVersion());
        System.out.println(config.getNanorest());

        System.out.println(config.getNanorest().getApplication());
        System.out.println(config.getNanorest().getApplication().getName());
        System.out.println(config.getNanorest().getApplication().getVersion());
        System.out.println(config.getNanorest().getApplication().getLogger());

        System.out.println(config.getNanorest().getDatasource());
        System.out.println(config.getNanorest().getDatasource().getDriverClassName());
        System.out.println(config.getNanorest().getDatasource().getUrl());
        System.out.println(config.getNanorest().getDatasource().getUsername());
        System.out.println(config.getNanorest().getDatasource().getPassword());

        System.out.println(config.getNanorest().getNanohttpd());
        System.out.println(config.getNanorest().getNanohttpd().getPort());

        //AppConfig.app = CsvHelper.csvParser("src/main/resources/seeders/types.csv", ";");

    }

    public Integer getPort() {
        return 8080;
    }


}
