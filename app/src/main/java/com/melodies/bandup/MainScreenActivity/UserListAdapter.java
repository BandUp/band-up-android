package com.melodies.bandup.MainScreenActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.melodies.bandup.helper_classes.User;

import java.util.ArrayList;

/**
 * Created by Bergthor on 1.11.2016.
 */

public class UserListAdapter extends FragmentStatePagerAdapter {
    ArrayList<User> userList;

    public UserListAdapter(FragmentManager fm) {
        super(fm);
        userList = new ArrayList();
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    public User getUser(int position) {
        return userList.get(position);
    }


    public void addUser(User u) {
        userList.add(u);
    }

    @Override
    public Fragment getItem(int position) {
        return UserArrayListFragment.newInstance(position, getUser(position));
    }
}