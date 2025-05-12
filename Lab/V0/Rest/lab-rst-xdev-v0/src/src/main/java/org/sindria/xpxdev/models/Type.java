package org.sindria.xpxdev.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name="types")
public class Type {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    @NotNull
    @NotBlank(message = "Name is mandatory")
    private String name;

    @Column(unique = true)
    @NotNull
    @NotBlank(message = "Short name is mandatory")
    private String shortName;

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

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Type() {}

    /**
     * Type constructor
     */
    public Type(Long id, String name, String shortName) {
        super();
        this.id = id;
        this.name = name;
        this.shortName = shortName;
    }
}