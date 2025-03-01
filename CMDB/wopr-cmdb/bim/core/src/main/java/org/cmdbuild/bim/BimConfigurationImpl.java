package org.cmdbuild.bim;

import static org.cmdbuild.bim.utils.BimConfigUtils.BIM_CONFIG_ENABLED;
import static org.cmdbuild.bim.utils.BimConfigUtils.BIM_CONFIG_NAMESPACE;
import static org.cmdbuild.bim.utils.BimConfigUtils.BIM_CONFIG_VIEWER;
import org.cmdbuild.config.BimConfiguration;
import org.cmdbuild.config.BimViewers;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent(BIM_CONFIG_NAMESPACE)
public final class BimConfigurationImpl implements BimConfiguration {

    @ConfigValue(key = BIM_CONFIG_ENABLED, description = "bim enabled", defaultValue = FALSE)
    private boolean isEnabled;

    @ConfigValue(key = BIM_CONFIG_VIEWER, description = "bim viewer", defaultValue = "xeokit")
    private BimViewers viewer;

    @ConfigValue(key = "conversiontimeout", description = "timeout for ifx to xkt conversion expressed in seconds", defaultValue = "300")
    private Long conversionTimeout;

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public BimViewers getViewer() {
        return viewer;
    }

    @Override
    public Long getConversionTimeout() {
        return conversionTimeout;
    }

}
