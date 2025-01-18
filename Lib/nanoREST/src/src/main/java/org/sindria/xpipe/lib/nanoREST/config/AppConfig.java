package org.sindria.xpipe.lib.nanoREST.config;

import org.sindria.xpipe.lib.nanoREST.config.models.App;
import org.sindria.xpipe.lib.nanoREST.helpers.YamlHelper;

import java.io.IOException;

public class AppConfig {

    /**
     * app
     */
    //public static List<List> app = null;
    public static App app = null;
    //public static Map<String, Object> app = null;

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
        YamlHelper yamlHelper = new YamlHelper();
        AppConfig.app = yamlHelper.loadConfig("application.yaml");

        System.out.println("Stampa cose");
        System.out.println(app.getVersion());
        System.out.println(app.getNanorest());

        System.out.println(app.getNanorest().getApplication());
        System.out.println(app.getNanorest().getApplication().getName());
        System.out.println(app.getNanorest().getApplication().getVersion());
        System.out.println(app.getNanorest().getApplication().getLogger());

        System.out.println(app.getNanorest().getDatasource());
        System.out.println(app.getNanorest().getDatasource().getDriverClassName());
        System.out.println(app.getNanorest().getDatasource().getUrl());
        System.out.println(app.getNanorest().getDatasource().getUsername());
        System.out.println(app.getNanorest().getDatasource().getPassword());

        System.out.println(app.getNanorest().getNanohttpd());
        System.out.println(app.getNanorest().getNanohttpd().getPort());

        //AppConfig.app = CsvHelper.csvParser("src/main/resources/seeders/types.csv", ";");

    }

    public Integer getPort() {
        return 8080;
    }


}
