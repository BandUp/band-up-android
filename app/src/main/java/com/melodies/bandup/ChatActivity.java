package com.melodies.bandup;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class ChatActivity extends AppCompatActivity {

    String username = "elvar";
    String sendTo =  "bergthor";

    private Socket mSocket;

    Ack sendMessageAck = new Ack() {
        @Override
        public void call(Object... args) {
            // If the message transmission succeeded or not
            if (args[0].equals(false)) {
                System.out.println("Sending message failed");
            } else {
                System.out.println("Sending message succeeded");
            }
        }
    };

    Ack addUserAck = new Ack() {
        @Override
        public void call(Object... args) {

            // If the username is taken
            if (args[0].equals(false)) {
                finish();
            }
        }
    };

    /* Adds the message to the ScrollView and scrolls to the bottom. */
    private void displayMessage(Boolean isUser, String sender, String message) {

        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);

        LinearLayout ll = (LinearLayout) findViewById(R.id.chatCells);
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View myView = vi.inflate(R.layout.chat_message_cell, ll, false);
        if (isUser) {
            LinearLayout chatCell = (LinearLayout) myView.findViewById(R.id.chatMessageCell);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) chatCell.getLayoutParams();
            params.gravity = Gravity.END;
            chatCell.setLayoutParams(params);
        }

        TextView tv = (TextView) myView.findViewById(R.id.txtChatMessageText);
        tv.setText(message);

        ll.addView(myView);
        scrollToBottom(scrollView);
    }

    /* Scroll to the bottom. */
    private void scrollToBottom(final ScrollView scrollView) {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    /* When the user taps on the Send Message button. */
    public void onClickSend (View v) throws JSONException {
        final EditText txtMessage = (EditText) findViewById(R.id.txtMessage);

        switch (v.getId()) {
            case R.id.btnSend:

                String message = txtMessage.getText().toString();

                // Do not allow user to send empty string.
                if (message.equals("")) {
                    return;
                }

                txtMessage.setText("");

                // Create the JSON object to send the message.
                final JSONObject msgObject = new JSONObject();
                msgObject.put("nick", sendTo);
                msgObject.put("message", message);

                displayMessage(true, "You", message);

                mSocket.emit("privatemsg", msgObject, sendMessageAck);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mSocket = IO.socket(getResources().getString(R.string.api_address));
        } catch (URISyntaxException e) {
            Toast.makeText(ChatActivity.this, "URL parsing failed", Toast.LENGTH_SHORT).show();
        }

        setContentView(R.layout.activity_chat);
        mSocket.on("recv_privatemsg", onNewMessage);
        mSocket.connect();

        mSocket.emit("adduser", username, addUserAck);

        String url = getResources().getString(R.string.api_address).concat("/chat_history/").concat("57f30487cc0be459610523e2");

        // Get chat history.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
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
                    displayMessage(false, args[0].toString(), args[1].toString());
                }
            });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off();
        mSocket.disconnect();
        mSocket.off("recv_privatemsg", onNewMessage);
    }
}