package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import org.cmdbuild.asyncjob.AsyncRequestJob;
import org.cmdbuild.asyncjob.AsyncRequestJobService;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

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
