/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.xpdl;

import org.cmdbuild.common.annotations.Legacy;
import org.cmdbuild.workflow.model.Process;

public class SharkXpdlUtils {

	@Legacy("As in 1.x")
	public static String getStandardProcessDefinitionId(Process process) {
		return "Process_" + process.getName().toLowerCase();
	}

	@Legacy("As in 1.x")
	public static String getStandardPackageId(Process process) {
		return "Package_" + process.getName().toLowerCase();
	}

}
