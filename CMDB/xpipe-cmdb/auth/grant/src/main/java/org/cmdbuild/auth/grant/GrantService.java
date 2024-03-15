/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.grant;

import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

public interface GrantService extends GrantDataRepository {

    Collection<Grant> getAllPrivilegesByGroupId(long groupId);

    Collection<Grant> getClassPrivilegesByGroupId(long groupId);

    Collection<Grant> getViewPrivilegesByGroupId(long groupId);

    Collection<Grant> getFilterPrivilegesByGroupId(long groupId);

    Collection<Grant> getCustomPagesPrivilegesByGroupId(long groupId);

    Collection<Grant> getReportPrivilegesByGroupId(long groupId);
    
    Collection<Grant> getDashboardPrivilegesByGroupId(long groupId);

    List<GrantData> getGrantsForRoleIncludeRecordsWithoutGrant(long roleId);

    @Nullable
    String getGrantObjectDescription(GrantData grant);

    GrantData getGrantDataByRoleAndTypeAndName(Long id, PrivilegedObjectType objectType, String objectTypeName);

}
