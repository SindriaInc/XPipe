package org.sindria.xpipe.lib.nanoREST.services;

import org.sindria.xpipe.lib.nanoREST.repositories.BaseRepository;

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
