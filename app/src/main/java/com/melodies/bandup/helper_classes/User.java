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
import java.util.Locale;

public class User implements Serializable {

    public User(JSONObject responseObj){
        instruments = new ArrayList<>();
        genres = new ArrayList<>();
        liked = new ArrayList<>();

        try {
            if (!responseObj.isNull("_id")) {
                this.id = responseObj.getString("_id");
            }
            if (!responseObj.isNull("username")) {
                this.name = responseObj.getString("username");
            }
            if (!responseObj.isNull("dateOfBirth")) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                this.dateOfBirth = df.parse(responseObj.getString("dateOfBirth"));
            }

            if (!responseObj.isNull("favoriteinstrument")) {
                this.favoriteinstrument = responseObj.getString("favoriteinstrument");
            }

            if (!responseObj.isNull("percentage")) {
                this.percentage = responseObj.getInt("percentage");
            }

            if (!responseObj.isNull("genres")) {
                JSONArray genreArray = responseObj.getJSONArray("genres");
                for (int i = 0; i < genreArray.length(); i++) {
                    this.genres.add(genreArray.getString(i));
                }
            }

            if (!responseObj.isNull("instruments")) {
                JSONArray instrumentArray = responseObj.getJSONArray("instruments");
                for (int i = 0; i < instrumentArray.length(); i++) {
                    this.instruments.add(instrumentArray.getString(i));
                }
            }

            if (!responseObj.isNull("aboutme")) {
                this.aboutme = responseObj.getString("aboutme");
            }

            if (!responseObj.isNull("image")) {
                JSONObject imageObj = responseObj.getJSONObject("image");

                if (!imageObj.isNull("url")) {
                    this.imgURL = imageObj.getString("url");
                }
            }

            if (!responseObj.isNull("soundCloudId")){
                this.soundCloudId = responseObj.getInt("soundCloudId");
            }

            if (!responseObj.isNull("soundcloudurl")){
                this.soundCloudURL = responseObj.getString("soundcloudurl");
            }

            if (!responseObj.isNull("soundCloudSongName")){
                this.soundCloudSongName = responseObj.getString("soundCloudSongName");
            }

            UserLocation userLocation = new UserLocation();
            if (!responseObj.isNull("location")) {

                JSONObject location = responseObj.getJSONObject("location");
                if (!location.isNull("lat")) {
                    userLocation.setLatitude(location.getDouble("lat"));
                }

                if (!location.isNull("lon")) {
                    userLocation.setLongitude(location.getDouble("lon"));
                }

                if (!location.isNull("valid")) {
                    userLocation.setValid(location.getBoolean("valid"));
                }
            } else {
                userLocation.setValid(false);
            }
            this.location = userLocation;

            if (!responseObj.isNull("liked")) {
                JSONArray likedArray = responseObj.getJSONArray("liked");
                for (int i = 0; i < likedArray.length(); i++) {
                    this.liked.add(likedArray.getString(i));
                }
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    public User(){
        instruments = new ArrayList<>();
        genres = new ArrayList<>();
        liked = new ArrayList<>();
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
    public String aboutme;
    public Integer soundCloudId;
    public String soundCloudURL;
    public String soundCloudSongName;
    public UserLocation location;
    public List<String> liked;

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