/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.uicomponents.custompage;

import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent("org.cmdbuild.custompage")
public class CustomPageConfigurationImpl implements CustomPageConfiguration {

    @ConfigValue(key = "js.compression.enabled", description = "enable compression of js files", defaultValue = TRUE)
    private boolean jsCompressionEnabled;

    @Override
    public boolean isJsCompressionEnabled() {
        return jsCompressionEnabled;
    }

}
