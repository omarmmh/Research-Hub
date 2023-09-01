package com.webapp.researchhub.domain.calendar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.webapp.researchhub.domain.MyUser;
import jakarta.persistence.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Entity
public class Event {

    @Id
    @GeneratedValue
    private long id;

    private String title;

    @Column(length = 655535)
    private String description;

    private Date startDate;

    private Date endDate;

    private Date dateModified;

    private boolean isVirtual;

    private String location;

    @ManyToOne
    @JsonIgnore
    private MyUser organiser;

    @OneToMany
    private List<EventInterest> eventInterestList;


    public Event(String title, String description, Date startDate, Date endDate, boolean isVirtual, String location, MyUser organiser) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isVirtual = isVirtual;
        this.location = location;
        this.organiser = organiser;
    }

    public String getLocation() {
        return location;
    }


    public Date getDateModified() {
        return dateModified;
    }


    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Event() {

    }

    public boolean isVirtual() {return isVirtual;}

    public void setVirtual(boolean virtual) {isVirtual = virtual;}

    public long getId() {return id;}

    public void setId(long id) {this.id = id;}

    public String getTitle() {return title;}

    public void setTitle(String title) {this.title = title;}

    public String getDescription() {return description;}

    public void setDescription(String description) {this.description = description;}

    public Date getStartDate() {return startDate;}

    public void setStartDate(Date startDate) {this.startDate = startDate;}

    public Date getEndDate() {return endDate;}

    public void setEndDate(Date endDate) {this.endDate = endDate;}

    public MyUser getOrganiser() {return organiser;}

    public void setOrganiser(MyUser organiser) {
        this.organiser = organiser;
    }

    public String getSnippet() {
        int maxSnippetLength = 120;
        if(getDescription().length() <= maxSnippetLength) {
            return getDescription();
        }
        return getDescription().substring(0, maxSnippetLength) + "...";
    }

    public String getFormattedDateTimeEventString() {
        LocalDate date1 = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate date2 = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String date="";

        if(date1.isBefore(date2)) {
            date = _getFormattedStartDate() + " to " + _getFormattedEndDate();
        } else {
            date = _getFormattedStartDate() + " to " + _getEndTime();
        }
        return date;
    }

    private String _getFormattedStartDate() {
        String pattern = "E, MMM dd yyyy, HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(startDate);
        return date;
    }

    private String _getFormattedEndDate() {
        String pattern = "E, MMM dd yyyy, HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(endDate);
        return date;
    }

    private String _getEndTime() {
        String pattern = "HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String time = simpleDateFormat.format(endDate);
        return time;
    }

    public List<EventInterest> getEventInterestList() {
        return eventInterestList;
    }

    public void setEventInterestList(List<EventInterest> eventInterestList) {
        this.eventInterestList = eventInterestList;
    }
}
