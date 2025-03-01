package org.cmdbuild.config;

import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigService;
import org.cmdbuild.config.api.ConfigValue;
import org.springframework.stereotype.Component;
import org.cmdbuild.config.api.NamespacedConfigService;

@Component
@ConfigComponent("org.cmdbuild.cmdbf")
public final class CmdbfConfigurationImpl implements CmdbfConfiguration {

    private static final String MDR_ID = "mdrid";
    private static final String SCHEMA_LOCATION = "schemalocation";
    private static final String RECONCILIATION_RULES = "reconciliationrules";

    @ConfigService
    private NamespacedConfigService config;

    @ConfigValue(key = MDR_ID, description = "", defaultValue = "http://www.cmdbuild.org")
    private String mdrId;

    @ConfigValue(key = SCHEMA_LOCATION, description = "", defaultValue = "http://localhost:8080/cmdbuild/services/cmdb-schema")
    private String schemalocation;

    @ConfigValue(key = RECONCILIATION_RULES, description = "")
    private String reconciliationRules;

    @Override
    public String getMdrId() {
        return mdrId;
    }

    @Override
    public String getSchemaLocation() {
        return schemalocation;
    }

    @Override
    public String getReconciliationRules() {
        return reconciliationRules;
    }

}
