package org.sindria.xpipe.core.policies.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="capabilities")
public class Capability {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    @NotNull
    @NotBlank(message = "Name is mandatory")
    private String name;

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

    public Capability() {}

    /**
     * Capability constructor
     */
    public Capability(Long id, String name) {
        super();
        this.id = id;
        this.name = name;
    }
}