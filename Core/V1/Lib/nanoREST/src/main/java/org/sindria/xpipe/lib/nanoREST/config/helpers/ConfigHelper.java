package org.sindria.xpipe.lib.nanoREST.config.helpers;

import org.sindria.xpipe.lib.nanoREST.config.models.*;

import org.sindria.xpipe.lib.nanoREST.config.models.core.Notifications;
import org.sindria.xpipe.lib.nanoREST.config.models.core.Product;
import org.sindria.xpipe.lib.nanoREST.config.models.core.StoreView;

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

        // Begin Nanorest

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

        String host = this.configService.parseValue(datasource.getHost());
        datasource.setHost(host);

        String dbPort = this.configService.parseValue(datasource.getPort());
        datasource.setPort(dbPort);

        String schema = this.configService.parseValue(datasource.getSchema());
        datasource.setSchema(schema);

        // Parse nanohttpd
        Nanohttpd nanohttpd = data.getNanorest().getNanohttpd();

        String port = this.configService.parseValue(nanohttpd.getPort());
        // TODO: convert type maybe
        nanohttpd.setPort(port);

        // End Nanorest

        // -----------------------------

        // APP

        // Parse bitbucket
        Bitbucket bitbucket = data.getApp().getBitbucket();

        String bitbucketUsername = this.configService.parseValue(bitbucket.getUsername());
        bitbucket.setUsername(bitbucketUsername);

        String bitbucketToken = this.configService.parseValue(bitbucket.getToken());
        bitbucket.setToken(bitbucketToken);

        // Parse github
        Github github = data.getApp().getGithub();

        String githubUsername = this.configService.parseValue(github.getUsername());
        github.setUsername(githubUsername);

        String githubToken = this.configService.parseValue(github.getToken());
        github.setToken(githubToken);

        // Parse gitea
        Gitea gitea = data.getApp().getGitea();

        String giteaUsername = this.configService.parseValue(gitea.getUsername());
        gitea.setUsername(giteaUsername);

        String giteaToken = this.configService.parseValue(gitea.getToken());
        gitea.setToken(giteaToken);

        // Parse jenkins
        Jenkins jenkins = data.getApp().getJenkins();

        String jenkinsUsername = this.configService.parseValue(jenkins.getUsername());
        jenkins.setUsername(jenkinsUsername);

        String jenkinsToken = this.configService.parseValue(jenkins.getToken());
        jenkins.setToken(jenkinsToken);

        // Parse cmdbuild
        Cmdbuild cmdbuild = data.getApp().getCmdbuild();

        String cmdbuildUsername = this.configService.parseValue(cmdbuild.getUsername());
        cmdbuild.setUsername(cmdbuildUsername);

        String cmdbuildToken = this.configService.parseValue(cmdbuild.getToken());
        cmdbuild.setToken(cmdbuildToken);


        // End App

        // -----------------------------

        // CORE

        // Parse Product
        Product product = data.getCore().getProduct();

        String productName = this.configService.parseValue(product.getName());
        product.setName(productName);

        String productVersion = this.configService.parseValue(product.getVersion());
        product.setVersion(productVersion);


        // Parse StoreView
        StoreView storeView = data.getCore().getStoreView();

        String italian = this.configService.parseValue(storeView.getItalian());
        storeView.setItalian(italian);

        // Parse Notifications
        Notifications notifications = data.getCore().getNotifications();

        String notificationsAccessToken = this.configService.parseValue(notifications.getAccessToken());
        notifications.setAccessToken(notificationsAccessToken);

        // Parse Github
        org.sindria.xpipe.lib.nanoREST.config.models.core.Github coreGithub = data.getCore().getGithub();

        String githubAccessToken = this.configService.parseValue(coreGithub.getAccessToken());
        coreGithub.setAccessToken(githubAccessToken);

        return data;
    }
}
