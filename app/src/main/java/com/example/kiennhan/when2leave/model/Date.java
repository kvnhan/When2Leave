package com.example.kiennhan.when2leave.model;

/**
 * Hold Date information
 */
public class Date {
    private int month;
    private int date;
    private int year;
    private String day;

    public Date(int month, int date, int year, String day) {
        this.month = month;
        this.date = date;
        this.year = year;
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
