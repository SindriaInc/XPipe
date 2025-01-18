package org.sindria.xpipe.lib.nanoREST.config.models;

public class App {

    public String version;

    public Nanorest nanorest;

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

}
