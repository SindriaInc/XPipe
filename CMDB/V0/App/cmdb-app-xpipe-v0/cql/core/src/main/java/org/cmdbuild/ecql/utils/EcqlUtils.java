/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.ecql.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Iterables.getOnlyElement;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.quote;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.cql.EcqlException;
import org.cmdbuild.dao.entrytype.AbstractMetadata;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.easytemplate.EasytemplateProcessor;
import org.cmdbuild.easytemplate.EasytemplateProcessorImpl;
import org.cmdbuild.ecql.EcqlBindingInfo;
import org.cmdbuild.ecql.EcqlExpression;
import org.cmdbuild.ecql.EcqlId;
import org.cmdbuild.ecql.EcqlSource;
import static org.cmdbuild.ecql.EcqlSource.SYSPARAM;
import org.cmdbuild.ecql.inner.EcqlBindingInfoImpl;
import org.cmdbuild.ecql.inner.EcqlExpressionImpl;
import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeString;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeString;
import static org.cmdbuild.utils.json.CmJsonUtils.LIST_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import org.cmdbuild.utils.lang.CmStringUtils;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;

public class EcqlUtils {

    public final static String ECQL_WIDGET_FROM_CLASS = "CLASS";
    public final static String UNIQUE_CLASS_TOKEN_DELIMITER = "::";

    private final static BiMap<EcqlSource, String> MINIFIED_ECQL_SOURCE_KEY_MAP = ImmutableBiMap.copyOf(map(
            EcqlSource.CLASS_ATTRIBUTE, "C",
            EcqlSource.DOMAIN, "O",
            EcqlSource.REPORT_ATTRIBUTE, "R",
            EcqlSource.EMBEDDED, "E",
            EcqlSource.NAVTREE_NODE, "N",
            EcqlSource.EASYTEMPLATE, "T",
            EcqlSource.DASHBOARD_ITEM, "D",
            EcqlSource.WIDGET, "W",
            SYSPARAM, "P"
    ));

    public static String buildEcqlId(EcqlSource source, Object... sourceId) {
        List<String> keys = asList(checkNotNull(sourceId)).stream().map(CmStringUtils::toStringOrNull).collect(toList());
        return buildEcqlId(source, keys.toArray(String[]::new));
    }

    public static String buildClassAttrEcqlId(Attribute attribute) {
        return buildEcqlId(EcqlSource.CLASS_ATTRIBUTE, attribute.getOwner().getName(), attribute.getName());
    }

//    public static String buildDomainEcqlId(Domain domain, Attribute attribute) {
//        return buildEcqlId(EcqlSource.DOMAIN, domain.getName(), domain.getReferencedClassToken(attribute));
//    }
    
    public static String buildDomainEcqlId(Domain domain, String classeToken) {
        return buildEcqlId(EcqlSource.DOMAIN, domain.getName(), classeToken);
    }
    
    public static String buildReportAttrEcqlId(Attribute attribute) {
        return buildEcqlId(EcqlSource.REPORT_ATTRIBUTE, attribute.getOwner().getId(), attribute.getName());
    }

    public static String buildTemplateEcqlId(String code) {
        return buildEcqlId(EcqlSource.EASYTEMPLATE, checkNotBlank(code));
    }

    @Deprecated
    public static String buildEmbeddedEcqlId(String cqlExpr) {
        return buildEcqlId(EcqlSource.EMBEDDED, checkNotBlank(cqlExpr));
    }

    public static String buildNavTreeEcqlId(String navTreeCode, String nodeId) {
        return buildEcqlId(EcqlSource.NAVTREE_NODE, checkNotBlank(navTreeCode), checkNotBlank(nodeId));
    }

    public static String buildDashboardEcqlId(String dashboardCode, int index) {
        return buildEcqlId(EcqlSource.DASHBOARD_ITEM, checkNotBlank(dashboardCode), toStringNotBlank(index));
    }

    public static String buildWidgetEcqlId(String classId, String widgetId, String widgetAttr, Map<String, Object> xaContext) {
        return buildWidgetEcqlId(classId, null, widgetId, widgetAttr, xaContext);
    }

    public static String buildWidgetEcqlId(String processIdOrClassId, @Nullable String taskDefinitionIdOrNull, String widgetId, String widgetAttr, Map<String, Object> xaContext) {
        return buildEcqlId(EcqlSource.WIDGET, processIdOrClassId, firstNotBlank(taskDefinitionIdOrNull, ECQL_WIDGET_FROM_CLASS), widgetId, widgetAttr, toJson(xaContext));
    }

    public static String buildEcqlId(EcqlSource source, String... sourceId) {
        List<String> args = asList(checkNotNull(sourceId));
        checkArgument(args.stream().allMatch(StringUtils::isNotBlank));
        String ecqlRawId = format("%s%s",
                checkNotNull(MINIFIED_ECQL_SOURCE_KEY_MAP.get(source)),
                args.size() == 1 ? getOnlyElement(args) : toJson(sourceId));
        String encodedId = encodeString(ecqlRawId);
        return encodedId;
    }

    public static EcqlId parseEcqlId(String encodedId) {
        try {
            String decodedId = decodeString(encodedId);
            Matcher matcher = Pattern.compile("(.)(.+)").matcher(decodedId);
            checkArgument(matcher.matches(), "invalid ecql format for value =< %s >", abbreviate(decodedId));
            String key = checkNotBlank(matcher.group(1)),
                    value = checkNotBlank(matcher.group(2));
            EcqlSource source = checkNotNull(MINIFIED_ECQL_SOURCE_KEY_MAP.inverse().get(key), "ecql source not found for key = '%s'", key);
            List<String> idList;
            if (value.matches("\\[.*\\]")) {
                idList = fromJson(value, LIST_OF_STRINGS);
            } else {
                idList = singletonList(value);
            }
            return new EcqlIdImpl(source, idList);
        } catch (Exception ex) {
            throw new EcqlException(ex, "error parsing ecqlId = '%s'", encodedId);
        }
    }

    public static EcqlBindingInfo getEcqlBindingInfoForExpr(EcqlExpression expression) {
        return getEcqlBindingInfoForExpr(expression.getEcql(), expression.getContext());
    }

    public static String resolveEcqlXa(String ecql, Map<String, Object> xaContext) {
        return EasytemplateProcessorImpl.builder()
                .withResolver("xa", xaContext::get)
                .build().processExpression(ecql);
    }

    public static EcqlBindingInfo getEcqlBindingInfoForExpr(String ecql, Map<String, Object> context) {
        checkNotBlank(ecql, "ecql expression is null or blank");

        List<String> clientBindings = list(),
                serverBindings = list(),
                xaBindings = list();

        AtomicReference<EasytemplateProcessor> processor = new AtomicReference<>();

        processor.set(EasytemplateProcessorImpl.builder()
                .withResolver("client", (key) -> clientBindings.add(key))
                .withResolver("server", (key) -> serverBindings.add(key))
                .withResolver("xa", (key) -> xaBindings.add(key))
                .withResolver("js", (key) -> processor.get().processExpression(getJsExprFromContext(key, context)))
                .build());

        processor.get().processExpression(ecql);

        return EcqlBindingInfoImpl.builder()
                .withClientBindings(clientBindings)
                .withServerBindings(serverBindings)
                .withXaBindings(xaBindings)
                .build();
    }

    public static String getJsExprFromContext(String key, Map<String, Object> context) {
        return checkNotBlank((String) context.get(key), "value not found in context for js key = '%s'", key);
    }

    public static EcqlExpression getEcqlExpressionFromClassAttributeFilter(Attribute attribute) {
        String filter = attribute.getFilter();
        checkNotBlank(filter);
        //TODO check syntax??

        return getEcqlExpression(attribute.getMetadata(), filter);
    }
    
    public static EcqlExpression getEcqlExpressionFromDomainClassFilter(Domain domain, String classeId) {        
        String filter = domain.getMetadata().getClassReferenceFilters().get(classeId);
        checkNotBlank(filter);
        //TODO check syntax??
        
        return getEcqlExpression(domain.getMetadata(), filter);
    }    

    public static EcqlExpression getEcqlExpression(AbstractMetadata metadata, String filter) {
        Map<String, Object> context = fetchTemplateValues(metadata);

        return new EcqlExpressionImpl(filter, context);
    }    

    /**
     * Returns unique token that identifies the edge of domain and the {@link Classe} (to correctly handle autoloops, that is
     * domains with the same {@link Classe} as both source and target)
     * 
     * @param classToken <code>sourceFilter</code> or <code>targetFilter</code>
     * @param classe
     * @return <code>sourceFilter::MyClass</code> or <code>targetFilter::MyClass</code>
     */
    public static String buildUniqueClassToken(String classToken, Classe classe) {
        return format("%s%s%s", classToken, UNIQUE_CLASS_TOKEN_DELIMITER, classe.getName());
    }    
    
    /**
     * Returns token that identifies the edge of domain (source or target) to use for filtering.
     * 
     * @param uniqueClassToken <code>sourceFilter::MyClass</code> or <code>targetFilter::MyClass</code>
     * @return <code>sourceFilter</code> or <code>targetFilter</code>
     */
    public static String fetchClassToken(String uniqueClassToken) {
        checkArgument(uniqueClassToken.contains(UNIQUE_CLASS_TOKEN_DELIMITER), format("invalid unique class token =< %s >, must start with *Filter%s", uniqueClassToken, UNIQUE_CLASS_TOKEN_DELIMITER));
        return uniqueClassToken.substring(0, uniqueClassToken.indexOf(UNIQUE_CLASS_TOKEN_DELIMITER));
    }
    
    private static Map<String, Object> fetchTemplateValues(AbstractMetadata metadata) {
        Map<String, Object> context = map(metadata.getAll());
        metadata.getAll().entrySet().stream()
                .filter((e) -> e.getKey().startsWith("system.template."))
                .forEach((e) -> context.put(e.getKey().replaceFirst(quote("system.template."), ""), e.getValue()));
        
        return context;
    }
    
    private final static class EcqlIdImpl implements EcqlId {

        private final EcqlSource source;
        private final List<String> id;

        public EcqlIdImpl(EcqlSource source, List<String> id) {
            this.source = checkNotNull(source);
            this.id = copyOf(checkNotNull(id));
        }

        @Override
        public EcqlSource getSource() {
            return source;
        }

        @Override
        public List<String> getId() {
            return id;
        }

        @Override
        public String toString() {
            return "EcqlId{" + "source=" + source + ", id=" + id + '}';
        }

    }

}
