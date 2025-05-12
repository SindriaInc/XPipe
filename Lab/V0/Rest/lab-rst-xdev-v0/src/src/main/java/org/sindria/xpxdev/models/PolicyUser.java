package org.sindria.xpxdev.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="policy_user")
public class PolicyUser {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    //@NotBlank(message = "Policy ID is mandatory")
    private Long policy_id;

    @NotNull
    //@NotBlank(message = "User ID is mandatory")
    private String user_id;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPolicyId() {
        return policy_id;
    }

    public void setPolicyId(Long policyId) {
        this.policy_id = policyId;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String userId) {
        this.user_id = userId;
    }

    public PolicyUser() {}

    /**
     * PolicyUser constructor
     */
    public PolicyUser(Long id, Long policyId, String userId) {
        super();
        this.id = id;
        this.policy_id = policyId;
        this.user_id = userId;
    }
}
