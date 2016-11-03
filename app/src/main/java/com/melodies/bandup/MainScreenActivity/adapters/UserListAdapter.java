package com.melodies.bandup.MainScreenActivity.adapters;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.melodies.bandup.MainScreenActivity.UserItemFragment;
import com.melodies.bandup.helper_classes.User;

import java.util.ArrayList;

/**
 * Created by Bergthor on 1.11.2016.
 */

public class UserListAdapter extends FragmentStatePagerAdapter {
    /**
     * The list that is used to display the users.
     */
    ArrayList<User> userList;


    /**
     * Constructor for the UserListAdapter
     * @param fm
     */
    public UserListAdapter(FragmentManager fm) {
        super(fm);
        userList = new ArrayList();
    }

    /**
     * Get the number of users in the list.
     * @return number of users.
     */
    @Override
    public int getCount() {
        return userList.size();
    }

    /**
     * Get a user object at a specific index.
     * @param position The index of the user in the list
     * @return the User object at the index 'position'
     * @see User
     */
    public User getUser(int position) {
        if (position < userList.size()) {
            return userList.get(position);
        } else {
            return null;
        }
    }


    /**
     * Adds the user 'u' to the list.
     * @param u The user that should be added.
     * @return Returns true if adding was successful.
     *         False otherwise.
     */
    public Boolean addUser(User u) {
        if (u != null) {
            userList.add(u);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Creates a new fragment and returns it
     * @param position
     * @return a new UserItemFragment
     * @see UserItemFragment
     */
    @Override
    public Fragment getItem(int position) {
        return UserItemFragment.newInstance(position, getUser(position));
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
        // Do not restore the state. We restore it by calling the API.
        // super.restoreState(arg0, arg1);
    }
}