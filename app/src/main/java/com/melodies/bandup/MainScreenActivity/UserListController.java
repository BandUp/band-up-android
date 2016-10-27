package com.melodies.bandup.MainScreenActivity;

import com.melodies.bandup.helper_classes.User;

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

    public User getUser(int index) {
        return users.get(index);
    }

    public User getCurrentUser() {
        if (users.size() == 0){
            return null;
        }
        return users.get(currentIndex);
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
}
