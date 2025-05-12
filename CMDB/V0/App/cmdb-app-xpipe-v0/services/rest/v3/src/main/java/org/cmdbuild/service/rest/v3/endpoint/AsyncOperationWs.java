package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import org.cmdbuild.asyncjob.AsyncRequestJob;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.asyncjob.AsyncRequestJobService;

@Path("async/")
@Produces(APPLICATION_JSON)
public class AsyncOperationWs {

    private final AsyncRequestJobService service;

    public AsyncOperationWs(AsyncRequestJobService service) {
        this.service = checkNotNull(service);
    }

    @GET
    @Path("jobs/{jobId}")
    public Object getAsyncJobStatus(@PathParam("jobId") Long jobId) {
        AsyncRequestJob job = service.getJobForCurrentUserById(jobId);
        return response(map(
                "_id", job.getId(),
                "status", job.isCompleted() ? "completed" : "running",
                "_completed", job.isCompleted()
        ));
    }

    @GET
    @Path("jobs/{jobId}/response")
    public Object getAsyncJobResult(@PathParam("jobId") Long jobId) {
        AsyncRequestJob job = service.getJobForCurrentUserById(jobId);
        checkArgument(job.isCompleted(), "cannot get result for job = %s: job is still running", jobId);
        service.deleteJob(job.getId());
        return new String(job.getResponseContent());//TODO status code, non--json response
    }

}
