package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Stopwatch;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.lang.String.format;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import javax.annotation.security.RolesAllowed;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ACCESS_AUTHORITY;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.classe.access.UserDomainService;
import static org.cmdbuild.common.Constants.BASE_CLASS_NAME;
import static org.cmdbuild.common.Constants.LOOKUP_CLASS_NAME;
import static org.cmdbuild.common.Constants.ROLE_CLASS_NAME;
import static org.cmdbuild.common.Constants.USER_CLASS_NAME;
import org.cmdbuild.dao.beans.Card;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDCLASS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_USER;
import static org.cmdbuild.dao.constants.SystemAttributes.STANDARD_CLASS_INFO_ATTRIBUTES;
import static org.cmdbuild.dao.constants.SystemAttributes.SYSTEM_ATTRIBUTE_ALIASES;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.entrytype.DomainCardinality.MANY_TO_ONE;
import static org.cmdbuild.dao.entrytype.DomainCardinality.ONE_TO_MANY;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FOREIGNKEY;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.LOOKUP;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import org.cmdbuild.dao.postgres.utils.SqlQueryUtils;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.pgObjectToString;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;
import org.cmdbuild.data.filter.SorterElement;
import static org.cmdbuild.data.filter.SorterElementDirection.ASC;
import static org.cmdbuild.data.filter.SorterElementDirection.DESC;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.toLowerCaseOrNull;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("globalsearch")
@Produces(APPLICATION_JSON)
public class GlobalSearchWs {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final UserClassService classService;
    private final UserDomainService domainService;

    private static final String EMAIL_CLASS = "Email";

    public GlobalSearchWs(DaoService dao, UserClassService classService, UserDomainService domainService) {
        this.dao = checkNotNull(dao);
        this.classService = checkNotNull(classService);
        this.domainService = checkNotNull(domainService);
    }

    @GET
    @Path("")
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
    public Object globalSearch(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, WsQueryOptions wsQueryOptions) {
        Stopwatch totalStopwatch = Stopwatch.createStarted();
        String query = wsQueryOptions.getQuery().getFilter().getFulltextFilter().getQuery();
        logger.trace("global search =< {} >", query);
        Stream<Domain> domains = domainService.getActiveUserDomains().stream().filter(d -> d.hasCardinality(ONE_TO_MANY) || d.hasCardinality(MANY_TO_ONE));

        Set<Classe> classesFromDomains = set();
        domains.forEach(d -> {
            classesFromDomains.addAll(classService.getActiveUserClasses().stream().filter(c -> c.equalToOrDescendantOf(d.getSourceClass())).collect(toList()));
            classesFromDomains.addAll(classService.getActiveUserClasses().stream().filter(c -> c.equalToOrDescendantOf(d.getSourceClass())).collect(toList()));
        });
        // adding and removing special classes
        classesFromDomains.addAll(list(classService.getUserClassOrNull(LOOKUP_CLASS_NAME), classService.getUserClassOrNull(USER_CLASS_NAME), classService.getUserClassOrNull(ROLE_CLASS_NAME)).filter(Objects::nonNull));
        classesFromDomains.remove(dao.getClasse(BASE_CLASS_NAME));

        String sqlClass = format("SELECT %s FROM %s WHERE %s = 'A' AND %s IN (%s)",
                STANDARD_CLASS_INFO_ATTRIBUTES.stream().map(SqlQueryUtils::quoteSqlIdentifier).collect(joining(", ")),
                quoteSqlIdentifier(BASE_CLASS_NAME),
                quoteSqlIdentifier(ATTR_STATUS),
                quoteSqlIdentifier(ATTR_IDCLASS),
                classesFromDomains.stream().map(c -> format("'%s'::regclass", quoteSqlIdentifier(c.getName()))).collect(joining(", ")));

        Stopwatch codeDescrStopwatch = Stopwatch.createStarted();
        List<Map<String, Object>> codeDescrMatches = dao.getJdbcTemplate().queryForList(sqlClass);
        logger.trace("executing global search query on =< Code, Description > on {}, elapsed: {}", classesFromDomains, codeDescrStopwatch);

        // remove system classes
        codeDescrMatches.removeIf(r -> pgObjectToString(r.get(ATTR_IDCLASS)).startsWith("_") || pgObjectToString(r.get(ATTR_IDCLASS)).equals(EMAIL_CLASS));

        // remove all records not containing the text searched
        String textToSearch = toLowerCaseOrNull(query);
        codeDescrMatches.removeIf(r -> {
            String code = toLowerCaseOrNull(r.get(ATTR_CODE));
            String description = toLowerCaseOrNull(r.get(ATTR_DESCRIPTION));
            return code == null ? true : !code.contains(textToSearch) && description == null ? true : !description.contains(textToSearch);
        });
        logger.trace("Code, Description matches on =< {} >", codeDescrMatches);

        Stream<Classe> classes = classService.getAllUserClasses().stream().filter(c -> !equal(c.getName(), BASE_CLASS_NAME) && !c.isSuperclass() && c.isStandardClass());

        List<Card> cards = list();
        classes.forEach(c -> {
            String attributesQuery = c.getActiveUiAttributes().stream().map(a -> {
                return switch (a.getType().getName()) {
                    case LOOKUP -> {
                        String lookupIds = codeDescrMatches.stream().filter(r -> pgObjectToString(r.get(ATTR_IDCLASS)).equals(LOOKUP_CLASS_NAME)).map(r -> toStringNotBlank(r.get(ATTR_ID))).collect(joining(", "));
                        yield isBlank(lookupIds) ? "" : format("%s IN (%s)", quoteSqlIdentifier(a.getName()), lookupIds);
                    }
                    case REFERENCE, FOREIGNKEY -> {
                        Classe relatedClasse = switch (a.getType().getName()) {
                            case REFERENCE ->
                                domainService.getDomain(a.getMetadata().getDomain()).getReferencedClass(a);
                            case FOREIGNKEY ->
                                classService.getUserClass(a.getForeignKeyDestinationClassName());
                            default ->
                                throw unsupported("unsopported attribute type =< {} >", a.getType().getName());
                        };
                        String referenceIds = codeDescrMatches.stream().filter(r -> relatedClasse.equalToOrAncestorOf(dao.getClasse(pgObjectToString(r.get(ATTR_IDCLASS)))) || dao.getClasse(pgObjectToString(r.get(ATTR_IDCLASS))).equalToOrAncestorOf(relatedClasse)).map(r -> toStringNotBlank(r.get(ATTR_ID))).collect(joining(", "));
                        logger.trace("class {} is reference of attribute {} - ids =< {} >", relatedClasse.getName(), a.getName(), referenceIds);
                        yield isBlank(referenceIds) ? "" : format("%s IN (%s)", quoteSqlIdentifier(a.getName()), referenceIds);
                    }
                    case FORMULA ->
                        "";
                    default ->
                        format("%s::text ILIKE '%%%s%%'", quoteSqlIdentifier(a.getName()), query);
                };
            }).filter(StringUtils::isNotEmpty).collect(joining(" OR "));

            logger.trace("executing global search query for {} with where {}", c.getName(), attributesQuery);
            Stopwatch classStopwatch = Stopwatch.createStarted();
            dao.select(set(STANDARD_CLASS_INFO_ATTRIBUTES).with(ATTR_USER, ATTR_BEGINDATE)).from(c).whereExpr(isBlank(attributesQuery) ? "" : format("%s", attributesQuery)).getCards().forEach(cards::add);
            logger.trace("executing global search query for {}, elapsed: {}", c.getName(), classStopwatch);
        });
        logger.debug("found {} elements that matches to {}, elapsed: {}", cards.size(), query, totalStopwatch);

        SorterElement sorter = wsQueryOptions.getQuery().getSorter().getElements().stream().collect(onlyElement("global search cannot be ordered with more than one attribute"));
        Comparator<Card> cardSorter = reorderComparator(Comparator.comparing(c -> c.getString(sorter.getProperty())), sorter);
        Comparator<Card> classSorter = Comparator.comparing(Card::getTypeName);

        return response(cards.stream().sorted(cardSorter).sorted(classSorter).map(this::cardToMap));
    }

    private Map<String, Object> cardToMap(Card card) {
        return map(
                SYSTEM_ATTRIBUTE_ALIASES.get(ATTR_ID), card.getId(),
                SYSTEM_ATTRIBUTE_ALIASES.get(ATTR_IDCLASS), card.getTypeName(),
                SYSTEM_ATTRIBUTE_ALIASES.get(ATTR_USER), card.getUser(),
                SYSTEM_ATTRIBUTE_ALIASES.get(ATTR_BEGINDATE), toIsoDateTime(card.getBeginDate()),
                ATTR_CODE, card.getCode(),
                ATTR_DESCRIPTION, card.getDescription()
        );
    }

    private Comparator reorderComparator(Comparator<Card> comparator, SorterElement sorter) {
        return switch (sorter.getDirection()) {
            case DESC ->
                comparator;
            case ASC ->
                comparator.reversed();
        };
    }

}
