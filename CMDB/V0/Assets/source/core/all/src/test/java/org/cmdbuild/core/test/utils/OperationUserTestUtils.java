/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.core.test.utils;

import org.cmdbuild.auth.multitenant.api.UserTenantContext;
import org.cmdbuild.auth.user.OperationUser;
import static org.cmdbuild.auth.user.OperationUserImpl.builder;
import org.cmdbuild.auth.grant.UserPrivileges;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.user.LoginUser;

/**
 *
 */
@Deprecated
public class OperationUserTestUtils {

	@Deprecated
	public static OperationUser newOperationUser(LoginUser authUser, UserPrivileges privilegeCtx, Role selectedGroup, UserTenantContext userTenantContext) {
		return builder().withAuthenticatedUser(authUser).withPrivilegeContext(privilegeCtx).withDefaultGroup(selectedGroup).withUserTenantContext(userTenantContext).build();
	}
}
