package com.webapp.researchhub.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import java.util.List;


@Entity
public class Role {
    @Id
    private String name;

    @ManyToMany(mappedBy = "roles")
    private List<MyUser> users;

    public Role() { }

    public Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
