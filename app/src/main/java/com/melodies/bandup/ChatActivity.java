package com.melodies.bandup;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.melodies.bandup.MainScreenActivity.ProfileFragment.DEFAULT;

public class ChatActivity extends AppCompatActivity {

    private String sendTo;
    private String sendToUsername;
    private Socket mSocket;
    private AdView mAdView;
    private ChatRecyclerAdapter mAdapter;
    private ScrollView mScrollView;
    private RecyclerView mRecycler;
    private EditText txtMessage;


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

    /* Adds the message to the ScrollView. */
    private void displayMessage(String sender, String message) {

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.text = message;
        chatMessage.senderUserId = sender;

        mAdapter.addMessage(chatMessage);
    }

    private void scrollToBottom(final ScrollView scrollView) {
        scrollView.post(new Runnable() {
            @Override
            public void run() {

                scrollView.fullScroll(View.FOCUS_DOWN);
                txtMessage.requestFocus();
            }
        });
    }

    private void scrollAfterHalfSecond(final ScrollView scrollView) {
        final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);

        exec.schedule(new Runnable(){
            @Override
            public void run(){
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        }, 600, TimeUnit.MILLISECONDS);
    }

    /* When the user taps on the Send Message button. */
    public void onClickSend (View v) throws JSONException {

        switch (v.getId()) {
            case R.id.btnSend:

                String message = txtMessage.getText().toString();

                // Do not allow user to send empty string.
                if (message.equals("")) {
                    return;
                }

                if (!mSocket.connected()) {
                    mSocket.connect();
                }

                txtMessage.setText("");

                // Create the JSON object to send the message.
                final JSONObject msgObject = new JSONObject();
                msgObject.put("nick", sendTo);
                msgObject.put("message", message);

                displayMessage(getUserId(), message);
                scrollToBottom(mScrollView);

                mSocket.emit("privatemsg", msgObject, sendMessageAck);
                break;
        }
    }

    public String getUserId() {
        SharedPreferences srdPref = getSharedPreferences("UserIdRegister", Context.MODE_PRIVATE);
        String userId = srdPref.getString("userID", DEFAULT);
        return (!userId.equals(DEFAULT)) ? userId : "No data Found";
    }

    // Adding ad Banner
    private void getAd() {
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        txtMessage = (EditText) findViewById(R.id.txtMessage);
        txtMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    scrollAfterHalfSecond(mScrollView);
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        mAdapter = new ChatRecyclerAdapter(ChatActivity.this, null, getUserId());
        mScrollView = (ScrollView) findViewById(R.id.scrollView);

        if (extras != null) {
            sendTo = extras.getString("SEND_TO_USER_ID");
            sendToUsername = extras.getString("SEND_TO_USERNAME");
        }
        else {
            finish();
        }

        try {
            setTitle(getString(R.string.chat_title));
            getSupportActionBar().setSubtitle(sendToUsername);
        } catch (Exception npe) {
            setTitle(getString(R.string.chat_chat_with).concat(" ").concat(sendToUsername));
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

        mRecycler = (RecyclerView) findViewById(R.id.recyclerList);
        mRecycler.setAdapter(mAdapter);

        getAd();
        mSocket.on("recv_privatemsg", onNewMessage);
        mSocket.connect();

        mSocket.emit("adduser", getUserId(), addUserAck);

        String url = getResources().getString(R.string.api_address).concat("/chat_history/").concat(sendTo);

        // Get chat history.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response.toString());
                        if (!response.isNull("chatHistory")) {
                            try {
                                JSONArray chatHistory = response.getJSONArray("chatHistory");
                                for (int i = 0; i < chatHistory.length(); i++) {
                                    JSONObject item = chatHistory.getJSONObject(i);
                                    if (!item.isNull("message")) {
                                        displayMessage(item.getString("sender"), item.getString("message"));
                                    }
                                }
                                scrollToBottom(mScrollView);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error == null) {
                            // TODO: Display Error Message
                            return;
                        }

                        if (error.networkResponse == null) {
                            // TODO: Display Error Message
                            return;
                        }

                        // If there is no chat found. No worries.
                        if (error.networkResponse.statusCode != 404) {
                            VolleySingleton.getInstance(ChatActivity.this).checkCauseOfError(error);
                        }
                    }
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    // Return to previous Activity
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    // Listener to listen to the "recv_privatemsg" emission.
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                // args[0] = from username
                // args[1] = message
                if (sendToUsername.equals(args[0])) {
                    displayMessage(args[0].toString(), args[1].toString());
                    scrollToBottom(mScrollView);
                }
                }
            });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("ONDESTROY");
        mSocket.off();
        mSocket.disconnect();
        mSocket.off("recv_privatemsg", onNewMessage);
    }
}