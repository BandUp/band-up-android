package com.melodies.bandup.helper_classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class User implements Serializable {
    public User(){
        instruments = new ArrayList<>();
        genres = new ArrayList<>();
    }
    public String id;
    public String name;
    public List<String> instruments;
    public List<String> genres;
    public String status;
    public Integer distance;
    public Integer percentage;
    public String imgURL;
    public Date dateOfBirth;
    public Boolean userHasLiked;
    public String aboutme;
    public Integer soundCloudId;
    public String soundCloudURL;

    public Integer ageCalc() {
        if (dateOfBirth == null) {
            return null;
        }
        Calendar dayOfBirth = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dayOfBirth.setTime(dateOfBirth);
        Integer userAge = today.get(Calendar.YEAR) - dayOfBirth.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dayOfBirth.get(Calendar.DAY_OF_YEAR)){
            userAge--;
        }

        return userAge;
    }
}