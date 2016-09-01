package com.melodies.bandup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toChat:
                setContentView(R.layout.chat_window);
        }
    }

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.144.131:8080");
        } catch (URISyntaxException e) {
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSocket.connect();
        Button mClickButton1 = (Button)findViewById(R.id.toChat);
        mClickButton1.setOnClickListener(this);
        System.out.println("Connected?");
        System.out.println(mSocket.connected());

    }
}