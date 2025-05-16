package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.nullToEmpty;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import javax.annotation.Nullable;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.classe.access.UserClassService;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.dao.utils.CmFilterUtils;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.gis.GisService;
import org.cmdbuild.gis.stylerules.GisStyleRulesService;
import static org.cmdbuild.gis.stylerules.GisStyleRulesUtils.parseRules;
import static org.cmdbuild.gis.stylerules.GisStyleRulesUtils.serializeRules;
import org.cmdbuild.gis.stylerules.GisStyleRuleset;
import org.cmdbuild.gis.stylerules.GisStyleRulesetImpl;
import org.cmdbuild.gis.stylerules.GisStyleRulesetImpl.GisStyleRulesetImplBuilder;
import org.cmdbuild.gis.stylerules.GisStyleRulesetAnalysisType;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.emptyToNull;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;

@Path("{a:classes|processes}/{" + CLASS_ID + "}/geostylerules/")
@Produces(APPLICATION_JSON)
public class GeoStyleRulesWs {

    private final GisStyleRulesService rulesService;
    private final GisService gisService;
    private final UserClassService userClassService;

    public GeoStyleRulesWs(GisStyleRulesService rulesService, GisService gisService, UserClassService userClassService) {
        this.rulesService = checkNotNull(rulesService);
        this.gisService = checkNotNull(gisService);
        this.userClassService = checkNotNull(userClassService);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@PathParam(CLASS_ID) String classId, @QueryParam(FILTER) String filterStr, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset) {
        List<GisStyleRuleset> list;
        if (equal(classId, "_ANY")) {
            list = rulesService.getAll().stream().filter(r -> userClassService.getUserClass(r.getOwnerClassName()).hasGisAttributeReadPermission(r.getGisAttribute().getLayerName())).collect(toList());
        } else {
            list = rulesService.getForClass(classId).stream().filter(r -> userClassService.getUserClass(classId).hasGisAttributeReadPermission(r.getGisAttribute().getLayerName())).collect(toList());
        }
        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
        filter.checkHasOnlySupportedFilterTypes(FilterType.FULLTEXT);
        if (filter.hasFulltextFilter()) {
            list = list.stream().filter(r -> nullToEmpty(r.getDescription()).toLowerCase().contains(filter.getFulltextFilter().getQuery().toLowerCase())
                    || r.getCode().toLowerCase().contains(filter.getFulltextFilter().getQuery().toLowerCase())).collect(toList());//TODO improve this
        }
        return response(paged(list, offset, limit).map(this::serializeRuleset));
    }

    @GET
    @Path("{rulesetId}/")
    public Object read(@PathParam(CLASS_ID) String classId, @PathParam("rulesetId") Long rulesetId) {
        GisStyleRuleset rulesetById = rulesService.getById(rulesetId);
        checkArgument(userClassService.getUserClass(classId).hasGisAttributeReadPermission(rulesetById.getGisAttribute().getLayerName()), "User now allowed to read the related geo attribute");
        return response(serializeRuleset(rulesService.getById(rulesetId)));
    }

    @POST
    @Path(EMPTY)
    public Object create(@PathParam(CLASS_ID) String classId, WsRulesetData data) {
        GisStyleRuleset rules = rulesService.create(dataToRules(data).build());
        return response(serializeRuleset(rules));
    }

    @PUT
    @Path("{rulesetId}/")
    public Object update(@PathParam(CLASS_ID) String classId, @PathParam("rulesetId") Long rulesetId, WsRulesetData data) {
        GisStyleRuleset rules = rulesService.update(dataToRules(data).withId(rulesetId).build());
        return response(serializeRuleset(rules));
    }

    @DELETE
    @Path("{rulesetId}/")
    public Object delete(@PathParam(CLASS_ID) String classId, @PathParam("rulesetId") Long rulesetId) {
        rulesService.delete(rulesetId);
        return success();
    }

    @GET
    @Path("{rulesetId}/result")
    public Object applyRules(@PathParam(CLASS_ID) String classId, @PathParam("rulesetId") Long rulesetId, @QueryParam("cards") @Nullable String cards) {
        Set<Long> cardIds = isBlank(cards) ? null : emptyToNull(Splitter.on(",").splitToList(cards).stream().map(CmConvertUtils::toLong).collect(toSet()));
        return response(serializeRulesResult(rulesService.applyRulesOnCards(rulesetId, cardIds)));
    }

    @POST
    @Path("tryRules")
    public Object testRules(@PathParam(CLASS_ID) String classId, WsRulesetData data, @QueryParam("cards") @Nullable String cards) {
        Set<Long> cardIds = isBlank(cards) ? null : emptyToNull(Splitter.on(",").splitToList(cards).stream().map(CmConvertUtils::toLong).collect(toSet()));
        GisStyleRuleset ruleset = dataToRules(data).build();
        return response(serializeRulesResult(rulesService.applyRulesOnCards(ruleset, cardIds)));
    }

    private Object serializeRuleset(GisStyleRuleset rules) {
        return map(
                "_id", rules.getId(),
                "name", rules.getCode(),
                "description", rules.getDescription(),
                "owner", rules.getOwnerClassName(),
                "attribute", rules.getGisAttribute().getLayerName(),
                "type", rules.hasFunction() ? "function" : "table",
                "function", rules.getFunction(),
                "analysistype", serializeEnum(rules.getAnalysisType()),
                "segments", rules.getSegments(),
                "classattribute", rules.getClassAttribute(),
                "rules", serializeRules(rules.getRules())
        );
    }

    private GisStyleRulesetImplBuilder dataToRules(WsRulesetData data) {
        return GisStyleRulesetImpl.builder()
                .withCode(data.name)
                .withDescription(data.description)
                .withFunction(data.function)
                .withGisAttribute(gisService.getGisAttributeIncludeInherited(data.owner, data.attribute))
                .withParams(b -> b.withSegments(data.segments).withAnalysisType(parseEnumOrNull(data.analysistype, GisStyleRulesetAnalysisType.class)).withClassAttribute(data.classattribute))
                .withRules(parseRules(toJson(data.rules)));
    }

    private Object serializeRulesResult(Map<Long, Map<String, Object>> res) {
        return res.entrySet().stream().map(e -> map("_id", e.getKey(), "style", e.getValue())).collect(toList());
    }

    public static class WsRulesetData {

        private final String name;
        private final String description;
        private final String function;
        private final String attribute;
        private final String analysistype;
        private final String classattribute;
        private final Integer segments;
        private final String owner;
        private final JsonNode rules;

        public WsRulesetData(@JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("function") String function,
                @JsonProperty("attribute") String attribute,
                @JsonProperty("analysistype") String analysistype,
                @JsonProperty("classattribute") String classattribute,
                @JsonProperty("segments") Integer segments,
                @JsonProperty("owner") String owner,
                @JsonProperty("rules") JsonNode rules) {
            this.name = checkNotBlank(name, "missing required param 'name'");
            this.description = description;
            this.function = function;
            this.analysistype = analysistype;
            this.classattribute = classattribute;
            this.segments = segments;
            this.attribute = checkNotBlank(attribute, "missing required param 'attribute'");
            this.owner = checkNotBlank(owner, "missing required param 'owner'");
            this.rules = checkNotNull(rules, "missing required param 'rules'");
        }

    }

}
