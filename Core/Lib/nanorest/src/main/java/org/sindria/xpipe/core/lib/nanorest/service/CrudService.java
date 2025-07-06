package org.sindria.xpipe.core.lib.nanorest.service;

import org.sindria.xpipe.core.lib.nanorest.repository.BaseRepository;

public class CrudService extends BaseService {

    /**
     * Service constructor
     *
     * @param repository
     */
    public CrudService(BaseRepository repository) {
        super(repository);
    }
}
