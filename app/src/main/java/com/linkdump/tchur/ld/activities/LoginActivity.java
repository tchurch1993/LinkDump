package com.linkdump.tchur.ld.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.linkdump.tchur.ld.R;
import com.linkdump.tchur.ld.abstractions.OnGoogleSignInFailure;
import com.linkdump.tchur.ld.abstractions.OnGoogleSignInSuccess;
import com.linkdump.tchur.ld.api.ClientManager;
import com.linkdump.tchur.ld.api.GroupManager;
import com.linkdump.tchur.ld.api.listeners.TrialListener;
import com.linkdump.tchur.ld.api.MessageManager;
import com.linkdump.tchur.ld.authorization.GoogleAuthManager;
import com.linkdump.tchur.ld.constants.FirebaseConstants;
import com.linkdump.tchur.ld.persistence.FirebaseDbContext;
import com.linkdump.tchur.ld.ui.LoginViewCoordinator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements Button.OnClickListener,
                                                                        OnGoogleSignInSuccess,
                                                                        OnGoogleSignInFailure {


    private FirebaseDbContext firebaseDbContext;
    private SharedPreferences sharedPreferences;
    private LoginViewCoordinator loginViewCoordinator;
    private GoogleAuthManager googleAuthManager;


    private ClientManager userManager;
    private MessageManager messageManager;
    private GroupManager groupManager;


    private final String TAG = "Log";


    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions googleSignInOptions;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseDbContext = new FirebaseDbContext(getApplicationContext());

        loginViewCoordinator = new LoginViewCoordinator(getApplicationContext(), this);
        loginViewCoordinator.initialiseViewFromXml(R.layout.activity_login);
        loginViewCoordinator.signUpButton.setOnClickListener(this);
        loginViewCoordinator.loginButton.setOnClickListener(this);
        loginViewCoordinator.googleSignInButton.setOnClickListener(this);

        setContentView(loginViewCoordinator.getRootView());


        handleNotificationChannels();
        sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
        loginViewCoordinator.editTextUsername.setText(sharedPreferences.getString("email", ""));




        googleAuthManager = new GoogleAuthManager(getApplicationContext(),this,this,this);
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);



        userManager = new ClientManager(firebaseDbContext);
        groupManager = new GroupManager(firebaseDbContext);
        messageManager = new MessageManager(firebaseDbContext);
  }




    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 101);
    }



    public void handleNotificationChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = "chat";
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW));
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try
            {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            }
            catch (ApiException e)
            {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }



   public Map<String, Object> MapAccountToUser(GoogleSignInAccount acct){


       Map<String, Object> mUser = new HashMap<>();
       if (acct.getGivenName() != null){
           mUser.put("firstName", acct.getGivenName());
       }
       if (acct.getFamilyName() != null){
           mUser.put("lastName", acct.getFamilyName());
       }
       if (acct.getEmail() != null){
           mUser.put("email", acct.getEmail());
       }
       if (acct.getPhotoUrl() != null){
           mUser.put("photoUrl", acct.getPhotoUrl());
       }

       return mUser;

   }




    // TODO: Get Google auth working
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct)
    {

        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        firebaseDbContext.getAuth()
                         .signInWithCredential(credential)
                         .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");

                        FirebaseUser user = firebaseDbContext.getAuth().getCurrentUser();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(acct.getDisplayName())
                                .build();


                        Map<String, Object> mUser = MapAccountToUser(acct);




                        firebaseDbContext.getDb()
                                .collection(FirebaseConstants.USERS)
                                .document(Objects.requireNonNull(user).getUid())
                                .set(user)
                                .addOnSuccessListener(aVoid -> Log.d("demo", "DocumentSnapshot successfully written!"))
                                .addOnFailureListener(e -> Log.w("demo", "Error writing document", e));


                         user.updateProfile(profileUpdates).addOnCompleteListener(task1 -> {

                                    if (task1.isSuccessful()) {
                                        Log.d(TAG, "User profile updated");
                                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    }

                                });

                    }
                    else
                        {

                            // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(getApplicationContext(), "Failed Sign In", Toast.LENGTH_SHORT).show();

                    }


                });
    }




    @Override
    protected void onStart() {
        super.onStart();

        if (firebaseDbContext.getAuth().getCurrentUser() != null)
        {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }



    @Override
    public void onClick(View v)
    {

        if(v.getTag().equals("googleSignInButton"))
        {
             signIn();
        }
        else if(v.getTag().equals("signUpButton"))
        {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
            finish();
        }
        else if(v.getTag().equals("loginButton"))
        {
            String email = loginViewCoordinator.editTextUsername.getText().toString();
            String password = loginViewCoordinator.editTextPassword.getText().toString();

            if (email.equals(""))
            {
                Toast.makeText(LoginActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
            }
            else if (password.equals(""))
            {
                Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
            }
            else
                {
                loginViewCoordinator.progressBar.setVisibility(View.VISIBLE);

                googleAuthManager.attemptGoogleSignIn(firebaseDbContext.getAuth(),email,password);
                }
        }
        else
        {

        }
    }




    @Override
    public void OnSignInSuccess(Task task) {

        Log.d(TAG, "signInWithEmail:success");
        FirebaseUser user = firebaseDbContext.getAuth().getCurrentUser();
        loginViewCoordinator.progressBar.setVisibility(View.INVISIBLE);

        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }



    @Override
    public void OnSignInFailure(Task task)
    {
        Log.w(TAG, "signInWithEmail:failure", task.getException());
        loginViewCoordinator.progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
    }


}
