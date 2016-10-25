package com.melodies.bandup.helper_classes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bergthor on 15.10.2016.
 */

public class User {
    public User(){
        instruments = new ArrayList<>();
        genres = new ArrayList<>();
    }
    public String id;
    public String name;
    public List<String> instruments;
    public List<String> genres;
    public String status;
    public int distance;
    public int percentage;
    public String imgURL;
    public int age;
    public Boolean userHasLiked;
}