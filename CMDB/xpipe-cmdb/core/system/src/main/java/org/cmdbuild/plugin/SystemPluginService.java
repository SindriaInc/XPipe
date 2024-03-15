/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.plugin;

import java.io.File;
import java.util.Collection;
import java.util.List;

public interface SystemPluginService {

    List<SystemPlugin> getSystemPlugins();

    void deploySystemPlugins(Collection<File> files);

}
