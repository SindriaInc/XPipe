/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.grant;

import jakarta.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public interface GrantService extends GrantDataRepository {

    Collection<Grant> getAllPrivilegesByGroupId(long groupId);

    List<GrantData> getGrantsForRoleIncludeRecordsWithoutGrant(long roleId);

    @Nullable
    String getGrantObjectDescription(GrantData grant);

    GrantData getGrantDataByRoleAndTypeAndName(Long id, PrivilegedObjectType objectType, String objectTypeName);

}
