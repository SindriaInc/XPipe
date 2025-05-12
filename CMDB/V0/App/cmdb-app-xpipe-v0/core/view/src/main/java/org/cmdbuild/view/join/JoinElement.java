/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.view.join;

import javax.annotation.Nullable;
import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.data.filter.CmdbFilter;

public interface JoinElement {

    String getSource();

    String getDomain();

    @Nullable
    String getTargetType();

    String getDomainAlias();

    String getTargetAlias();

    RelationDirection getDirection();

    JoinType getJoinType();

}
