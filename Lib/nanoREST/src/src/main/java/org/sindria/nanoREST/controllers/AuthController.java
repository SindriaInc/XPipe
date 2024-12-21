package org.sindria.nanoREST.controllers;

public class AuthController<T> extends BaseController<AuthController> {

    /**
     * Controller constructor
     */
    public AuthController(Class<T> typeController) {
        super((Class<AuthController>) typeController);
    }

    // TODO: implement REST Oauth2 authentication feature
}
