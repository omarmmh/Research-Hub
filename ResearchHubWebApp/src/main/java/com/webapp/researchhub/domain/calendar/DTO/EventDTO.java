package com.webapp.researchhub.domain.calendar.DTO;

import java.util.Date;

public class EventDTO {
    private long id;

    private String title;

    private String description;

    private Date startDate;

    private Date endDate;

    private boolean isVirtual;

    private String location;

    private String organiser;

    private long organiserId;

    private Date dateModified;

    public EventDTO(long id, String title, String description, Date startDate, Date endDate, boolean isVirtual, String location, String organiser, long organiserId, Date dateModified) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isVirtual = isVirtual;
        this.location = location;
        this.organiser = organiser;
        this.organiserId = organiserId;
        this.dateModified = dateModified;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public EventDTO() {
    }


    public long getOrganiserId() {
        return organiserId;
    }

    public void setOrganiserId(long organiserId) {
        this.organiserId = organiserId;
    }

    public boolean getIsVirtual() {
        return isVirtual;
    }

    public void setVirtual(boolean virtual) {
        isVirtual = virtual;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getOrganiser() {
        return organiser;
    }

    public void setOrganiser(String organiser) {
        this.organiser = organiser;
    }
}
