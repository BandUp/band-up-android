package com.melodies.bandup.helper_classes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class User implements Serializable {
    public User(JSONObject json){
        instruments = new ArrayList<>();
        genres = new ArrayList<>();

        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            id = json.getString("_id");
            name = json.getString("username");
            status = json.getString("status");
            distance = json.getInt("distance");
            percentage = json.getInt("percentage");
            imgURL = json.getJSONObject("image").getString("url");
            dateOfBirth = df.parse(json.getString("dateOfBirth"));
            aboutme = json.getString("aboutme");
            favoriteinstrument = json.getString("favoriteinstrument");
            soundCloudId = json.getInt("soundCloudId");
            soundCloudURL = json.getString("soundcloudurl");

            JSONArray genreArr = json.getJSONArray("genres");
            JSONArray instrArr = json.getJSONArray("instruments");
            for(int i = 0; i < genreArr.length(); i++){
                genres.add(genreArr.getString(i));
            }

            for(int i = 0; i < instrArr.length(); i++){
                instruments.add(instrArr.getString(i));
            }

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    public User(){
        instruments = new ArrayList<>();
        genres = new ArrayList<>();
    }
    public String id;
    public String name;
    public List<String> instruments;
    public List<String> genres;
    public String status;
    public String favoriteinstrument;
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
            return 0;
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