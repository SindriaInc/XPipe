/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.plugin.checker;

/**
 *
 * @author ataboga
 */
public interface PluginChecker {

    public boolean checkPlugin(String pluginName, String path);
}
