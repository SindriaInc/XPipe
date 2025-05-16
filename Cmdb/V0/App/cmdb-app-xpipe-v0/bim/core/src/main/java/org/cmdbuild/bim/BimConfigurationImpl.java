package org.cmdbuild.bim;

import org.cmdbuild.config.BimViewers;
import static org.cmdbuild.bim.utils.BimConfigUtils.BIMSERVER_CONFIG_DELETE_BEFORE_UPLOAD;
import static org.cmdbuild.bim.utils.BimConfigUtils.BIMSERVER_CONFIG_ENABLED;
import static org.cmdbuild.bim.utils.BimConfigUtils.BIMSERVER_CONFIG_PASSWORD;
import static org.cmdbuild.bim.utils.BimConfigUtils.BIMSERVER_CONFIG_URL;
import static org.cmdbuild.bim.utils.BimConfigUtils.BIMSERVER_CONFIG_USERNAME;
import static org.cmdbuild.config.api.ConfigCategory.CC_ENV;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.springframework.stereotype.Component;
import static org.cmdbuild.bim.utils.BimConfigUtils.BIM_CONFIG_NAMESPACE;
import org.cmdbuild.config.BimConfiguration;
import static org.cmdbuild.bim.utils.BimConfigUtils.BIM_CONFIG_VIEWER;
import static org.cmdbuild.bim.utils.BimConfigUtils.BIM_CONFIG_ENABLED;

@Component
@ConfigComponent(BIM_CONFIG_NAMESPACE)
public final class BimConfigurationImpl implements BimConfiguration {

    @ConfigValue(key = BIM_CONFIG_ENABLED, description = "bim enabled", defaultValue = FALSE)
    private boolean isEnabled;

    @ConfigValue(key = BIMSERVER_CONFIG_ENABLED, description = "bim server enabled", defaultValue = FALSE)
    private boolean isBimserverEnabled;

    @ConfigValue(key = BIMSERVER_CONFIG_USERNAME, description = "bim server username", defaultValue = "admin@bimserver.com", category = CC_ENV)
    private String username;

    @ConfigValue(key = BIMSERVER_CONFIG_PASSWORD, description = "bim server password", defaultValue = "admin", category = CC_ENV)
    private String password;

    @ConfigValue(key = BIMSERVER_CONFIG_URL, description = "bim server url", defaultValue = "http://localhost:11080/bimserver", category = CC_ENV)
    private String url;

    @ConfigValue(key = BIM_CONFIG_VIEWER, description = "bim viewer", defaultValue = "bimserver")
    private BimViewers viewer;

    @ConfigValue(key = BIMSERVER_CONFIG_DELETE_BEFORE_UPLOAD, description = "delete project before upload", defaultValue = FALSE)
    private boolean deleteProjectBeforeUpload;

    @ConfigValue(key = "conversiontimeout", description = "timeout for ifx to xkt conversion expressed in seconds", defaultValue = "300")
    private Long conversionTimeout;

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public boolean isBimserverEnabled() {
        return isBimserverEnabled;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public BimViewers getViewer() {
        return viewer;
    }

    @Override
    public boolean deleteProjectBeforeUpload() {
        return deleteProjectBeforeUpload;
    }

    @Override
    public Long getConversionTimeout() {
        return conversionTimeout;
    }

}
