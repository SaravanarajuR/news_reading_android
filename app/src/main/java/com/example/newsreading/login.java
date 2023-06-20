package com.example.newsreading;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {
    TextInputEditText t1;
    TextInputEditText t2;
    Button b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        t1 = findViewById(R.id.inp1);
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Intent i = new Intent(getApplicationContext(), chooseInterest.class);
            startActivity(i);
        } else {
            b = findViewById(R.id.sub);

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone = String.valueOf(t1.getText());
                    if (TextUtils.isEmpty(phone)) {
                        Toast.makeText(login.this, "Phone Number cannot be empty", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent j = new Intent(getApplicationContext(), otp.class);
                        j.putExtra("phone", phone);
                        startActivity(j);
                    }
                }
            });
        }
    }
}