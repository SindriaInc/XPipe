package org.sindria.xpipe.academy.password.model;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="credentials")
public class Credential {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    @NotNull
    @NotBlank(message = "Name is mandatory")
    private String name;

    @Column
    @NotNull
    @NotBlank(message = "Value is mandatory")
    private String value;

    @Column
    @NotNull
    private String note;

    //password constructor
    public Credential(Long id) {
        super();
        this.id = id;
        this.name = name;
        this.value = value;
        this.note = note;
    }

    //no args constructor
   public Credential() {}


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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
