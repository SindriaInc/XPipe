package org.sindria.xpipe.core.policies.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name="policies")
public class Policy {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    @NotNull
    @NotBlank(message = "Name is mandatory")
    private String name;

    @Column(columnDefinition = "TEXT")
    @NotNull
    @NotBlank(message = "Content is mandatory")
    private String content;

    @NotNull
    @Column(name = "type_id")
    private Long typeId;

    // Relation many-to-one - many policies have one type at a time
    @ManyToOne(targetEntity = Type.class, fetch = FetchType.EAGER)
    @JoinColumn(name="type_id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Type type;

//    @ManyToMany
//    @JoinTable(
//            name = "policy_user",
//            joinColumns = @JoinColumn(name = "policy_id"),
//            inverseJoinColumns = @JoinColumn(name = "user_id"))
//    Set<User> policies;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Policy() {}

    /**
     * Policy constructor
     */
    public Policy(Long id, String name, String content, Long typeId) {
        super();
        this.id = id;
        this.name = name;
        this.content = content;
        this.typeId = typeId;
        //this.type = type;
    }
}
