package org.cmdbuild.bim.bimserverclient;

import javax.activation.DataHandler;
import javax.annotation.Nullable;

import org.cmdbuild.bim.legacy.model.Entity;

public interface BimserverClientService {

    void uploadIfc(String projectId, DataHandler data, @Nullable String ifcFormat);

    DataHandler downloadIfc(String revisionId, String ifcFormat);

    BimserverProject createProject(String name, String description, String ifcFormat, @Nullable Long parentId);

    BimserverProject updateProject(BimserverProject project);

    BimserverProject getProjectByPoid(String projectId);

    void disableProject(String projectId);

    void enableProject(String projectId);

    Iterable<Entity> getEntitiesByType(String className, String revisionId);

    Entity getEntityByOid(String revisionId, String objectId);

    Entity getEntityByProjectIdAndGlobald(String projectId, String globalId);

    String getLastRevisionOfProject(String projectId);
    
    String getIfcVersion(String projectId);

    Entity getReferencedEntity(ReferenceAttribute reference, String revisionId);

}
