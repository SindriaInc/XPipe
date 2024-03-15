package org.sindria.xppipelines.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name="pipelines")
public class Pipeline {

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

//    @ManyToMany
//    @JoinTable(
//            name = "pipeline_user",
//            joinColumns = @JoinColumn(name = "pipeline_id"),
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

    public Pipeline() {}

    /**
     * Policy constructor
     */
    public Pipeline(Long id, String name, String content) {
        super();
        this.id = id;
        this.name = name;
        this.content = content;
    }
}
