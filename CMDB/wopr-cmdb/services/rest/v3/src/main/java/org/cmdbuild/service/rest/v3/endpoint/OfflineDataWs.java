/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v3.endpoint;

import jakarta.activation.DataHandler;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import java.util.List;
import java.util.Map;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.modeldiff.diff.data.GeneratedDiffData;
import org.cmdbuild.offline.loader.OfflineLoaderService;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *
 * @author ataboga
 */
@Path("offline/data")
@Produces(APPLICATION_JSON)
public class OfflineDataWs {

    private final OfflineLoaderService offlineLoaderService;

    public OfflineDataWs(OfflineLoaderService offlineLoaderService) {
        this.offlineLoaderService = checkNotNull(offlineLoaderService);
    }

    @POST
    @Path("/{offlineCode}/load")
    public Object load(@PathParam("offlineCode") String offlineCode, @RequestBody Map<String, String> filters) {
        offlineLoaderService.executeDataFromDataset(offlineCode, filters);
        return response(serializeOffline(offlineCode));
    }

    @POST
    @Path("/{offlineCode}/diff/{tempId}")
    public Object diff(@PathParam("offlineCode") String offlineCode, @PathParam("tempId") String tempId) {
        return response(serializeOfflineDiff(offlineCode, offlineLoaderService.executeDiffFromData(offlineCode, map(), tempId), tempId));
    }

    @POST
    @Path("/{offlineCode}/merge/{tempId}")
    public Object merge(@PathParam("offlineCode") String offlineCode, @PathParam("tempId") String tempId, GeneratedDiffData wsDiffData) {
        List<Map<String, Object>> data = offlineLoaderService.executeMergeFromDiff(offlineCode, wsDiffData, tempId);
        return response(data);
    }

    @POST
    @Path("/{offlineCode}/notify")
    @Consumes(MULTIPART_FORM_DATA)
    public Object notify(@Multipart("file") DataHandler dataHandler, @PathParam("offlineCode") String offlineCode) {
        String tempId = offlineLoaderService.uploadToTempService(dataHandler);
        offlineLoaderService.sendNotificationForDiff(offlineCode, tempId);
        return response(serializeOffline(offlineCode));
    }

    private FluentMap<String, String> serializeOffline(String offlineCode) {
        return map("_id", offlineCode);
    }

    private FluentMap<String, String> serializeOfflineDiff(String offlineCode, String result, String tempId) {
        return serializeOffline(offlineCode)
                .with(
                        "diff", fromJson(result, MAP_OF_OBJECTS),
                        "tempId", tempId
                );
    }
}
