package com.linkdump.tchur.ld.activities;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.linkdump.tchur.ld.R;
import com.linkdump.tchur.ld.adapters.GroupNameAdapter;
import com.linkdump.tchur.ld.objects.GroupItem;
import com.linkdump.tchur.ld.utils.MessageHistoryUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements GroupNameAdapter.ItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    public static final String SERVICE_TO_ACTIVITY_TRANSFER = "service.to.activity.transfer";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private DrawerLayout mDrawerLayout;
    private CollectionReference groupsRef;
    BroadcastReceiver updateUIReceiver;
    private List<GroupItem> groupItems;
    private List<String> userGroups;
    private List<String> groupIDs;
    private RecyclerView mRecyclerView;
    private FloatingActionButton createGroupFab;
    private SwipeRefreshLayout mRefreshLayout;
    private GroupNameAdapter adapter;
    private final String NO_GROUP_STRING = "No Groups Found";
    private IntentFilter intentFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.LinkDumpDark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clearNotifications();


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            // Create channel to show notifications.
//            String channelId = "Group Chat";
//            String channelName = "Group Chat";
//            NotificationManager notificationManager =
//                    getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
//                    channelName, NotificationManager.IMPORTANCE_HIGH));
//        }

        registerUIReceiver();

        //setup toolbar/actionbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        createGroupFab = findViewById(R.id.addGroupFabButton);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24px);
        //initialize firebase objects
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("users").document(mAuth.getUid());
        //initialize data lists
        userGroups = new ArrayList<>();
        groupItems = new ArrayList<>();
        groupIDs = new ArrayList<>();
        groupsRef = db.collection("groups");
        //handles on notification click
        if (getIntent().getExtras() != null) {
            Log.d("demo", "clicked thingy with extras");
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("demo", "Key: " + key + " Value: " + value);

            }
            if (getIntent().getStringExtra("groupID") != null) {
                Log.d("demo", "inside groupId Intent thing: " + getIntent().getStringExtra("groupID"));
                String groupId = getIntent().getStringExtra("groupID");
                try {
                    MessageHistoryUtil.clearGroupHistory(this, groupId);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("groupID", groupId);
                startActivity(intent);
            } else {
                Log.d("demo", "in else of getIntent checks");
                if (getIntent().getStringExtra("link") != null) {
                    Log.d("demo", "inside link Intent thing: " + getIntent().getStringExtra("link"));
                    String url = getIntent().getStringExtra("link");
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            }
        }

        //getGroupIDs();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = "chat";
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }
        mRecyclerView = findViewById(R.id.groupNameRecyclerView);
        mRefreshLayout = findViewById(R.id.swipeRefreshGroupLayout);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new GroupNameAdapter(this, groupItems);
        adapter.setClickListener(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setAdapter(adapter);

        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, android.R.color.holo_green_dark),
                ContextCompat.getColor(this, android.R.color.holo_orange_dark),
                ContextCompat.getColor(this, android.R.color.holo_blue_dark));


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
                        case R.id.update:
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            String url = "https://drive.google.com/open?id=1UgIoA5YPAs6j_ZHjONHypnocVahOv1OX";
                            i.setData(Uri.parse(url));
                            startActivity(i);
                            return true;
//                        case R.id.create_group:
//                            startActivity(new Intent(MainActivity.this, CreateGroupActivity.class));
//                            return true;
                        case R.id.settings:
                            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                            return true;
                    }

                    // Add code here to update the UI based on the item selected
                    // For example, swap UI fragments here

                    return true;
                });
        //createGroupFab.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CreateGroupActivity.class)));
    }

    public void subscriptionHandler() {
        for (String groupthing : groupIDs) {
            FirebaseMessaging.getInstance().subscribeToTopic(groupthing);
        }
    }


    public void getGroupIDs() {

        DocumentReference docRef = db.collection("alfredoTest").document("MUMBODFHOWEUFHEWFOU");
        docRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){

            }
        });




        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {

//                        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task2 -> {
//                            if (task2.isSuccessful()) {
//                                if (task2.getResult() != null) {
//                                    if (!task2.getResult().getToken().equals(task.getResult().get("token"))) {
//                                        Map<String, Object> data = new HashMap<>();
//                                        data.put("token", task2.getResult().getToken());
//                                        db.collection("users").document(mAuth.getUid()).set(data, SetOptions.merge()).addOnCompleteListener(task1 -> {
//                                            if (task1.isSuccessful()) {
//                                                Log.d("demo", "Successfully updated token in DB");
//                                            }
//                                        });
//                                    } else {
//                                        Log.d("demo", "has valid token");
//                                    }
//                                }
//                            }
//                        });
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
                if (task.getResult() != null) {
                    for (DocumentSnapshot document : task.getResult()) {
                        if (document != null) {
                            String mID = document.getId();
                            for (String id : mGroupIDs) {
                                if (mID.equals(id)) {
                                    groupIDs.add(document.getId());
                                    Log.d("demo", "Inside GroupNames: " + mID);
                                    userGroups.add((String) document.get("groupName"));
                                    GroupItem groupItem = new GroupItem();
                                    groupItem.setGroupId(id);
                                    groupItem.setGroupName((String) document.get("groupName"));
                                    groupItem.setUserName((String) document.get("userName"));
                                    groupItem.setLastMessage((String) document.get("lastMessage"));
                                    groupItem.setLastSentTime((long) document.get("lastSentTime"));
                                    groupItem.setLastMessageType((String) document.get("lastMessageType"));
                                    Log.d("demo", groupItem.toString());
                                    groupItems.add(groupItem);
                                }
                            }
                        }
                    }
                }
                subscriptionHandler();
                Collections.sort(groupItems);
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }
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
    protected void onResume() {
        super.onResume();
        registerReceiver(updateUIReceiver, intentFilter);
        updateList();
        clearNotifications();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(updateUIReceiver);
    }

    @Override
    public void onItemClick(View view, int position) {
        if (groupItems.size() > 0) {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            intent.putExtra("groupID", groupItems.get(position).getGroupId());
            intent.putExtra("groupName", groupItems.get(position).getGroupName());
            startActivity(intent);
        } else {
            Toast.makeText(this, "Please add a Group", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateList() {
        groupItems.clear();
        getGroupIDs();
    }


    public void clearNotifications() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.cancelAll();
    }

    private void registerUIReceiver() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(SERVICE_TO_ACTIVITY_TRANSFER);
        updateUIReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("test", "onRecieve called");
                if (intent != null) {
                    List<GroupItem> tempGroupItemList = new ArrayList<>(groupItems);
                    GroupItem updateItem = intent.getParcelableExtra("update");
                    for (int i = 0; i < groupItems.size(); i++) {
                        if (groupItems.get(i).getGroupId().equals(updateItem.getGroupId())) {
                            tempGroupItemList.set(i, updateItem);

                            Log.d("test", "replaced groupItem");
                            break;
                        }
                    }
                    Collections.sort(tempGroupItemList);
                    adapter.updateGroupItemListItems(tempGroupItemList);
                    groupItems.clear();
                    groupItems.addAll(tempGroupItemList);
                }
            }
        };
        registerReceiver(updateUIReceiver, intentFilter);
    }

    @Override
    public void onRefresh() {
        updateList();
    }

}
