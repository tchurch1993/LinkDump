package com.linkdump.tchur.ld.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.linkdump.tchur.ld.R;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private final String TAG = "Log";

    EditText editTextUsername, editTextPassword;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        editTextUsername = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        progressBar = findViewById(R.id.progressBar);

        SharedPreferences prefs = getSharedPreferences("info", MODE_PRIVATE);
        editTextUsername.setText(prefs.getString("email", ""));

        findViewById(R.id.buttonSignup).setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.buttonLogin).setOnClickListener(view -> {
            String email = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();

            if(email.equals("")){
                Toast.makeText(LoginActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
            } else if(password.equals("")){
                Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
            } else{
                progressBar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                progressBar.setVisibility(View.INVISIBLE);


                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                progressBar.setVisibility(View.INVISIBLE);



                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        });

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null){
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }
}
