package org.sindria.xpipe.core.lib.nanorest.config.model;

import org.sindria.xpipe.core.lib.nanorest.config.model.core.Core;
import org.sindria.xpipe.core.lib.nanorest.config.model.products.Products;

public class Config {

    public String version;

    public Nanorest nanorest;

    public App app;

    public Core core;

//    public Products products;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Nanorest getNanorest() {
        return nanorest;
    }

    public void setNanorest(Nanorest nanorest) {
        this.nanorest = nanorest;
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public Core getCore() {
        return core;
    }

    public void setCore(Core core) {
        this.core = core;
    }

//    public Products getProducts() {
//        return products;
//    }
//
//    public void setProducts(Products products) {
//        this.products = products;
//    }

}
