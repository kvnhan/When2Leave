package com.example.kiennhan.when2leave.model;

/**
 * Holds Loaction information
 */
public class Location {

    private double Long;
    private double Lati;

    public Location(double aLong, double lati) {
        Long = aLong;
        Lati = lati;
    }

    public double getLong() {
        return Long;
    }

    public void setLong(double aLong) {
        Long = aLong;
    }

    public double getLati() {
        return Lati;
    }

    public void setLati(double lati) {
        Lati = lati;
    }
}
