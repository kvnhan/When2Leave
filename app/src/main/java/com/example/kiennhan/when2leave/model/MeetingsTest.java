package com.example.kiennhan.when2leave.model;

/**
 * Created by Kien on 12/11/2017.
 */

public class MeetingsTest {
    private String id;
    private String title;
    private Account account;
    private String timeOfM0eeting;
    private String dateOfMeeting;
    private String userLocation;
    private String destination;
    private String description;

    public MeetingsTest(){
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

    public String getTimeOfM0eeting() {
        return timeOfM0eeting;
    }

    public void setTimeOfM0eeting(String timeOfM0eeting) {
        this.timeOfM0eeting = timeOfM0eeting;
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
