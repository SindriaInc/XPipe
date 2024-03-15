/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.log;

import javax.annotation.Nullable;

public interface LogbackConfigFileHelper {

    String setConfigPropertiesInLogbackConfig(String logbackConfig);

    String getConfigOrDefault();

    @Nullable
    String getConfigOrNull();

    String getDefaultConfig();

    String getFallbackConfig();

    void setConfig(String config);

    boolean hasConfigFile();

}
