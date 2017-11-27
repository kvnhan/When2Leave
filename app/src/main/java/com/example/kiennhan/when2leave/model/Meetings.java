package com.example.kiennhan.when2leave.model;


/**
 * Created by Kien Nhan on 11/27/2017.
 */

public class Meetings {
    private Time timeOfM0eeting;
    private Date dateOfMeeting;
    private Address userLocation;
    private Address destination;
    private String description;

    public Meetings(Time timeOfM0eeting, Date dateOfMeeting, Address userLocation, Address destination, String description) {
        this.timeOfM0eeting = timeOfM0eeting;
        this.dateOfMeeting = dateOfMeeting;
        this.userLocation = userLocation;
        this.destination = destination;
        this.description = description;
    }

    public Time getTimeOfM0eeting() {
        return timeOfM0eeting;
    }

    public void setTimeOfM0eeting(Time timeOfM0eeting) {
        this.timeOfM0eeting = timeOfM0eeting;
    }

    public Date getDateOfMeeting() {
        return dateOfMeeting;
    }

    public void setDateOfMeeting(Date dateOfMeeting) {
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
