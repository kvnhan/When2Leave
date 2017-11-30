package com.example.kiennhan.when2leave.model;


import java.util.ArrayList;

/**
 * Created by Kien Nhan on 11/27/2017.
 */

public class Account {
    private String uid;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String password;
    private Address streetAddress;
    private ArrayList<Meetings> listOfMeetings = new ArrayList<Meetings>();

    public Account(String uid, String firstName, String lastName, String userName, String email, String password, Address streetAddress, ArrayList<Meetings> listOfMeetings) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.streetAddress = streetAddress;
        this.listOfMeetings = listOfMeetings;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Address getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(Address streetAddress) {
        this.streetAddress = streetAddress;
    }

    public ArrayList<Meetings> getListOfMeetings() {
        return listOfMeetings;
    }

    public void setListOfMeetings(ArrayList<Meetings> listOfMeetings) {
        this.listOfMeetings = listOfMeetings;
    }
}
