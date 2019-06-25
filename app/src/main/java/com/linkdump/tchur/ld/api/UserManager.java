package com.linkdump.tchur.ld.api;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.model.Document;
import com.linkdump.tchur.ld.constants.FirebaseConstants;
import com.linkdump.tchur.ld.objects.User;
import com.linkdump.tchur.ld.persistence.FirebaseDbContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class UserManager {


    FirebaseDbContext firebaseDbContext;


    private OnSuccessListener successListener;
    private OnFailureListener failureListener;


    private UserManager(FirebaseDbContext firebaseDbContext) {
        this.firebaseDbContext = firebaseDbContext;
    }


    public static UserManager Create(FirebaseDbContext firebaseDbContext) {
        return new UserManager(firebaseDbContext);
    }


    public UserManager OnSuccess(OnSuccessListener onSuccessListener) {
        this.successListener = onSuccessListener;
        return this;
    }


    public UserManager OnFailure(OnFailureListener onFailureListener) {
        this.failureListener = onFailureListener;
        return this;
    }


    public User GetUser() {
        User user = new User();

        firebaseDbContext.getDb()
                .collection(FirebaseConstants.USERS)
                .get()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);


        return new User();
    }


    public List<User> GetUsers(int limit, Predicate<String> filter) {
        List<User> users;


        return null;
    }


    public User CreateUser(User user) {


        firebaseDbContext.getDbReference()
                .child("users")
                .child(String.valueOf(user.uid))
                .setValue(user).addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);

        return null;
    }


    public User UpdateUser(User user) {

        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", user.uid);
        result.put("firstName", user.firstName);
        result.put("lastName", user.lastName);
        result.put("userName", user.userName);


        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = firebaseDbContext.getDbReference().child("posts").push().getKey();
        User userPost = new User();
        Map<String, Object> postValues = result;


        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + user.uid + "/" + key, postValues);

        firebaseDbContext.getDbReference()
                .updateChildren(childUpdates);

        firebaseDbContext.getDbReference()
                .child("users")
                .child(String.valueOf(user.uid))
                .setValue(user).addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);


        return new User();
    }


    public boolean DeleteUser(User user) {

        firebaseDbContext.getDbReference()
                .child("users")
                .child(String.valueOf(user.uid))
                .setValue(user)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);

        return false;
    }


    public boolean DeleteUser(int id) {


        return false;
    }


}
