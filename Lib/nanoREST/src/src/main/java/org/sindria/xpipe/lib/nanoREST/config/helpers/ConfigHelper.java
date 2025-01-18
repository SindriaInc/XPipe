package org.sindria.xpipe.lib.nanoREST.config.helpers;

import org.sindria.xpipe.lib.nanoREST.config.models.Config;
import org.sindria.xpipe.lib.nanoREST.config.models.Application;
import org.sindria.xpipe.lib.nanoREST.config.models.Datasource;
import org.sindria.xpipe.lib.nanoREST.config.models.Nanohttpd;
import org.sindria.xpipe.lib.nanoREST.config.services.ConfigService;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ConfigHelper {

    public ConfigService configService;

    public ConfigHelper() {
        this.configService = new ConfigService();
    }

    public Config loadConfig(String file) throws FileNotFoundException {

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(file);
        Yaml yaml = new Yaml(new Constructor(Config.class, new LoaderOptions()));
        Config data = yaml.load(inputStream);

        // Parse application
        Application application = data.getNanorest().getApplication();

        String name = this.configService.parseValue(application.getName());
        application.setName(name);

        String version = this.configService.parseValue(application.getVersion());
        application.setVersion(version);

        String logger = this.configService.parseValue(application.getLogger());
        application.setLogger(logger);

        // Parse datasource
        Datasource datasource = data.getNanorest().getDatasource();

        String driverClassName = this.configService.parseValue(datasource.getDriverClassName());
        datasource.setDriverClassName(driverClassName);

        String url = this.configService.parseValue(datasource.getUrl());
        datasource.setUrl(url);

        String username = this.configService.parseValue(datasource.getUsername());
        datasource.setUsername(username);

        String password = this.configService.parseValue(datasource.getPassword());
        datasource.setPassword(password);

        // Parse nanohttpd
        Nanohttpd nanohttpd = data.getNanorest().getNanohttpd();

        String port = this.configService.parseValue(nanohttpd.getPort());
        // TODO: convert type maybe
        nanohttpd.setPort(port);

        return data;
    }
}
