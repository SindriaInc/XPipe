package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.activation.DataHandler;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import org.cmdbuild.easyupload.EasyuploadService;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.easyupload.EasyuploadUtils;

@Path("resources/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ResourcesWs {

    private final EasyuploadService easyuploadService;
    private final CoreConfiguration coreConfiguration;

    public ResourcesWs(EasyuploadService easyuploadService, CoreConfiguration coreConfiguration) {
        this.easyuploadService = checkNotNull(easyuploadService);
        this.coreConfiguration = checkNotNull(coreConfiguration);
    }

    @GET
    @Path("company_logo/{file}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler downloadCompanyLogo() {
        return EasyuploadUtils.toDataHandler(easyuploadService.getById(coreConfiguration.getCompanyLogoUploadsId()));
    }

}
