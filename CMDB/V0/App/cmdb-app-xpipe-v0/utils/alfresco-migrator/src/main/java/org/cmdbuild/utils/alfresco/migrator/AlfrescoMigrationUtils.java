/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.alfresco.migrator;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import org.cmdbuild.utils.alfresco.migrator.inner.AlfrescoLegacySourceImpl;
import org.cmdbuild.utils.alfresco.migrator.inner.AlfrescoSource;
import org.cmdbuild.utils.alfresco.migrator.inner.AlfrescoSourceDocumentImpl;
import org.cmdbuild.utils.alfresco.migrator.inner.AlfrescoSourceDocumentInfoImpl;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterators.limit;
import static com.google.common.collect.Iterators.size;
import com.google.common.collect.Ordering;
import static com.google.common.collect.Streams.stream;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import static java.util.function.Predicate.not;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import static org.apache.chemistry.opencmis.commons.PropertyIds.NAME;
import static org.apache.chemistry.opencmis.commons.PropertyIds.OBJECT_TYPE_ID;
import static org.apache.chemistry.opencmis.commons.PropertyIds.SECONDARY_OBJECT_TYPE_IDS;
import static org.apache.chemistry.opencmis.commons.enums.VersioningState.MAJOR;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import static org.apache.commons.io.FileUtils.touch;
import org.apache.commons.lang.StringUtils;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.dms.cmis.CmisDmsRepositoryConfig;
import static org.cmdbuild.dms.cmis.CmisDmsUtils.CMIS_DOCUMENT;
import static org.cmdbuild.dms.cmis.CmisDmsUtils.CMIS_FOLDER;
import static org.cmdbuild.dms.cmis.CmisDmsUtils.CMIS_PROPERTY_AUTHOR;
import static org.cmdbuild.dms.cmis.CmisDmsUtils.CMIS_PROPERTY_CATEGORY;
import static org.cmdbuild.dms.cmis.CmisDmsUtils.CMIS_PROPERTY_DESCRIPTION;
import static org.cmdbuild.dms.cmis.CmisDmsUtils.getCmisRepository;
import org.cmdbuild.utils.alfresco.migrator.inner.AlfrescoSource.DocumentFoundEvent;
import org.cmdbuild.utils.alfresco.migrator.inner.AlfrescoSource.DocumentInfoFoundEvent;
import org.cmdbuild.utils.alfresco.migrator.inner.AlfrescoSource.FolderProcessedEvent;
import org.cmdbuild.utils.alfresco.migrator.inner.DocumentFoundEventImpl;
import org.cmdbuild.utils.alfresco.migrator.inner.DocumentInfoFoundEventImpl;
import org.cmdbuild.utils.alfresco.migrator.inner.FolderProcessedEventImpl;
import org.cmdbuild.utils.io.BigByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.getContentType;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.toBigByteArray;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.lazyString;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExecutorUtils.sleepSafe;
import static org.cmdbuild.utils.lang.CmExecutorUtils.unsafe;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrNull;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.lang.EventBusUtils.rethrowingEventBus;
import static org.cmdbuild.utils.url.CmUrlUtils.decodeUrlParams;
import static org.cmdbuild.utils.url.CmUrlUtils.getUrlPathFilename;
import static org.cmdbuild.utils.url.CmUrlUtils.getUrlPathParent;
import static org.codehaus.plexus.util.StringUtils.isBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

public class AlfrescoMigrationUtils {

    public static AlfrescoMigrationHelper newHelper(Map<String, String> config) {
        return new AlfrescoMigrationHelperImpl(config);
    }

    public static String getConfigFileExample() {
        return checkNotBlank(readToString(AlfrescoMigrationUtils.class.getResourceAsStream("/org/cmdbuild/utils/alfresco/migrator/example_config.properties")));
    }

    public static String buildPath(String folderPath, String itemName) {
        return new File(folderPath, itemName).toString();
    }

    private static class AlfrescoMigrationHelperImpl implements AlfrescoMigrationHelper {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final Map<String, String> config;
        private final AlfrescoSource source;
        private final AlfrescoDestination destination;
        private final Map<String, String> pathMapping, categoryMapping;
        private final String includePattern, excludePattern, defaultCategory;
        private final boolean filterFolders, useIndex, continueOnError, deleteEmptyFolders;
        private final File indexFile;
        private final Set<String> processedFiles = set();

        private AlfrescoMigrationCallback listener;

        private AlfrescoMigrationHelperImpl(Map<String, String> config) {
            this.config = map(config).immutable();
            logger.info("loaded alfresco migration helper with config = \n\n{}\n", mapToLoggableStringLazy(config));

            this.source = buildSource();
            this.destination = buildDestinationOrNull();

            if (isNotBlank(config.get("migration.mapping"))) {
                File file = new File(config.get("migration.mapping"));
                pathMapping = map();
                try (CsvListReader reader = new CsvListReader(new InputStreamReader(new FileInputStream(file)), CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE)) {
                    List<String> line;
                    while ((line = reader.read()) != null) {
                        pathMapping.put(checkNotBlank(line.get(0)).trim(), checkNotBlank(line.get(1)).trim());
                    }
                } catch (IOException ex) {
                    throw runtime(ex);
                }
                logger.info("loaded document mapping, {} mappins ready", pathMapping.size());
            } else {
                pathMapping = emptyMap();
            }

            if (isNotBlank(config.get("migration.categoryMapping"))) {
                categoryMapping = decodeUrlParams(config.get("migration.categoryMapping").trim());
                logger.info("loaded category mapping = \n\n{}\n", mapToLoggableString(categoryMapping));
            } else {
                categoryMapping = emptyMap();
            }

            includePattern = trimToNull(config.get("migration.include"));
            excludePattern = trimToNull(config.get("migration.exclude"));
            filterFolders = toBooleanOrDefault(config.get("migration.filterFolders"), false);
            continueOnError = toBooleanOrDefault(config.get("migration.continueOnError"), false);
            deleteEmptyFolders = toBooleanOrDefault(config.get("migration.deleteEmptyFolders"), false);
            defaultCategory = trimToNull(config.get("migration.defaultCategory"));
            indexFile = Optional.ofNullable(trimToNull(config.get("migration.indexFile"))).map(File::new).orElse(null);
            useIndex = indexFile != null;
            if (useIndex) {
                unsafe(() -> indexFile.createNewFile());
                checkArgument(indexFile.exists() && indexFile.isFile(), "invalid index file =< %s >", indexFile);
                try (BufferedReader reader = new BufferedReader(new FileReader(indexFile, UTF_8))) {
                    reader.lines().filter(not(StringUtils::isBlank)).map(String::trim).map(AlfrescoMigrationHelperImpl::normalizePath).forEach(processedFiles::add);
                } catch (IOException ex) {
                    throw runtime(ex);
                }
                logger.info("loaded index file =< {} >, {} records already processed", indexFile.getAbsolutePath(), processedFiles.size());
            }
        }

        @Override
        public void listSourceDocuments() {
            source.readSourceDocumentInfos(new Object() {

                @Subscribe
                public void handleDocumentInfoFoundEvent(DocumentInfoFoundEvent event) {

                    if (shouldProcessPath(event.getDocumentInfo().getPath()) && listener != null) {
                        listener.processigDocument(event.getDocumentInfo());
                        indexProcessedPath(event.getDocumentInfo().getPath());
                    }
                }

                @Subscribe
                public void handleFolderProcessedEvent(FolderProcessedEvent event) {
                    indexProcessedPath(event.getPath());
                }
            });
        }

        @Override
        public void migrateDocuments() {
            checkHasDestination();
            source.readSourceDocuments(new Object() {

                @Subscribe
                public void handleDocumentFoundEvent(DocumentFoundEvent event) {

                    AlfrescoSourceDocument sourceDocument = event.getDocument();
                    if (listener != null) {
                        listener.processigDocument(sourceDocument);
                    }
                    try {
                        AlfrescoSourceDocument document = sourceDocument;
                        if (pathMapping.containsKey(document.getFolder())) {
                            document = document.withFolder(pathMapping.get(document.getFolder()));
                        }
                        destination.putDocument(document);
                        indexProcessedPath(sourceDocument.getPath());
                    } catch (Exception ex) {
                        if (continueOnError) {
                            logger.error("error processing document =< {} >", sourceDocument.getPath(), ex);
                            sleepSafe(300);
                            source.checkConnectionOk();
                        } else {
                            throw ex;
                        }
                    }
                }

                @Subscribe
                public void handleFolderProcessedEvent(FolderProcessedEvent event) {
                    indexProcessedPath(event.getPath());
                }
            }, d -> shouldProcessPath(d.getPath()));
        }

        @Override
        public AlfrescoMigrationHelper withListener(AlfrescoMigrationCallback listener) {
            this.listener = listener;
            return this;
        }

        @Nullable
        @Override
        public String getMappedCategory(@Nullable String sourceCategory) {
            return firstNotBlankOrNull(categoryMapping.getOrDefault(sourceCategory, sourceCategory), defaultCategory);
        }

        private void indexProcessedPath(String path) {
            if (isNotBlank(normalizePath(path)) && useIndex && !processedFiles.contains(normalizePath(path))) {
                try (FileWriter writer = new FileWriter(indexFile, UTF_8, true)) {
                    writer.append(checkNotBlank(path)).append("\n");
                } catch (IOException ex) {
                    throw runtime(ex);
                }
                processedFiles.add(normalizePath(path));
            }
        }

        private boolean shouldProcessPath(String path) {
            if (isNotBlank(includePattern) && !path.matches(includePattern)) {
                logger.debug("skip item =< {} > (does not match include pattern)", path);
                return false;
            } else if (isNotBlank(excludePattern) && path.matches(excludePattern)) {
                logger.debug("skip item =< {} > (does match exclude pattern)", path);
                return false;
            } else if (alreadyProcessed(path)) {
                return false;
            } else {
                return true;
            }
        }

        private boolean alreadyProcessed(String path) {
            if (isNotBlank(normalizePath(path)) && useIndex && processedFiles.contains(normalizePath(path))) {
                logger.debug("skip item =< {} > (already processed, found in index)", path);
                return true;
            } else {
                return false;
            }
        }

        private static String normalizePath(String path) {
            return nullToEmpty(path).replaceAll("^/*|/*$", "");
        }

        private void checkHasDestination() {
            checkNotNull(destination, "destination not configured");
        }

        private AlfrescoSource buildSource() {
            switch (checkNotBlank(config.get("source.type"))) {
                case "cmis":
                    return new AlfrescoCmisSourceImpl(map(config).filterKeys(k -> k.startsWith("source.cmis.")).mapKeys(k -> k.replaceFirst("source.cmis.", "")));
                case "alfresco":
                    return new AlfrescoLegacySourceImpl(map(config).filterKeys(k -> k.startsWith("source.alfresco.")).mapKeys(k -> k.replaceFirst("source.alfresco.", "")));
                default:
                    throw new UnsupportedOperationException("unsupported source type");
            }
        }

        @Nullable
        private AlfrescoDestination buildDestinationOrNull() {
            switch (nullToEmpty(config.get("destination.type")).trim()) {
                case "":
                    return null;
                case "cmis":
                    return new AlfrescoCmisDestinationImpl(map(config).filterKeys(k -> k.startsWith("destination.cmis.")).mapKeys(k -> k.replaceFirst("destination.cmis.", "")));
                default:
                    throw new UnsupportedOperationException("unsupported destination type");
            }
        }

        @Override
        public void close() throws Exception {
            source.close();
            destination.close();
        }

        private class AlfrescoCmisDestinationImpl implements AlfrescoDestination {

            private final Logger logger = LoggerFactory.getLogger(getClass());

            private final String url, user, password, path;
            private final Repository repository;
            private final Session session;

            public AlfrescoCmisDestinationImpl(Map<String, String> cmisConfig) {
                this.url = checkNotBlank(cmisConfig.get("url"));
                this.user = checkNotBlank(cmisConfig.get("user"));
                this.password = checkNotBlank(cmisConfig.get("password"));
                this.path = checkNotBlank(cmisConfig.get("path"));
                logger.info("loading alfresco cmis destination with url =< {} > and base path =< {} >", url, path);

                CmisDmsRepositoryConfig repoConfig = CmisDmsRepositoryConfig.builder()
                        .withUsername(user)
                        .withUrl(url)
                        .withPassword(password)
                        .withReadTimeout(Integer.MAX_VALUE).build();

                repository = getCmisRepository(repoConfig);
                session = checkNotNull(repository.createSession());
                logger.info("cmis destination ready");
            }

            @Override
            public void putDocument(AlfrescoSourceDocument document) {
                logger.info("upload document = {}", document.getPath());
                String folderName = buildPath(path, document.getFolder());
                Folder folder = getFolderCreateIfNotExists(folderName);
                String mimeType = getContentType(document.getData()),
                        mappedCategory = getMappedCategory(document.getCategory());
                if (stream(folder.getChildren()).filter(c -> equal(c.getName(), document.getName())).findAny().isPresent()) {
                    logger.warn("document already present for folder =< {} > name =< {} >, skipping", folderName, document.getName());
                } else {
                    Map<String, Object> map = map(
                            CMIS_PROPERTY_AUTHOR, document.getAuthor(),
                            SECONDARY_OBJECT_TYPE_IDS, list("P:cm:author", "P:cm:titled", "P:cmdbuild:classifiable"),
                            CMIS_PROPERTY_DESCRIPTION, document.getDescription(),
                            CMIS_PROPERTY_CATEGORY, mappedCategory,
                            OBJECT_TYPE_ID, CMIS_DOCUMENT,
                            NAME, document.getName());

                    Document destinationDocument = folder.createDocument(map, session.getObjectFactory().createContentStream(document.getName(), document.getData().length(), mimeType, document.getData().toInputStream()), MAJOR);
                    logger.info("uploaded document = {}", destinationDocument.getId());
                }
            }

            private Folder getFolderCreateIfNotExists(String path) {
                try {
                    return (Folder) session.getObjectByPath(path);
                } catch (CmisObjectNotFoundException e) {
                    String parentPath = getUrlPathParent(path), name = getUrlPathFilename(path);
                    Folder parentFolder = (Folder) getFolderCreateIfNotExists(parentPath);
                    logger.debug("create cmis folder =< {} >", path);
                    parentFolder.createFolder(map(
                            OBJECT_TYPE_ID, CMIS_FOLDER,
                            NAME, name
                    ));
                    return (Folder) session.getObjectByPath(path);
                }
            }

            @Override
            public void close() throws Exception {
            }
        }

        private class AlfrescoCmisSourceImpl implements AlfrescoSource {

            private final Logger logger = LoggerFactory.getLogger(getClass());

            private final EventBus eventBus = rethrowingEventBus();

            private final String url, user, password, basePath;
            private final Repository repository;
            private final Session session;

            public AlfrescoCmisSourceImpl(Map<String, String> config) {
                this.url = checkNotBlank(config.get("url"));
                this.user = checkNotBlank(config.get("user"));
                this.password = checkNotBlank(config.get("password"));
                this.basePath = checkNotBlank(config.get("path"));
                logger.info("loading alfresco cmis source with url =< {} > and base path =< {} >", url, basePath);
                CmisDmsRepositoryConfig repoConfig = CmisDmsRepositoryConfig.builder()
                        .withUsername(user)
                        .withUrl(url)
                        .withPassword(password)
                        .withReadTimeout(Integer.MAX_VALUE).build();
                repository = getCmisRepository(repoConfig);
                session = checkNotNull(repository.createSession());
                logger.info("cmis source ready");
            }

            @Override
            public void readSourceDocumentInfos(Object listener) {
                logger.info("reading source documents (info)");
                eventBus.register(listener);
                try {
                    forEachDocument(d -> eventBus.post(new DocumentInfoFoundEventImpl(readSourceDocument(d, false))));
                } finally {
                    eventBus.unregister(listener);
                }
            }

            @Override
            public void readSourceDocuments(Object listener, @Nullable Predicate<AlfrescoSourceDocumentInfo> filter) {
                logger.info("reading source documents (info and data)");
                eventBus.register(listener);
                try {
                    forEachDocument(d -> {
                        if (filter == null || filter.test(readSourceDocument(d, false))) {
                            eventBus.post(new DocumentFoundEventImpl(readSourceDocument(d, true)));
                        }
                    });
                } finally {
                    eventBus.unregister(listener);
                }
            }

            @Override
            public void checkConnectionOk() {
                try {
                    ItemIterable<CmisObject> children = getRootFolder().getChildren();
                    size(limit(children.iterator(), 10));//TODO improve this
                } catch (Exception ex) {
                    throw new IllegalArgumentException("cmis dms connection is NOT ok", ex);
                }
            }

            private <T> T readSourceDocument(CmisDocumentWithPath documentWithPath, boolean getData) {
                Document document = documentWithPath.getDocument();
                Map<String, String> properties = document.getProperties().stream().sorted(Ordering.natural().onResultOf(Property::getId)).collect(toMap(Property::getId, p -> p.isMultiValued() ? p.getValuesAsString() : p.getValueAsString()));

                String absFolder = documentWithPath.getFolderPath(),
                        name = document.getName(),
                        relativeFolder = relativePath(absFolder),
                        category = properties.get(CMIS_PROPERTY_CATEGORY),
                        description = document.getDescription(),
                        author = properties.get(CMIS_PROPERTY_AUTHOR);

                if (getData) {
                    BigByteArray data = toBigByteArray(document.getContentStream().getStream());
                    logger.info("downloaded document = {}", buildPath(absFolder, name));
                    return (T) new AlfrescoSourceDocumentImpl(name, relativeFolder, category, description, author, properties, data);
                } else {
                    return (T) new AlfrescoSourceDocumentInfoImpl(name, relativeFolder, category, description, author, properties);
                }
            }

            private void forEachDocument(Consumer<CmisDocumentWithPath> consumer) {
                new IteratorHelper(consumer).handleItem("", getRootFolder());
            }

            private Folder getRootFolder() {
                logger.debug("open root folder =< {} >", basePath);
                Folder baseFolder = (Folder) session.getObjectByPath(basePath);
                return checkNotNull(baseFolder);
            }

            private String relativePath(String absPath) {
                return absPath.replaceFirst("/?" + Pattern.quote(basePath.replaceAll("^/|/$", "")) + "/?", "");
            }

            private class IteratorHelper {

                private final Consumer<CmisDocumentWithPath> consumer;
                private final OperationContext folderListOperationContext;

                public IteratorHelper(Consumer<CmisDocumentWithPath> consumer) {
                    this.consumer = checkNotNull(consumer);
                    folderListOperationContext = session.createOperationContext();
                    folderListOperationContext.setMaxItemsPerPage(1000);
                    folderListOperationContext.setOrderBy("cmis:name ASC");
                    folderListOperationContext.setCacheEnabled(false);
                }

                public boolean handleItem(String folderPath, CmisObject item) {
                    boolean shouldRestart = false;
                    if (item instanceof Document) {
                        Document document = (Document) item;
                        String path = buildPath(folderPath, item.getName());
                        if (shouldProcessPath(relativePath(path))) {
                            logger.debug("found document =< {} >", path);
                            consumer.accept(new CmisDocumentWithPath(document, folderPath));
                        }
                    } else if (item instanceof Folder) {
                        Folder folder = (Folder) item;
                        String path = folder.getPath();
                        if (!alreadyProcessed(relativePath(path)) && (filterFolders == false || isBlank(relativePath(path)) || shouldProcessPath(relativePath(path)))) {
                            logger.debug("found folder =< {} >", path);
                            logger.trace("folder properties =\n{}", lazyString(() -> mapToLoggableString(folder.getProperties().stream().sorted(Ordering.natural().onResultOf(Property::getId)).collect(toMap(Property::getId, p -> p.isMultiValued() ? p.getValuesAsString() : p.getValueAsString())))));
                            shouldRestart = handleChilds(folder);
                            eventBus.post(new FolderProcessedEventImpl(relativePath(path)));
                        }
                    }
                    return shouldRestart;
                }

                private boolean handleChilds(Folder folder) {
                    boolean shouldRestart = false;
                    String path = folder.getPath();
                    logger.debug("list children of folder =< {} >", path);
                    ItemIterable<CmisObject> children = folder.getChildren(folderListOperationContext);
                    Iterator<CmisObject> iterator = children.iterator();
                    if (iterator.hasNext()) {
                        logger.debug("processing approx {} folder children, current page size = {}", children.getTotalNumItems(), children.getPageNumItems());
                        while (iterator.hasNext()) {
                            if (handleItem(path, iterator.next())) {
                                shouldRestart = true;
                            }
                        }
                        if (shouldRestart) {
                            logger.debug("deleted items: reset and restart processing of folder =< {} >", path);
                            shouldRestart = handleChilds(folder);
                        }
                    } else if (deleteEmptyFolders) {
                        logger.debug("delete empty folder =< {} >", path);
                        folder.delete();
                        shouldRestart = true;
                    }
                    return shouldRestart;
                }

            }

            @Override
            public void close() throws Exception {
            }

        }

    }

    interface AlfrescoDestination extends AutoCloseable {

        void putDocument(AlfrescoSourceDocument document);

    }

    private static class CmisDocumentWithPath {

        private final Document document;
        private final String folderPath;

        public CmisDocumentWithPath(Document document, String folderPath) {
            this.document = checkNotNull(document);
            this.folderPath = checkNotBlank(folderPath);
        }

        public Document getDocument() {
            return document;
        }

        public String getFolderPath() {
            return folderPath;
        }

    }

}
