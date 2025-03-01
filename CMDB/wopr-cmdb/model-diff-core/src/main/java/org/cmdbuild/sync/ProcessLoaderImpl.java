/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.inner.ProcessRepository;
import org.cmdbuild.workflow.model.Process;
import org.springframework.stereotype.Component;

/**
 * <ob>Note</b>: I refuse to use {@link WorkflowService}, that has 24 services
 * as dependencies in its implementation. And only loading is needed, because
 * {@link Process} are not created/modified/deleted in <i>mobile offline</i>.
 * Some method of {@link WorkflowService} is so replicated here.
 *
 * @author afelice
 */
@Component
public class ProcessLoaderImpl implements ProcessLoader {

    private final ProcessRepository processRepository;

    public ProcessLoaderImpl(ProcessRepository processRepository) {
        this.processRepository = checkNotNull(processRepository);
    }

    /**
     * As was in <code>WorkflowService.getActiveProcessClasses()</code>
     *
     * @return
     */
    @Override
    public Collection<Process> getAllProcessColl() {
        return processRepository.getAllPlanClassesForCurrentUser().stream().filter(p -> p.isActive() && p.isSuperclass() ? true : p.hasPlan()).collect(toList());
    }

    /**
     * As was in <code>WorkflowService.getProcess(String processId)</code>
     *
     * @param processClasseName
     * @return
     */
    @Override
    public Process getProcess(String processClasseName) {
        return processRepository.getProcessClassByName(processClasseName);
    }

}
