/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cardfilter;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class CardFilterAsDefaultForClassImpl implements CardFilterAsDefaultForClass {

    private final String forClass;
    private final StoredFilter filter;
    private final long roleId;

    public CardFilterAsDefaultForClassImpl(StoredFilter filter, String forClass, Long forRole) {
        this.forClass = checkNotBlank(forClass);
        this.filter = checkNotNull(filter);
        this.roleId = forRole;
    }

    @Override
    public StoredFilter getFilter() {
        return filter;
    }

    @Override
    public String getDefaultForClass() {
        return forClass;
    }

    @Override
    public long getDefaultForRole() {
        return roleId;
    }

    @Override
    public String toString() {
        return "CardFilterAsDefaultForClass{" + "class=" + forClass + ", filter=" + filter + ", roleId=" + roleId + '}';
    }

}
