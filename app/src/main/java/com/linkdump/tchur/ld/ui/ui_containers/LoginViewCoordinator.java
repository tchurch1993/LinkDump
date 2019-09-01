package com.linkdump.tchur.ld.ui.ui_containers;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.google.android.gms.common.SignInButton;
import com.linkdump.tchur.ld.R;

public class LoginViewCoordinator extends ViewCoordinator {


    public EditText editTextUsername;
    public EditText editTextPassword;
    public ProgressBar progressBar;
    public Button buttonSignUp;
    public Button buttonLogin;
    public SignInButton googleSignInButton;


    public LoginViewCoordinator(Context context, AppCompatActivity appCompatActivity) {
        super(context, appCompatActivity);



    }


    @Override
    public void PostViewInit(View view) {

        editTextUsername = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        progressBar = view.findViewById(R.id.progressBar);
        buttonSignUp = view.findViewById(R.id.buttonSignup);
        buttonLogin = view.findViewById(R.id.buttonLogin);
        googleSignInButton = view.findViewById(R.id.googleSignInButton);

    }
}
