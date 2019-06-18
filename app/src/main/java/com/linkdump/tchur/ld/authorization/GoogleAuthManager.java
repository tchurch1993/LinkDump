package com.linkdump.tchur.ld.authorization;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.linkdump.tchur.ld.abstractions.OnGoogleSignInFailure;
import com.linkdump.tchur.ld.abstractions.OnGoogleSignInSuccess;
import com.linkdump.tchur.ld.activities.LoginActivity;
import com.linkdump.tchur.ld.activities.MainActivity;
import com.linkdump.tchur.ld.constants.FirebaseConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.support.constraint.Constraints.TAG;

public class GoogleAuthManager {


    private GoogleSignInClient googleSignInClient;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInAccount googleSignInAccount;
    private AuthCredential authCredential;

    private Context context;
    private AppCompatActivity activity;

    private OnGoogleSignInSuccess onSuccess;
    private OnGoogleSignInFailure onFailure;


    /*


         remember to put initialisers for all the google signin objects
         otherwise the signin methods will not work, duh


     */


    public GoogleAuthManager(Context context, AppCompatActivity activity){
         this.context = context;
    }

    public GoogleAuthManager(Context context, AppCompatActivity activity, OnGoogleSignInSuccess success, OnGoogleSignInFailure failure){
          this.context = context;
          this.onSuccess = success;
          this.onFailure = failure;
    }



    public GoogleAuthManager attemptGoogleSignIn(FirebaseAuth auth, String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        onSuccess.OnSignInSuccess(task);
                    }
                    else {
                        onFailure.OnSignInFailure(task);
                    }
                });
        return this;
    }


    private void firebaseAuthWithGoogle(FirebaseAuth firebaseAuth)
    {
        Log.d(TAG, "firebaseAuthWithGoogle:" + googleSignInAccount.getId());

        authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);

        firebaseAuth.signInWithCredential(authCredential)
                    .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");

                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(googleSignInAccount.getDisplayName())
                                .build();

                        /*

                           -Add Firebase ORM Mapper here
                           -


                         */


                        Map<String, Object> mUser = new HashMap<>();
                        if (googleSignInAccount.getGivenName() != null){
                            mUser.put("firstName", googleSignInAccount.getGivenName());
                        }
                        if (googleSignInAccount.getFamilyName() != null){
                            mUser.put("lastName", googleSignInAccount.getFamilyName());
                        }
                        if (googleSignInAccount.getEmail() != null){
                            mUser.put("email", googleSignInAccount.getEmail());
                        }
                        if (googleSignInAccount.getPhotoUrl() != null){
                            mUser.put("photoUrl", googleSignInAccount.getPhotoUrl());
                        }


                      /*  firebaseDbContext.getDb()
                                         .collection(FirebaseConstants.USERS)
                                         .document(Objects.requireNonNull(user).getUid())
                                         .set(mUser)
                                         .addOnSuccessListener(aVoid -> Log.d("demo", "DocumentSnapshot successfully written!"))
                                         .addOnFailureListener(e -> Log.w("demo", "Error writing document", e));


                        user.updateProfile(profileUpdates).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Log.d(TAG, "User profile updated");
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                        });*/

                    }
                    else
                    {

                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(activity, "Failed Sign In", Toast.LENGTH_SHORT).show();

                    }


                });
    }





    public GoogleSignInClient getGoogleSignInClient() {
        return googleSignInClient;
    }

    public void setGoogleSignInClient(GoogleSignInClient mGoogleSignInClient) {
        this.googleSignInClient = mGoogleSignInClient;
    }

    public GoogleSignInOptions getGoogleSignInOptions() {
        return googleSignInOptions;
    }

    public void setGoogleSignInOptions(GoogleSignInOptions googleSignInOptions) {
        this.googleSignInOptions = googleSignInOptions;
    }

    public GoogleSignInAccount getGoogleSignInAccount() {
        return googleSignInAccount;
    }

    public void setGoogleSignInAccount(GoogleSignInAccount googleSignInAccount) {
        this.googleSignInAccount = googleSignInAccount;
    }

    public AuthCredential getAuthCredential() {
        return authCredential;
    }

    public void setAuthCredential(AuthCredential authCredential) {
        this.authCredential = authCredential;
    }
}
