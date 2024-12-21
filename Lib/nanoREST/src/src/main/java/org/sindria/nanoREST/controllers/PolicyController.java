package org.sindria.nanoREST.controllers;

public class PolicyController<T> extends BaseController<PolicyController> {

    /**
     * Controller constructor
     */
    public PolicyController(Class<T> typeController) {
        super((Class<PolicyController>) typeController);
    }

    // TODO: implement REST permissions policy feature
}
