/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.stylerules;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import org.apache.commons.lang3.tuple.Pair;
import static org.cmdbuild.cache.CacheConfig.SYSTEM_OBJECTS;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.WhereOperator.IN;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.function.StoredFunction;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.gis.GisAttributeRepository;
import static org.cmdbuild.gis.stylerules.GisStyleRulesUtils.parseRules;
import static org.cmdbuild.gis.stylerules.GisStyleRulesUtils.serializeRules;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GisStyleRulesServiceImpl implements GisStyleRulesService {

    public static final String FUNCTION_OUTPUT_KEYWORD = "OUTPUT";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final GisStyleRulesRepository repository;
    private final GisAttributeRepository gisAttributeRepository;
    private final Holder<List<GisStyleRuleset>> allRules;

    public GisStyleRulesServiceImpl(DaoService dao, GisAttributeRepository gisAttributeRepository, GisStyleRulesRepository repository, CacheService cacheService) {
        this.dao = checkNotNull(dao);
        this.repository = checkNotNull(repository);
        this.gisAttributeRepository = checkNotNull(gisAttributeRepository);
        allRules = cacheService.newHolder("gis_style_rules_all", SYSTEM_OBJECTS);
    }

    @Override
    public GisStyleRuleset create(GisStyleRuleset rules) {
        rules = parseData(repository.create(serializeData(rules)));
        allRules.invalidate();
        return rules;
    }

    @Override
    public GisStyleRuleset update(GisStyleRuleset rules) {
        rules = parseData(repository.update(serializeData(rules)));
        allRules.invalidate();
        return rules;
    }

    @Override
    public List<GisStyleRuleset> getForClass(String classId) {
        return getAll().stream().filter(r -> equal(r.getOwnerClassName(), classId)).collect(toList());
    }

    @Override
    public GisStyleRuleset getById(long rulesetId) {
        return getAll().stream().filter(r -> equal(r.getId(), rulesetId)).collect(onlyElement("ruleset not found for id = %s", rulesetId));
    }

    @Override
    public void delete(long rulesetId) {
        repository.delete(rulesetId);
        allRules.invalidate();
    }

    @Override
    public List<GisStyleRuleset> getAll() {
        return allRules.get(() -> repository.getAll().stream().map(this::parseData).collect(toImmutableList()));
    }

    @Override
    public Map<Long, Map<String, Object>> applyRulesOnCards(GisStyleRuleset ruleset, Set<Long> cardIds) {
        return new RulesHelper(ruleset).applyRulesOnCards(cardIds);
    }

    private class RulesHelper {

        private final GisStyleRuleset ruleset;
        private final StoredFunction storedFunction;
        private final Classe classe;

        public RulesHelper(GisStyleRuleset ruleset) {
            this.ruleset = checkNotNull(ruleset);
            classe = dao.getClasse(ruleset.getOwnerClassName());
            if (ruleset.hasFunction()) {
                storedFunction = dao.getFunctionByName(ruleset.getFunction());
            } else {
                storedFunction = null;
            }
        }

        public Map<Long, Map<String, Object>> applyRulesOnCards(Set<Long> cardIds) {
            List<Map<String, Object>> list;
            List<Pair<CmdbFilter, Map<String, Object>>> rules = ruleset.getRules();
            if (ruleset.hasFunction()) {
                String query = format("SELECT \"Id\" _id, %s(\"Id\") _val FROM %s WHERE \"Status\" = 'A'", quoteSqlIdentifier(storedFunction.getName()), entryTypeToSqlExpr(classe));
                if (cardIds != null) {
                    query += format(" AND \"Id\" IN (%s)", cardIds.stream().map(l -> l.toString()).collect(joining(",")));
                }
                list = dao.getJdbcTemplate().query(query, (r, i) -> map(ATTR_ID, r.getLong("_id"), storedFunction.getOnlyOutputParameter().getName(), r.getObject("_val")));
                rules = mapOutputKeywordToFunctionOutput(rules);
            } else {
                list = dao.selectAll().from(classe).accept(q -> {
                    if (cardIds != null) {
                        q.where(ATTR_ID, IN, cardIds);
                    }
                }).run().stream().map(r -> r.asMap()).collect(toList());
            }
            return applyRules(rules, list);
        }

        private List<Pair<CmdbFilter, Map<String, Object>>> mapOutputKeywordToFunctionOutput(List<Pair<CmdbFilter, Map<String, Object>>> rules) {
            String outputName = storedFunction.getOnlyOutputParameter().getName();
            return rules.stream().map(r -> Pair.of(r.getLeft().mapNames(FUNCTION_OUTPUT_KEYWORD, outputName), r.getRight())).collect(toList());
        }

        private Map<Long, Map<String, Object>> applyRules(List<Pair<CmdbFilter, Map<String, Object>>> rules, List<Map<String, Object>> cards) {
            return cards.stream().collect(toMap(r -> toLong(r.get("Id")), r -> {
                logger.trace("apply rules on record id = {}", r.get("Id"));
                for (Pair<CmdbFilter, Map<String, Object>> rule : rules) {
                    CmdbFilter filter = rule.getLeft();
                    if (matchRule(filter, r)) {
                        logger.trace("found match for rule = {} on record id = {}", filter, r.get("Id"));
                        return rule.getRight();
                    }
                }
                logger.trace("no rule match for record id = {}", r.get("Id"));
                return emptyMap();
            }));
        }

        private boolean matchRule(CmdbFilter filter, Map<String, Object> record) {
            logger.trace("test rule = {} on record id = {}", filter, record.get("Id"));
            if (filter.isNoop()) {
                return true;
            }
            filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
            if (filter.hasAttributeFilter()) {
                filter = filter.mapNames(classe.getAliasToAttributeMap());
                return AttributeFilterProcessor.<Map<String, Object>>builder().withKeyToValueFunction((k, c) -> {
                    checkArgument(
                            (storedFunction != null && equal(storedFunction.getOnlyOutputParameter().getName(), k))
                            || (classe.hasAttribute(k) && classe.getAttribute(k).hasServiceReadPermission()), "invalid attr =< %s > : attr not found or user not allowed to read", k);
                    return c.get(k);
                }).withAttributeTypeFunction((k) -> classe.hasAttribute((String) k) ? classe.getAttribute((String) k).getType() : (storedFunction != null && equal(storedFunction.getOnlyOutputParameter().getName(), k) ? storedFunction.getOnlyOutputParameter().getType() : null)).withFilter(filter.getAttributeFilter()).build().match(record);
            }
            return false;//TODO improve this
        }
    }

    private GisStyleRuleset parseData(GisStyleRulesetData data) {
        return GisStyleRulesetImpl.builder()
                .withCode(data.getCode())
                .withDescription(data.getDescription())
                .withFunction(data.getFunction())
                .withGisAttribute(gisAttributeRepository.getLayer(data.getGisAttribute()))
                .withId(data.getId())
                .withRules(parseRules(data.getRules()))
                .withParams(data.getParams())
                .build();
    }

    private GisStyleRulesetData serializeData(GisStyleRuleset rules) {
        return GisStyleRulesetDataImpl.builder()
                .withCode(rules.getCode())
                .withDescription(rules.getDescription())
                .withFunction(rules.getFunction())
                .withGisAttribute(rules.getGisAttribute().getId())
                .withId(rules.getId())
                .withOwner(rules.getGisAttribute().getOwnerClassName())
                .withParams(rules.getParams())
                .withRules(serializeRules(rules.getRules()))
                .build();
    }

}
