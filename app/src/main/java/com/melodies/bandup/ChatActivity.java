package com.melodies.bandup;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.melodies.bandup.MainScreenActivity.UserDetailsFragment;

import org.json.JSONObject;

import java.net.URISyntaxException;

import static com.melodies.bandup.MainScreenActivity.ProfileFragment.DEFAULT;

public class ChatActivity extends AppCompatActivity implements ChatFragment.OnFragmentInteractionListener, UserDetailsFragment.OnFragmentInteractionListener {

    private String receiverId;
    private String receiverUsername;
    private Socket mSocket;

    private ChatFragment chatFragment;
    UserDetailsFragment userDetailsFragment;

    private MyAdapter mSectionsPagerAdapter;




    Ack sendMessageAck = new Ack() {
        @Override
        public void call(Object... args) {
            // If the message transmission succeeded or not
            if (args[0].equals(false)) {
                System.out.println("Sending message. Message saved to server");
            } else {
                System.out.println("Sending message. Receiver online.");
            }
        }
    };

    Ack addUserAck = new Ack() {
        @Override
        public void call(Object... args) {

            // If the username is taken
            if (args[0].equals(false)) {
            }
        }
    };

    public String getUserId() {
        SharedPreferences srdPref = getSharedPreferences("UserIdRegister", Context.MODE_PRIVATE);
        String userId = srdPref.getString("userID", DEFAULT);
        return (!userId.equals(DEFAULT)) ? userId : "No data Found";
    }

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            receiverId = extras.getString("SEND_TO_USER_ID");
            receiverUsername = extras.getString("SEND_TO_USERNAME");
        }
        else {
            finish();
        }
        mSectionsPagerAdapter = new MyAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        chatFragment = (ChatFragment) mSectionsPagerAdapter.getItem(0);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                View view = ChatActivity.this.getCurrentFocus();
                if (position == 1) {
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }

                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        try {
            setTitle(getString(R.string.chat_title));
            getSupportActionBar().setSubtitle(receiverUsername);
        } catch (Exception npe) {
            setTitle(getString(R.string.chat_chat_with).concat(" ").concat(receiverUsername));
        }

        try {
            mSocket = IO.socket(getResources().getString(R.string.api_address));
        } catch (URISyntaxException e) {
            Toast.makeText(ChatActivity.this, "URL parsing failed", Toast.LENGTH_SHORT).show();
        }

        // back to activity before
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mSocket.on("recv_privatemsg", onNewMessage);
        mSocket.connect();

        mSocket.emit("adduser", getUserId(), addUserAck);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_chat, menu);
        MenuItem item = menu.findItem(R.id.action_user);

        if (mViewPager.getCurrentItem() == 0) {
            item.getIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        } else {
            item.getIcon().setColorFilter(getResources().getColor(R.color.bandUpYellow), PorterDuff.Mode.SRC_ATOP);
        }

        return super.onCreateOptionsMenu(menu);

    }

    public void sendMessage(JSONObject msgObject) {
        if (!mSocket.connected()) {
            mSocket.connect();
        }
        mSocket.emit("privatemsg", msgObject, sendMessageAck);

    }

    // Return to previous Activity
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_user:
                if (mViewPager.getCurrentItem() == 0) {
                    mViewPager.setCurrentItem(1, true);
                    View view = this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    invalidateOptionsMenu();
                } else {
                    mViewPager.setCurrentItem(0, true);
                    invalidateOptionsMenu();
                }

                return true;

            case android.R.id.home:
                finish();
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }



    public Boolean hasLocationPermission() {
        return !(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("ONDESTROY");
        mSocket.off();
        mSocket.disconnect();
        mSocket.off("recv_privatemsg", onNewMessage);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class MyAdapter extends FragmentStatePagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }
        ChatFragment chatFragment;
        UserDetailsFragment userDetailsFragment;

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (chatFragment == null) {
                    chatFragment = ChatFragment.newInstance(receiverId, receiverUsername);
                }
                return chatFragment;
            } else if (position == 1) {
                if (userDetailsFragment == null) {
                    userDetailsFragment = UserDetailsFragment.newInstance();
                    Bundle bundle = new Bundle();

                    bundle.putString("user_id", receiverId);

                    if (userDetailsFragment.getArguments() != null) {
                        userDetailsFragment.getArguments().clear();
                        userDetailsFragment.getArguments().putAll(bundle);
                    } else {
                        userDetailsFragment.setArguments(bundle);
                    }
                }

                return userDetailsFragment;
            } else {
                return null;
            }
        }
    }

    // Listener to listen to the "recv_privatemsg" emission.
    public Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // args[0] = from username
                    // args[1] = message
                    if (receiverId.equals(args[0])) {
                        chatFragment.displayMessage(args[0].toString(), args[1].toString());
                        chatFragment.mRecycler.scrollToPosition(0);
                    }
                }
            });
        }
    };
}