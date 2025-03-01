/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.plugin;

/**
 * Placeholder for dummy services used in plugins
 *
 * @author afelice
 */
public interface DummyPluginService extends PluginService {

    @Override
    default String getName() {
        return "dummy-plugin-service";
    }

    @Override
    default boolean isDummy() {
        return true;
    }
}
