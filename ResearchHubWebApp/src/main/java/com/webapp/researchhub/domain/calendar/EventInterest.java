package com.webapp.researchhub.domain.calendar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.webapp.researchhub.domain.MyUser;
import jakarta.persistence.*;

@Entity
public class EventInterest {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    @JsonIgnore
    private Event event;

    @ManyToOne
    @JsonIgnore
    private MyUser users;

    public EventInterest(Event event, MyUser user) {
        this.event = event;
    }

    public EventInterest() {
    }

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public MyUser getUser() {
        return this.users;
    }

    public void setUsers(MyUser users) {
        this.users = users;
    }
}
