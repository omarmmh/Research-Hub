package com.webapp.researchhub.domain;

import jakarta.persistence.*;

@Entity
public class Account {
    //region Class Variables
    @Id
    @GeneratedValue
    private Long id;


    enum PrivacyAccess{
        //FRIENDS,
        ALL,
        LOGGEDIN,
        NONE
    }

    // Settings variables
    private PrivacyAccess displayForumActivity = PrivacyAccess.NONE;
    private PrivacyAccess displayPapers = PrivacyAccess.LOGGEDIN;
    private PrivacyAccess displayEmail = PrivacyAccess.NONE;
    private PrivacyAccess displayName = PrivacyAccess.LOGGEDIN;
    private PrivacyAccess displayCountry = PrivacyAccess.LOGGEDIN;
    private PrivacyAccess displayResearchFacility = PrivacyAccess.LOGGEDIN;


    // Email notification settings
    private boolean emailOnNewForumPost = false;
    private boolean emailOnForumReply = true;
    private boolean emailOnForumMessageUpvoted = true;

    //endregion


    public Account(){}

    public Account(String displayForumActivity, String displayPapers, String displayEmail, String displayName, String displayCountry, String displayResearchFacility) {
        setDisplayForumActivity(displayForumActivity);
        setDisplayPapers(displayPapers);
        setDisplayEmail(displayEmail);
        setDisplayName(displayName);
        setDisplayCountry(displayCountry);
        setDisplayResearchFacility(displayResearchFacility);
    }

    public Account(boolean emailOnNewForumPost, boolean emailOnForumReply, boolean emailOnForumMessageUpvoted) {
        this.emailOnNewForumPost = emailOnNewForumPost;
        this.emailOnForumReply = emailOnForumReply;
        this.emailOnForumMessageUpvoted = emailOnForumMessageUpvoted;
    }

    //region Getters and Setters

    public String getDisplayForumActivity() {
        return displayForumActivity.toString().toLowerCase();
    }

    public void setDisplayForumActivity(String displayForumActivity) {
        switch(displayForumActivity.toLowerCase()){
            case "all":
                this.displayForumActivity = PrivacyAccess.ALL;
                break;
            case "none":
                this.displayForumActivity = PrivacyAccess.NONE;
                break;
            case "loggedin":
                this.displayForumActivity = PrivacyAccess.LOGGEDIN;
        }
    }

    public String getDisplayPapers() {
        return displayPapers.toString().toLowerCase();
    }

    public void setDisplayPapers(String displayPapers) {
        switch(displayPapers.toLowerCase()){
            case "all":
                this.displayPapers = PrivacyAccess.ALL;
                break;
            case "none":
                this.displayPapers = PrivacyAccess.NONE;
                break;
            case "loggedin":
                this.displayPapers = PrivacyAccess.LOGGEDIN;
        }
    }

    public String getDisplayEmail() {
        return displayEmail.toString().toLowerCase();
    }

    public void setDisplayEmail(String displayEmail) {
        switch(displayEmail.toLowerCase()){
            case "all":
                this.displayEmail = PrivacyAccess.ALL;
                break;
            case "none":
                this.displayEmail = PrivacyAccess.NONE;
                break;
            case "loggedin":
                this.displayEmail = PrivacyAccess.LOGGEDIN;
        }
    }

    public String getDisplayName() {
        return displayName.toString().toLowerCase();
    }

    public void setDisplayName(String displayName) {
        switch(displayName.toLowerCase()){
            case "all":
                this.displayName = PrivacyAccess.ALL;
                break;
            case "none":
                this.displayName = PrivacyAccess.NONE;
                break;
            case "loggedin":
                this.displayName = PrivacyAccess.LOGGEDIN;
        }
    }

    public String getDisplayCountry(){
        return displayCountry.toString().toLowerCase();
    }

    public void setDisplayCountry(String displayCountry){
        switch(displayCountry.toLowerCase()){
            case "all":
                this.displayCountry = PrivacyAccess.ALL;
                break;
            case "none":
                this.displayCountry = PrivacyAccess.NONE;
                break;
            case "loggedin":
                this.displayCountry = PrivacyAccess.LOGGEDIN;
        }
    }

    public String getDisplayResearchFacility(){
        return displayResearchFacility.toString().toLowerCase();
    }

    public void setDisplayResearchFacility(String displayResearchFacility){
        switch(displayResearchFacility.toLowerCase()){
            case "all":
                this.displayResearchFacility = PrivacyAccess.ALL;
                break;
            case "none":
                this.displayResearchFacility = PrivacyAccess.NONE;
                break;
            case "loggedin":
                this.displayResearchFacility = PrivacyAccess.LOGGEDIN;
        }
    }

    // Email notification Settings
    public boolean isEmailOnNewForumPost() {
        return emailOnNewForumPost;
    }

    public void setEmailOnNewForumPost(boolean emailOnNewForumPost) {
        this.emailOnNewForumPost = emailOnNewForumPost;
    }

    public boolean isEmailOnForumReply() {
        return emailOnForumReply;
    }

    public void setEmailOnForumReply(boolean emailOnForumReply) {
        this.emailOnForumReply = emailOnForumReply;
    }

    public boolean isEmailOnForumMessageUpvoted() {
        return emailOnForumMessageUpvoted;
    }

    public void setEmailOnForumMessageUpvoted(boolean emailOnForumMessageUpvoted) {
        this.emailOnForumMessageUpvoted = emailOnForumMessageUpvoted;
    }


    //endregion
}