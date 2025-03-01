/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.job;

import org.cmdbuild.etl.waterway.message.WaterwayMessageData;
import org.cmdbuild.plugin.PluginService;

public interface EtlLoadHandler extends PluginService {

    @Override
    default public String getName() {
        return getType();
    }

    /**
     * Used as key by {@link EtlLoadHandlerRepository} to load all handlers.
     *
     * @return
     */
    String getType();

    WaterwayMessageData load(EtlLoaderApi api);

}
