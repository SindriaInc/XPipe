/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.navtree;

import static com.google.common.base.Objects.equal;
import javax.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;

public interface NavTree extends NavTreeInfo {

    @Nullable
    Long getId();

    NavTreeNode getData();

    boolean getActive();

    NavTreeType getType();

    default NavTreeNode getNodeById(String nodeId) {
        return getData().getThisNodeAndAllDescendants().stream().filter(n -> equal(n.getId(), nodeId)).collect(onlyElement("node not found for id = %s", nodeId));
    }

}
