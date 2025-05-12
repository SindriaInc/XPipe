package org.sindria.xpipe.core.policies.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="actions")
public class Action {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    @NotNull
    @NotBlank(message = "Name is mandatory")
    private String name;

    @Column
    @NotNull
    @NotBlank(message = "Uri is mandatory")
    private String uri;

    @Column
    @NotNull
    @NotBlank(message = "Method is mandatory")
    private String method;

//    // Relation one-to-many - one type for many policies
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @JoinColumn(name = "id", referencedColumnName = "type_id") // we need to duplicate the physical information
//    private Set<Type> types = new HashSet<>();

    // Relation one-to-many - one type for many policies
    //@OneToMany(mappedBy="type_id")
    //private Set<Type> types;

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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Action() {}

    /**
     * Action constructor
     */
    public Action(Long id, String name, String uri, String method) {
        super();
        this.id = id;
        this.name = name;
        this.uri = uri;
        this.method = method;
    }
}