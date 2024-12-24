package org.sindria.xpipe.lib.nanoREST.controllers;

public class ProxyController<T> extends BaseController<ProxyController> {

    /**
     * Controller constructor
     */
    public ProxyController(Class<T> typeController) {
        super((Class<ProxyController>) typeController);
    }

    // TODO: implement HTTP proxy feature
}
