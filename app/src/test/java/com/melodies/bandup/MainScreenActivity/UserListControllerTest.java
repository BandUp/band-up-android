package com.melodies.bandup.MainScreenActivity;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class UserListControllerTest {
    @Test
    public void addUser() throws Exception {
        String username = "User 1";
        com.melodies.bandup.MainScreenActivity.UserListController ulc = new com.melodies.bandup.MainScreenActivity.UserListController();
        com.melodies.bandup.MainScreenActivity.UserListController.User u = new com.melodies.bandup.MainScreenActivity.UserListController.User();
        u.name = username;
        ulc.addUser(u);
        assertEquals(ulc.users.size(), 1);
        assertEquals(ulc.users.get(0).name, username);
    }

    @Test
    public void getUser() throws Exception {
        String username = "User 1";
        com.melodies.bandup.MainScreenActivity.UserListController ulc = new com.melodies.bandup.MainScreenActivity.UserListController();
        com.melodies.bandup.MainScreenActivity.UserListController.User u = new com.melodies.bandup.MainScreenActivity.UserListController.User();
        u.name = username;
        ulc.addUser(u);
        assertEquals(ulc.getUser(0).name, username);
    }

    @Test
    public void getNextUser() throws Exception {
        com.melodies.bandup.MainScreenActivity.UserListController ulc = new com.melodies.bandup.MainScreenActivity.UserListController();
        com.melodies.bandup.MainScreenActivity.UserListController.User u1 = new com.melodies.bandup.MainScreenActivity.UserListController.User();
        String username1 = "User 1";

        com.melodies.bandup.MainScreenActivity.UserListController.User u2 = new com.melodies.bandup.MainScreenActivity.UserListController.User();
        String username2 = "User 2";

        com.melodies.bandup.MainScreenActivity.UserListController.User u3 = new com.melodies.bandup.MainScreenActivity.UserListController.User();
        String username3 = "User 3";

        u1.name = username1;
        u2.name = username2;
        u3.name = username3;

        ulc.addUser(u1);
        ulc.addUser(u2);
        ulc.addUser(u3);

        assertEquals(ulc.getNextUser().name, u2.name);
        assertEquals(ulc.getNextUser().name, u3.name);
        assertEquals(ulc.getNextUser(), null);
        assertEquals(ulc.getNextUser().name, null);

    }

    @Test
    public void getPrevUser() throws Exception {
        com.melodies.bandup.MainScreenActivity.UserListController ulc = new com.melodies.bandup.MainScreenActivity.UserListController();
        com.melodies.bandup.MainScreenActivity.UserListController.User u1 = new com.melodies.bandup.MainScreenActivity.UserListController.User();
        String username1 = "User 1";

        com.melodies.bandup.MainScreenActivity.UserListController.User u2 = new com.melodies.bandup.MainScreenActivity.UserListController.User();
        String username2 = "User 2";

        com.melodies.bandup.MainScreenActivity.UserListController.User u3 = new com.melodies.bandup.MainScreenActivity.UserListController.User();
        String username3 = "User 3";

        u1.name = username1;
        u2.name = username2;
        u3.name = username3;

        ulc.addUser(u1);
        ulc.addUser(u2);
        ulc.addUser(u3);

        assertEquals(ulc.getNextUser().name, u2.name);
        assertEquals(ulc.getNextUser().name, u3.name);
        assertEquals(ulc.getPrevUser().name, u2.name);
        assertEquals(ulc.getPrevUser().name, u1.name);
        assertEquals(ulc.getPrevUser(), null);
        assertEquals(ulc.getPrevUser(), null);
    }

}