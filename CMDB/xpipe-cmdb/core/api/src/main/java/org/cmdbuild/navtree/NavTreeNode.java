/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.navtree;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.utils.json.JsonBean;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@JsonBean(NavTreeNodeImpl.class)
public interface NavTreeNode {

    String getId();

    @Nullable
    String getParentId();

    String getTargetClassName();

    String getTargetClassDescription();

    @Nullable
    String getDomainName();

    RelationDirection getDirection();

    boolean getShowOnlyOne();

    List<NavTreeNode> getChildNodes();

    @Nullable
    String getTargetFilter();

    boolean getEnableRecursion();

    NavTreeNodeSubclassViewMode getSubclassViewMode();

    boolean getSubclassViewShowIntermediateNodes();

    List<String> getSubclassFilter();

    Map<String, String> getSubclassDescriptions();

    default boolean hasChildNodes() {
        return !getChildNodes().isEmpty();
    }

    default List<NavTreeNode> getThisNodeAndAllDescendants() {
        return list(this).accept(l -> getChildNodes().stream().map(NavTreeNode::getThisNodeAndAllDescendants).forEach(l::addAll));
    }

    default boolean hasParent() {
        return isNotBlank(getParentId());
    }

    default boolean hasFilter() {
        return isNotBlank(getTargetFilter());
    }

    default boolean getDirect() {
        return getDirection().isDirect();
    }
}
