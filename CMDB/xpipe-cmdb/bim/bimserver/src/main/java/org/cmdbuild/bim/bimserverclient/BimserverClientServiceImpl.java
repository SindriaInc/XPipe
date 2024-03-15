package org.cmdbuild.bim.bimserverclient;

import static com.google.common.base.Objects.equal;
import org.cmdbuild.bim.BimException;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.ByteArrayOutputStream;
import static java.lang.Long.max;
import static java.lang.Long.min;
import java.lang.reflect.Method;
import java.util.List;

import javax.activation.DataHandler;

import org.apache.commons.lang3.StringUtils;
import org.bimserver.client.BimServerClient;
import org.bimserver.client.ClientIfcModel;
import org.bimserver.client.json.JsonBimServerClientFactory;
import org.bimserver.emf.IdEObject;
import org.bimserver.emf.IfcModelInterface;
import org.bimserver.interfaces.objects.SDeserializerPluginConfiguration;
import org.bimserver.interfaces.objects.SProject;
import org.bimserver.interfaces.objects.SRevision;
import org.bimserver.shared.UsernamePasswordAuthenticationInfo;
import org.cmdbuild.bim.legacy.model.Entity;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.slf4j.Logger;

import static java.lang.Long.parseLong;
import static java.util.Collections.emptyList;
import java.util.function.Consumer;
import static java.util.stream.Collectors.joining;
import javax.annotation.Nullable;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.bimserver.interfaces.objects.SActionState.STARTED;
import org.bimserver.interfaces.objects.SLongActionState;
import static org.bimserver.interfaces.objects.SObjectState.ACTIVE;
import static org.cmdbuild.bim.utils.BimConstants.IFC_CONTENT_TYPE;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.cache.CmCache;
import static org.cmdbuild.utils.io.CmIoUtils.countBytes;
import static org.cmdbuild.utils.io.CmIoUtils.dataSourceInfoSafe;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.io.CmStreamProgressUtils.buildProgressListener;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExecutorUtils.sleepSafe;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.minions.MinionComponent;
import org.cmdbuild.minions.MinionHandler;
import org.cmdbuild.minions.MinionHandlerImpl;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.cmdbuild.config.BimConfiguration;
import org.cmdbuild.minions.MinionHandlerExt;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_ERROR;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_NOTRUNNING;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_READY;

@Component
public final class BimserverClientServiceImpl implements BimserverClientService, MinionComponent {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final MinionHandlerExt minionHandler;

    private final BimConfiguration configuration;
    private BimServerClient client;
    private JsonBimServerClientFactory clientFactory;
    private final CmCache<IfcModelInterface> models;

    public BimserverClientServiceImpl(BimConfiguration configuration, CacheService cacheService) {
        this.configuration = checkNotNull(configuration);
        models = cacheService.newCache("bimserver_models", CacheConfig.SYSTEM_OBJECTS);
        this.minionHandler = MinionHandlerImpl.builder()
                .withName("BIM_ Bimserver client")
                .withEnabledChecker(configuration::isBimserverEnabled)
                .reloadOnConfigs(BimConfiguration.class)
                .build();
    }

    @Override
    public MinionHandler getMinionHandler() {
        return minionHandler;
    }

    @Override
    public synchronized void start() {
        try {
            logger.info("connecting to bimserver url =< {} >", configuration.getUrl());
            clientFactory = new JsonBimServerClientFactory(configuration.getUrl().replaceFirst("/$", ""));
            client = clientFactory.create(new UsernamePasswordAuthenticationInfo(configuration.getUsername(), configuration.getPassword()));
            checkArgument(isConnected());
            logger.info("bimserver connection established");
            minionHandler.setStatus(MRS_READY);
        } catch (Exception e) {
            minionHandler.setStatus(MRS_ERROR);
            throw runtime(e, "bimserver connection failed (bimserver url =< %s >)", configuration.getUrl());
        }
    }

    @Override
    public synchronized void stop() {
        if (client != null) {
            logger.info("close bimserver connection");
            try {
                client.disconnect();
                client.close();
            } catch (Exception ex) {
                logger.warn("error closing bimserver client", ex);
            }
            client = null;
        }
        if (clientFactory != null) {
            try {
                clientFactory.close();
            } catch (Exception ex) {
                logger.warn("error closing bim server client factory", ex);
            }
            clientFactory = null;
        }
        minionHandler.setStatus(MRS_NOTRUNNING);
    }

    @Override
    public void uploadIfc(String projectId, DataHandler data, @Nullable String ifcFormat) {
        long topicId = -1;
        try {
            long size = countBytes(data);
            String fileName = data.getName();
            logger.info("upload ifc file = {} ({}) for bim project id = {} with suggested ifc format =< {} >", fileName, FileUtils.byteCountToDisplaySize(size), projectId, ifcFormat);
            Long poid = parseLong(projectId);
            SDeserializerPluginConfiguration deserializer;
            requireConnection();
            if (isBlank(ifcFormat)) {
                deserializer = checkNotNull(client.getServiceInterface().getSuggestedDeserializerForExtension("ifc", poid), "deserializer not found for poid = %s", poid);
            } else {
                String deserializerName = IfcVersions.valueOf(ifcFormat).getDeserializerName();
                deserializer = checkNotNull(client.getServiceInterface().getDeserializerByName(deserializerName), "deserializer not found for name =< %s >", deserializerName);
            }
            logger.debug("begin upload of ifc file =< {} > for bim poid = {} with actual ifc format =< {} > deserializer = {}", fileName, poid, deserializer.getName(), deserializer);
            Consumer<Long> progressListener = buildProgressListener(100, (p) -> logger.info("ifc upload progress = {}", p.getProgressDescriptionDetailed()));
            long progress = 0;
            topicId = client.getServiceInterface().checkinAsync(poid, "", deserializer.getOid(), size, fileName, data, false);
            for (int i = 0; i < 10 && client.getNotificationRegistryInterface().getProgress(topicId) == null; i++) {
                sleepSafe(500);
            }
            while (true) {
                logger.debug("check operation progress");
                SLongActionState progressReport = client.getNotificationRegistryInterface().getProgress(topicId);
                checkNotNull(progressReport, "Operation progress status not available");
                logger.debug("upload status =< {} > stage =< {} >  title =< {} > progress =< {} >", progressReport.getState(), progressReport.getStage(), progressReport.getTitle(), progressReport.getProgress());
                progress = min(max(progress, firstNotNull(progressReport.getProgress(), 0)), 99);
                progressListener.accept(progress);
                handleUploadProgressStatus(progressReport);
                if (!equal(STARTED, progressReport.getState()) || !progressReport.getErrors().isEmpty()) {
                    break;
                }
                sleepSafe(2000);
            }
            progressListener.accept(100l);
            logger.info("completed upload of ifc file = {} for bim poid = {}", fileName, poid);
        } catch (Throwable e) {
            throw new BimException(e, "ifc upload error with file = %s", dataSourceInfoSafe(data));
        } finally {
            cleanupLongAction(topicId);
        }
    }

    private void cleanupLongAction(long topicId) {
        try {
            if (topicId != -1) {
                client.getServiceInterface().cleanupLongAction(topicId);
            }
        } catch (Exception ex) {
            logger.error("There has been a problem with the topic cleanup: {}", ex.getMessage());
        }
    }

    private void handleUploadProgressStatus(SLongActionState state) {
        if (!state.getInfos().isEmpty()) {
            logger.debug("info: {}", state.getInfos().stream().collect(joining("; ")));
        }
        if (!state.getWarnings().isEmpty()) {
            logger.warn("warning: {}", state.getWarnings().stream().collect(joining("; ")));
        }
        if (!state.getErrors().isEmpty()) {
            throw new BimException("bimserver error: %s", state.getErrors().stream().collect(joining("; ")));
        }
    }

    @Override
    public BimserverProject createProject(String projectName, String description, String ifcVersion, @Nullable Long parentId) {
        try {
            requireConnection();
            SProject project;
            logger.debug("create ifc project with name =< {} > schema =< {} >", projectName, ifcVersion);
            if (parentId == null) {
                project = client.getServiceInterface().addProject(projectName, ifcVersion);
            } else {
                project = client.getServiceInterface().addProjectAsSubProject(projectName, parentId, ifcVersion);
            }
            project.setDescription(description);
            client.getServiceInterface().updateProject(project);
            return toBimserverProject(project);
        } catch (Exception ex) {
            throw new BimException(ex);
        }
    }

    @Override
    public void disableProject(String projectId) {
        try {
            requireConnection();
            Long poid = parseLong(projectId);
            client.getServiceInterface().deleteProject(poid);
        } catch (Exception e) {
            throw new BimException(e);
        }
    }

    @Override
    public void enableProject(String projectId) {
        try {
            requireConnection();
            Long poid = parseLong(projectId);
            client.getServiceInterface().undeleteProject(poid);
        } catch (Exception e) {
            throw new BimException(e);
        }
    }

    @Override
    public DataHandler downloadIfc(String roid, String ifcVersion) {
        try {
            requireConnection();
            String serializerName = IfcVersions.valueOf(ifcVersion).getSerializerName();
            Serializer serializer = new BimserverSerializer(client.getServiceInterface().getSerializerByName(serializerName));

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            client.download(parseLong(roid), serializer.getOid(), outputStream);

            return newDataHandler(outputStream.toByteArray(), IFC_CONTENT_TYPE, roid + ".ifc");
        } catch (Exception e) {
            throw new BimException(e);
        }
    }

    @Override
    public Iterable<Entity> getEntitiesByType(String type, String revisionId) {
        try {
            requireConnection();
            SRevision revision = client.getServiceInterface().getRevision(parseLong(revisionId));
            SProject project = client.getServiceInterface().getProjectByPoid(revision.getProjectId());
            IfcVersions ifcVersion = IfcVersions.valueOf(project.getSchema());

            EPackage einstance = ifcVersion.getPackage();
            String methodName = "get" + type;
            Method method = einstance.getClass().getDeclaredMethod(methodName);
            EClass response = (EClass) method.invoke(einstance, (Object[]) null);

            List<IdEObject> entitiesResponse = firstNotNull(getModelByRevId(revisionId).getAllWithSubTypes(response), emptyList());

            List<Entity> entities = list();
            entitiesResponse.forEach((object) -> {
                if (ifcVersion.equals(IfcVersions.ifc2x3tc1) && object instanceof org.bimserver.models.ifc2x3tc1.IfcRoot) {
                    Entity entity = new org.cmdbuild.bim.bimserverclient.BimserverEntity(org.bimserver.models.ifc2x3tc1.IfcRoot.class.cast(object));
                    entities.add(entity);
                } else if (ifcVersion.equals(IfcVersions.ifc4) && object instanceof org.bimserver.models.ifc4.IfcRoot) {
                    Entity entity = new org.cmdbuild.bim.bimserverclient.BimserverEntity(org.bimserver.models.ifc4.IfcRoot.class.cast(object));
                    entities.add(entity);
                }
            });
            return entities;
        } catch (Exception e) {
            throw new BimException(e);
        }
    }

    @Override
    public Entity getEntityByOid(String revisionId, String objectId) {
        requireConnection();
        IfcModelInterface model = getModelByRevId(revisionId);
        return new BimserverEntity(model.get(parseLong(objectId)));
    }

    @Override
    public Entity getEntityByProjectIdAndGlobald(String projectId, String globalId) {
        requireConnection();
        return new BimserverEntity(getModelByProjectId(projectId).getByGuid(checkNotBlank(globalId)));
    }

    @Override
    public String getLastRevisionOfProject(String projectId) {
        try {
            requireConnection();
            SProject project = client.getServiceInterface().getProjectByPoid(parseLong(projectId));
            return String.valueOf(project.getLastRevisionId());
        } catch (Exception e) {
            throw new BimException(e);
        }
    }

    @Override
    public BimserverProject getProjectByPoid(String projectId) {
        try {
            requireConnection();
            SProject project = client.getServiceInterface().getProjectByPoid(parseLong(projectId));
            return toBimserverProject(project);
        } catch (Exception e) {
            throw new BimException(e);
        }
    }

    @Override
    public String getIfcVersion(String projectId) {
        try {
            requireConnection();
            SProject project = client.getServiceInterface().getProjectByPoid(parseLong(projectId));
            return project.getSchema();
        } catch (Exception e) {
            throw new BimException(e);
        }
    }

    @Override
    public Entity getReferencedEntity(ReferenceAttribute reference, String revisionId) {
        requireConnection();
        Entity entity;
        if (!StringUtils.isBlank(reference.getGlobalId())) {
            String guid = reference.getGlobalId();
            entity = getEntityByGuid(revisionId, guid, null);
        } else {
            Long oid = reference.getOid();
            entity = getEntityByOid(revisionId, String.valueOf(oid));
        }
        return entity;
    }

    @Override
    public BimserverProject updateProject(BimserverProject project) {
        try {
            requireConnection();
            Long poid = parseLong(project.getProjectId());
            SProject currentProject = client.getServiceInterface().getProjectByPoid(poid);
//            logger.debug("update bimserver project = {} set descr =< {} > schema =< {} >", poid, project.getDescription(), project.getIfcFormat());
            logger.debug("update bimserver project = {} set descr =< {} >", poid, project.getDescription());
            currentProject.setDescription(project.getDescription());
//            currentProject.setSchema(project.getIfcFormat()); TODO this does not work :(
            client.getServiceInterface().updateProject(currentProject);
            return getProjectByPoid(poid.toString());
        } catch (Exception e) {
            throw new BimException(e);
        }
    }

    private void requireConnection() {
        try {
            checkArgument(isConnected(), "client not connected");
        } catch (Exception e) {
            minionHandler.setErrorIfReady();
            throw e;
        }
    }

    private BimserverProject toBimserverProject(SProject project) {
        return BimserverProjectImpl.builder()
                .withDescription(project.getDescription())
                .withName(project.getName())
                .withIfcFormat(project.getSchema())
                .withIsActive(equal(project.getState(), ACTIVE))
                .withProjectId(Long.toString(project.getOid()))
                .build();
    }

    private boolean isConnected() {
        if (client == null) {
            return false;
        } else {
            try {
                return client.getBimServerAuthInterface().isLoggedIn();
            } catch (Exception t) {
                logger.error("Unable to check login state", t);
                return false;
            }
        }
    }

    private Entity getEntityByGuid(String revisionId, String guid, Iterable<String> candidateTypes) {
        IfcModelInterface model = getModelByRevId(revisionId);
        return new BimserverEntity(model.getByGuid(guid));
    }

    private BimRevision getRevision(String identifier) {
        Long roid = parseLong(identifier);
        BimRevision revision = BimRevision.NULL_REVISION;
        try {
            if (roid != -1) {
                requireConnection();
                revision = new BimserverRevision(client.getServiceInterface().getRevision(roid));
            }
            return revision;
        } catch (Exception e) {
            throw new BimException(e);
        }
    }

    private IfcModelInterface getModelByRevId(String revisionId) {
        return models.get(revisionId, () -> doGetModelByRevId(revisionId));
    }

    private IfcModelInterface getModelByProjectId(String projectId) {
        return getModelByRevId(getLastRevisionOfProject(projectId));
    }

    private IfcModelInterface doGetModelByRevId(String revisionId) {
        requireConnection();
        String projectId = getRevision(revisionId).getProjectId();
        try {
            SProject project = client.getServiceInterface().getProjectByPoid(parseLong(projectId));
            ClientIfcModel model = client.getModel(project, parseLong(revisionId), true, false);
            return model;
        } catch (Exception e) {
            throw new BimException(e, "error loading model with rev id = %s", revisionId);
        }
    }

    private enum IfcVersions {

        ifc2x3tc1 {
            @Override
            public String getDeserializerName() {
                return "Ifc2x3tc1 (Streaming)";
            }

            @Override
            public String getSerializerName() {
                return "Ifc2x3tc1";
            }

            @Override
            public EPackage getPackage() {
                return org.bimserver.models.ifc2x3tc1.Ifc2x3tc1Package.eINSTANCE;
            }

        },
        ifc4 {
            @Override
            public String getDeserializerName() {
                return "Ifc4 (Streaming)";
            }

            @Override
            public String getSerializerName() {
                return "Ifc4";
            }

            @Override
            public EPackage getPackage() {
                return org.bimserver.models.ifc4.Ifc4Package.eINSTANCE;
            }

        };

        public abstract String getDeserializerName();

        public abstract String getSerializerName();

        public abstract EPackage getPackage();
    }
}
