/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.service;

import org.cmdbuild.config.api.ConfigEntry;
import org.cmdbuild.config.api.ConfigUpdate;
import java.util.List;

public interface ConfigRepository {

    List<ConfigEntry> getConfigEntries();

    void updateConfigs(List<? extends ConfigUpdate> configs);

}
