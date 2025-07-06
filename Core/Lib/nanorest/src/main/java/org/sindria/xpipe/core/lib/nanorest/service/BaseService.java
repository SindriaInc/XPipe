package org.sindria.xpipe.core.lib.nanorest.service;

import org.json.JSONObject;

import org.sindria.xpipe.core.lib.nanorest.logger.Logger;
import org.sindria.xpipe.core.lib.nanorest.repository.BaseRepository;

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