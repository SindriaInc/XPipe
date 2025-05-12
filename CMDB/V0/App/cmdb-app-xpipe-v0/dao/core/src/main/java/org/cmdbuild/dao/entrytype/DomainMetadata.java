/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import java.util.Collection;
import java.util.Map;

public interface DomainMetadata extends EntryTypeMetadata {

    static final String DOMAIN_TYPE = "cm_type",
            CARDINALITY = "cm_cardinality",
            CLASS_1 = "cm_class1",
            CLASS_2 = "cm_class2",
            DESCRIPTION_1 = "cm_description1",
            DESCRIPTION_2 = "cm_description2",
            MASTERDETAIL = "cm_masterdetail",
            MASTERDETAIL_DESCRIPTION = "cm_masterdetail_label",
            MASTERDETAIL_FILTER = "cm_masterdetail_filter",
            MASTERDETAIL_AGGREGATE = "cm_masterdetail_aggregateattrs",
            MASTERDETAIL_DISABLEDATTRS = "cm_masterdetail_disabledcreateattrs",
            REFERENCE_FILTERS = "cm_reference_filters",
            CLASS_REFERENCE_FILTERS = "cm_class_reference_filters",
            DISABLED_1 = "cm_disabled_1",
            DISABLED_2 = "cm_disabled_2",
            INDEX_1 = "cm_index1",
            INDEX_2 = "cm_index2",
            INLINE_1 = "cm_show_inline_1",
            INLINE_2 = "cm_show_inline_2",
            DEFAULT_CLOSED_1 = "cm_show_inline_default_closed_1",
            DEFAULT_CLOSED_2 = "cm_show_inline_default_closed_2",
            CASCADE_ACTION_DIRECT = "cm_cascade_direct",
            CASCADE_ACTION_INVERSE = "cm_cascade_inverse",
            CASCADE_ACTION_DIRECT_ASK_CONFIRM = "cm_cascade_direct_ask_confirm",
            CASCADE_ACTION_INVERSE_ASK_CONFIRM = "cm_cascade_inverse_ask_confirm";

    String getDirectDescription();

    String getInverseDescription();

    DomainCardinality getCardinality();

    boolean isMasterDetail();

    String getMasterDetailDescription();

    String getMasterDetailFilter();
    
    Map<String, String> getReferenceFilters();
    
    /**
     * Map <code>{Classe.name= Ecql_filter_string}</code>.
     * With:
     * <ul>
     * <li><dt>Classe.name <dd>the unique name of {@link Classe}
     * <li><dt><code>Ecql_filter_string</code> <dd>the string containing the Ecql
     * </li>
     * @return empty map if no reference filters defined at domain level for related classes
     */
    Map<String, String> getClassReferenceFilters();
    
    Collection<String> getDisabledSourceDescendants();

    Collection<String> getDisabledTargetDescendants();

    Collection<String> getMasterDetailAggregateAttrs();

    Collection<String> getMasterDetailDisabledCreateAttrs();

    int getIndexForSource();

    int getIndexForTarget();

    boolean isSourceInline();

    boolean isSourceDefaultClosed();

    boolean isTargetInline();

    boolean isTargetDefaultClosed();

    CascadeAction getCascadeActionDirect();

    CascadeAction getCascadeActionInverse();

    boolean getCascadeActionDirectAskConfirm();

    boolean getCascadeActionInverseAskConfirm();

}
