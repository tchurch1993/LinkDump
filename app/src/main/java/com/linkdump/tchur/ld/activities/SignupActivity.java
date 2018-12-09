package com.linkdump.tchur.ld.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.linkdump.tchur.ld.R;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    EditText editTextEmail, editTextPassword, editTextFirstName, editTextLastName, editTextPasswordConfirm;
    private FirebaseAuth mAuth;
    final String TAG = "demo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.LinkDumpDark);
        setContentView(R.layout.activity_signup);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPasswordConfirm = findViewById(R.id.editTextPasswordConfirm);
        editTextFirstName = findViewById(R.id.editTextName);
        editTextLastName = findViewById(R.id.editTextPhone);


        findViewById(R.id.buttonCancel).setOnClickListener(view -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.buttonSignup).setOnClickListener(view -> {
            final String email = editTextEmail.getText().toString();
            String passwordFirst = editTextPassword.getText().toString();
            String passwordSecond = editTextPasswordConfirm.getText().toString();
            final String firstName = editTextFirstName.getText().toString();
            final String lastName = editTextLastName.getText().toString();

            if (firstName.equals("")) {
                Toast.makeText(SignupActivity.this, "Enter First Name", Toast.LENGTH_SHORT).show();
            } else if (lastName.equals("")) {
                Toast.makeText(SignupActivity.this, "Enter Last Name", Toast.LENGTH_SHORT).show();
            } else if (email.equals("")) {
                Toast.makeText(SignupActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
            } else if (passwordFirst.equals("")) {
                Toast.makeText(SignupActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
            } else if (passwordSecond.equals("")) {
                Toast.makeText(SignupActivity.this, "Enter Confirm Password", Toast.LENGTH_SHORT).show();
            } else if (!passwordSecond.equals(passwordFirst)) {
                Toast.makeText(SignupActivity.this, "Enter Passwords Don't Match", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.createUserWithEmailAndPassword(email, passwordFirst)
                        .addOnCompleteListener(SignupActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signUpWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(firstName + " " + lastName)
                                        .build();
                                Map<String, Object> mUser = new HashMap<>();
                                mUser.put("firstName", firstName);
                                mUser.put("lastName", lastName);
                                mUser.put("email", email);
                                db.collection("users").document(user.getUid())
                                        .set(mUser)
                                        .addOnSuccessListener(aVoid -> Log.d("demo", "DocumentSnapshot successfully written!"))
                                        .addOnFailureListener(e -> Log.w("demo", "Error writing document", e));


                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Log.d(TAG, "User profile updated");
                                                Intent i = new Intent(SignupActivity.this, MainActivity.class);
                                                startActivity(i);
                                                finish();
                                            }
                                        });

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signUpWithEmail:failure", task.getException());
                                Toast.makeText(SignupActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }


                        });

            }
        });

    }
}
