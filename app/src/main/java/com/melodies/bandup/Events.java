package com.melodies.bandup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Events extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        ListView eventList = (ListView) findViewById(R.id.eventList);

        ArrayList<String> events = new ArrayList<String>();

        events.add("Elvar");
        events.add("Dagur");
        events.add("Bergþór");
        events.add("Rafá");

        /* TODO: Add events to ArrayList */

        eventList.setAdapter(new ArrayAdapter<String>(this, // context
                                                      android.R.layout.simple_list_item_1, // layout
                                                      events)); // data
    }
}
