package org.sindria.xppipelines.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="pipeline_user")
public class PipelineUser {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    //@NotBlank(message = "Pipeline ID is mandatory")
    private Long pipeline_id;

    @NotNull
    //@NotBlank(message = "User ID is mandatory")
    private String user_id;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPipelineId() {
        return pipeline_id;
    }

    public void setPipelineId(Long pipelineId) {
        this.pipeline_id = pipelineId;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String userId) {
        this.user_id = userId;
    }

    public PipelineUser() {}

    /**
     * PipelineUser constructor
     */
    public PipelineUser(Long id, Long pipelineId, String userId) {
        super();
        this.id = id;
        this.pipeline_id = pipelineId;
        this.user_id = userId;
    }
}
