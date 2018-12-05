package com.linkdump.tchur.ld.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.linkdump.tchur.ld.R;
import com.linkdump.tchur.ld.objects.Event;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ShowEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);
        String startTimeDate = "";

        Intent intent = getIntent();
        Event event = intent.getParcelableExtra("event");
        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        startTime.setTimeInMillis(event.getStartTime());
        endTime.setTimeInMillis(event.getEndTime());
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy   hh:mm aaa", Locale.US);

        startTimeDate = sdf.format(startTime.getTime());
        TextView eventNameTextView = findViewById(R.id.show_event_name);
        TextView eventTimeTextView = findViewById(R.id.show_event_time);
        TextView eventDescriptionTextView = findViewById(R.id.show_event_description);
        eventNameTextView.setText(event.getEventName());
        eventTimeTextView.setText(startTimeDate);
        eventDescriptionTextView.setText(event.getDescription());

        Button editButton = findViewById(R.id.editEventButton);
        editButton.setOnClickListener(v -> finish());


    }
}
