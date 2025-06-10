package org.sindria.xpipe.core.policies.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;

import java.util.Set;

@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    @NotNull
    @NotBlank(message = "UUID is mandatory")
    private String uuid;

//    @ManyToMany(mappedBy = "policies")
//    Set<Policy> users;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public User() {}

    /**
     * User constructor
     */
    public User(Long id, String uuid) {
        super();
        this.id = id;
        this.uuid = uuid;
    }
}
