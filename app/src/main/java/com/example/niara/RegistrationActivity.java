package com.example.niara;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }

    public void movetologin(View view) {
        Intent intent=new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);

    }
}