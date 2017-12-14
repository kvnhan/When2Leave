package com.example.kiennhan.when2leave.model;


/**
 * Holds Meeting information
 */

public class Meetings {
    private String id;
    private String title;
    private Account account;
    private String timeOfMeeting;
    private String dateOfMeeting;
    private String userLocation;
    private String destination;
    private String description;
    private Boolean isComplete;

    public Meetings(String id, String title, Account account, String timeOfM0eeting, String dateOfMeeting, String userLocation,
                    String destination, String description, Boolean isComplete) {
        this.id = id;
        this.title = title;
        this.account = account;
        this.timeOfMeeting = timeOfM0eeting;
        this.dateOfMeeting = dateOfMeeting;
        this.userLocation = userLocation;
        this.destination = destination;
        this.description = description;
        this.isComplete = isComplete;
    }

    public Boolean getComplete() {
        return isComplete;
    }

    public void setComplete(Boolean complete) {
        isComplete = complete;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle(){
        return title;
    }
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getTimeOfMeeting() {
        return timeOfMeeting;
    }

    public void setTimeOfMeeting(String timeOfM0eeting) {
        this.timeOfMeeting = timeOfM0eeting;
    }

    public String getDateOfMeeting() {
        return dateOfMeeting;
    }

    public void setDateOfMeeting(String dateOfMeeting) {
        this.dateOfMeeting = dateOfMeeting;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
