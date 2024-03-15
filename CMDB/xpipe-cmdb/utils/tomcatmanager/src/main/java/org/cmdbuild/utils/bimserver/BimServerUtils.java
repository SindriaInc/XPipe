/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.bimserver;
 
import com.fasterxml.jackson.databind.JsonNode;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.File;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import java.util.List;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.cmdbuild.bim.utils.BimserverConfig;
import static org.cmdbuild.utils.io.CmIoUtils.fetchFileWithCache;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.cmdbuild.utils.tomcatmanager.TomcatBuilder;
import org.cmdbuild.utils.tomcatmanager.TomcatConfig;
import org.cmdbuild.utils.tomcatmanager.TomcatManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BimServerUtils {

    public static BimserverConfig createAndStartBimserver() {
        return createAndStartBimserver(new File(tempDir(), "bimserver_tomcat_" + randomId(4)).getAbsolutePath());
    }

    public static BimserverConfig createAndStartBimserver(String path) {
        TomcatConfig tomcatConfig = TomcatConfig.builder().withTomcatInstallDir(path).build();
        TomcatManager tomcatManager = new TomcatManager(tomcatConfig);
        tomcatManager.build();
        File bimserverWarFile = fetchFileWithCache("b1c5ceac8fad9440be750bbaf05b6416aa3ebdb5", "https://github.com/opensourceBIM/BIMserver/releases/download/v1.5.138/bimserverwar-1.5.138.war");
        new TomcatBuilder(tomcatConfig).deployWar(format("%s AS bimserver", bimserverWarFile.getAbsolutePath()));
        tomcatManager.startTomcat();
        tomcatManager.waitForTomcatStartup();
        String bimserverUrl = format("http://localhost:%s/bimserver", tomcatManager.getConfig().getHttpPort());
        BimserverConfig bimserverConfig = new BimserverConfigImpl(bimserverUrl, "bimserver.admin@cmdbuild.org", randomId(), tomcatManager.getConfig().getInstallDir().getAbsolutePath());
        new BimserverConfigurator(bimserverConfig).configureBimserver();
        tomcatManager.stopFollowingLogs();
        return bimserverConfig;
    }

    public static void stopAndDestroyBimserver(BimserverConfig bimserverConfig) {
        stopAndDestroyBimserver(bimserverConfig.getTomcatPath());
    }

    public static void stopAndDestroyBimserver(String location) {
        new TomcatManager(TomcatConfig.builder().withTomcatInstallDir(location).build()).stopAndCleanup();
    }

    private static class BimserverConfigImpl implements BimserverConfig {

        final String bimserverUrl;

        private final String adminUsername, adminPassword, tomcatPath;

        public BimserverConfigImpl(String bimserverUrl, String adminUsername, String adminPassword, String tomcatPath) {
            this.bimserverUrl = checkNotBlank(bimserverUrl);
            this.adminUsername = checkNotBlank(adminUsername);
            this.adminPassword = checkNotBlank(adminPassword);
            this.tomcatPath = checkNotBlank(tomcatPath);
        }

        @Override
        public String getBimserverUrl() {
            return bimserverUrl;
        }

        @Override
        public String getAdminUsername() {
            return adminUsername;
        }

        @Override
        public String getAdminPassword() {
            return adminPassword;
        }

        @Override
        public String getTomcatPath() {
            return tomcatPath;
        }

    }

    private static class BimserverConfigurator {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final BimserverConfig bimserverConfig;
        private final String bimserverApiUrl;

        private String authToken;

        public BimserverConfigurator(BimserverConfig bimserverConfig) {
            this.bimserverConfig = checkNotNull(bimserverConfig);
            bimserverApiUrl = bimserverConfig.getBimserverUrl() + "/json";
        }

        public void configureBimserver() {

            String serverState = callApiMethod("AdminInterface", "getServerInfo", emptyMap()).get("response").get("result").get("serverState").asText();
            checkArgument(equal(serverState, "NOT_SETUP"), "invalid server state =< %s >", serverState);

            logger.info("setup admin account and bimserver system config");
            callApiMethod("AdminInterface", "setup", map(
                    "siteAddress", bimserverConfig.getBimserverUrl(),
                    "serverName", "MyServer",//TODO
                    "serverDescription", "",
                    "serverIcon", "",//TODO
                    "adminName", "Administrator",
                    "adminUsername", bimserverConfig.getAdminUsername(),
                    "adminPassword", bimserverConfig.getAdminPassword()
            ));

            logger.info("bimserver login");
            authToken = checkNotBlank(callApiMethod("AuthInterface", "login", map(
                    "username", bimserverConfig.getAdminUsername(),
                    "password", bimserverConfig.getAdminPassword()
            )).get("response").get("result").asText(), "login failed");

            JsonNode extendedDataSchemaList = callApiMethod("ServiceInterface", "getAllRepositoryExtendedDataSchemas", map("usePre", true)).get("response").get("result");
            for (JsonNode element : extendedDataSchemaList) {
                logger.info("install extended data schema =< {} >", element.get("name").asText());
                callApiMethod("ServiceInterface", "addExtendedDataSchema", map("extendedDataSchema", element));
            }

            callApiMethod("SettingsInterface", "setPluginStrictVersionChecking", map("strict", true));

            list(
                    "bimviews",
                    "bimsurfer3",
                    "ifcopenshellplugin",
                    "ifcplugins",
                    "binaryserializers",
                    "console",
                    "gltf",
                    "mergers"
            ).forEach(this::installPlugin);
        }

        private void installPlugin(String plugin) {
            JsonNode lastVersionInfo = callApiMethod("PluginInterface", "getPluginBundle", map(
                    "repository", "http://central.maven.org/maven2",
                    "groupId", "org.opensourcebim",
                    "artifactId", checkNotBlank(plugin)
            )).get("response").get("result").get("latestVersion");

//if (latestVersion == pluginBundle.installedVersion) {
//				o.logError("Plugin version already installed" + "\n");
            String repository = lastVersionInfo.get("repository").asText(),
                    groupId = lastVersionInfo.get("groupId").asText(),
                    artifactId = lastVersionInfo.get("artifactId").asText(),
                    version = lastVersionInfo.get("version").asText();

            logger.info("install plugin = {} v{}", plugin, version);

            JsonNode pluginList = callApiMethod("PluginInterface", "getPluginInformation", map(
                    "repository", repository,
                    "groupId", groupId,
                    "artifactId", artifactId,
                    "version", version
            )).get("response").get("result");

            List pluginsToLoad = list().accept(l -> {
                for (JsonNode element : pluginList) {
                    l.add(map(
                            "__type", element.get("__type").asText(),
                            "enabled", true,
                            "installForAllUsers", true,
                            "installForNewUsers", true,
                            "name", element.get("name").asText(),
                            "identifier", element.get("identifier").asText(),
                            "description", element.get("description").asText(),
                            "type", element.get("type").asText(),
                            "oid", element.get("oid").asInt(),
                            "rid", element.get("rid").asInt()
                    ));
                }
            });

            JsonNode pluginInstallRes = callApiMethod("PluginInterface", "installPluginBundle", map(
                    "repository", repository,
                    "groupId", groupId,
                    "artifactId", artifactId,
                    "version", version,
                    "plugins", pluginsToLoad
            ));
            checkArgument(equal(toJson(pluginInstallRes.get("response").get("result")), "{}"), "plugin install error, response = %s");
        }

        private JsonNode callApiMethod(String apiInterface, String method, Object parameters) {
            return callApi(map("request", map("interface", checkNotBlank(apiInterface), "method", checkNotBlank(method), "parameters", parameters)).accept(m -> {
                if (isNotBlank(authToken)) {
                    m.put("token", authToken);
                }
            }));
        }

        private JsonNode callApi(Object payload) {
            String jsonPayload = toJson(payload);
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost request = new HttpPost(bimserverApiUrl);
                logger.debug("api request = {}", jsonPayload);
                request.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));
                CloseableHttpResponse response = client.execute(request);
                checkArgument(response.getStatusLine().getStatusCode() == 200, "api call error, status code = %s", response.getStatusLine());
                String responseContent = EntityUtils.toString(response.getEntity());
                EntityUtils.consumeQuietly(response.getEntity());
                logger.debug("api response = {}", responseContent);
                return fromJson(responseContent, JsonNode.class);
            } catch (Exception ex) {
                throw runtime(ex);
            }
        }

    }

}
