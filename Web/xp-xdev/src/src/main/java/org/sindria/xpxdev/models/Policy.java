package org.sindria.xpxdev.models;

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

//    @NotNull
//    @NotBlank(message = "Type id is mandatory")
//    private Long typeId;

    // Relation many-to-one - many policies have one type at a time
    @ManyToOne
    @JoinColumn(name="type_id", nullable=false)
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
    public Policy(Long id, String name, String content, Type type) {
        super();
        this.id = id;
        this.name = name;
        this.content = content;
        this.type = type;
    }
}
