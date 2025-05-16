/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.alfresco.migrator.inner;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import static com.google.common.collect.Iterables.getOnlyElement;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.io.IOException;
import static java.lang.Integer.parseInt;
import java.rmi.RemoteException;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import javax.annotation.Nullable;
import org.alfresco.webservice.authentication.AuthenticationFault;
import org.alfresco.webservice.repository.RepositoryServiceSoapPort;
import org.alfresco.webservice.types.Node;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.ResultSet;
import org.alfresco.webservice.types.ResultSetRow;
import org.alfresco.webservice.types.ResultSetRowNode;
import org.alfresco.webservice.types.Store;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.alfresco.webservice.util.Constants;
import static org.alfresco.webservice.util.Constants.PROP_DESCRIPTION;
import static org.alfresco.webservice.util.Constants.PROP_NAME;
import org.alfresco.webservice.util.WebServiceFactory;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.cmdbuild.utils.alfresco.migrator.AlfrescoSourceDocument;
import org.cmdbuild.utils.alfresco.migrator.AlfrescoSourceDocumentInfo;
import org.cmdbuild.utils.io.BigByteArray;
import org.cmdbuild.utils.io.BigByteArrayOutputStream;
import static org.cmdbuild.utils.io.CmIoUtils.getContentType;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrNull;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.lang.EventBusUtils.rethrowingEventBus;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlfrescoLegacySourceImpl implements AlfrescoSource {

    private static final String DEFAULT_STORE_ADDRESS = "SpacesStore";
    private static final Store STORE = new Store(Constants.WORKSPACE_STORE, DEFAULT_STORE_ADDRESS);

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String url, user, password, wsPath, ftpHost, ftpPath;
    private final Integer ftpPort, ftpDefaultTimeout, ftpSoTimeout, ftpDataTimeout, ftpSocketConnectTimeout;
    private final boolean ftpPassiveMode;

    private final RepositoryServiceSoapPort repository;
    private final LoadingCache<String, String> categoryUuidToCategoryName = CacheBuilder.newBuilder().build((new CacheLoader<String, String>() {
        @Override
        public String load(String key) throws Exception {
            return loadCategoryNameForUuid(key);
        }
    }));
    private final FTPClient ftpClient;
    private final EventBus eventBus = rethrowingEventBus();

    public AlfrescoLegacySourceImpl(Map<String, String> config) {
        try {

            this.url = checkNotBlank(config.get("ws.url")).trim();
            this.user = checkNotBlank(config.get("user")).trim();
            this.password = checkNotBlank(config.get("password")).trim();
            this.wsPath = checkNotBlank(config.get("ws.path")).trim();

            this.ftpHost = checkNotBlank(config.get("ftp.host")).trim();
            this.ftpPath = checkNotBlank(config.get("ftp.path")).trim();
            this.ftpPort = checkNotNullAndGtZero(parseInt(checkNotBlank(config.get("ftp.port")).trim()));
            this.ftpPassiveMode = (boolean) firstNotBlankOrNull(Boolean.parseBoolean(config.get("ftp.passiveMode")), false);
            this.ftpDefaultTimeout = config.get("ftp.defaultTimeout") == null ? null : parseInt(config.get("ftp.defaultTimeout"));
            this.ftpSoTimeout = config.get("ftp.soTimeout") == null ? null : parseInt(config.get("ftp.soTimeout"));
            this.ftpDataTimeout = config.get("ftp.dataTimeout") == null ? null : parseInt(config.get("ftp.dataTimeout"));
            this.ftpSocketConnectTimeout = config.get("ftp.connectionTimeout") == null ? null : parseInt(config.get("ftp.connectionTimeout"));

            logger.info("loading alfresco legacy source with url =< {} > and ws path =< {} >", url, wsPath);

            WebServiceFactory.setEndpointAddress(url);
            AuthenticationUtils.startSession(user, password);
            repository = checkNotNull(WebServiceFactory.getRepositoryService());

            ftpClient = new FTPClient();
            if (ftpDataTimeout != null) {
                ftpClient.setDataTimeout(ftpDataTimeout);
            }
            if (ftpDefaultTimeout != null) {
                ftpClient.setDefaultTimeout(ftpDefaultTimeout);
            }
            if (ftpSocketConnectTimeout != null) {
                ftpClient.setConnectTimeout(ftpSocketConnectTimeout);
            }
            ftpClient.setAutodetectUTF8(true);
            ftpClient.connect(ftpHost, ftpPort);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            if (ftpSoTimeout != null) {
                ftpClient.setSoTimeout(ftpSoTimeout);
            }
            if (ftpPassiveMode) {
                ftpClient.enterLocalPassiveMode();
            }
            checkArgument(ftpClient.login(user, password), "ftp login failed");
            checkArgument(ftpClient.changeWorkingDirectory(ftpPath), "failed to open ftp root folder =< %s >", ftpPath);

            logger.info("alfresco legacy source ready");

        } catch (AuthenticationFault ex) {
            throw runtime(ex);
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    @Override
    public void readSourceDocuments(Object listener, @Nullable java.util.function.Predicate<AlfrescoSourceDocumentInfo> filter) {
        readSourceDocumentInfos(new Object() {
            @Subscribe
            public void handleDocumentInfoFoundEvent(DocumentInfoFoundEvent event) {
                AlfrescoSourceDocumentInfo d = event.getDocumentInfo();
                if (filter == null || filter.test(d)) {
                    try {
                        String ftpFolder = ftpPath + "/" + d.getFolder(),
                                ftpFile = d.getName();
                        logger.debug("open ftp folder =< {} >", ftpFolder);
                        checkArgument(ftpClient.changeWorkingDirectory(ftpFolder), "failed to open ftp folder =< %s >", ftpFolder);
                        BigByteArrayOutputStream out = new BigByteArrayOutputStream();
                        logger.debug("fetch ftp file =< {} >", ftpFile);
                        checkArgument(ftpClient.retrieveFile(ftpFile, out), "failed to retrieve file =< %s > from path =< %s >", ftpFile, ftpFolder);
                        BigByteArray data = out.toBigByteArray();
                        logger.debug("retrieved document content = {} {} for document =< {} >", getContentType(data), byteCountToDisplaySize(data.length()), d.getPath());
                        AlfrescoSourceDocument document = new AlfrescoSourceDocumentImpl(d.getName(), d.getFolder(), d.getCategory(), d.getDescription(), d.getAuthor(), d.getProperties(), data);
                        eventBus.post(new DocumentFoundEventImpl(document));
                        ftpClient.changeWorkingDirectory(ftpPath);
                    } catch (IOException ex) {
                        throw runtime(ex);
                    }
                }
            }
        });
    }

    @Override
    public void readSourceDocumentInfos(Object listener) {
        eventBus.register(listener);
        try {
            new AlfrescoLegacyQueryHelper().readSourceDocumentInfos();
        } catch (RemoteException ex) {
            throw runtime(ex);
        } finally {
            eventBus.unregister(listener);
        }
    }

    @Override
    public void checkConnectionOk() {
        checkArgument(ftpClient.isConnected(), "ftp alfresco connection is NOT ok");//TODO improve this
    }

    private String loadCategoryNameForUuid(String categoryUuid) {
        try {
            Reference reference = new Reference(STORE, checkNotBlank(categoryUuid), null);
            Node node = getOnlyElement(list(repository.get(new Predicate(new Reference[]{reference}, STORE, null))));
            Map<String, Object> attributes = map(list(node.getProperties()), n -> n.getName(), n -> n.getIsMultiValue() ? list(firstNotNull(n.getValues(), new String[]{})) : n.getValue());
            logger.debug("category attributes = \n\n{}\n", mapToLoggableStringLazy(attributes));
            return checkNotBlank((String) attributes.get(PROP_NAME));
        } catch (RemoteException ex) {
            throw runtime(ex);
        }
    }

    @Override
    public void close() throws Exception {
        ftpClient.disconnect();
    }

    private class AlfrescoLegacyQueryHelper {

        public void readSourceDocumentInfos() throws RemoteException {
            readSourceDocumentInfos(emptyList());
        }

        private void readSourceDocumentInfos(List<String> path) throws RemoteException {

            String query = wsPath;
            if (!path.isEmpty()) {
                query += "/" + path.stream().map(v -> "cm:" + escapeQuery(v)).collect(joining("/"));
            }

            Reference reference = new Reference(STORE, null, query);
            logger.debug("execute alfresco legacy query for path =< {} >", query);
            handleQueryResult(path, repository.queryChildren(reference).getResultSet());
            logger.debug("processed all results for query path =< {} >", query);
            eventBus.post(new FolderProcessedEventImpl(Joiner.on("/").join(path)));
        }

        private void handleQueryResult(List<String> path, ResultSet resultSet) throws RemoteException {
            list(firstNotNull(resultSet.getRows(), new ResultSetRow[]{})).forEach(rethrowConsumer(r -> {
                ResultSetRowNode node = r.getNode();
                Map<String, String> attributes = map(list(r.getColumns()), n -> n.getName(), n -> n.getIsMultiValue() ? Joiner.on(",").join(list(firstNotNull(n.getValues(), new String[]{}))) : n.getValue());
                String name = attributes.get(PROP_NAME);
                logger.info("found node id =< {} > type =< {} > name =< {} >", node.getId(), node.getType(), name);
                logger.debug("node attributes = \n\n{}\n", mapToLoggableStringLazy(attributes));

                switch (node.getType()) {
                    case "{http://www.alfresco.org/model/content/1.0}folder":
                        readSourceDocumentInfos(list(path).with(name));
                        break;
                    case "{http://www.alfresco.org/model/content/1.0}content":
                        String description = attributes.get(PROP_DESCRIPTION),
                         author = attributes.get("{http://www.alfresco.org/model/content/1.0}author"),
                         category = attributes.get("{http://www.alfresco.org/model/content/1.0}categories");
                        if (isNotBlank(category)) {
                            category = category.replaceFirst("^" + Pattern.quote("workspace://SpacesStore/"), "");
                            category = categoryUuidToCategoryName.getUnchecked(category);
                        }

                        AlfrescoSourceDocumentInfo document = new AlfrescoSourceDocumentInfoImpl(name, Joiner.on("/").join(path), category, description, author, attributes);
                        logger.info("found document = {}", document.getPath());
                        eventBus.post(new DocumentInfoFoundEventImpl(document));
                        break;
                    default:
                        logger.info("found unknown node type =< {} > : skipping", node.getType());
                }
            }));
        }
    }

    public static String escapeQuery(String query) {
        return query.replaceAll(" ", "_x0020_");
    }
}
