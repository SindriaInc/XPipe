package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.uniqueIndex;
import com.google.common.collect.Ordering;
import java.util.Collection;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.data.filter.AttributeFilterCondition;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FUNCTION_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.NAME;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PARAMETERS;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.utils.json.CmJsonUtils;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

@Path("functions/")
@Produces(APPLICATION_JSON)
public class FunctionsWs {

    private final DaoService dao;
    private final CardWsSerializationHelperv3 helper;
    private final ObjectTranslationService translationService;
    private final AttributeTypeConversionService attributeTypeConversionService;

    public FunctionsWs(DaoService dao, CardWsSerializationHelperv3 helper, ObjectTranslationService translationService, AttributeTypeConversionService attributeTypeConversionService) {
        this.dao = checkNotNull(dao);
        this.helper = checkNotNull(helper);
        this.translationService = checkNotNull(translationService);
        this.attributeTypeConversionService = checkNotNull(attributeTypeConversionService);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset, @QueryParam(FILTER) String filterStr, @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed) {
        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);

        List<StoredFunction> list = dao.getAllFunctions().stream().sorted(Ordering.natural().onResultOf(StoredFunction::getId)).collect(toList());
        filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
        if (filter.hasAttributeFilter()) {

            list = AttributeFilterProcessor.<StoredFunction>builder()
                    .withKeyToValueFunction((key, fun) -> switch (key) {
                case NAME ->
                    fun.getName();
                case "tags" ->
                    fun.getTags();
                case "source" ->
                    fun.hasSourceClassName() ? dao.getClasse(fun.getSourceClassName()) : null;
                default ->
                    throw unsupported("unsupported filter key = %s", key);
            })
                    .withConditionEvaluatorFunction((AttributeFilterProcessor.ConditionEvaluatorFunction) (AttributeFilterCondition condition, Object value) -> switch (condition.getOperator()) {
                case EQUAL ->
                    equal(toStringOrNull(value), condition.getSingleValue());
                case IN ->
                    condition.getValues().contains(toStringOrNull(value));
                case CONTAIN -> {
                    if (value instanceof Collection collection) {
                        yield collection.contains(condition.getSingleValue());//TODO improve this
                    } else {
                        yield value != null && ((Classe) value).equalToOrAncestorOf(dao.getClasse(condition.getSingleValue())); //TODO filter also
                    }
                }
                default ->
                    throw new IllegalArgumentException("unsupported operator = " + condition.getOperator());
            })
                    .withFilter(filter.getAttributeFilter())
                    .filter(list);
        }

        PagedElements<StoredFunction> paged = PagedElements.paged(list, offset, limit);

        return response(paged.stream().map(detailed ? this::toDetailedResponse : this::toResponse).collect(toList()), paged.totalSize());
    }

    @GET
    @Path("{" + FUNCTION_ID + "}/")
    public Object read(@PathParam(FUNCTION_ID) String functionId) {
        StoredFunction function = getFunction(functionId);
        return response(toDetailedResponse(function));
    }

    @GET
    @Path("{" + FUNCTION_ID + "}/parameters/")
    public Object readInputParameters(@PathParam(FUNCTION_ID) String functionId, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset) {
        StoredFunction function = getFunction(functionId);
        Collection<Attribute> parameters = function.getInputParameters();
        return serializeResponse(parameters, function.getName(), limit, offset);
    }

    @GET
    @Path("{" + FUNCTION_ID + "}/attributes/")
    public Object readOutputParameters(@PathParam(FUNCTION_ID) String functionId, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset) {
        StoredFunction function = getFunction(functionId);
        Collection<Attribute> oParameters = function.getOutputParameters();
        return serializeResponse(oParameters, function.getName(), limit, offset);
    }

    @GET
    @Path("{" + FUNCTION_ID + "}/outputs/")
    public Object call(@PathParam(FUNCTION_ID) String functionId, @QueryParam(PARAMETERS) String inputs, @QueryParam("model") String model) {
        StoredFunction function = getFunction(functionId);

        Map<String, Object> params = isBlank(inputs) ? emptyMap() : fromJson(inputs, MAP_OF_OBJECTS);
        List<Object> inputParams = function.getInputParameters().stream().map(Attribute::getName).map(params::get).collect(toList());

        List<Attribute> outputParams = isBlank(model)
                ? function.getOutputParameters()
                : parseOutputParameters(function, model);

        List rows = dao.selectFunction(function, inputParams, outputParams).run().stream().map((r) -> r.asMap())
                .map((source) -> map().accept((map) -> outputParams.forEach((p) -> helper.addCardValuesAndDescriptionsAndExtras(p.getName(), p.getType(), source::get, map::put)))).collect(toList());

        return response(rows);
    }

    @POST
    @Path("{" + FUNCTION_ID + "}/outputs/")
    public Object callAsPost(
            @PathParam(FUNCTION_ID) String functionId,
            @QueryParam("model") String model,
            @Multipart(value = PARAMETERS, required = false) @Nullable String parameters) {
        StoredFunction function = getFunction(functionId);

        Map<String, Object> params = isBlank(parameters) ? emptyMap() : fromJson(parameters, MAP_OF_OBJECTS);
        List<Object> inputParams = function.getInputParameters().stream().map(Attribute::getName).map(params::get).collect(toList());

        List<Attribute> outputParams = isBlank(model)
                ? function.getOutputParameters()
                : parseOutputParameters(function, model);

        List rows = dao.selectFunction(function, inputParams, outputParams).run().stream().map((r) -> r.asMap())
                .map((source) -> map().accept((map) -> outputParams.forEach((p) -> helper.addCardValuesAndDescriptionsAndExtras(p.getName(), p.getType(), source::get, map::put)))).collect(toList());

        return response(rows);
    }

    private Object serializeResponse(Collection<Attribute> parameters, String funName, @Nullable Integer limit, @Nullable Integer offset) {
        return response(paged(parameters.stream().map(p -> serializeParam(p, funName)).collect(toList()), offset, limit));
    }

    private Object serializeParam(Attribute param, String functionId) {
        return map(attributeTypeConversionService.serializeAttributeType(param)).with("_description_translation", translationService.translateFunctionAttributeDescription(functionId, param.getName(), param.getName()));
    }

    private StoredFunction getFunction(String idOrName) {
        checkNotBlank(idOrName);
        if (NumberUtils.isCreatable(idOrName)) {
            return dao.getFunctionById(Long.parseLong(idOrName));
        } else {
            return dao.getFunctionByName(idOrName);
        }
    }

    private FluentMap<String, Object> toResponse(StoredFunction input) {
        return map("_id", input.getId(),
                "name", input.getName(),
                "description", input.getName());
    }

    private Object toDetailedResponse(StoredFunction input) {
        return toResponse(input).with(
                "tags", input.getTags(),
                "source", input.getSourceClassName(),
                "metadata", input.getMetadataExt(),
                "parameters", serializeParams(input.getInputParameters()));
    }

    private List serializeParams(List<Attribute> parameters) {
        return list(parameters)
                .mapWithIndex((index, attr) -> map(
                "_id", attr.getName(),
                "type", AttributeTypeConversionService.serializeAttributeType(attr.getType().getName()),
                "name", attr.getName(),
                "description", attr.getName(),
                "active", true,
                "index", index))
                .collect(toList());
    }

    private List<Attribute> parseOutputParameters(StoredFunction function, String modelStr) {
        WsFunctionOutputModel model = CmJsonUtils.fromJson(modelStr, WsFunctionOutputModel.class);
        Map<String, WsFunctionOutputParameter> customParams = uniqueIndex(model.output, (p) -> p.name);
        return function.getOutputParameters().stream().map((param) -> {
            WsFunctionOutputParameter customParam = customParams.get(param.getName());
            if (customParam != null) {
                param = AttributeImpl.builder().withOwner(function).withName(param.getName()).withType(customParam.type).withMeta(param.getMetadata()).build();
            }
            return param;

        }).collect(toList());
    }

    private static class WsFunctionOutputModel {

        private final List<WsFunctionOutputParameter> output;

        public WsFunctionOutputModel(@JsonProperty("output") List<WsFunctionOutputParameter> output) {
            this.output = checkNotNull(output);
        }

        @Override
        public String toString() {
            return "WsFunctionOutputModel{" + "output=" + output + '}';
        }

    }

    private static class WsFunctionOutputParameter {

        private final String name;
        private final CardAttributeType type;

        public WsFunctionOutputParameter(@JsonProperty("name") String name, @JsonProperty("type") String typeStr, @JsonProperty("fkTarget") String fkTarget, @JsonProperty("lookupType") String lookupType) {
            this.name = checkNotBlank(name);
            AttributeTypeName typeName = AttributeTypeName.valueOf(typeStr.toUpperCase());
            type = switch (typeName) {
                case FOREIGNKEY ->
                    new ForeignKeyAttributeType(fkTarget);
                case LOOKUP ->
                    new LookupAttributeType(lookupType);
                default ->
                    throw unsupported("unsupported param type = %s", typeStr);
            };
        }

        @Override
        public String toString() {
            return "WsFunctionOutputParameter{" + "name=" + name + ", type=" + type + '}';
        }

    }
}
