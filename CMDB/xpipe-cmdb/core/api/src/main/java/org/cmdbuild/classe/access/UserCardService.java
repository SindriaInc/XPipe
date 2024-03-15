/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.access;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.core.q3.BasicWhereMethods;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import static org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl.emptyOptions;
import org.cmdbuild.dao.driver.postgres.q3.stats.DaoStatsQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.stats.StatsQueryResponse;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.dao.entrytype.Classe;

public interface UserCardService {

    static final String FOR_DOMAIN_HAS_THIS_RELATION = "_fordomain_hasthisrelation", FOR_DOMAIN_HAS_ANY_RELATION = "_fordomain_hasanyrelation";

    Card getUserCard(String classId, long cardId);

    Card getUserCardInfo(String classId, long cardId);

    boolean userCanReadCard(String classId, long cardId);

    Card createCard(String classId, Map<String, Object> values);

    Card updateCard(String classId, long cardId, Map<String, Object> values);

    void deleteCard(String classId, long cardId);

    UserCardAccess getUserCardAccess(String classId);

    PagedElements<Card> getUserCards(String classId, UserCardQueryOptions options, Pair<String, Function<Classe, Consumer<BasicWhereMethods>>> where);

    StatsQueryResponse getStats(String classId, DaoQueryOptions options, DaoStatsQueryOptions query);

    void deleteCards(String classId, CmdbFilter filter);

    void updateCards(String classId, CmdbFilter filter, Map<String, Object> values);

    SuperclassUserQueryHelper getSuperclassQueryHelper(Classe superClass);

    default PagedElements<Card> getUserCards(String classId, UserCardQueryOptions options) {
        return getUserCards(classId, options, Pair.of("", (c) -> w -> {
        }));
    }

    default PagedElements<Card> getUserCards(String classId, @Nullable DaoQueryOptions queryOptions) {
        return getUserCards(classId, UserCardQueryOptionsImpl.builder().withQueryOptions(queryOptions).build());
    }

    default PagedElements<Card> getUserCards(String classId) {
        return getUserCards(classId, emptyOptions());
    }

}
