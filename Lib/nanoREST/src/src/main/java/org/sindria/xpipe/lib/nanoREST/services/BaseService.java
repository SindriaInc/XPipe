package org.sindria.xpipe.lib.nanoREST.services;

import org.json.JSONObject;
import org.sindria.xpipe.lib.nanoREST.helpers.BaseHelper;
import org.sindria.xpipe.lib.nanoREST.logger.Logger;
import org.sindria.xpipe.lib.nanoREST.repositories.BaseRepository;

public abstract class BaseService {

    /**
     * logger
     */
    protected final Logger logger;

    protected final BaseRepository repository;

    /**
     * Service constructor
     */
    public BaseService(BaseRepository repository) {
        this.logger = Logger.getInstance();
        this.repository = repository;
    }

}