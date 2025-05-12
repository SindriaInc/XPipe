package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.Ordering;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.easyupload.EasyuploadItem;
import org.cmdbuild.easyupload.EasyuploadItemInfo;
import org.cmdbuild.easyupload.EasyuploadService;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import javax.annotation.security.RolesAllowed;
import org.apache.commons.io.FilenameUtils;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ACCESS_AUTHORITY;

import static org.cmdbuild.auth.role.RolePrivilegeAuthority.SYSTEM_ACCESS_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.easyupload.EasyuploadUtils;
import org.cmdbuild.temp.TempInfo;
import org.cmdbuild.temp.TempService;
import static org.cmdbuild.temp.TempServiceUtils.isTempId;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrNull;

@Path("{a:uploads|downloads}/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class UploadWs {

    private final EasyuploadService easyuploadService;
    private final TempService tempService;

    public UploadWs(EasyuploadService easyuploadService, TempService tempService) {
        this.easyuploadService = checkNotNull(easyuploadService);
        this.tempService = checkNotNull(tempService);
    }

    @GET
    @Path("")
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
    public Object readMany(@QueryParam("path") @Nullable String dir) {
        List<EasyuploadItemInfo> list;
        if (isNotBlank(dir)) {
            list = easyuploadService.getByDir(dir);
        } else {
            list = easyuploadService.getAll();
        }
        return response(list.stream()
                .sorted(Ordering.natural().onResultOf(EasyuploadItemInfo::getPath))
                .map(this::serializeItem));
    }

    @GET
    @Path("{fileId}")
    public Object readFile(@PathParam("fileId") String fileId) {
        if (isTempId(fileId)) {
            TempInfo tempInfo = tempService.getTempInfo(fileId);
            return response(map("_id", fileId).with(serializeTempInfo(tempInfo)));
        } else {
            EasyuploadItem item = easyuploadService.getById(toLong(fileId));
            return response(serializeItem(item));
        }
    }

    @GET
    @Path("{fileId}/{file}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler downloadFile(@PathParam("fileId") String fileId) {
        if (isTempId(fileId)) {
            return tempService.getTempData(fileId);
        } else {
            EasyuploadItem item = easyuploadService.getById(toLong(fileId));
            return EasyuploadUtils.toDataHandler(item);
        }
    }

    @GET
    @Path("_MANY/{file:.+.zip}")
    @Produces(APPLICATION_OCTET_STREAM)
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
    public DataHandler downloadManyFiles(@QueryParam("path") String dir) {
        return new DataHandler(easyuploadService.getUploadsAsZipFile(dir));
    }

    @GET
    @Path("_ALL/{file:.+.zip}")
    @Produces(APPLICATION_OCTET_STREAM)
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
    public DataHandler downloadAllFiles() {
        return new DataHandler(easyuploadService.getAllUploadsAsZipFile());
    }

    @POST
    @Path("_TEMP")
    @Consumes(MULTIPART_FORM_DATA)
    public Object createTempFile(@Multipart("file") DataHandler dataHandler) {
        String tempId = tempService.putTempData(dataHandler);
        TempInfo tempInfo = tempService.getTempInfo(tempId);
        return response(map("_id", tempId).with(serializeTempInfo(tempInfo)));
    }

    @DELETE
    @Path("_TEMP/{tempId}")
    public Object deleteTempFile(@PathParam("tempId") String tempId) {
        tempService.deleteTempData(tempId);
        return success();
    }

    @POST
    @Path("")
    @Consumes(MULTIPART_FORM_DATA)
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
    public Object create(@Multipart("file") DataHandler dataHandler, @QueryParam("path") String pathFromQuery, @Multipart(value = "data", required = false) @Nullable WsUploadData uploadData, @Multipart(value = "path", required = false) String pathFromMultipart, @QueryParam("temp") @DefaultValue(FALSE) Boolean temp, @QueryParam("overwrite_existing") @DefaultValue(FALSE) Boolean overwriteExisting) {
        if (temp == true) {
            return createTempFile(dataHandler);
        } else {
            String path = firstNotBlankOrNull(pathFromQuery, pathFromMultipart, uploadData == null ? null : uploadData.path);
            String description = uploadData == null ? null : nullToEmpty(uploadData.description);
            EasyuploadItem item = overwriteExisting ? easyuploadService.createOrUpdate(dataHandler, path, description) : easyuploadService.create(dataHandler, path, description);
            return response(serializeItem(item));
        }
    }

    @POST
    @Path("_MANY")
    @Consumes(MULTIPART_FORM_DATA)
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
    public Object loadZipFile(@Multipart("file") DataHandler dataHandler) {
        easyuploadService.uploadZip(toByteArray(dataHandler));
        return success();
    }

    @PUT
    @Path("{fileId}")
    @Consumes(MULTIPART_FORM_DATA)
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
    public Object update(@PathParam("fileId") Long fileId, @Multipart(value = "file", required = false) @Nullable DataHandler dataHandler, @Multipart(value = "data", required = false) @Nullable WsUploadData uploadData) {
        String description = uploadData == null ? null : nullToEmpty(uploadData.description);
        EasyuploadItem item = easyuploadService.update(fileId, dataHandler == null ? null : toByteArray(dataHandler), description);
        return response(serializeItem(item));
    }

    @DELETE
    @Path("{fileId}")
    @RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
    public Object deleteFile(@PathParam("fileId") String fileId) {
        if (isTempId(fileId)) {
            tempService.deleteTempData(fileId);
        } else {
            easyuploadService.delete(toLong(fileId));
        }
        return success();
    }

    private Map<String, String> serializeTempInfo(TempInfo tempInfo) {
        return map(
                //                "path", "_TEMP",
                //                "folder", "_TEMP",
                "name", tempInfo.getFileName(),
                "contentType", tempInfo.getContentType(),
                "size", tempInfo.getSize());
    }

    private Object serializeItem(EasyuploadItemInfo item) {
        return map(
                "_id", item.getId(),
                "path", item.getPath(),
                "name", item.getFileName(),
                "contentType", item.getMimeType(),
                "size", item.getSize(),
                "folder", item.getFolder(),
                "description", firstNotBlank(item.getDescription(), FilenameUtils.getBaseName(item.getFileName()))
        );
    }

    public static class WsUploadData {

        private final String path;
        private final String description;

        public WsUploadData(@JsonProperty("path") String path, @JsonProperty("description") String description) {
            this.path = path;
            this.description = description;
        }

    }

}
