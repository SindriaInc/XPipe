/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.sharepoint;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.activation.DataHandler;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.tika.Tika;
import org.cmdbuild.dms.DocumentData;
import static org.cmdbuild.dms.sharepoint.SharepointDmsUtils.escapeUrlPart;
import org.cmdbuild.exception.DmsException;
import org.cmdbuild.utils.date.CmDateUtils;
import org.cmdbuild.utils.io.CmHttpRequestException;
import org.cmdbuild.utils.io.CmIoUtils;
import org.cmdbuild.utils.io.HttpClientUtils;
import org.cmdbuild.utils.json.CmJsonUtils;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.toInt;
import org.cmdbuild.utils.lang.CmExceptionUtils;
import org.cmdbuild.utils.lang.CmInlineUtils;
import static org.cmdbuild.utils.lang.CmInlineUtils.flattenMaps;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.cmdbuild.utils.lang.CmNullableUtils;
import org.cmdbuild.utils.lang.CmPreconditions;
import org.cmdbuild.utils.lang.CmStringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SharepointGraphApiClientImpl implements SharepointGraphApiClient {

    private static final int UPLOAD_PART_SIZE = 327680 * 4; // the size of each byte range MUST be a multiple of 320 KiB (327,680 bytes)
    private static final int UPLOAD_SPLIT_THRESHOLD = UPLOAD_PART_SIZE * 2; //must be less than 4MB

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SharepointConfiguration config;
    private final BiConsumer<Map<String, Object>, DocumentData> customPropertySetter;
    private final Tika tika = new Tika();
    private String accessToken;
    private String refreshToken;
    private ZonedDateTime tokenRelease;
    private ZonedDateTime tokenExpiration;
    private final Supplier<String> driveId = Suppliers.memoize(this::acquireDriveId);
    private final Supplier<String> listId = Suppliers.memoize(this::acquireListId);
    private final Supplier<String> siteId = Suppliers.memoize(this::acquireSiteId);
    private final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    private boolean isOk = true;

    protected SharepointGraphApiClientImpl(SharepointConfiguration config) {
        this(config, (m, d) -> {
        });
    }

    protected SharepointGraphApiClientImpl(SharepointConfiguration config, BiConsumer<Map<String, Object>, DocumentData> customPropertySetter) {
        this.config = Preconditions.checkNotNull(config);
        this.customPropertySetter = Preconditions.checkNotNull(customPropertySetter);
    }

    @Override
    public void close() throws IOException {
        connectionManager.close();
    }

    @Override
    public synchronized boolean isOk() {
        if (!isOk) {
            return false;
        } else {
            try {
                checkOk();
                return true;
            } catch (CmHttpRequestException ex) {
                logger.warn("sharepoint helper is not ok = {}", ex.toString());
                return isOk = false;
            }
        }
    }

    @Override
    public synchronized void checkOk() {
        getResource("me");
        getResource(String.format("drives/%s/root/children", escapeUrlPart(getDriveId())));
        logger.debug("sharepoint graph api client is OK");
    }

    @Override
    public synchronized void checkRefreshToken() {
        switch (getAuthProtocol()) {
            case MSAZUREOAUTH2:
                if (tokenRelease != null && tokenExpiration != null && CmNullableUtils.isNotBlank(refreshToken) && CmNullableUtils.isNotBlank(accessToken)) {
                    long initialSeconds = Duration.between(tokenRelease, tokenExpiration).getSeconds();
                    long remainingSeconds = Duration.between(CmDateUtils.now(), tokenExpiration).getSeconds();
                    if (remainingSeconds < initialSeconds / 2 || remainingSeconds < 600) {
                        logger.debug("{} seconds remaining before token expiration, execute token refresh", remainingSeconds);
                        refreshMsAccessToken();
                    } else {
                        logger.debug("{} seconds remaining before token expiration, token is ok", remainingSeconds);
                    }
                }
                break;
            default:
                throw CmExceptionUtils.unsupported("unsupported sharepoint auth protocol =< %s >", config.getSharepointAuthProtocol());
        }
    }

    @Override
    public List<Map<String, Object>> listFolderContent(List<String> path) {
        return listFolderContent(path, false);
    }

    @Override
    public List<Map<String, Object>> listFolderContentCreateIfMissing(List<String> path) {
        return listFolderContent(path, true);
    }

    @Override
    public Map<String, Object> getItemByPath(List<String> path) {
        return Preconditions.checkNotNull(doGetItemByPath(path, false));
    }

    @Nullable
    @Override
    public Map<String, Object> getItemByPathOrNull(List<String> path) {
        return doGetItemByPath(path, true);
    }

    @Override
    public Map<String, Object> getItemById(String itemId, boolean includeVersions) {
        String resource = "drives/%s/items/%s?$expand=listItem($expand=fields)";
        if (includeVersions) {
            resource += ",versions";
        }
        Map<String, Object> item = CmInlineUtils.flattenMapsKeepOriginal(getResource(String.format(resource, escapeUrlPart(getDriveId()), escapeUrlPart(CmPreconditions.checkNotBlank(itemId)))));
        if (includeVersions) {
            Map<String, Map<String, Object>> driveItemVersions = CmMapUtils.map((List<Map<String, Object>>) item.get("versions"), m -> CmStringUtils.toStringNotBlank(m.get("id")), Function.identity());
            Map<String, Map<String, Object>> listItemVersions = CmMapUtils.map((List<Map<String, Object>>) getResource(String.format("sites/%s/lists/%s/items/%s?$expand=versions($expand=fields)", escapeUrlPart(getSiteId()), escapeUrlPart(getListId()), escapeUrlPart(CmStringUtils.toStringNotBlank(item.get("listItem___id"))))).get("versions"), m -> CmStringUtils.toStringNotBlank(m.get("id")), Function.identity());
            item = CmMapUtils.map(item).with("versions", CmCollectionUtils.set(driveItemVersions.keySet()).with(listItemVersions.keySet()).stream().map(v -> CmInlineUtils.flattenMapsKeepOriginal(CmMapUtils.map("listItemVersion", listItemVersions.getOrDefault(v, Collections.emptyMap()), "driveItemVersion", driveItemVersions.getOrDefault(v, Collections.emptyMap())))).collect(ImmutableList.toImmutableList()));
        }
        return item;
    }

    @Override
    public DataHandler getItemContent(String itemId) {
        return getContent(String.format(getGraphApiBaseUrl() + "drives/%s/items/%s/content", escapeUrlPart(getDriveId()), escapeUrlPart(CmPreconditions.checkNotBlank(itemId))));
    }

    @Override
    public DataHandler getItemContentByPath(List<String> path) {
        return getContent(String.format(getGraphApiBaseUrl() + "drives/%s/root:/%s:/content", escapeUrlPart(getDriveId()), path.stream().map(SharepointDmsUtils::escapeUrlPart).collect(Collectors.joining("/"))));
    }

    @Override
    public DataHandler getItemPreview(String itemId) {
        return getContent(String.format(getGraphApiBaseUrl() + "drives/%s/items/%s/thumbnails/0/medium/content", escapeUrlPart(getDriveId()), escapeUrlPart(CmPreconditions.checkNotBlank(itemId))));
    }

    @Override
    public DataHandler getVersionContent(String itemId, String version) {
        return getContent(String.format(getGraphApiBaseUrl() + "drives/%s/items/%s/versions/%s/content", escapeUrlPart(getDriveId()), escapeUrlPart(CmPreconditions.checkNotBlank(itemId)), escapeUrlPart(CmPreconditions.checkNotBlank(version))));
    }

    @Override
    public Map<String, Object> setItem(List<String> folder, DocumentData document) {
        listFolderContentCreateIfMissing(folder);
        return doSetItem(String.format("drives/%s/root:/%s:", escapeUrlPart(getDriveId()), CmCollectionUtils.list(folder).with(document.getFilename()).map(SharepointDmsUtils::escapeUrlPart).collect(Collectors.joining("/"))), document);
    }

    @Override
    public Map<String, Object> setItem(String documentId, DocumentData document) {
        return doSetItem(String.format("drives/%s/items/%s", escapeUrlPart(getDriveId()), escapeUrlPart(CmPreconditions.checkNotBlank(documentId))), document);
    }

    @Override
    public void deleteItemIfExists(List<String> path) {
        Optional.ofNullable(getItemByPathOrNull(path)).ifPresent(this::doDeleteItem);
    }

    @Override
    public void deleteItem(List<String> path) {
        doDeleteItem(getItemByPath(path));
    }

    @Override
    public void deleteItem(String documentId) {
        doDeleteItem(getItemById(documentId));
    }

    @Override
    public List<String> queryPathWithFulltext(String path, String textQuery, boolean orNull) {
        List<String> documentIds = CmCollectionUtils.list();
        try {
            String token = getAccessToken();
            String body = String.format("{\"requests\":[{\"entityTypes\":[\"driveItem\"],\"query\":{\"queryString\":\"%s AND isDocument=true AND path:\\\"%s/%s\\\"\"},\"fields\":[\"title\",\"id\"]}]}", textQuery, config.getSharepointUrl(), path);
            String fullQueryUrl = config.getSharepointGraphApiBaseUrl() + "search/query";
            HttpPost request = new HttpPost(fullQueryUrl);
            request.setHeader("Authorization", String.format("Bearer %s", token));
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
            logger.debug("get resource =< {} >", request);
            Map<String, Object> map = CmJsonUtils.fromJson(HttpClientUtils.checkStatusAndReadResponse(getHttpClient().execute(request)), CmJsonUtils.MAP_OF_OBJECTS);
            logger.debug("got response = \n\n{}\n", CmStringUtils.mapToLoggableString(map));
            JSONObject jsonObject = new JSONObject(map).getJSONArray("value").getJSONObject(0).getJSONArray("hitsContainers").getJSONObject(0);
            if (jsonObject.has("hits")) {
                jsonObject.getJSONArray("hits").forEach(e -> {
                    documentIds.add(((JSONObject) e).getJSONObject("resource").getString("id"));
                });
            }
            logger.debug("Found < {} > attachments with matching content", documentIds.size());
        } catch (CmHttpRequestException ex) {
            if (Strings.nullToEmpty(getErrorCodeSafe(ex)).equalsIgnoreCase("itemNotFound") && orNull) {
                logger.debug("item not found for path =< {} >", path);
                return null;
            } else {
                throw ex;
            }
        } catch (IOException e) {
            throw new DmsException(e);
        }
        return documentIds;
    }

    @Nullable
    private Map<String, Object> doGetItemByPath(List<String> path, boolean orNull) {
        return Optional.ofNullable(doGetResource(String.format("drives/%s/root:/%s?$expand=listItem($expand=fields)", escapeUrlPart(getDriveId()), path.stream().map(SharepointDmsUtils::escapeUrlPart).collect(Collectors.joining("/"))), orNull)).map(CmInlineUtils::flattenMapsKeepOriginal).orElse(null);
    }

    private void doDeleteItem(Map<String, Object> item) {
        try {
            String documentId = CmStringUtils.toStringNotBlank(item.get("id"));
            String token = getAccessToken();
            HttpDelete request = new HttpDelete(String.format(getGraphApiBaseUrl() + "drives/%s/items/%s", escapeUrlPart(getDriveId()), escapeUrlPart(CmPreconditions.checkNotBlank(documentId))));
            request.setHeader("Authorization", String.format("Bearer %s", token));
            logger.debug("delete resource =< {} >", request);
            HttpClientUtils.checkStatusAndClose(getHttpClient().execute(request));
            logger.debug("delete OK");
            String parentId = CmStringUtils.toStringOrNull(item.get("parentReference___id"));
            if (config.autodeleteEmptyDirectories() && isNotBlank(parentId) && toInt(flattenMaps(getResource(String.format("drives/%s/items/%s", escapeUrlPart(getDriveId()), escapeUrlPart(parentId)))).get("folder___childCount")) == 0) {
                deleteItem(parentId);
            }
        } catch (IOException e) {
            throw new DmsException(e);
        }
    }

    private DataHandler getContent(String path) {
        try {
            String token = getAccessToken();
            HttpGet request = new HttpGet(path);
            request.setHeader("Authorization", String.format("Bearer %s", token));
            logger.debug("get resource =< {} >", request);
            CloseableHttpResponse response = getHttpClient().execute(request);
            HttpClientUtils.checkStatus(response);
            HttpEntity entity = Preconditions.checkNotNull(response.getEntity());
            byte[] data = CmIoUtils.toByteArray(entity.getContent());
            String contentType = Optional.ofNullable(entity.getContentType()).map(Header::getValue).orElseGet(() -> tika.detect(data));
            logger.debug("got data = {} bytes {}", data.length, entity.getContentType());
            logger.debug("headers = \n\n{}\n", CmStringUtils.mapToLoggableStringLazy(CmMapUtils.map(CmCollectionUtils.list(response.getAllHeaders()), Header::getName, Header::getValue)));
            EntityUtils.consumeQuietly(entity);
            return CmIoUtils.newDataHandler(data, contentType);
        } catch (IOException e) {
            throw new DmsException(e);
        }
    }

    private byte[] getItemContent(List<String> path) {
        return Preconditions.checkNotNull(getItemContentOrNull(path), "item content not found for path =< %s >", path);
    }

    @Nullable
    private byte[] getItemContentOrNull(List<String> path) {
        try {
            String token = getAccessToken();
            HttpGet request = new HttpGet(String.format(getGraphApiBaseUrl() + "drives/%s/root:/%s:/content", escapeUrlPart(getDriveId()), path.stream().map(SharepointDmsUtils::escapeUrlPart).collect(Collectors.joining("/"))));
            request.setHeader("Authorization", String.format("Bearer %s", token));
            logger.debug("get resource =< {} >", request);
            CloseableHttpResponse response = getHttpClient().execute(request);
            HttpClientUtils.checkStatus(response);
            HttpEntity entity = Preconditions.checkNotNull(response.getEntity());
            byte[] data = CmIoUtils.toByteArray(entity.getContent());
            logger.debug("got data = {} bytes {}", data.length, entity.getContentType());
            logger.debug("headers = \n\n{}\n", CmStringUtils.mapToLoggableStringLazy(CmMapUtils.map(CmCollectionUtils.list(response.getAllHeaders()), Header::getName, Header::getValue)));
            EntityUtils.consumeQuietly(entity);
            return data;
        } catch (CmHttpRequestException ex) {
            if (Strings.nullToEmpty(getErrorCodeSafe(ex)).equalsIgnoreCase("itemNotFound")) {
                logger.debug("item not found for path =< {} >", Joiner.on("/").join(path));
                return null;
            } else {
                throw ex;
            }
        } catch (IOException e) {
            throw new DmsException(e);
        }
    }

    private Map<String, Object> doSetItem(String path, DocumentData document) {
        try {
            String token = getAccessToken();
            Map<String, Object> item;
            if (!document.hasData()) {
                item = CmInlineUtils.flattenMapsKeepOriginal(getResource(path + "?$expand=listItem($expand=fields)"));
            } else if (document.getData().length < UPLOAD_SPLIT_THRESHOLD) {
                HttpPut request = new HttpPut(getGraphApiBaseUrl() + path + "/content");
                request.setHeader("Authorization", String.format("Bearer %s", token));
                request.setEntity(new ByteArrayEntity(document.getData(), ContentType.create(tika.detect(document.getData()))));
                logger.debug("put resource =< {} >", request);
                item = CmJsonUtils.fromJson(HttpClientUtils.checkStatusAndReadResponse(getHttpClient().execute(request)), CmJsonUtils.MAP_OF_OBJECTS);
                logger.debug("got response = \n\n{}\n", CmStringUtils.mapToLoggableStringLazy(item));
                item = getItemById(CmStringUtils.toStringNotBlank(item.get("id")));
            } else {
                HttpEntityEnclosingRequestBase request = new HttpPost(getGraphApiBaseUrl() + path + "/createUploadSession");
                request.setHeader("Authorization", String.format("Bearer %s", token));
                request.setEntity(new StringEntity(CmJsonUtils.toJson(CmMapUtils.map("item", CmMapUtils.map("@microsoft.graph.conflictBehavior", "replace", "name", document.getFilename()))), ContentType.APPLICATION_JSON));
                logger.debug("post resource =< {} >", request);
                Map<String, Object> uploadInfo = CmJsonUtils.fromJson(HttpClientUtils.checkStatusAndReadResponse(getHttpClient().execute(request)), CmJsonUtils.MAP_OF_OBJECTS);
                logger.debug("got response = \n\n{}\n", CmStringUtils.mapToLoggableStringLazy(uploadInfo));
                String uploadUrl = CmStringUtils.toStringNotBlank(uploadInfo.get("uploadUrl"));
                logger.debug("upload url =< {} >", uploadUrl);
                InputStream in = new ByteArrayInputStream(document.getData());
                byte[] buffer = new byte[UPLOAD_PART_SIZE];
                int offset = 0;
                int count;
                int size = document.getData().length;
                item = Collections.emptyMap();
                while (offset < size) {
                    count = in.read(buffer);
                    logger.debug("upload range {}-{} of total {} bytes", offset, offset + count - 1, size);
                    request = new HttpPut(uploadUrl);
                    request.setHeader("Content-Range", String.format("bytes %s-%s/%s", offset, offset + count - 1, size));
                    request.setEntity(new ByteArrayEntity(buffer, 0, count));
                    item = CmJsonUtils.fromJson(HttpClientUtils.checkStatusAndReadResponse(getHttpClient().execute(request)), CmJsonUtils.MAP_OF_OBJECTS);
                    offset += count;
                }
                logger.debug("upload completed");
                item = getItemById(CmStringUtils.toStringNotBlank(item.get("id")));
            }
            String itemId = CmStringUtils.toStringNotBlank(item.get("id"));
            String listItemId = CmStringUtils.toStringNotBlank(item.get("listItem___id"));
            Map<String, Object> meta = CmMapUtils.mapOf(String.class, Object.class).accept(m -> customPropertySetter.accept(m, document));
            if (!meta.isEmpty()) {
                HttpPatch request = new HttpPatch(String.format(getGraphApiBaseUrl() + "sites/%s/lists/%s/items/%s/fields", escapeUrlPart(getSiteId()), escapeUrlPart(getListId()), escapeUrlPart(listItemId)));
                logger.debug("patch resource =< {} > with payload =\n\n{}\n", request, CmStringUtils.mapToLoggableStringLazy(meta));
                request.setHeader("Authorization", String.format("Bearer %s", token));
                request.setEntity(new StringEntity(CmJsonUtils.toJson(meta), ContentType.APPLICATION_JSON));
                HttpClientUtils.checkStatusAndClose(getHttpClient().execute(request));
            }
            return getItemById(itemId);
        } catch (IOException e) {
            throw new DmsException(e);
        }
    }

    private List<Map<String, Object>> listFolderContent(List<String> path, boolean createIfMissing) {
        try {
            Map<String, Object> resource = getResource(String.format("drives/%s/root:/%s:/children?$expand=listItem($expand=fields)", escapeUrlPart(getDriveId()), path.stream().map(SharepointDmsUtils::escapeUrlPart).collect(Collectors.joining("/"))));
            return CmCollectionUtils.list((List<Map<String, Object>>) resource.get("value")).map(CmInlineUtils::flattenMapsKeepOriginal);
        } catch (CmHttpRequestException ex) {
            if (Strings.nullToEmpty(getErrorCodeSafe(ex)).equalsIgnoreCase("itemNotFound")) {
                logger.debug("folder not found for path =< {} >", Joiner.on("/").join(path));
                if (createIfMissing) {
                    createFolder(path);
                }
                return Collections.emptyList();
            } else {
                throw ex;
            }
        }
    }

    private void createFolder(List<String> path) {
        if (path.size() > 1) {
            listFolderContent(path.subList(0, path.size() - 1), true);
        }
        logger.debug("create folder =< {} >", Joiner.on("/").join(path));
        Object payload = CmMapUtils.map("name", path.get(path.size() - 1), "folder", Collections.emptyMap(), "@microsoft.graph.conflictBehavior", "fail");
        if (path.size() == 1) {
            postResource(payload, "drives/%s/root/children", escapeUrlPart(getDriveId()));
        } else {
            postResource(payload, "drives/%s/root:/%s:/children", escapeUrlPart(getDriveId()), path.subList(0, path.size() - 1).stream().map(SharepointDmsUtils::escapeUrlPart).collect(Collectors.joining("/")));
        }
    }

    @Nullable
    private String getErrorCodeSafe(CmHttpRequestException ex) {
        try {
            if (!ex.hasJsonContent()) {
                return null;
            } else {
                Map<String, Object> map = ex.getContentAsJsonSafe();
                if (map.containsKey("error") && map.get("error") instanceof Map) {
                    return CmStringUtils.toStringOrNull(((Map) map.get("error")).get("code"));
                } else {
                    return null;
                }
            }
        } catch (Exception exx) {
            logger.warn("error reading json response", exx);
            return null;
        }
    }

    private String getGraphApiBaseUrl() {
        return CmPreconditions.checkNotBlank(config.getSharepointGraphApiBaseUrl(), "missing sharepoint graph api base url");
    }

    private String getDriveId() {
        return driveId.get();
    }

    private String getListId() {
        return listId.get();
    }

    private String getSiteId() {
        return siteId.get();
    }

    private synchronized String getAccessToken() {
        if (StringUtils.isBlank(accessToken)) {
            acquireAccessToken();
        }
        return accessToken;
    }

    private String acquireDriveId() {
        String sharepointUrl = CmPreconditions.trimAndCheckNotBlank(config.getSharepointUrl(), "missing required sharepoint url param");
        Matcher matcher = Pattern.compile("(https?://([^/]+)/sites/([^/]+)(/([^/]+))?)/?").matcher(sharepointUrl);
        Preconditions.checkArgument(matcher.matches(), "invalid format for sharepoint url =< %s >", sharepointUrl);
        String hostname = CmPreconditions.checkNotBlank(matcher.group(2));
        String site = CmPreconditions.checkNotBlank(matcher.group(3));
        String webUrl = CmPreconditions.checkNotBlank(matcher.group(1));
        Map<String, Object> driveInfo = ((List<Map<String, Object>>) getResource(String.format("sites/%s:/sites/%s:/drives", escapeUrlPart(hostname), escapeUrlPart(site))).get("value")).stream().filter(d -> CmStringUtils.toStringOrEmpty(d.get("webUrl")).equalsIgnoreCase(webUrl)).collect(CmCollectionUtils.onlyElement("drive not found for hostname =< %s >, site =< %s >, weburl =< %s >", hostname, site, webUrl));
        logger.debug("drive info = \n\n{}\n", CmStringUtils.mapToLoggableString(driveInfo));
        String id = CmPreconditions.checkNotBlank(CmStringUtils.toStringOrNull(driveInfo.get("id")), "missing drive id");
        logger.debug("found drive id =< {} >", id);
        return id;
    }

    private String acquireListId() {
        //TODO fix duplicate code
        String sharepointUrl = CmPreconditions.trimAndCheckNotBlank(config.getSharepointUrl(), "missing required sharepoint url param");
        Matcher matcher = Pattern.compile("(https?://([^/]+)/sites/([^/]+)(/([^/]+))?)/?").matcher(sharepointUrl);
        Preconditions.checkArgument(matcher.matches(), "invalid format for sharepoint url =< %s >", sharepointUrl);
        String hostname = CmPreconditions.checkNotBlank(matcher.group(2));
        String site = CmPreconditions.checkNotBlank(matcher.group(3));
        String webUrl = CmPreconditions.checkNotBlank(matcher.group(1));
        Map<String, Object> driveInfo = ((List<Map<String, Object>>) getResource(String.format("sites/%s:/sites/%s:/lists", escapeUrlPart(hostname), escapeUrlPart(site))).get("value")).stream().filter(d -> CmStringUtils.toStringOrEmpty(d.get("webUrl")).equalsIgnoreCase(webUrl)).collect(CmCollectionUtils.onlyElement("list not found for hostname =< %s >, site =< %s >, weburl =< %s >", hostname, site, webUrl));
        logger.debug("list info = \n\n{}\n", CmStringUtils.mapToLoggableString(driveInfo));
        String id = CmPreconditions.checkNotBlank(CmStringUtils.toStringOrNull(driveInfo.get("id")), "missing list id");
        logger.debug("found list id =< {} >", id);
        return id;
    }

    private String acquireSiteId() {
        //TODO fix duplicate code
        String sharepointUrl = CmPreconditions.trimAndCheckNotBlank(config.getSharepointUrl(), "missing required sharepoint url param");
        Matcher matcher = Pattern.compile("(https?://([^/]+)/sites/([^/]+)(/([^/]+))?)/?").matcher(sharepointUrl);
        Preconditions.checkArgument(matcher.matches(), "invalid format for sharepoint url =< %s >", sharepointUrl);
        String hostname = CmPreconditions.checkNotBlank(matcher.group(2));
        String site = CmPreconditions.checkNotBlank(matcher.group(3));
        String webUrl = CmPreconditions.checkNotBlank(matcher.group(1));
        Map<String, Object> info = getResource(String.format("sites/%s:/sites/%s:", escapeUrlPart(hostname), escapeUrlPart(site)));
        logger.debug("site info = \n\n{}\n", CmStringUtils.mapToLoggableString(info));
        String id = CmPreconditions.checkNotBlank(CmStringUtils.toStringOrNull(info.get("id")), "missing site id");
        logger.debug("found site id =< {} >", id);
        return id;
    }

    @Nullable
    private Map<String, Object> getResourceOrNull(String path) throws CmHttpRequestException {
        return doGetResource(path, true);
    }

    private Map<String, Object> getResource(String path) throws CmHttpRequestException {
        return doGetResource(path, false);
    }

    @Nullable
    private Map<String, Object> doGetResource(String path, boolean orNull) throws CmHttpRequestException {
        try {
            String token = getAccessToken();
            HttpGet request = new HttpGet(getGraphApiBaseUrl() + path);
            request.setHeader("Authorization", String.format("Bearer %s", token));
            request.setHeader("Accept", "application/json");
            logger.debug("get resource =< {} >", request);
            Map<String, Object> map = CmJsonUtils.fromJson(HttpClientUtils.checkStatusAndReadResponse(getHttpClient().execute(request)), CmJsonUtils.MAP_OF_OBJECTS);
            logger.debug("got response = \n\n{}\n", CmStringUtils.mapToLoggableString(map));
            return map;
        } catch (CmHttpRequestException ex) {
            if (Strings.nullToEmpty(getErrorCodeSafe(ex)).equalsIgnoreCase("itemNotFound") && orNull) {
                logger.debug("item not found for path =< {} >", path);
                return null;
            } else {
                throw ex;
            }
        } catch (IOException e) {
            throw new DmsException(e);
        }
    }

    private Map<String, Object> postResource(Object payload, String path, Object... params) throws CmHttpRequestException {
        try {
            String token = getAccessToken();
            HttpPost request = new HttpPost(getGraphApiBaseUrl() + String.format(path, params));
            request.setHeader("Authorization", String.format("Bearer %s", token));
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(CmJsonUtils.toJson(payload), ContentType.APPLICATION_JSON));
            logger.debug("post resource =< {} >", request);
            Map<String, Object> map = CmJsonUtils.fromJson(HttpClientUtils.checkStatusAndReadResponse(getHttpClient().execute(request)), CmJsonUtils.MAP_OF_OBJECTS);
            logger.debug("got response = \n\n{}\n", CmStringUtils.mapToLoggableString(map));
            return map;
        } catch (IOException e) {
            throw new DmsException(e);
        }
    }

    private SharepointDmsAuthProtocol getAuthProtocol() {
        return CmConvertUtils.parseEnum(config.getSharepointAuthProtocol(), SharepointDmsAuthProtocol.class);
    }

    private synchronized void acquireAccessToken() {
        logger.debug("get sharepoint access token with protocol =< {} >", config.getSharepointAuthProtocol());
        switch (getAuthProtocol()) {
            case MSAZUREOAUTH2:
                acqireMsAccessToken();
                break;
            default:
                throw CmExceptionUtils.unsupported("unsupported sharepoint auth protocol =< %s >", config.getSharepointAuthProtocol());
        }
    }

    private synchronized void refreshMsAccessToken() {
        acquireMsAccessToken(CmMapUtils.map("grant_type", "refresh_token", "refresh_token", CmPreconditions.checkNotBlank(refreshToken)));
    }

    private synchronized void acqireMsAccessToken() {
        acquireMsAccessToken(CmMapUtils.map("grant_type", "password", "username", CmPreconditions.checkNotBlank(config.getSharepointUser(), "missing sharepoint auth usename"), "password", CmPreconditions.checkNotBlank(config.getSharepointPassword(), "missing sharepoint auth user password")));
    }

    private synchronized void acquireMsAccessToken(Map<String, Object> payload) {
        try (final CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost tokenRequest = new HttpPost(String.format("%s/%s/oauth2/v2.0/token", CmPreconditions.checkNotBlank(config.getSharepointAuthServiceUrl(), "missing sharepoint auth service url"), CmPreconditions.firstNotBlank(config.getSharepointAuthTenantId(), "common")));
            CmMapUtils.FluentMap<String, Object> tokenRequestPayload = (CmMapUtils.FluentMap) CmMapUtils.map("client_id", CmPreconditions.checkNotBlank(config.getSharepointAuthClientId(), "missing sharepoint auth client id"), "client_secret", CmPreconditions.checkNotBlank(config.getSharepointAuthClientSecret(), "missing sharepoint auth client secret"), "scope", "openid offline_access https://graph.microsoft.com/.default").with(payload);
            tokenRequest.setEntity(new UrlEncodedFormEntity(tokenRequestPayload.toList((k, v) -> (NameValuePair) new BasicNameValuePair((String) k, (String) v)), StandardCharsets.UTF_8.name()));
            logger.debug("execute sharepoint oauth token request =< {} > with payload =\n\n{}\n", tokenRequest, CmStringUtils.mapToLoggableStringLazy(tokenRequestPayload));
            Map<String, String> tokenResponse = CmJsonUtils.fromJson(HttpClientUtils.checkStatusAndReadResponse(client.execute(tokenRequest)), CmJsonUtils.MAP_OF_STRINGS);
            logger.debug("received sharepoint oauth token response = \n\n{}\n", CmStringUtils.mapToLoggableStringLazy(tokenResponse));
            String token = CmPreconditions.checkNotBlank(tokenResponse.get("access_token"), "missing sharepoint oauth access token in token response");
            logger.debug("acquired sharepoint oauth access token =< {} >", token);
            DecodedJWT jwt = JWT.decode(token);
            Map<String, String> info = CmMapUtils.map(jwt.getClaims()).mapValues(c -> c.asString());
            logger.debug("token info = \n\n{}\n", CmStringUtils.mapToLoggableStringLazy(info));
            Set<String> privileges = CmCollectionUtils.set(Splitter.on(" ").omitEmptyStrings().trimResults().split(Strings.nullToEmpty(info.get("scp"))));
            logger.debug("token privileges = {}", privileges);
            Preconditions.checkArgument(CmCollectionUtils.list(privileges).map(String::toLowerCase).contains("Files.ReadWrite.All".toLowerCase()), "missing required access privilege `Files.ReadWrite.All`");
            Preconditions.checkArgument(CmCollectionUtils.list(privileges).map(String::toLowerCase).contains("Sites.Read.All".toLowerCase()), "missing required access privilege `Sites.Read.All`");
            String refresh = CmPreconditions.checkNotBlank(tokenResponse.get("refresh_token"), "missing refresh token");
            ZonedDateTime release = CmDateUtils.now();
            ZonedDateTime expiration = release.plusSeconds(CmConvertUtils.toInt(tokenResponse.get("expires_in")));
            logger.debug("token will expire at = {}", CmDateUtils.toIsoDateTime(expiration));
            logger.debug("access token ok");
            accessToken = token;
            refreshToken = refresh;
            tokenRelease = release;
            tokenExpiration = expiration;
        } catch (Exception ex) {
            throw new DmsException(ex, "unable to authenticate sharepoint client");
        }
    }

    private CloseableHttpClient getHttpClient() {
        return HttpClients.custom().setConnectionManager(connectionManager).build();
    }

}
