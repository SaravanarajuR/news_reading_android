package com.example.newsreading;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
        setContentView(R.layout.activity_login);

        t1=findViewById(R.id.inp1);
        t2=findViewById(R.id.inp2);
        b=findViewById(R.id.sub);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone=String.valueOf(t1.getText());
                String pass =String.valueOf(t2.getText());
                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(login.this, "Phone Number cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(pass)){
                    Toast.makeText(login.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                }else {
                    Intent j = new Intent(getApplicationContext(), otp.class);
                    j.putExtra("phone", phone);
                    j.putExtra("pass", pass);
                    startActivity(j);
                }
            }
        });
    }
}