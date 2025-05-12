package org.cmdbuild.config;

import static java.util.Collections.emptyList;
import java.util.List;
import javax.annotation.Nullable;
import static org.cmdbuild.config.api.ConfigCategory.CC_DATA;
import static org.cmdbuild.config.api.ConfigCategory.CC_ENV;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.dms.DmsConfiguration.DMS_CONFIG_NAMESPACE;
import org.cmdbuild.dms.cmis.CmisDmsConfiguration;
import org.cmdbuild.dms.oracleucm.OracleUcmDmsConfiguration;
import org.cmdbuild.dms.sharepoint.SharepointDmsConfiguration;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent(DMS_CONFIG_NAMESPACE)
public final class DmsConfigurationImpl implements CmisDmsConfiguration, SharepointDmsConfiguration, OracleUcmDmsConfiguration {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String ENABLED = "enabled",
            CATEGORY_LOOKUP = "category",
            POSTGRES_CONFIG = "service.postgres.",
            CMIS_CONFIG = "service.cmis.",
            SHAREPOINT_CONFIG = "service.sharepoint.",
            ORACLEUCM_CONFIG = "service.oracleucm.";

    @ConfigValue(key = DMS_CONFIG_PROVIDER, description = "dms service (cmis, postgres, sharepoint_online); cmis is a standard protocol used, for example, by Alfresco dms; postgres is an embedded dms implementation that relies upon cmdbuild postgres db", defaultValue = "cmis")
    private String service;

    @ConfigValue(key = ENABLED, description = "", defaultValue = FALSE)
    private boolean isEnabled;

    @ConfigValue(key = "checkAttachmentExistanceOnlyOnDB", description = "", defaultValue = FALSE)
    private boolean checkAttachmentExistanceOnlyOnDB;

    @ConfigValue(key = "autolink.script", description = "autolink helper script")
    private String autolinkHelperScript;

    @ConfigValue(key = "autolink.path", description = "autolink base path (replaces dms provider base path, for links)")
    private String autolinkPath;

    @ConfigValue(key = "regularAttachments.allowedFileExtensions", description = "allowed file extensions, lowercase, for card/email attachments (via ui/attachment ws)")
    private List<String> regularAttachmentsAllowedFileExtensions;

    @ConfigValue(key = "incomingEmailAttachments.allowedFileExtensions", description = "allowed file extensions, lowercase, for incoming email (rejected attachments will be ignored and print a warning, without affecting email processing)")
    private List<String> incomingEmailAttachmentsAllowedFileExtensions;

    @ConfigValue(key = "regularAttachments.maxFileSize", description = "maximum allowed file size, expressed in MB")
    private Integer maxFileSize;

    @ConfigValue(key = CATEGORY_LOOKUP, description = "", defaultValue = "AlfrescoCategory", category = CC_DATA)
    private String dmsCategory;

    @ConfigValue(key = POSTGRES_CONFIG + "preview", description = "generate preview when postgres dms service is enabled (images, pdf and text)", defaultValue = FALSE)
    private boolean isPgPreviewEnabled;

    @ConfigValue(key = POSTGRES_CONFIG + "preview.maxFileSize", description = "maximum postgres preview file size, expressed in MB", defaultValue = "5")
    private Integer pgPreviewMaxFileSize;

    @ConfigValue(key = CMIS_CONFIG + CMIS_URL, description = "", defaultValue = "http://localhost:10080/alfresco/api/-default-/public/cmis/versions/1.1/atom", category = CC_ENV)
    private String cmisUrl;

    @ConfigValue(key = CMIS_CONFIG + CMIS_USER, description = "", defaultValue = "admin", category = CC_ENV)
    private String cmisUser;

    @ConfigValue(key = CMIS_CONFIG + CMIS_PASSWORD, description = "", defaultValue = "admin", category = CC_ENV)
    private String cmisPassword;

    @ConfigValue(key = CMIS_CONFIG + "path", description = "", defaultValue = "/User Homes/cmdbuild", category = CC_ENV)
    private String cmdisPath;

    @ConfigValue(key = SHAREPOINT_CONFIG + SHAREPOINT_URL, description = "", defaultValue = "", category = CC_ENV)
    private String sharepointUrl;

    @ConfigValue(key = SHAREPOINT_CONFIG + SHAREPOINT_USER, description = "", defaultValue = "admin", category = CC_ENV)
    private String sharepointUser;

    @ConfigValue(key = SHAREPOINT_CONFIG + SHAREPOINT_PASSWORD, description = "", defaultValue = "admin", category = CC_ENV)
    private String sharepointPassword;

    @ConfigValue(key = SHAREPOINT_CONFIG + "path", description = "", defaultValue = "/", category = CC_ENV)
    private String sharepointPath;

    @ConfigValue(key = SHAREPOINT_CONFIG + SHAREPOINT_GRAPH_API_BASE_URL, defaultValue = SHAREPOINT_GRAPH_API_BASE_URL_DFAULT, category = CC_ENV)
    private String sharepointGraphApiUrl;

    @ConfigValue(key = SHAREPOINT_CONFIG + SHAREPOINT_AUTH_PROTOCOL, description = "sharepoint auth protocol (es: `msazureoauth2`)", defaultValue = SHAREPOINT_AUTH_PROTOCOL_DEFAULT, category = CC_ENV)
    private String sharepointAuthProtocol;

    @ConfigValue(key = SHAREPOINT_CONFIG + SHAREPOINT_AUTH_RESOURCE_ID, description = "sharepoint auth resource id", category = CC_ENV)
    private String sharepointAuthResourceId;

    @ConfigValue(key = SHAREPOINT_CONFIG + SHAREPOINT_AUTH_CLIENT_ID, description = "sharepoint auth client id", category = CC_ENV)
    private String sharepointAuthClientId;

    @ConfigValue(key = SHAREPOINT_CONFIG + SHAREPOINT_AUTH_TENANT_ID, description = "sharepoint auth tenant id", category = CC_ENV)
    private String sharepointAuthTenantId;

    @ConfigValue(key = SHAREPOINT_CONFIG + SHAREPOINT_AUTH_SERVICE_URL, description = "sharepoint auth service url", defaultValue = SHAREPOINT_AUTH_SERVICE_URL_DEFAULT, category = CC_ENV)
    private String sharepointAuthServiceUrl;

    @ConfigValue(key = SHAREPOINT_CONFIG + SHAREPOINT_AUTH_CLIENT_SECRET, description = "sharepoint auth client secret", category = CC_ENV)
    private String sharepointAuthClientSecret;

    @ConfigValue(key = SHAREPOINT_CONFIG + "model.authorColumn", description = "sharepoint custom author column", category = CC_ENV)
    private String sharepointCustomAuthorColumn;

    @ConfigValue(key = SHAREPOINT_CONFIG + "model.descriptionColumn", description = "sharepoint custom description column", defaultValue = "Label", category = CC_ENV)
    private String sharepointCustomDescriptionColumn;

    @ConfigValue(key = SHAREPOINT_CONFIG + "model.categoryColumn", description = "sharepoint custom category column", category = CC_ENV)
    private String sharepointCustomCategoryColumn;

    @ConfigValue(key = ORACLEUCM_CONFIG + "url", description = "", defaultValue = "", category = CC_ENV)
    private String oracleUcmUrl;

    @ConfigValue(key = ORACLEUCM_CONFIG + "user", description = "", defaultValue = "", category = CC_ENV)
    private String oracleUcmUser;

    @ConfigValue(key = ORACLEUCM_CONFIG + "path", description = "", defaultValue = "/", category = CC_ENV)
    private String oracleUcmPath;

    @ConfigValue(key = ORACLEUCM_CONFIG + "securityGroup", description = "", defaultValue = "", category = CC_ENV)
    private String oracleUcmSecurityGroup;

    @ConfigValue(key = ORACLEUCM_CONFIG + "timeout", description = "socket and connection timeout millis", defaultValue = "300000", category = CC_ENV)
    private int oracleUcmTimeout;

    @ConfigValue(key = ORACLEUCM_CONFIG + "model.descriptionColumn", description = "oracle ucm custom description column", defaultValue = "xComments", category = CC_ENV)
    private String oracleUcmCustomDescriptionColumn;

    @Override
    public String getOracleUcmDescriptionField() {
        return oracleUcmCustomDescriptionColumn;
    }

    @Override
    public int getOracleUcmTimeoutMillis() {
        return oracleUcmTimeout;
    }

    @Override
    public String getOracleUcmPath() {
        return oracleUcmPath;
    }

    @Override
    public String getOracleUcmIdcContextUsername() {
        return oracleUcmUser;
    }

    @Override
    public String getOracleUcmIdcUrl() {
        return oracleUcmUrl;
    }

    @Override
    public String getOracleUcmSecurityGroup() {
        return oracleUcmSecurityGroup;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public boolean isPgPreviewEnabled() {
        return isPgPreviewEnabled;
    }

    @Override
    public Integer getPgPreviewMaxFileSize() {
        return pgPreviewMaxFileSize;
    }

    @Override
    public Integer getMaxFileSize() {
        return maxFileSize;
    }

    @Override
    @Nullable
    public String getAutolinkHelperScript() {
        return autolinkHelperScript;
    }

    @Override
    @Nullable
    public String getAutolinkBasePath() {
        return autolinkPath;
    }

    @Override
    public boolean checkAttachmentExistanceOnlyOnDB() {
        return checkAttachmentExistanceOnlyOnDB;
    }

    @Override
    public String getService() {
        return service;
    }

    @Override
    public String getDefaultDmsCategory() {
        return dmsCategory;
    }

    @Override
    public String getCmisUrl() {
        return cmisUrl;
    }

    @Override
    public String getCmisUser() {
        return cmisUser;
    }

    @Override
    public String getCmisPassword() {
        return cmisPassword;
    }

    @Override
    public String getCmisPath() {
        return cmdisPath;
    }

    @Override
    public String getSharepointUrl() {
        return sharepointUrl;
    }

    @Override
    public String getSharepointUser() {
        return sharepointUser;
    }

    @Override
    public String getSharepointPassword() {
        return sharepointPassword;
    }

    @Override
    public String getSharepointPath() {
        return sharepointPath;
    }

    @Override
    public String getSharepointAuthProtocol() {
        return sharepointAuthProtocol;
    }

    @Override
    public String getSharepointAuthResourceId() {
        return sharepointAuthResourceId;
    }

    @Override
    public String getSharepointAuthClientId() {
        return sharepointAuthClientId;
    }

    @Override
    public String getSharepointAuthTenantId() {
        return sharepointAuthTenantId;
    }

    @Override
    public String getSharepointAuthServiceUrl() {
        return sharepointAuthServiceUrl;
    }

    @Override
    public String getSharepointAuthClientSecret() {
        return sharepointAuthClientSecret;
    }

    @Override
    @Nullable
    public String getSharepointCustomAuthorColumn() {
        return sharepointCustomAuthorColumn;
    }

    @Override
    @Nullable
    public String getSharepointCustomDescriptionColumn() {
        return sharepointCustomDescriptionColumn;
    }

    @Override
    @Nullable
    public String getSharepointCustomCategoryColumn() {
        return sharepointCustomCategoryColumn;
    }

    @Override
    public String getSharepointGraphApiBaseUrl() {
        return sharepointGraphApiUrl;
    }

    @Override
    public List<String> getRegularAttachmentsAllowedFileExtensions() {
        return firstNotNull(regularAttachmentsAllowedFileExtensions, emptyList());
    }

    @Override
    public List<String> getIncomingEmailAttachmentsAllowedFileExtensions() {
        return firstNotNull(incomingEmailAttachmentsAllowedFileExtensions, emptyList());
    }

}
