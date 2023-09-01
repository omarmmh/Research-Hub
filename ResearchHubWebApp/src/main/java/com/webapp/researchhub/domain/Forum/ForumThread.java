package com.webapp.researchhub.domain.Forum;

import com.webapp.researchhub.domain.MyUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ForumThread extends ForumBaseEntity {
    @NotNull
    @Column(length = 200)
    private String title;


    @ManyToMany(cascade = CascadeType.ALL)
    List<ForumComment> commentList = new ArrayList<>();

    @ManyToOne
    ForumCategory category;

    @ManyToMany
    List<MyUser> votedUsers = new ArrayList<>();

    public List<MyUser> getVotedUsers() {
        return votedUsers;
    }

    public void setVotedUsers(List<MyUser> votedUsers) {
        this.votedUsers = votedUsers;
    }

    public ForumCategory getCategory() {
        return category;
    }

    public void setCategory(ForumCategory category) {
        this.category = category;
    }

    public List<ForumComment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<ForumComment> commentList) {
        this.commentList = commentList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        if (getBody().length() < 300) {
            return getBody();
        } else {
            return getBody().substring(0, 300) + "...";
        }
    }

    @Override
    public String toString() {
        return "ForumThread{" +
                "title='" + title + '\'' +
                ", body='" + getBody() + '\'' +
                ", category=" + category + '\'' +
                ", user='" + getUser() +
                '}';
    }
}
