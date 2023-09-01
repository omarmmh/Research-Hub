package com.webapp.researchhub.domain.Forum;

import com.webapp.researchhub.domain.MyUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@MappedSuperclass
public class ForumBaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @CreationTimestamp
    private Date dateCreated;

    @CreationTimestamp
    private Date dateModified;

    @Column(length = 655535)
    @NotNull
    private String body;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private MyUser user;

    private Long pageViewCount = 0L;

    public Long getPageViewCount() {
        return pageViewCount;
    }

    public void incrementPageViewCount() {
        this.pageViewCount += 1L;
    }

    public Long getId() {
        return id;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String description) {
        this.body = description;
    }

    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }
}
