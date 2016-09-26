package com.melodies.bandup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bergthor on 26.9.2016.
 */

public class UserListController {
    List<User> users;
    int currentIndex;
    public UserListController() {
        users = new ArrayList<>();
        currentIndex = 0;
    }

    public void addUser(User u) {
        users.add(u);
    }

    public void getUser(int index) {
        users.get(index);
    }

    public User getNextUser() {
        if (currentIndex == users.size()-1) {
            return null;
        }
        currentIndex++;
        return users.get(currentIndex);
    }

    public User getPrevUser() {
        if (currentIndex == 0) {
            return null;
        }
        currentIndex--;
        return users.get(currentIndex);
    }

    static public class User {
        public User(){

        }
        String id;
        String name;
        String[] instruments;
        String[] genres;
        String status;
        int distance;
        int percentage;
    }
}
