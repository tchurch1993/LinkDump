package com.linkdump.tchur.ld.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.linkdump.tchur.ld.R;
import com.linkdump.tchur.ld.adapters.MyRecyclerViewAdapter;

import com.linkdump.tchur.ld.objects.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {
    private FirebaseAuth mAuth;
    private ArrayList<EventDay> eventDays;
    private ArrayList<Event> events;
    private ArrayList<Event> dayEvents;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private com.applandeo.materialcalendarview.CalendarView materialCalendarView;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("users").document(mAuth.getUid());
        eventDays = new ArrayList<>();
        events = new ArrayList<>();
        dayEvents = new ArrayList<>();
        materialCalendarView = findViewById(R.id.applandeoCalendarView);

        mRecyclerView = findViewById(R.id.eventRecyclerView);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new MyRecyclerViewAdapter(this, dayEvents);
        adapter.setClickListener(this);
        mRecyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        Toast.makeText(this, "Loading Events", Toast.LENGTH_SHORT).show();
//        grabMonthEventDays(Calendar.getInstance());

        //final CalendarView calendar = findViewById(R.id.calendarView2);
        mDrawerLayout = findViewById(R.id.menu_drawer);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    // set item as selected to persist highlight
                    menuItem.setChecked(true);
                    // close drawer when item is tapped
                    mDrawerLayout.closeDrawers();
                    switch (menuItem.getItemId()) {
                        case R.id.create_event:
                            startActivity(new Intent(MainActivity.this, CreateEventActivity.class));
                            return true;
                        case R.id.sign_out:
                            SharedPreferences prefs = getSharedPreferences("info", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("email", Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
                            editor.apply();
                            mAuth.signOut();
                            Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent1);
                            finish();
                        case R.id.group_chat:
                            Intent groupChat = new Intent(MainActivity.this, UserGroupsActivity.class);
                            startActivity(groupChat);
                            return true;
                        case R.id.create_group:
                            startActivity(new Intent(MainActivity.this, CreateGroupActivity.class));
                            return true;
                        case R.id.week_view:
                            startActivity(new Intent(MainActivity.this, WeekViewActivity.class));
                            return true;
                    }

                    // Add code here to update the UI based on the item selected
                    // For example, swap UI fragments here

                    return true;
                });

//        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
//                cal.set(year, month, dayOfMonth);
//                if (dayOfMonth == 13){
//                    mRecyclerView.setAdapter(adapter);
//                }else {
//                    tempEvents.clear();
//                    MyRecyclerViewAdapter emptyAdapter = new MyRecyclerViewAdapter(MainActivity.this, tempEvents);
//                    mRecyclerView.setAdapter(emptyAdapter);
//                }
//                Log.d("demo", cal.getTime().toString());
//            }
//        });

        materialCalendarView.setOnDayClickListener(eventDay -> {
            dayEvents.clear();
            Calendar clickedDayCalendar = eventDay.getCalendar();
            for (Event monthEvent : events){
                Calendar eventCal = Calendar.getInstance();
                eventCal.setTimeInMillis(monthEvent.getStartTime());
                if (eventCal.get(Calendar.DAY_OF_MONTH) == clickedDayCalendar.get(Calendar.DAY_OF_MONTH)){
                    Log.d("demo", "added: " + monthEvent.getEventName());
                    dayEvents.add(monthEvent);
                }
            }
            for (Event listEvent : dayEvents){
                Log.d("demo", "dayEvent Names: " + listEvent.getEventName());
            }
            adapter.notifyDataSetChanged();
            Log.d("demo", "onDayClick: ");
        });
        materialCalendarView.setOnForwardPageChangeListener(() -> {
            Calendar calendar1 = materialCalendarView.getCurrentPageDate();
            grabMonthEventDays(calendar1);
        });
        materialCalendarView.setOnPreviousPageChangeListener(() -> {
            Calendar calendar1 = materialCalendarView.getCurrentPageDate();
            grabMonthEventDays(calendar1);
        });

        userRef.collection("events").addSnapshotListener((queryDocumentSnapshots, e) -> grabMonthEventDays(materialCalendarView.getCurrentPageDate()));
    }

    public void grabMonthEventDays(Calendar cal){
        eventDays.clear();
        events.clear();
        userRef.collection("events").whereEqualTo("startMonth", cal.get(Calendar.MONTH) ).orderBy("startTime").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("demo", "inside db call");
                        for (DocumentSnapshot doc : task.getResult()){
                            events.add(doc.toObject(Event.class));
                            Calendar eventStartTime = Calendar.getInstance();
                            eventStartTime.setTimeInMillis(doc.getLong("startTime"));
                            EventDay eventDay = new EventDay(eventStartTime, R.drawable.g_logo);
                            eventDays.add(eventDay);
                    }
                    for (EventDay day : eventDays){
                            Log.d("demo", "all eventdays: " + day.getCalendar().getTimeInMillis());
                    }
                    Log.d("demo", "eventday size" + eventDays.size());
                    materialCalendarView.setEvents(eventDays);
                        Toast.makeText(this, "Events Loaded!", Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_event:
                startActivity(new Intent(MainActivity.this, CreateEventActivity.class));
                return true;
            case R.id.sign_out:
                SharedPreferences prefs = getSharedPreferences("info", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("email", Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
                editor.apply();

                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(MainActivity.this, ShowEventActivity.class);
        Event clickedEvent = adapter.getItem(position);
        intent.putExtra("event", clickedEvent);
        startActivity(intent);
    }
}
