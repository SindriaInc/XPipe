/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.grant;

import java.util.List;

public interface UserPrivilegesForObject {

	GroupOfPrivileges getMinPrivilegesForAllRecords();

	GroupOfPrivileges getMaxPrivilegesForSomeRecords();

	List<GroupOfPrivileges> getPrivilegeGroupsWithFilter();

	boolean hasPrivilegesWithFilter();

}
