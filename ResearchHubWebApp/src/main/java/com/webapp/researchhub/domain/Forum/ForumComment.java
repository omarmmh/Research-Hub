package com.webapp.researchhub.domain.Forum;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ForumComment extends ForumBaseEntity {
    @ManyToOne
    ForumThread thread;

    @OneToMany(cascade = CascadeType.ALL)
    List<ForumVote> voteList = new ArrayList<>();

    public List<ForumVote> getVoteList() {
        return voteList;
    }

    public ForumThread getThread() {
        return thread;
    }

    public void setThread(ForumThread thread) {
        this.thread = thread;
    }

    public int getVoteCount() {
        int count = 0;
        for (ForumVote v : voteList) {
            if (v.getType().equals("up")) {
                count++;
            } else if (v.getType().equals("down")) {
                count--;
            }
        }
        return count;
    }
}
