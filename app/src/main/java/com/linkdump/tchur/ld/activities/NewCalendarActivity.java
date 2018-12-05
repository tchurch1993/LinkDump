package com.linkdump.tchur.ld.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.linkdump.tchur.ld.R;
import com.linkdump.tchur.ld.objects.Assignment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class NewCalendarActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ArrayList<EventDay> events;
    private ArrayList<Assignment> assignments;
    private CalendarView calendarView;
    private com.google.api.services.calendar.Calendar.CalendarList calendarLst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_calendar);

        events = new ArrayList<>();
        assignments = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        calendarView = findViewById(R.id.calendarView);

        mDatabase.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                events.clear();
                for (DataSnapshot node : dataSnapshot.getChildren()) {

                    Log.d("demo", node.toString());
                    Assignment assignment = node.getValue(Assignment.class);
                    assignments.add(assignment);
                    Log.d("demo", assignment.toString());
//                    assignment.key = node.getKey();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(assignment.getDueDate().getTimeInMillis());
                    EventDay event = new EventDay(calendar, R.drawable.g_logo);
                    //Log.d("demo", "assignment key value: " + assignment.key);
                    events.add(event);

                }
                Log.d("demo", events.toString());
                if (!events.isEmpty()) {
                    calendarView.setEvents(events);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(NewCalendarActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
            }


        });
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {

            }
        });
    }
}
