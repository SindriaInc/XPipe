package org.cmdbuild.bim.legacy.mapper;

import java.util.EventListener;
import java.util.List;

import org.cmdbuild.bim.legacy.model.Entity;
import org.cmdbuild.bim.legacy.model.EntityDefinition;

public interface Reader {

	interface ReaderListener extends EventListener {

		void retrieved(Entity entity);

	}

	List<Entity> readEntities(String revisionId, EntityDefinition entityDefinition);

}
