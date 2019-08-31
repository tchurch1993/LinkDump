package com.linkdump.tchur.ld.ui.ui_containers;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.linkdump.tchur.ld.R;

public class SignUpViewCoordinator extends ViewCoordinator {



    public EditText editTextEmail;
    public EditText editTextPassword;
    public EditText editTextFirstName;
    public EditText editTextLastName;
    public EditText editTextPasswordConfirm;
    public Button cancelButton;
    public Button signUpButton;


    public SignUpViewCoordinator(Context context, AppCompatActivity appCompatActivity) {
        super(context, appCompatActivity);


        editTextEmail = appCompatActivity.findViewById(R.id.editTextEmail);
        editTextPassword = appCompatActivity.findViewById(R.id.editTextPassword);
        editTextPasswordConfirm = appCompatActivity.findViewById(R.id.editTextPasswordConfirm);
        editTextFirstName = appCompatActivity.findViewById(R.id.editTextName);
        editTextLastName = appCompatActivity.findViewById(R.id.editTextPhone);
        cancelButton = appCompatActivity.findViewById(R.id.buttonCancel);
        signUpButton = appCompatActivity.findViewById(R.id.buttonSignup);

    }

    @Override
    public void PostViewInit(View view) {








    }
}
