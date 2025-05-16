/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.plugin;

import java.util.List;
import org.cmdbuild.config.api.ConfigCategory;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent(value = "org.cmdbuild.plugin")
public class SystemPluginConfigs {

    @ConfigValue(key = "list", description = "list of system plugins (read only)", category = ConfigCategory.CC_ENV)
    private List<String> list;

    @ConfigValue(key = "info", description = "info of system plugins (read only)", category = ConfigCategory.CC_ENV)
    private String info;
}
