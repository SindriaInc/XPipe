/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.log;

import jakarta.annotation.Nullable;

public interface LogbackConfigFileHelper {

    String setConfigPropertiesInLogbackConfig(String logbackConfig);

    String getConfigOrDefault();

    @Nullable
    String getConfigOrNull();

    String getDefaultConfig();

    String getStdOutConfig();

    String getFallbackConfig();

    void setConfig(String config);

    boolean hasConfigFile();

}
