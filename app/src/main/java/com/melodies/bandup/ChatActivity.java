package com.melodies.bandup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class ChatActivity extends AppCompatActivity {

    public void onClickSend (View v) {
        final Button btnSend = (Button) findViewById(R.id.btnSend);
        final TextView txtChatView = (TextView) findViewById(R.id.txtChatView);
        final EditText txtMessage = (EditText) findViewById(R.id.txtMessage);
        txtChatView.setMovementMethod(new ScrollingMovementMethod());
        switch (v.getId()) {
            case R.id.btnSend:
                String message = txtMessage.getText().toString();
                txtChatView.append(message + "\n");
                txtMessage.setText("");
        }
    }

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://band-up-server.herokuapp.com");
        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mSocket.connect();
    }
}
