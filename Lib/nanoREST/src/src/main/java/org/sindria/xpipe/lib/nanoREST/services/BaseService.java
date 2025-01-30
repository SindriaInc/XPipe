package org.sindria.xpipe.lib.nanoREST.services;

import org.json.JSONObject;
import org.sindria.xpipe.lib.nanoREST.helpers.BaseHelper;
import org.sindria.xpipe.lib.nanoREST.logger.Logger;

public class BaseService {

    /**
     * logger
     */
    protected Logger logger;

    /**
     * Service constructor
     */
    public BaseService() {
        this.logger = Logger.getInstance();
    }

}