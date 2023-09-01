package com.webapp.researchhub.domain;

import com.webapp.researchhub.domain.calendar.EventInterest;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
public class MyUser {
    //region Class Variables
    @Id
    @GeneratedValue
    private Long Id;
    @OneToOne
    private Account account;
    @Column(unique = true)
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email address is invalid", regexp = ".+[@].+[\\.].+")
    private String email;
    @NotEmpty(message = "Username cannot be empty")
    private String username;
    @NotEmpty(message = "First name cannot be empty")
    private String firstName;
    @NotEmpty(message = "Surname cannot be empty")
    private String surname;
    @NotNull
    private String password;
    @Column(length = 500)
    private String bio = "";
    private String country = "";
    private String researchFacility = "";


    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles = new ArrayList<>();

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<UserFile> userFiles = new ArrayList<>();

    @OneToOne
    private ProfilePic profilePic= null;

    @OneToMany
    private List<EventInterest> eventInterestList;

    //endregion

    public MyUser() {}

    public MyUser(String username, String firstName, String surname, String email, String password) {
        this.email = email;
        this.username = username;
        this.firstName = firstName;
        this.surname = surname;
        this.password = password;
    }

    public MyUser(String username, String firstName, String surname, String email,String country, String researchFacility, String bio){
        this.username=username;
        this.firstName=firstName;
        this.surname=surname;
        this.email=email;
        this.country=country;
        this.researchFacility=researchFacility;
        this.bio=bio;
    }

    //region Getters and Setters
    public Long getId() { return Id; }

    public Account getAccount() {
        return account;
    }
    public void setAccount(Account account) {
        this.account = account;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String name) {
        this.email = name;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    public String getResearchFacility() {
        return researchFacility;
    }
    public void setResearchFacility(String researchFacility) {
        this.researchFacility = researchFacility;
    }

    public List<UserFile> getUserFiles() {return userFiles;}
    public void setUserFiles(List<UserFile> userFiles) {this.userFiles = userFiles;}

    public ProfilePic getProfilePic() {return profilePic;}
    public void setProfilePic(ProfilePic profilePic) {this.profilePic = profilePic;}

    public List<Role> getRoles() {
        return roles;
    }

    public String getFullName() { return this.firstName + " " + this.surname; }

    public List<EventInterest> getEventInterestList() {
        return eventInterestList;
    }

    public void setEventInterestList(List<EventInterest> eventInterestList) {
        this.eventInterestList = eventInterestList;
    }
}
