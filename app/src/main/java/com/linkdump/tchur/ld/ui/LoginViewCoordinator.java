package com.linkdump.tchur.ld.ui;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.linkdump.tchur.ld.R;

public class LoginViewCoordinator extends ViewCoordinator {


    //ui
    public Button signUpButton;
    public Button loginButton;
    public Button googleSignInButton;
    public EditText editTextUsername, editTextPassword;
    public ProgressBar progressBar;

    public LoginViewCoordinator(Context context, AppCompatActivity appCompatActivity) {
        super(context, appCompatActivity);
    }


    @Override
    public void PostViewInit(View view) {

        editTextUsername = rootView.findViewById(R.id.editTextEmail);
        editTextPassword = rootView.findViewById(R.id.editTextPassword);
        progressBar = rootView.findViewById(R.id.progressBar);
        signUpButton = rootView.findViewById(R.id.buttonSignup);
        loginButton = rootView.findViewById(R.id.buttonLogin);
        googleSignInButton = rootView.findViewById(R.id.googleSignInButton);


        editTextUsername.setTag("editTextUsername");
        editTextPassword.setTag("editTextPassword");
        progressBar.setTag("progressBar");
        signUpButton.setTag("signUpButton");
        loginButton.setTag("loginButton");
        googleSignInButton.setTag("googleSignInButton");

    }
}
