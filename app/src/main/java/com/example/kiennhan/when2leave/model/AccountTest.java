package com.example.kiennhan.when2leave.model;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Created by Kien Nhan on 12/9/2017.
 */

public class AccountTest {
    private static int workload = 12;
    public String uid;
    public String firstName;
    public String lastName;
    public String userName;
    public String email;
    public String password;
    public AddressTest streetAddress;

    public AccountTest(){}

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

    public AddressTest getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(AddressTest streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String hashPassword(String password_plaintext) {
        String salt = BCrypt.gensalt(workload);
        String hashed_password = BCrypt.hashpw(password_plaintext, salt);

        return(hashed_password);
    }

    public boolean checkPassword(String password_plaintext, String stored_hash) {
        boolean password_verified = false;

        if(null == stored_hash || !stored_hash.startsWith("$2a$"))
            throw new java.lang.IllegalArgumentException("Invalid hash provided");

        password_verified = BCrypt.checkpw(password_plaintext, stored_hash);

        return(password_verified);
    }
}
