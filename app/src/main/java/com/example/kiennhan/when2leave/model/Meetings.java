package com.example.kiennhan.when2leave.model;


/**
 * Created by Kien Nhan on 11/27/2017.
 */

public class Meetings {
    private Account account;
    private String timeOfM0eeting;
    private String dateOfMeeting;
    private Address userLocation;
    private Address destination;
    private String description;

    public Meetings(Account account, String timeOfM0eeting, String dateOfMeeting, Address userLocation, Address destination, String description) {
        this.account = account;
        this.timeOfM0eeting = timeOfM0eeting;
        this.dateOfMeeting = dateOfMeeting;
        this.userLocation = userLocation;
        this.destination = destination;
        this.description = description;
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

    public Address getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(Address userLocation) {
        this.userLocation = userLocation;
    }

    public Address getDestination() {
        return destination;
    }

    public void setDestination(Address destination) {
        this.destination = destination;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
