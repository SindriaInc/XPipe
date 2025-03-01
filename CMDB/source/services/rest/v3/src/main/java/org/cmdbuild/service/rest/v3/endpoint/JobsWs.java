package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyMap;
import java.util.Map;
import jakarta.annotation.Nullable;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.WILDCARD;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.cmdbuild.common.utils.PagedElements;

import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobRun;
import static org.cmdbuild.jobs.JobRunStatusImpl.serializeJobRunStatus;
import org.cmdbuild.jobs.JobService;
import org.cmdbuild.jobs.beans.JobDataImpl;
import org.cmdbuild.jobs.beans.JobDataImpl.JobDataImplBuilder;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.v3.endpoint.AuditWs.serializeErrors;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import jakarta.annotation.security.RolesAllowed;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_JOBS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_JOBS_VIEW_AUTHORITY;
import static org.cmdbuild.dao.utils.CmFilterProcessingUtils.mapFilter;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmInlineUtils.flattenMaps;
import static org.cmdbuild.utils.lang.CmInlineUtils.unflattenMaps;

@Path("jobs/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_JOBS_VIEW_AUTHORITY)
public class JobsWs {

    private final JobService service;

    public JobsWs(JobService service) {
        this.service = checkNotNull(service);
    }

    @GET
    @Path(EMPTY)
    public Object readMany(WsQueryOptions wsQueryOptions) {
        return response(paged(list(service.getAllJobs())
                .map(wsQueryOptions.isDetailed() ? this::serializeDetailedJob : this::serializeBasicJob)
                .withOnly(mapFilter(wsQueryOptions.getQuery().getFilter())), wsQueryOptions.getQuery()));
    }

    @GET
    @Path("{jobId}")
    public Object readOne(@PathParam("jobId") String jobId) {
        JobData job = service.getOneByIdOrCode(jobId);
        return response(serializeDetailedJob(job));
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_JOBS_MODIFY_AUTHORITY)
    public Object create(WsJobData data) {
        JobData job = service.createJob(data.toJobData().build());
        return response(serializeDetailedJob(job));
    }

    @PUT
    @Path("{jobId}")
    @RolesAllowed(ADMIN_JOBS_MODIFY_AUTHORITY)
    public Object update(@PathParam("jobId") String jobId, WsJobData data) {
        JobData job = service.updateJob(data.toJobData().withId(service.getOneByIdOrCode(jobId).getId()).build());
        return response(serializeDetailedJob(job));
    }

    @DELETE
    @Path("{jobId}")
    @RolesAllowed(ADMIN_JOBS_MODIFY_AUTHORITY)
    public Object delete(@PathParam("jobId") String jobId) {
        service.deleteJob(service.getOneByIdOrCode(jobId).getId());
        return success();
    }

    @POST
    @Path("{jobId}/run")
    @Consumes(WILDCARD)
    @RolesAllowed(ADMIN_JOBS_MODIFY_AUTHORITY)
    public Object runJobNow(@PathParam("jobId") String jobId, @Nullable WsJobRunTriggerData triggerData) {
        JobRun jobRun = service.runJob(service.getOneByIdOrCode(jobId).getId(), (Map) firstNonNull(triggerData == null ? null : triggerData.config, emptyMap()));
        return response(serializeDetailedJobRun(jobRun));
    }

    @GET
    @Path("{jobId}/runs")
    public Object getJobRunsForJob(@PathParam("jobId") String jobId, WsQueryOptions wsQueryOptions) {
        PagedElements<JobRun> res = service.getJobRuns(service.getOneByIdOrCode(jobId).getId(), wsQueryOptions.getQuery());
        return response(res.map(wsQueryOptions.isDetailed() ? this::serializeDetailedJobRun : this::serializeBasicJobRun));
    }

    @GET
    @Path("{jobId}/errors")
    public Object getJobRunErrorsForJob(@PathParam("jobId") String jobId, @QueryParam(START) Long offset, @QueryParam(LIMIT) Long limit) {//TODO fix this, use standard get 
        PagedElements<JobRun> res = service.getJobErrors(service.getOneByIdOrCode(jobId).getId(), DaoQueryOptionsImpl.builder().withPaging(offset, limit).build());
        return response(res.map(this::serializeBasicJobRun));
    }

    @GET
    @Path("_ANY/runs")
    public Object getJobRuns(WsQueryOptions wsQueryOptions) {
        PagedElements<JobRun> res = service.getJobRuns(wsQueryOptions.getQuery());
        return response(res.map(wsQueryOptions.isDetailed() ? this::serializeDetailedJobRun : this::serializeBasicJobRun));
    }

    @GET
    @Path("_ANY/errors")
    public Object getJobRunErrors(@QueryParam(START) Long offset, @QueryParam(LIMIT) Long limit) {//TODO fix this, use standard get 
        PagedElements<JobRun> res = service.getJobErrors(DaoQueryOptionsImpl.builder().withPaging(offset, limit).build());
        return response(res.map(this::serializeBasicJobRun));
    }

    @GET
    @Path("{jobId}/runs/{runId}")
    public Object getJobRun(@PathParam("jobId") String jobId, @PathParam("runId") Long runId) {
        JobRun jobRun = service.getJobRun(runId);
        return response(serializeDetailedJobRun(jobRun));
    }

    @GET
    @Path("_ANY/runs/stats")
    public Object readJobRunStats() {
        return response(map(service.getJobRunStats().getJobRunCountByStatus()).mapKeys(CmConvertUtils::serializeEnum));
    }

    private FluentMap<String, Object> serializeBasicJobRun(JobRun jobRun) {
        return map(
                "_id", jobRun.getId(),
                "jobCode", jobRun.getJobCode(),
                "status", serializeJobRunStatus(jobRun.getJobStatus()),
                "completed", jobRun.isCompleted(),
                "nodeId", jobRun.getNodeId(),
                "timestamp", toIsoDateTime(jobRun.getTimestamp()),
                "elapsedMillis", jobRun.getElapsedTime());
    }

    private Object serializeDetailedJobRun(JobRun jobRun) {
        return serializeBasicJobRun(jobRun).with(
                "errors", serializeErrors(jobRun.getErrorOrWarningEvents()),
                "logs", jobRun.getLogs(),
                "meta", jobRun.getMetadata());
    }

    private FluentMap<String, Object> serializeDetailedJob(JobData jobData) {
        return serializeBasicJob(jobData).with("config", unflattenMaps(jobData.getConfig()));
    }

    private FluentMap<String, Object> serializeBasicJob(JobData jobData) {
        return map(
                "_id", jobData.getId(),
                "code", jobData.getCode(),
                "description", jobData.getDescription(),
                "type", jobData.getType(),
                "cronExpression", jobData.getConfig().get("cronExpression"),
                "enabled", jobData.isEnabled());
    }

    public static class WsJobRunTriggerData {

        private final Map<String, Object> config;

        public WsJobRunTriggerData(@JsonProperty("config") Map<String, Object> config) {
            this.config = config;
        }
    }

    public static class WsJobData {

        private final String code, description, type;
        private final Boolean enabled;
        private final Map<String, Object> config;

        public WsJobData(
                @JsonProperty("code") String code,
                @JsonProperty("description") String description,
                @JsonProperty("type") String type,
                @JsonProperty("enabled") Boolean enabled,
                @JsonProperty("config") Map<String, Object> config) {
            this.code = code;
            this.description = description;
            this.type = type;
            this.enabled = enabled;
            this.config = config;
        }

        public JobDataImplBuilder toJobData() {
            return JobDataImpl.builder()
                    .withCode(code)
                    .withConfig(flattenMaps(config))
                    .withDescription(description)
                    .withEnabled(enabled)
                    .withType(type);
        }

    }
}
