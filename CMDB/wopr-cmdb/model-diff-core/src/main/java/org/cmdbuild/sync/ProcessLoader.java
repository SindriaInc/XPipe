/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import java.util.Collection;
import org.cmdbuild.workflow.model.Process;

/**
 *
 * @author afelice
 */
public interface ProcessLoader {

    Collection<Process> getAllProcessColl();

    Process getProcess(String processClasseName);

}
