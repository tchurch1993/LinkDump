package com.linkdump.tchur.ld.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.linkdump.tchur.ld.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateEventActivity extends AppCompatActivity {
    private EditText nameEditText, courseNameEditText, eventTypeEditText, descriptionEditText;
    private TextView dueDate, startTime, endTime;
    private Switch overwriteSwitch, groupEventSwitch;
    private Spinner groupNameSpinner;
    private FirebaseAuth mAuth;
    private ArrayList<String> groupIDs;
    private ArrayList<String> userGroups;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private CollectionReference groupsRef;
    private CollectionReference userEventsRef;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mStartTimeSetListener;
    private TimePickerDialog.OnTimeSetListener mEndTimeSetListener;
    private int MIN_30 = 60 * 1000 * 30;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("users").document(mAuth.getUid());
        userEventsRef = db.collection("users").document(mAuth.getUid() + "").collection("events");
        groupsRef = db.collection("groups");
        groupIDs = new ArrayList<>();
        userGroups = new ArrayList<>();


        Calendar tempCal = Calendar.getInstance();
        final long timeInMillis = tempCal.getTimeInMillis();
        final Calendar startTimeCal = Calendar.getInstance();
        final Calendar endTimeCal = Calendar.getInstance();
        startTimeCal.setTimeInMillis(timeInMillis);
        Log.d("demo", startTimeCal.getTime().toString());
        startTimeCal.setTimeInMillis(calRoundUp(startTimeCal));
        endTimeCal.setTimeInMillis(calRoundUp(endTimeCal) + MIN_30);

        groupNameSpinner = findViewById(R.id.groupNameSpinner);
        overwriteSwitch = findViewById(R.id.multiSwitch);
        groupEventSwitch = findViewById(R.id.groupEventSwitch);
        nameEditText = findViewById(R.id.assignmentNameEditText);
        courseNameEditText = findViewById(R.id.courseNameEditText);
        eventTypeEditText = findViewById(R.id.eventTypeEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        dueDate = findViewById(R.id.dueDateTextView);
        startTime = findViewById(R.id.dueTimeTextView);
        endTime = findViewById(R.id.dueTimeTextView2);


        groupEventSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (groupIDs.isEmpty()) {
                    Log.d("demo", "groupIDs empty");
                    getGroupIds();
                } else {
                    Log.d("demo", "groupIDs populated");
                    groupNameSpinner.setVisibility(View.VISIBLE);
                }
            } else {
                groupNameSpinner.setVisibility(View.INVISIBLE);
            }
        });

        dueDate.setText(startTimeCal.get(Calendar.MONTH) + 1 + "/" + startTimeCal.get(Calendar.DAY_OF_MONTH) + "/" + startTimeCal.get(Calendar.YEAR));
        startTime.setText(milliesToTimeString(startTimeCal.getTimeInMillis()));
        endTime.setText(milliesToTimeString(endTimeCal.getTimeInMillis()));
        findViewById(R.id.submitCreateEventButton).setOnClickListener(v -> {
            if (!(startTimeCal.getTimeInMillis() > endTimeCal.getTimeInMillis())) {
                Map<String, Object> mEvent = new HashMap<>();
                mEvent.put("eventName", nameEditText.getText() + "");
                mEvent.put("courseName", courseNameEditText.getText() + "");
                mEvent.put("eventType", eventTypeEditText.getText() + "");
                mEvent.put("description", descriptionEditText.getText() + "");
                mEvent.put("startTime", startTimeCal.getTimeInMillis());
                mEvent.put("startYear", startTimeCal.get(Calendar.YEAR));
                mEvent.put("startMonth", startTimeCal.get(Calendar.MONTH));
                mEvent.put("startDayOfMonth", startTimeCal.get(Calendar.DAY_OF_MONTH));
                mEvent.put("endTime", endTimeCal.getTimeInMillis());
                mEvent.put("overwrittable", overwriteSwitch.isChecked());
                Log.d("demo", mAuth.getUid() + "");
                userEventsRef
                        .add(mEvent)
                        .addOnSuccessListener(documentReference -> Log.d("demo", "document ID is: " + documentReference.getId()))
                        .addOnFailureListener(e -> Log.w("demo", "Error adding document", e));
                finish();
            } else {
                Toast.makeText(this, "End Time must be after Start Time", Toast.LENGTH_SHORT).show();
            }

        });

        dueDate.setOnClickListener(v -> {

            int year = startTimeCal.get(Calendar.YEAR);
            int month = startTimeCal.get(Calendar.MONTH);
            int day = startTimeCal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(
                    CreateEventActivity.this,
                    android.R.style.Theme_DeviceDefault_Light_Dialog,
                    mDateSetListener,
                    year, month, day);
            dialog.show();
        });

        startTime.setOnClickListener(view -> {
            int hour = startTimeCal.get(Calendar.HOUR_OF_DAY);
            int minute = startTimeCal.get(Calendar.MINUTE);
            TimePickerDialog dialog = new TimePickerDialog(
                    CreateEventActivity.this,
                    mStartTimeSetListener,
                    hour,
                    minute, false);
            dialog.show();
        });

        endTime.setOnClickListener(view -> {
            int hour = startTimeCal.get(Calendar.HOUR_OF_DAY) + 1;
            int minute = startTimeCal.get(Calendar.MINUTE);
            TimePickerDialog dialog = new TimePickerDialog(
                    CreateEventActivity.this,
                    mEndTimeSetListener,
                    hour,
                    minute, false);
            dialog.show();
        });

        mStartTimeSetListener = (timePicker, hour, minute) -> {
            startTimeCal.set(Calendar.HOUR_OF_DAY, hour);
            startTimeCal.set(Calendar.MINUTE, minute);
            String meridiem = "AM";
            if (hour == 0) {
                hour = 12;
            } else {
                meridiem = "PM";
                hour = hour % 12;
                if (hour == 0) {
                    hour = 12;
                }
            }


            startTime.setText(hour + ":" + String.format("%02d", minute) + " " + meridiem);
            Log.d("demo", hour + ":" + minute + " " + meridiem);
        };

        mEndTimeSetListener = (timePicker, hour, minute) -> {
            Calendar testCal = Calendar.getInstance();
            testCal.set(Calendar.HOUR_OF_DAY, hour);
            testCal.set(Calendar.MINUTE, minute);
            if (testCal.getTimeInMillis() > startTimeCal.getTimeInMillis()) {
                endTimeCal.set(Calendar.HOUR_OF_DAY, hour);
                endTimeCal.set(Calendar.MINUTE, minute);
                String meridiem = "AM";
                if (hour == 0) {
                    hour = 12;
                } else {
                    meridiem = "PM";
                    hour = hour % 12;
                    if (hour == 0) {
                        hour = 12;
                    }
                }

                endTime.setText(hour + ":" + String.format("%02d", minute) + " " + meridiem);
                Log.d("demo", hour + ":" + minute + " " + meridiem);
            } else {
                Toast.makeText(CreateEventActivity.this, "Please Input Valid End Time", Toast.LENGTH_SHORT).show();
            }
        };

        mDateSetListener = (view, year, month, dayOfMonth) -> {
            startTimeCal.set(year, month, dayOfMonth);
            dueDate.setText(month + 1 + "/" + dayOfMonth + "/" + year);
            Log.d("demo", dueDate.getText().toString());
        };
    }

    public void getGroupIds() {
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    List<String> tempGroupIDs;
                    tempGroupIDs = (List<String>) task.getResult().get("groups");
                    getGroupNames(tempGroupIDs);
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(CreateEventActivity.this, "Failed to get Groups", Toast.LENGTH_SHORT).show();
            Log.w("demo", "Transaction Failure.", e);
        });
    }

    public long calRoundUp(Calendar cal) {
        long calMillis = cal.getTimeInMillis();
        long timeOverThirtyMin = calMillis % MIN_30;
        calMillis = (calMillis - timeOverThirtyMin) + MIN_30;
        return calMillis;
    }

    public String milliesToTimeString(Long millis) {
        int hour, minute;
        String meridiem;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);
        meridiem = "AM";
        if (hour == 0) {
            hour = 12;
        } else {
            meridiem = "PM";
            hour = hour % 12;
            if (hour == 0) {
                hour = 12;
            }
        }


        return (hour + ":" + String.format(Locale.US, "%02d", minute) + " " + meridiem);
    }

    public void getGroupNames(List<String> mGroupIDs) {
        groupsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    String mID = document.getId();
                    for (String id : mGroupIDs) {
                        if (mID.equals(id)) {
                            groupIDs.add(document.getId());
                            Log.d("demo", "Inside GroupNames: " + mID);
                            userGroups.add((String) document.get("groupName"));
                        }
                    }
                }
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>
                        (this, android.R.layout.simple_spinner_dropdown_item,
                                userGroups);
                groupNameSpinner.setAdapter(spinnerArrayAdapter);
                groupNameSpinner.setVisibility(View.VISIBLE);

            }
        }).addOnFailureListener(e -> {
            Toast.makeText(CreateEventActivity.this, "Failed to get Groups", Toast.LENGTH_SHORT).show();
            Log.w("demo", "Transaction Failure.", e);
        });
    }
}
