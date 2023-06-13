package com.example.newsreading;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class login extends AppCompatActivity {
    Private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}