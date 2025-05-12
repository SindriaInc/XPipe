package org.cmdbuild.services.soap.operation;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toSet;

import org.cmdbuild.auth.login.AuthenticationStore;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.auth.userrole.UserGroup;
import org.cmdbuild.auth.user.UserInfo;
import org.springframework.stereotype.Component;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.user.LoginUser;

@Component
public class AuthenticationLogicHelper {

	private final OperationUserSupplier operationUser;
	private final AuthenticationStore authenticationStore;
	private final RoleRepository groupRepository;

	public AuthenticationLogicHelper(OperationUserSupplier operationUser, AuthenticationStore authenticationStore, RoleRepository groupRepository) {
		this.operationUser = checkNotNull(operationUser);
		this.authenticationStore = checkNotNull(authenticationStore);
		this.groupRepository = checkNotNull(groupRepository);
	}

	public UserInfo getUserInfo() {
		LoginUser authenticatedUser = operationUser.getUser().getLoginUser();
		UserInfo userInfo = new UserInfo();
		userInfo.setUsername(authenticatedUser.getUsername());
		userInfo.setGroups(operationUser.getUser().getLoginUser().getGroupNames().stream()
				.map((groupName) -> {
					Role group = groupRepository.getGroupWithName(groupName);
					return new UserGroup(group.getName(), group.getDescription());
				}).collect(toSet()));
		userInfo.setUserType(authenticationStore.getType());
		return userInfo;
	}

}
