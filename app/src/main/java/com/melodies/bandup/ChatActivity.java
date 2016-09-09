package com.melodies.bandup;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class ChatActivity extends AppCompatActivity {
    public Activity activity = this;
    private void displayMessage(String message) {
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myView = vi.inflate(R.layout.chat_message_cell,null, false);
        TextView tv = (TextView) myView.findViewById(R.id.txtChatMessageText);
        tv.setText(message);
        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.chatCells);
        insertPoint.addView(myView);

    }
    private void scrollToBottom(final ScrollView scrollView) {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    public void onClickSend (View v) throws JSONException {
        final EditText txtMessage = (EditText) findViewById(R.id.txtMessage);
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        switch (v.getId()) {
            case R.id.btnSend:
                String message = txtMessage.getText().toString();
                if (message.equals("")) {
                    return;
                }
                txtMessage.setText("");
                displayMessage(message);
                scrollToBottom(scrollView);
                JSONObject msgObject = new JSONObject();
                String sendTo = "Elvar";
                msgObject.put("nick", sendTo);
                msgObject.put("message", message);

                mSocket.emit("privatemsg", msgObject);
                break;
        }
    }

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("https://band-up-server.herokuapp.com");
        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String username = "helloworld";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mSocket.on("recv_privatemsg", onNewMessage);
        mSocket.connect();

        mSocket.emit("adduser", username, new Ack() {
            @Override
            public void call(Object... args) {
                if (args[0].equals(false)) {
                    System.out.println("Username already taken");
                } else {
                    System.out.println("Username available");
                }
            }
        });
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message;
                    System.out.println("args:");
                    System.out.println(args[0]);
                    System.out.println("DATA");
                    System.out.println(data);
                    try {
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }
                    displayMessage(message);
                }
            });
        }
    };

}
