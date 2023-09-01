package com.webapp.researchhub.domain.Forum;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ForumCategory extends ForumBaseEntity {

    @NotNull
    private String title;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "category")
    List<ForumThread> forumThreadList = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ForumThread> getForumThreadList() {
        return forumThreadList;
    }
}
