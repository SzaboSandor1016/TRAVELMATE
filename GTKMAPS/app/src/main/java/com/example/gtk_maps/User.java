package com.example.gtk_maps;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class User extends AppCompatActivity {

    private String email;
    private String family;
    private String given;
    private String year;
    private String month;
    private String day;
    private String birthDate;
    private String username;


    public User(String email,String username,String family, String given, String year, String month, String day){
        this.email=email;
        this.username = username;
        this.family= family;
        this.given=given;
        this.year=year;
        this.month=month;
        this.day=day;
        this.birthDate=year + "/" + month +"/" + day;
    }

    public String getGiven() {
        return given;
    }

    public String getUsername() {
        return username;
    }

    public String getBirthDate() {
        return birthDate;
    }
    public String getYear(){return year;}
    public String getMonth() {
        return month;
    }
    public String getDay() {
        return day;
    }

    public String getFamily() {
        return family;
    }

    public String getEmail() {
        return email;
    }
    /*public static String[] getActivities() {
        return activities;
    }*/
}
