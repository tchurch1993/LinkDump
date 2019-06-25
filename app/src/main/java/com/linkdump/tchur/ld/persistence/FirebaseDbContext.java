package com.linkdump.tchur.ld.persistence;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.linkdump.tchur.ld.abstractions.IFirebaseDbContext;
import com.linkdump.tchur.ld.objects.Message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class FirebaseDbContext implements IFirebaseDbContext {

    private Context context;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private DatabaseReference dbReference;

    public DocumentReference userRef;
    public DocumentReference groupRef;
    public CollectionReference usersRef;

    private ArrayList<String> events;
    private List<Message> messages;
    private List<String> userGroups;


    public FirebaseDbContext(Context context) {

        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
        this.dbReference = FirebaseDatabase.getInstance().getReference();

        this.events = new ArrayList<>();
        this.userGroups = new ArrayList<>();
        this.messages = new ArrayList<>();

    }


    public ArrayList<String> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<String> events) {
        this.events = events;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<String> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(List<String> userGroups) {
        this.userGroups = userGroups;
    }

    public void setDbCollection(String moniker) {
        db.collection(moniker).document(Objects.requireNonNull(mAuth.getUid()));
    }

    //groupRef = db.collection("groups").document(currentGroup);
    public FirebaseDbContext PopulateFromMoniker(List list, String moniker) {
        db.collection(moniker).document(Objects.requireNonNull(mAuth.getUid()));
        return this;
    }


    public FirebaseFirestore getDb() {
        return db;
    }

    public void setDb(FirebaseFirestore db) {
        this.db = db;
    }

    public FirebaseAuth getAuth() {
        return mAuth;
    }

    public void setAuth(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }
    public DatabaseReference getDbReference() {
        return dbReference;
    }

    public void setDbReference(DatabaseReference dbReference) {
        this.dbReference = dbReference;
    }

}
