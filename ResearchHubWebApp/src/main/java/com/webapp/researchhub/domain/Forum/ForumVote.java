package com.webapp.researchhub.domain.Forum;

import com.webapp.researchhub.domain.MyUser;
import jakarta.persistence.*;

@Entity
public class ForumVote {
    @Id
    @GeneratedValue
    private Long id;
    private String type;
    @ManyToOne
    private ForumComment comment;
    @ManyToOne
    private ForumThread thread;
    @ManyToOne
    @JoinColumn(name = "user_id")

    private MyUser user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ForumComment getComment() {
        return comment;
    }

    public void setComment(ForumComment comment) {
        this.comment = comment;
    }

    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }

    public ForumThread getThread() {
        return thread;
    }

    public void setThread(ForumThread thread) {
        this.thread = thread;
    }
}
