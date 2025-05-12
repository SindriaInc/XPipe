package org.cmdbuild.dms.sharepoint;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.MoreCollectors.toOptional;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import static java.util.stream.Collectors.joining;

import javax.activation.DataHandler;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.dao.DaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dms.DmsConfiguration.DMS_CONFIG_PROVIDER;
import org.cmdbuild.dms.DocumentData;
import org.cmdbuild.dms.DocumentDataImpl;
import org.cmdbuild.dms.inner.DmsProviderService;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.dms.inner.DocumentInfoAndDetailImpl;
import static org.cmdbuild.dms.inner.DocumentPathUtils.buildDocumentPathList;
import static org.cmdbuild.dms.inner.DocumentPathUtils.buildClassFolderPathList;
import static org.cmdbuild.dms.sharepoint.SharepointDmsUtils.SHAREPOINT_ENTRY_NAME;
import org.cmdbuild.scheduler.ScheduledJob;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.io.HttpClientUtils.closeQuietly;
import org.cmdbuild.utils.lang.CmCollectionUtils.FluentList;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.toInt;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmInlineUtils.unflattenMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;
import org.cmdbuild.minions.MinionComponent;
import org.cmdbuild.minions.MinionHandler;
import org.cmdbuild.minions.MinionHandlerImpl;
import static org.cmdbuild.minions.MinionUtils.buildMinionRuntimeStatusChecker;

@Component
public class SharepointDmsProviderService implements DmsProviderService, MinionComponent {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final SharepointDmsConfiguration config;

    private final MinionHandler minionHandler;
    private final AtomicReference<SharepointGraphApiClient> helperHolder = new AtomicReference<>();

    public SharepointDmsProviderService(DaoService dao, SharepointDmsConfiguration config) {
        this.dao = checkNotNull(dao);
        this.config = checkNotNull(config);
        this.minionHandler = MinionHandlerImpl.builder()
                .withName("DMS_ SharePoint Online")
                .withEnabledChecker(() -> config.isEnabled(getDmsProviderServiceName()))
                .withStatusChecker(buildMinionRuntimeStatusChecker(() -> helperHolder.get() != null, this::checkService))
                .reloadOnConfigs(SharepointDmsConfiguration.class, DMS_CONFIG_PROVIDER)
                .build();
    }

    @Override
    public MinionHandler getMinionHandler() {
        return minionHandler;
    }

    @Override
    public void start() {
        doWithHelper((SharepointGraphApiClient h) -> h.setItem(buildBasePath(), DocumentDataImpl.builder().withFilename("README.txt").withData("\nroot folder used by cmdbuild sharepoint dms driver\n\nDO NOT DELETE THIS FILE\n".getBytes()).build()));
        logger.info("dms service ready");
    }

    @Override
    public void stop() {
        cleanup();
    }

    @Override
    public boolean isServiceOk() {
        return isReady();
    }

    @Override
    public void checkService() {
        doWithHelper((SharepointGraphApiClient h) -> h.checkOk());
    }

    @Override
    public String getDmsProviderServiceName() {
        return DMS_PROVIDER_SHAREPOINT_ONLINE;
    }

    private boolean isOk() {
        try {
            checkService();
            return true;
        } catch (Exception ex) {
            logger.warn("sharepoint dms service is NOT OK : {}", ex.toString());
            return false;
        }
    }

    @ScheduledJob(value = "0 */10 * * * ?", persistRun = false) //run every 10 minutes
    public void refreshExpiredToken() {
        if (isEnabled()) {
            synchronized (helperHolder) {
                logger.debug("check sharepoint dms token");
                if (helperHolder.get() != null) {
                    helperHolder.get().checkRefreshToken();
                }
            }
        }
    }

    @Override
    public DocumentInfoAndDetail getDocument(String documentId) {
        return doWithHelper((SharepointGraphApiClient helper) -> parseDocument(helper.getItemById(checkNotBlank(documentId))));
    }

    @Override
    public List<DocumentInfoAndDetail> getDocuments(String classId, long cardId) {
        return doWithHelper((SharepointGraphApiClient helper) -> helper.listFolderContent(buildPath(classId, cardId)).stream().filter(SharepointDmsUtils::isFile).map(d -> parseDocument(d)).collect(toImmutableList()));
    }

    @Override
    public List<DocumentInfoAndDetail> getDocumentVersions(String documentId) {
        return doWithHelper((SharepointGraphApiClient helper) -> {
            Map<String, Object> item = helper.getItemById(documentId, true);
            DocumentInfoAndDetail document = parseDocument(item);
            return list(document).accept(l -> ((List<Map<String, Object>>) item.get("versions")).stream()
                    .filter(v -> !equal(v.get("id"), document.getVersion()))
                    .map(v -> {
                        logger.debug("found version = \n\n{}\n", mapToLoggableStringLazy(v));
                        return DocumentInfoAndDetailImpl.copyOf(document)
                                .withVersion(toStringNotBlank(v.get("driveItemVersion___id")))
                                .withFileSize(toInt(v.get("driveItemVersion___size")))
                                .withHash("unknown")//TODO (?)
                                .withModified(toDateTime(v.get("driveItemVersion___lastModifiedDateTime")))
                                .withAuthor(firstNotBlank(config.hasSharepointCustomAuthorColumn() ? toStringOrEmpty(v.get(format("listItemVersion___fields___%s", config.getSharepointCustomAuthorColumn()))) : "", "unknown"))
                                .withDescription(config.hasSharepointCustomDescriptionColumn() ? toStringOrEmpty(v.get(format("listItemVersion___fields___%s", config.getSharepointCustomDescriptionColumn()))) : "")
                                .withCategory(config.hasSharepointCustomCategoryColumn() ? toStringOrEmpty(v.get(format("listItemVersion___fields___%s", config.getSharepointCustomCategoryColumn()))) : "")
                                .build();
                    }).forEach(l::add));
        });
    }

    @Override
    public DocumentInfoAndDetail create(String classId, long cardId, DocumentData data) {
        return doWithHelper((SharepointGraphApiClient helper) -> parseDocument(helper.setItem(buildPath(classId, cardId), data)));
    }

    @Override
    public DocumentInfoAndDetail update(String documentId, DocumentData data) {
        return doWithHelper((SharepointGraphApiClient helper) -> parseDocument(helper.setItem(documentId, data)));
    }

    @Override
    public DataHandler download(String documentId, @Nullable String version) {
        DocumentInfoAndDetail document = getDocument(documentId);
        DataHandler content = doWithHelper((SharepointGraphApiClient helper) -> {
            if (isBlank(version) || equal(version, document.getVersion())) {
                return helper.getItemContent(documentId);
            } else {
                return helper.getVersionContent(documentId, version);
            }
        });
        return newDataHandler(toByteArray(content), document.getMimeType(), document.getFileName());
    }

    @Override
    public void delete(String documentId) {
        doWithHelper((SharepointGraphApiClient helper) -> helper.deleteItem(documentId));
    }

    @Override
    public Optional<DataHandler> preview(String documentId) {
        try {
            return Optional.ofNullable(doWithHelper((SharepointGraphApiClient helper) -> helper.getItemPreview(documentId)));
        } catch (Exception ex) {
            logger.warn(marker(), "error retrieving preview for documentId =< {} >", documentId, ex);
            return Optional.empty();
        }
    }

    @Override
    public DocumentInfoAndDetail createLink(DocumentInfoAndDetail document, List<String> targetAbsolutePath) {
        return doWithHelper(helper -> {
            DataHandler data = helper.getItemContent(document.getDocumentId());
            List<String> folder = targetAbsolutePath.subList(0, targetAbsolutePath.size() - 1);
            String filename = targetAbsolutePath.get(targetAbsolutePath.size() - 1);
            return parseDocument(helper.setItem(folder, DocumentDataImpl.builder().withData(data).withFilename(filename).build()));
        });
    }

    @Override
    public void deleteLink(List<String> targetAbsolutePath) {
        doWithHelper(helper -> {
            helper.deleteItemIfExists(targetAbsolutePath);
        });
    }

    @Override
    public List<String> queryDocuments(String fulltextQuery, String classId, @Nullable Long cardId) {
        return doWithHelper(helper -> {
            FluentList<String> searchPath;
            if (cardId == null) {
                searchPath = buildPath(classId);
            } else {
                searchPath = buildPath(classId, cardId);
            }
            return helper.queryPathWithFulltext(searchPath.stream().map(SharepointDmsUtils::escapeUrlPart).collect(joining("/")), fulltextQuery, true);
        });
    }

    private FluentList<String> buildPath(String classId, long cardId) {
        return buildBasePath().with(buildDocumentPathList(dao.getClasse(classId), cardId));
    }

    private FluentList<String> buildPath(String classId) {
        return buildBasePath().with(buildClassFolderPathList(dao.getClasse(classId)));
    }

    private DocumentInfoAndDetail parseDocument(Map<String, Object> d) {
        logger.debug("found document = \n\n{}\n", mapToLoggableStringLazy(d));
        return DocumentInfoAndDetailImpl.builder()
                .withAuthor(firstNotBlank(config.hasSharepointCustomAuthorColumn() ? toStringOrEmpty(d.get(format("listItem___fields___%s", config.getSharepointCustomAuthorColumn()))) : "", "unknown"))
                .withDescription(config.hasSharepointCustomDescriptionColumn() ? toStringOrEmpty(d.get(format("listItem___fields___%s", config.getSharepointCustomDescriptionColumn()))) : "")
                .withCategory(config.hasSharepointCustomCategoryColumn() ? toStringOrEmpty(d.get(format("listItem___fields___%s", config.getSharepointCustomCategoryColumn()))) : "")
                .withCreated(toDateTime(d.get("createdDateTime")))
                .withDocumentId(toStringNotBlank(d.get("id")))
                .withFileName(toStringNotBlank(d.get(SHAREPOINT_ENTRY_NAME)))
                .withFileSize(toInt(d.get("size")))
                .withHash(unflattenMap(d, "file___hashes").entrySet().stream().limit(1).map(e -> format("%s:%s", e.getKey(), e.getValue())).collect(toOptional()).orElse("unknown"))
                .withMimeType(firstNotBlank(toStringOrEmpty(d.get("file___mimeType")), "application/octet-stream"))
                .withModified(toDateTime(d.get("lastModifiedDateTime")))
                .withVersion(toStringNotBlank(d.get("listItem___fields____UIVersionString")))
                .build();
    }

    private void doWithHelper(Consumer<SharepointGraphApiClient> fun) {
        doWithHelper((h) -> {
            fun.accept(h);
            return null;
        });
    }

    private <T> T doWithHelper(Function<SharepointGraphApiClient, T> fun) {
        SharepointGraphApiClient helper;
        synchronized (helperHolder) {
            if (helperHolder.get() == null) {
                helperHolder.set(buildHelper());
            }
            helper = helperHolder.get();
        }
        try {
            helper.checkRefreshToken();
            return fun.apply(helper);
        } catch (Exception ex) {
            logger.debug("sharepoint dms error, checking client");
            if (helper.isOk()) {
                throw new DaoException(ex);
            } else {
                logger.debug("sharepoint dms error, caused by invalid client", ex);
                logger.warn("sharepoint dms request failed for invalid client, reset client and retry: {}", ex.toString());
                synchronized (helperHolder) {
                    if (helperHolder.get() == helper) {
                        helperHolder.set(null);
                        closeQuietly(helper);
                        helperHolder.set(buildHelper());
                    }
                    helper = helperHolder.get();
                }
                return fun.apply(helper);
            }
        }
    }

    private void cleanup() {
        synchronized (helperHolder) {
            if (helperHolder.get() != null) {
                logger.debug("close sharepoint client helper");
                closeQuietly(helperHolder.get());
                helperHolder.set(null);
            }
        }
    }

    private SharepointGraphApiClient buildHelper() {
        SharepointGraphApiClient helper = new SharepointGraphApiClientImpl(config, this::setCustomProperties);
        try {
            helper.checkOk();
            return helper;
        } catch (RuntimeException ex) {
            closeQuietly(helper);
            throw ex;
        }
    }

    private void setCustomProperties(Map<String, Object> m, DocumentData document) {
        if (config.hasSharepointCustomAuthorColumn()) {
            m.put(config.getSharepointCustomAuthorColumn(), nullToEmpty(document.getAuthor()));
        }
        if (config.hasSharepointCustomCategoryColumn()) {
            m.put(config.getSharepointCustomCategoryColumn(), nullToEmpty(document.getCategory()));
        }
        if (config.hasSharepointCustomDescriptionColumn()) {
            m.put(config.getSharepointCustomDescriptionColumn(), nullToEmpty(document.getDescription()));
        }
    }

    private FluentList<String> buildBasePath() {
        return list(Splitter.on("/").omitEmptyStrings().splitToList(nullToEmpty(config.getSharepointPath())));
    }

}
