package org.sindria.xpipe.core.policies.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="action_capability")
public class ActionCapability {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private Long action_id;

    @NotNull
    private Long capability_id;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActionId() {
        return action_id;
    }

    public void setActionId(Long actionId) {
        this.action_id = actionId;
    }

    public Long getCapabilityId() {
        return capability_id;
    }

    public void setCapabilityId(Long capabilityId) {
        this.capability_id = capabilityId;
    }

    public ActionCapability() {}

    /**
     * ActionCapability constructor
     */
    public ActionCapability(Long id, Long actionId, Long capabilityId) {
        super();
        this.id = id;
        this.action_id = actionId;
        this.capability_id = capabilityId;
    }
}
