/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.postgres.q3;

import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.common.utils.PagingInfo;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;

public interface DaoQueryOptions extends PagingInfo {

    Set<String> getAttrs();

    boolean getOnlyGridAttrs();

    CmdbSorter getSorter();

    CmdbFilter getFilter();

    long getPositionOf();

    boolean getGoToPage();

    boolean hasPositionOf();

    DaoQueryOptions mapAttrNames(Map<String, String> map);

    DaoQueryOptions withOffset(@Nullable Long offset);

    DaoQueryOptions expandFulltextFilter(Classe userclass);

    DaoQueryOptions withoutPaging();

    default boolean hasAttrs() {
        return !getAttrs().isEmpty();
    }

    default boolean isPaged() {
        return PagedElements.isPaged(getOffset(), getLimit());
    }

    default boolean isPagedAndHasFullPage(int cardCount) {
        return isPaged() && (!hasLimit() || getLimit() == cardCount);
    }

    default boolean hasOffset() {
        return PagedElements.hasOffset(getOffset());
    }

    default boolean hasLimit() {
        return PagedElements.hasLimit(getLimit());
    }

}
