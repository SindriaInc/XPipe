package org.cmdbuild.service.rest.v2.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.ArrayList;
import java.util.List;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.translation.TranslationService;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.lookup.LookupValue;

@Path("configuration/attachments/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class AttachmentsConfigurationWsV2 {

    private final LookupService lookupService;
    private final GlobalConfigService configService;
    private final TranslationService translationService;

    public AttachmentsConfigurationWsV2(LookupService lookupService, GlobalConfigService configService, TranslationService translationService) {
        this.lookupService = checkNotNull(lookupService);
        this.configService = checkNotNull(configService);
        this.translationService = checkNotNull(translationService);
    }

    @GET
    @Path("categories/")
    public Object readCategories() {
        PagedElements<LookupValue> lookupList = lookupService.getAllLookup(configService.getStringOrDefault("org.cmdbuild.dms.category"));

        List categories = list();
        lookupList.forEach((l) -> categories.add(map(
                "description", translationService.translateLookupDescription(l.getType().getName(), l.getCode(), l.getDescription()),
                "_id", l.getId())));
        return map("data", categories, "meta", map("total", categories.size()));
    }

    @GET
    @Path("categories/{categoryId}/attributes/")
    public Object readCategoryAttributes(@PathParam("categoryId") String categoryId) {
        List list = new ArrayList();
        return map("data", list, "meta", map());
    }
}
