package org.sindria.xpipe.lib.nanoREST.config.models;

public class Config {

    public String version;

    public Nanorest nanorest;

    public App app;

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

}
