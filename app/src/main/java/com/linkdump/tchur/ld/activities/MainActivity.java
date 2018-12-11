package com.linkdump.tchur.ld.activities;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.linkdump.tchur.ld.R;
import com.linkdump.tchur.ld.adapters.GroupNameAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements GroupNameAdapter.ItemClickListener {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private DrawerLayout mDrawerLayout;
    private CollectionReference groupsRef;
    private List<String> userGroups;
    private List<String> groupIDs;
    private RecyclerView mRecyclerView;
    private GroupNameAdapter adapter;
    private String NO_GROUP_STRING = "No Groups Found";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.LinkDumpDark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24px);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("users").document(mAuth.getUid());
        userGroups = new ArrayList<>();
        groupIDs = new ArrayList<>();
        groupsRef = db.collection("groups");
        if (getIntent().getExtras() != null) {
            Log.d("demo", "clicked thingy with extras");
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("demo", "Key: " + key + " Value: " + value);

            }
            if (!getIntent().getStringExtra("groupId").isEmpty()) {
                Log.d("demo", "inside groupId Intent thing: " + getIntent().getStringExtra("groupId"));

                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("groupID", getIntent().getStringExtra("groupId"));
                startActivity(intent);
            } else {
                if (!getIntent().getStringExtra("link").isEmpty()) {
                    Log.d("demo", "inside link Intent thing: " + getIntent().getStringExtra("link"));
                    String url = getIntent().getStringExtra("link");
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            }
        }

        getGroupIDs();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }
        mRecyclerView = findViewById(R.id.groupNameRecyclerView);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new GroupNameAdapter(this, userGroups);
        adapter.setClickListener(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setAdapter(adapter);

//        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
//            Map<String, Object> data = new HashMap<>();
//            data.put("token", task.getResult().getToken());
//            db.collection("users").document(mAuth.getUid()).set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if (task.isSuccessful()) {
//                        Log.d("demo", "Successfully updated token in DB");
//                    }
//                }
//            });
//        });

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]


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
                        case R.id.sign_out:
                            SharedPreferences prefs = getSharedPreferences("info", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("email", Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
                            editor.apply();
                            mAuth.signOut();
                            Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent1);
                            finish();
                            return true;
                        case R.id.create_group:
                            startActivity(new Intent(MainActivity.this, CreateGroupActivity.class));
                            return true;
                    }

                    // Add code here to update the UI based on the item selected
                    // For example, swap UI fragments here

                    return true;
                });
    }

    public void subscriptionHandler() {
        for (String groupthing : groupIDs) {
            FirebaseMessaging.getInstance().subscribeToTopic(groupthing);

        }
    }


    public void getGroupIDs() {
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            if (!task2.getResult().getToken().equals(task.getResult().get("token"))) {
                                Map<String, Object> data = new HashMap<>();
                                data.put("token", task2.getResult().getToken());
                                db.collection("users").document(mAuth.getUid()).set(data, SetOptions.merge()).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Log.d("demo", "Successfully updated token in DB");
                                    }
                                });
                            } else {
                                Log.d("demo", "has valid token");
                            }
                        }
                    });
                    List<String> tempGroupIDs;
                    tempGroupIDs = (List<String>) task.getResult().get("groups");
                    if (!(tempGroupIDs == null)) {
                        getGroupNames(tempGroupIDs);
                    } else {
                        userGroups.add(NO_GROUP_STRING);
                        adapter.notifyDataSetChanged();

                    }
                }
            }
        });
    }

    public void getGroupNames(final List<String> mGroupIDs) {
        groupsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    if (task.isSuccessful()) {
                        String mID = document.getId();
                        for (String id : mGroupIDs) {
                            if (mID.equals(id)) {
                                groupIDs.add(document.getId());
                                Log.d("demo", "Inside GroupNames: " + mID);
                                userGroups.add((String) document.get("groupName"));

                            }
                        }
                    }
                }
                subscriptionHandler();
                adapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        for (String mID : groupIDs) {
            Log.d("demo", "Group IDs: " + mID);
        }
        if (groupIDs.size() > 0) {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            intent.putExtra("groupID", groupIDs.get(position));
            startActivity(intent);
        } else {
            Toast.makeText(this, "Please add a Group", Toast.LENGTH_SHORT).show();
        }
    }

    // This snippet takes the simple approach of using the first returned Google account,
    // but you can pick any Google account on the device.
    public String getAccount() {
        // This call requires the Android GET_ACCOUNTS permission
        Account[] accounts = AccountManager.get(this /* activity */).
                getAccountsByType("com.google");
        if (accounts.length == 0) {
            return null;
        }
        return accounts[0].name;
    }

    public void getAuthToken() {
        // [START fcm_get_token]
        String accountName = getAccount();

        // Initialize the scope using the client ID you got from the Console.
        final String scope = "596243123444-pql15uql1o7fvt12sfjkfdv0m1ppkkjb.apps.googleusercontent.com";

        String idToken = null;
        try {
            idToken = GoogleAuthUtil.getToken(this, accountName, scope);
        } catch (Exception e) {
            Log.w("demo", "Exception while getting idToken: " + e);
        }
        // [END fcm_get_token]
    }
}
