package org.sindria.xpipe.lib.nanoREST.config.models;

public class Nanorest {

    public Application application;

    public Datasource datasource;

    public Nanohttpd nanohttpd;

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Datasource getDatasource() {
        return datasource;
    }

    public void setDatasource(Datasource datasource) {
        this.datasource = datasource;
    }

    public Nanohttpd getNanohttpd() {
        return nanohttpd;
    }

    public void setNanohttpd(Nanohttpd nanohttpd) {
        this.nanohttpd = nanohttpd;
    }
}
