package com.linkdump.tchur.ld.ui.ui_containers;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.linkdump.tchur.ld.R;

public class LoginViewCoordinator extends ViewCoordinator {


    public EditText editTextUsername;
    public EditText editTextPassword;
    public ProgressBar progressBar;
    public Button buttonSignUp;
    public Button buttonLogin;
    public Button googleSignInButton;


    public LoginViewCoordinator(Context context, AppCompatActivity appCompatActivity) {
        super(context, appCompatActivity);

        editTextUsername = appCompatActivity.findViewById(R.id.editTextEmail);
        editTextPassword = appCompatActivity.findViewById(R.id.editTextPassword);
        progressBar = appCompatActivity.findViewById(R.id.progressBar);
        buttonSignUp = appCompatActivity.findViewById(R.id.buttonSignup);
        buttonLogin = appCompatActivity.findViewById(R.id.buttonLogin);
        googleSignInButton = appCompatActivity.findViewById(R.id.googleSignInButton);

    }


    @Override
    public void PostViewInit(View view) {






    }
}
