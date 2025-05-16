package org.cmdbuild.dms.cmis;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.MoreCollectors.toOptional;
import java.io.ByteArrayInputStream;
import static java.lang.Math.toIntExact;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import javax.activation.DataHandler;
import javax.annotation.Nullable;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Rendition;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import static org.apache.chemistry.opencmis.commons.PropertyIds.NAME;
import static org.apache.chemistry.opencmis.commons.PropertyIds.OBJECT_TYPE_ID;
import static org.apache.chemistry.opencmis.commons.PropertyIds.SECONDARY_OBJECT_TYPE_IDS;
import org.apache.chemistry.opencmis.commons.enums.CmisVersion;
import static org.apache.chemistry.opencmis.commons.enums.CmisVersion.CMIS_1_0;
import static org.apache.chemistry.opencmis.commons.enums.VersioningState.MAJOR;
import static org.apache.chemistry.opencmis.commons.enums.VersioningState.MINOR;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.tika.Tika;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dms.DmsConfiguration.DMS_CONFIG_PROVIDER;
import org.cmdbuild.dms.DocumentData;
import static org.cmdbuild.dms.cmis.CmisDmsUtils.CMIS_DOCUMENT;
import static org.cmdbuild.dms.cmis.CmisDmsUtils.CMIS_PROPERTY_AUTHOR;
import static org.cmdbuild.dms.cmis.CmisDmsUtils.CMIS_PROPERTY_CATEGORY;
import static org.cmdbuild.dms.cmis.CmisDmsUtils.CMIS_PROPERTY_DESCRIPTION;
import static org.cmdbuild.dms.cmis.CmisDmsUtils.getCmisRepository;
import static org.cmdbuild.dms.cmis.CmisDmsUtils.getFolderOrNull;
import org.cmdbuild.dms.inner.DmsProviderService;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.dms.inner.DocumentInfoAndDetailImpl;
import static org.cmdbuild.dms.inner.DocumentPathUtils.buildClassFolderPath;
import static org.cmdbuild.dms.inner.DocumentPathUtils.buildDocumentPath;
import org.cmdbuild.exception.DmsException;
import org.cmdbuild.lock.ItemLock;
import static org.cmdbuild.lock.LockScope.LS_REQUEST;
import org.cmdbuild.lock.LockService;
import org.cmdbuild.minions.MinionComponent;
import org.cmdbuild.minions.MinionConfig;
import static org.cmdbuild.minions.MinionConfig.MC_DISABLED;
import static org.cmdbuild.minions.MinionConfig.MC_ENABLED;
import org.cmdbuild.minions.MinionHandler;
import org.cmdbuild.minions.MinionHandlerImpl;
import static org.cmdbuild.minions.MinionUtils.buildMinionRuntimeStatusChecker;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeString;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeString;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmNullableUtils.ltZeroToNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import static org.cmdbuild.utils.url.CmUrlUtils.buildUrlPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CmisDmsProviderService implements DmsProviderService, MinionComponent {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Tika tika = new Tika();

    private final DaoService dao;
    private final CmisDmsConfiguration configuration;
    private final LockService lockService;

    private final MinionHandler minionHandler;

    private final Holder<Repository> repository;

    public CmisDmsProviderService(DaoService dao, CmisDmsConfiguration configuration, CacheService cacheService, LockService lockService) {
        this.dao = checkNotNull(dao);
        this.configuration = checkNotNull(configuration);
        this.repository = cacheService.newHolder("dms_cmis_repository");
        this.lockService = checkNotNull(lockService);
        this.minionHandler = MinionHandlerImpl.builder()
                .withName("DMS_ CMIS client")
                .withEnabledChecker(() -> configuration.isEnabled(getDmsProviderServiceName()))
                .withStatusChecker(buildMinionRuntimeStatusChecker(this::isEnabled, this::checkService))
                .reloadOnConfigs(CmisDmsConfiguration.class, DMS_CONFIG_PROVIDER)
                .build();
    }

    @Override
    public MinionHandler getMinionHandler() {
        return minionHandler;
    }

    @Override
    public void stop() {
        repository.invalidate();
    }

    @Override
    public boolean isServiceOk() {
        return isReady();
    }

    @Override
    public void checkService() {
        getRepository();
    }

    public MinionConfig getMinionConfig() {
        return isEnabled() ? MC_ENABLED : MC_DISABLED;
    }

    @Override
    public String getDmsProviderServiceName() {
        return DMS_PROVIDER_CMIS;
    }

    @Override
    public List<DocumentInfoAndDetail> getDocuments(String classId, long cardId) {
        logger.debug("get all attachments for classId = {} cardId = {}", classId, cardId);
        Session session = createSession();
        OperationContext context = session.createOperationContext();
        context.setMaxItemsPerPage(Integer.MAX_VALUE);
        session.setDefaultContext(context);
        Folder folder = getFolderOrNull(session, buildCmisPath(classId, cardId));
        if (folder == null) {
            return emptyList();
        } else {
            return list(folder.getChildren()).filter(Document.class::isInstance).map(Document.class::cast).map(this::toDocumentInfoAndDetail).sorted(DocumentInfoAndDetail::getFileName).collect(toList());
        }
    }

    @Override
    public List<DocumentInfoAndDetail> getDocumentVersions(String documentId) {
        logger.debug("get all attachment versions for documentId = {}", documentId);
        Session session = createSession();
        Document document = (Document) session.getObject(decodeString(checkNotBlank(documentId)));
        if (document == null || !document.isVersionable()) {
            return emptyList();
        } else {
            return document.getAllVersions().stream().map(this::toDocumentInfoAndDetail).collect(toList());
        }
    }

    @Override
    public DataHandler download(String documentId, @Nullable String version) {
        logger.debug("download document id = {} version = {}", documentId, version);
        Document document = (Document) createSession().getObject(decodeString(checkNotBlank(documentId)));
        if (isNotBlank(version)) {
            document = checkNotNull(document.getAllVersions().stream().filter(v -> equal(v.getVersionLabel(), version)).collect(toOptional()).orElse(null), "document not found for version = %s", version);
        }
        return newDataHandler(toByteArray(document.getContentStream().getStream()), document.getContentStreamMimeType(), document.getContentStreamFileName());
    }

    @Override
    public Optional<DataHandler> preview(String documentId) {
        logger.debug("get document preview for doc id = {}", documentId);
        Session session = createSession();
        OperationContext context = session.createOperationContext();
        context.setRenditionFilterString("cmis:thumbnail");
        session.setDefaultContext(context);
        Document cmisDocument = (Document) session.getObject(decodeString(checkNotBlank(documentId)));
        List<Rendition> list = cmisDocument.getRenditions();
        if (isNullOrEmpty(list)) {
            logger.debug("no preview available for document = {}", documentId);
            return Optional.empty();
        } else {
            logger.debug("available previews = {}", list);
            Rendition rendition = list.iterator().next(); //should contain only one record, as per rendition filter before
            logger.debug("selected preview = {}", rendition);
            return Optional.of(newDataHandler(toByteArray(rendition.getContentStream().getStream()), rendition.getContentStream().getMimeType(), rendition.getContentStream().getFileName()));
        }
    }

    @Override
    public DocumentInfoAndDetail getDocument(String documentId) {
        logger.debug("get document with id = {}", documentId);
        return doGetDocument(createSession(), documentId);
    }

    @Override
    public DocumentInfoAndDetail create(String classId, long cardId, DocumentData data) {
        logger.debug("create document for classId = {} cardId = {} fileName =< {} >", classId, cardId, data.getFilename());
        Session session = createSession();
        String path = buildCmisPath(classId, cardId);
        Folder folder = getFolderCreateIfNotExists(session, path);
        byte[] bytes = checkNotNull(data.getData());
        String mimeType = tika.detect(bytes);
        Document document = folder.createDocument(getProperties(session, data).with(OBJECT_TYPE_ID, CMIS_DOCUMENT, NAME, data.getFilename()).accept(m -> {
            if (hasSecondaryTypesSupport(session)) {
                m.put(SECONDARY_OBJECT_TYPE_IDS, list("P:cm:author", "P:cm:titled", "P:cmdbuild:classifiable"));
            }
        }), session.getObjectFactory().createContentStream(data.getFilename(), bytes.length, mimeType, new ByteArrayInputStream(bytes)), data.isMajorVersion() ? MAJOR : MINOR);
        return toDocumentInfoAndDetail(document);
    }

    @Override
    public DocumentInfoAndDetail update(String documentId, DocumentData data) {
        logger.debug("update document with id =< {} > fileName =< {} >", documentId, data.getFilename());
        Session session = createSession();
        Document document = (Document) session.getLatestDocumentVersion(decodeString(checkNotBlank(documentId)));
        if (data.hasData()) {
            document = (Document) session.getObject(document.checkOut());
            try {
                logger.debug("update document properties and content");
                documentId = encodeString(document.checkIn(data.isMajorVersion(), getProperties(session, data), session.getObjectFactory().createContentStream(data.getFilename(), data.getData().length, tika.detect(data.getData()), new ByteArrayInputStream(data.getData())), "").getId());
            } catch (Exception e) {
                document.cancelCheckOut();
                throw new DmsException(e, "cmis document update failed");
            }
        } else {
            logger.debug("update document properties only");
            documentId = encodeString(document.updateProperties(getProperties(session, data)).getId());
        }
        logger.debug("updated document with (final) id =< {} > fileName =< {} >", documentId, data.getFilename());
        return doGetDocument(session, documentId);
    }

    @Override
    public void delete(String documentId) {
        logger.debug("delete document with id = {}", documentId);
        ((Document) createSession().getObject(decodeString(checkNotBlank(documentId)))).delete();
    }

    @Override
    public List<String> queryDocuments(String fulltextQuery, String classId, @Nullable Long cardId) {
        Session session = createSession();
        Folder cardFolder;
        if (cardId == null) {
            cardFolder = getFolderOrNull(session, buildCmisPath(classId));
        } else {
            cardFolder = getFolderOrNull(session, buildCmisPath(classId, cardId));
        }
        if (cardFolder == null) {
            logger.info(format("Cannot locate a cmis folder for class %s and card %s ", classId, cardId));
            return emptyList();
        }
        String preparedQuery = format("SELECT cmis:objectId FROM cmis:document WHERE CONTAINS('%s') AND IN_TREE('%s')", fulltextQuery, cardFolder.getId());
        List<String> documentIds = list();
        ItemIterable<QueryResult> results = session.query(preparedQuery, false);
        for (QueryResult hit : results) {
            hit.getProperties().forEach(p -> documentIds.add(encodeString(toStringOrNull(p.getFirstValue()))));
        }
        return documentIds;
    }

    private DocumentInfoAndDetail doGetDocument(Session session, String documentId) {
        Document document = (Document) session.getObject(decodeString(checkNotBlank(documentId)));
        return toDocumentInfoAndDetail(document);
    }

//    private boolean isOk() {
//        try {
//            getRepository();
//            return true;
//        } catch (Exception ex) {
//            logger.warn("cmis dms service is NOT OK : {}", ex.toString());
//            return false;
//        }
//    }
    private String buildCmisPath(String classId, long cardId) {
        return toCmisPath(buildDocumentPath(dao.getClasse(classId), cardId));
    }

    private String buildCmisPath(String classId) {
        return toCmisPath(buildClassFolderPath(dao.getClasse(classId)));
    }

    private DocumentInfoAndDetail toDocumentInfoAndDetail(Document document) {
        String category = CmisDmsUtils.getProperty(document, CMIS_PROPERTY_CATEGORY);
        String author = CmisDmsUtils.getProperty(document, CMIS_PROPERTY_AUTHOR);
        return DocumentInfoAndDetailImpl.builder()
                .withDocumentId(encodeString(document.getId()))
                .withAuthor(author)
                .withCategory(category)
                .withCreated(toDateTime(document.getCreationDate()))
                .withDescription(document.getDescription())
                .withFileName(document.getName())
                .withFileSize(ltZeroToNull((Integer) toIntExact(document.getContentStreamLength())))
                .withHash(CmCollectionUtils.nullToEmpty(document.getContentStreamHashes()).stream().findFirst().map(h -> format("%s:%s", firstNotBlank(h.getAlgorithm(), "unknown"), firstNotBlank(h.getHash(), "unknown"))).orElse("unknown"))//TODO improve this
                .withMimeType(firstNonNull(document.getContentStreamMimeType(), "application/octet-stream"))
                .withModified(toDateTime(document.getLastModificationDate()))
                .withVersion(document.getVersionLabel())
                .build();
    }

    private Session createSession() {
        return getRepository().createSession();
    }

    private String toCmisPath(String path) {
        return buildUrlPath(configuration.getCmisPath(), path);
    }

    private Folder getFolderCreateIfNotExists(Session session, String path) {
        ItemLock lock = lockService.aquireLockOrWait(key("dms.cmis", path), LS_REQUEST).getLock();
        try {
            return CmisDmsUtils.getFolderCreateIfNotExists(session, path);
        } finally {
            lockService.releaseLock(lock);
        }
    }

    private FluentMap<String, Object> getProperties(Session session, DocumentData data) {
        return mapOf(String.class, Object.class).accept(m -> {
            if (hasSecondaryTypesSupport(session)) {
                m.put(
                        CMIS_PROPERTY_AUTHOR, toStringOrNull(data.getAuthor()),
                        CMIS_PROPERTY_DESCRIPTION, nullToEmpty(data.getDescription()),
                        CMIS_PROPERTY_CATEGORY, nullToEmpty(data.getCategory())
                );
            }
        });
//        FluentMap<String, Object> map = map();

//        CmisVersion version = session.getRepositoryInfo().getCmisVersion();
//        if (version.compareTo(CMIS_1_0) <= 0) {
//            logger.warn("secondary types not supported by cmis version =< {} >", version);
//        } else {
//            map.put(
//                    CMIS_PROPERTY_AUTHOR, toStringOrNull(data.getAuthor()),
//                    //                    SECONDARY_OBJECT_TYPE_IDS, new ArrayList(set("P:cm:author", "P:cm:titled", "P:cmdbuild:classifiable").accept(s -> {
//                    //                        if (cmisDocument != null) {
//                    //                            cmisDocument.getSecondaryTypes().stream().map(ObjectType::getId).forEach(s::add);
//                    //                        }
//                    //                    })),
//                    "cm:description", nullToEmpty(data.getDescription()),
//                    CMIS_PROPERTY_CATEGORY, nullToEmpty(data.getCategory())
//            );
//        }
//        if (cmisDocument == null) {
//            map.put(OBJECT_TYPE_ID, CMIS_DOCUMENT, NAME, data.getFilename());
//        }
//        logger.debug("built cmis document properties: \n{}\n", mapToLoggableString(map));
//
//        return map;
    }

    private boolean hasSecondaryTypesSupport(Session session) {
        CmisVersion version = session.getRepositoryInfo().getCmisVersion();
        if (version.compareTo(CMIS_1_0) <= 0) {
            logger.warn("secondary types not supported by cmis version =< {} >", version);
            return false;
        } else {
            return true;
        }
    }

    private Repository getRepository() {
        return repository.get(this::doGetRepository);
    }

    private Repository doGetRepository() {
        return getCmisRepository(configuration);
    }

}
